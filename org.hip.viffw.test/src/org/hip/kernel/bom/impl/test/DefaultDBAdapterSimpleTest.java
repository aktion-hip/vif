package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hip.kernel.bom.DBAdapterSimple;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.DBAdapterType;
import org.hip.kernel.bom.impl.DefaultDBAdapterSimple;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.bom.impl.GroupByObjectImpl;
import org.hip.kernel.bom.impl.HavingObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.LimitObjectImpl;
import org.hip.kernel.bom.impl.ModifierStrategy;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 15.10.2006
 */
public class DefaultDBAdapterSimpleTest {
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
	public void testCreateInsertString() {
		String lExpected = "INSERT INTO tblTest( FDOUBLE, SFIRSTNAME, FAMOUNT, DTMUTATION, BSEX, SNAME ) VALUES (1.2345678899999998900938180668163113296031951904296875, 'Riese', 33, TIMESTAMP('2002-02-01 10:00:00'), 1, 'Adam' )";
		try {
			DomainObject lObject = data.getSimpleHome().create();
			lObject.set(Test2DomainObjectHomeImpl.KEY_NAME, "Adam");
			lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Riese");
			lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
			lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(33));
			lObject.set(Test2DomainObjectHomeImpl.KEY_DOUBLE, new BigDecimal(1.23456789));
			
			Calendar lCalender = GregorianCalendar.getInstance();
			lCalender.set(2002, 1, 1, 10, 0, 0);
			lCalender.getTime();			
			lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
			
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createInsertString("tblTest", lObject);
			assertEquals("insert 1", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreateDeleteString() {
		String lExpected1 = "DELETE FROM tblTest WHERE tblTest.TESTID = 12";
		String lExpected2 = "DELETE FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			lObject.set("TestID", new BigDecimal(12));
			
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createDeleteString("tblTest", lObject);
			assertEquals("delete 1", lExpected1, lSQL);
			
			lSQL = lAdapter.createDeleteString(createKey(), lHome);
			assertEquals("delete 2", lExpected2, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreateUpdateString() throws SQLException, VException {
		String lExpected = "UPDATE tblTest SET SFIRSTNAME = 'Nova', FAMOUNT = 33, BSEX = 1, DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') WHERE tblTest.TESTID = ";

		Long lID = data.createTestEntry("Testing");
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lID);
		DomainObject lObject = data.getSimpleHome().findByKey(lKey);
		
		String lTestID = ((BigDecimal)lObject.get("TestID")).toString();
		
		lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Nova");
		lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
		lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(33));
		
		Calendar lCalender = GregorianCalendar.getInstance();
		lCalender.set(2002, 1, 1, 10, 0, 0);
		lCalender.getTime();			
		lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
		
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		String lSQL = lAdapter.createUpdateString("tblTest", lObject);
		assertEquals("update", lExpected + lTestID, lSQL);
	}
	
	@Test
	public void testCreateUpdateString2() throws VException {
		String lExpectedIns = "INSERT INTO tblTestShort( TESTID, SHORTID ) VALUES (77, 5 )";
		String lExpectedUpd = "UPDATE tblTestShort SET SHORTID = 6 WHERE tblTestShort.TESTID = 77 AND tblTestShort.SHORTID = 5";
		
		DomainObject lObject = new TestShort();
		lObject.set(TestShortHome.KEY_SHORTID, new Long(5));
		lObject.set(TestShortHome.KEY_TESTID, new Long(77));
		((DomainObjectImpl)lObject).reinitialize();
		
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		assertEquals("insert", lExpectedIns, lAdapter.createInsertString("tblTestShort", lObject));
		
		//Note: ShortID is in the key. We set ShortID=6 for entry with ShortID=5 -> an update of an entry retrieved by findByKey will work. 
		lObject.set(TestShortHome.KEY_SHORTID, new Long(6));
		assertEquals("update", lExpectedUpd, lAdapter.createUpdateString("tblTestShort", lObject));

		//Note: Here we set ShortID=66 again for entry with ShortID=5 -> this will not work because the entry has ShortID=6 now.
		//      However, this shouldn't be a problem as we will have a findByKey before we update (and not several updates immediately after another).
		lObject.set(TestShortHome.KEY_SHORTID, new Long(66));
		lExpectedUpd = "UPDATE tblTestShort SET SHORTID = 66 WHERE tblTestShort.TESTID = 77 AND tblTestShort.SHORTID = 5";
		assertEquals("update 2", lExpectedUpd, lAdapter.createUpdateString("tblTestShort", lObject));
	}
	
	@Test
	public void testCreateUpdateString3() throws SQLException, VException {
		String lExpected = "UPDATE tblTest SET tblTest.SSTREET = 'New 99', tblTest.SCITY = 'City 31' WHERE tblTest.TESTID = 23 AND tblTest.SNAME = 'Eva'";
		
		DomainObjectHome lHome = data.getSimpleHome();
		DomainObject lObject = lHome.create();
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		
		KeyObject lValues = new KeyObjectImpl();
		lValues.setValue(Test2DomainObjectHomeImpl.KEY_STREET, "New 99");
		lValues.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City 31");
		
		KeyObject lWhere = new KeyObjectImpl();
		lWhere.setValue(Test2DomainObjectHomeImpl.KEY_ID, new Long(23));
		lWhere.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Eva");
		
		String lSQL = lAdapter.createUpdateString(lHome, lValues, lWhere);
		assertEquals("prepared update with keys", lExpected, lSQL);
	}
	
	@Test
	public void testCreatePreparedUpdateString() throws SQLException, VException {
		String lExpected = "UPDATE tblTest SET SFIRSTNAME = ?, FAMOUNT = ?, BSEX = ?, DTMUTATION = ? WHERE tblTest.TESTID = ?";

		Long lID = data.createTestEntry("Testing");
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lID);
		DomainObject lObject = data.getSimpleHome().findByKey(lKey);
		
		lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Seconda");
		lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
		lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24));
		
		Calendar lCalender = GregorianCalendar.getInstance();
		lCalender.set(2002, 1, 1, 10, 0, 0);
		lCalender.getTime();			
		lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
		
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		String lSQL = lAdapter.createPreparedUpdateString("tblTest", lObject);
		assertEquals("prepared", lExpected, lSQL);
	}
	
	@Test
	public void testCreateCountAllString() {
		String lExpected = "SELECT COUNT(tblTest.TESTID) FROM tblTest";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createCountAllString(lHome);
			assertEquals("count all", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreateCountString() throws Exception {
		String lExpected1 = "SELECT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
		String lExpected2 = "SELECT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') GROUP BY tblTest.FAMOUNT";
		String lExpected3 = "SELECT DISTINCT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";

		DomainObjectHome lHome = data.getSimpleHome();
		DomainObject lObject = lHome.create();
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		
		String lSQL = lAdapter.createCountString(createKey(), lHome);
		assertEquals("count 1", lExpected1, lSQL);
		
		GroupByObject lGroupBy = new GroupByObjectImpl();
		lGroupBy.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, false, 0);
		lSQL = lAdapter.createCountString(createKey(), new HavingObjectImpl(), lGroupBy, lHome);
		assertEquals("count 2", lExpected2, lSQL);
		
		KeyObject lKey = createKey();
		lKey.setDistinct(true);
		assertEquals("count distinct", lExpected3, lAdapter.createCountString(lKey, lHome));
	}
	
	@Test
	public void testCreateMaxAllString() {
		String lExpected = "SELECT MAX(tblTest.FAMOUNT) FROM tblTest";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
						
			String lSQL = lAdapter.createModifiedString(new ModifierStrategy("Amount", ModifierStrategy.MAX), lHome);
			assertEquals("max", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreateMaxString() throws VException {
		String lExpected = "SELECT MAX(tblTest.FAMOUNT) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
		String lExpected2 = "SELECT MAX(tblTest.FAMOUNT), MAX(tblTest.FDOUBLE) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
		DomainObjectHome lHome = data.getSimpleHome();
		DomainObject lObject = lHome.create();
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
					
		String lSQL = lAdapter.createModifiedString(new ModifierStrategy("Amount", ModifierStrategy.MAX), createKey(), lHome);
		assertEquals("max", lExpected, lSQL);
		
		String[] lColumns = {Test2DomainObjectHomeImpl.KEY_AMOUNT, "Double"};
		lSQL = lAdapter.createModifiedString(new ModifierStrategy(lColumns, ModifierStrategy.MAX), createKey(), lHome);
		assertEquals("max 2", lExpected2, lSQL);
	}
	
	@Test
	public void testCreateSelectAllString() {
		String lExpected = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createSelectAllString();
			assertEquals("select all", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreateSelectString() throws Exception {
		String lExpected1 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
		String lExpected2 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') ORDER BY tblTest.SFIRSTNAME, tblTest.FAMOUNT DESC";
		String lExpected3 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest ORDER BY tblTest.SFIRSTNAME, tblTest.FAMOUNT DESC";
		String lExpected4 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') AND UCASE(tblTest.SNAME) LIKE 'A%'";
		String lExpected5 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') HAVING tblTest.FAMOUNT <= 10";
		String lExpected6 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') GROUP BY tblTest.FAMOUNT HAVING tblTest.FAMOUNT <= 10";
		String lExpected7 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest GROUP BY tblTest.FAMOUNT";
		String lExpected8 = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') LIMIT 10 OFFSET 60";
		String lExpected9 = "SELECT DISTINCT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";

		DomainObjectHome lHome = data.getSimpleHome();
		DomainObject lObject = lHome.create();
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		String lSQL = lAdapter.createSelectString(createKey(), lHome);
		assertEquals("select key 1", lExpected1, lSQL);
		
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, false, 1);
		lOrder.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, true, 2);
		lSQL = lAdapter.createSelectString(createKey2(), lOrder, lHome);
		assertEquals("select key 2", lExpected2, lSQL);
		
		lSQL = lAdapter.createSelectString(lOrder, lHome);
		assertEquals("select key 3", lExpected3, lSQL);
		
		if (data.isDBMySQL()) {			
			lSQL = lAdapter.createSelectString(createKey3(), lHome);
			assertEquals("select key 4", lExpected4, lSQL);
		}
		
		HavingObject lHaving = new HavingObjectImpl();
		lHaving.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(10), "<=");
		lSQL = lAdapter.createSelectString(createKey2(), lHaving, lHome);
		assertEquals("select key 5", lExpected5, lSQL);
		
		GroupByObject lGroupBy = new GroupByObjectImpl();
		lGroupBy.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, false, 0);
		lSQL = lAdapter.createSelectString(createKey2(), new OrderObjectImpl(), lHaving, lGroupBy, lHome);
		assertEquals("select key 6", lExpected6, lSQL);
		
		lSQL = lAdapter.createSelectString(new KeyObjectImpl(), new OrderObjectImpl(), new HavingObjectImpl(), lGroupBy, lHome);
		assertEquals("select key 7", lExpected7, lSQL);
		
		LimitObject lLimit = new LimitObjectImpl(10, 60);
		lSQL = lAdapter.createSelectString(createKey(), lLimit, lHome);
		assertEquals("select key 8", lExpected8, lSQL);
		
		KeyObject lKey = createKey();
		lKey.setDistinct(true);
		assertEquals("select distinct", lExpected9, lAdapter.createSelectString(lKey, lHome));
	}
	
	@Test
	public void testCreatePreparedSelectString() {
		String lExpected = "SELECT tblTest.SPLZ, tblTest.SLANGUAGE, tblTest.SFAX, tblTest.STEL, tblTest.SFIRSTNAME, tblTest.SNAME, tblTest.FAMOUNT, tblTest.SSTREET, tblTest.SPASSWORD, tblTest.BSEX, tblTest.SCITY, tblTest.SMAIL, tblTest.FDOUBLE, tblTest.TESTID, tblTest.DTMUTATION FROM tblTest WHERE tblTest.SFIRSTNAME = ? AND tblTest.BSEX = ? AND tblTest.FAMOUNT = ? AND tblTest.DTMUTATION = ?";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createPreparedSelectString(createKey(), lHome);
			assertEquals("select prepared", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreatePreparedUpdate() throws VException {
		String lExpected = "UPDATE tblTest SET tblTest.SSTREET = ?, tblTest.SCITY = ? WHERE tblTest.TESTID = ? AND tblTest.SNAME = ?";
		
		DomainObjectHome lHome = data.getSimpleHome();
		DomainObject lObject = lHome.create();
		DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
		
		KeyObject lValues = new KeyObjectImpl();
		lValues.setValue(Test2DomainObjectHomeImpl.KEY_STREET, "New 99");
		lValues.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City 31");
		
		KeyObject lWhere = new KeyObjectImpl();
		lWhere.setValue(Test2DomainObjectHomeImpl.KEY_ID, new Long(23));
		lWhere.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Eva");
		
		String lSQL = lAdapter.createPreparedUpdate(lHome, lValues, lWhere);
		assertEquals("prepared update with keys", lExpected, lSQL);
	}
	
	@Test
	public void testCreateKeyCountColumnList() {
		String lExpected = "COUNT(tblTest.TESTID)";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = lAdapter.createKeyCountColumnList(lHome);
			assertEquals("column list", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreatePreparedInserts() {
		String lExpected = "INSERT INTO tblTest( FDOUBLE, SFIRSTNAME, SLANGUAGE, TESTID, SFAX, SMAIL, STEL, SSTREET, SPLZ, SPASSWORD, FAMOUNT, SCITY, DTMUTATION, BSEX, SNAME ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = (String)lAdapter.createPreparedInserts().elementAt(0);
			assertEquals("column list", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testCreatePreparedUpdates() {
		String lExpected = "UPDATE tblTest SET FDOUBLE = ?, SFIRSTNAME = ?, SLANGUAGE = ?, SFAX = ?, SMAIL = ?, STEL = ?, SSTREET = ?, SPLZ = ?, SPASSWORD = ?, FAMOUNT = ?, SCITY = ?, DTMUTATION = ?, BSEX = ?, SNAME = ? WHERE TESTID = ?";
		try {
			DomainObjectHome lHome = data.getSimpleHome();
			DomainObject lObject = lHome.create();
			DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
			String lSQL = (String)lAdapter.createPreparedUpdates().elementAt(0);
			assertEquals("column list", lExpected, lSQL);
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
// --- helper methods ---	
	
	private KeyObject createKey() {
		KeyObject outKey = new KeyObjectImpl();
		try {
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Seconda");
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24));
			
			Calendar lCalender = GregorianCalendar.getInstance();
			lCalender.set(2002, 1, 1, 10, 0, 0);
			lCalender.getTime();			
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		return outKey;
	}
	
	private KeyObject createKey2() {
		KeyObject outKey = new KeyObjectImpl();
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Test%", "LIKE");
			lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "test%", "LIKE", KeyObject.BinaryBooleanOperator.OR);
			outKey.setValue(lKey);
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24), ">");
			
			Calendar lCalender = GregorianCalendar.getInstance();
			lCalender.set(2002, 1, 1, 10, 0, 0);
			lCalender.getTime();			
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		return outKey;
	}
	
	private KeyObject createKey3() {
		KeyObject outKey = null;
		try {
			outKey = createKey();
			outKey.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "A%", "LIKE", KeyObject.BinaryBooleanOperator.AND, DBAdapterType.DB_TYPE_MYSQL.getColumnModifierUCase());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		return outKey;
	}

// --- private classes ---
	
	@SuppressWarnings("serial")
	private class TestShort extends DomainObjectImpl {
		private final static String HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestShortHome";
		public String getHomeClassName() {
			return HOME_CLASS_NAME;
		}
	}	
	
}
