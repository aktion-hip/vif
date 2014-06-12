package org.hip.vif.web.stale;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 13.10.2010
 */
public class StaleRequestRemoverTest {
	private static final long ONE_DAY = 1000*60*60*24;
	
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(false);
	}

	@After
	public void tearDown() throws Exception {
		IndexHouseKeeper.deleteTestIndexDir();
		data.deleteAllInAll();
	}
	
	@Test
	public void testRemoveStaleRequests() throws Exception {
		//create members
		String[] lMemberIDs = data.create2Members();
		//create questions
		Long lQuestionID1 = data.createQuestion("Question 1", "1:2");
		Long lQuestionID2 = data.createQuestion("Question 2", "1:2.3");
		setQuestionState(lQuestionID2);
		//create completions
		Long lCompletionID1 = data.createCompletion("Completion 1", lQuestionID1);
		Long lCompletionID2 = data.createCompletion("Completion 2", lQuestionID1);
		setCompletionState(lCompletionID2);
		//create texts
		Long lTextID1 = data.createText("Text 1", "Foo, Jane");
		Long lTextID2 = data.createText("Text 2", "Doe, Jon");
		setTextState(lTextID2);
		
		long lNow = System.currentTimeMillis();

		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID1, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID2, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID1, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		//only this question is stale
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID2, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID1, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID2, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID1, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		//only this completion is stale
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID2, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createTextAuthorReviewerEntry(lTextID1, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createTextAuthorReviewerEntry(lTextID2, lMemberIDs[0], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-ONE_DAY));
		createTextAuthorReviewerEntry(lTextID1, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		//only this text entry is stale
		createTextAuthorReviewerEntry(lTextID2, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		//some assertions before
		assertEquals(4, data.getQuestionAuthorReviewerHome().getCount());
		assertEquals(4, data.getCompletionAuthorReviewerHome().getCount());
		assertEquals(4, data.getTextAuthorReviewerHome().getCount());
		
		KeyObject lKeyReviewer = new KeyObjectImpl();
		lKeyReviewer.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		assertEquals(4, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(4, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(4, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));
		
		//process
		StaleRequestRemoverSub lRemover = new StaleRequestRemoverSub(new Timestamp(lNow-(2*ONE_DAY)));
		lRemover.removeStaleRequests();
		
		//check assertions after
		assertEquals(4, data.getQuestionAuthorReviewerHome().getCount());
		assertEquals(4, data.getCompletionAuthorReviewerHome().getCount());
		assertEquals(4, data.getTextAuthorReviewerHome().getCount());
		assertEquals(3, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(3, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(3, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));
		
		KeyObject lKeyRefused = new KeyObjectImpl();
		lKeyRefused.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER_REFUSED.getValue());
		assertEquals(1, data.getQuestionAuthorReviewerHome().getCount(lKeyRefused));
		assertEquals(1, data.getCompletionAuthorReviewerHome().getCount(lKeyRefused));
		assertEquals(1, data.getTextAuthorReviewerHome().getCount(lKeyRefused));
	}
	
	@Test
	public void testCreateNewRequests1() throws Exception {
		//create 2 members
		String[] lMemberIDs = data.create2Members();
		//create questions
		Long lQuestionID = data.createQuestion("Question 2", "1:2.3");
		setQuestionState(lQuestionID);
		//create completions
		Long lCompletionID = data.createCompletion("Completion 2", lQuestionID);
		setCompletionState(lCompletionID);
		//create texts
		Long lTextID = data.createText("Text 2", "Doe, Jon");
		setTextState(lTextID);
		
		for (String lMemberID : lMemberIDs) {			
			createParticipant(lMemberID);
		}
		
		long lNow = System.currentTimeMillis();

		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		//some assertions before
		KeyObject lKeyReviewer = new KeyObjectImpl();
		lKeyReviewer.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		assertEquals(1, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(1, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(1, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));		
		
		StaleRequestRemoverSub lRemover = new StaleRequestRemoverSub(new Timestamp(lNow-(2*ONE_DAY)));
		//fill the remover's helper class
		lRemover.removeStaleRequests();
		
		//process
		lRemover.createNewRequests();

		//since we've created only two participants, the author and one first reviewer that refused, there's no one left to review now
		assertEquals(0, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(0, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(0, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));
	}

	@Test
	public void testCreateNewRequests2() throws Exception {
		//create 3 members
		String[] lMemberIDs = data.create3Members();
		//create questions
		Long lQuestionID = data.createQuestion("Question 2", "1:2.3");
		setQuestionState(lQuestionID);
		//create completions
		Long lCompletionID = data.createCompletion("Completion 2", lQuestionID);
		setCompletionState(lCompletionID);
		//create texts
		Long lTextID = data.createText("Text 2", "Doe, Jon");
		setTextState(lTextID);
		
		for (String lMemberID : lMemberIDs) {			
			createParticipant(lMemberID);
		}
		
		long lNow = System.currentTimeMillis();
		
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), (lNow-(3*ONE_DAY)));
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), (lNow-(3*ONE_DAY)));
		
		//some assertions before
		KeyObject lKeyReviewer = new KeyObjectImpl();
		lKeyReviewer.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		assertEquals(1, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(1, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
		assertEquals(1, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));		
		
		StaleRequestRemoverSub lRemover = new StaleRequestRemoverSub(new Timestamp(lNow-(2*ONE_DAY)));
		//fill the remover's helper class
		lRemover.removeStaleRequests();
		
		//process
		lRemover.createNewRequests();
		
		//there's a probabilistic possibility for a NoReviewerException
		if (data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer) == 1) {
			//if the application chose a reviewer, it must be member 3
			lKeyReviewer.setValue(ResponsibleHome.KEY_MEMBER_ID, new Long(lMemberIDs[2]));
			
			assertEquals(1, data.getQuestionAuthorReviewerHome().getCount(lKeyReviewer));
			assertEquals(1, data.getCompletionAuthorReviewerHome().getCount(lKeyReviewer));
			assertEquals(1, data.getTextAuthorReviewerHome().getCount(lKeyReviewer));		
		}
	}
	
	private void createParticipant(String inMemberID) throws VException, SQLException {
		DomainObject lParticipant = data.getParticipantHome().create();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(inMemberID));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, DataHouseKeeper.DFT_GROUP_ID);
		
		//on some MySQL systems, it seems that we need to have at least 1 second
		Timestamp lInit = new Timestamp(1000);
		lParticipant.set(ParticipantHome.KEY_SUSPEND_FROM, lInit);
		lParticipant.set(ParticipantHome.KEY_SUSPEND_TO, lInit);
		lParticipant.insert(true);		
	}

	private void setQuestionState(Long inQuestionID) throws VException, SQLException {
		Question lQuestion = data.getQuestionHome().getQuestion(inQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lQuestion.update(true);
	}
	
	private void setCompletionState(Long inCompletionID) throws VException, SQLException {
		Completion lCompletion = data.getCompletionHome().getCompletion(inCompletionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lCompletion.update(true);
	}
	
	private void setTextState(Long inTextID) throws VException, SQLException {
		Text lText = data.getTextHome().getText(inTextID, 0);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lText.update(true);
	}
	
	private void createAuthorReviewerEntry(DomainObject lEntry, String inFieldName, Long inContributionID, String inMemberID, Integer inType, long inTime) throws VException, SQLException {
		lEntry.set(inFieldName, inContributionID);
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, inType);
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}

	private void createTextAuthorReviewerEntry(Long inTextID, String inMemberID, Integer inType, long inTime) throws VException, SQLException {
		DomainObject lEntry = data.getTextAuthorReviewerHome().create();
		lEntry.set(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
		lEntry.set(TextAuthorReviewerHome.KEY_VERSION, new Long(0));
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, inType);
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}

	
// ---
	
	private class StaleRequestRemoverSub extends StaleRequestRemover {

		public StaleRequestRemoverSub(Timestamp inStaleDate) {
			super(inStaleDate);
		}
		@Override
		public void removeStaleRequests() throws Exception {
			super.removeStaleRequests();
		}
		@Override
		protected void sendRequestExpirationNotification(Long inReviewerID) throws Exception {
			// do nothing
		}
		
		@Override
		public void createNewRequests() throws Exception {
			super.createNewRequests();
		}
	}
}
