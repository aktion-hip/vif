package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * GroupHomeImplTest.java
 * 
 * Created on 09.08.2002
 * @author Benno Luthiger
 */
public class GroupHomeImplTest {
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
	public void testDo() throws Exception {
		GroupHome lHome = data.getGroupHome();
		String lExpected = "Group for testing";

		int lNumberBefore = lHome.getCount();
		Long lGroupID1 = lHome.createNew("testGroup1", lExpected, "1", "2", "3", false);
		assertEquals("created 1", lNumberBefore + 1, lHome.getCount());
		
		Long lGroupID2 = lHome.createNew("testGroup2", lExpected + "111", "1", "2", "3", false);
		assertEquals("created 2", lNumberBefore + 2, lHome.getCount());
		
		Group lRetrieved = lHome.getGroup(lGroupID1.toString());
		assertEquals("retrieved 1", lExpected, lRetrieved.get(GroupHome.KEY_DESCRIPTION));
		lRetrieved = lHome.getGroup(lGroupID1);
		assertEquals("retrieved 2", lExpected, lRetrieved.get(GroupHome.KEY_DESCRIPTION));
		
		//retrieve with incorrect group id
		try {
			lRetrieved = lHome.getGroup(lGroupID2.toString() + "1");
			fail("shouldn't get here");
		}
		catch (BOMInvalidKeyException exc) {
			//left blank intentionally
		}
	}
	
	@Test
	public void testSelectForAdministration() throws Exception {
		GroupHome lHome = data.getGroupHome();
		int lCountMembers = data.getMemberHome().getCount();
		int lCountGroups = lHome.getCount();
		int lCountGroupAdmins = data.getGroupAdminHome().getCount();
		
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(GroupHome.KEY_NAME, 0);

		Long lIDAdmin = new Long(data.createMemberRoles("TestAMIN", "TestMember1", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_ADMIN}).intValue());
		Long lIDGroupAdmin = new Long(data.createMemberRoles("TestGROUPADMIN", "TestMember2", new String[] {ApplicationConstants.ROLE_ID_MEMBER, ApplicationConstants.ROLE_ID_GROUP_ADMIN}).intValue());
		Long lIDMember = new Long(data.createMemberRoles("TestMEMBER", "TestMember3", new String[] {ApplicationConstants.ROLE_ID_MEMBER}).intValue());
		assertEquals("count members", lCountMembers + 3, data.getMemberHome().getCount());
		
		Long[] lGroups = data.create5Groups();
		assertEquals("count groups", lCountGroups + 5, lHome.getCount());
		
		Long[] lExpectedGroupAdmin = {lGroups[1], lGroups[3], lGroups[4]};
		for (int i = 0; i < lExpectedGroupAdmin.length; i++) {
			data.createGroupAdmin(lExpectedGroupAdmin[i], lIDGroupAdmin);
		}
		assertEquals("count group admins", lCountGroupAdmins + 3, data.getGroupAdminHome().getCount());
		
		QueryResult lResult = lHome.selectForAdministration(lIDAdmin, lOrder);
		int i = 0;
		Collection<Long> lExpected = new Vector<Long>(Arrays.asList(lGroups));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Admin", lExpected.contains(lObject.get(GroupHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Admin 1", lGroups.length, i);
		
		lResult = lHome.selectForAdministration(lIDGroupAdmin, lOrder);
		i = 0;
		lExpected = new Vector<Long>(Arrays.asList(lExpectedGroupAdmin));
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lObject = lResult.nextAsDomainObject();				
			assertTrue("contains Group Admin", lExpected.contains(lObject.get(GroupHome.KEY_ID)));
			i++;
		}
		assertEquals("number of filtered Group Admin 1", lExpectedGroupAdmin.length, i);
		
		lResult = lHome.selectForAdministration(lIDMember, lOrder);
		i = 0;
		while (lResult.hasMoreElements()) {
			lResult.nextAsDomainObject();
			i++;
			fail("should'nt get here");
		}
		assertEquals("number of filtered Member 1", 0, i);
	}
	
	@Test
	public void testNeedsReview() throws Exception {
		GroupHome lHome = data.getGroupHome();
		int lCount = lHome.getCount();
		lHome.createNew("test1", "Test 1", "1", "4", "5", false);
		BigDecimal lGroupID1 = lHome.getMax(GroupHome.KEY_ID);
		lHome.createNew("test2", "Test 2", "0", "4", "5", false);
		BigDecimal lGroupID2 = lHome.getMax(GroupHome.KEY_ID);
		assertEquals("count 1", lCount+2, lHome.getCount());
		
		assertTrue("review", lHome.needsReview(lGroupID1.toString()));
		assertFalse("no review", lHome.needsReview(lGroupID2.toString()));
	}
}
