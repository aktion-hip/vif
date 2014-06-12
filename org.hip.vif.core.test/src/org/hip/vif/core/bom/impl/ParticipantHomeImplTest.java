package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ParticipantHomeImplTest.java
 * 
 * Created on 02.11.2002
 * @author Benno Luthiger
 */
public class ParticipantHomeImplTest {
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
	
	private void setGroupStateOpen(Long[] inGroupIDs) throws VException, SQLException {
		GroupHome lHome = BOMHelper.getGroupHome();
		for (Long lGroupID : inGroupIDs) {
			Group lGroup = lHome.getGroup(lGroupID);
			lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.S_OPEN));
			lGroup.update(true);
		}
	}
	
	private Collection<Long> toCollection(Long[] inArray) {
		Collection<Long> out = new ArrayList<Long>();
		Collections.addAll(out, inArray);
		return out;
	}

	@Test
	public void testSaveRegisterings() throws Exception {
		String[] lMembers = data.create2Members();
		Long[] lGroups = data.create5Groups();
		setGroupStateOpen(lGroups);
		ParticipantHome lParticipantHome = data.getParticipantHome();
		assertEquals("number 0", 0, lParticipantHome.getCount());
		
		//Register member0 for groups 1, 4 => registered in member0[1, 4]
		Long[] lGroupIDs = new Long[2];
		lGroupIDs[0] = lGroups[1];
		lGroupIDs[1] = lGroups[4];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), Arrays.asList(lGroupIDs));
		assertEquals("number 1", 2, lParticipantHome.getCount());

		Collection<Long> lRegistrations = lParticipantHome.getRegisterings(new Long(lMembers[0]));
		assertTrue("Contains 1", lRegistrations.contains(lGroups[1]));
		assertTrue("Contains 2", lRegistrations.contains(lGroups[4]));
		
		//Register member1 for groups 1, 3, 4 => registered in member1[1, 3, 4]
		lGroupIDs = new Long[3];
		lGroupIDs[0] = lGroups[1];
		lGroupIDs[1] = lGroups[4];
		lGroupIDs[2] = lGroups[3];
		lParticipantHome.saveRegisterings(new Long(lMembers[1]), Arrays.asList(lGroupIDs));
		assertEquals("number 2", 5, lParticipantHome.getCount());
		
		//Unregister member0 from 1, 4; registered to 0, 3 => registered in member0[0, 3]
		lGroupIDs = new Long[2];
		lGroupIDs[0] = lGroups[0];
		lGroupIDs[1] = lGroups[3];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), toCollection(lGroupIDs));
		assertEquals("number 3", 5, lParticipantHome.getCount());
		
		lRegistrations = lParticipantHome.getRegisterings(new Long(lMembers[0]));
		assertTrue("Contains 3", lRegistrations.contains(lGroups[0]));
		assertTrue("Contains 4", lRegistrations.contains(lGroups[3]));
		
		//Unregister member0 from 0; registered to 1 => registered in member0[1, 3]
		lGroupIDs = new Long[2];
		lGroupIDs[0] = lGroups[1];
		lGroupIDs[1] = lGroups[3];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), toCollection(lGroupIDs));
		assertEquals("number 4", 5, lParticipantHome.getCount());
		
		lRegistrations = lParticipantHome.getRegisterings(new Long(lMembers[0]));
		assertTrue("Contains 5", lRegistrations.contains(lGroups[1]));
		assertTrue("Contains 6", lRegistrations.contains(lGroups[3]));
		
		//Unregister member1 from 1, 3, 4 => registered in member1[]
		lGroupIDs = new Long[0];
		lParticipantHome.saveRegisterings(new Long(lMembers[1]), toCollection(lGroupIDs));
		assertEquals("number 5", 2, lParticipantHome.getCount());
	}

	@Test
	public void testSaveRegisteringsPrivate() throws Exception {
		String[] lMembers = data.create2Members();
		Long[] lGroups = data.create5Groups();
		setGroupStateOpen(lGroups);
		
		ParticipantHome lParticipantHome = data.getParticipantHome();
		assertEquals("number 0", 0, lParticipantHome.getCount());
		
		//Make group 0 private
		Group lGroup = data.getGroupHome().getGroup(lGroups[0]);
		lGroup.set(GroupHome.KEY_PRIVATE, new Long(GroupHome.IS_PRIVATE));
		lGroup.update(true);
		
		//Make member 0 participating in group 0
		Long[] lGroupIDs = new Long[1];
		lGroupIDs[0] = lGroups[0];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), toCollection(lGroupIDs));
		assertEquals("number 1", 1, lParticipantHome.getCount());
		
		//Make member 0 participating in group 2, 3 => member 0 remains participating in 0
		lGroupIDs = new Long[2];
		lGroupIDs[0] = lGroups[2];
		lGroupIDs[1] = lGroups[3];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), toCollection(lGroupIDs));
		assertEquals("number 2", 3, lParticipantHome.getCount());
		
		//Unregister member 0 from group 2, 3 => member 0 remains participating in 0
		lGroupIDs = new Long[0];
		lParticipantHome.saveRegisterings(new Long(lMembers[0]), toCollection(lGroupIDs));
		assertEquals("number 3", 1, lParticipantHome.getCount());

		Collection<Long> lRegistrations = lParticipantHome.getRegisterings(new Long(lMembers[0]));
		assertTrue("Contains 5", lRegistrations.contains(lGroups[0]));
	}
	
	@Test
	public void testCreateChecked() throws VException, NumberFormatException, SQLException, WorkflowException {
		String lMemberID = "88";
		ParticipantHome lHome = data.getParticipantHome();
		int lCount = lHome.getCount();
		Long lGroupID = data.createGroup();
		lHome.create(lMemberID, lGroupID.toString());
		assertEquals("count 1", lCount+1, lHome.getCount());
		
		boolean lNew = lHome.createChecked(lMemberID, lGroupID.toString());
		assertEquals("count 1", lCount+1, lHome.getCount());
		assertFalse("not created", lNew);
		
		lNew = lHome.createChecked(new Long(lMemberID), new Long(lGroupID));
		assertEquals(lCount+1, lHome.getCount());
		assertFalse(lNew);
	}
	
	@Test
	public void testIsParticipant() throws VException, NumberFormatException, WorkflowException, SQLException {
		String lActorID = "92";
		ParticipantHome lHome = data.getParticipantHome();
		assertFalse("not participant", lHome.isParticipant(new Long(lActorID)));
		
		Long lGroupID = data.createGroup();
		lHome.create(lActorID, lGroupID.toString());
		assertTrue("participant", lHome.isParticipant(new Long(lActorID)));			
	}
	
	@Test
	public void testIsParticipantOfGroup() throws VException, NumberFormatException, SQLException, WorkflowException {
		Long lActorID = new Long(92);
		ParticipantHome lHome = data.getParticipantHome();
		Long lGroupID = new Long(data.createGroup());
		assertFalse("not participant", lHome.isParticipantOfGroup(lGroupID, lActorID));
		
		lHome.create(lActorID.toString(), lGroupID.toString());
		assertTrue("participant", lHome.isParticipantOfGroup(lGroupID, lActorID));
	}
	
	@Test
	public void testGetParticipantsOfGroup() throws VException, NumberFormatException, SQLException {
		Long[] lGroups = data.create2Groups();
		Long lGroupID1 = new Long(lGroups[0]);
		Long lGroupID2 = new Long(lGroups[1]);
		
		ParticipantHome lHome = data.getParticipantHome();
		int lCount = lHome.getCount();
		
		DomainObject lParticipant = lHome.create();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(11));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, lGroupID1);
		lParticipant.insert(true);
		
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(12));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, lGroupID1);
		lParticipant.insert(true);
		
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(13));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, lGroupID1);
		lParticipant.insert(true);
		
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(11));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, lGroupID2);
		lParticipant.insert(true);
		
		lParticipant.setVirgin();
		lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(13));
		lParticipant.set(ParticipantHome.KEY_GROUP_ID, lGroupID2);
		lParticipant.insert(true);
		
		assertEquals("count", lCount + 5, lHome.getCount());
		assertEquals("registered 1", 3, lHome.getParticipantsOfGroup(lGroupID1));
		assertEquals("registered 2", 2, lHome.getParticipantsOfGroup(lGroupID2));
	}
	
	@Test
	public void testRemove() throws VException, NumberFormatException, SQLException, WorkflowException {
		Long lActorID = new Long(86);
		ParticipantHome lHome = data.getParticipantHome();
		assertFalse("not participant before", lHome.isParticipant(lActorID));
		assertEquals("no participants", 0, lHome.getCount());
		
		Long lGroupID = data.createGroup();
		lHome.create(lActorID.toString(), lGroupID.toString());
		assertTrue("participant", lHome.isParticipant(lActorID));			
		assertEquals("one participant", 1, lHome.getCount());
		
		lHome.removeParticipant(new Long(lGroupID), lActorID);
		assertEquals("no participants again", 0, lHome.getCount());
		assertFalse("not participant after", lHome.isParticipant(lActorID));			
	}
	
	@Test
	public void testSuspendParticipation() throws Exception {
		String[] lMembers = data.create2Members();
		Long[] lGroups = data.create5Groups();
		ParticipantHome lParticipantHome = data.getParticipantHome();
		
		//Register member0 for groups 1, 4 => registered in member0[1, 4]
		Long lActorID = new Long(lMembers[0]);
		Long[] lGroupIDs = new Long[2];
		lGroupIDs[0] = lGroups[1];
		lGroupIDs[1] = lGroups[4];
		lParticipantHome.saveRegisterings(lActorID, Arrays.asList(lGroupIDs));
		
		Collection<Object> lSuspended = lParticipantHome.getActorSuspend(lActorID);
		assertEquals("number of values", 2, lSuspended.size());
		Timestamp lNow = new Timestamp(new Date().getTime());
		for (Iterator<Object> lDates = lSuspended.iterator(); lDates.hasNext();) {
			assertTrue("Not suspended", ((Timestamp)lDates.next()).before(lNow));
		}
		
		
		GregorianCalendar[] lRange = new GregorianCalendar[2]; // {new GregorianCalendar(1999, 10, 20), new GregorianCalendar(1999, 10, 20)};
		lRange[0] = new GregorianCalendar(1999, 10, 20);
		lRange[1] = new GregorianCalendar(1999, 11, 15);
		lParticipantHome.suspendParticipation(lActorID, new Timestamp(lRange[0].getTimeInMillis()), new Timestamp(lRange[1].getTimeInMillis()));
		lSuspended = lParticipantHome.getActorSuspend(lActorID);
		int i = 0;
		for (Iterator<Object> lDates = lSuspended.iterator(); lDates.hasNext();) {
			assertEquals("Date suspended " + i, lRange[i++].getTimeInMillis(), ((Timestamp) lDates.next()).getTime());
		}
	}

}
