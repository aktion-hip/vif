package org.hip.vif.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class DBConnectionProberTest {

	@BeforeClass
	public static void init() throws Exception {
		DataHouseKeeper.getInstance();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		DataHouseKeeper.getInstance().reInitialize();
	}

	@Test
	public final void testDo() throws Exception {
		DBConnectionProber lProber = new DBConnectionProber();
		assertFalse(lProber.needsDBConfiguration());
		assertFalse(lProber.needsTableCreation());
		assertTrue(lProber.needsSUCreation());
		
		//
		DataSourceRegistry.INSTANCE.setActiveConfiguration(new DBAccessConfiguration("DBSourceID", "Server", "Schema", "User", "Password"));
		lProber = new DBConnectionProber();
		assertTrue(lProber.needsDBConfiguration());
	}
	
}
