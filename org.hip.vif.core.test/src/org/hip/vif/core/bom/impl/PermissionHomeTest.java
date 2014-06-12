package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 13.07.2009
 */
public class PermissionHomeTest {
	private final static String labelExp = "addNewLabel";
	private final static String descriptionExp = "New Permission added for testing purpose";

	private static DataHouseKeeper data;

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
	public void testGetPermission() throws BOMException, SQLException, ExternIDNotUniqueException, VException {
		PermissionHome lHome = data.getPermissionHome();
		assertEquals("number of entries 0", 0, lHome.getCount());
		
		Long lID = lHome.createPermission(labelExp, descriptionExp);
		assertTrue("new ID not 0", lID.longValue() != 0);
		assertEquals("number of entries 1", 1, lHome.getCount());
		
		Permission lPermission = lHome.getPermission(lID);
		assertEquals("retrieved label", labelExp, lPermission.get(PermissionHome.KEY_LABEL));
		assertEquals("retrieved description", descriptionExp, lPermission.get(PermissionHome.KEY_DESCRIPTION));
	}

	@Test
	public void testCreatePermission() throws ExternIDNotUniqueException, BOMException, SQLException, VException {
		PermissionHome lHome = data.getPermissionHome();
		assertEquals("number of entries 0", 0, lHome.getCount());
		
		Long lID = lHome.createPermission(labelExp, descriptionExp);
		assertTrue("new ID not 0", lID.longValue() != 0);
		assertEquals("number of entries 1", 1, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(PermissionHome.KEY_ID, lID);
		Permission lPermission = (Permission) lHome.findByKey(lKey);
		assertEquals("Retrieved description", descriptionExp, lPermission.get(PermissionHome.KEY_DESCRIPTION));
		
		try {
			lID = lHome.createPermission(labelExp, "something different");
			fail("Shouldn't get here!");
		}
		catch (ExternIDNotUniqueException exc) {
			//intentionally left empty
		}
	}

}
