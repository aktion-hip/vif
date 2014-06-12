package org.hip.vif.admin.admin.internal.test;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.admin.internal.GroupExtent;
import org.hip.vif.bom.Group;
import org.hip.vif.bom.Question;
import org.hip.vif.bom.VIFGroupWorkflow;
import org.hip.vif.bom.impl.WorkflowAwareContribution;
import org.hip.vif.bom.impl.test.DataHouseKeeper;
import org.hip.vif.markup.serializer.MarkupToHtmlSerializer;
import org.hip.vif.search.test.IndexHouseKeeper;
import org.hip.vif.util.GroupStateChangeParameters;

/**
 * @author Luthiger
 * Created 20.09.2009
 */
public class GroupExtentTest extends TestCase {
	private DataHouseKeeper data;
	private Object[] actorID = new Object[] {new Long(96)};
	
	public GroupExtentTest(String name) {
		super(name);
		data = DataHouseKeeper.getInstance();
	}

	protected void setUp() throws Exception {
		super.setUp();
		IndexHouseKeeper.redirectDocRoot(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	public void testCreation() throws Exception {
		//preparation: create group and open it
		String lGroupID = data.createGroup();
 	 	Group lGroup = data.getGroupHome().getGroup(lGroupID);
 	 	((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {new GroupStateChangeParameters("en")});
		
 	 	//preparation: create two members who will be the authors of the question
 	 	String[] lAuthorIDs = data.create2Members();

 	 	//three questions: root and two children
 	 	String lQuestion1 = "Root question for test.";
 	 	String lQuestion2 = "For test: First follow up question.";
 	 	String lQuestion3 = "For test: Second follow up question.";
 	 	
		String lQuestionID1 = createQuestion(lQuestion1, "1:1", lGroupID, true, lAuthorIDs[0]);
		createQuestion(lQuestion2, "1:1.1", lGroupID, false, lAuthorIDs[1]);
		String lQuestionID3 = createQuestion(lQuestion3, "1:1.2", lGroupID, false, lAuthorIDs[0]);
		
		//three completions
		String lCompletion1 = "Root has one completion.";
		String lCompletion2 = "Root has another completion.";
		String lCompletion3 = "A completion for a follow up question.";
		
		String lCompletionID = data.createCompletion(lCompletion1, lQuestionID1, WorkflowAwareContribution.S_OPEN);
		data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[1]), true);
		
		lCompletionID = data.createCompletion(lCompletion2, lQuestionID1, WorkflowAwareContribution.S_OPEN);
		data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[1]), true);
		data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[0]), false);

		lCompletionID = data.createCompletion(lCompletion3, lQuestionID3, WorkflowAwareContribution.S_OPEN);
		data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[0]), true);
		
		//start the test
		GroupExtent lExtent = new GroupExtent(new Long(lGroupID));
		
		XMLSerializer lSerializer = new MarkupToHtmlSerializer();
		lExtent.getGroup().accept(lSerializer);
		String lSerialized = lSerializer.toString();
		System.out.println(lSerialized);
		assertEquals("Group tag", 2, count(Pattern.compile("<?Group>").matcher(lSerialized)));
		assertEquals("Group description node", 1, count(Pattern.compile("<Description><p>Group Nr. 1</p></Description>").matcher(lSerialized)));		
		
		lSerialized = serialize(lExtent.getQuestions(), lSerializer);
		System.out.println(lSerialized);
		assertEquals("number of entries", 3, count(Pattern.compile("<propertySet>").matcher(lSerialized)));
		
		String lNode = "<Question><p>%s</p></Question>";
		assertEquals("question 1", 1, count(Pattern.compile(String.format(lNode, lQuestion1)).matcher(lSerialized)));
		assertEquals("question 2", 1, count(Pattern.compile(String.format(lNode, lQuestion2)).matcher(lSerialized)));
		assertEquals("question 3", 1, count(Pattern.compile(String.format(lNode, lQuestion3)).matcher(lSerialized)));
		
		lSerialized = serialize(lExtent.getCompletions(), lSerializer);
		System.out.println(lSerialized);
		lNode = "<Completion><p>%s</p></Completion>";
		assertEquals("number of entries", 4, count(Pattern.compile("<propertySet>").matcher(lSerialized)));
		assertEquals("completion 1", 1, count(Pattern.compile(String.format(lNode, lCompletion1)).matcher(lSerialized)));
		assertEquals("completion 2", 2, count(Pattern.compile(String.format(lNode, lCompletion2)).matcher(lSerialized)));
		assertEquals("completion 3", 1, count(Pattern.compile(String.format(lNode, lCompletion3)).matcher(lSerialized)));
	}
	

	private int count(Matcher inMatcher) {
		int i = 0;
		while (inMatcher.find()) {
			i++;
		}
		return i;
	}

	private String serialize(QueryResult inResult, XMLSerializer inSerializer) throws BOMException, SQLException {
		inSerializer.clear();
		while (inResult.hasMoreElements()) {
			inResult.next().accept(inSerializer);
		}
		return inSerializer.toString();
	}

	private String createQuestion(String inQuestion, String inDecimal, String inGroupID, boolean isRoot, String inAuthorID) throws VException, SQLException, WorkflowException {
		String outQuestionID = data.createQuestion(inQuestion, inDecimal, new Long(inGroupID), isRoot);
		Question lQuestion = data.getQuestionHome().getQuestion(outQuestionID);
		((WorkflowAwareContribution)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, actorID);
		data.createQuestionProducer(new Long(outQuestionID), new Long(inAuthorID), true);
		return outQuestionID;
	}

}
