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
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 20.09.2009
 */
public class JoinQuestionToContributorsHomeTest {
	private static final long ONE_DAY = 1000*60*60*24;
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
		data.deleteAllFromQuestion();
		data.deleteAllFromMember();
		data.deleteAllFromQuestionAuthorReviewer();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() throws Exception {
		JoinQuestionToContributorsHome lHome = new JoinQuestionToContributorsHomeSub();		
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.BROOTQUESTION, tblQuestion.GROUPID, tblQuestion.NSTATE, tblQuestion.DTMUTATION, tblQuestionAuthorReviewer.MEMBERID, tblQuestionAuthorReviewer.NTYPE, tblQuestionAuthorReviewer.DTCREATION, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL FROM tblQuestion INNER JOIN tblQuestionAuthorReviewer ON tblQuestion.QUESTIONID = tblQuestionAuthorReviewer.QUESTIONID INNER JOIN tblMember ON tblQuestionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblQuestion.GROUPID = 8";
		assertEquals("test objects", lExpected, (String)lTest.next());
//		System.out.println((String)lTest.next());
	}
	
	@Test
	public void testSelectStaleWaitingForReview() throws Exception {
		String[] lMemberIDs = data.create2Members();
		Long lQuestionID = data.createQuestion("Test", "1:2");
		setQuestionState(lQuestionID);
		
		long lNow = System.currentTimeMillis();
		
		createQuestionAuthorReviewerEntry(lQuestionID, lMemberIDs[0], (lNow-ONE_DAY));
		createQuestionAuthorReviewerEntry(lQuestionID, lMemberIDs[1], (lNow-(3*ONE_DAY)));
		
		JoinQuestionToContributorsHome lHome = data.getJoinQuestionToContributorsHome();
		QueryResult lResult = lHome.selectStaleWaitingForReview(new Timestamp(lNow-(2*ONE_DAY)));
		while (lResult.hasMoreElements()) {
			assertEquals(lMemberIDs[1], lResult.next().get(ResponsibleHome.KEY_MEMBER_ID).toString());
		}
	}
	
	private void setQuestionState(Long inQuestionID) throws VException, SQLException {
		Question lQuestion = data.getQuestionHome().getQuestion(inQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lQuestion.update(true);
	}
	
	private void createQuestionAuthorReviewerEntry(Long inQuestionID, String inMemberID, long inTime) throws VException, SQLException {
		DomainObject lEntry = data.getQuestionAuthorReviewerHome().create();
		lEntry.set(QuestionAuthorReviewerHome.KEY_QUESTION_ID, inQuestionID);
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}
	
//	---
	
	@SuppressWarnings("serial")
	private class JoinQuestionToContributorsHomeSub extends JoinQuestionToContributorsHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(8));
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
	
}
