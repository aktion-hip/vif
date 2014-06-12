package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 16.02.2012
 */
public class AppVersionHomeTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromAppVersion();
	}

	@Test
	public final void testSetVersion() throws Exception {
		AppVersionHome lHome = BOMHelper.getAppVersionHome();
		assertEquals(0, lHome.getCount());
		assertEquals("0.1", lHome.getVersion());
		
		lHome.setVersion("0.2");
		assertEquals(1, lHome.getCount());
		assertEquals("0.2", lHome.getVersion());
		
		lHome.setVersion("1.1");
		assertEquals(1, lHome.getCount());
		assertEquals("1.1", lHome.getVersion());
	}

}
