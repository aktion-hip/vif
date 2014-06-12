package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 17.09.2009
 */
public class RatingsCalculateHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}

	@Test
	public void testObjects() throws Exception {
		RatingsCalculateHome lHome = new RatingsCalculateHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT SUM(tblRatings.NCORRECTNESS) AS Sum1, SUM(tblRatings.NEFFICIENCY) AS Sum2, SUM(tblRatings.NETIQUETTE) AS Sum3, (AVG(tblRatings.NCORRECTNESS) * 100) AS Mean1, (AVG(tblRatings.NEFFICIENCY) * 100) AS Mean2, (AVG(tblRatings.NETIQUETTE) * 100) AS Mean3, COUNT(tblRatings.NCORRECTNESS) AS Count1, COUNT(tblRatings.NEFFICIENCY) AS Count2, COUNT(tblRatings.NETIQUETTE) AS Count3 FROM tblRatingEvents INNER JOIN tblRatings ON tblRatingEvents.RATINGEVENTSID = tblRatings.RATINGEVENTSID WHERE tblRatings.RATEDID = 67 AND tblRatingEvents.BCOMPLETED != 0";
		assertEquals("test object", lExpected, lTest.next().toString());
	}
	
//	---
	
	@SuppressWarnings("serial")
	private class RatingsCalculateHomeSub extends RatingsCalculateHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RatingsHome.KEY_RATED_ID, new Long(67));
				lKey.setValue(RatingEventsHome.KEY_COMPLETED, new Integer(0), "!=", BinaryBooleanOperator.AND);
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}

}
