package org.hip.vifapp.test;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 * @author Luthiger
 * Created 02.02.2009
 */
public class CreateMembers extends VifTestCase {	

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateMembers() throws Exception {
		doLoginInAdmin();
		HtmlPage lCreateMemberPage = getNewMembersForm();
		assertEquals("title of page to create new member", "Registration of a new member's data", ((HtmlParagraph)lCreateMemberPage.getByXPath("//p").get(0)).asText());
		
		createMember(lCreateMemberPage);
	}

	private HtmlPage createMember(HtmlPage inCreateMemberPage) throws IOException {
		final HtmlForm lForm = inCreateMemberPage.getFormByName("Form");
		setInput(lForm, "fldUserID", "testactor");
		setInput(lForm, "fldName", "Actor");
		setInput(lForm, "fldFirstname", "Test");
		setInput(lForm, "fldStreet", "Highway 2");
		setInput(lForm, "fldZIP", "2234");
		setInput(lForm, "fldCity", "Heaven");
		setInput(lForm, "fldMail", "test1@localhost");
		
		//make it male
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
		HtmlPage lSuccess = (HtmlPage)lSend.click();
		return lSuccess;
	}

	private HtmlPage getNewMembersForm() throws Exception {
		HtmlPage lFrameSet = (HtmlPage)webClient.getPage(getRequestAdminBody("org.hip.vif.admin.member.newMember"));
		return (HtmlPage) lFrameSet.getFrameByName("body").getEnclosedPage();
	}
	
}
