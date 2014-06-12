package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.List;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFMember;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 14.08.2003
 * @author Luthiger
 */
public class AbstractMemberTest {
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
	public void testIsSameMember() throws Exception {
		int lCount = data.getGroupHome().getCount();
		Long[] lGroupIDs = data.create2Groups();
		assertEquals("count 1", lCount+2, data.getGroupHome().getCount());
		
		lCount = data.getMemberHome().getCount();
		String lMemberID1 = data.createMember("1");
		String lMemberID2 = data.createMember("2");
		assertEquals("count 2", lCount+2, data.getMemberHome().getCount());

		Member lMember_1 = data.getMemberHome().getMember(lMemberID1);
		Member lMember_2 = data.getMemberHome().getMember(lMemberID2);
		
		ParticipantHome lHome = data.getParticipantHome();
		lCount = lHome.getCount();
		lHome.create(lMemberID1, lGroupIDs[0].toString());
		lHome.create(lMemberID2, lGroupIDs[0].toString());
		assertEquals("count 3", lCount+2, data.getParticipantHome().getCount());
		
		List<VIFMember> lMembers = data.getJoinParticipantToMemberHome().getActive(new Long(lGroupIDs[0]));
		assertEquals("count 4", 2, lMembers.size());
		VIFMember lMember1 = lMembers.get(0);
		if (lMember1.getMemberID().toString().equals(lMemberID1)) {
			assertTrue("same 1", lMember1.isSameMember(new Long(lMemberID1)));
			assertFalse("not same 1", lMember1.isSameMember(new Long(lMemberID2)));
			
			assertTrue("same 2", lMember1.isSameMember((VIFMember)lMember_1));
			assertFalse("not same 2", lMember1.isSameMember((VIFMember)lMember_2));
		}
		else {
			assertTrue("same 3", lMember1.isSameMember(new Long(lMemberID2)));
			assertFalse("not same 3", lMember1.isSameMember(new Long(lMemberID1)));
			
			assertTrue("same 4", lMember1.isSameMember((VIFMember)lMember_2));
			assertFalse("not same 4", lMember1.isSameMember((VIFMember)lMember_1));
		}
	}

}
