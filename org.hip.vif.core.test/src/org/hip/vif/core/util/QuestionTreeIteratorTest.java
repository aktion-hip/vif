package org.hip.vif.core.util;

import static org.junit.Assert.*;
import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.impl.JoinCompletionToQuestion;
import org.hip.vif.core.bom.impl.JoinQuestionToChild;
import org.hip.vif.core.bom.impl.JoinSubscriptionToMember;
import org.hip.vif.core.bom.impl.QuestionHierarchyVisitor;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Feb 17, 2004
 */
public class QuestionTreeIteratorTest {
	private static DataHouseKeeper data;
	private Long[] questionIDs = new Long[11];
	
	private class TestVisitor extends Object implements QuestionHierarchyVisitor {
		private StringBuffer questions = new StringBuffer();
		public TestVisitor() {
			super();
		}
		public void visitCompletion(Completion inCompletion) throws VException, SQLException {
			fail("Should not visitCompletion!");
		}	
		public void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException {
			fail("Should not visitCompletion!");
		}	
		public void visitQuestion(Question inQuestion) throws VException, SQLException {
			questions.append((String)inQuestion.get(QuestionHome.KEY_QUESTION)).append("; ");
		}	
		public void visitSubscriber(JoinSubscriptionToMember inSubscriber) throws VException, SQLException {
			fail("Should not visitSubscriber!");
		}	
		public void visitChild(JoinQuestionToChild inChild) throws VException, SQLException {
			fail("Should not visitChild!");
		}
		public String getQuestions() {
			return new String(questions);
		}
		public void visitText(Text inText) throws VException, SQLException {
			fail("Should not visitText!");
		}
	}
	private class TestChecker1 extends Object implements INodeCheckedProcessor {
		public boolean checkPreCondition(Long inQuestionID) {
			return true;
		}
		public void doAction(Long inQuestionID) throws WorkflowException, VException, SQLException {
			// Do nothing
		}
	}
	private class TestChecker2 extends Object implements INodeCheckedProcessor {
		private String state;
		private QuestionHome home = data.getQuestionHome();
		public TestChecker2(int inStateToCheck) {
			super();
			state = String.valueOf(inStateToCheck);
		}
		public boolean checkPreCondition(Long inQuestionID) {
			try {
				return state.equals(home.getQuestion(inQuestionID.toString()).get(QuestionHome.KEY_STATE).toString());
			}
			catch (Exception exc) {
				return false;
			}
		}
		public void doAction(Long inQuestionID) throws WorkflowException, VException, SQLException {
			// Do nothing
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		createHiarachy();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestion();
		data.deleteAllFromQuestionHierarchy();
	}

	@Test
	public void testStart() throws Exception {
		QuestionTreeIterator lIterator = new QuestionTreeIterator(new Long(questionIDs[9]));
		TestVisitor lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		assertEquals("iteration 1", "Q10; Q4; Q1; ", lVisitor.getQuestions());
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[8]));
		lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		assertEquals("iteration 2", "Q9; Q4; Q1; ", lVisitor.getQuestions());
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[4]));
		lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		assertEquals("iteration 3", "Q5; Q2; Q1; ", lVisitor.getQuestions());
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[2]));
		lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		assertEquals("iteration 4", "Q3; Q1; ", lVisitor.getQuestions());
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[8]));
		lVisitor = new TestVisitor();
		lIterator.start(false, lVisitor);
		assertEquals("iteration 5", "Q4; Q1; ", lVisitor.getQuestions());
		
		lIterator = new QuestionTreeIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[10]), true, lVisitor);
		lIterator.start(new Long(questionIDs[9]), true, lVisitor);
		assertEquals("iteration 6", "Q11; Q4; Q1; Q10; ", lVisitor.getQuestions());
	}
	
	@Test
	public void testStartProcess() throws Exception {
		QuestionTreeIterator lIterator = new QuestionTreeIterator(new Long(questionIDs[9]));
		INodeCheckedProcessor lChecker = new TestChecker1();
		assertEquals("root node ID 1", questionIDs[0], lIterator.start(true, lChecker));
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[4]));
		lChecker = new TestChecker2(WorkflowAwareContribution.S_ANSWERED_REQUESTED);
		assertEquals("node ID 1", questionIDs[4], lIterator.start(true, lChecker));
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[9]));
		lChecker = new TestChecker2(WorkflowAwareContribution.S_ANSWERED);
		assertEquals("node ID 2", questionIDs[3], lIterator.start(true, lChecker));
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[10]));
		lChecker = new TestChecker2(WorkflowAwareContribution.S_ANSWERED);
		assertEquals("node ID 3", questionIDs[3], lIterator.start(true, lChecker));
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[7]));
		lChecker = new TestChecker2(WorkflowAwareContribution.S_OPEN);
		assertEquals("root node ID 2", questionIDs[0], lIterator.start(true, lChecker));
		
		lIterator = new QuestionTreeIterator(new Long(questionIDs[7]));
		lChecker = new TestChecker2(WorkflowAwareContribution.S_DELETED);
		assertNull("no node", lIterator.start(true, lChecker));
	}
	
	private void createHiarachy() throws Exception {
		Long lGroupID = new Long(99);
		questionIDs[0]  = data.createQuestion("Q1",  "1", lGroupID, WorkflowAwareContribution.S_OPEN, true);
		questionIDs[1]  = data.createQuestion("Q2",  "1.1", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[2]  = data.createQuestion("Q3",  "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[3]  = data.createQuestion("Q4",  "1.3", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		questionIDs[4]  = data.createQuestion("Q5",  "1.1.1", lGroupID, WorkflowAwareContribution.S_ANSWERED_REQUESTED, false);
		questionIDs[5]  = data.createQuestion("Q6",  "1.1.2", lGroupID, WorkflowAwareContribution.S_ANSWERED_REQUESTED, false);
		questionIDs[6]  = data.createQuestion("Q7",  "1.2.1", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[7]  = data.createQuestion("Q8",  "1.2.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[8]  = data.createQuestion("Q9",  "1.3.1", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		questionIDs[9]  = data.createQuestion("Q10", "1.3.2", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		questionIDs[10] = data.createQuestion("Q11", "1.3.3", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		
		data.createQuestionHierachy(questionIDs[0], questionIDs[1], lGroupID);
		data.createQuestionHierachy(questionIDs[0], questionIDs[2], lGroupID);
		data.createQuestionHierachy(questionIDs[0], questionIDs[3], lGroupID);
		
		data.createQuestionHierachy(questionIDs[1], questionIDs[4], lGroupID);
		data.createQuestionHierachy(questionIDs[1], questionIDs[5], lGroupID);
		
		data.createQuestionHierachy(questionIDs[2], questionIDs[6], lGroupID);
		data.createQuestionHierachy(questionIDs[2], questionIDs[7], lGroupID);
		
		data.createQuestionHierachy(questionIDs[3], questionIDs[8], lGroupID);
		data.createQuestionHierachy(questionIDs[3], questionIDs[9], lGroupID);
		data.createQuestionHierachy(questionIDs[3], questionIDs[10], lGroupID);
	}

}
