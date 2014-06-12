package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinQuestionToChildHomeTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class JoinQuestionToChildHomeSub extends JoinQuestionToChildHome {
		public JoinQuestionToChildHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, new Integer(32));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestion();
		data.deleteAllFromQuestionHierarchy();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestionHierarchy.PARENTID, tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.BROOTQUESTION, tblQuestion.GROUPID, tblQuestion.NSTATE, tblQuestion.DTMUTATION FROM tblQuestionHierarchy INNER JOIN tblQuestion ON tblQuestionHierarchy.CHILDID = tblQuestion.QUESTIONID WHERE tblQuestionHierarchy.PARENTID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblQuestionHierarchy.ParentID, tblQuestion.QuestionID, tblQuestion.sQuestionID, tblQuestion.sQuestion, tblQuestion.sRemark, tblQuestion.bRootQuestion, tblQuestion.GroupID, tblQuestion.nState, tblQuestion.dtMutation FROM tblQuestionHierarchy, tblQuestion WHERE tblQuestionHierarchy.ChildID = tblQuestion.QuestionID AND tblQuestionHierarchy.ParentID = 32";
		}
		JoinQuestionToChildHome lSubHome = new JoinQuestionToChildHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}

	@Test
	public void testGetChilds() throws Exception {
		String[] lQuestions = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11"};
		Long[] lQuestionIDs = createQuestionsWithHierachy(lQuestions);
		JoinQuestionToChildHome lHome = data.getJoinQuestionToChildHome();

		QueryResult lChilds = lHome.getChilds(lQuestionIDs[0]);
		Vector<Object> lTexts = getQuestionTexts(lChilds);
		assertEquals("number of childs 1", 3, lTexts.size());
		assertTrue("contains child 1", lTexts.contains(lQuestions[1]));
		assertTrue("contains child 2", lTexts.contains(lQuestions[2]));
		assertTrue("contains child 3", lTexts.contains(lQuestions[3]));
		assertTrue("not contains child 1", !lTexts.contains(lQuestions[4]));
		
		lChilds = lHome.getChilds(lQuestionIDs[1]);
		lTexts = getQuestionTexts(lChilds);
		assertEquals("number of childs 2", 2, lTexts.size());
		assertTrue("contains child 4", lTexts.contains(lQuestions[4]));
		assertTrue("contains child 5", lTexts.contains(lQuestions[5]));
		assertTrue("not contains child 2", !lTexts.contains(lQuestions[2]));
		
		lChilds = lHome.getChilds(lQuestionIDs[2]);
		lTexts = getQuestionTexts(lChilds);
		assertEquals("number of childs 3", 2, lTexts.size());
		assertTrue("contains child 6", lTexts.contains(lQuestions[6]));
		assertTrue("contains child 7", lTexts.contains(lQuestions[7]));
		assertTrue("not contains child 2", !lTexts.contains(lQuestions[4]));

		lChilds = lHome.getChilds(lQuestionIDs[3]);
		lTexts = getQuestionTexts(lChilds);
		assertEquals("number of childs 4", 3, lTexts.size());
		assertTrue("contains child 8", lTexts.contains(lQuestions[8]));
		assertTrue("contains child 9", lTexts.contains(lQuestions[9]));
		assertTrue("contains child 10", lTexts.contains(lQuestions[10]));
		assertTrue("not contains child 2", !lTexts.contains(lQuestions[6]));
	}

	@Test
	public void testGetSiblings() throws Exception {
		String[] lQuestions = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11"};
		Long[] lQuestionIDs = createQuestionsWithHierachy(lQuestions);
		JoinQuestionToChildHome lHome = data.getJoinQuestionToChildHome();

		QueryResult lSiblings = lHome.getSiblings(lQuestionIDs[0]);
		Vector<Object> lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 1", 1, lTexts.size());
		assertTrue("contains sibling 1", lTexts.contains(lQuestions[0]));
		assertTrue("not contains sibling 1", !lTexts.contains(lQuestions[1]));

		lSiblings = lHome.getSiblings(lQuestionIDs[1]);
		lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 2", 3, lTexts.size());
		assertTrue("contains sibling 2", lTexts.contains(lQuestions[1]));
		assertTrue("contains sibling 3", lTexts.contains(lQuestions[2]));
		assertTrue("contains sibling 4", lTexts.contains(lQuestions[3]));
		assertTrue("not contains sibling 2", !lTexts.contains(lQuestions[0]));

		lSiblings = lHome.getSiblings(lQuestionIDs[5]);
		lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 2", 2, lTexts.size());
		assertTrue("contains sibling 5", lTexts.contains(lQuestions[4]));
		assertTrue("contains sibling 6", lTexts.contains(lQuestions[5]));
		assertTrue("not contains sibling 3", !lTexts.contains(lQuestions[6]));

		lSiblings = lHome.getSiblings(lQuestionIDs[7]);
		lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 2", 2, lTexts.size());
		assertTrue("contains sibling 7", lTexts.contains(lQuestions[6]));
		assertTrue("contains sibling 8", lTexts.contains(lQuestions[7]));
		assertTrue("not contains sibling 4", !lTexts.contains(lQuestions[4]));

		lSiblings = lHome.getSiblings(lQuestionIDs[8]);
		lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 2", 3, lTexts.size());
		assertTrue("contains sibling 9", lTexts.contains(lQuestions[8]));
		assertTrue("contains sibling 10", lTexts.contains(lQuestions[9]));
		assertTrue("contains sibling 11", lTexts.contains(lQuestions[10]));
		assertTrue("not contains sibling 5", !lTexts.contains(lQuestions[4]));

		lSiblings = lHome.getSiblings(lQuestionIDs[4]);
		lTexts = getQuestionTexts(lSiblings);
		assertEquals("number of siblings 2", 2, lTexts.size());
		assertTrue("contains sibling 5", lTexts.contains(lQuestions[4]));
		assertTrue("contains sibling 6", lTexts.contains(lQuestions[5]));
	}
	
	@Test
	public void testGetPublishedChilds() throws Exception {
		String[] lQuestions = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11"};
		Long[] lQuestionIDs = createQuestionsWithHierachy(lQuestions);
		JoinQuestionToChildHome lHome = data.getJoinQuestionToChildHome();
		
		Vector<Object> lTexts = getQuestionTexts(lHome.getPublishedChilds(lQuestionIDs[0]));
		assertEquals("number of published childs 1", 2, lTexts.size());
		assertTrue("contains published 1", lTexts.containsAll(Arrays.asList(new String[] {lQuestions[1], lQuestions[2]})));
		assertTrue("not contains child 1", !lTexts.contains(lQuestions[3]));
		
		lTexts = getQuestionTexts(lHome.getPublishedChilds(lQuestionIDs[1]));
		assertEquals("number of published childs 1", 2, lTexts.size());
		assertTrue("contains published 1", lTexts.containsAll(Arrays.asList(new String[] {lQuestions[4], lQuestions[5]})));
		assertTrue("not contains child 1", !lTexts.contains(lQuestions[3]));
		
		lTexts = getQuestionTexts(lHome.getPublishedChilds(lQuestionIDs[2]));
		assertEquals("number of published childs 1", 2, lTexts.size());
		assertTrue("contains published 1", lTexts.containsAll(Arrays.asList(new String[] {lQuestions[6], lQuestions[7]})));
		assertTrue("not contains child 1", !lTexts.contains(lQuestions[8]));
		
		lTexts = getQuestionTexts(lHome.getPublishedChilds(lQuestionIDs[3]));
		assertEquals("number of published childs 1", 0, lTexts.size());
		assertTrue("contains published 1", lTexts.containsAll(Arrays.asList(new String[] {})));
		assertTrue("not contains child 1", !lTexts.contains(lQuestions[9]));
	}
	
	private Long[] createQuestionsWithHierachy(String[] inQuestions) throws VException, SQLException {
		Long lGroupID = 15l;
		Long[] lIDs = new Long[11];
		lIDs[0] = data.createQuestion(inQuestions[0], "1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, true);
		lIDs[1] = data.createQuestion(inQuestions[1], "1.1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false);
		lIDs[2] = data.createQuestion(inQuestions[2], "1.2", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false);
		lIDs[3] = data.createQuestion(inQuestions[3], "1.3", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false);
		lIDs[4] = data.createQuestion(inQuestions[4], "1.1.1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false);
		lIDs[5] = data.createQuestion(inQuestions[5], "1.1.2", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false);
		lIDs[6] = data.createQuestion(inQuestions[6], "1.2.1", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false);
		lIDs[7] = data.createQuestion(inQuestions[7], "1.2.3", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false);
		lIDs[8] = data.createQuestion(inQuestions[8], "1.3.1", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false);
		lIDs[9] = data.createQuestion(inQuestions[9], "1.3.2", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false);
		lIDs[10] = data.createQuestion(inQuestions[10], "1.3.3", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false);
		
		data.createQuestionHierachy(lIDs[0], lIDs[1], lGroupID);
		data.createQuestionHierachy(lIDs[0], lIDs[2], lGroupID);
		data.createQuestionHierachy(lIDs[0], lIDs[3], lGroupID);
		data.createQuestionHierachy(lIDs[1], lIDs[4], lGroupID);
		data.createQuestionHierachy(lIDs[1], lIDs[5], lGroupID);
		data.createQuestionHierachy(lIDs[2], lIDs[6], lGroupID);
		data.createQuestionHierachy(lIDs[2], lIDs[7], lGroupID);
		data.createQuestionHierachy(lIDs[3], lIDs[8], lGroupID);
		data.createQuestionHierachy(lIDs[3], lIDs[9], lGroupID);
		data.createQuestionHierachy(lIDs[3], lIDs[10], lGroupID);
		
		return lIDs;
	}
	
	private Vector<Object> getQuestionTexts(QueryResult inResult) throws SQLException, VException {
		Vector<Object> outTexts = new Vector<Object>();
		while (inResult.hasMoreElements()) {
			outTexts.add(inResult.next().get(QuestionHome.KEY_QUESTION));
		}
		return outTexts;
	}
}
