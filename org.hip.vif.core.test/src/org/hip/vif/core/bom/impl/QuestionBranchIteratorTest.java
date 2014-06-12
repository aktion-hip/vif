package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.TreeSet;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 12, 2004
 */
public class QuestionBranchIteratorTest {
	private static DataHouseKeeper data;
	Long[] questionIDs;
	String[] expected = {"1", "1.1", "1.2", "1.3", "1.1.1", "1.1.2", "1.2.1", "1.2.2", "1.3.1", "1.3.2", "1.3.3"};
	
	private class TestVisitor extends Object implements QuestionHierarchyVisitor {
		private TreeSet<String> questions = new TreeSet<String>();
		public TestVisitor() {
			super();
		}
		public void visitCompletion(Completion inCompletion) throws VException, SQLException {
			fail("Should not visitCompletion!");
		}	
		public void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException {
			questions.add((String)inCompletion.get(CompletionHome.KEY_COMPLETION));
		}	
		public void visitQuestion(Question inQuestion) throws VException, SQLException {
			questions.add((String)inQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL));
		}	
		public void visitSubscriber(JoinSubscriptionToMember inSubscriber) throws VException, SQLException {
			fail("Should not visitSubscriber!");
		}	
		public void visitChild(JoinQuestionToChild inChild) throws VException, SQLException {
			questions.add((String)inChild.get(QuestionHome.KEY_QUESTION_DECIMAL));
		}
		public Object[] getQuestions() {
			return questions.toArray();
		}
		public void visitText(Text inText) throws VException, SQLException {
			fail("Should not visitText!");
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();		
	}

	@Before
	public void setUp() throws Exception {
		questionIDs = createHierachy();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestionHierarchy();
		data.deleteAllFromQuestion();
		data.deleteAllFromCompletion();
	}

	@Test
	public void testStart() throws Exception {
		String lExpectedCompletion = "00001";

		QuestionBranchIterator lIterator = new QuestionBranchIterator(new Long(questionIDs[0]));
		TestVisitor lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		Object[] lQuestions = lVisitor.getQuestions();
		assertEquals("question 1", expected[0], (String)lQuestions[0]);
		assertEquals("question 2", expected[1], (String)lQuestions[1]);
		assertEquals("question 3", expected[4], (String)lQuestions[2]);
		assertEquals("question 4", expected[7], (String)lQuestions[6]);
		assertEquals("question 5", expected[10], (String)lQuestions[10]);
		assertEquals("length 1", expected.length, lQuestions.length);
		
		lIterator = new QuestionBranchIterator(new Long(questionIDs[3]));
		lVisitor = new TestVisitor();
		lIterator.start(true, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 6", expected[3], (String)lQuestions[0]);
		assertEquals("question 7", expected[8], (String)lQuestions[1]);
		assertEquals("question 8", expected[9], (String)lQuestions[2]);
		assertEquals("question 9", expected[10], (String)lQuestions[3]);
		assertEquals("length 2", 4, lQuestions.length);
		
		lIterator = new QuestionBranchIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[3]), true, false, lVisitor);
		lIterator.start(new Long(questionIDs[2]), true, false, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 10", expected[2], (String)lQuestions[0]);
		assertEquals("question 11", expected[6], (String)lQuestions[1]);
		assertEquals("question 12", expected[7], (String)lQuestions[2]);
		assertEquals("question 13", expected[3], (String)lQuestions[3]);
		assertEquals("question 14", expected[8], (String)lQuestions[4]);
		assertEquals("question 15", expected[9], (String)lQuestions[5]);
		assertEquals("question 16", expected[10], (String)lQuestions[6]);
		assertEquals("length 3", 7, lQuestions.length);
		
		lIterator = new QuestionBranchIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[3]), true, false, lVisitor);
		lIterator.start(new Long(questionIDs[2]), true, false, lVisitor);
		lIterator.start(new Long(questionIDs[9]), true, false, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 17", expected[2], (String)lQuestions[0]);
		assertEquals("question 18", expected[6], (String)lQuestions[1]);
		assertEquals("question 19", expected[7], (String)lQuestions[2]);
		assertEquals("question 20", expected[3], (String)lQuestions[3]);
		assertEquals("question 21", expected[8], (String)lQuestions[4]);
		assertEquals("question 22", expected[9], (String)lQuestions[5]);
		assertEquals("question 23", expected[10], (String)lQuestions[6]);
		assertEquals("length 4", 7, lQuestions.length);

		data.createCompletion(lExpectedCompletion, questionIDs[3]);
		lIterator = new QuestionBranchIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[3]), false, false, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 24", expected[8], (String)lQuestions[0]);
		assertEquals("question 25", expected[9], (String)lQuestions[1]);
		assertEquals("question 26", expected[10], (String)lQuestions[2]);
		assertEquals("length 5", 3, lQuestions.length);
		
		lIterator = new QuestionBranchIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[3]), true, false, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 27", expected[3], (String)lQuestions[0]);
		assertEquals("question 28", expected[8], (String)lQuestions[1]);
		assertEquals("question 29", expected[9], (String)lQuestions[2]);
		assertEquals("question 30", expected[10], (String)lQuestions[3]);
		assertEquals("length 6", 4, lQuestions.length);
		
		lIterator = new QuestionBranchIterator();
		lVisitor = new TestVisitor();
		lIterator.start(new Long(questionIDs[3]), true, true, lVisitor);
		lQuestions = lVisitor.getQuestions();
		assertEquals("question 31", lExpectedCompletion, (String)lQuestions[0]);
		assertEquals("question 32", expected[3], (String)lQuestions[1]);
		assertEquals("question 33", expected[8], (String)lQuestions[2]);
		assertEquals("question 34", expected[9], (String)lQuestions[3]);
		assertEquals("question 35", expected[10], (String)lQuestions[4]);
		assertEquals("length 7", 5, lQuestions.length);
	}

	private Long[] createHierachy()throws Exception {
		Long lGroupID = 2l;
		Long[] outIDs = new Long[11];

		outIDs[0] = data.createQuestion("Q1", expected[0]);
		outIDs[1] = data.createQuestion("Q2", expected[1]);
		outIDs[2] = data.createQuestion("Q3", expected[2]);
		outIDs[3] = data.createQuestion("Q4", expected[3]);
		outIDs[4] = data.createQuestion("Q5", expected[4]);
		outIDs[5] = data.createQuestion("Q6", expected[5]);
		outIDs[6] = data.createQuestion("Q7", expected[6]);
		outIDs[7] = data.createQuestion("Q8", expected[7]);
		outIDs[8] = data.createQuestion("Q9", expected[8]);
		outIDs[9] = data.createQuestion("Q10", expected[9]);
		outIDs[10] = data.createQuestion("Q11", expected[10]);			
		
		data.createQuestionHierachy(outIDs[0], outIDs[1], lGroupID);
		data.createQuestionHierachy(outIDs[0], outIDs[2], lGroupID);
		data.createQuestionHierachy(outIDs[0], outIDs[3], lGroupID);

		data.createQuestionHierachy(outIDs[1], outIDs[4], lGroupID);
		data.createQuestionHierachy(outIDs[1], outIDs[5], lGroupID);

		data.createQuestionHierachy(outIDs[2], outIDs[6], lGroupID);
		data.createQuestionHierachy(outIDs[2], outIDs[7], lGroupID);

		data.createQuestionHierachy(outIDs[3], outIDs[8], lGroupID);
		data.createQuestionHierachy(outIDs[3], outIDs[9], lGroupID);
		data.createQuestionHierachy(outIDs[3], outIDs[10], lGroupID);

		return outIDs;
	}
}
