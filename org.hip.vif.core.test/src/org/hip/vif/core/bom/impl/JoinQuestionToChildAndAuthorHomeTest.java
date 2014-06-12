package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 12.08.2003
 * @author Luthiger
 */
public class JoinQuestionToChildAndAuthorHomeTest {
	
	@SuppressWarnings("serial")
	private class JoinQuestionToChildAndAuthorHomeSub extends JoinQuestionToChildAndAuthorHome {
		public JoinQuestionToChildAndAuthorHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, new Integer(32));
				lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, new Integer(12));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}
	}

	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestionHierarchy.PARENTID, tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.BROOTQUESTION, tblQuestion.GROUPID, tblQuestion.NSTATE, tblQuestion.DTMUTATION, tblQuestionAuthorReviewer.MEMBERID, tblQuestionAuthorReviewer.NTYPE FROM tblQuestionHierarchy INNER JOIN tblQuestion ON tblQuestionHierarchy.CHILDID = tblQuestion.QUESTIONID INNER JOIN tblQuestionAuthorReviewer ON tblQuestion.QUESTIONID = tblQuestionAuthorReviewer.QUESTIONID WHERE tblQuestionHierarchy.PARENTID = 32 AND tblQuestionAuthorReviewer.MEMBERID = 12";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinQuestionToChildAndAuthorHome lSubHome = new JoinQuestionToChildAndAuthorHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testGetAuthorView() throws Exception {
		String[] lQuestions = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11"};
		Long lMember1 = new Long(20);
		Long lMember2 = new Long(30);
		Long lMember3 = new Long(40);
		Long[] lQuestionIDs = createQuestionsWithHierachy(lQuestions);
		
		int lCount = data.getQuestionAuthorReviewerHome().getCount();
		data.createQuestionProducer(new Long(lQuestionIDs[0]), lMember1, true);
		data.createQuestionProducer(new Long(lQuestionIDs[1]), lMember2, true);
		data.createQuestionProducer(new Long(lQuestionIDs[2]), lMember3, true);
		data.createQuestionProducer(new Long(lQuestionIDs[3]), lMember1, true);
		data.createQuestionProducer(new Long(lQuestionIDs[4]), lMember2, true);
		data.createQuestionProducer(new Long(lQuestionIDs[5]), lMember3, true);
		data.createQuestionProducer(new Long(lQuestionIDs[6]), lMember1, true);
		data.createQuestionProducer(new Long(lQuestionIDs[7]), lMember2, true);
		data.createQuestionProducer(new Long(lQuestionIDs[8]), lMember3, true);
		data.createQuestionProducer(new Long(lQuestionIDs[9]), lMember1, true);
		data.createQuestionProducer(new Long(lQuestionIDs[10]), lMember2, true);
		assertEquals("count authors", lCount+11, data.getQuestionAuthorReviewerHome().getCount());
		
		Long[] lExpected = new Long[] {lQuestionIDs[1], lQuestionIDs[2], lQuestionIDs[3]};
		data.checkQueryResult(lExpected, data.getJoinQuestionToChildAndAuthorHome().getAuthorView(lQuestionIDs[0], lMember1, WorkflowAwareContribution.STATES_PUBLISHED), QuestionHome.KEY_ID, "childs 1, member 1");

		lExpected = new Long[] {lQuestionIDs[1], lQuestionIDs[2]};
		data.checkQueryResult(lExpected, data.getJoinQuestionToChildAndAuthorHome().getAuthorView(lQuestionIDs[0], lMember2, WorkflowAwareContribution.STATES_PUBLISHED), QuestionHome.KEY_ID, "childs 1, member 2");

		lExpected = new Long[] {lQuestionIDs[4], lQuestionIDs[5]};
		data.checkQueryResult(lExpected, data.getJoinQuestionToChildAndAuthorHome().getAuthorView(lQuestionIDs[1], lMember1, WorkflowAwareContribution.STATES_PUBLISHED), QuestionHome.KEY_ID, "childs 2, member 1");

		lExpected = new Long[] {lQuestionIDs[6], lQuestionIDs[7]};
		data.checkQueryResult(lExpected, data.getJoinQuestionToChildAndAuthorHome().getAuthorView(lQuestionIDs[2], lMember1, WorkflowAwareContribution.STATES_PUBLISHED), QuestionHome.KEY_ID, "childs 3, member 1");

		lExpected = new Long[] {lQuestionIDs[9]};
		data.checkQueryResult(lExpected, data.getJoinQuestionToChildAndAuthorHome().getAuthorView(lQuestionIDs[3], lMember1, WorkflowAwareContribution.STATES_PUBLISHED), QuestionHome.KEY_ID, "childs 4, member 1");
	}

	private Long[] createQuestionsWithHierachy(String[] inQuestions) throws VException, SQLException {
		Long lGroupID = 15l;
		Long[] lIDs = new Long[11];
		
		int lCount = data.getQuestionHome().getCount();
		lIDs[0] = new Long(data.createQuestion(inQuestions[0], "1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, true));
		lIDs[1] = new Long(data.createQuestion(inQuestions[1], "1.1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false));
		lIDs[2] = new Long(data.createQuestion(inQuestions[2], "1.2", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false));
		lIDs[3] = new Long(data.createQuestion(inQuestions[3], "1.3", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false));
		lIDs[4] = new Long(data.createQuestion(inQuestions[4], "1.1.1", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false));
		lIDs[5] = new Long(data.createQuestion(inQuestions[5], "1.1.2", new Long(lGroupID), WorkflowAwareContribution.S_OPEN, false));
		lIDs[6] = new Long(data.createQuestion(inQuestions[6], "1.2.1", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false));
		lIDs[7] = new Long(data.createQuestion(inQuestions[7], "1.2.3", new Long(lGroupID), WorkflowAwareContribution.S_ANSWERED, false));
		lIDs[8] = new Long(data.createQuestion(inQuestions[8], "1.3.1", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false));
		lIDs[9] = new Long(data.createQuestion(inQuestions[9], "1.3.2", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false));
		lIDs[10] = new Long(data.createQuestion(inQuestions[10], "1.3.3", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false));
		assertEquals("count questions", lCount+11, data.getQuestionHome().getCount());
	
		lCount = data.getQuestionHierarchyHome().getCount();
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
		assertEquals("count question hierarchy", lCount+10, data.getQuestionHierarchyHome().getCount());
	
		return lIDs;
	}
}
