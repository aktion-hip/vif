package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.SQLNull;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 29.08.2009
 */
public class JoinRatingsToRaterHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}

	@Test
	public void testObjects() throws Exception {
		JoinRatingsToRaterHome lHome = new JoinRatingsToRaterHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblRatings.RATINGEVENTSID, tblRatings.RATERID, tblRatings.RATEDID, tblRatings.NCORRECTNESS, tblRatings.NEFFICIENCY, tblRatings.NETIQUETTE, tblRatings.SREMARK, tblMember.MEMBERID, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblRatings INNER JOIN tblMember ON tblRatings.RATEDID = tblMember.MEMBERID WHERE tblRatings.RATERID = 67 AND tblRatings.NEFFICIENCY IS NULL AND tblRatings.NETIQUETTE IS NULL";
		assertEquals("test sql", lExpected, (String)lTest.next());
//		System.out.println((String)lTest.next());
	}
	
	
//	
	
	@SuppressWarnings("serial")
	private class JoinRatingsToRaterHomeSub extends JoinRatingsToRaterHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RatingsHome.KEY_RATER_ID, new Long(67));
				lKey.setValue(RatingsHome.KEY_EFFICIENCY, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND);
				lKey.setValue(RatingsHome.KEY_ETIQUETTE, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND);
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
}
