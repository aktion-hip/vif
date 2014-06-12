package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 29.05.2003
 * @author Luthiger
 */
public class JoinCompletionToMemberHomeTest {
	private static final long ONE_DAY = 1000*60*60*24;
	
	private static DataHouseKeeper data;
	private Long[] questionIDs = new Long[2];
	private Long[] completionIDs = new Long[8];
	private String[] memberIDs = null;
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);

		int lCountMember = data.getMemberHome().getCount();
		int lCountQuestions = data.getQuestionHome().getCount();
		int lCountCompletions = data.getCompletionHome().getCount();
		int lCountAuthors = data.getCompletionAuthorReviewerHome().getCount();
			
		memberIDs = data.create3Members();
		assertEquals("count members", lCountMember+3, data.getMemberHome().getCount());
			
		questionIDs[0] = data.createQuestion("This is question 1", "1");
		questionIDs[1] = data.createQuestion("This is question 2", "1.1");
		assertEquals("count questions", lCountQuestions+2, data.getQuestionHome().getCount());

		completionIDs[0] = data.createCompletion("Completion 1", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[1] = data.createCompletion("Completion 2", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[2] = data.createCompletion("Completion 3", questionIDs[0], WorkflowAwareContribution.S_PRIVATE);
		completionIDs[3] = data.createCompletion("Completion 4", questionIDs[0], WorkflowAwareContribution.S_WAITING_FOR_REVIEW);
		completionIDs[4] = data.createCompletion("Completion 5", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[5] = data.createCompletion("Completion 6", questionIDs[1], WorkflowAwareContribution.S_ANSWERED);
		completionIDs[6] = data.createCompletion("Completion 7", questionIDs[1], WorkflowAwareContribution.S_PRIVATE);
		completionIDs[7] = data.createCompletion("Completion 8", questionIDs[0], WorkflowAwareContribution.S_DELETED);
		assertEquals("count completions", lCountCompletions+completionIDs.length, data.getCompletionHome().getCount());

		//add authors
		data.createCompletionProducer(new Long(completionIDs[0]), new Long(memberIDs[0]), true);
		data.createCompletionProducer(new Long(completionIDs[1]), new Long(memberIDs[1]), true);
		data.createCompletionProducer(new Long(completionIDs[2]), new Long(memberIDs[0]), true);
		data.createCompletionProducer(new Long(completionIDs[3]), new Long(memberIDs[0]), true);
		data.createCompletionProducer(new Long(completionIDs[4]), new Long(memberIDs[1]), true);
		data.createCompletionProducer(new Long(completionIDs[5]), new Long(memberIDs[1]), true);
		data.createCompletionProducer(new Long(completionIDs[6]), new Long(memberIDs[0]), true);
		data.createCompletionProducer(new Long(completionIDs[7]), new Long(memberIDs[0]), true);
		//add reviewers
		data.createCompletionProducer(new Long(completionIDs[0]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[1]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[2]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[3]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[4]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[5]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[6]), new Long(memberIDs[2]), false);
		data.createCompletionProducer(new Long(completionIDs[7]), new Long(memberIDs[2]), false);
		assertEquals("count authored", lCountAuthors+completionIDs.length*2, data.getCompletionAuthorReviewerHome().getCount());
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	public void testCreateTestObjects() {
		String lExpected = "SELECT tblCompletion.COMPLETIONID, tblCompletion.SCOMPLETION, tblCompletion.NSTATE, tblCompletion.QUESTIONID, tblCompletion.DTMUTATION, tblCompletionAuthorReviewer.MEMBERID, tblCompletionAuthorReviewer.NTYPE, tblCompletionAuthorReviewer.DTCREATION, tblMember.MEMBERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL FROM tblCompletion INNER JOIN tblCompletionAuthorReviewer ON tblCompletion.COMPLETIONID = tblCompletionAuthorReviewer.COMPLETIONID INNER JOIN tblMember ON tblCompletionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblCompletion.QUESTIONID = 16";
		JoinCompletionToMemberHome lJoinHome = new JoinCompletionToMemberHomeSub();
		Iterator<Object> lTest = lJoinHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}

	@Test
	public void testSelect() throws Exception {
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4], completionIDs[7]};
		Long[] lExpected2 = {completionIDs[5], completionIDs[6]};
		
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		data.checkQueryResult(lExpected1, lHome.select(new Long(questionIDs[0])), CompletionHome.KEY_ID, "question 1");
		data.checkQueryResult(lExpected2, lHome.select(new Long(questionIDs[1])), CompletionHome.KEY_ID, "question 2");
	}
	
	@Test
	public void testSelectState() throws Exception {
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		
		//1: pub, priv
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[4]};
		data.checkQueryResult(lExpected1, lHome.select(new Long(questionIDs[0]), new Integer[] {WorkflowAwareContribution.S_OPEN, WorkflowAwareContribution.S_PRIVATE}), CompletionHome.KEY_ID, "question 1 (1)");
		//1: wai, priv
		Long[] lExpected2 = {completionIDs[2], completionIDs[3]};
		data.checkQueryResult(lExpected2, lHome.select(new Long(questionIDs[0]), new Integer[] {WorkflowAwareContribution.S_WAITING_FOR_REVIEW, WorkflowAwareContribution.S_PRIVATE}), CompletionHome.KEY_ID, "question 1 (2)");
		//1: pub
		Long[] lExpected3 = {completionIDs[0], completionIDs[1], completionIDs[4]};
		data.checkQueryResult(lExpected3, lHome.select(new Long(questionIDs[0]), new Integer[] {WorkflowAwareContribution.S_OPEN}), CompletionHome.KEY_ID, "question 1 (3)");
		//2: pub, priv
		Long[] lExpected4 = {completionIDs[6]};
		data.checkQueryResult(lExpected4, lHome.select(new Long(questionIDs[1]), new Integer[] {WorkflowAwareContribution.S_OPEN, WorkflowAwareContribution.S_PRIVATE}), CompletionHome.KEY_ID, "question 2 (4)");
		//2: priv
		Long[] lExpected5 = {completionIDs[6]};
		data.checkQueryResult(lExpected5, lHome.select(new Long(questionIDs[1]), new Integer[] {WorkflowAwareContribution.S_PRIVATE}), CompletionHome.KEY_ID, "question 2 (5)");
		//2: settl
		Long[] lExpected6 = {completionIDs[5]};
		data.checkQueryResult(lExpected6, lHome.select(new Long(questionIDs[1]), new Integer[] {WorkflowAwareContribution.S_ANSWERED}), CompletionHome.KEY_ID, "question 2 (6)");
	}
	
	@Test
	public void testSelectPublished() throws Exception {
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[4]};
		data.checkQueryResult(lExpected1, data.getJoinCompletionToMemberHome().selectPublished(new Long(questionIDs[0])), CompletionHome.KEY_ID, "question 1");

		Long[] lExpected2 = {completionIDs[5]};
		data.checkQueryResult(lExpected2, data.getJoinCompletionToMemberHome().selectPublished(new Long(questionIDs[1])), CompletionHome.KEY_ID, "question 2");

		//all responsibles
		Long[] lExpected3 = {completionIDs[0], completionIDs[1], completionIDs[4], completionIDs[0], completionIDs[1], completionIDs[4]};
		data.checkQueryResult(lExpected3, data.getJoinCompletionToMemberHome().selectPublishedWithResponsibles(new Long(questionIDs[0])), CompletionHome.KEY_ID, "question 1");
		
		Long[] lExpected4 = {completionIDs[5], completionIDs[5]};
		data.checkQueryResult(lExpected4, data.getJoinCompletionToMemberHome().selectPublishedWithResponsibles(new Long(questionIDs[1])), CompletionHome.KEY_ID, "question 2");
	}
	
	@Test
	public void testGroupAdminView() throws Exception {
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		
		//q1, m1
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4], completionIDs[7]};
		data.checkQueryResult(lExpected1, lHome.getGroupAdminView(new Long(questionIDs[0]), new Long(memberIDs[0])), CompletionHome.KEY_ID, "question 1, member 1");
		//q1, m2
		Long[] lExpected2 = {completionIDs[0], completionIDs[1], completionIDs[4]};
		data.checkQueryResult(lExpected2, lHome.getGroupAdminView(new Long(questionIDs[0]), new Long(memberIDs[1])), CompletionHome.KEY_ID, "question 1, member 2");
		//q2, m1
		Long[] lExpected3 = {completionIDs[5], completionIDs[6]};
		data.checkQueryResult(lExpected3, lHome.getGroupAdminView(new Long(questionIDs[1]), new Long(memberIDs[0])), CompletionHome.KEY_ID, "question 2, member 1");
		//q2, m2
		Long[] lExpected4 = {completionIDs[5]};
		data.checkQueryResult(lExpected4, lHome.getGroupAdminView(new Long(questionIDs[1]), new Long(memberIDs[1])), CompletionHome.KEY_ID, "question 2, member 2");
	}
	
	@Test
	public void testGetAuthorView() throws Exception {
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();

		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4]};
		Long[] lExpected2 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4], completionIDs[7]};

		//deleted completion is filtered out
		data.checkQueryResult(lExpected1, lHome.getAuthorView(new Long(questionIDs[0]), new Long(memberIDs[0])), CompletionHome.KEY_ID, "question 1, member 1");
		//deleted completion is included
		data.checkQueryResult(lExpected2, lHome.selectAuthorView(new Long(questionIDs[0]), new Long(memberIDs[0]), WorkflowAwareContribution.STATES_PUBLISHED), CompletionHome.KEY_ID, "question 1, member 1");
	}

	@Test
	public void testGetFiltered() throws Exception {
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		Long[] lMemberIDs = addAuthorReviewer();

		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4]};
		data.checkQueryResult(lExpected1, lHome.getFiltered(questionIDs[0], lMemberIDs[0]), CompletionHome.KEY_ID, "admin");

		Long[] lExpected2 = {completionIDs[0], completionIDs[1], completionIDs[3], completionIDs[4]};
		data.checkQueryResult(lExpected2, lHome.getFiltered(questionIDs[0], lMemberIDs[1]), CompletionHome.KEY_ID, "group admin");

		Long[] lExpected3 = {completionIDs[0], completionIDs[1], completionIDs[4]};
		data.checkQueryResult(lExpected3, lHome.getFiltered(questionIDs[0], lMemberIDs[2]), CompletionHome.KEY_ID, "member admin");
	}
	
	@Test
	public void testSelectAuthorViewOfSiblings() throws Exception {
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		Long[] lMemberIDs = addAuthorReviewer();

		Long[] lExpected1 = {completionIDs[1], completionIDs[2], completionIDs[4]};
		data.checkQueryResult(lExpected1, lHome.selectAuthorViewOfSiblings(questionIDs[0], lMemberIDs[0], completionIDs[0]), CompletionHome.KEY_ID, "question 1, member 1");

		Long[] lExpected2 = {completionIDs[1], completionIDs[3], completionIDs[4]};
		data.checkQueryResult(lExpected2, lHome.selectAuthorViewOfSiblings(questionIDs[0], lMemberIDs[1], completionIDs[0]), CompletionHome.KEY_ID, "question 1, member 2");

		Long[] lExpected3 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[4]};
		data.checkQueryResult(lExpected3, lHome.selectAuthorViewOfSiblings(questionIDs[0], lMemberIDs[0], completionIDs[3]), CompletionHome.KEY_ID, "question 1, member 2, completion 2");
	}
	
	private Long[] addAuthorReviewer() throws Exception {
		Long[] outMembers = new Long[3];
		data.deleteAllFromMember();
		data.deleteAllFromCompletionAuthorReviewer();
		
		int lCountMembers = data.getMemberHome().getCount();
		outMembers[0] = new Long(data.createMemberRoles("TestAMIN", "TestMember1", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_ADMIN}).intValue());
		outMembers[1] = new Long(data.createMemberRoles("TestGROUPADMIN", "TestMember2", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_GROUP_ADMIN}).intValue());
		outMembers[2] = new Long(data.createMemberRoles("TestMEMBER", "TestMember3", new String[] {ApplicationConstants.ROLE_ID_MEMBER}).intValue());
		assertEquals("count members", lCountMembers + 3, data.getMemberHome().getCount());

		//add authors/reviewers
		int lCountAuthors = data.getCompletionAuthorReviewerHome().getCount();
		data.createCompletionProducer(new Long(completionIDs[0]), outMembers[0], true);
		data.createCompletionProducer(new Long(completionIDs[1]), outMembers[1], true);
		data.createCompletionProducer(new Long(completionIDs[2]), outMembers[0], true);
		data.createCompletionProducer(new Long(completionIDs[3]), outMembers[1], true);
		data.createCompletionProducer(new Long(completionIDs[4]), outMembers[0], true);
		data.createCompletionProducer(new Long(completionIDs[5]), outMembers[0], true);
		data.createCompletionProducer(new Long(completionIDs[6]), outMembers[0], true);
		assertEquals("count authored", lCountAuthors+7, data.getCompletionAuthorReviewerHome().getCount());

		return outMembers;
	}
	
	@Test
	public void testSelectStaleWaitingForReview() throws Exception {
		data.deleteAllFromMember();
		String[] lMemberIDs = data.create2Members();
		Long lQuestionID = data.createQuestion("Test question", "1:2");
		Long lCompletionID = data.createCompletion("Test completion", lQuestionID);
		setCompletionState(lCompletionID);
		
		long lNow = System.currentTimeMillis();
		
		createCompletionAuthorReviewerEntry(lCompletionID, lMemberIDs[0], (lNow-ONE_DAY));
		createCompletionAuthorReviewerEntry(lCompletionID, lMemberIDs[1], (lNow-(3*ONE_DAY)));
		
		JoinCompletionToMemberHome lHome = data.getJoinCompletionToMemberHome();
		QueryResult lResult = lHome.selectStaleWaitingForReview(new Timestamp(lNow-(2*ONE_DAY)));
		while (lResult.hasMoreElements()) {
			assertEquals(lMemberIDs[1], lResult.next().get(ResponsibleHome.KEY_MEMBER_ID).toString());
		}
	}

	private void createCompletionAuthorReviewerEntry(Long inCompletionID, String inMemberID, long inTime) throws VException, SQLException {
		DomainObject lEntry = data.getCompletionAuthorReviewerHome().create();
		lEntry.set(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, inCompletionID);
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Integer(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}

	private void setCompletionState(Long inCompletionID) throws VException, SQLException {
		Completion lCompletion = data.getCompletionHome().getCompletion(inCompletionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lCompletion.update(true);
	}

// ---
	
	@SuppressWarnings("serial")
	private class JoinCompletionToMemberHomeSub extends JoinCompletionToMemberHome {
		public JoinCompletionToMemberHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(CompletionHome.KEY_QUESTION_ID, new Integer(16));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}		
	}

}
