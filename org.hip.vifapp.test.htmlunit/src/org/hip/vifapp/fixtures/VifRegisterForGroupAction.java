/*
	This package is part of the application VIF.
	Copyright (C) 2009, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.vifapp.fixtures;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 *
 * @author Luthiger
 * Created 19.02.2009 
 */
public class VifRegisterForGroupAction extends VifForumFixture {

	public VifRegisterForGroupAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}

	public boolean registerTo(String inGroupName) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "data odd"), String.format(XPATH_TR, "data even"));
		for (HtmlTableRow lRow : lRows) {
			if (inGroupName.equals(lRow.getCell(1).asText())) {
				((HtmlCheckBoxInput)lRow.getCell(0).getFirstChild()).setChecked(true);
			}
		}
		
		HtmlForm lForm = getSuccess().getFormByName(FORM_NAME);
		setSuccess(getActionPageChecked(lForm.getInputByName("actionButton").click()));
		return true;
	}
	
	public String groupSize(String inGroupName) throws Exception {
		return getGroupValue(inGroupName, 2);
	}
	
	public String groupState(String inGroupName) throws Exception {
		return getGroupValue(inGroupName, 3);
	}
	
	private String getGroupValue(String inGroupName, int inCell) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "data odd"), String.format(XPATH_TR, "data even"));
		for (HtmlTableRow lRow : lRows) {
			if (inGroupName.equals(lRow.getCell(0).asText())) {
				return lRow.getCell(inCell).asText();
			}
		}
		return "";		
	}
	
}
