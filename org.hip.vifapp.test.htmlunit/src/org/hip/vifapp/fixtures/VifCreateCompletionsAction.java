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

/**
 *
 * @author Luthiger
 * Created 19.02.2009 
 */
public class VifCreateCompletionsAction extends VifForumFixture {
	public VifCreateCompletionsAction(String inPort) {
		super(inPort);
	}

	/* (non-Javadoc)
	 * @see org.hip.vifapp.fixtures.VifFixture#getRequestType()
	 */
	@Override
	protected String getRequestType() {
		return null;
	}
	
	public String getGroupId(String inGroupName) throws Exception {		
		return getGroupId(inGroupName, 0, getTableRows(String.format(XPATH_TR, "data odd"), String.format(XPATH_TR, "data even")));
	}
	
	public boolean createCompletion(String inCompletion) throws Exception {
		HtmlForm lForm = getSuccess().getFormByName(FORM_NAME);
		setTextArea(lForm, "fldCompletion", inCompletion);
		setSuccess(getActionPageChecked(lForm.getInputByName("cmdSend").click()));
		return true;
	}
	
	public boolean createQuestionWithRemark(String inQuestion, String inRemark) throws Exception {
		HtmlForm lForm = getSuccess().getFormByName(FORM_NAME);
		setTextArea(lForm, "fldQuestion", inQuestion);
		setTextArea(lForm, "fldRemark", inRemark);
		setSuccess(getActionPageChecked(lForm.getInputByName("cmdSend").click()));
		return true;
	}
	
	public String numberOfPending() throws Exception {
		return String.valueOf(getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even")).size());
	}
	
	public String numberOfContributions() throws Exception {
		return String.valueOf(getTableRows(String.format(XPATH_TR, "odd"), String.format(XPATH_TR, "even")).size());
	}
	
}
