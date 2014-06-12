package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class QuestionHomeImplTest {
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
	public void testGetCountOfGroup() throws Exception {
		String lGroupID1 = "15";
		String lGroupID2 = "16";
		
		QuestionHome lHome = (QuestionHome)BOMHelper.getQuestionHome();
		
		assertEquals("count of group 1", 0, lHome.getCountOfGroup(lGroupID1));
		data.createQuestion("Q1", "1", new Long(lGroupID1), true);
		assertEquals("count of group 1", 1, lHome.getCountOfGroup(lGroupID1));
		data.createQuestion("Q2", "1.1", new Long(lGroupID1), false);
		assertEquals("count of group 2", 2, lHome.getCountOfGroup(lGroupID1));
		data.createQuestion("Q3", "2", new Long(lGroupID2), false);
		assertEquals("count of group 3", 2, lHome.getCountOfGroup(lGroupID1));
		data.createQuestion("Q4", "1.1", new Long(lGroupID1), false);
		assertEquals("count of group 4", 3, lHome.getCountOfGroup(lGroupID1));
		assertEquals("count of group 5", 1, lHome.getCountOfGroup(lGroupID2));
	}

	@Test
	public void testSelectOfGroup() throws Exception {
		String lGroupID1 = "15";
		String lGroupID2 = "16";
		String lQ1 = "Q1";
		String lQ2 = "Q2";
		String lQ3 = "Q3";
		String lQ4 = "Q4";
		
		data.createQuestion(lQ1, "1", new Long(lGroupID1), true);
		data.createQuestion(lQ2, "1.1", new Long(lGroupID1), false);
		data.createQuestion(lQ3, "2", new Long(lGroupID2), false);
		data.createQuestion(lQ4, "1.1", new Long(lGroupID1), false);
		
		QuestionHome lHome = (QuestionHome)BOMHelper.getQuestionHome();
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, false, 1);
		
		QueryResult lQuery = lHome.selectOfGroup(lGroupID1, lOrder);
		Vector<String> lTexts = getQuestionTexts(lQuery);
		assertTrue("contains 1", lTexts.contains(lQ1));
		assertTrue("contains 2", lTexts.contains(lQ2));
		assertTrue("contains 3", lTexts.contains(lQ4));
		assertTrue("contains 4", !lTexts.contains(lQ3));
		assertEquals("number of entries 1", 3, lTexts.size());
		
		lQuery = lHome.selectOfGroup(lGroupID2, lOrder);
		lTexts = getQuestionTexts(lQuery);
		assertTrue("contains 5", lTexts.contains(lQ3));
		assertTrue("contains 6", !lTexts.contains(lQ1));
		assertEquals("number of entries 2", 1, lTexts.size());
	}

	@Test
	public void testGetQuestion() throws Exception {
		String lGroupID = "15";
		String lQ1 = "Q1";
		String lQ2 = "Q2";
		
		Long lID1 = data.createQuestion(lQ1, "1", new Long(lGroupID), true);
		Long lID2 = data.createQuestion(lQ2, "1.1", new Long(lGroupID), false);
		
		QuestionHome lHome = (QuestionHome)BOMHelper.getQuestionHome();
		assertEquals("get question 1", lQ1, lHome.getQuestion(lID1).get(QuestionHome.KEY_QUESTION));
		assertEquals("get question 2", lQ2, lHome.getQuestion(lID2).get(QuestionHome.KEY_QUESTION));
		
		try {
			lHome.getQuestion("9999999999999");
			fail("shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			//left blank intentionally
		}
	}
	
	@Test
	public void testGetRootQuestions() throws Exception {
		String lGroupID = "15";
		String lQ1 = "Q1";
		String lQ2 = "Q2";
		String lQ3 = "Q3";
		String lQ4 = "Q4";
		
		data.createQuestion(lQ1, "1", new Long(lGroupID), true);
		data.createQuestion(lQ2, "1.1", new Long(lGroupID), false);
		data.createQuestion(lQ3, "2", new Long(lGroupID), true);
		data.createQuestion(lQ4, "1.1", new Long(lGroupID), false);
		
		QuestionHome lHome = (QuestionHome)BOMHelper.getQuestionHome();
		QueryResult lRoots = lHome.getRootQuestions(lGroupID);
		Vector<String> lTexts = getQuestionTexts(lRoots);
		assertTrue("contains root 1", lTexts.contains(lQ1));
		assertTrue("contains root 2", lTexts.contains(lQ3));
		assertTrue("contains root 3", !lTexts.contains(lQ4));
		assertEquals("count roots", 2, lTexts.size());
	}
	
	private Vector<String> getQuestionTexts(QueryResult inQuery) throws SQLException, VException {
		Vector<String> outQuestionTexts = new Vector<String>();
		while (inQuery.hasMoreElements()) {
			outQuestionTexts.add(inQuery.next().get(QuestionHome.KEY_QUESTION).toString());
		}
		return outQuestionTexts;
	}

	@Test
	public void testSelectOfGroupPublished() throws Exception {
		QuestionHome lQuestionHome = data.getQuestionHome();
		int lCountGroup = data.getGroupHome().getCount();
		int lCountQuestion = lQuestionHome.getCount();
		
		Long lGroupID = new Long(data.createGroup());
		assertEquals("count groups", lCountGroup + 1, data.getGroupHome().getCount());
		
		Long lQuestionID1 = data.createQuestion("This is the Root.", "1", lGroupID, WorkflowAwareContribution.S_OPEN, true);
		Long lQuestionID2 = data.createQuestion("This is question 1.1 on level 1.", "1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID3 = data.createQuestion("This is question 1.2 on level 1.", "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID4 = data.createQuestion("This is question 1.1.1 on level 2.", "1.1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID5 = data.createQuestion("This is question 1.1.2 on level 2.", "1.1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID6 = data.createQuestion("This is question 1.1.3 on level 2.", "1.1.3", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		data.createQuestion("This is question 1.2.1 on level 2.", "1.2.1", lGroupID, WorkflowAwareContribution.S_DELETED, false);
		assertEquals("count questions", lCountQuestion + 7, data.getQuestionHome().getCount());
		Long[] lExpectedValues = {lQuestionID1, lQuestionID2, lQuestionID3, lQuestionID4, lQuestionID5, lQuestionID6};
		
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, 0);
		QueryResult lResult = lQuestionHome.selectOfGroupPublished(lGroupID, lOrder);
		Collection<Long> lExpected = new Vector<Long>(Arrays.asList(lExpectedValues));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
		}
	}
	
	@Test
	public void testSelectOfGroupFiltered() throws Exception {
		QuestionHome lQuestionHome = data.getQuestionHome();
		int lCountGroup = data.getGroupHome().getCount();
		int lCountQuestion = lQuestionHome.getCount();
		int lCountAuthors = data.getQuestionAuthorReviewerHome().getCount();
		int lCountMembers = data.getMemberHome().getCount();
		
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, 0);
		
		Long lIDAdmin = new Long(data.createMemberRoles("TestAMIN", "TestMember1", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_ADMIN}).intValue());
		Long lIDGroupAdmin = new Long(data.createMemberRoles("TestGROUPADMIN", "TestMember2", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_GROUP_ADMIN}).intValue());
		Long lIDMember = new Long(data.createMemberRoles("TestMEMBER", "TestMember3", new String[] {ApplicationConstants.ROLE_ID_MEMBER}).intValue());
		assertEquals("count members", lCountMembers + 3, data.getMemberHome().getCount());
		
		Long lGroupID = new Long(data.createGroup());
		assertEquals("count groups", lCountGroup + 1, data.getGroupHome().getCount());
		
		Long lQuestionID1 = data.createQuestion("This is the Root.", "1", lGroupID, WorkflowAwareContribution.S_OPEN, true);
		Long lQuestionID2 = data.createQuestion("This is question 1.1 on level 1.", "1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID3 = data.createQuestion("This is question 1.2 on level 1.", "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID4 = data.createQuestion("This is question 1.1.1 on level 2.", "1.1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID5 = data.createQuestion("This is question 1.1.2 on level 2.", "1.1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID6 = data.createQuestion("This is question 1.1.3 on level 2.", "1.1.3", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		Long lQuestionID7 = data.createQuestion("This is question 1.2.1 on level 2.", "1.2.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		assertEquals("count questions", lCountQuestion + 7, data.getQuestionHome().getCount());
		Long[] lExpectedAdmin 		= {lQuestionID1, lQuestionID2, lQuestionID3, lQuestionID4, lQuestionID5, lQuestionID6, lQuestionID7};
		Long[] lExpectedGroupAdmin 	= {lQuestionID1, lQuestionID2, lQuestionID3, lQuestionID4, lQuestionID5, lQuestionID6};
		Long[] lExpectedMember 		= {lQuestionID1, lQuestionID3, lQuestionID5, lQuestionID6};
		
		data.createQuestionProducer(new Long(lQuestionID1), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID2), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID3), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID4), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID5), lIDMember, true);
		data.createQuestionProducer(new Long(lQuestionID6), lIDMember, true);
		data.createQuestionProducer(new Long(lQuestionID7), lIDMember, true);
		assertEquals("count authors", lCountAuthors + 7, data.getQuestionAuthorReviewerHome().getCount());
		
		QueryResult lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDAdmin);
		int i = 0;
		Collection<Long> lExpected = new Vector<Long>(Arrays.asList(lExpectedAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Admin", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Admin 1", lExpectedAdmin.length, i);
		
		lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDGroupAdmin);
		i = 0;
		lExpected = new Vector<Long>(Arrays.asList(lExpectedGroupAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains GroupAdmin", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered GroupAdmin 1", lExpectedGroupAdmin.length, i);
		
		lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDMember);
		i = 0;
		lExpected = new Vector<Long>(Arrays.asList(lExpectedGroupAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Member", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Member 1", lExpectedMember.length, i);

	}
	
	@Test
	public void testSelectOfGroupFiltered2() throws Exception {
		QuestionHome lQuestionHome = data.getQuestionHome();
		int lCountGroup = data.getGroupHome().getCount();
		int lCountQuestion = lQuestionHome.getCount();
		int lCountAuthors = data.getQuestionAuthorReviewerHome().getCount();
		int lCountMembers = data.getMemberHome().getCount();
		
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, 0);
		
		Long lIDAdmin = new Long(data.createMemberRoles("TestAMIN", "TestMember1", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_ADMIN}).intValue());
		Long lIDGroupAdmin = new Long(data.createMemberRoles("TestGROUPADMIN", "TestMember2", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_GROUP_ADMIN}).intValue());
		Long lIDMember = new Long(data.createMemberRoles("TestMEMBER", "TestMember3", new String[] {ApplicationConstants.ROLE_ID_MEMBER}).intValue());
		assertEquals("count members", lCountMembers + 3, data.getMemberHome().getCount());
		
		Long lGroupID = new Long(data.createGroup());
		assertEquals("count groups", lCountGroup + 1, data.getGroupHome().getCount());
		
		Long lQuestionID1 = data.createQuestion("This is the Root.", "1", lGroupID, WorkflowAwareContribution.S_OPEN, true);
		Long lQuestionID2 = data.createQuestion("This is question 1.1 on level 1.", "1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID3 = data.createQuestion("This is question 1.2 on level 1.", "1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID4 = data.createQuestion("This is question 1.1.1 on level 2.", "1.1.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		Long lQuestionID5 = data.createQuestion("This is question 1.1.2 on level 2.", "1.1.2", lGroupID, WorkflowAwareContribution.S_OPEN, false);
		Long lQuestionID6 = data.createQuestion("This is question 1.1.3 on level 2.", "1.1.3", lGroupID, WorkflowAwareContribution.S_ANSWERED, false);
		Long lQuestionID7 = data.createQuestion("This is question 1.2.1 on level 2.", "1.2.1", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
		assertEquals("count questions", lCountQuestion + 7, data.getQuestionHome().getCount());
		Long[] lExpectedAdmin 		= {lQuestionID1, lQuestionID2, lQuestionID3, lQuestionID4, lQuestionID5, lQuestionID6, lQuestionID7};
		Long[] lExpectedGroupAdmin 	= {lQuestionID1, lQuestionID2, lQuestionID3, lQuestionID4, lQuestionID5, lQuestionID6};
		Long[] lExpectedMember 		= {lQuestionID1, lQuestionID3, lQuestionID5, lQuestionID6};
		
		data.createQuestionProducer(new Long(lQuestionID1), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID2), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID3), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID4), lIDGroupAdmin, true);
		data.createQuestionProducer(new Long(lQuestionID5), lIDMember, true);
		data.createQuestionProducer(new Long(lQuestionID6), lIDMember, true);
		data.createQuestionProducer(new Long(lQuestionID7), lIDMember, true);
		assertEquals("count authors", lCountAuthors + 7, data.getQuestionAuthorReviewerHome().getCount());

		QueryResult lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDAdmin);
		int i = 0;
		Collection<Long> lExpected = new Vector<Long>(Arrays.asList(lExpectedAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Admin", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Admin 1", lExpectedAdmin.length, i);
		
		lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDGroupAdmin);
		i = 0;
		lExpected = new Vector<Long>(Arrays.asList(lExpectedGroupAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains GroupAdmin", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered GroupAdmin 1", lExpectedGroupAdmin.length, i);
		
		lResult = lQuestionHome.selectOfGroupFiltered(lGroupID, lOrder, lIDMember);
		i = 0;
		lExpected = new Vector<Long>(Arrays.asList(lExpectedGroupAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Member", lExpected.contains(lObject.get(QuestionHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Member 1", lExpectedMember.length, i);
	}
	
	@Test
	public void testIsRoot() throws VException, SQLException {
		Long lGroupID = new Long(12);
		Long lRootID = data.createQuestion("root", "1", lGroupID, true);
		Long lQuestionID = data.createQuestion("other", "1.1.1", lGroupID, false);
		
		QuestionHome lHome = data.getQuestionHome();
		assertTrue("root", lHome.getQuestion(lRootID).isRoot());
		assertFalse("not root", lHome.getQuestion(lQuestionID).isRoot());
	}
	
	@Test
	public void testHasQuestionsInGroup() throws Exception {
		Long[] lGroupIDs = data.create2Groups();
		QuestionHome lHome = data.getQuestionHome();
		
		assertFalse("no questions 1 (1)", lHome.hasQuestionsInGroup(lGroupIDs[0]));
		assertFalse("no questions 2 (2)", lHome.hasQuestionsInGroup(lGroupIDs[1]));
		
		data.createQuestion("Question in group 1.", "1", lGroupIDs[0], true);
		
		assertTrue("has questions 1 (1)", lHome.hasQuestionsInGroup(lGroupIDs[0]));
		assertFalse("no questions 3 (2)", lHome.hasQuestionsInGroup(lGroupIDs[1]));
		
		Long lQuestionID2 = data.createQuestion("Question in group 2.", "1", lGroupIDs[1], true);
		
		assertTrue("has questions 2 (1)", lHome.hasQuestionsInGroup(lGroupIDs[0]));
		assertTrue("has questions 2 (2)", lHome.hasQuestionsInGroup(lGroupIDs[1]));
		
		((WorkflowAwareContribution)lHome.getQuestion(lQuestionID2)).onTransition_Delete(new Long(1));
		
		assertTrue("has questions 3 (1)", lHome.hasQuestionsInGroup(lGroupIDs[0]));
		assertFalse("no questions 4 (2)", lHome.hasQuestionsInGroup(lGroupIDs[1]));
	}
	
}
