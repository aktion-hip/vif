package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
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
 * @author: Benno Luthiger
 */
public class QuestionImplTest {
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
		String lQuestion1 = "Question1";
		String lRemark1 = "Remark1";
		String lQuestion2 = "Question2";
		String lRemark2 = "Remark2";

		QuestionHome lQuestionHome = (QuestionHome)BOMHelper.getQuestionHome();
		int lInitialQuestions = lQuestionHome.getCount();
		int lInitialAuthors = BOMHelper.getQuestionAuthorReviewerHome().getCount();
		int lInitialHierarchy = BOMHelper.getQuestionHierarchyHome().getCount();
		Long lGroupID = ((GroupHome)BOMHelper.getGroupHome()).createNew("testGroup1", "Group for testing", "1", "2", "3", false);
		
		Question lQuestion = (Question)BOMHelper.getQuestionHome().create();
		Long lQuestionID = lQuestion.ucNew(lQuestion1, lRemark1, 0l, lGroupID.toString(), new Long(77));
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		lQuestion = (Question)lQuestionHome.findByKey(lKey);
		Long lQuestionID1 = new Long(lQuestion.get(QuestionHome.KEY_ID).toString());
		
		assertEquals("count questions 1", lInitialQuestions + 1, lQuestionHome.getCount());
		assertEquals("count authors 1", lInitialAuthors + 1, BOMHelper.getQuestionAuthorReviewerHome().getCount());
		assertEquals("count hierarchy 1", lInitialHierarchy, BOMHelper.getQuestionHierarchyHome().getCount());
		assertEquals("root question", QuestionHome.IS_ROOT.toString(), lQuestion.get(QuestionHome.KEY_ROOT_QUESTION).toString());
		
		lQuestion = (Question)BOMHelper.getQuestionHome().create();
		lQuestionID = lQuestion.ucNew(lQuestion2, lRemark2, lQuestionID1, lGroupID.toString(), new Long(77));
		
		lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		lQuestion = (Question)lQuestionHome.findByKey(lKey);
		
		assertEquals("count questions 2", lInitialQuestions + 2, lQuestionHome.getCount());
		assertEquals("count authors 2", lInitialAuthors + 2, BOMHelper.getQuestionAuthorReviewerHome().getCount());
		assertEquals("count hierarchy 2", lInitialHierarchy + 1, BOMHelper.getQuestionHierarchyHome().getCount());
		assertEquals("not root question", QuestionHome.NOT_ROOT.toString(), lQuestion.get(QuestionHome.KEY_ROOT_QUESTION).toString());
	}

	@Test
	public void testUcSave() throws Exception {
		String lQuestion1 = "Question1";
		String lRemark1 = "Remark1";
		String lQuestion2 = "Question2";
		String lRemark2 = "Remark2";
		Long lAuthor1 = new Long(77);
		Long lAuthor2 = new Long(7);

		QuestionHome lQuestionHome = (QuestionHome)BOMHelper.getQuestionHome();
		QuestionAuthorReviewerHome lQARHome = (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
		int lInitialHistory = BOMHelper.getQuestionHistoryHome().getCount();
		Long lGroupID = ((GroupHome)BOMHelper.getGroupHome()).createNew("testGroup1", "Group for testing", "1", "2", "3", false);
		
		Question lQuestion = (Question)lQuestionHome.create();
		Long lQuestionID = lQuestion.ucNew(lQuestion1, lRemark1, 0l, lGroupID.toString(), lAuthor1);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		lQuestion = (Question)lQuestionHome.findByKey(lKey);
		
		assertEquals("question 1", lQuestion1, (String)lQuestion.get(QuestionHome.KEY_QUESTION));
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor1);
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestion.get(QuestionHome.KEY_ID));			
		
		DomainObject lQAR = lQARHome.findByKey(lKey);
		assertNotNull("question-author-reviewer 1", lQAR);
		
		lQuestion.ucSave(lQuestion2, lRemark2, "3", lAuthor2);
		assertEquals("question 2", lQuestion2, (String)lQuestion.get(QuestionHome.KEY_QUESTION));

		assertEquals("count history 1", lInitialHistory + 1, BOMHelper.getQuestionHistoryHome().getCount());
		try {
			lQAR = lQARHome.findByKey(lKey);
			fail("shouldn't get here 1");
		}
		catch (BOMNotFoundException exc) {
			//left empty intentionally
		}
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor2);
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestion.get(QuestionHome.KEY_ID));			
		
		lQAR = lQARHome.findByKey(lKey);
		assertNotNull("question-author-reviewer 2", lQAR);
		
		//save again with identical values, therefore, no update is made
		lQuestion.ucSave(lQuestion2, lRemark2, "3", lAuthor1);

		assertEquals("count history 2", lInitialHistory + 1, BOMHelper.getQuestionHistoryHome().getCount());
		
		lQAR = lQARHome.findByKey(lKey);
		assertNotNull("question-author-reviewer 3", lQAR);
	}
	
	@Test
	public void testIndexing() throws Exception {
//		IndexHouseKeeper.redirectDocRoot(true);
		
		Object[] lActorID = new Object[] {new Long(96)};
		assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());

		//preparation: create group and open it
		Long lGroupID = data.createGroup();
 	 	Group lGroup = data.getGroupHome().getGroup(lGroupID);
 	 	((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {new GroupStateChangeParameters()});
		
 	 	//preparation: create two members who will be the authors of the question
 	 	String[] lAuthorIDs = data.create2Members();
 	 	
 	 	//create new question and link it to the admin
 	 	Long lQuestionID = data.createQuestion("Question1", "2:5.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[0]), true);
		assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());
		
		pause(900);
		//the question is indexed after publishing
		Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
		assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());
		
		//the index entry is deleted after deletion of the question
		lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE2, lActorID);
		assertEquals("number of indexed 3", 0, IndexHouseKeeper.countIndexedContents());
		
		//create another question and link it to the author
 	 	lQuestionID = data.createQuestion("Question2", "2:5.2", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[1]), true);
		assertEquals("number of indexed 4", 0, IndexHouseKeeper.countIndexedContents());
		
		pause(900);
		//request and accept review
		lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_REQUEST, lActorID);
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ACCEPT, lActorID);
		data.deleteAllFromQuestionHistory();
		assertEquals("number of indexed 5", 0, IndexHouseKeeper.countIndexedContents());
		
		//now we publish the reviewed question
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_PUBLISH, lActorID);
		assertEquals("number of indexed 6", 1, IndexHouseKeeper.countIndexedContents());
		data.deleteAllFromQuestionHistory();
		
		//setting the question's state to answered and reopening the question doesn't change the indexing
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
		assertEquals("number of indexed 7", 1, IndexHouseKeeper.countIndexedContents());
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_REOPEN1, lActorID);
		data.deleteAllFromQuestionHistory();
		assertEquals("number of indexed 8", 1, IndexHouseKeeper.countIndexedContents());
		
		//now we delete the answered question
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
		data.deleteAllFromQuestionHistory();
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE1, lActorID);
		assertEquals("number of indexed 9", 0, IndexHouseKeeper.countIndexedContents());
	}
	
	private void pause(long inMillis) throws InterruptedException {
		synchronized (this) {
			wait(inMillis);
		}
	}

}
