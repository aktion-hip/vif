package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.impl.DefaultQueryStatement;
import org.hip.kernel.bom.impl.DefaultStatement;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class QueryStatementTest {
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
	public void testDo() throws Exception {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		String lSQL = "SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName";
		String lExpected1 = "< org.hip.kernel.bom.impl.DefaultQueryStatement SQL=\"null\" />";
		String lExpected2 = "< org.hip.kernel.bom.impl.DefaultQueryStatement SQL=\"SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName\" />";
		
		QueryStatement lStatement = new DefaultQueryStatement(data.getSimpleHome());
		assertFalse(lStatement.hasMoreResults());
		assertEquals("toString 1", lExpected1, lStatement.toString());
		
		QueryResult lResult = lStatement.executeQuery(lSQL);
		
		int i = 0;
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lModel = lResult.next();
			assertEquals("name " + i, lNames[i++], lModel.get(Test2DomainObjectHomeImpl.KEY_NAME));
		}
		assertEquals(3, i);
		assertEquals(lSQL, ((DefaultQueryStatement)lStatement).getSQLString());
		assertEquals(lExpected2, lStatement.toString());
	}
	
	@Test
	public void testEquals() throws Exception {
		String lSQL = "SELECT * FROM tblTest ORDER BY sName";
		
		QueryStatement lStatement1 = new DefaultQueryStatement(data.getSimpleHome());
		lStatement1.executeQuery(lSQL);

		QueryStatement lStatement2 = new DefaultQueryStatement(data.getSimpleHome());
		lStatement2.executeQuery("SELECT * FROM tblTest");

		QueryStatement lStatement3 = new DefaultQueryStatement(data.getSimpleHome());
		lStatement3.executeQuery(lSQL);

		assertTrue("equals", lStatement1.equals(lStatement3));
		assertEquals("equal hash code", lStatement1.hashCode(), lStatement3.hashCode());

		assertTrue("not equals", !lStatement1.equals(lStatement2));
		assertTrue("not equal hashcode", lStatement1.hashCode() != lStatement2.hashCode());
	}
	
	@Test
	public void testDelete() throws Exception {
		String lExpectedName1 = "Test_Name_1";
		String lExpectedName2 = "Test_Name_2";
		
		data.createTestEntry(lExpectedName1);
		data.createTestEntry(lExpectedName2);
		
		DomainObjectHome lHome = data.getSimpleHome();
		assertEquals("Number after insert", 2, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue("Name", lExpectedName1);
		
		String lDelete = "DELETE FROM TBLTEST WHERE TBLTEST.SNAME='Test_Name_1'";
		QueryStatement lStatement = new DefaultQueryStatement(data.getSimpleHome());
		lStatement.setSQLString(lDelete);
		
		assertEquals("Number of deleted", 1, lStatement.executeUpdate(true));			
		assertEquals("Number after delete", 1, lHome.getCount());
		
		assertEquals("Remaining entry", lExpectedName2, (String)lHome.select().next().get("Name"));
	}
	
	@Test
	public void testSerialization() throws SQLException, IOException, ClassNotFoundException {
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		data.createTestEntry(lNames[2]);		
		data.createTestEntry(lNames[1]);
		data.createTestEntry(lNames[0]);

		QueryStatement lStatement = new DefaultQueryStatement(data.getSimpleHome());
		String lSQL = "SELECT SNAME FROM tblTest ORDER BY sName";
		lStatement.executeQuery(lSQL);
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lStatement);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lStatement = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		QueryStatement lRetrieved = (QueryStatement)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		assertEquals("SQL command", lSQL, lRetrieved.getSQLString());
	}
	
	@Test
	public void testShowTables() throws Exception {
		QueryStatement lStatement = new DefaultStatement();
		Collection<String> lTables = lStatement.showTables();
		assertTrue("number of tables", lTables.size() > 20);
		
		Collection<String> lTablesLower = new Vector<String>();
		for (String lTable : lTables) {
			lTablesLower.add(lTable.toLowerCase());
		}
		assertTrue("catalog contains tblquestion", lTablesLower.contains("tblquestion"));
		assertTrue("catalog contains tblmember", lTablesLower.contains("tblmember"));
		assertTrue("catalog contains tblgroup", lTablesLower.contains("tblgroup"));
		assertTrue("catalog contains tblcompletion", lTablesLower.contains("tblcompletion"));
		assertTrue("catalog contains tblgroupadmin", lTablesLower.contains("tblgroupadmin"));
		assertTrue("catalog contains tblparticipant", lTablesLower.contains("tblparticipant"));
		assertTrue("catalog contains tblpermission", lTablesLower.contains("tblpermission"));
		assertTrue("catalog contains tblrole", lTablesLower.contains("tblrole"));
	}
}
