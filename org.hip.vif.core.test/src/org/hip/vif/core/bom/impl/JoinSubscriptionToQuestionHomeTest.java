package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.SubscriptionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Feb 29, 2004
 */
public class JoinSubscriptionToQuestionHomeTest {
	
	@SuppressWarnings("serial")
	private class JoinSubscriptionToQuestionHomeSub extends JoinSubscriptionToQuestionHome {
		public JoinSubscriptionToQuestionHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(SubscriptionHome.KEY_MEMBERID, new Integer(76));
				outTest.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}		
	}
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblSubscription.MEMBERID, tblSubscription.BLOCAL, tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.NSTATE, tblQuestion.GROUPID FROM tblSubscription INNER JOIN tblQuestion ON tblSubscription.QUESTIONID = tblQuestion.QUESTIONID WHERE tblSubscription.MEMBERID = 76";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinSubscriptionToQuestionHome lSubHome = new JoinSubscriptionToQuestionHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
}
