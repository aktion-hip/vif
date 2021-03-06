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
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 *
 * @author Luthiger
 * Created 02.02.2009 
 */
public class VifCreateMembersAction extends VifAdminFixture {
	private String port;
	private HtmlPage memberNewForm;

	/**
	 * @param inPort
	 */
	public VifCreateMembersAction(String inPort) {
		super(inPort);
		port = inPort;
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifAdminFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public boolean memberNewForm() throws Exception {
		HtmlPage lFrameSet = (HtmlPage)StaticWebClient.theClient.getPage(String.format(URL_BODY_WITH_REQUEST, port, APP, "org.hip.vif.admin.member.newMember"));
		memberNewForm = (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
		return true;
	}
	
	public String pageTitle2() throws Exception {
		return getPageTitle(memberNewForm);
	}
	
	public boolean createMemberNameFirstNameStreetPostalCityMail(String inUserID, String inName, String inFirstName, String inStreet, String inPostal, String inCity, String inMail) throws Exception {
		final HtmlForm lForm = memberNewForm.getFormByName("Form");
		setInput(lForm, "fldUserID", inUserID);
		setInput(lForm, "fldName", inName);
		setInput(lForm, "fldFirstname", inFirstName);
		setInput(lForm, "fldStreet", inStreet);
		setInput(lForm, "fldZIP", inPostal);
		setInput(lForm, "fldCity", inCity);
		setInput(lForm, "fldMail", inMail);

		//make the member a male
		((HtmlOption)((HtmlSelect)lForm.getSelectByName("fldSex")).getOptionByValue("0")).setSelected(true);

		//make su an admin too
		List<HtmlInput> lCheckBoxes = lForm.getInputsByName("chkRole");
		for (HtmlInput lCheckBox : lCheckBoxes) {
			if ("5".equals(((HtmlCheckBoxInput)lCheckBox).getValueAttribute())) {
				((HtmlCheckBoxInput)lCheckBox).setChecked(true);
				break;
			}
		}
		
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		setSuccess(getActionPageChecked(lSend.click()));

		return true;
	}
	
	public String password() throws Exception {
		String lSuccess = successMessage();
		int lPos1 = lSuccess.indexOf(": ");
		return lSuccess.substring(lPos1+2, lSuccess.length()-1);
	}

}
