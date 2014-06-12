package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Jul 11, 2005
 */
public class QuestionStateChangeHelperTest {
	private static DataHouseKeeper data;
	private static GroupHome groupHome;
	private static QuestionHome questionHome;

	private Long[] questionIDs = new Long[4];
	private Long groupID;

	private Long actorID = new Long(81);
	private Object[] args = {actorID};
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
		groupHome = data.getGroupHome();
		questionHome = data.getQuestionHome();
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.GERMAN);
	}

	@Before
	public void setUp() throws Exception {
		groupID = data.createGroup();
		Group lGroup = groupHome.getGroup(groupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_ACTIVE));
		lGroup.update(true);
		
		questionIDs[0] = data.createQuestion("Parent", "chk:1", groupID, WorkflowAwareContribution.S_OPEN, true);
		questionIDs[1] = data.createQuestion("Child1", "chk:1.1", groupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[2] = data.createQuestion("Child11", "chk:1.1.1", groupID, WorkflowAwareContribution.S_OPEN, false);
		questionIDs[3] = data.createQuestion("Child12", "chk:1.1.2", groupID, WorkflowAwareContribution.S_OPEN, false);
		
		data.createQuestionHierachy(questionIDs[0], questionIDs[1], groupID);
		data.createQuestionHierachy(questionIDs[1], questionIDs[2], groupID);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}

	@Test
	public void testSetAnsweredFromOpen1() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setAnsweredFromOpen(lQuestion, args);
		
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is settled", VIFGroupWorkflow.STATE_SETTLED, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
	}

	@Test
	public void testSetAnsweredFromOpen2() throws Exception {
		data.createQuestionHierachy(questionIDs[1], questionIDs[3], new Long(groupID));
		
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setAnsweredFromOpen(lQuestion, args);
		
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is not answered", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is not settled", VIFGroupWorkflow.STATE_ACTIVE, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
	}
	
	@Test
	public void testSetAnsweredFromOpen3() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lQuestion.doTransition(WorkflowAwareContribution.TRANS_REQUEST_ANSWERED, args);
		assertNotNull(lQuestion);
		
		try {
			lHelper.setAnsweredFromOpen(lQuestion, args);
			fail("Shouldn't get here.");
		}
		catch (Exception exc) {
			//left blank intentionally
		}
	}

	@Test
	public void testSetAnsweredFromRequested() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lQuestion.doTransition(WorkflowAwareContribution.TRANS_REQUEST_ANSWERED, args);
		data.deleteAllFromQuestionHistory();
		
		lHelper.setAnsweredFromRequested(lQuestion, args);
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is settled", VIFGroupWorkflow.STATE_SETTLED, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
	}

	@Test
	public void testRejectAnswered() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lQuestion.doTransition(WorkflowAwareContribution.TRANS_REQUEST_ANSWERED, args);
		data.deleteAllFromQuestionHistory();
		
		lHelper.rejectAnswered(lQuestion, args);
		assertEquals("question is open", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
	}

	@Test
	public void testSetOpenFromAnswered() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setAnsweredFromOpen(lQuestion, args);
		
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is settled", VIFGroupWorkflow.STATE_SETTLED, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
		
		data.deleteAllFromQuestionHistory();
		lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setOpenFromAnswered(lQuestion, args);
		assertEquals("question is open", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is open", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is active", VIFGroupWorkflow.STATE_ACTIVE, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
	}

	@Test
	public void testSetOpenFromRequested() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setAnsweredFromOpen(lQuestion, args);
		
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is settled", VIFGroupWorkflow.STATE_SETTLED, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
		
		data.deleteAllFromQuestionHistory();
		lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lQuestion.doTransition(WorkflowAwareContribution.TRANS_REQUEST_REOPEN, args);
		data.deleteAllFromQuestionHistory();
		
		lHelper.setOpenFromRequested(lQuestion, args);
		assertEquals("question is open", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is open", WorkflowAwareContribution.STATE_OPEN, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is active", VIFGroupWorkflow.STATE_ACTIVE, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
	}

	@Test
	public void testRejectReopen() throws Exception {
		QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(data.getQuestionHome(), data.getGroupHome());
		WorkflowAwareContribution lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lHelper.setAnsweredFromOpen(lQuestion, args);
		
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("parent is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[1]).get(QuestionHome.KEY_STATE).toString());
		assertEquals("group is settled", VIFGroupWorkflow.STATE_SETTLED, data.getGroupHome().getGroup(groupID).get(GroupHome.KEY_STATE).toString());
		
		data.deleteAllFromQuestionHistory();
		lQuestion = (WorkflowAwareContribution)questionHome.getQuestion(questionIDs[2]);
		lQuestion.doTransition(WorkflowAwareContribution.TRANS_REQUEST_REOPEN, args);
		data.deleteAllFromQuestionHistory();

		lHelper.rejectReopen(lQuestion, args);
		assertEquals("question is answered", WorkflowAwareContribution.STATE_ANSWERED, questionHome.getQuestion(questionIDs[2]).get(QuestionHome.KEY_STATE).toString());			
	}
	
}
