package org.hip.vifapp.test;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * @author Luthiger
 * Created 15.02.2009
 */
public class CreateStartingQuestion extends VifTestCase {
	private final static String GROUP_ID = "Walkthrough test 2";
	private final static String QUESTION = "Where shall we go?";
	
	public void testCreateStartingQuestion() throws Exception {
		doLogin();
		HtmlPage lResult = createStartingQuestion();
		assertQuestionState(lResult, "private");
		publish();
		assertQuestionState(getNewQuestionPage(), "open");
		doLogoutAdmin(webClient);
	}

	private HtmlPage publish() throws Exception  {
		HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdminBody("org.hip.vif.admin.groupadmin.showPublishables&groupID=2"));
		HtmlPage lContributions = (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
		List<HtmlTableRow> lRows = getTableRows(lContributions, "odd", "even");
		for (HtmlTableRow lRow : lRows) {
			if (QUESTION.equals(lRow.getCell(2).asText())) {
				((HtmlCheckBoxInput)lRow.getCell(0).getFirstChild()).setChecked(true);
			}
		}
		
		HtmlForm lForm = lContributions.getFormByName("Form");
		HtmlInput lSend = lForm.getInputByName("actionButton");
		return (HtmlPage) lSend.click();
	}

	private void assertQuestionState(HtmlPage inResult, String inState) {
		List<HtmlTableRow> lRows = getTableRows(inResult, "odd", "even");
		for (HtmlTableRow lRow : lRows) {
			if (QUESTION.equals(lRow.getCell(1).asText())) {
				assertEquals("state of question", inState, lRow.getCell(2).asText());
			}
		}
	}

	private HtmlPage createStartingQuestion() throws Exception {
		HtmlForm lForm = getNewQuestionPage().getFormByName("Form");
		setTextArea(lForm, "fldQuestion", QUESTION);
		setTextArea(lForm, "fldRemark", "A reasonable goal is prerequisite for a successful walk.");
		
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		return lSend.click();
	}

	private void setTextArea(HtmlForm inForm, String inFieldName, String inFieldValue) {
		final HtmlTextArea lInputField = inForm.getTextAreaByName(inFieldName);
		lInputField.setText(inFieldValue);
	}
	
	private HtmlPage getNewQuestionPage() throws Exception {
		HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdminBody("org.hip.vif.admin.groupadmin.showAdminGroupList"));
		HtmlPage lGroups = (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
		List<HtmlTableRow> lRows = getTableRows(lGroups, "dataSmall odd", "dataSmall even");
		HtmlAnchor lAnchor = null;
		for (HtmlTableRow lRow : lRows) {
			if (GROUP_ID.equals(lRow.getCell(1).asText())) {
				lAnchor = (HtmlAnchor) lRow.getCell(1).getFirstChild();
			}
		}
		assertNotNull("must find anchor of group", lAnchor);
		return lAnchor.click();
	}

	private HtmlPage doLogin() throws Exception {
		final HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdmin());
		final HtmlPage lBody = (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
		HtmlPage outSuccess = doLogin(lBody, "groupAdmin1", "groupAdmin1");
		assertEquals("title text after group admin logged in", "Management of discussion groups", getTitle(outSuccess));
		return outSuccess;		
	}

}
