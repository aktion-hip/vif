package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.core.search.VIFIndexing;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * GroupImplTest.java
 * 
 * Created on 09.08.2002
 * @author Benno Luthiger
 */
public class GroupImplTest {
	private GroupStateChangeParameters parameters;
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		parameters = new GroupStateChangeParameters();
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testNew() throws Exception {
		GroupHome lHome = data.getGroupHome();
		String lGroupID = "testGroup";

		int lNumberBefore = lHome.getCount();
		Group lGroup = (Group)lHome.create();
		
		//normal creation
		lGroup.ucNew(lGroupID, "Group for testing", "1", "2", "3", false);
		assertEquals("new 1", lNumberBefore + 1, lHome.getCount());
		
		//missing group ID
		lGroup = (Group)lHome.create();
		try {
			lGroup.ucNew("", "Group for testing", "1", "2", "3", false);
			fail("new 2: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//missing group name
		lGroup = (Group)lHome.create();
		try {
			lGroup.ucNew(lGroupID + "1", "", "1", "2", "3", false);
			fail("new 3: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//both missing
		lGroup = (Group)lHome.create();
		try {
			lGroup.ucNew("", "", "1", "2", "3", false);
			fail("new 4: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//create group with group ID which exists yet
		lGroup = (Group)lHome.create();
		try {
			lGroup.ucNew(lGroupID, "Group for testing 2", "1", "2", "3", false);
			fail("new 5: shouldn't get here");
		}
		catch (ExternIDNotUniqueException exc) {
			//left blank intentionally
		}
		assertEquals("number 2", lNumberBefore + 1, lHome.getCount());
		
		lGroup = (Group)lHome.create();
		lGroup.ucNew(lGroupID + "2", "Group for testing 2", "1", "2", "3", false);
		assertEquals("number 3", lNumberBefore + 2, lHome.getCount());			
	}
	
	@Test
	public void testNew2() throws Exception {
		GroupHome lHome = data.getGroupHome();
		String lGroupID = "testGroup";
		
		int lNumberBefore = lHome.getCount();
		Group lGroup = (Group)lHome.create();
		//normal creation
		lGroup.set(GroupHome.KEY_NAME, lGroupID);
		lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing");
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		lGroup.ucNew();
		assertEquals("new 1", lNumberBefore + 1, lHome.getCount());
		
		//missing group ID
		lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_NAME, ""); //empty mandatory field
		lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing");
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		try {
			lGroup.ucNew();
			fail("new 2: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//missing group name
		lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_NAME, lGroupID + "1");
		lGroup.set(GroupHome.KEY_DESCRIPTION, ""); //empty mandatory field
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		try {
			lGroup.ucNew();
			fail("new 3: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//both missing
		lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_NAME, ""); //empty mandatory field
		lGroup.set(GroupHome.KEY_DESCRIPTION, ""); //empty mandatory field
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		try {
			lGroup.ucNew();
			fail("new 4: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//create group with group ID which exists yet
		lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_NAME, lGroupID); //existing group name
		lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing 2"); 
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		try {
			lGroup.ucNew();
			fail("new 5: shouldn't get here");
		}
		catch (ExternIDNotUniqueException exc) {
			//left blank intentionally
		}
		assertEquals("number 2", lNumberBefore + 1, lHome.getCount());
		
		lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_NAME, lGroupID + "2");
		lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing 2"); 
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		lGroup.ucNew();
		assertEquals("number 3", lNumberBefore + 2, lHome.getCount());			
	}
	
	@Test
	public void testSave() throws Exception {
		GroupHome lHome = data.getGroupHome();
		String lGroupName = "testGroup";
		String lExpected1 = "Group for retrieving 1";
		String lExpected2 = "Group for retrieving 2";

		int lNumberBefore = lHome.getCount();
		
		//create two groups
		Group lGroup = (Group)lHome.create();
		lGroup.ucNew(lGroupName + "1", "Group for testing 1", "1", "2", "3", false);

		lGroup = (Group)lHome.create();
		lGroup.ucNew(lGroupName + "2", "Group for testing 2", "1", "2", "3", false);
		assertEquals("number 1", lNumberBefore + 2, lHome.getCount());
	
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "1");
		Group lUpdated = (Group)lHome.findByKey(lKey);
		lUpdated.ucSave(lGroupName + "1", lExpected1, "1", "2", "3", false);
		
		lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "2");
		lUpdated = (Group)lHome.findByKey(lKey);
		lUpdated.ucSave(lGroupName + "2", lExpected2, "1", "2", "3", false);

		assertEquals("number 2", lNumberBefore + 2, lHome.getCount());
		
		lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "1");
		DomainObject lRetrieved = lHome.findByKey(lKey);
		assertEquals("retrieved name", lExpected1, (String)lRetrieved.get(GroupHome.KEY_DESCRIPTION));
		
		//update group id to a value which exists yet
		try {
			((Group)lRetrieved).ucSave(lGroupName + "2", "???", "1", "2", "3", false);
			fail("save 1: shouldn't get here");
		}
		catch (ExternIDNotUniqueException exc) {
			//left blank intentionally
		}
		
		//update with missing name
		try {
			((Group)lRetrieved).ucSave(lGroupName + "1", "", "1", "2", "3", false);
			fail("save 2: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//update with missing id
		try {
			((Group)lRetrieved).ucSave("", "missing", "1", "2", "3", false);
			fail("save 3: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//Test activation of group if MinGroupSize is set to number of regestered participants
		Long lGroupID = new Long(lRetrieved.get(GroupHome.KEY_ID).toString());
		((Group)lRetrieved).ucSave(lGroupName + "1", "Group for testing 1", "1", "2", "5", false);
		create3Participants(lGroupID);
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 1", VIFGroupWorkflow.STATE_CREATED, lGroup.get(GroupHome.KEY_STATE).toString());

		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 2", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
		
		try {
			lGroup.ucSave(lGroupName + "1", "Group for testing 1", "1", "2", "3", false);
		}
		catch (MailGenerationException exc) {
			//intentionally left empty
		}
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 3", VIFGroupWorkflow.STATE_ACTIVE, lGroup.get(GroupHome.KEY_STATE).toString());
	}
	
	
	@Test
	public void testSave2() throws Exception {
		GroupHome lHome = data.getGroupHome();
		String lGroupName = "testGroup";
		String lExpected1 = "Group for retrieving 1";
		String lExpected2 = "Group for retrieving 2";
		
		int lNumberBefore = lHome.getCount();
		
		//create two groups
		Group lGroup = (Group)lHome.create();
		lGroup.ucNew(lGroupName + "1", "Group for testing 1", "1", "2", "3", false);
		
		lGroup = (Group)lHome.create();
		lGroup.ucNew(lGroupName + "2", "Group for testing 2", "1", "2", "3", false);
		assertEquals("number 1", lNumberBefore + 2, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "1");
		Group lUpdated = (Group)lHome.findByKey(lKey);
		lUpdated.set(GroupHome.KEY_NAME, lGroupName + "1");
		lUpdated.set(GroupHome.KEY_DESCRIPTION, lExpected1);
		lUpdated.set(GroupHome.KEY_REVIEWERS, 1l);
		lUpdated.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lUpdated.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lUpdated.ucSave(lGroupName + "1", 3);
		
		lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "2");
		lUpdated = (Group)lHome.findByKey(lKey);
		lUpdated.set(GroupHome.KEY_NAME, lGroupName + "2");
		lUpdated.set(GroupHome.KEY_DESCRIPTION, lExpected2);
		lUpdated.set(GroupHome.KEY_REVIEWERS, 1l);
		lUpdated.set(GroupHome.KEY_GUEST_DEPTH, 2l);
		lUpdated.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
		lUpdated.ucSave(lGroupName + "2", 3);
		
		assertEquals("number 2", lNumberBefore + 2, lHome.getCount());
		
		lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_NAME, lGroupName + "1");
		DomainObject lRetrieved = lHome.findByKey(lKey);
		assertEquals("retrieved name", lExpected1, (String)lRetrieved.get(GroupHome.KEY_DESCRIPTION));
		
		//update group id to a value which exists yet
		try {
			lRetrieved.set(GroupHome.KEY_NAME, lGroupName + "2");
			lRetrieved.set(GroupHome.KEY_DESCRIPTION, "???");
			lRetrieved.set(GroupHome.KEY_REVIEWERS, 1l);     
			lRetrieved.set(GroupHome.KEY_GUEST_DEPTH, 2l);   
			lRetrieved.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
			((Group)lRetrieved).ucSave(lGroupName + "1", 3);
			fail("save 1: shouldn't get here");
		}
		catch (ExternIDNotUniqueException exc) {
			//left blank intentionally
		}
		
		//update with missing name
		try {
			lRetrieved.set(GroupHome.KEY_NAME, lGroupName + "2");
			lRetrieved.set(GroupHome.KEY_DESCRIPTION, "");
			lRetrieved.set(GroupHome.KEY_REVIEWERS, 1l);     
			lRetrieved.set(GroupHome.KEY_GUEST_DEPTH, 2l);   
			lRetrieved.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
			((Group)lRetrieved).ucSave(lGroupName + "1", 3);
			fail("save 2: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//update with missing id
		try {
			lRetrieved.set(GroupHome.KEY_NAME, "");
			lRetrieved.set(GroupHome.KEY_DESCRIPTION, "missing");
			lRetrieved.set(GroupHome.KEY_REVIEWERS, 1l);     
			lRetrieved.set(GroupHome.KEY_GUEST_DEPTH, 2l);   
			lRetrieved.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
			((Group)lRetrieved).ucSave(lGroupName + "1", 3);
			fail("save 3: shouldn't get here");
		}
		catch (AssertionFailedError err) {
			//left blank intentionally
		}
		
		//Test activation of group if MinGroupSize is set to number of registered participants
		Long lGroupID = new Long(lRetrieved.get(GroupHome.KEY_ID).toString());
		lRetrieved.set(GroupHome.KEY_NAME, lGroupName + "1");
		lRetrieved.set(GroupHome.KEY_DESCRIPTION, "Group for testing 1");
		lRetrieved.set(GroupHome.KEY_REVIEWERS, 1l);     
		lRetrieved.set(GroupHome.KEY_GUEST_DEPTH, 2l);   
		lRetrieved.set(GroupHome.KEY_MIN_GROUP_SIZE, 5l);
		((Group)lRetrieved).ucSave(lGroupName + "1", 3);
		create3Participants(lGroupID);
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 1", VIFGroupWorkflow.STATE_CREATED, lGroup.get(GroupHome.KEY_STATE).toString());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 2", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
		
		try {
			lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
			lGroup.ucSave(lGroupName + "1", 5);
		}
		catch (MailGenerationException exc) {
			//intentionally left empty
		}
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("state 3", VIFGroupWorkflow.STATE_ACTIVE, lGroup.get(GroupHome.KEY_STATE).toString());
	}
	
	@Test
	public void testRootCount() throws Exception {
		GroupHome lHome = data.getGroupHome();
		QuestionHome lQuestionHome = data.getQuestionHome();

		int lGroups0 = lHome.getCount();
		Long lGroupID = data.createGroup();
		assertEquals("added group for count test", lGroups0 + 1, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_ID, lGroupID);
		Group lGroup = (Group)lHome.findByKey(lKey);
		
		int lLinks0 = lQuestionHome.getCount();
		assertEquals("number of roots 0", 0, lGroup.rootCount());
		
		insertQuestion(lGroupID, 1, false);
		assertEquals("number of links 1", lLinks0 + 1, lQuestionHome.getCount());
		assertEquals("number of roots 1", 0, lGroup.rootCount());
		
		insertQuestion(lGroupID, 2, true);
		assertEquals("number of links 2", lLinks0 + 2, lQuestionHome.getCount());
		assertEquals("number of roots 2", 1, lGroup.rootCount());
		
		insertQuestion(lGroupID, 3, false);
		assertEquals("number of links 3", lLinks0 + 3, lQuestionHome.getCount());
		assertEquals("number of roots 3", 1, lGroup.rootCount());
		
		insertQuestion(lGroupID, 4, true);
		assertEquals("number of links 4", lLinks0 + 4, lQuestionHome.getCount());
		assertEquals("number of roots 4", 2, lGroup.rootCount());
	}
	
	private void insertQuestion(Long inGroupID, int inQuestionID, boolean isRoot) {
		try {
			DomainObject lQuestion = BOMHelper.getQuestionHome().create();
			
//			lQuestion.set(QuestionHome.KEY_ID, new Long(inQuestionID));
			lQuestion.set(QuestionHome.KEY_GROUP_ID, inGroupID);
			lQuestion.set(QuestionHome.KEY_ROOT_QUESTION, (isRoot ? new Long(1) : new Long(0)));
			lQuestion.set(QuestionHome.KEY_QUESTION, "Question");
			lQuestion.set(QuestionHome.KEY_QUESTION_DECIMAL, "1.1");
			lQuestion.set(QuestionHome.KEY_REMARK, "Remark");
			lQuestion.insert(true);
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testGetTransitions() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Group lGroup = (Group)lHome.create();
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_CREATED));
		assertEquals("transitions 1", VIFGroupWorkflow.TRANS_OPEN, getTransitions(lGroup.getTransitions()));
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_OPEN));
		assertEquals("transitions 2", VIFGroupWorkflow.TRANS_CLOSE, getTransitions(lGroup.getTransitions()));
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_SUSPENDED));
		assertEquals("transitions 3", VIFGroupWorkflow.TRANS_REACTIVATE, getTransitions(lGroup.getTransitions()));
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_SETTLED));
		assertEquals("transitions 4", VIFGroupWorkflow.TRANS_REACTIVATE, getTransitions(lGroup.getTransitions()));
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_CLOSED));
		assertEquals("transitions 5", VIFGroupWorkflow.TRANS_REACTIVATE, getTransitions(lGroup.getTransitions()));
		
		//Active groups can be deactivated if the number of participants drops below minimal group size
		lGroup.ucNew("testGroup1", "Group for testing 1", "1", "2", "4", false);
		Long lGroupID = lHome.getMax(GroupHome.KEY_ID).longValue();
		
		lGroup = lHome.getGroup(lGroupID.toString());
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		create3Participants(lGroupID);
		
		lGroup = lHome.getGroup(lGroupID.toString());
		lGroup.ucSave("testGroup1", "Group for testing 1", "1", "2", "3", false);
		
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("transitions 6", VIFGroupWorkflow.TRANS_SUSPEND+VIFGroupWorkflow.TRANS_CLOSE, getTransitions(lGroup.getTransitions()));
		
		data.deleteAllFromParticipant();
		lGroup = lHome.getGroup(lGroupID.toString());
		assertEquals("transitions 7", VIFGroupWorkflow.TRANS_SUSPEND+VIFGroupWorkflow.TRANS_CLOSE+VIFGroupWorkflow.TRANS_DEACTIVATE, getTransitions(lGroup.getTransitions()));
	}
	
	private String getTransitions(Collection<String> inTransitions) {
		String outTransitions = "";
		for (String lTransition : inTransitions) {
			outTransitions += lTransition;
		}
		return outTransitions;
	}
	
	@Test
	public void testWorkflow() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Long lGroupID = data.createGroup();
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupHome.KEY_ID, lGroupID);
		Group lGroup = (Group)lHome.findByKey(lKey);
		assertEquals("state created", VIFGroupWorkflow.STATE_CREATED, lGroup.get(GroupHome.KEY_STATE).toString());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		lGroup = (Group)lHome.findByKey(lKey);
		assertEquals("state open 1", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_CLOSE1, new Object[] {parameters});
		lGroup = (Group)lHome.findByKey(lKey);
		assertEquals("state closed", VIFGroupWorkflow.STATE_CLOSED, lGroup.get(GroupHome.KEY_STATE).toString());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_REOPEN, new Object[] {parameters});
		lGroup = (Group)lHome.findByKey(lKey);
		assertEquals("state open 2", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
	}
	
	@Test
	public void testGetCloseTransition() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Group lGroup = (Group)lHome.create();
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_OPEN));
		assertEquals("transition 1", VIFGroupWorkflow.TRANS_CLOSE1, lGroup.getCloseTransition());
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_ACTIVE));
		assertEquals("transition 2", VIFGroupWorkflow.TRANS_CLOSE2, lGroup.getCloseTransition());
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SETTLED));
		assertEquals("transition 3", VIFGroupWorkflow.TRANS_CLOSE3, lGroup.getCloseTransition());
	}
	
	@Test
	public void testGetReactivateTransition() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Group lGroup = (Group)lHome.create();
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SUSPENDED));
		assertEquals("transition 1", VIFGroupWorkflow.TRANS_REACTIVATE1, lGroup.getReactivateTransition());
		
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SETTLED));
		assertEquals("transition 2", VIFGroupWorkflow.TRANS_REACTIVATE2, lGroup.getReactivateTransition());

		Long lGroupID = data.createGroup();
		create3Participants(lGroupID);
		
		lGroup = lHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_CLOSED));
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, new Long(1));
		assertEquals("transition 3", VIFGroupWorkflow.TRANS_REACTIVATE3, lGroup.getReactivateTransition());

		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, new Long(5));
		assertEquals("transition 4", VIFGroupWorkflow.TRANS_REOPEN, lGroup.getReactivateTransition());
	}
	
	@Test
	public void testCheckActivationState() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Long[] lGroups = data.create2Groups();
		Long lGroupID1 = lGroups[0];
		
		Group lGroup = lHome.getGroup(lGroupID1);
		lGroup.ucSave("GroupID1", "GroupName1", "1", "0", "3", false);
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		
		lGroup = lHome.getGroup(lGroupID1);
		assertEquals("state 1", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
		
		lGroup.checkActivationState(3);
		lGroup = lHome.getGroup(lGroupID1);
		assertEquals("state 2", VIFGroupWorkflow.STATE_ACTIVE, lGroup.get(GroupHome.KEY_STATE).toString());
		
		Long lGroupID2 = lGroups[1];
		lGroup = lHome.getGroup(lGroupID2);
		lGroup.ucSave("GroupID2", "GroupName2", "1", "0", "3", false);
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});

		lGroup = lHome.getGroup(lGroupID2);
		assertEquals("state 3", VIFGroupWorkflow.STATE_OPEN, lGroup.get(GroupHome.KEY_STATE).toString());
		create3Participants(lGroupID2);
		data.getParticipantHome().create("97", lGroupID2.toString());
		lGroup = lHome.getGroup(lGroupID2);
		assertEquals("state 4", VIFGroupWorkflow.STATE_ACTIVE, lGroup.get(GroupHome.KEY_STATE).toString());			
	}
	
	private void create3Participants(Long inGroupID) throws VException, SQLException {
		ParticipantHome lParticipantHome = data.getParticipantHome();
			
		DomainObject lParticipant = lParticipantHome.create();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(11));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
		lParticipant.insert(true);
			
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(12));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
		lParticipant.insert(true);
			
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(13));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
		lParticipant.insert(true);
	}
	
	@Test
	public void testGetParticipantsMail() throws Exception {
		String[] lExpected = new String[] {"mail1@test", "mail2@test", "mail3@test", "mail4@test"};
		Long lGroupID = data.createGroup();
		String lMemberID1 = data.createMember("1", lExpected[0]);
		String lMemberID2 = data.createMember("2", lExpected[1]);
		String lMemberID3 = data.createMember("3", lExpected[2]);
		String lMemberID4 = data.createMember("4", lExpected[3]);
		
		ParticipantHome lHome = data.getParticipantHome();
		lHome.create(lMemberID1, lGroupID.toString());
		lHome.create(lMemberID2, lGroupID.toString());
		lHome.create(lMemberID3, lGroupID.toString());
		lHome.create(lMemberID4, lGroupID.toString());
		
		InternetAddress[] lMails = data.getGroupHome().getGroup(lGroupID).getParticipantsMail();
		assertEquals("number of mails", 4, lMails.length);
		for (int i = 0; i < lMails.length; i++) {
			assertEquals("mail " + String.valueOf(i), lExpected[i], lMails[i].toString());
		}
	}
	
	@Test
	public void testIsActive() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Long lGroupID = data.createGroup();
		
		Group lGroup = lHome.getGroup(lGroupID);
		assertFalse("not active 1", lGroup.isActive());

		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		lGroup = lHome.getGroup(lGroupID);
		assertFalse("not active 2", lGroup.isActive());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_ACTIVATE, new Object[] {parameters});
		lGroup = lHome.getGroup(lGroupID);
		assertTrue("active", lGroup.isActive());
		
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_SUSPEND, new Object[] {parameters});
		lGroup = lHome.getGroup(lGroupID);
		assertFalse("not active 3", lGroup.isActive());
	}
	
	@Test
	public void testIsParticipant() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Long lGroupID = data.createGroup();
		String lMemberID = data.createMember();
		
		assertFalse("not participant", lHome.getGroup(lGroupID).isParticipant(new Long(lMemberID)));
		
		data.getParticipantHome().create(lMemberID, lGroupID.toString());
		assertTrue("participant", lHome.getGroup(lGroupID).isParticipant(new Long(lMemberID)));
	}
	
	@Test
	public void testIsPrivate() throws Exception {
		GroupHome lHome = data.getGroupHome();
		Long lGroupID = data.createGroup();
		Group lGroup = lHome.getGroup(lGroupID);
		assertFalse("not private", lGroup.isPrivate());
		
		lGroup.set(GroupHome.KEY_PRIVATE, new Long(1));
		lGroup.update(true);
		
		lGroup = lHome.getGroup(lGroupID);
		assertTrue("private", lGroup.isPrivate());			
	}
	
	@Test
	public void testIsDeletable() throws Exception {
		GroupHome lHome	= data.getGroupHome();
		Long lGroupID = data.createGroup();
		Group lGroup = lHome.getGroup(lGroupID);
		WorkflowAware lWorkflowGroup = (WorkflowAware)lGroup;
		
		//created -> deletable
		assertTrue("deletable 1", lGroup.isDeletable());
		
		//open -> not deletable
		lWorkflowGroup.doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		assertFalse("not deletable 2", lGroup.isDeletable());
		
		//closed -> deletable
		lWorkflowGroup.doTransition(VIFGroupWorkflow.TRANS_CLOSE1, new Object[] {parameters});
		assertTrue("deletable 2", lGroup.isDeletable());
	}
	
	/**
	 * We only want to see content of public groups in the search index.
	 * Content of <code>created</code> or <code>closed</code> groups must not appear in the index. 
	 * 
	 * @throws Exception
	 * @see VIFGroupWorkflow
	 */
	@Test
	public void testContentIndexation() throws Exception {
		//we create the context: member as author of a question with completion in a discussion group
		//three groups: two public, one private
		GroupHome lGroupHome = data.getGroupHome();		
		String lGroupID1 = lGroupHome.createNew("TestGroup1", "Group Nr. 1", "1", "3", "10", false).toString();
		String lGroupID2 = lGroupHome.createNew("TestGroup2", "Group Nr. 2", "1", "3", "10", false).toString();
		String lGroupID_priv = lGroupHome.createNew("TestGroup3", "Group Nr. 3", "1", "3", "10", true).toString();
		//one member as author of all contributions
		Long lMemberID = new Long(data.createMember("one"));
		
		//in each group one distinct question with contribution
		Long lQuestionID1 = data.createQuestion("This is charm.", "1.1", new Long(lGroupID1), true);
		Long lCompletionID1 = data.createCompletion("Design pattern", lQuestionID1);
		data.createQuestionProducer(new Long(lQuestionID1), lMemberID, true);
		data.createCompletionProducer(new Long(lCompletionID1), lMemberID, true);
		
		Long lQuestionID2 = data.createQuestion("This is strange.", "1.1", new Long(lGroupID2), true);
		Long lCompletionID2 = data.createCompletion("Talking and shopping", lQuestionID2);
		data.createQuestionProducer(new Long(lQuestionID2), lMemberID, true);
		data.createCompletionProducer(new Long(lCompletionID2), lMemberID, true);
	
		Long lQuestionID3 = data.createQuestion("This is quark.", "1.1", new Long(lGroupID_priv), true);
		Long lCompletionID3 = data.createCompletion("Head and body", lQuestionID3);
		data.createQuestionProducer(new Long(lQuestionID3), lMemberID, true);
		data.createCompletionProducer(new Long(lCompletionID3), lMemberID, true);
		
		//publish contributions
		((WorkflowAwareContribution)data.getQuestionHome().getQuestion(lQuestionID1)).onTransition_AdminPublish(lMemberID);
		((WorkflowAwareContribution)data.getQuestionHome().getQuestion(lQuestionID2)).onTransition_AdminPublish(lMemberID);
		((WorkflowAwareContribution)data.getQuestionHome().getQuestion(lQuestionID3)).onTransition_AdminPublish(lMemberID);
		((WorkflowAwareContribution)data.getCompletionHome().getCompletion(lCompletionID1)).onTransition_AdminPublish(lMemberID);
		((WorkflowAwareContribution)data.getCompletionHome().getCompletion(lCompletionID2)).onTransition_AdminPublish(lMemberID);
		((WorkflowAwareContribution)data.getCompletionHome().getCompletion(lCompletionID3)).onTransition_AdminPublish(lMemberID);
		
		assertEquals("no contributions in the index", 0, IndexHouseKeeper.countIndexedContents());
		
		//open first group 
		((WorkflowAware)lGroupHome.getGroup(lGroupID1)).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		assertEquals("two contributions in index", 2, IndexHouseKeeper.countIndexedContents());
		assertSearch(createQuery("charm"), "searching first question", 1);
		assertSearch(createQuery("pattern"), "searching first completion", 1);
		assertSearch(createQuery("strange"), "searching second question (1)", 0);
		assertSearch(createQuery("quark"), "searching third question (1)", 0);
		
		//open second group 
		((WorkflowAware)lGroupHome.getGroup(lGroupID2)).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		assertEquals("four contributions in index", 4, IndexHouseKeeper.countIndexedContents());
		assertSearch(createQuery("strange"), "searching second question (2)", 1);
		assertSearch(createQuery("shopping"), "searching second completion", 1);
		assertSearch(createQuery("quark"), "searching third question (2)", 0);
		
		//open third group
		((WorkflowAware)lGroupHome.getGroup(lGroupID_priv)).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {parameters});
		assertEquals("still four contributions in index", 4, IndexHouseKeeper.countIndexedContents());
		assertSearch(createQuery("quark"), "still nothing found when searched for third question", 0);
		assertSearch(createQuery("body"), "nothing found when searched for third completion", 0);
		
		//close first group
		((WorkflowAware)lGroupHome.getGroup(lGroupID1)).doTransition(VIFGroupWorkflow.TRANS_CLOSE1, new Object[] {parameters});
		assertEquals("removed two contributions from index", 2, IndexHouseKeeper.countIndexedContents());
		assertSearch(createQuery("charm"), "first question removed", 0);
		assertSearch(createQuery("pattern"), "first completion removed", 0);
		
		//close second group
		((WorkflowAware)lGroupHome.getGroup(lGroupID2)).doTransition(VIFGroupWorkflow.TRANS_CLOSE1, new Object[] {parameters});
		assertEquals("removed two more contributions from index", 0, IndexHouseKeeper.countIndexedContents());
		assertSearch(createQuery("strange"), "second question removed", 0);
		assertSearch(createQuery("shopping"), "second completion removed", 0);
	}	
	
	private void assertSearch(Query inQuery, String inMsg, int inExpected) throws Exception {
		IndexReader lReader = VIFIndexing.INSTANCE.createContentIndexReader();
		try {
			Document[] lHits = IndexHouseKeeper.search(inQuery, lReader);
			assertEquals(inMsg, inExpected, lHits.length);
		}
		catch (NoHitsException exc) {
			assertEquals(inMsg, inExpected, 0);
		}
	}
	
	private Query createQuery(String inQuery) throws ParseException {
		QueryParser lParser = new QueryParser(IndexHouseKeeper.LUCENE_VERSION, AbstractSearching.IndexField.CONTENT_FULL.fieldName, IndexHouseKeeper.getAnalyzer());
		return lParser.parse(inQuery);
	}
	
}
