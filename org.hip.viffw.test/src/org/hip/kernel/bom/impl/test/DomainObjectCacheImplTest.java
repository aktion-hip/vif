package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectCache;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectCacheImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class DomainObjectCacheImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
		System.out.println("Deleted all entries in tblTest.");
	}
	
	@Test
	public void testDo() throws BOMException, SQLException, VException {
		int lNumberBefore = data.getSimpleHome().getCount();

		String lName = "Dummy";
		String lFirstname1 = "Fi";
		String lFirstname2 = "Adam";
		data.createTestEntry(lName, lFirstname1);
		data.createTestEntry(lName, lFirstname2);
		assertEquals("number after insert", lNumberBefore + 2, data.getSimpleHome().getCount());
	
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname1);
		DomainObject lFound1 = data.getSimpleHome().findByKey(lKey);
		KeyObject lID1 = lFound1.getKey();
		
		DomainObjectCache lCache = new DomainObjectCacheImpl();
		lCache.put(lFound1);
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Name", lName);
		lKey.setValue("Firstname", lFirstname2);
		DomainObject lFound2 = data.getSimpleHome().findByKey(lKey);
		KeyObject lID2 = lFound2.getKey();
		lCache.put(lFound2);

		assertTrue("Identity 1", lFound1 == lCache.get(lID1));
		assertTrue("Identity 2", lFound2 == lCache.get(lID2));
		assertTrue("Not equal", !lFound1.equals(lCache.get(lID2)));
	}
}
