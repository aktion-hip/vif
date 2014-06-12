package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.GroupByObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 17.09.2009
 */
public class RatingsCountEfficiencyHomeTest {

	@BeforeClass
	public static void init() throws Exception {
		DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjectsEfficiency() throws Exception {
		RatingsCountEfficiencyHome lHome = new RatingsCountEfficiencyHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblRatings.NEFFICIENCY, COUNT(tblRatings.NEFFICIENCY) AS Count FROM tblRatingEvents INNER JOIN tblRatings ON tblRatingEvents.RATINGEVENTSID = tblRatings.RATINGEVENTSID WHERE tblRatings.RATEDID = 67 AND tblRatingEvents.BCOMPLETED != 0 GROUP BY tblRatings.NEFFICIENCY ORDER BY tblRatings.NEFFICIENCY";
		assertEquals("test object", lExpected, lTest.next().toString());
	}
	
	@Test
	public void testObjectsEtiquette() throws Exception {
		RatingsCountEtiquetteHome lHome = new RatingsCountEtiquetteHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblRatings.NETIQUETTE, COUNT(tblRatings.NETIQUETTE) AS Count FROM tblRatingEvents INNER JOIN tblRatings ON tblRatingEvents.RATINGEVENTSID = tblRatings.RATINGEVENTSID WHERE tblRatings.RATEDID = 67 AND tblRatingEvents.BCOMPLETED != 0 GROUP BY tblRatings.NETIQUETTE ORDER BY tblRatings.NETIQUETTE";
		assertEquals("test object", lExpected, lTest.next().toString());
	}

//	---
	
	@SuppressWarnings("serial")
	private class RatingsCountEfficiencyHomeSub extends RatingsCountEfficiencyHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RatingsHome.KEY_RATED_ID, new Long(67));
				lKey.setValue(RatingEventsHome.KEY_COMPLETED, new Integer(0), "!=", BinaryBooleanOperator.AND);
				GroupByObject lGroup = new GroupByObjectImpl();
				lGroup.setValue(RatingsHome.KEY_EFFICIENCY, 1);
				OrderObject lOrder = new OrderObjectImpl();
				lOrder.setValue(RatingsHome.KEY_EFFICIENCY, 1);
				out.add(createSelectString(lKey, lOrder, null, lGroup));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
	
	@SuppressWarnings("serial")
	private class RatingsCountEtiquetteHomeSub extends RatingsCountEtiquetteHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RatingsHome.KEY_RATED_ID, new Long(67));
				lKey.setValue(RatingEventsHome.KEY_COMPLETED, new Integer(0), "!=", BinaryBooleanOperator.AND);
				GroupByObject lGroup = new GroupByObjectImpl();
				lGroup.setValue(RatingsHome.KEY_ETIQUETTE, 1);
				OrderObject lOrder = new OrderObjectImpl();
				lOrder.setValue(RatingsHome.KEY_ETIQUETTE, 1);
				out.add(createSelectString(lKey, lOrder, null, lGroup));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}		
	}

}
