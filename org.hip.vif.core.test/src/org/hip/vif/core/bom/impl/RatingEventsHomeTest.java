package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 02.10.2009
 */
public class RatingEventsHomeTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromRatingEvents();
	}
	
	@Test
	public void testSetCompleted() throws Exception {
		RatingEventsHome lHome = BOMHelper.getRatingEventsHome();
		assertEquals("count 0", 0, lHome.getCount());
		Long lEventID = createRatingEvent(lHome);
		assertEquals("count 1", 1, lHome.getCount());
		
		RatingEvents lEvent = lHome.getRatingEvents(lEventID);
		assertFalse("not completed", lEvent.isCompleted());
		
		lEvent.setCompleted();
		lEvent = lHome.getRatingEvents(lEventID);
		assertTrue("completed", lEvent.isCompleted());
	}

	private Long createRatingEvent(RatingEventsHome inHome) throws VException, SQLException {
		DomainObject lEvent = inHome.create();
		lEvent.set(RatingEventsHome.KEY_CREATION, new Timestamp(System.currentTimeMillis()));
		return lEvent.insert();
	}
}
