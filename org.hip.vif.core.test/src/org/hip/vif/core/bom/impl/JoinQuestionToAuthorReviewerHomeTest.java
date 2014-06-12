package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinQuestionToAuthorReviewerHomeTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class JoinQuestionToAuthorReviewerHomeSub extends JoinQuestionToAuthorReviewerHome {
		public JoinQuestionToAuthorReviewerHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, new Integer(32));
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
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestionAuthorReviewer.QUESTIONID, tblQuestionAuthorReviewer.NTYPE, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SSTREET, tblMember.SZIP, tblMember.SCITY, tblMember.BSEX, tblMember.SMAIL FROM tblQuestionAuthorReviewer INNER JOIN tblMember ON tblQuestionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblQuestionAuthorReviewer.QUESTIONID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblQuestionAuthorReviewer.QuestionID, tblQuestionAuthorReviewer.nType, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SSTREET, tblMember.SZIP, tblMember.SCITY, tblMember.BSEX, tblMember.SMAIL FROM tblQuestionAuthorReviewer, tblMember WHERE tblQuestionAuthorReviewer.MEMBERID = tblMember.MEMBERID AND tblQuestionAuthorReviewer.QUESTIONID = 32";
		}
		JoinQuestionToAuthorReviewerHome lSubHome = new JoinQuestionToAuthorReviewerHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testGetAuthors() throws Exception {
		String[] lContained = new String[2];
		Long lQuestionID = new Long(22);
		String[] lMemberIDs = data.create3Members();
		
		JoinQuestionToAuthorReviewerHome lJoinHome = data.getJoinQuestionToAuthorReviewerHome();
		QueryResult lResult = lJoinHome.getAuthors(lQuestionID.toString());
		
		assertNull("Check before", lResult.next());
		
		data.createQuestionProducer(lQuestionID, new Long(lMemberIDs[0]), true);
		lContained[0] = retrieveUserID(lMemberIDs[0]);
		lResult = lJoinHome.getAuthors(lQuestionID.toString());
		assertEquals("Count 1", 1, countEntries(lResult));
		
		data.createQuestionProducer(lQuestionID, new Long(lMemberIDs[1]), false);
		String lNotContained = retrieveUserID(lMemberIDs[1]);
		lResult = lJoinHome.getAuthors(lQuestionID.toString());
		assertEquals("Count 2", 1, countEntries(lResult));
		
		data.createQuestionProducer(lQuestionID, new Long(lMemberIDs[2]), true);
		lContained[1] = retrieveUserID(lMemberIDs[2]);
		lResult = lJoinHome.getAuthors(lQuestionID.toString());
		assertEquals("Count 3", 2, countEntries(lResult));
		
		Vector<Object> lUserIDs = new Vector<Object>();
		lResult = lJoinHome.getAuthors(lQuestionID.toString());
		while (lResult.hasMoreElements()) {
			lUserIDs.add(lResult.next().get(MemberHome.KEY_USER_ID));
		}
		
		assertTrue("contains 1", lUserIDs.contains(lContained[0]));
		assertTrue("contains 2", lUserIDs.contains(lContained[1]));
		assertTrue("not contains", !lUserIDs.contains(lNotContained));
	}
	
	private int countEntries(QueryResult lResult) {
		int outCount = 0;
		try {
			while (lResult.hasMoreElements()) {
				lResult.next();
				outCount++;
			}
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
		return outCount;
	}
	
	private String retrieveUserID(String inMemberID) {
		String outUserID = "";
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(MemberHome.KEY_ID, new Integer(inMemberID));
			outUserID = (String)data.getMemberHome().findByKey(lKey).get(MemberHome.KEY_USER_ID);
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
		return outUserID;
	}
}
