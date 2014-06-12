package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.SubscriptionHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 3, 2004
 */
public class SubscriptionHomeImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSubscription();
	}
	
	@Test
	public void testDo() throws Exception {
		Long lMemberID = new Long(65);
		Long lQuestionID1 = 73l;
		Long lQuestionID2 = 100l;
		SubscriptionHome lHome = data.getSubscriptionHome();
		int lCount = lHome.getCount();
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(SubscriptionHome.KEY_MEMBERID, lMemberID);
		lKey.setValue(SubscriptionHome.KEY_LOCAL, SubscriptionHome.IS_LOCAL);
		int lCountFiltered = lHome.getCount(lKey);
		
		lHome.ucNew(lQuestionID1.toString(), lMemberID, true);
		assertEquals("count 1", lCount+1, lHome.getCount());
		lHome.ucNew(lQuestionID2.toString(), lMemberID, false);
		assertEquals("count 2", lCount+2, lHome.getCount());
		assertEquals("count 3", lCountFiltered+1, lHome.getCount(lKey));
		
		assertTrue("subscription 1", lHome.hasSubscription(lQuestionID1, lMemberID));
		assertTrue("subscription 2", lHome.hasSubscription(lQuestionID2, lMemberID));
		
		lHome.updateRange(lQuestionID2, lMemberID, true);
		assertEquals("count 4", lCountFiltered+2, lHome.getCount(lKey));
		
		lHome.delete(lQuestionID2, lMemberID);
		assertEquals("count 5", lCount+1, lHome.getCount());
		assertFalse("not subscription", lHome.hasSubscription(lQuestionID2, lMemberID));
	}
}
