package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.GroupHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Oct 31, 2004
 */
public class NestedGroupHome2Test {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class NestedGroupHomeSub extends NestedGroupHome2 { 
		public NestedGroupHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				OrderObject lOrder = new OrderObjectImpl();
				lOrder.setValue(GroupHome.KEY_NAME, true, 0);
				outTest.add(createSelectString(lOrder));
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

	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblGroup.SNAME, tblGroup.GROUPID, tblGroup.SDESCRIPTION, tblGroup.NMINGROUPSIZE, tblGroup.NSTATE, tblGroup.NREVIEWER, tblGroup.NGUESTDEPTH, tblGroup.BPRIVATE, count.Registered FROM tblParticipant INNER JOIN tblGroup ON tblParticipant.GROUPID = tblGroup.GROUPID INNER JOIN (SELECT tblParticipant.GROUPID, COUNT(tblParticipant.MEMBERID) AS Registered FROM tblParticipant GROUP BY tblParticipant.GROUPID) count ON tblGroup.GROUPID = count.GROUPID ORDER BY tblGroup.SNAME DESC";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}

		NestedGroupHome2 lSubHome = new NestedGroupHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
}
