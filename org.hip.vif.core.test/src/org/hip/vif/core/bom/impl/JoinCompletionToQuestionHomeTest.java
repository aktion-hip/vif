package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.CompletionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 13, 2004
 */
public class JoinCompletionToQuestionHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class JoinCompletionToQuestionHomeSub extends JoinCompletionToQuestionHome {
		public JoinCompletionToQuestionHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(CompletionHome.KEY_QUESTION_ID, new Integer(32));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}		
	}

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
		data.deleteAllFromQuestion();
		data.deleteAllFromCompletion();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblCompletion.COMPLETIONID, tblCompletion.QUESTIONID, tblCompletion.SCOMPLETION, tblCompletion.NSTATE, tblQuestion.SQUESTIONID, tblQuestion.GROUPID FROM tblCompletion INNER JOIN tblQuestion ON tblQuestion.QUESTIONID = tblCompletion.QUESTIONID WHERE tblCompletion.QUESTIONID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinCompletionToQuestionHome lSubHome = new JoinCompletionToQuestionHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}

	@Test
	public void testGetCompletions() throws Exception {
		String[] lExpected1 = {"cmpl1", "cmpl2", "cmpl3"};
		String lExpected2 = "cmpl4"; 
		Long lQuestionID1 = data.createQuestion("q1", "5.12.7");
		Long lQuestionID2 = data.createQuestion("q2", "7.7");
		data.createCompletion(lExpected1[0], lQuestionID1);
		data.createCompletion(lExpected1[1], lQuestionID1);
		data.createCompletion(lExpected1[2], lQuestionID1);
		data.createCompletion(lExpected2, lQuestionID2);
		
		Collection<String> lExpected = Arrays.asList(lExpected1);
		QueryResult lCompletions = data.getJoinCompletionToQuestionHome().getCompletions(new Long(lQuestionID1));
		int lCount = 0;
		while (lCompletions.hasMoreElements()) {
			assertTrue("completion 1." + String.valueOf(++lCount), lExpected.contains(lCompletions.next().get(CompletionHome.KEY_COMPLETION).toString()));
		}
		assertEquals("number of completions 1", 3, lCount);
		
		lCompletions = data.getJoinCompletionToQuestionHome().getCompletions(new Long(lQuestionID2));
		lCount = 0;
		while (lCompletions.hasMoreElements()) {
			assertEquals("completion 2." + String.valueOf(++lCount), lExpected2, lCompletions.next().get(CompletionHome.KEY_COMPLETION).toString());
		}
		assertEquals("number of completions 2", 1, lCount);
	}
	
	@Test
	public void testGetCompletion() throws Exception {
		String[] lExpected = {"cmpl1", "cmpl2", "cmpl3"};
		Long lQuestionID1 = data.createQuestion("q1", "5.12.7");
		Long lCompletionID1 = data.createCompletion(lExpected[0], lQuestionID1);
		Long lCompletionID2 = data.createCompletion(lExpected[1], lQuestionID1);
		Long lCompletionID3 = data.createCompletion(lExpected[2], lQuestionID1);
		
		assertEquals("completion 1", lExpected[0], data.getJoinCompletionToQuestionHome().getCompletion(new Long(lCompletionID1)).get(CompletionHome.KEY_COMPLETION));
		assertEquals("completion 2", lExpected[1], data.getJoinCompletionToQuestionHome().getCompletion(new Long(lCompletionID2)).get(CompletionHome.KEY_COMPLETION));
		assertEquals("completion 3", lExpected[2], data.getJoinCompletionToQuestionHome().getCompletion(new Long(lCompletionID3)).get(CompletionHome.KEY_COMPLETION));
	}
	
	@Test
	public void testGetGroupID() throws Exception {
		Long lGroupID = new Long(55);
		Long lQuestionID = data.createQuestion("Test question", "5.12.7", lGroupID, true);
		Long lCompletionID = data.createCompletion("Test completions", lQuestionID);
		
		assertEquals(new Long(lGroupID.longValue()), data.getJoinCompletionToQuestionHome().getGroupID(new Long(lCompletionID)));
	}
	
}
