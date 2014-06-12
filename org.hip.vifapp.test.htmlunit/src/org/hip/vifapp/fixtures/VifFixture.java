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
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 *
 * @author Luthiger
 * Created 27.01.2009 
 */
public abstract class VifFixture {
	protected final static String URL_WITH_REQUEST = "http://localhost:%s/%s?requestType=%s";
	protected final static String URL_PLAIN = "http://localhost:%s/%s";
	protected final static String URL_BODY_WITH_REQUEST = "http://localhost:%s/%s?requestType=master&body=%s";
	protected final static String XPATH_TR = "//tr[@class='%s']";
	protected static final String FORM_NAME = "Form"; 
	
	private final static String XPATH_TITLE = "//p[@class='%s']";
	private final static String FRAME_BODY = "body";
	private static final String GROUP_ID_KEY = "groupID";
	
	private String port;
	protected HtmlPage success = null;
	
	public VifFixture(String inPort) {
		port = inPort;
	}

	public String getPageTitle(String inTitleClass) throws Exception {
		return getPageTitle(getSuccess(), inTitleClass);
	}
	
	public String getPageTitle(HtmlPage inPage) {
		return getPageTitle(inPage, "title");
	}

	public String pageTitle1() throws Exception {
		return getPageTitle(getSuccess());
	}
	
	public String getPageTitle(HtmlPage inPage, String inTitleClass) {
		List<?> lTitles = inPage.getByXPath(String.format(XPATH_TITLE, inTitleClass));
		HtmlParagraph lTitleNode = (HtmlParagraph) lTitles.get(0);
		return lTitleNode.asText();		
	}
	
	protected String getPort() {
		return port;
	}
	
	abstract protected String getApp();

	protected abstract String getRequestType();
	
	public boolean logout() throws Exception {
		HtmlPage lFareWell = doLogout();
		String lExpected = String.format(URL_BODY_WITH_REQUEST, port, getApp(), "logout");
		success = null;
		return lExpected.equals(lFareWell.getWebResponse().getRequestSettings().getUrl().toString());
	}
	
	protected HtmlPage doLogout() throws Exception {
		return (HtmlPage)StaticWebClient.theClient.getPage(String.format(URL_BODY_WITH_REQUEST, port, getApp(), "logout"));
	}
	
	public boolean changeTo(String inRequestType) throws Exception {
		HtmlPage lFrameSet = (HtmlPage)StaticWebClient.theClient.getPage(String.format(URL_BODY_WITH_REQUEST, port, getApp(), inRequestType));
		setSuccess((HtmlPage) lFrameSet.getFrameByName(FRAME_BODY).getEnclosedPage());
		return true;
	}

	protected HtmlPage getSuccess() throws Exception {
		if (success == null) {
			if (getRequestType() == null) {
				success = (HtmlPage)StaticWebClient.theClient.getPage(String.format(URL_PLAIN, getPort(), getApp()));				
			}
			else {
				success = (HtmlPage)StaticWebClient.theClient.getPage(String.format(URL_WITH_REQUEST, getPort(), getApp(), getRequestType()));
			}			
		}
		return success;
	}

	protected void setSuccess(HtmlPage inSuccess) {
		success = inSuccess;
	}

	public boolean loginAsWithPassword(String inUserName, String inPassword) throws Exception {
		final HtmlPage lPage = getBodyFrame();
		final HtmlForm lForm = lPage.getFormByName("Form");
		final HtmlTextInput lText = (HtmlTextInput) lForm.getInputByName("userId");
		final HtmlPasswordInput lPassword = (HtmlPasswordInput) lForm.getInputByName("passwd");
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("button");
		lText.setValueAttribute(inUserName);
		lPassword.setValueAttribute(inPassword);
		setSuccess(getActionPageChecked(lSend.click()));
	
		return true;
	}
	
	protected HtmlPage getActionPageChecked(Page inActionPage) throws Exception {
		HtmlPage lActionPage = (HtmlPage) inActionPage;
		WebWindow lEnclosing = lActionPage.getEnclosingWindow();
		if (lEnclosing == null) return lActionPage;
		HtmlPage lTopFrame = (HtmlPage) ((FrameWindow)lEnclosing).getParentWindow().getEnclosedPage();
		if (lTopFrame == null) return lActionPage;
		return getBodyFrame(lTopFrame);
	}

	private HtmlPage getBodyFrame() throws Exception {
		return getBodyFrame(getSuccess());
	}

	private HtmlPage getBodyFrame(HtmlPage inFrameSet) throws Exception {
		return (HtmlPage) inFrameSet.getFrameByName(FRAME_BODY).getEnclosedPage();
	}

	protected void setInput(HtmlForm inForm, String inFieldName, String inFieldValue) {
		final HtmlTextInput lInputField = (HtmlTextInput) inForm.getInputByName(inFieldName);
		lInputField.setValueAttribute(inFieldValue);
	}

	public String successMessage() throws Exception {
		return ((HtmlDivision)getSuccess().getByXPath("//div[@class='message']").get(0)).asText();
	}

	/**
	 * Returns a list of <code>HtmlTableRow</code>s using the specified XPath commands.
	 * 
	 * @param inXPaths String[]
	 * @return List<HtmlTableRow>
	 */
	@SuppressWarnings("unchecked")
	protected List<HtmlTableRow> getTableRows(String... inXPaths) throws Exception {
		List<HtmlTableRow> outTableRows = null;
		boolean lFirst = true;
		
		for (String lXPath : inXPaths) {
			if (lFirst) {
				outTableRows = (List<HtmlTableRow>) getSuccess().getByXPath(lXPath);
				lFirst = false;
			}
			else {
				outTableRows.addAll((List<HtmlTableRow>) getSuccess().getByXPath(lXPath));
			}
		}
		return outTableRows;
	}

	public String getGroupId(String inGroupName, int inCell, List<HtmlTableRow> inRows) throws Exception {
		for (HtmlTableRow lRow : inRows) {
			if (inGroupName.equals(lRow.getCell(inCell).asText())) {
				return getID(((HtmlAnchor) lRow.getCell(inCell).getFirstChild()).getHrefAttribute(), GROUP_ID_KEY);
			}
		}
		return "";
	}

	protected String getID(String inHref, String inStartsWith) {
		for (String lPart : inHref.split("&")) {
			if (lPart.startsWith(inStartsWith)) {
				return lPart.split("=")[1].trim();
			}
		}
		return "";
	}

	protected void setTextArea(HtmlForm inForm, String inFieldName, String inFieldValue) {
		final HtmlTextArea lInputField = inForm.getTextAreaByName(inFieldName);
		lInputField.setText(inFieldValue);
	}
	
}
