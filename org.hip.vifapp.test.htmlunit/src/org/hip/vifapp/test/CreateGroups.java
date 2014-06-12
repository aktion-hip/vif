package org.hip.vifapp.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * @author Luthiger
 * Created 11.02.2009
 */
public class CreateGroups extends VifTestCase {
	private final static String GROUP_ID = "Walkthrough test 2";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateGroups() throws Exception {
		doLoginInAdmin();
		HtmlPage lEditPage = createNewGroup();
		assignAdmins(lEditPage);
		checkCreation();
		doLogoutAdmin(webClient);
	}

	@SuppressWarnings("unchecked")
	private void checkCreation() throws Exception {
		HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdminBody("org.hip.vif.admin.groupedit.showGroupList"));
		HtmlPage lAdminPage = (HtmlPage)lFrameSet.getFrameByName("body").getEnclosedPage();
		assertEquals("ensure starting page", "Management of discussion groups", ((HtmlParagraph)lAdminPage.getByXPath("//p[@class='title']").get(0)).asText());
		
		//activate correct group
		List<HtmlTableRow> lTableRows = (List<HtmlTableRow>) lAdminPage.getByXPath("//tr[@class='odd']");
		lTableRows.addAll((List<HtmlTableRow>)lAdminPage.getByXPath("//tr[@class='even']"));
		HtmlAnchor lAnchor = null;
		for (HtmlTableRow lRow : lTableRows) {
			if (GROUP_ID.equals(lRow.getCell(1).asText())) {
				lAnchor = (HtmlAnchor)lRow.getCell(1).getFirstChild();
			}
		}
		
		assertNotNull("must find anchor of group", lAnchor);
		HtmlPage lEditPage = lAnchor.click();
		assertEquals("we're on the group's edit page now", "Edit the discussion group", ((HtmlParagraph)lEditPage.getByXPath("//p[@class='title']").get(0)).asText());
		
		//collect all group admin entries
		String[] lExpected = {"groupAdmin1", "groupAdmin2"};
		List<String> lExpectedAdmins = Arrays.asList(lExpected);
		
		lTableRows = (List<HtmlTableRow>) lEditPage.getByXPath("//tr[@class='dataSmall odd']");
		lTableRows.addAll((List<HtmlTableRow>)lEditPage.getByXPath("//tr[@class='dataSmall even']"));
		assertEquals("number of group admins", lExpected.length, lTableRows.size());
		for (HtmlTableRow lRow : lTableRows) {
			String lAdmin = lRow.getCell(1).asText();
			assertTrue("expected " + lAdmin, lExpectedAdmins.contains(lAdmin));
		}
	}

	@SuppressWarnings("unchecked")
	private void assignAdmins(HtmlPage inEditPage) throws IOException {
		HtmlSubmitInput lInput = (HtmlSubmitInput) inEditPage.getByXPath("//input[@name='assignGroupAdmin']").get(0);
		HtmlPage lSelectionPage = lInput.click();
		assertEquals("title of selection page", "Select members", ((HtmlParagraph)lSelectionPage.getByXPath("//p[@class='title']").get(0)).asText());
		
		List<HtmlTableRow> lTableRows = (List<HtmlTableRow>) lSelectionPage.getByXPath("//tr[@class='dataSmall odd']");
		lTableRows.addAll((List<HtmlTableRow>)lSelectionPage.getByXPath("//tr[@class='dataSmall even']"));
		for (HtmlTableRow lRow : lTableRows) {
			String lAdminName = lRow.getCell(1).asText();
			if ("groupAdmin1".equals(lAdminName) || "groupAdmin2".equals(lAdminName)) {
				((HtmlCheckBoxInput)lRow.getCell(0).getFirstChild()).setChecked(true);
			}
		}
		
		HtmlForm lForm = lSelectionPage.getFormByName("Form");
		final HtmlSubmitInput lSend = (HtmlSubmitInput)lForm.getInputByName("actionButton");
		lSend.click();

//		assertEquals("title after admins assigned", "Edit the discussion group", ((HtmlParagraph)outSuccess.getByXPath("//p[@class='title']").get(0)).asText());
		
	}

	private HtmlPage createNewGroup() throws Exception {
		final HtmlForm lForm = getNewGroupPage().getFormByName("Form");
		setInput(lForm, "fldName", GROUP_ID);
		setTextArea(lForm, "fldDescription", "Discussion group to talk about the forum in light of a walk through");
		setInput(lForm, "fldGuestDepth", "5");
		setInput(lForm, "fldMinGoupSize", "3");
		
		((HtmlOption)((HtmlSelect)lForm.getSelectByName("fldReviewers")).getOptionByValue("1")).setSelected(true);
		final HtmlSubmitInput lSend = (HtmlSubmitInput) lForm.getInputByName("cmdSend");
		HtmlPage outSuccess = lSend.click();
		assertEquals("title after new group created", "Edit the discussion group", ((HtmlParagraph)outSuccess.getByXPath("//p[@class='title']").get(0)).asText());
		return outSuccess;
	}

	private void setTextArea(HtmlForm inForm, String inFieldName, String inFieldValue) {
		final HtmlTextArea lInputField = inForm.getTextAreaByName(inFieldName);
		lInputField.setText(inFieldValue);
	}

	private HtmlPage getNewGroupPage() throws Exception {
		HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdminBody("org.hip.vif.admin.groupedit.newGroup"));
		return (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
	}

}
