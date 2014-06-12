package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 10.07.2003
 * @author Luthiger
 */
public class JoinAuthorReviewerToCompletionHomeTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class JoinAuthorReviewerToCompletionHomeSub extends JoinAuthorReviewerToCompletionHome {
		public JoinAuthorReviewerToCompletionHomeSub() {
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

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblCompletionAuthorReviewer.MEMBERID, tblCompletionAuthorReviewer.NTYPE, tblCompletion.COMPLETIONID, tblCompletion.QUESTIONID, tblCompletion.SCOMPLETION, tblCompletion.NSTATE, tblQuestion.SQUESTIONID, tblQuestion.GROUPID FROM tblCompletion INNER JOIN tblCompletionAuthorReviewer ON tblCompletion.COMPLETIONID = tblCompletionAuthorReviewer.COMPLETIONID INNER JOIN tblQuestion ON tblQuestion.QUESTIONID = tblCompletion.QUESTIONID WHERE tblCompletionAuthorReviewer.MEMBERID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinAuthorReviewerToCompletionHome lSubHome = new JoinAuthorReviewerToCompletionHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
}
