package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Actor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Jul 15, 2004
 */
public class ActorTest {
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
	public void testGetActorID() throws VException {
		Long lExpected1 = new Long(77); 
		String lExpected2 = "test77";

		Actor lActor = new Actor(lExpected1, lExpected2);		
		assertEquals("actor id", lExpected1, lActor.getActorID());
		assertEquals("user id", lExpected2, lActor.getUserID());
	}
	
	@Test
	public void testIsRegistered() throws VException, WorkflowException, SQLException {
		Long lActorID = new Long(77); 

		Actor lActor = new Actor(lActorID, "test77");
		Long lGroupID = new Long(data.createGroup());
		assertFalse("not registered", lActor.isRegistered(lGroupID));
		
		data.getParticipantHome().create(lActorID.toString(), lGroupID.toString());
		assertTrue("registered", lActor.isRegistered(lGroupID));
	}
	
	@Test
	public void testIsGroupAdmin() throws VException {
		Long lActorID = new Long(77); 

		Actor lActor = new Actor(lActorID, "test77");
		Long lGroupID = new Long(data.createGroup());
		assertFalse("not administering", lActor.isGroupAdmin(lGroupID));
		
		data.getGroupAdminHome().associateGroupAdmins(lGroupID, new String[] {lActorID.toString()});
		assertTrue("administering", lActor.isGroupAdmin(lGroupID));
	}
	
	@Test
	public void testRefreshAuthorization() throws Exception {
		String lUserID = "test77";
		String lPermissions1 = "testPermission1 testPermission2";
		String lPermissions2 = "testPermission1";

		Long[] lActors = data.create2MembersAndRoleAndPermissions();
		Actor lActor1 = new Actor(lActors[0], lUserID);
		assertEquals("permissions 1", lPermissions1, join(lActor1.getAuthorization().getPermissions()));
		
		Actor lActor2 = new Actor(lActors[1], lUserID);
		assertEquals("permissions 2", lPermissions2, join(lActor2.getAuthorization().getPermissions()));
		
		data.getLinkMemberRoleHome().deleteGroupAdminRole(lActors[1]);
		lActor2.refreshAuthorization();
		assertEquals("permissions 3", "", join(lActor2.getAuthorization().getPermissions()));
	}
	
	private String join(Collection<String> inList) {
		String lSeparator = " ";
		StringBuilder out = new StringBuilder();
		for (String lPart : inList) {
			out.append(lPart).append(lSeparator);
		}
		return out.toString().trim();
	}
}
