package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JoinGroupAdminToMemberHomeTest.java
 * 
 * Created on 16.08.2002
 * @author Benno Luthiger
 */
public class JoinGroupAdminToMemberHomeTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class JoinGroupAdminToMemberHomeSub extends JoinGroupAdminToMemberHome {
		public JoinGroupAdminToMemberHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue("GroupID", "5");
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
			lExpected = "SELECT tblGroupAdmin.GROUPID, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL, tblMember.BSEX FROM tblGroupAdmin INNER JOIN tblMember ON tblGroupAdmin.MEMBERID = tblMember.MEMBERID WHERE tblGroupAdmin.GROUPID = '5'";
		}
		else if (data.isDBOracle()) {
			lExpected = "SELECT tblGroupAdmin.GroupID, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL, tblMember.BSEX FROM tblGroupAdmin, tblMember WHERE tblGroupAdmin.MemberID = tblMember.MEMBERID AND tblGroupAdmin.GroupID = '5'";
		}
		JoinGroupAdminToMemberHome lSubHome = new JoinGroupAdminToMemberHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
	@Test
	public void testSelect() throws Exception {
		//add three members
		String[] lAdmins = data.create3Members();
		
		String[] lAdminNames = new String[3];
		MemberHome lMemberHome = data.getMemberHome();
		for (int i = 0; i < lAdmins.length; i++) {
			lAdminNames[i] = (String)lMemberHome.getMember(lAdmins[i]).get("Name");
		}
		
		//add two groups
		Long[] lGroups = data.create2Groups();
		//associate one member to first group as group admin
		data.getGroupAdminHome().associateGroupAdmins(lGroups[0], new String[] {lAdmins[0]});
		//associate tow members to second group as group admins
		String[] lAdmins2 = new String[2];
		System.arraycopy(lAdmins, 1, lAdmins2, 0, 2);
		data.getGroupAdminHome().associateGroupAdmins(lGroups[1], lAdmins2);

		//retrieve group admins of different groups
		JoinGroupAdminToMemberHome lHome = (JoinGroupAdminToMemberHome)BOMHelper.getJoinGroupAdminToMemberHome();
		QueryResult lResult = lHome.select(new Long(lGroups[0]));
		while (lResult.hasMoreElements()) {
			assertEquals("name 0", lAdminNames[0], (String)lResult.next().get("Name"));
		}
		
		int lCount = 1;
		lResult = lHome.select(new Long(lGroups[1]));
		while (lResult.hasMoreElements()) {
			assertEquals("name " + lCount, lAdminNames[lCount], (String)lResult.next().get("Name"));
			lCount++;
		}
	}
}
