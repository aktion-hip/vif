package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.impl.InsertStatement;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class InsertStatementTest {
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
	public void testCommit() {
		try {
			assertEquals("number before insert", 0, data.getSimpleHome().getCount());
	
			String lSQL1 = "INSERT INTO TBLTEST( FDOUBLE, SFIRSTNAME, SLANGUAGE, TESTID, SFAX, SMAIL, STEL, SSTREET, SPLZ, SPASSWORD, FAMOUNT, SCITY, DTMUTATION, BSEX, SNAME )VALUES (NULL, 'Fi', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'Dummy' )";
			String lSQL2 = "INSERT INTO TBLTEST( FDOUBLE, SFIRSTNAME, SLANGUAGE, TESTID, SFAX, SMAIL, STEL, SSTREET, SPLZ, SPASSWORD, FAMOUNT, SCITY, DTMUTATION, BSEX, SNAME )VALUES (NULL, 'Adam', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'Pfiff' )";
			
			InsertStatement lStatement = new InsertStatement(data.getSimpleHome());
			Vector<String> lInserts = new Vector<String>();
			lInserts.addElement(lSQL1);
			lInserts.addElement(lSQL2);
			lStatement.setInserts(lInserts);
			
			Collection<Long> lAutoKeys = lStatement.executeInsert();
			lStatement.commit();
			assertEquals("number after insert", 2, data.getSimpleHome().getCount());
			assertEquals("number of auto generated keys", 2, countAutoKeys(lAutoKeys));
		}
		catch (SQLException exc) {
			fail(exc.getMessage());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testDo() {
		try {
			assertEquals("number before insert", 0, data.getSimpleHome().getCount());
	
			String lSQL1 = "INSERT INTO TBLTEST( FDOUBLE, SFIRSTNAME, SLANGUAGE, TESTID, SFAX, SMAIL, STEL, SSTREET, SPLZ, SPASSWORD, FAMOUNT, SCITY, DTMUTATION, BSEX, SNAME )VALUES (NULL, 'Fi', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'Dummy' )";
			String lSQL2 = "INSERT INTO TBLTEST( FDOUBLE, SFIRSTNAME, SLANGUAGE, TESTID, SFAX, SMAIL, STEL, SSTREET, SPLZ, SPASSWORD, FAMOUNT, SCITY, DTMUTATION, BSEX, SNAME )VALUES (NULL, 'Adam', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'Pfiff' )";
			
			InsertStatement lStatement = new InsertStatement(data.getSimpleHome());
			Vector<String> lInserts = new Vector<String>();
			lInserts.addElement(lSQL1);
			lInserts.addElement(lSQL2);
			lStatement.setInserts(lInserts);
			
			Collection<Long> lAutoKeys = lStatement.executeInsert();
			lStatement.close();
			assertEquals("number after insert", 2, data.getSimpleHome().getCount());
			assertEquals("number of auto generated keys", 2, countAutoKeys(lAutoKeys));
	
			//mySQL can't rollback
			try {
				lStatement.rollback();
			}
			catch (Error err) {
				lStatement.close();
			}
		}
		catch (SQLException exc) {
			fail(exc.getMessage());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testException() {
		InsertStatement lStatement = null;
		try {
			lStatement = new InsertStatement(data.getSimpleHome());
			Vector<String> lInserts = new Vector<String>();
			lInserts.addElement("Insert quatsch");
			lStatement.setInserts(lInserts);
			lStatement.executeInsert();
			fail("Shouldn't get here!");
		}
		catch (SQLException exc) {
		}
		finally {
			if (lStatement != null)
				lStatement.close();
		}
	}
	
	private int countAutoKeys(Collection<Long> inKeys) {
		int outNumber = 0;
		for (Long lKey : inKeys) {
			System.out.println(lKey.toString());
			outNumber++;
		}
		return outNumber;
	}
}
