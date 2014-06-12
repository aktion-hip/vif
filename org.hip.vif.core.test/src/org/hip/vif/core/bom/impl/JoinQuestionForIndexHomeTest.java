package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 24.09.2005
 */
public class JoinQuestionForIndexHomeTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class JoinQuestionForIndexHomeSub extends JoinQuestionForIndexHome {
		public JoinQuestionForIndexHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHome.KEY_ID, new Integer(32));
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
			lExpected = "SELECT tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.GROUPID, tblQuestion.NSTATE, tblGroup.SNAME AS GROUPNAME, tblGroup.BPRIVATE, tblGroup.NSTATE, tblQuestionAuthorReviewer.NTYPE, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblQuestion INNER JOIN tblQuestionAuthorReviewer ON tblQuestion.QUESTIONID = tblQuestionAuthorReviewer.QUESTIONID INNER JOIN tblMember ON tblQuestionAuthorReviewer.MEMBERID = tblMember.MEMBERID INNER JOIN tblGroup ON tblQuestion.GROUPID = tblGroup.GROUPID WHERE tblQuestion.QUESTIONID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinQuestionForIndexHome lSubHome = new JoinQuestionForIndexHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testGetKeyPublicAndAuthor() throws VException {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.GROUPID, tblQuestion.NSTATE, tblGroup.SNAME AS GROUPNAME, tblGroup.BPRIVATE, tblGroup.NSTATE, tblQuestionAuthorReviewer.NTYPE, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblQuestion INNER JOIN tblQuestionAuthorReviewer ON tblQuestion.QUESTIONID = tblQuestionAuthorReviewer.QUESTIONID INNER JOIN tblMember ON tblQuestionAuthorReviewer.MEMBERID = tblMember.MEMBERID INNER JOIN tblGroup ON tblQuestion.GROUPID = tblGroup.GROUPID WHERE tblQuestionAuthorReviewer.NTYPE = 0 AND tblGroup.BPRIVATE = 0 AND (tblQuestion.NSTATE = 4 OR tblQuestion.NSTATE = 6 OR tblQuestion.NSTATE = 5) AND (tblGroup.NSTATE = 2 OR tblGroup.NSTATE = 3 OR tblGroup.NSTATE = 4 OR tblGroup.NSTATE = 5)";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		KeyObject lKey = JoinQuestionForIndexHome.getKeyPublicAndAuthor(QuestionHome.KEY_STATE);
		JoinQuestionForIndexHomeSub lSubHome = new JoinQuestionForIndexHomeSub();
		assertEquals("test key", lExpected, lSubHome.testKey(lKey));
	}
}
