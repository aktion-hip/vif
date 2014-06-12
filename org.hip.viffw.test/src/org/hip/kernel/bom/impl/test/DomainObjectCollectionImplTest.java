package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DefaultQueryResult;
import org.hip.kernel.bom.impl.DomainObjectCollectionImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class DomainObjectCollectionImplTest {
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
	public void testDo() throws SQLException, NamingException, VException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		String[] lFirstnames = {"Eva", "Pustekuchen", "Fi"};
		String[] lFirstnames2 = {"Adam", "Egon", "Brum"};
	
		data.createTestEntry(lNames[2], lFirstnames[2]);		
		data.createTestEntry(lNames[1], lFirstnames[1]);
		data.createTestEntry(lNames[0], lFirstnames[0]);
	
		ResultSet lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		DomainObjectCollection lCollection = new DomainObjectCollectionImpl(lQueryResult);
	
		DomainObject lDomainObject;
		DomainObjectIterator lIterator = lCollection.elements();
		int i = 0;
		while (lIterator.hasMoreElements()) {
			lDomainObject = (DomainObject)lIterator.nextElement();
			assertEquals("nextElement " + i, lNames[i], (String)lDomainObject.get("Name"));
			i++;
		}
		assertEquals("numer of domain objects", lNames.length, i);
	
		lIterator = lCollection.elements();
		i = 0;
		while (lIterator.hasMoreElements()) {
			lDomainObject = (DomainObject)lIterator.nextElement();
			lDomainObject.set("Firstname", lFirstnames2[i]);
			lDomainObject.update();
			lDomainObject.release();
			i++;
		}
	
		//test find
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lNames[0]);
		lKey.setValue("Firstname", lFirstnames[0]);
	
		//test find updated
		DomainObject lFound;
		try {
			lFound = data.getSimpleHome().findByKey(lKey);
			fail("Should not find object with Firstname=" + lFirstnames[0]);
		}
		catch(BOMNotFoundException exc) {
			assertNotNull("findByKey", exc);
		}
		
		lKey = new KeyObjectImpl();
		lKey.setValue("Name", lNames[0]);
		lKey.setValue("Firstname", lFirstnames2[0]);
		lFound = data.getSimpleHome().findByKey(lKey);
		assertNotNull("found updated", lFound);
	}
	
	@Test
	public void testSerialization() throws SQLException, NamingException, IOException, ClassNotFoundException, VException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		String[] lFirstnames = {"Eva", "Pustekuchen", "Fi"};
	
		data.createTestEntry(lNames[2], lFirstnames[2]);		
		data.createTestEntry(lNames[1], lFirstnames[1]);
		data.createTestEntry(lNames[0], lFirstnames[0]);
	
		ResultSet lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		DomainObjectCollection lCollection = new DomainObjectCollectionImpl(lQueryResult);
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lCollection);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lCollection = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		DomainObjectCollection lRetrieved = (DomainObjectCollection)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		DomainObjectIterator lIterator = lRetrieved.elements();
		int i = 0;
		while (lIterator.hasMoreElements()) {
			DomainObject lDomainObject = (DomainObject)lIterator.nextElement();
			assertEquals("nextElement " + i, lNames[i], (String)lDomainObject.get("Name"));
			i++;
		}
		assertEquals("numer of domain objects", lNames.length, i);
	}
}
