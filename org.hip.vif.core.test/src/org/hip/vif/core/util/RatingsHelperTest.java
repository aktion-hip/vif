package org.hip.vif.core.util;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.impl.RatingEventsHome;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 10.10.2011
 */
public class RatingsHelperTest {
	private static DataHouseKeeper data;
	private RatingsHome ratingsHome;
	private RatingEventsHome eventHome;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
		ratingsHome = BOMHelper.getRatingsHome();
		eventHome = BOMHelper.getRatingEventsHome();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public final void testHelper() throws Exception {
		Long lRatedID = 67l;
		RatingsHelper lHelper = new RatingsHelper(lRatedID);
		assertEquals("0%", lHelper.getMean1());
		assertEquals("0%", lHelper.getMean2());
		assertEquals("0%", lHelper.getMean3());
		assertEquals("0 (0)", lHelper.getTotal1());
		assertEquals("0 (0)", lHelper.getTotal2());
		assertEquals("0 (0)", lHelper.getTotal3());
		
		Long lRater1 = 66l;
		ratingsHome.saveRating(createRating(lRater1, lRatedID), lRater1, 1, 0, -1, "test");
		
		lHelper = new RatingsHelper(lRatedID);
		assertEquals("100%", lHelper.getMean1());
		assertEquals("0%", lHelper.getMean2());
		assertEquals("-100%", lHelper.getMean3());
		assertEquals("1 (1)", lHelper.getTotal1());
		assertEquals("0 (1)", lHelper.getTotal2());
		assertEquals("-1 (1)", lHelper.getTotal3());
		
		Long lRater2 = 70l;
		ratingsHome.saveRating(createRating(lRater2, lRatedID), lRater2, 1, 0, -1, "test");
		lHelper = new RatingsHelper(lRatedID);
		assertEquals("100%", lHelper.getMean1());
		assertEquals("0%", lHelper.getMean2());
		assertEquals("-100%", lHelper.getMean3());
		assertEquals("2 (2)", lHelper.getTotal1());
		assertEquals("0 (2)", lHelper.getTotal2());
		assertEquals("-2 (2)", lHelper.getTotal3());
		
		Long lRater3 = 40l;
		ratingsHome.saveRating(createRating(lRater3, lRatedID), lRater3, 0, 1, 0, "test");
		lHelper = new RatingsHelper(lRatedID);
		if (data.isEmbedded()) {			
			assertEquals("66.66%", lHelper.getMean1());
			assertEquals("33.33%", lHelper.getMean2());
			assertEquals("-66.66%", lHelper.getMean3());
		}
		else {			
			assertEquals("66.667%", lHelper.getMean1());
			assertEquals("33.333%", lHelper.getMean2());
			assertEquals("-66.667%", lHelper.getMean3());
		}
		assertEquals("2 (3)", lHelper.getTotal1());
		assertEquals("1 (3)", lHelper.getTotal2());
		assertEquals("-2 (3)", lHelper.getTotal3());
	}
	
	private Long createRating(Long inRaterID, Long inRatedID) throws VException, SQLException {
		DomainObject lEvent = eventHome.create();
		lEvent.set(RatingEventsHome.KEY_COMPLETED, 1);
		Long outEventID = lEvent.insert(true);
		
		DomainObject lRating = ratingsHome.create();
		lRating.set(RatingsHome.KEY_RATINGEVENTS_ID, outEventID);
		lRating.set(RatingsHome.KEY_RATER_ID, inRaterID);
		lRating.set(RatingsHome.KEY_RATED_ID, inRatedID);
		lRating.set(RatingsHome.KEY_ISAUTHOR, 1);
		lRating.insert(true);
		
		return outEventID;
	}

}
