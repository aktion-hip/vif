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

/** @author Luthiger Created: 15.10.2006 */
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
        final String lExpected = "INSERT INTO tblTest( SFIRSTNAME, FDOUBLE, BSEX, FAMOUNT, SNAME, DTMUTATION ) VALUES ('Riese', 1.2345678899999998900938180668163113296031951904296875, 1, 33, 'Adam', TIMESTAMP('2002-02-01 10:00:00') )";
        try {
            final DomainObject lObject = data.getSimpleHome().create();
            lObject.set(Test2DomainObjectHomeImpl.KEY_NAME, "Adam");
            lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Riese");
            lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
            lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(33));
            lObject.set(Test2DomainObjectHomeImpl.KEY_DOUBLE, new BigDecimal(1.23456789));

            final Calendar lCalender = GregorianCalendar.getInstance();
            lCalender.set(2002, 1, 1, 10, 0, 0);
            lCalender.getTime();
            lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));

            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            final String lSQL = lAdapter.createInsertString("tblTest", lObject);
            assertEquals("insert 1", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateDeleteString() {
        final String lExpected1 = "DELETE FROM tblTest WHERE tblTest.TESTID = 12";
        final String lExpected2 = "DELETE FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            lObject.set("TestID", new BigDecimal(12));

            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            String lSQL = lAdapter.createDeleteString("tblTest", lObject);
            assertEquals("delete 1", lExpected1, lSQL);

            lSQL = lAdapter.createDeleteString(createKey(), lHome);
            assertEquals("delete 2", lExpected2, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateUpdateString() throws SQLException, VException {
        final String lExpected = "UPDATE tblTest SET FAMOUNT = 33, SFIRSTNAME = 'Nova', DTMUTATION = TIMESTAMP('2002-02-01 10:00:00'), BSEX = 1 WHERE tblTest.TESTID = ";

        final Long lID = data.createTestEntry("Testing");
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lID);
        final DomainObject lObject = data.getSimpleHome().findByKey(lKey);

        final String lTestID = ((BigDecimal) lObject.get("TestID")).toString();

        lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Nova");
        lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
        lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(33));

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));

        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
        final String lSQL = lAdapter.createUpdateString("tblTest", lObject);
        assertEquals("update", lExpected + lTestID, lSQL);
    }

    @Test
    public void testCreateUpdateString2() throws VException {
        final String lExpectedIns = "INSERT INTO tblTestShort( SHORTID, TESTID ) VALUES (5, 77 )";
        String lExpectedUpd = "UPDATE tblTestShort SET SHORTID = 6 WHERE tblTestShort.TESTID = 77 AND tblTestShort.SHORTID = 5";

        final DomainObject lObject = new TestShort();
        lObject.set(TestShortHome.KEY_SHORTID, new Long(5));
        lObject.set(TestShortHome.KEY_TESTID, new Long(77));
        ((DomainObjectImpl) lObject).reinitialize();

        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
        assertEquals("insert", lExpectedIns, lAdapter.createInsertString("tblTestShort", lObject));

        // Note: ShortID is in the key. We set ShortID=6 for entry with ShortID=5 -> an update of an entry retrieved by
        // findByKey will work.
        lObject.set(TestShortHome.KEY_SHORTID, new Long(6));
        assertEquals("update", lExpectedUpd, lAdapter.createUpdateString("tblTestShort", lObject));

        // Note: Here we set ShortID=66 again for entry with ShortID=5 -> this will not work because the entry has
        // ShortID=6 now.
        // However, this shouldn't be a problem as we will have a findByKey before we update (and not several updates
        // immediately after another).
        lObject.set(TestShortHome.KEY_SHORTID, new Long(66));
        lExpectedUpd = "UPDATE tblTestShort SET SHORTID = 66 WHERE tblTestShort.TESTID = 77 AND tblTestShort.SHORTID = 5";
        assertEquals("update 2", lExpectedUpd, lAdapter.createUpdateString("tblTestShort", lObject));
    }

    @Test
    public void testCreateUpdateString3() throws SQLException, VException {
        final String lExpected = "UPDATE tblTest SET tblTest.SSTREET = 'New 99', tblTest.SCITY = 'City 31' WHERE tblTest.TESTID = 23 AND tblTest.SNAME = 'Eva'";

        final DomainObjectHome lHome = data.getSimpleHome();
        final DomainObject lObject = lHome.create();
        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());

        final KeyObject lValues = new KeyObjectImpl();
        lValues.setValue(Test2DomainObjectHomeImpl.KEY_STREET, "New 99");
        lValues.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City 31");

        final KeyObject lWhere = new KeyObjectImpl();
        lWhere.setValue(Test2DomainObjectHomeImpl.KEY_ID, new Long(23));
        lWhere.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Eva");

        final String lSQL = lAdapter.createUpdateString(lHome, lValues, lWhere);
        assertEquals("prepared update with keys", lExpected, lSQL);
    }

    @Test
    public void testCreatePreparedUpdateString() throws SQLException, VException {
        final String lExpected = "UPDATE tblTest SET FAMOUNT = ?, SFIRSTNAME = ?, DTMUTATION = ?, BSEX = ? WHERE tblTest.TESTID = ?";

        final Long lID = data.createTestEntry("Testing");
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lID);
        final DomainObject lObject = data.getSimpleHome().findByKey(lKey);

        lObject.set(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Seconda");
        lObject.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
        lObject.set(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24));

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        lObject.set(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));

        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
        final String lSQL = lAdapter.createPreparedUpdateString("tblTest", lObject);
        assertEquals("prepared", lExpected, lSQL);
    }

    @Test
    public void testCreateCountAllString() {
        final String lExpected = "SELECT COUNT(tblTest.TESTID) FROM tblTest";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            final String lSQL = lAdapter.createCountAllString(lHome);
            assertEquals("count all", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateCountString() throws Exception {
        final String lExpected1 = "SELECT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected2 = "SELECT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') GROUP BY tblTest.FAMOUNT";
        final String lExpected3 = "SELECT DISTINCT COUNT(tblTest.TESTID) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";

        final DomainObjectHome lHome = data.getSimpleHome();
        final DomainObject lObject = lHome.create();
        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());

        String lSQL = lAdapter.createCountString(createKey(), lHome);
        assertEquals("count 1", lExpected1, lSQL);

        final GroupByObject lGroupBy = new GroupByObjectImpl();
        lGroupBy.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, false, 0);
        lSQL = lAdapter.createCountString(createKey(), new HavingObjectImpl(), lGroupBy, lHome);
        assertEquals("count 2", lExpected2, lSQL);

        final KeyObject lKey = createKey();
        lKey.setDistinct(true);
        assertEquals("count distinct", lExpected3, lAdapter.createCountString(lKey, lHome));
    }

    @Test
    public void testCreateMaxAllString() {
        final String lExpected = "SELECT MAX(tblTest.FAMOUNT) FROM tblTest";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());

            final String lSQL = lAdapter.createModifiedString(new ModifierStrategy("Amount", ModifierStrategy.MAX),
                    lHome);
            assertEquals("max", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateMaxString() throws VException {
        final String lExpected = "SELECT MAX(tblTest.FAMOUNT) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected2 = "SELECT MAX(tblTest.FAMOUNT), MAX(tblTest.FDOUBLE) FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final DomainObjectHome lHome = data.getSimpleHome();
        final DomainObject lObject = lHome.create();
        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());

        String lSQL = lAdapter.createModifiedString(new ModifierStrategy("Amount", ModifierStrategy.MAX), createKey(),
                lHome);
        assertEquals("max", lExpected, lSQL);

        final String[] lColumns = { Test2DomainObjectHomeImpl.KEY_AMOUNT, "Double" };
        lSQL = lAdapter.createModifiedString(new ModifierStrategy(lColumns, ModifierStrategy.MAX), createKey(), lHome);
        assertEquals("max 2", lExpected2, lSQL);
    }

    @Test
    public void testCreateSelectAllString() {
        final String lExpected = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            final String lSQL = lAdapter.createSelectAllString();
            assertEquals("select all", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateSelectString() throws Exception {
        final String lExpected1 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected2 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') ORDER BY tblTest.SFIRSTNAME, tblTest.FAMOUNT DESC";
        final String lExpected3 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest ORDER BY tblTest.SFIRSTNAME, tblTest.FAMOUNT DESC";
        final String lExpected4 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') AND UCASE(tblTest.SNAME) LIKE 'A%'";
        final String lExpected5 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') HAVING tblTest.FAMOUNT <= 10";
        final String lExpected6 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE (tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') GROUP BY tblTest.FAMOUNT HAVING tblTest.FAMOUNT <= 10";
        final String lExpected7 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest GROUP BY tblTest.FAMOUNT";
        final String lExpected8 = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') LIMIT 10 OFFSET 60";
        final String lExpected9 = "SELECT DISTINCT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";

        final DomainObjectHome lHome = data.getSimpleHome();
        final DomainObject lObject = lHome.create();
        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
        String lSQL = lAdapter.createSelectString(createKey(), lHome);
        assertEquals("select key 1", lExpected1, lSQL);

        final OrderObject lOrder = new OrderObjectImpl();
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

        final HavingObject lHaving = new HavingObjectImpl();
        lHaving.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(10), "<=");
        lSQL = lAdapter.createSelectString(createKey2(), lHaving, lHome);
        assertEquals("select key 5", lExpected5, lSQL);

        final GroupByObject lGroupBy = new GroupByObjectImpl();
        lGroupBy.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, false, 0);
        lSQL = lAdapter.createSelectString(createKey2(), new OrderObjectImpl(), lHaving, lGroupBy, lHome);
        assertEquals("select key 6", lExpected6, lSQL);

        lSQL = lAdapter.createSelectString(new KeyObjectImpl(), new OrderObjectImpl(), new HavingObjectImpl(),
                lGroupBy, lHome);
        assertEquals("select key 7", lExpected7, lSQL);

        final LimitObject lLimit = new LimitObjectImpl(10, 60);
        lSQL = lAdapter.createSelectString(createKey(), lLimit, lHome);
        assertEquals("select key 8", lExpected8, lSQL);

        final KeyObject lKey = createKey();
        lKey.setDistinct(true);
        assertEquals("select distinct", lExpected9, lAdapter.createSelectString(lKey, lHome));
    }

    @Test
    public void testCreatePreparedSelectString() {
        final String lExpected = "SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SFIRSTNAME = ? AND tblTest.BSEX = ? AND tblTest.FAMOUNT = ? AND tblTest.DTMUTATION = ?";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            final String lSQL = lAdapter.createPreparedSelectString(createKey(), lHome);
            assertEquals("select prepared", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreatePreparedUpdate() throws VException {
        final String lExpected = "UPDATE tblTest SET tblTest.SSTREET = ?, tblTest.SCITY = ? WHERE tblTest.TESTID = ? AND tblTest.SNAME = ?";

        final DomainObjectHome lHome = data.getSimpleHome();
        final DomainObject lObject = lHome.create();
        final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());

        final KeyObject lValues = new KeyObjectImpl();
        lValues.setValue(Test2DomainObjectHomeImpl.KEY_STREET, "New 99");
        lValues.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City 31");

        final KeyObject lWhere = new KeyObjectImpl();
        lWhere.setValue(Test2DomainObjectHomeImpl.KEY_ID, new Long(23));
        lWhere.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Eva");

        final String lSQL = lAdapter.createPreparedUpdate(lHome, lValues, lWhere);
        assertEquals("prepared update with keys", lExpected, lSQL);
    }

    @Test
    public void testCreateKeyCountColumnList() {
        final String lExpected = "COUNT(tblTest.TESTID)";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            final String lSQL = lAdapter.createKeyCountColumnList(lHome);
            assertEquals("column list", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreatePreparedInserts() {
        final String lExpected = "INSERT INTO tblTest( SFIRSTNAME, SSTREET, SFAX, SMAIL, SPLZ, STEL, FDOUBLE, SPASSWORD, TESTID, BSEX, FAMOUNT, SLANGUAGE, SCITY, SNAME, DTMUTATION ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            // String lSQL = (String)lAdapter.createPreparedInserts().elementAt(0);
            final String lSQL = lAdapter.createPreparedInserts2().get(0);
            assertEquals("column list", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreatePreparedUpdates() {
        final String lExpected = "UPDATE tblTest SET SFIRSTNAME = ?, SSTREET = ?, SFAX = ?, SMAIL = ?, SPLZ = ?, STEL = ?, FDOUBLE = ?, SPASSWORD = ?, BSEX = ?, FAMOUNT = ?, SLANGUAGE = ?, SCITY = ?, SNAME = ?, DTMUTATION = ? WHERE TESTID = ?";
        try {
            final DomainObjectHome lHome = data.getSimpleHome();
            final DomainObject lObject = lHome.create();
            final DBAdapterSimple lAdapter = new DefaultDBAdapterSimple(lObject.getObjectDef());
            // final String lSQL = lAdapter.createPreparedUpdates().elementAt(0);
            final String lSQL = lAdapter.createPreparedUpdates2().get(0);
            assertEquals("column list", lExpected, lSQL);
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    // --- helper methods ---

    private KeyObject createKey() {
        final KeyObject outKey = new KeyObjectImpl();
        try {
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Seconda");
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24));

            final Calendar lCalender = GregorianCalendar.getInstance();
            lCalender.set(2002, 1, 1, 10, 0, 0);
            lCalender.getTime();
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
        return outKey;
    }

    private KeyObject createKey2() {
        final KeyObject outKey = new KeyObjectImpl();
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Test%", "LIKE");
            lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "test%", "LIKE", KeyObject.BinaryBooleanOperator.OR);
            outKey.setValue(lKey);
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24), ">");

            final Calendar lCalender = GregorianCalendar.getInstance();
            lCalender.set(2002, 1, 1, 10, 0, 0);
            lCalender.getTime();
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
        return outKey;
    }

    private KeyObject createKey3() {
        KeyObject outKey = null;
        try {
            outKey = createKey();
            outKey.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "A%", "LIKE", KeyObject.BinaryBooleanOperator.AND,
                    DBAdapterType.DB_TYPE_MYSQL.getColumnModifierUCase());
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
        return outKey;
    }

    // --- private classes ---

    @SuppressWarnings("serial")
    private class TestShort extends DomainObjectImpl {
        private final static String HOME_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestShortHome";

        @Override
        public String getHomeClassName() {
            return HOME_CLASS_NAME;
        }
    }

}
