package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.RoleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JoinMemberToRoleHomeTest.java
 * 
 * Created on 23.08.2002
 * @author Benno Luthiger
 */
public class JoinMemberToRoleHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class JoinMemberToRoleHomeSub extends JoinMemberToRoleHome {
		public JoinMemberToRoleHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue("MemberID", "12");
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
			lExpected = "SELECT tblLinkMemberRole.MEMBERID, tblRole.ROLEID, tblRole.SXMLROLEID, tblRole.SROLEDESCRIPTION, tblRole.BGROUPSPECIFIC FROM tblLinkMemberRole INNER JOIN tblRole ON tblLinkMemberRole.ROLEID = tblRole.ROLEID WHERE tblLinkMemberRole.MEMBERID = '12'";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblLinkMemberRole.MemberID, tblRole.RoleID, tblRole.sXMLRoleID, tblRole.sRoleDescription, tblRole.bGroupSpecific FROM tblLinkMemberRole, tblRole WHERE tblLinkMemberRole.RoleID = tblRole.RoleID AND tblLinkMemberRole.MemberID = '12'";
		}
		JoinMemberToRoleHome lSubHome = new JoinMemberToRoleHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testSelect() throws Exception {
		JoinMemberToRoleHome lJoinHome = BOMHelper.getJoinMemberToRoleHome();
		
		Long[] lMemberIDs = data.create2MembersAndRoleAndPermissions();

		QueryResult lSelected = lJoinHome.select(lMemberIDs[0]);
		Vector<String> lRoleIDs = new Vector<String>();
		while (lSelected.hasMoreElements()) {
			GeneralDomainObject lJoinEntry = lSelected.next();
			lRoleIDs.add((String)lJoinEntry.get("RoleID"));
		}
		assertEquals("number of entries 1", 1, lRoleIDs.size());
		assertEquals("role SU", String.valueOf(RoleHome.ROLE_SU), lRoleIDs.elementAt(0));
		
		lSelected = lJoinHome.select(lMemberIDs[1]);
		lRoleIDs = new Vector<String>();
		while (lSelected.hasMoreElements()) {
			GeneralDomainObject lJoinEntry = lSelected.next();
			lRoleIDs.add((String)lJoinEntry.get("RoleID"));
		}
		assertEquals("number of entries 2", 1, lRoleIDs.size());
		assertEquals("role group admin", String.valueOf(RoleHome.ROLE_GROUP_ADMIN), lRoleIDs.elementAt(0));
	}
}
