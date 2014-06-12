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

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 *
 * @author Luthiger
 * Created 05.02.2009 
 */
public class VifCreateGroupAction extends VifAdminFixture {
	private static final String XPATH_TR = "//tr[@class='%s']"; 

	public VifCreateGroupAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public boolean newGroupDescriptionReviewersPublicSize(String inGroupName, String inDescription, String inReviewers, String inPublicLevel, String inSize) throws Exception {
		final HtmlForm lForm = getSuccess().getFormByName("Form");
		setInput(lForm, "fldName", inGroupName);
		setTextArea(lForm, "fldDescription", inDescription);
		setInput(lForm, "fldGuestDepth", inPublicLevel);
		setInput(lForm, "fldMinGoupSize", inSize);
		
		if ("0".equals(inReviewers) || "1".equals(inReviewers)) {			
			((HtmlOption)((HtmlSelect)lForm.getSelectByName("fldReviewers")).getOptionByValue(inReviewers)).setSelected(true);
		}

		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		setSuccess(getActionPageChecked(lSend.click()));

		return true;
	}
	
	public String stateOf(String inGroupID) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(getXPathArgs());
		for (HtmlTableRow lRow : lRows) {
			if (inGroupID.equals(lRow.getCell(1).asText())) {
				return lRow.getCell(6).asText();
			}
		}
		return "";
	}
	
	public boolean edit(String inGroupID) throws Exception {
		List<HtmlTableRow> lRows = getTableRows(getXPathArgs());
		HtmlAnchor lAnchor = null;
		for (HtmlTableRow lRow : lRows) {
			if (inGroupID.equals(lRow.getCell(1).asText())) {
				lAnchor = (HtmlAnchor)lRow.getCell(1).getFirstChild();
			}
		}
		
		if (lAnchor == null) {
			return false;
		}
		setSuccess(getActionPageChecked(lAnchor.click()));
		return true;
	}
	
	
	private String[] getXPathArgs() {
		return new String[] {String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even")};
	}
	
	public boolean assignAdminsAnd(String inAdmin1, String inAdmin2) throws Exception {
		HtmlSubmitInput lInput = (HtmlSubmitInput) getSuccess().getByXPath("//input[@name='assignGroupAdmin']").get(0);
		setSuccess((HtmlPage) lInput.click());
		
		//assure we're on the page to select members
		if (!"Select members".equals(pageTitle1())) {
			return false;
		}
		//select the admins
		List<HtmlTableRow> lRows = getTableRows(String.format(XPATH_TR, "dataSmall odd"), String.format(XPATH_TR, "dataSmall even"));
		for (HtmlTableRow lRow : lRows) {
			String lAdminName = lRow.getCell(1).asText();
			if (inAdmin1.equals(lAdminName) || inAdmin2.equals(lAdminName)) {
				((HtmlCheckBoxInput)lRow.getCell(0).getFirstChild()).setChecked(true);
			}
		}
		//save selection
		HtmlForm lForm = getSuccess().getFormByName("Form");
		final HtmlSubmitInput lSend = (HtmlSubmitInput)lForm.getInputByName("actionButton");
		lSend.click();

		//return back to admin page for groups
		changeTo("org.hip.vif.admin.groupedit.showGroupList");
		return "Management of discussion groups".equals(pageTitle1());
	}
	
	

}
