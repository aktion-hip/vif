package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
 * Created on Feb 15, 2004
 */
public class JoinSubscriptionToMemberHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class JoinSubscriptionToMemberHomeSub extends JoinSubscriptionToMemberHome {
		public JoinSubscriptionToMemberHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(SubscriptionHome.KEY_QUESTIONID, new Integer(32));
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
			lExpected = "SELECT tblSubscription.QUESTIONID, tblSubscription.BLOCAL, tblMember.MEMBERID, tblMember.SMAIL, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.BSEX FROM tblSubscription INNER JOIN tblMember ON tblSubscription.MEMBERID = tblMember.MEMBERID WHERE tblSubscription.QUESTIONID = 32";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		JoinSubscriptionToMemberHome lSubHome = new JoinSubscriptionToMemberHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
}
