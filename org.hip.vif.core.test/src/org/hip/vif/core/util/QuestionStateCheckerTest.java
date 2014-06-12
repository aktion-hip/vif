package org.hip.vif.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Apr 15, 2005
 */
public class QuestionStateCheckerTest {
	private static DataHouseKeeper data;
	private Long groupID = new Long(82);
	private Long[] questionIDs = new Long[4];

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		questionIDs[0] = data.createQuestion("Parent", "chk:1", groupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[1] = data.createQuestion("Child1", "chk:1.1", groupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[2] = data.createQuestion("Child2", "chk:1.2", groupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[3] = data.createQuestion("Child3", "chk:1.3", groupID, WorkflowAwareContribution.S_OPEN, false);
		
		data.createQuestionHierachy(questionIDs[0], questionIDs[1], groupID);
		data.createQuestionHierachy(questionIDs[0], questionIDs[2], groupID);
		data.createQuestionHierachy(questionIDs[0], questionIDs[3], groupID);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestionHierarchy();
		data.deleteAllFromQuestion();
		data.deleteAllFromQuestionHistory();
	}

	@Test
	public void testCheckAnswerdOfChilds() throws Exception {
		assertTrue("no childs", QuestionStateChecker.checkStateOfChilds(new Long(questionIDs[2]), WorkflowAwareContribution.STATES_ANSWERED));
		assertFalse("unanswered childs", QuestionStateChecker.checkStateOfChilds(new Long(questionIDs[0]), WorkflowAwareContribution.STATES_ANSWERED));

		changeStates();
		assertTrue("answered childs", QuestionStateChecker.checkStateOfChilds(new Long(questionIDs[0]), WorkflowAwareContribution.STATES_ANSWERED));
	}
	
	private void changeStates() throws Exception {
		QuestionHome lHome = data.getQuestionHome();
		Object[] lParameters = {new Long(1)};
		((WorkflowAwareContribution)lHome.getQuestion(questionIDs[1])).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lParameters);
		((WorkflowAwareContribution)lHome.getQuestion(questionIDs[2])).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lParameters);
		((WorkflowAwareContribution)lHome.getQuestion(questionIDs[3])).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lParameters);
	}
}
