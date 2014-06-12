package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import org.hip.kernel.bitmap.IDPosition;
import org.hip.kernel.bitmap.IDPositions;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class LinkPermissionRoleHomeImplTest {
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
	public void testGetEntry() throws VException, SQLException {
		LinkPermissionRoleHome lHome = data.getLinkPermissionRoleHome();
		
		IDPositions lPositions = data.create2Permissions3Links();
		IDPosition lEntry1 = (IDPosition)lPositions.get(0);
		
		assertEquals("number of links 1", 3, lHome.getCount());
		
		DomainObject lLink = lHome.getEntry(lEntry1);
		
		assertEquals("PermissionID", lEntry1.getRowID(), String.valueOf(lLink.get("PermissionID")));
		assertEquals("RoleID", lEntry1.getColumnID(), String.valueOf(lLink.get("RoleID")));
		
		lLink.delete(true);
		assertEquals("number of links 2", 2, lHome.getCount());	
	}
	
	@Test
	public void testAddAndDelete() throws VException, SQLException {
		LinkPermissionRoleHome lHome = data.getLinkPermissionRoleHome();
		Collection<IDPosition> lPositions1 = new Vector<IDPosition>();
		IDPosition lPosition = new IDPosition("10", "1");
		lPositions1.add(lPosition);
		lPosition = new IDPosition("12", "1");
		lPositions1.add(lPosition);
		lPosition = new IDPosition("13", "4");
		lPositions1.add(lPosition);
		
		assertEquals("number 1", 0, lHome.getCount());
		lHome.create(lPositions1);
		assertEquals("number 2", 3, lHome.getCount());
		
		Collection<IDPosition> lPositions2 = new Vector<IDPosition>();
		lPosition = new IDPosition("13", "5");
		lPositions2.add(lPosition);
		lHome.create(lPositions2);
		assertEquals("number 3", 4, lHome.getCount());
		
		lHome.delete(lPositions1);
		assertEquals("number 4", 1, lHome.getCount());
		
		lHome.delete(lPositions2);
		assertEquals("number 5", 0, lHome.getCount());
	}
	
	@Test
	public void testAddAndDelete2() throws Exception {
		LinkPermissionRoleHome lHome = data.getLinkPermissionRoleHome();
		Collection<IDPosition> lPositions = new Vector<IDPosition>();
		Collections.addAll(lPositions, new IDPosition("10", "1"), new IDPosition("12", "1"), new IDPosition("10", "4"));
		
		assertEquals(0, lHome.getCount());
		lHome.create(lPositions);
		assertEquals(3, lHome.getCount());
		
		lHome.delete(10l);
		assertEquals(1, lHome.getCount());
		
		lHome.delete(12l);
		assertEquals(0, lHome.getCount());
	}

	@Test
	public void testCreateLink() throws Exception {
		LinkPermissionRoleHome lHome = data.getLinkPermissionRoleHome();
		assertEquals("number 0", 0, lHome.getCount());
		
		lHome.createLink(new Long(98), 3);
		assertEquals("number 1", 1, lHome.getCount());
	}

}
