package org.hip.vif.core.bom.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.util.RolesCheck;

/**
 * @author Benno Luthiger
 */
public class LinkMemberRoleHomeImplTest {
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
	public void testGetRolesOf() throws Exception {
		String lUserID = "userL";
		MemberHome lHomeMember = data.getMemberHome();
		LinkMemberRoleHome lHomeLink = data.getLinkMemberRoleHome();

		//pre
		data.checkForEmptyTable();
		
		//filling in some data
		Member lMember = (Member)lHomeMember.create();
		lMember.ucNew(lUserID, "NameL", "VornameL", "StrasseL", "PLZ-L", "StadtL", "", "", "mail.l@test", "1", "de", "123", new String[] {"1", "3"});
		
		assertEquals("number of associated roles 1", 2, lHomeLink.getCount());
		
		//now we are prepared to test the functionality
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("UserID", lUserID);
		lMember = (Member)lHomeMember.findByKey(lKey);			
		Collection<Role> lRoles = lHomeLink.getRolesOf(new Long(lMember.get("ID").toString()));
		assertEquals("number of associated roles 1", 2, lRoles.size());
		assertTrue("contains 1", lRoles.contains(new Role("1")));
		assertTrue("contains 3", lRoles.contains(new Role("3")));
		
		lRoles = lHomeLink.getRolesOf(new Long(0));
		assertEquals("number of associated roles 2", 0, lRoles.size());

		data.deleteAllFromLinkMemberRole();
		lRoles = lHomeLink.getRolesOf(new Long(lMember.get("ID").toString()));
		assertEquals("number of associated roles 3", 0, lRoles.size());
		
		lMember.release();
	}
	
	@Test
	public void testCheckRolesOf() throws Exception {
		String[] lRolesUnChanged = new String[] {"1", "3"};
		String[] lRolesChanged1 = new String[] {"1", "2"};
		String[] lRolesChanged2 = new String[] {"3"};
		
		//pre
		data.checkForEmptyTable();
		
		Long lMemberID = new Long(data.createMember2Roles().toString());
		
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		
		assertTrue("Unchanged roles", !lHome.checkRolesOf(lMemberID, lRolesUnChanged).hasChanged());
		assertTrue("Changed roles 1", lHome.checkRolesOf(lMemberID, lRolesChanged1).hasChanged());
		assertTrue("Changed roles 2", lHome.checkRolesOf(lMemberID, lRolesChanged2).hasChanged());
		
		RolesCheck lCheck = lHome.checkRolesOf(lMemberID, lRolesChanged1);
		assertEquals("Old roles", "1, 3", lCheck.getOldRoles());
	}
	
	@Test
	public void testAssociateRoles() throws Exception {
		Long lMemberID = new Long(33);
		String[] lRoles = new String[] {"1", "3", "4"};
		//pre
		data.checkForEmptyTable();
		
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		lHome.associateRoles(lMemberID, lRoles);
		
		assertEquals("Number of associated", 3, lHome.getCount());
		
		Collection<Role> lActualRoles = lHome.getRolesOf(lMemberID);			
		int i = 0;
		for (Role lRole : lActualRoles) {
			assertEquals("Role " + i, lRoles[i++], lRole.getElementID());
		}
	}
	
	@Test
	public void testDeleteRolesOf() throws Exception {
		Long lMemberID1 = new Long(33);
		String[] lRoles1 = new String[] {"1", "3", "4"};
		Long lMemberID2 = new Long(32);
		String[] lRoles2 = new String[] {"2", "5"};
		//pre
		data.checkForEmptyTable();
		
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		lHome.associateRoles(lMemberID1, lRoles1);
		lHome.associateRoles(lMemberID2, lRoles2);
		
		assertEquals("Number of roles 0", 5, lHome.getCount());
		
		lHome.deleteRolesOf(lMemberID1);
		assertEquals("Number of roles 1", 2, lHome.getCount());
		
		Collection<Role> lRoles = lHome.getRolesOf(lMemberID1);
		assertEquals("Number of roles of 1", 0, lRoles.size());
		
		lRoles = lHome.getRolesOf(lMemberID2);
		assertEquals("Number of roles of 2", 2, lRoles.size());
		
		int i = 0;
		for (Role lRole : lRoles) {
			assertEquals("Role element " + i, lRoles2[i++], lRole.getElementID());
		}
	}
	
	@Test
	public void testCreateParticipant() throws Exception {
		Long lMemberID = new Long(96);
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		int lCount = lHome.getCount();
		lHome.createParticipantRole(lMemberID);
		assertEquals("count 1", lCount+1, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(LinkMemberRoleHome.KEY_MEMBER_ID, lMemberID);
		DomainObject lDomainObject = lHome.findByKey(lKey);
		assertEquals("role", LinkMemberRoleHome.ROLE_PARTICIPANT.toString(), lDomainObject.get(LinkMemberRoleHome.KEY_ROLE_ID).toString());
		
		lHome.deleteParticipantRole(lMemberID);
		assertEquals("count 2", lCount, lHome.getCount());			
	}
	
	@Test
	public void testParticipantRole() throws Exception {
		Long lMemberID = new Long(96);
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		assertFalse("not participant", lHome.hasRoleParticipant(lMemberID));
		
		lHome.createParticipantRole(lMemberID);
		assertTrue("now participant", lHome.hasRoleParticipant(lMemberID));
	}
	
	@Test
	public void testGroupAdminRole() throws Exception {
		Long lMemberID = new Long(97);
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		assertFalse("not group admin", lHome.hasRoleGroupAdmin(lMemberID));
		
		lHome.createGroupAdminRole(lMemberID);
		assertTrue("now group admin", lHome.hasRoleGroupAdmin(lMemberID));
	}
	
	@Test
	public void testUpdateRoles() throws Exception {
		Long lMemberID = new Long(96);
		String[] lRoles1 = new String[] {"1", "3"};
		String[] lRoles2 = new String[] {"1", "2"};
		String[] lRoles3 = new String[] {"2"};
		
		LinkMemberRoleHome lHome = data.getLinkMemberRoleHome();
		assertTrue(lHome.updateRoles(lMemberID, new Vector<String>(Arrays.asList(lRoles1))));
		assertFalse(lHome.updateRoles(lMemberID, new Vector<String>(Arrays.asList(lRoles1))));
		assertEquals(2, lHome.getRolesOf(lMemberID).size());
		
		assertTrue(lHome.updateRoles(lMemberID, new Vector<String>(Arrays.asList(lRoles2))));
		//expect [1,2,3], 3 is not removed
		assertEquals(3, lHome.getRolesOf(lMemberID).size());
		
		assertTrue(lHome.updateRoles(lMemberID, new Vector<String>(Arrays.asList(lRoles3))));
		//expect [2,3], 3 is not removed
		assertEquals(2, lHome.getRolesOf(lMemberID).size());		
		
	}
	
}
