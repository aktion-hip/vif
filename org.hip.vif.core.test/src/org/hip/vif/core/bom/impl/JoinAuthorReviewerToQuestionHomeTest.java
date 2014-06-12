package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 09.07.2003
 * @author Luthiger
 */
public class JoinAuthorReviewerToQuestionHomeTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}

	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestionAuthorReviewer.MEMBERID, tblQuestionAuthorReviewer.NTYPE, tblQuestion.QUESTIONID, tblQuestion.GROUPID, tblQuestion.SQUESTION, tblQuestion.SQUESTIONID, tblQuestion.SREMARK, tblQuestion.BROOTQUESTION, tblQuestion.NSTATE FROM tblQuestion INNER JOIN tblQuestionAuthorReviewer ON tblQuestion.QUESTIONID = tblQuestionAuthorReviewer.QUESTIONID WHERE tblQuestionAuthorReviewer.MEMBERID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblQuestionAuthorReviewer.MemberID, tblQuestionAuthorReviewer.nType, tblQuestion.QuestionID, tblQuestion.GroupID, tblQuestion.sQuestion, tblQuestion.sQuestionID, tblQuestion.sRemark, tblQuestion.bRootQuestion, tblQuestion.nState FROM tblQuestion, tblQuestionAuthorReviewer WHERE tblQuestion.QuestionID = tblQuestionAuthorReviewer.QuestionID AND tblQuestionAuthorReviewer.MemberID = 32";
		}
		JoinAuthorReviewerToQuestionHome lSubHome = new JoinAuthorReviewerToQuestionHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testGetGroupAdminView() throws Exception {
		Long lMemberID = new Long(11);
		Long lMemberID2 = new Long(12);
		
		int lCountGroup = data.getGroupHome().getCount();
		int lCountQuestion = data.getQuestionHome().getCount();
		int lCountAuthors = data.getQuestionAuthorReviewerHome().getCount();
		
		Long lGroupID = new Long(data.createGroup());
		Long lQuestionID1 = data.createQuestion("This is the Root.", "1", lGroupID, WorkflowAwareContribution.S_OPEN, true); 							// owned - open: select
		Long lQuestionID2 = data.createQuestion("This is question 1.1 on level 1.", "1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false); 	// owned - private: select
		Long lQuestionID3 = data.createQuestion("This is question 1.2 on level 1.", "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false); 		// not owned - open: select
		Long lQuestionID4 = data.createQuestion("This is question 1.1.1 on level 2.", "1.1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false); // not owned - private: NOT SELECT
		Long lQuestionID5 = data.createQuestion("This is question 1.1.2 on level 2.", "1.1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false); 	// owned - open: select
		data.createQuestion("This is question 1.1.3 on level 2.", "1.1.3", lGroupID, WorkflowAwareContribution.S_OPEN, false); 	// not owned - open: select
		Long lQuestionID7 = data.createQuestion("This is question 1.2.1 on level 2.", "1.2.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false); // owned - private: select

		data.createQuestionProducer(new Long(lQuestionID1), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID2), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID5), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID7), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID3), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID4), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID5), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID2), lMemberID2, false);
		data.createQuestionProducer(new Long(lQuestionID7), lMemberID2, false);
		
		assertEquals("Number of created in tbl group", lCountGroup+1, data.getGroupHome().getCount());
		assertEquals("Number of created in tbl question", lCountQuestion+7, data.getQuestionHome().getCount());
		assertEquals("Number of created in tbl author", lCountAuthors+9, data.getQuestionAuthorReviewerHome().getCount());
		
		Long lNotSelectedID = new Long(lQuestionID4);
		int i = 0;
		JoinAuthorReviewerToQuestionHome lHome = data.getJoinAuthorReviewerToQuestionHome(); 
		QueryResult lResult = lHome.getGroupAdminView(lMemberID, lGroupID);
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lDomainObject = lResult.nextAsDomainObject();
			assertFalse("Id of selected", lNotSelectedID.equals(lDomainObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("Number of selected groupAdmin view 1", 6, i);
	}
	
	private int count(QueryResult inResult) throws BOMException, SQLException {
		int outCount = 0;
		while (inResult.hasMoreElements()) {
			inResult.nextAsDomainObject();
			outCount++;
		}
		return outCount;
	}
	
	@Test
	public void testGetAuthorsQuestions() throws Exception {
		Long lMemberID = new Long(11);
		Long lMemberID2 = new Long(12);
		
//		int lCountGroup = data.getGroupHome().getCount();
//		int lCountQuestion = data.getQuestionHome().getCount();
//		int lCountAuthors = data.getQuestionAuthorReviewerHome().getCount();
		
		Long lGroupID = new Long(data.createGroup());
		Long lQuestionID1 = data.createQuestion("This is the Root.", "1", lGroupID, WorkflowAwareContribution.S_OPEN, true); 							// owned - open
		Long lQuestionID2 = data.createQuestion("This is question 1.1 on level 1.", "1.1", lGroupID, WorkflowAwareContribution.S_WAITING_FOR_REVIEW, false); 	// owned - waiting
		Long lQuestionID3 = data.createQuestion("This is question 1.2 on level 1.", "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false); 		// not owned - open
		Long lQuestionID4 = data.createQuestion("This is question 1.1.1 on level 2.", "1.1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false); // not owned - private
		Long lQuestionID5 = data.createQuestion("This is question 1.1.2 on level 2.", "1.1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false); 	// owned - open
		data.createQuestion("This is question 1.1.3 on level 2.", "1.1.3", lGroupID, WorkflowAwareContribution.S_OPEN, false); 	// not owned - open
		Long lQuestionID7 = data.createQuestion("This is question 1.2.1 on level 2.", "1.2.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false); // owned - private

		data.createQuestionProducer(new Long(lQuestionID1), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID2), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID5), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID7), lMemberID, true);
		data.createQuestionProducer(new Long(lQuestionID3), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID4), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID5), lMemberID2, true);
		data.createQuestionProducer(new Long(lQuestionID2), lMemberID2, false);
		data.createQuestionProducer(new Long(lQuestionID7), lMemberID2, false);
		
		JoinAuthorReviewerToQuestionHome lHome = data.getJoinAuthorReviewerToQuestionHome(); 
		assertEquals("Number of selected authored 1", 4, count(lHome.getAuthorsQuestions(lMemberID, lGroupID)));
		assertEquals("Number of selected authored 2", 3, count(lHome.getAuthorsQuestions(lMemberID2, lGroupID)));
		assertEquals("Number of selected authored 3", 2, count(lHome.getAuthorsQuestions(lMemberID2, lGroupID, new Integer[] {WorkflowAwareContribution.S_OPEN})));
		assertEquals("Number of selected authored 4", 3, count(lHome.getAuthorsQuestions(lMemberID, lGroupID, new Integer[] {WorkflowAwareContribution.S_OPEN, WorkflowAwareContribution.S_PRIVATE})));
		assertEquals("Number of selected authored 5", 3, count(lHome.getAuthorsQuestions(lMemberID2, lGroupID, new Integer[] {WorkflowAwareContribution.S_OPEN, WorkflowAwareContribution.S_PRIVATE})));
		assertEquals("Number of selected authored 6", 2, count(lHome.getAuthorsUnpublishedQuestions(lMemberID, lGroupID)));

		assertEquals("Number of selected reviewed", 2, count(lHome.getReviewersQuestions(lMemberID2, lGroupID)));
	}

	@SuppressWarnings("serial")
	private class JoinAuthorReviewerToQuestionHomeSub extends JoinAuthorReviewerToQuestionHome {
		public JoinAuthorReviewerToQuestionHomeSub() {
			super(); 
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, new Integer(32));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}		
	}
}
