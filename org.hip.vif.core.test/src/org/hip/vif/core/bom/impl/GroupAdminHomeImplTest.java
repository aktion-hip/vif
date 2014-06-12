package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Vector;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * GroupAdminHomeImplTest.java
 * 
 * Created on 09.08.2002
 * @author Benno Luthiger
 */
public class GroupAdminHomeImplTest {
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
	public void testAssociateGroupAdmins() throws Exception {
		//create group and get groupID
		GroupHome lGroupHome = data.getGroupHome();
		int lGroupsBefore = lGroupHome.getCount();
		Long lGroupID = lGroupHome.createNew("testGroup1", "test for association", "1", "2", "3", false);
		assertEquals("number of groups", lGroupsBefore + 1, lGroupHome.getCount());
		
		//create members and get ids
		MemberHome lMemberHome = data.getMemberHome();
		int lMembersBefore = lMemberHome.getCount();
		String[] lGroupAdmins = data.create2Members();
		assertEquals("number of members", lMembersBefore + 2, lMemberHome.getCount());
		
		GroupAdminHome lHome = data.getGroupAdminHome();
		int lAdminsBefore = lHome.getCount();
		lHome.associateGroupAdmins(lGroupID, lGroupAdmins);
		assertEquals("number of admins 1", lAdminsBefore + 2, lHome.getCount());
		
		//add the same admins again
		lHome.associateGroupAdmins(lGroupID, lGroupAdmins);
		assertEquals("number of admins 2", lAdminsBefore + 2, lHome.getCount());
		
		//adding admins which don't exist
		lGroupAdmins[0] += "11";
		lGroupAdmins[1] += "22";
		lHome.associateGroupAdmins(lGroupID, lGroupAdmins);
		assertEquals("number of admins 3", lAdminsBefore + 4, lHome.getCount());
		
		//adding same admins with collection
		Collection<Long> lAdminIDs = new Vector<Long>(lGroupAdmins.length);
		for (String lAdminID : lGroupAdmins) {
			lAdminIDs.add(new Long(lAdminID));
		}
		lHome.associateGroupAdmins(lGroupID, lAdminIDs);
		assertEquals(lAdminsBefore + 4, lHome.getCount());		
	}
	
	@Test
	public void testCheckGroupAdmins() throws Exception {
		Long lGroupID = new Long(87);
		GroupAdminHome lHome = data.getGroupAdminHome();
		int lCount = lHome.getCount();
		lHome.associateGroupAdmins(lGroupID, new String[] {"21", "20", "32", "33"});
		assertEquals("count", lCount + 4, lHome.getCount());
		
		Collection<String> lNotAdmins = lHome.checkGroupAdmins(new String[] {"77", "20", "78", "33"});
		assertEquals("count not admins", 2, lNotAdmins.size());
		assertTrue("contains 1", lNotAdmins.contains("77"));
		assertTrue("contains 1", lNotAdmins.contains("78"));
	}
	
	@Test
	public void testIsGroupAdmin() throws Exception {
		Long lGroupID = new Long(87);
		GroupAdminHome lHome = data.getGroupAdminHome();
		int lCount = lHome.getCount();
		lHome.associateGroupAdmins(lGroupID, new String[] {"21", "20", "32", "33"});
		assertEquals("count 1", lCount + 4, lHome.getCount());
		
		assertTrue("is admin 1", lHome.isGroupAdmin(new Long(21)));
		assertTrue("is admin 2", lHome.isGroupAdmin(new Long(20)));
		assertTrue("is admin 3", lHome.isGroupAdmin(new Long(32)));
		assertTrue("is admin 4", lHome.isGroupAdmin(new Long(33)));
		if (lCount == 0) {
			assertFalse("not admin 1", lHome.isGroupAdmin(new Long(10)));
		}
		
		assertTrue("is admin 5", lHome.isGroupAdmin(new Long(32), lGroupID));
		if (lCount == 0) {
			assertFalse("not admin 2", lHome.isGroupAdmin(new Long(10), lGroupID));
			assertFalse("not admin 3", lHome.isGroupAdmin(new Long(32), new Long(55)));
		}
	}

}
