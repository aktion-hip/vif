package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class PermissionImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {		
		data.deleteAllInAll();
	}
	
	@Test
	public void testNew() throws Exception {
		String lLabelExp = "addNewLabel";
		String lDescriptionExp = "New Permission added for testing purpose";
		PermissionHome lHome = data.getPermissionHome();

		assertEquals("number of permissions 1", 0, lHome.getCount());
		Permission lPermission = (Permission)lHome.create();
		lPermission.ucNew(lLabelExp, lDescriptionExp);
		
		assertEquals("number of permissions 2", 1, lHome.getCount());
		assertEquals("label of inserted", lLabelExp, (String)lHome.select().next().get("Label"));
		
		try {
			lPermission.setVirgin();
			lPermission.ucNew(lLabelExp, "123");
			fail("Shouldn't get here");
		}
		catch (ExternIDNotUniqueException exc) {
			//left empty inentionally
		}
	}
	
}
