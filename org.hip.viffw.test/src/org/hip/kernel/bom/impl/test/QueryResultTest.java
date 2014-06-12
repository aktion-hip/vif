package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.naming.NamingException;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.AlternativeModelFactory;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.impl.AbstractQueryResult;
import org.hip.kernel.bom.impl.DefaultQueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class QueryResultTest {
	private final static String EXPECTED1 = "<Name>1 eins</Name>";
	private final static String EXPECTED2 = "<Name>2 zwei</Name>";
	private final static String EXPECTED3 = "<Name>3 drei</Name>";
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

		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		ResultSet lResult = data.executeQuery("SELECT tblTest.SNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		assertTrue("hasMoreElements", lQueryResult.hasMoreElements());
			
		GeneralDomainObject lDomainObject;
		int i = 0;
		while (lQueryResult.hasMoreElements()) {
			lDomainObject = lQueryResult.nextAsDomainObject();
			assertEquals("nextAsDomainObject " + i, lNames[i], (String)lDomainObject.get("Name"));
			i++;
		}
		assertEquals("numer of domain objects", lNames.length, i);
	}
	
	@Test
	public void testLoad() throws Exception {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};

		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		ResultSet lResult = data.executeQuery("SELECT tblTest.SNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new AlternativeQueryResult(data.getSimpleHome(), lResult, null);
		Collection<AlternativeModel> lEntries = lQueryResult.load(new TestModelFactory());
		assertEquals("number of entries 1", 3, lEntries.size());
		int i = 0;
		for (AlternativeModel lAlternativeModel : lEntries) {
			assertEquals("entry 1." + i, lNames[i++], ((TestModel)lAlternativeModel).name);
		}
		
		int lExpectedSize = 2;
		lResult = data.executeQuery("SELECT tblTest.SNAME FROM tblTest ORDER BY sName");
		lQueryResult = new AlternativeQueryResult(data.getSimpleHome(), lResult, null);
		lEntries = lQueryResult.load(new TestModelFactory(), lExpectedSize);
		assertEquals("number of entries 2", lExpectedSize, lEntries.size());
		i = 0;
		for (AlternativeModel lAlternativeModel : lEntries) {
			assertEquals("entry 2." + i, lNames[i++], ((TestModel)lAlternativeModel).name);
		}
	}
	
	@Test
	public void testSerialize() throws SQLException, NamingException, VException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		String lSerializerName = "org.hip.kernel.bom.impl.XMLSerializer";

		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		ResultSet lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		String lXML = lQueryResult.nextAsXMLString();
		assertTrue("position 1", lXML.indexOf(EXPECTED1) > -1);
		
		lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		String lXML2 = lQueryResult.nextAsXMLString(lSerializerName);
		assertEquals("serialize 1", lXML, lXML2);
		
		lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		lXML = lQueryResult.nextnAsXMLString(3);
		int lPosition1 = lXML.indexOf(EXPECTED1);
		int lPosition2 = lXML.indexOf(EXPECTED2);
		int lPosition3 = lXML.indexOf(EXPECTED3);
		assertTrue("position 2", lPosition1 > -1);
		assertTrue("position 3", lPosition2 > lPosition1);
		assertTrue("position 4", lPosition3 > lPosition2);
		
		lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		lXML2 = lQueryResult.nextnAsXMLString(3, lSerializerName);
		assertEquals("serialize 2", lXML, lXML2);
	}
	
	@Test
	public void testSerialization() throws SQLException, NamingException, IOException, ClassNotFoundException, VException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		QueryResult lQueryResult = createQueryResult(lNames);
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lQueryResult);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lQueryResult = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		QueryResult lRetrieved = (QueryResult)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		//serialization happened at the beginning of the QueryResult
		int i = 0;
		while(lRetrieved.hasMoreElements()) {
			assertEquals("next " + i, lNames[i], ((DomainObject)lRetrieved.next()).get(Test2DomainObjectHomeImpl.KEY_NAME));
			i++;
		}
		assertEquals("count retrieved", i, data.getSimpleHome().getCount());
	}
	
	@Test
	public void testSerialization2() throws SQLException, VException, IOException, ClassNotFoundException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		QueryResult lQueryResult = createQueryResult(lNames);
		//set cursor two steps ahead
		lQueryResult.next();
		lQueryResult.next();
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lQueryResult);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lQueryResult = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		QueryResult lRetrieved = (QueryResult)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		//now, we expect the retrieved QueryResult positioned two steps ahead
		int i = 2;
		while(lRetrieved.hasMoreElements()) {
			assertEquals("(2) next " + i, lNames[i], ((DomainObject)lRetrieved.next()).get(Test2DomainObjectHomeImpl.KEY_NAME));
			i++;
		}
	}

	private QueryResult createQueryResult(String[] inNames) throws SQLException, VException {
		data.createTestEntry(inNames[2]);		
		data.createTestEntry(inNames[1]);
		data.createTestEntry(inNames[0]);
		
		DomainObjectHome lHome = data.getSimpleHome();
		assertEquals("count created", 3, lHome.getCount());

		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(Test2DomainObjectHomeImpl.KEY_NAME, 1);
		return lHome.select(lOrder);
	}
	
//	--- private classes ---
	
	@SuppressWarnings("serial")
	private class AlternativeQueryResult extends AbstractQueryResult {
		public AlternativeQueryResult(GeneralDomainObjectHome inHome, ResultSet inResult, QueryStatement inStatement) {
			super(inHome, inResult, inStatement);
		}
		protected boolean isCollectionLoading() {
			return true;
		}		
	}

	private class TestModelFactory implements AlternativeModelFactory {

		@Override
		public AlternativeModel createModel(ResultSet inResultSet) throws SQLException {
			return new TestModel(inResultSet.getString(1));
		}
		
	}
	
	private class TestModel implements AlternativeModel {
		public String name;

		TestModel(String inName) {
			name = inName;
		}
	}
	
}
