package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHistoryHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 29.05.2003
 * @author Luthiger
 */
public class CompletionImplTest {
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
	public void testUcNew() throws Exception {
		CompletionHome lCompletionHome = data.getCompletionHome();
		int lInitialCompletions = lCompletionHome.getCount();
		int lInitialAuthors = BOMHelper.getCompletionAuthorReviewerHome().getCount();
		
		Completion lCompletion = (Completion)lCompletionHome.create();
		lCompletion.ucNew("New contribution.", "66", new Long(77));
		
		assertEquals("count contribution", lInitialCompletions + 1, lCompletionHome.getCount());
		assertEquals("count authors", lInitialAuthors + 1, BOMHelper.getCompletionAuthorReviewerHome().getCount());			
	}

	@Test
	public void testUcSave() throws Exception {
		String lCompletion1 = "Contribution1";
		String lCompletion2 = "Contribution2";
		Long lAuthor1 = new Long(77);
		Long lAuthor2 = new Long(7);

		CompletionHome lCompletionHome = (CompletionHome)BOMHelper.getCompletionHome();
		CompletionAuthorReviewerHome lCARHome = (CompletionAuthorReviewerHome)BOMHelper.getCompletionAuthorReviewerHome();
		CompletionHistoryHome lHistoryHome = (CompletionHistoryHome)BOMHelper.getCompletionHistoryHome();
		int lInitialHistories = lHistoryHome.getCount();
		
		Completion lCompletion = (Completion)lCompletionHome.create();
		Long lCompletionID = lCompletion.ucNew(lCompletion1, "21", lAuthor1);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
		lCompletion = (Completion)lCompletionHome.findByKey(lKey);
		
		assertEquals("contribution 1", lCompletion1, (String)lCompletion.get(CompletionHome.KEY_COMPLETION));
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor1);
		lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletion.get(CompletionHome.KEY_ID));			
		
		DomainObject lCAR = lCARHome.findByKey(lKey);
		assertNotNull("contribution-author-reviewer 1", lCAR);

		lCompletion.ucSave(lCompletion2, "2", lAuthor2);
		assertEquals("count history 1", lInitialHistories + 1, lHistoryHome.getCount());
		try {
			lCAR = lCARHome.findByKey(lKey);
			fail("shouldn't get here 1");
		}
		catch (BOMNotFoundException exc) {
			//left empty intentionally
		}
		
		lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
		lCompletion = (Completion)lCompletionHome.findByKey(lKey);
		assertEquals("contribution 2", lCompletion2, (String)lCompletion.get(CompletionHome.KEY_COMPLETION));
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor2);
		lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletion.get(CompletionHome.KEY_ID));			
		
		lCAR = lCARHome.findByKey(lKey);
		assertNotNull("contribution-author-reviewer 2", lCAR);
		
		//save again with identical values, therefore, no update is made
		lCompletion.ucSave(lCompletion2, "2", lAuthor2);

		assertEquals("count history 2", lInitialHistories + 1, lHistoryHome.getCount());
		lCAR = lCARHome.findByKey(lKey);
		assertNotNull("contribution-author-reviewer 3", lCAR);
	}
	
	@Test
	public void testGetOwningQuestion() throws Exception {
		String lExpected1 = "1";
		String lExpected2 = "1.1";

		int lCountQuestions = data.getQuestionHome().getCount();
		Long lQuestionID1 = data.createQuestion("question 1", lExpected1);
		Long lQuestionID2 = data.createQuestion("question 2", lExpected2);
		assertEquals("count questions", lCountQuestions+2, data.getQuestionHome().getCount());
		
		CompletionHome lHome = data.getCompletionHome();
		int lCountCompletions = lHome.getCount();
		Long lCompletionID1 = data.createCompletion("completion 1", lQuestionID1);
		Long lCompletionID2 = data.createCompletion("completion 2", lQuestionID1);
		Long lCompletionID3 = data.createCompletion("completion 3", lQuestionID1);
		Long lCompletionID4 = data.createCompletion("completion 4", lQuestionID2);
		assertEquals("count completions", lCountCompletions+4, lHome.getCount());
		
		checkOwningQuestion(lCompletionID1, lExpected1, "question 1 owns completion 1");
		checkOwningQuestion(lCompletionID2, lExpected1, "question 1 owns completion 2");
		checkOwningQuestion(lCompletionID3, lExpected1, "question 1 owns completion 3");
		checkOwningQuestion(lCompletionID4, lExpected2, "question 2 owns completion 4");
	}
	
	private void checkOwningQuestion(Long inCompletionID, String inExpected, String inAssert) {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
			DomainObject lQuestion = ((Completion)data.getCompletionHome().findByKey(lKey)).getOwningQuestion();
			assertEquals(inAssert, inExpected, lQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL));
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testIndexing() throws Exception {		
		Object[] lActorID = new Object[] {new Long(96)};
		
		//preparation: create group and open it
		Long lGroupID = data.createGroup();
 	 	Group lGroup = data.getGroupHome().getGroup(lGroupID);
 	 	((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {new GroupStateChangeParameters()});
		
 	 	//preparation: create two members who will be the authors of the question
 	 	String[] lAuthorIDs = data.create2Members();
 	 	
 	 	//preparation: create new question, link it to the admin and open it
 	 	Long lQuestionID = data.createQuestion("Question1", "2:6.4", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[0]), true);
		Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
		assertEquals("number of indexed 1", 1, IndexHouseKeeper.countIndexedContents());
		
		//now, we create the completion
		String lCompletionText = "New completion 1.";
		CompletionHome lHome = data.getCompletionHome();
		Completion lCompletion = (Completion)lHome.create();
		Long lCompletionID = lCompletion.ucNew(lCompletionText, lQuestionID.toString(), new Long(lAuthorIDs[1]));
		
		lCompletion = getCompletion(lCompletionID, lQuestionID.toString(), lHome);		
		((WorkflowAware)lCompletion).doTransition(WorkflowAwareContribution.TRANS_REQUEST, lActorID);
		data.deleteAllFromCompletionHistory();
		((WorkflowAware)lCompletion).doTransition(WorkflowAwareContribution.TRANS_ACCEPT, lActorID);
		data.deleteAllFromCompletionHistory();
		assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());
		
		//the new completion is indexed after it is published
		((WorkflowAware)lCompletion).doTransition(WorkflowAwareContribution.TRANS_PUBLISH, lActorID);
		data.deleteAllFromCompletionHistory();
		assertEquals("number of indexed 3", 2, IndexHouseKeeper.countIndexedContents());
		
		//deleting the question deletes the index entry of the completion too
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE1, lActorID);
		assertEquals("number of indexed 4", 0, IndexHouseKeeper.countIndexedContents());
		
		//now we create a question and add a completion before we publish them
 	 	lQuestionID = data.createQuestion("Question2", "2:6.5", new Long(lGroupID), WorkflowAwareContribution.S_PRIVATE, false);
		data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[1]), true);
		lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		lCompletionText = "New completion 2.";
		lCompletion = (Completion)lHome.create();
		lCompletionID = lCompletion.ucNew(lCompletionText, lQuestionID.toString(), new Long(lAuthorIDs[1]));
		assertEquals("number of indexed 5", 0, IndexHouseKeeper.countIndexedContents());
		
		//publication of both question and completion as group admin
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
		lCompletion = getCompletion(lCompletionID, lQuestionID.toString(), lHome);		
		((WorkflowAware)lCompletion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
		assertEquals("number of indexed 6", 2, IndexHouseKeeper.countIndexedContents());

		//delete both question and completion
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE2, lActorID);
		assertEquals("number of indexed 7", 0, IndexHouseKeeper.countIndexedContents());
	}
	
	private Completion getCompletion(Long inCompletionID, String inQuestionID, CompletionHome inHome) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, new Long(inQuestionID));
		return (Completion)inHome.findByKey(lKey);		
	}
}
