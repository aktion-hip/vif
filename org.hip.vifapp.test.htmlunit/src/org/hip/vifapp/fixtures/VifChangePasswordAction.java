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

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 *
 * @author Luthiger
 * Created 05.02.2009 
 */
public class VifChangePasswordAction extends VifForumFixture {

	public VifChangePasswordAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public boolean changePassword(String inUserName, String inPassword) throws Exception {
		final HtmlForm lForm = getSuccess().getFormByName("Form");
		setPasswordInput(lForm, "fldPwrdOld", inPassword);
		setPasswordInput(lForm, "fldPwrdNew", inUserName);
		setPasswordInput(lForm, "fldPwrdNewConfirm", inUserName);

		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		setSuccess(getActionPageChecked(lSend.click()));

		return true;
	}

	private void setPasswordInput(HtmlForm inForm, String inFieldName, String inFieldValue) {
		final HtmlPasswordInput lInputField = (HtmlPasswordInput) inForm.getInputByName(inFieldName);
		lInputField.setValueAttribute(inFieldValue);		
	}	

}
