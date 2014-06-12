package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.SingleValueQueryStatement;
import org.hip.kernel.bom.impl.SingleValueQueryStatementImpl;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class SingleValueQueryStatementImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
	}

	@Test
	public void testExecuteQuery() throws SQLException {
		SingleValueQueryStatement lStatement = new SingleValueQueryStatementImpl();
		String lSQL = "SELECT COUNT(TestID) FROM tblTest";
	
		String[] lNames = {"1 eins", "2 zwei", "3 drei"};
		String[] lFirstnames = {"Eva", "Pustekuchen", "Fi"};
	
		assertNotNull("not null", lStatement);
		assertEquals("number of entries before", 0, lStatement.executeQuery(lSQL).intValue());
		
		data.createTestEntry(lNames[2], lFirstnames[2]);		
		assertEquals("number of entries 1", 1, lStatement.executeQuery(lSQL).intValue());
		data.createTestEntry(lNames[1], lFirstnames[1]);
		assertEquals("number of entries 2", 2, lStatement.executeQuery(lSQL).intValue());
		data.createTestEntry(lNames[0], lFirstnames[0]);
		assertEquals("number of entries 3", 3, lStatement.executeQuery(lSQL).intValue());
		
		lStatement = new SingleValueQueryStatementImpl();
		Collection<Object> lResult = lStatement.executeQuery2("SELECT COUNT(TESTID), COUNT(SNAME) FROM tblTest");
		Iterator<Object> lValues = lResult.iterator();
		assertEquals("length", 2, lResult.size());
		for (int i = 0; i < 2; i++) {
			assertEquals("number of entries 3."+i, "3", lValues.next().toString()); 
		}
		
		lResult = lStatement.executeQuery2("SELECT MIN(FAMOUNT), MAX(DTMUTATION) FROM tblTest");
		assertEquals("length", 2, lResult.size());
	}
}
