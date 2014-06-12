package org.hip.vif.groupadmin.views;

import java.math.BigDecimal;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import junit.framework.TestCase;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.EmptyQueryResult;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.servlet.impl.FullyQualifiedNameStrategy;
import org.hip.vif.bom.Group;
import org.hip.vif.bom.GroupHome;
import org.hip.vif.bom.Question;
import org.hip.vif.bom.QuestionHome;
import org.hip.vif.bom.Text;
import org.hip.vif.bom.TextHome;
import org.hip.vif.bom.impl.BOMHelper;
import org.hip.vif.bom.impl.TextQuestionHome;
import org.hip.vif.bom.impl.WorkflowAwareContribution;
import org.hip.vif.bom.impl.test.DataHouseKeeper;
import org.hip.vif.search.test.IndexHouseKeeper;
import org.hip.vif.test.resources.TestPrintWriter;
import org.hip.vif.test.resources.TestViewHelper;
import org.hip.vif.util.IQueryStrategy;
import org.w3c.dom.Document;

/**
 * @author Luthiger
 * Created: 15.08.2010
 */
public class QuestionViewTest extends TestCase {
	private static final String[] EDITOR_IDS = {"editor1", "editor2"};
	private static final String NBSP = new String(new byte[] {-62, -96});

	private DataHouseKeeper data;

	public QuestionViewTest(String inName) {
		super(inName);
		data = DataHouseKeeper.getInstance();
	}

	protected void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}

	protected void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	private void createText(String inQuestionID) throws Exception {
		TextHome lHome = data.getTextHome();
		TextQuestionHome lLinkHome = data.getTextQuestionHome();

		String lTextID1 = data.createText("All Calculations", "Riese, Adam");
		String lTextID2 = data.createText("Design Patterns", "Croft, Lara");
		
		Text lText = lHome.getText(lTextID1, 0);
		lText.set(TextHome.KEY_REFERENCE, "Lit1");
		lText.update(true);
		((WorkflowAwareContribution)lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, new Object[] {new Long(1)});
		lLinkHome.createEntry(lTextID1, inQuestionID);
		
		lText = lHome.getText(lTextID2, 0);
		lText.set(TextHome.KEY_REFERENCE, "Lit2");
		lText.update(true);
		((WorkflowAwareContribution)lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, new Object[] {new Long(1)});
		lLinkHome.createEntry(lTextID2, inQuestionID);
	}
	
	public void testRenderShow() throws Exception {
		String lQuestionText = "Question to test";
		QuestionHome lHome = data.getQuestionHome();
		String lParentID = data.createQuestion("The question's parent", "1:1");
		String lQuestionID = data.createQuestion(lQuestionText, "1:1.1");
		Long lQuestionIDdc = new Long(lQuestionID);
		
		String[] lIds = data.create2Members();
		data.createQuestionProducer(lQuestionIDdc, new Long(lIds[0]), true);
		data.createQuestionProducer(lQuestionIDdc, new Long(lIds[1]), false);

		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		createText(lQuestionID);

		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test german version
		QuestionViewSub lView = new QuestionViewSub(TestViewHelper.getTestContext(), lHome.getQuestion(lQuestionID), lHome.getQuestion(lParentID),
				getEmtpyQueryResult(), getAuthors(lQuestionID), getReviewers(lQuestionID), lGroup, getEmtpyQueryResult(), getPublishedBibliography(lQuestionID));
		
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("Page Title", "Frage: 1:1.1", lXPath.evaluate("//table/tbody/tr[1]/td[1]/span[@class='subtitle']", lHTML, XPathConstants.STRING));
		assertEquals("Page Subtitle", "Status: private", lXPath.evaluate("//table/tbody/tr[1]/td[1]/span[@class='data']", lHTML, XPathConstants.STRING));
		assertEquals("Question Text", lQuestionText, lXPath.evaluate("//table/tbody/tr[2]/td[@class='data']/p", lHTML, XPathConstants.STRING));
		assertEquals("remark label", "Bemerkung", lXPath.evaluate("//table/tbody/tr[3]/td[@class='subtitle']", lHTML, XPathConstants.STRING));
		assertEquals("remark text", "Remark", lXPath.evaluate("//table/tbody/tr[4]/td[@class='data']/p", lHTML, XPathConstants.STRING));		
		assertEquals("name author", "VornameT1"+NBSP+"NameT1", lXPath.evaluate("//table/tbody/tr[5]/td[@class='authoring']/a[1]", lHTML, XPathConstants.STRING));
		assertEquals("name reviewer", "VornameT2"+NBSP+"NameT2", lXPath.evaluate("//table/tbody/tr[5]/td[@class='authoring']/a[2]", lHTML, XPathConstants.STRING));
		String lLookup = "openLookupMember(%s);return false;";
		assertEquals("link lookup author", String.format(lLookup, lIds[0]), lXPath.evaluate("//table/tbody/tr[5]/td[@class='authoring']/a[1]/@onclick", lHTML, XPathConstants.STRING));
		assertEquals("link lookup reviewer", String.format(lLookup, lIds[1]), lXPath.evaluate("//table/tbody/tr[5]/td[@class='authoring']/a[2]/@onclick", lHTML, XPathConstants.STRING));
		
		assertEquals("label bibliography", "Literatur", lXPath.evaluate("//table/tbody/tr[6]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("bibliography ref 1", "Lit1", lXPath.evaluate("//table/tbody/tr[@id='Lit1']/td[1]/a", lHTML, XPathConstants.STRING));
		assertEquals("bibliography text 1", "Riese, Adam (2010). „All Calculations”. 12:8, 44-55.", lXPath.evaluate("//table/tbody/tr[@id='Lit1']/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("bibliography ref 2", "Lit2", lXPath.evaluate("//table/tbody/tr[@id='Lit2']/td[1]/a", lHTML, XPathConstants.STRING));
		assertEquals("bibliography text 2", "Croft, Lara (2010). „Design Patterns”. 12:8, 44-55.", lXPath.evaluate("//table/tbody/tr[@id='Lit2']/td[2]", lHTML, XPathConstants.STRING));
		
		assertEquals("label parent question", "Ausgangsfrage", lXPath.evaluate("//table/tbody/tr[9]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("parent question number", "1:1", lXPath.evaluate("//table/tbody/tr[9]/td[2]/table/tr[2]/td[1]/a", lHTML, XPathConstants.STRING));
		assertEquals("parent question text", "The question's parent", lXPath.evaluate("//table/tbody/tr[9]/td[2]/table/tr[2]/td[2]/a", lHTML, XPathConstants.STRING));
		
		//test english version
		lWriter = new TestPrintWriter();
		lView = new QuestionViewSub(TestViewHelper.getTestContext("en"), lHome.getQuestion(lQuestionID), lHome.getQuestion(lParentID),
				getEmtpyQueryResult(), getAuthors(lQuestionID), getReviewers(lQuestionID), lGroup, getEmtpyQueryResult(), getEmtpyQueryResult());
		
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();
		assertEquals("Page Title", "Question: 1:1.1", lXPath.evaluate("//table/tbody/tr[1]/td[1]/span[@class='subtitle']", lHTML, XPathConstants.STRING));
		assertEquals("Page Subtitle", "State: private", lXPath.evaluate("//table/tbody/tr[1]/td[1]/span[@class='data']", lHTML, XPathConstants.STRING));
		assertEquals("remark label", "Remark", lXPath.evaluate("//table/tbody/tr[3]/td[@class='subtitle']", lHTML, XPathConstants.STRING));
		assertEquals("label parent question", "Parent question", lXPath.evaluate("//table/tbody/tr[6]/td[1]", lHTML, XPathConstants.STRING));
	}
	
	public void testRenderEdit() throws Exception {
		String lQuestionText = "Question to test";
		QuestionHome lHome = data.getQuestionHome();
		String lParentID = data.createQuestion("The question's parent", "1:1");
		String lQuestionID = data.createQuestion(lQuestionText, "1:1.1");
		Long lQuestionIDdc = new Long(lQuestionID);
		
		String[] lIds = data.create2Members();
		data.createQuestionProducer(lQuestionIDdc, new Long(lIds[0]), true);
		data.createQuestionProducer(lQuestionIDdc, new Long(lIds[1]), false);
		
		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		createText(lQuestionID);
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test
		QuestionViewSub lView = new QuestionViewSub(TestViewHelper.getTestContext("de"), lHome.getQuestion(lQuestionID), lHome.getQuestion(lParentID),
				getEmtpyQueryResult(), getAuthors(lQuestionID), getReviewers(lQuestionID), lGroup, getEmtpyQueryResult(), getPublishedBibliography(lQuestionID), 
				"<!-- javascript -->", EDITOR_IDS);
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("request type", ".saveQuestion", lXPath.evaluate("//form/input[@name='requestType' and @type='hidden']/@value", lHTML, XPathConstants.STRING));
		assertEquals("content question", "Question to test", lXPath.evaluate("//table/tbody/tr[2]/td[2]/textarea[@id='editor1' and @name='fldQuestion']", lHTML, XPathConstants.STRING));
		assertEquals("content remark", "Remark", lXPath.evaluate("//table/tbody/tr[3]/td[2]/textarea[@id='editor2' and @name='fldRemark']", lHTML, XPathConstants.STRING));
		assertEquals("state", "privat", lXPath.evaluate("//table/tbody/tr[4]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("author", "VornameT1"+NBSP+"NameT1", lXPath.evaluate("//table/tbody/tr[8]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("reviewer", "VornameT2"+NBSP+"NameT2", lXPath.evaluate("//table/tbody/tr[9]/td[2]", lHTML, XPathConstants.STRING));
		
		//labels de
		assertEquals("label question", "Frage[Formatierung]", lXPath.evaluate("//table/tbody/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label remark", "Bemerkung[Formatierung]", lXPath.evaluate("//table/tbody/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label state", "Status", lXPath.evaluate("//table/tbody/tr[4]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label bibliography", "Literatur", lXPath.evaluate("//table/tbody/tr[5]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label author", "Autor/in", lXPath.evaluate("//table/tbody/tr[8]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label reviewer", "Reviewer/in", lXPath.evaluate("//table/tbody/tr[9]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label button", "Speichern", lXPath.evaluate("//table/tbody/tr[10]/td[1]/input/@value", lHTML, XPathConstants.STRING));
		
		//labels en
		lWriter = new TestPrintWriter();
		lView = new QuestionViewSub(TestViewHelper.getTestContext("en"), lHome.getQuestion(lQuestionID), lHome.getQuestion(lParentID),
				getEmtpyQueryResult(), getAuthors(lQuestionID), getReviewers(lQuestionID), lGroup, getEmtpyQueryResult(), getPublishedBibliography(lQuestionID), 
				"<!-- javascript -->", EDITOR_IDS);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();

		assertEquals("label question", "Question[Formatting]", lXPath.evaluate("//table/tbody/tr[2]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label remark", "Remark[Formatting]", lXPath.evaluate("//table/tbody/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label state", "State", lXPath.evaluate("//table/tbody/tr[4]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label bibliography", "Bibliography", lXPath.evaluate("//table/tbody/tr[5]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label author", "Author", lXPath.evaluate("//table/tbody/tr[8]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label reviewer", "Reviewer", lXPath.evaluate("//table/tbody/tr[9]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label button", "Save", lXPath.evaluate("//table/tbody/tr[10]/td[1]/input/@value", lHTML, XPathConstants.STRING));
	}
	
	public void testRenderNew() throws Exception {
		String lQuestion = "The new question?";
		String lRemark = "The new question's reason.";
		String lParent = "The parent";
		
		QuestionHome lHome = data.getQuestionHome();
		String lParentID = data.createQuestion(lParent, "1:1");

		Group lGroup = (Group) BOMHelper.getGroupHome().create();
		lGroup.set(GroupHome.KEY_ID, new Long(8));
		lGroup.set(GroupHome.KEY_NAME, "Group for testing");
		
		TestPrintWriter lWriter = new TestPrintWriter();
		
		//test
		QuestionViewSub lView = new QuestionViewSub(TestViewHelper.getTestContext("de"), lQuestion, lRemark, 
				"<!-- javascript -->", EDITOR_IDS, lHome.getQuestion(lParentID), lGroup);
		lView.renderToWriter(lWriter, "sessionId");
		Document lHTML = lWriter.getXML();
		XPath lXPath = TestViewHelper.getXPath();
		assertEquals("request type", ".saveQuestionNew", lXPath.evaluate("//form/input[@name='requestType' and @type='hidden']/@value", lHTML, XPathConstants.STRING));
		assertEquals("parent question", lParent, lXPath.evaluate("//table/tr[1]/td[2]/p", lHTML, XPathConstants.STRING).toString());
		assertEquals("content question", lQuestion, lXPath.evaluate("//table/tr[4]/td/textarea[@id='editor1' and @name='fldQuestion']", lHTML, XPathConstants.STRING));
		assertEquals("content remark", lRemark, lXPath.evaluate("//table/tr[6]/td/textarea[@id='editor2' and @name='fldRemark']", lHTML, XPathConstants.STRING));
		
		//labels de
		assertEquals("label parent", "Ausgangsfrage", lXPath.evaluate("//table/tr[1]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label question", "Frage", lXPath.evaluate("//table/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label formating", "[Formatierung]", lXPath.evaluate("//table/tr[3]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("label remark", "Bemerkung", lXPath.evaluate("//table/tr[5]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label button", "Speichern", lXPath.evaluate("//table/tr[8]/td[1]/input/@value", lHTML, XPathConstants.STRING));

		//labels en
		lWriter = new TestPrintWriter();
		lView = new QuestionViewSub(TestViewHelper.getTestContext("en"), lQuestion, lRemark, 
				"<!-- javascript -->", EDITOR_IDS, lHome.getQuestion(lParentID), lGroup);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();
		assertEquals("label parent", "Parent question", lXPath.evaluate("//table/tr[1]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label question", "Question", lXPath.evaluate("//table/tr[3]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label formating", "[Formatting]", lXPath.evaluate("//table/tr[3]/td[2]", lHTML, XPathConstants.STRING));
		assertEquals("label remark", "Remark", lXPath.evaluate("//table/tr[5]/td[1]", lHTML, XPathConstants.STRING));
		assertEquals("label button", "Save", lXPath.evaluate("//table/tr[8]/td[1]/input/@value", lHTML, XPathConstants.STRING));
		
		lWriter = new TestPrintWriter();
		lView = new QuestionViewSub(TestViewHelper.getTestContext("en"), lQuestion, lRemark, "<!-- javascript -->", EDITOR_IDS, null, lGroup);
		lView.renderToWriter(lWriter, "sessionId");
		lHTML = lWriter.getXML();
		lXPath = TestViewHelper.getXPath();
		assertEquals("no parent", "Question", lXPath.evaluate("//table/tr[1]/td[1]", lHTML, XPathConstants.STRING));
	}

	private IQueryStrategy getAuthors(final String inQuestionID) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return data.getJoinQuestionToAuthorReviewerHome().getAuthors(inQuestionID);
			}				
		};
	}

	private IQueryStrategy getReviewers(final String inQuestionID) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return data.getJoinQuestionToAuthorReviewerHome().getReviewers(inQuestionID);
			}				
		};
	}
	
	private IQueryStrategy getPublishedBibliography(final String inQuestionID) {
		return new IQueryStrategy() {
			public QueryResult getQueryResult() throws Exception {
				return BOMHelper.getJoinQuestionToTextHome().selectPublished(new BigDecimal(inQuestionID));
			}				
		};
	}
	
	private IQueryStrategy getEmtpyQueryResult() {
		return new IQueryStrategy() {
			@Override
			public QueryResult getQueryResult() throws Exception {
				return new EmptyQueryResult(data.getQuestionHome());
			}
		};
	}
	
// --- 
	
	private class QuestionViewSub extends QuestionView {
		//show
		public QuestionViewSub(Context inContext, Question inQuestion, Question inParent, IQueryStrategy inChildren,
				IQueryStrategy inAuthors, IQueryStrategy inReviewers,  Group inGroup, IQueryStrategy inCompletions,
				IQueryStrategy inBibliography) throws Exception {
			super(inContext, inQuestion, inParent, inChildren, inAuthors, inReviewers, inGroup, inCompletions, inBibliography);
		}
		
		//edit
		public QuestionViewSub(Context inContext, Question inQuestion, Question inParent, IQueryStrategy inChildren,
				IQueryStrategy inAuthors, IQueryStrategy inReviewers, Group inGroup, IQueryStrategy inCompletions, IQueryStrategy inBibliography, 
				String inScriptSetup, String[] inEditorIDs) throws Exception {
			super(inContext, inQuestion, inParent, inChildren, inAuthors, inReviewers, inGroup, inCompletions, inBibliography, inScriptSetup, inEditorIDs);
		}

		//new
		public QuestionViewSub(Context inContext, String inQuestion, String inRemark, String inScriptSetup, String[] inEditorIDs,
				DomainObject inParent, Group inGroup) throws GettingException {
			super(inContext, inQuestion, inRemark, inParent, inGroup, inScriptSetup, inEditorIDs);
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
