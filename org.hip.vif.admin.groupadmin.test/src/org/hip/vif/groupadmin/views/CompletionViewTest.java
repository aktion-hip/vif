package org.hip.vif.groupadmin.views;

import java.sql.SQLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.servlet.impl.FullyQualifiedNameStrategy;
import org.hip.vif.bom.Completion;
import org.hip.vif.bom.CompletionAuthorReviewerHome;
import org.hip.vif.bom.Group;
import org.hip.vif.bom.GroupHome;
import org.hip.vif.bom.Question;
import org.hip.vif.bom.ResponsibleHome;
import org.hip.vif.bom.impl.BOMHelper;
import org.hip.vif.bom.impl.WorkflowAwareContribution;
import org.hip.vif.bom.impl.test.DataHouseKeeper;
import org.hip.vif.search.test.IndexHouseKeeper;
import org.hip.vif.test.resources.TestPrintWriter;
import org.hip.vif.test.resources.TestViewHelper;
import org.w3c.dom.Document;

/**
 * @author Luthiger
 * Created: 29.08.2010
 */
public class CompletionViewTest extends TestCase {
	private static final String EDITOR_ID = "editor1";
	private static final String EDITOR_SCRIPT = "<!-- javascript -->";
	
	private DataHouseKeeper data;

	public CompletionViewTest(String name) {
		super(name);
		data = DataHouseKeeper.getInstance();
	}

	protected void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}

	protected void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	private void createSibling(String inCompletionText, String inQuestionID, Long inAuthorID) throws Exception {
		String lCompletionID = data.createCompletion(inCompletionText, inQuestionID, WorkflowAwareContribution.S_OPEN);
		DomainObject lLink = data.getCompletionAuthorReviewerHome().create();
		lLink.set(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, new Long(lCompletionID));
		lLink.set(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
		lLink.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lLink.insert(true);
	}
	
	private QueryResult getSiblings(String inQuestionID, String inCompletionID, Long inActorID) throws Exception {
		return data.getJoinCompletionToMemberHome().selectAuthorViewOfSiblings(inQuestionID, inActorID, inCompletionID);
	}
	
	public void testRenderEdit() throws Exception {
		Long lActor = new Long(data.createMember());
		String lQuestionText = "Question to complete";
		String lCompletionText = "Completion to test";
		String lSiblingText = "A sibling completion.";
		String lQuestionID = data.createQuestion(lQuestionText, "1:1.1");
		String lCompletionID = data.createCompletion(lCompletionText, lQuestionID, WorkflowAwareContribution.S_OPEN);
		createSibling(lSiblingText, lQuestionID, lActor);
		
		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		lGroup.set(GroupHome.KEY_NAME, "Group for testing");
		
		TestPrintWriter lWriter = new TestPrintWriter();

		//test
		CompletionViewSub lView = new CompletionViewSub(TestViewHelper.getTestContext("de"), data.getCompletionHome().getCompletion(lCompletionID),
				EDITOR_SCRIPT, EDITOR_ID, getSiblings(lQuestionID, lCompletionID, lActor), data.getQuestionHome().getQuestion(lQuestionID));
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("request type", ".saveCompletion", lXPath.evaluate("//form/input[@name='requestType']/@value", lHTML, XPathConstants.STRING));
		assertEquals("question id", lQuestionID, lXPath.evaluate("//form/input[@name='questionID']/@value", lHTML, XPathConstants.STRING));
		assertEquals("completion id", lCompletionID, lXPath.evaluate("//form/input[@name='completionID']/@value", lHTML, XPathConstants.STRING));
		assertEquals("question text", lQuestionText, lXPath.evaluate("//form/table/tr[1]/td[2]/p", lHTML, XPathConstants.STRING));
		assertEquals("sibling", lSiblingText, lXPath.evaluate("//form/table/tr[3]/td[2]/p", lHTML, XPathConstants.STRING));
		assertEquals("completion edit", lCompletionText, lXPath.evaluate("//form/table/tr[5]/td/textarea[@id='editor1']", lHTML, XPathConstants.STRING));
		assertEquals("button", "Speichern", lXPath.evaluate("//form/table/tr[8]/td/input[@type='submit']/@value", lHTML, XPathConstants.STRING));
		
		//labels de
		assertEquals("question label", "Frage", lXPath.evaluate("//form/table/tr[1]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("question label", "Bemerkung", lXPath.evaluate("//form/table/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("completions label", "Ergänzung", lXPath.evaluate("//form/table/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("completion label", "Editieren:", lXPath.evaluate("//form/table/tr[4]/td[1]", lHTML, XPathConstants.STRING));

		//labels en
		lWriter = new TestPrintWriter();		
		lView = new CompletionViewSub(TestViewHelper.getTestContext("en"), data.getCompletionHome().getCompletion(lCompletionID),
				EDITOR_SCRIPT, EDITOR_ID, getSiblings(lQuestionID, lCompletionID, lActor), data.getQuestionHome().getQuestion(lQuestionID));
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();
		assertEquals("question label", "Question", lXPath.evaluate("//form/table/tr[1]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("question label", "Remark", lXPath.evaluate("//form/table/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("completions label", "Completion", lXPath.evaluate("//form/table/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("completion label", "Edit:", lXPath.evaluate("//form/table/tr[4]/td[1]", lHTML, XPathConstants.STRING));
	}
	
	public void testRenderNew() throws Exception {
		Long lActor = new Long(data.createMember());
		String lQuestionText = "Question to complete";
		String lCompletionText = "Completion to test";
		String lSiblingText = "A sibling completion.";
		String lQuestionID = data.createQuestion(lQuestionText, "1:1.1");
		createSibling(lSiblingText, lQuestionID, lActor);
		createSibling("Something completely different.", lQuestionID, lActor);
		
		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		lGroup.set(GroupHome.KEY_NAME, "Group for testing");
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test
		CompletionViewSub lView = new CompletionViewSub(TestViewHelper.getTestContext("de"), lCompletionText, EDITOR_SCRIPT, EDITOR_ID, 
				data.getQuestionHome().getQuestion(lQuestionID), getSiblings2(lQuestionID, lActor), lGroup);
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("request type", ".saveCompletionNew", lXPath.evaluate("//form/input[@name='requestType']/@value", lHTML, XPathConstants.STRING));
		assertEquals("question id", lQuestionID, lXPath.evaluate("//form/input[@name='questionID']/@value", lHTML, XPathConstants.STRING));
		assertEquals("question text", lQuestionText, lXPath.evaluate("//form/table/tr[1]/td[2]/p", lHTML, XPathConstants.STRING));
		assertEquals("sibling 1", lSiblingText, lXPath.evaluate("//form/table/tr[3]/td[2]/p", lHTML, XPathConstants.STRING));
		assertEquals("sibling 2", "Something completely different.", lXPath.evaluate("//form/table/tr[4]/td[2]/p", lHTML, XPathConstants.STRING));
		assertEquals("completion edit", lCompletionText, lXPath.evaluate("//form/table/tr[6]/td/textarea[@id='editor1']", lHTML, XPathConstants.STRING));
		assertEquals("button", "Speichern", lXPath.evaluate("//form/table/tr[8]/td/input[@type='submit']/@value", lHTML, XPathConstants.STRING));

	}
	
	private QueryResult getSiblings2(String inQuestionID, Long inActorID) throws Exception {
		return data.getJoinCompletionToMemberHome().getAuthorView(new Long(inQuestionID), inActorID);
	}

// ---
	
	private class CompletionViewSub extends CompletionView {

		//edit
		public CompletionViewSub(Context inContext, Completion inCompletion, String inScriptSetup, String inEditorID,
				QueryResult inSiblings, Question inQuestion) throws SQLException, VException {
			super(inContext, inCompletion, inSiblings, inQuestion, inScriptSetup, inEditorID);
		}

		//new
		public CompletionViewSub(Context inContext, String inCompletion, String inScriptSetup, String inEditorID,
				DomainObject inQuestion, QueryResult inCompletions, Group inGroup) throws SQLException, VException {
			super(inContext, inCompletion, inQuestion, inCompletions, inGroup, inScriptSetup, inEditorID);
		}
		
		@Override
		protected ISourceCreatorStrategy getSourceStrategy() {
			return new FullyQualifiedNameStrategy(getRelativeXSLName());
		}
		
		private String getRelativeXSLName() {
			return DOCS_ROOT + getSubAppPath() + this.getLanguage() + "/" + getXMLName();
		}
		
		protected String getSubAppPath() {
			return "..\\org.hip.vif.admin.groupadmin\\resources\\";
		}
	}
	
}
