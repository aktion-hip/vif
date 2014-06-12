package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.CompletionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 26.09.2005
 */
public class JoinCompletionForIndexHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class JoinCompletionForIndexHomeSub extends JoinCompletionForIndexHome {
		public JoinCompletionForIndexHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(CompletionHome.KEY_ID, new Integer(32));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}
		public String testKey(KeyObject inKey) throws VException {
			return createSelectString(inKey);
		}
	}
	

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblCompletion.COMPLETIONID, tblCompletion.SCOMPLETION, tblCompletion.NSTATE, tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.GROUPID, tblGroup.SNAME AS GROUPNAME, tblGroup.BPRIVATE, tblGroup.NSTATE, tblCompletionAuthorReviewer.NTYPE, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblCompletion INNER JOIN tblQuestion ON tblCompletion.QUESTIONID = tblQuestion.QUESTIONID INNER JOIN tblGroup ON tblQuestion.GROUPID = tblGroup.GROUPID INNER JOIN tblCompletionAuthorReviewer ON tblCompletion.COMPLETIONID = tblCompletionAuthorReviewer.COMPLETIONID INNER JOIN tblMember ON tblCompletionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblCompletion.COMPLETIONID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinCompletionForIndexHome lSubHome = new JoinCompletionForIndexHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testGetKeyPublicAndAuthor() throws VException {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblCompletion.COMPLETIONID, tblCompletion.SCOMPLETION, tblCompletion.NSTATE, tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.GROUPID, tblGroup.SNAME AS GROUPNAME, tblGroup.BPRIVATE, tblGroup.NSTATE, tblCompletionAuthorReviewer.NTYPE, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblCompletion INNER JOIN tblQuestion ON tblCompletion.QUESTIONID = tblQuestion.QUESTIONID INNER JOIN tblGroup ON tblQuestion.GROUPID = tblGroup.GROUPID INNER JOIN tblCompletionAuthorReviewer ON tblCompletion.COMPLETIONID = tblCompletionAuthorReviewer.COMPLETIONID INNER JOIN tblMember ON tblCompletionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblCompletionAuthorReviewer.NTYPE = 0 AND tblGroup.BPRIVATE = 0 AND (tblCompletion.NSTATE = 4 OR tblCompletion.NSTATE = 6 OR tblCompletion.NSTATE = 5) AND (tblGroup.NSTATE = 2 OR tblGroup.NSTATE = 3 OR tblGroup.NSTATE = 4 OR tblGroup.NSTATE = 5)";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		KeyObject lKey = JoinCompletionForIndexHome.getKeyPublicAndAuthor(CompletionHome.KEY_STATE);
		JoinCompletionForIndexHomeSub lSubHome = new JoinCompletionForIndexHomeSub();
		assertEquals("test key", lExpected, lSubHome.testKey(lKey));
	}
}
