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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/**
 *
 * @author Luthiger
 * Created 28.01.2009 
 */
public class VifEditSUAction extends VifAdminFixture {
	private HtmlPage editMember;

	/**
	 * @param inPort
	 */
	public VifEditSUAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifAdminFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	private String[] getXPathArgs() {
		return new String[] {String.format(XPATH_TR, "dataSmall odd"), String.format(XPATH_TR, "dataSmall even")};
	}
	
	public String userIDOfMember() throws Exception {
		return ((HtmlTableRow)getTableRows(getXPathArgs()).get(0)).getCell(1).asText();
	}

	public String nameOfMember() throws Exception {
		return ((HtmlTableRow)getTableRows(getXPathArgs()).get(0)).getCell(2).asText();
	}
	
	public boolean openMemberEdit() throws Exception {
		Page lActionPage = ((HtmlAnchor)((HtmlTableRow)getTableRows(getXPathArgs()).get(0)).getCell(1).getHtmlElementsByTagName("a").get(0)).click();
		editMember = getActionPageChecked(lActionPage);
		return true;
	}

	public String pageTitle2() throws Exception {
		return getPageTitle(editMember);
	}
	
	public boolean editNameFirstNameStreetPostalCityMail(String inName, String inFirstName, String inStreet, String inPostal, String inCity, String inMail) throws Exception {
		final HtmlForm lForm = editMember.getFormByName("Form");
		setInput(lForm, "fldName", inName);
		setInput(lForm, "fldFirstname", inFirstName);
		setInput(lForm, "fldStreet", inStreet);
		setInput(lForm, "fldZIP", inPostal);
		setInput(lForm, "fldCity", inCity);
		setInput(lForm, "fldMail", inMail);
		
		//make su an admin too
		List<HtmlInput> lCheckBoxes = lForm.getInputsByName("chkRole");
		for (HtmlInput lCheckBox : lCheckBoxes) {
			if ("2".equals(((HtmlCheckBoxInput)lCheckBox).getValueAttribute())) {
				((HtmlCheckBoxInput)lCheckBox).setChecked(true);
				break;
			}
		}
		
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		setSuccess(getActionPageChecked(lSend.click()));
		return true;
	}
	
	public String successMessage() throws Exception {
		List<?> lMessages = getSuccess().getByXPath("//div[@class='message']");
		HtmlDivision lMessage = (HtmlDivision) lMessages.get(0);
		return lMessage.asText();
	}
	
}
