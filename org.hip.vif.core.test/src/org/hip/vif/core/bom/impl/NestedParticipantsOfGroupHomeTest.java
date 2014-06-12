package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Vector;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PlacefillerCollection;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.GroupAdminHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Nov 9, 2004
 */
public class NestedParticipantsOfGroupHomeTest {
	private static DataHouseKeeper data;
	private KeyObject key; 
	
	@SuppressWarnings("serial")
	private class NestedParticipantsOfGroupHomeSub extends NestedParticipantsOfGroupHome {
		public void initSelect(KeyObject inKey, PlacefillerCollection inPlacefillers) {
			try {
				key = inKey;
				select(inKey, inPlacefillers);
			}
			catch (Exception exc) {
				fail(exc.getMessage());
			}
		}
		protected Vector<Object> createTestObjects() {
			Vector<Object> outTests = new Vector<Object>();
			try {
				outTests.add(createSelectString(key));
			}
			catch (Exception exc) {
				fail(exc.getMessage());
			}
			return outTests;
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Test
	public void testSQLSelect() throws Exception {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblParticipant.GROUPID, (NOW() >= tblParticipant.DTSUSPENDFROM) AS TestSuspended1, (NOW() <= tblParticipant.DTSUSPENDTO) AS TestSuspended2, Admins.MEMBERID AS GroupAdminID, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SCITY, tblMember.SZIP, tblMember.SMAIL, tblMember.BSEX FROM tblParticipant INNER JOIN tblMember ON tblParticipant.MEMBERID = tblMember.MEMBERID LEFT JOIN (SELECT tblGroupAdmin.MEMBERID, tblGroupAdmin.GROUPID FROM tblGroupAdmin WHERE tblGroupAdmin.GROUPID = 13) AS Admins ON tblParticipant.MEMBERID = Admins.MEMBERID WHERE tblParticipant.GROUPID = 13";
		}
		
		DomainObjectHome lPlacefillerHome = data.getGroupAdminHome();
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupAdminHome.KEY_GROUP_ID, new Integer(13));
		
		PlacefillerCollection lPlacefillers = new PlacefillerCollection();
		lPlacefillers.add(lPlacefillerHome, lKey, NestedParticipantsOfGroupHome.NESTED_ALIAS);
		
		NestedParticipantsOfGroupHomeSub lHome = new NestedParticipantsOfGroupHomeSub();
		if (!data.isEmbedded()) {			
			lHome.initSelect(lKey, lPlacefillers);
			
			assertEquals("SQL select", lExpected, (String)lHome.getTestObjects().next());
		}
	}

}
