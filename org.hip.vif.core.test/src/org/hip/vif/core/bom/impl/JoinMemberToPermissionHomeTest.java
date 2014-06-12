package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
 * @author Benno Luthiger
 */
public class JoinMemberToPermissionHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class JoinMemberToPermissionHomeSub extends JoinMemberToPermissionHome {
		public JoinMemberToPermissionHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue("MemberID", "4");
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
			lExpected = "SELECT tblLinkMemberRole.MEMBERID, tblRole.ROLEID, tblRole.SXMLROLEID, tblRole.SROLEDESCRIPTION, tblRole.BGROUPSPECIFIC, tblPermission.PERMISSIONID, tblPermission.SPERMISSIONLABEL FROM tblLinkMemberRole INNER JOIN tblRole ON tblLinkMemberRole.ROLEID = tblRole.ROLEID INNER JOIN tblLinkPermissionRole ON tblRole.ROLEID = tblLinkPermissionRole.ROLEID INNER JOIN tblPermission ON tblLinkPermissionRole.PERMISSIONID = tblPermission.PERMISSIONID WHERE tblLinkMemberRole.MEMBERID = '4'";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblLinkMemberRole.MemberID, tblRole.RoleID, tblRole.sXMLRoleID, tblRole.sRoleDescription, tblRole.bGroupSpecific, tblPermission.PERMISSIONID, tblPermission.SPERMISSIONLABEL FROM tblLinkPermissionRole, tblPermission, tblLinkMemberRole, tblRole WHERE tblLinkMemberRole.RoleID = tblRole.RoleID AND tblRole.RoleID = tblLinkPermissionRole.ROLEID AND tblLinkPermissionRole.PERMISSIONID = tblPermission.PERMISSIONID AND tblLinkMemberRole.MemberID = '4'";
		}			
		JoinMemberToPermissionHome lSubHome = new JoinMemberToPermissionHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testSelect() throws Exception {
		JoinMemberToPermissionHome lJoinHome = BOMHelper.getJoinMemberToPermissionHome();
		
		Long[] lMemberIDs = data.create2MembersAndRoleAndPermissions();
		
		QueryResult lSelected = lJoinHome.select(lMemberIDs[0]);
		Vector<String> lRoleIDs = new Vector<String>();
		Vector<String> lPermissionLabels = new Vector<String>();
		while (lSelected.hasMoreElements()) {
			GeneralDomainObject lJoinEntry = lSelected.next();
			lRoleIDs.add((String)lJoinEntry.get("RoleID"));
			lPermissionLabels.add((String)lJoinEntry.get("PermissionLabel"));
		}
		assertEquals("number of entries 1", 2, lPermissionLabels.size());
		assertTrue("role SU", lRoleIDs.contains(String.valueOf(RoleHome.ROLE_SU)));
		assertTrue("permission 1", lPermissionLabels.contains(DataHouseKeeper.PERMISSION_LABEL_1));
		assertTrue("permission 2", lPermissionLabels.contains(DataHouseKeeper.PERMISSION_LABEL_2));
		
		lSelected = lJoinHome.select(lMemberIDs[1]);
		lRoleIDs = new Vector<String>();
		lPermissionLabels = new Vector<String>();
		while (lSelected.hasMoreElements()) {
			GeneralDomainObject lJoinEntry = lSelected.next();
			lRoleIDs.add((String)lJoinEntry.get("RoleID"));
			lPermissionLabels.add((String)lJoinEntry.get("PermissionLabel"));
		}
		assertEquals("number of entries 2", 1, lPermissionLabels.size());
		assertTrue("role group admin", lRoleIDs.contains(String.valueOf(RoleHome.ROLE_GROUP_ADMIN)));
		assertTrue("permission 3", lPermissionLabels.contains(DataHouseKeeper.PERMISSION_LABEL_1));
	}
}
