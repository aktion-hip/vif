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
package org.hip.vifapp.test;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 *
 * @author Luthiger
 * Created 28.01.2009 
 */
public class AdminLogin extends VifTestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAdminLogin() throws Exception {
		final HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdmin());
		final HtmlPage lBody = (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
		
		checkLoginPage(lBody);		
		HtmlPage lShowMembersPages = doLogin(lBody);
		final HtmlPage lEditMemberPage = getEditeMemberPage(lShowMembersPages);
		lShowMembersPages = editMember(lEditMemberPage);
		
		doLogoutAdmin(webClient);
	}

	private void checkLoginPage(HtmlPage inPage) {		
		List<?> lParas = inPage.getByXPath("//p[@class='title2']");
		HtmlParagraph lTitleNode = (HtmlParagraph) lParas.get(0);
		assertEquals("title text login", "Welcome to the Administration of the virtual discussion forum", lTitleNode.asText());
	}
	
	private HtmlPage doLogin(HtmlPage inPage) throws IOException {
		String lUserName = "FitSU";
		String lPassword = "fit4ever";
		final HtmlForm lForm = inPage.getFormByName("Form");
		final HtmlTextInput lText = (HtmlTextInput) lForm.getInputByName("userId");
		final HtmlPasswordInput lPasswordField = (HtmlPasswordInput) lForm.getInputByName("passwd");
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("button");
		lText.setValueAttribute(lUserName);
		lPasswordField.setValueAttribute(lPassword);
		
		final HtmlPage outSuccess = (HtmlPage)lSend.click();
		assertEquals("title text logged in", "Member administration", ((HtmlParagraph)outSuccess.getByXPath("//p[@class='title']").get(0)).asText());
		assertEquals("number of members", 1, outSuccess.getByXPath("//tr[@class='dataSmall odd']").size());
		return outSuccess;
	}
	
	private HtmlPage getEditeMemberPage(HtmlPage inPage) throws IOException {
		List<HtmlTableCell> lCells = ((HtmlTableRow)inPage.getByXPath("//tr[@class='dataSmall odd']").get(0)).getCells();
		assertEquals("userID SU", "FitSU", lCells.get(1).asText());
//		assertEquals("name SU", "SU", lCells.get(2).asText());
		
		HtmlPage lEditMember = (HtmlPage)((HtmlAnchor)((HtmlTableDataCell)lCells.get(1)).getHtmlElementsByTagName("a").get(0)).click();
		return lEditMember;
	}
	
	private HtmlPage editMember(HtmlPage inPage) throws IOException {
		final HtmlForm lForm = inPage.getFormByName("Form");
		setInput(lForm, "fldName", "Foo");
		setInput(lForm, "fldFirstname", "Jon");
		setInput(lForm, "fldStreet", "Highway 1");
		setInput(lForm, "fldZIP", "1234");
		setInput(lForm, "fldCity", "Heaven");
		setInput(lForm, "fldMail", "test1@localhost");
		List<HtmlInput> lCheckBoxes = lForm.getInputsByName("chkRole");
		for (HtmlInput lCheckBox : lCheckBoxes) {
			if ("2".equals(((HtmlCheckBoxInput)lCheckBox).getValueAttribute())) {
				((HtmlCheckBoxInput)lCheckBox).setChecked(true);
				break;
			}
		}
		
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		return (HtmlPage)lSend.click();
	}

}
