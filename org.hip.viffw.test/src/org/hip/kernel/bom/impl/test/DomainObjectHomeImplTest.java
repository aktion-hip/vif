package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.ModifierStrategy;
import org.hip.kernel.bom.impl.SetOperatorHomeImpl;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author: Benno Luthiger */
public class DomainObjectHomeImplTest {
    private final static String KEY_NAME = "Name";
    private static TestDomainObjectHomeImpl home;
    private static DataHouseKeeper data;

    private class UnionHomeSub extends SetOperatorHomeImpl {
        private UnionHomeSub() {
            super(SetOperatorHome.UNION);
        }

        public String getSQL() {
            return createSelect();
        }
    }

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
        home = (TestDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestDomainObjectHomeImpl");
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromSimple();
    }

    private String createColumnList(final ObjectDef inObjectDef) {
        String outColumnList = "";

        outColumnList = "";
        try {
            // We iterate over all mapping entries
            boolean lFirst = true;
            for (final PropertyDef lProperty : inObjectDef.getPropertyDefs2()) {
                final MappingDef lMapping = lProperty.getMappingDef();

                if (!lFirst) {
                    outColumnList += ", ";
                }
                lFirst = false;

                final String lTableName = lMapping.getTableName();
                final String lColumnName = lMapping.getColumnName();

                outColumnList += lTableName + "." + lColumnName;
            }
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
        return outColumnList;
    }

    @Test
    public void testCreate() {
        try {
            int lCount = data.getSimpleHome().getCount();
            assertEquals("number before insert", 0, lCount);

            final String lName = "Dummy";
            final String lFirstname = "Fi";

            // test create / insert
            data.createTestEntry(lName, lFirstname);
            lCount = data.getSimpleHome().getCount();
            assertEquals("number after insert", 1, lCount);

            // test find / delete
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(KEY_NAME, lName);
            lKey.setValue("Firstname", lFirstname);

            DomainObject lFound = data.getSimpleHome().findByKey(lKey);
            final XMLSerializer lXML = new XMLSerializer();
            lFound.accept(lXML);
            VSys.out.print(lXML.toString());
            assertTrue("found domain object", lXML.toString().length() > 400);
            lFound.delete();

            lCount = data.getSimpleHome().getCount();
            assertEquals("number after delete", 0, lCount);

            // insert with double amount
            final BigDecimal lAmount = new BigDecimal(17.556);
            final DomainObject lNew = data.getSimpleHome().create();
            lNew.set(KEY_NAME, lName);
            lNew.set("Firstname", lFirstname);
            lNew.set("Mail", "dummy1@aktion-hip.ch");
            lNew.set("Sex", new Integer(1));
            lNew.set("Amount", lAmount);
            lNew.insert(true);

            lFound = data.getSimpleHome().findByKey(lKey);
            final BigDecimal lReturned = (BigDecimal) lFound.get("Amount");
            assertEquals("Amount", lAmount.setScale(2, 2), lReturned);
            lFound.delete();
        } catch (final org.hip.kernel.exc.VException exc) {
            fail("testCreate f1 " + exc.getMessage());
        } catch (final java.sql.SQLException exc) {
            fail("testCreate f2 " + exc.getMessage());
        }
    }

    @Test
    public void testStrings() throws VException, SQLException {
        final DomainObjectHome lHome = data.getSimpleHome();
        assertEquals("count 1", 0, lHome.getCount());
        final DomainObject lModel = lHome.create();

        String lText = "String with apostroph: Here\'s one (1).";
        lModel.set(Test2DomainObjectHomeImpl.KEY_NAME, lText);
        lModel.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
        final Long lID = lModel.insert(true);
        assertEquals("count 2", 1, lHome.getCount());

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_ID, lID);
        DomainObject lRetrieved = lHome.findByKey(lKey);
        assertEquals("text 1", "String with apostroph: Here's one (1).",
                lRetrieved.get(Test2DomainObjectHomeImpl.KEY_NAME).toString());

        lText = "String with apostroph: Here's one (2).";
        lRetrieved.set(Test2DomainObjectHomeImpl.KEY_NAME, lText);
        lRetrieved.update(true);
        lRetrieved = lHome.findByKey(lKey);
        assertEquals("text 2", lText, lRetrieved.get(Test2DomainObjectHomeImpl.KEY_NAME).toString());

        lText = "This is a quote: \"Hello World\"";
        lRetrieved.set(Test2DomainObjectHomeImpl.KEY_NAME, lText);
        lRetrieved.update(true);
        lRetrieved = lHome.findByKey(lKey);
        assertEquals("text 3", lText, lRetrieved.get(Test2DomainObjectHomeImpl.KEY_NAME).toString());
    }

    @Test
    public void testEquals() {
        try {
            final String lName = "Dummy";
            final String lFirstname = "1";
            final String lName2 = "Dummy";
            final String lFirstname2 = "Eva";

            // test create / insert
            final DomainObject lNew = data.getSimpleHome().create();
            lNew.set(KEY_NAME, lName);
            lNew.set("Firstname", lFirstname);
            lNew.set("Mail", "dummy1@aktion-hip.ch");
            lNew.set("Sex", new Integer(1));

            final DomainObject lNew2 = data.getSimpleHome().create();
            lNew2.set(KEY_NAME, lName2);
            lNew2.set("Firstname", lFirstname2);
            lNew2.set("Mail", "dummy2@aktion-hip.ch");
            lNew2.set("Sex", new Integer(2));

            assertTrue("equals 1", lNew2.equals(lNew));
            assertEquals("hash code 1", lNew2.hashCode(), lNew.hashCode());

            lNew.insert();
            lNew2.insert();

            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(KEY_NAME, lName);
            lKey.setValue("Firstname", lFirstname);
            final DomainObject lFound = data.getSimpleHome().findByKey(lKey);

            final KeyObject lKey2 = new KeyObjectImpl();
            lKey2.setValue(KEY_NAME, lName2);
            lKey2.setValue("Firstname", lFirstname2);
            final DomainObject lFound2 = data.getSimpleHome().findByKey(lKey2);

            if (lFound.equals(lFound2)) {
                fail("Found domain objects arn't equal anymore!");
            }

            if (lFound.hashCode() == lFound2.hashCode()) {
                fail("Found domain objects must return different hashCode!");
            }

            lFound.delete();
            lFound2.delete();
        } catch (final org.hip.kernel.exc.VException exc) {
            fail("testCreate f1 " + exc.getMessage());
        } catch (final java.sql.SQLException exc) {
            fail("testCreate f2 " + exc.getMessage());
        }
    }

    @Test
    public void testGetColumnNameFor() {
        assertEquals("getColumnNameFor 1", "tblTestMember.TESTMEMBERID", home.getColumnNameFor("MemberID"));
        assertEquals("getColumnNameFor 2", "tblTestMember.DTMUTATION", home.getColumnNameFor("Mutation"));
        assertEquals("getColumnNameFor 3", "", home.getColumnNameFor("Dummy"));
    }

    @Test
    public void testGetObjectDef() {
        final ObjectDef lDef = home.getObjectDef();
        try {
            lDef.get("keyDefs");
            lDef.get("propertyDefs");
            assertEquals("testGetObjectDef 1", "org.hip.kernel.bom.DomainObject", lDef.get("parent"));
            assertEquals("testGetObjectDef 2", "TestDomainObject", lDef.get("objectName"));
        } catch (final org.hip.kernel.bom.GettingException exc) {
            fail("testGetObjectDef.get()");
        }
    }

    @Test
    public void testGetPropertyDef() {
        final String[] lExpected = { "propertyType", "valueType", "propertyName", "mappingDef", "formatPattern",
                "relationshipDef" };
        final Vector<String> lContainsExpected = new Vector<String>(Arrays.asList(lExpected));

        final PropertyDef lDef = home.getPropertyDef("MemberID");
        assertEquals("getPropertyType", "simple", lDef.getPropertyType());
        assertEquals("getValueType", "Number", lDef.getValueType());
        int i = 0;
        for (final String lName : lDef.getPropertyNames2()) {
            assertTrue("getPropertyNames " + i++, lContainsExpected.contains(lName));
        }
        assertEquals("getMappingDef", "tblTestMember.TESTMEMBERID", lDef.getMappingDef().getTableName() + "."
                + lDef.getMappingDef().getColumnName());
        assertNull("getRelationshipDef", lDef.getRelationshipDef());
    }

    @Test
    public void testGetPropertyDefFor() {
        final String[] lExpected = { "propertyType", "valueType", "propertyName", "mappingDef", "formatPattern",
                "relationshipDef" };
        final Vector<String> lContainsExpected = new Vector<String>(Arrays.asList(lExpected));

        final PropertyDef lDef = home.getPropertyDefFor("DTMUTATION");
        assertEquals("getPropertyType", "simple", lDef.getPropertyType());
        assertEquals("getValueType", "Timestamp", lDef.getValueType());
        assertEquals("getName", "Mutation", lDef.getName());
        int i = 0;
        for (final String lName : lDef.getPropertyNames2()) {
            assertTrue("getPropertyNames " + i++, lContainsExpected.contains(lName));
        }
        assertEquals("getMappingDef", "tblTestMember.DTMUTATION", lDef.getMappingDef().getTableName() + "."
                + lDef.getMappingDef().getColumnName());
        assertNull("getRelationshipDef", lDef.getRelationshipDef());
    }

    @Test
    public void testObjects() {
        final String[] lExpectedCols = { "tblTestMember.TESTMEMBERID", "tblTestMember.SNAME",
                "tblTestMember.SPASSWORD", "tblTestMember.SFIRSTNAME", "tblTestMember.DTMUTATION" };
        final Vector<String> lVExpectedCols = new Vector<String>(Arrays.asList(lExpectedCols));

        // ordering in ColumnList may be OS-dependent, so get the actual ColumnList
        final String lColumnList = createColumnList(home.getObjectDef());

        // check whether the actual ColumnList contains the expected items
        final StringTokenizer lStringTokenizer = new StringTokenizer(lColumnList, ",");
        int i = 0;
        while (lStringTokenizer.hasMoreTokens()) {
            i++;
            assertTrue("ColumnList " + i, lVExpectedCols.contains(lStringTokenizer.nextToken().trim()));
        }
        assertEquals("ColumnList size", lVExpectedCols.size(), i);

        // check the other test objects with the help of the actual ColumnList
        final String[] lExpected = new String[6];
        lExpected[0] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember";
        lExpected[1] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember WHERE tblTestMember.SNAME = 'Luthiger' AND tblTestMember.TESTMEMBERID = 12 AND tblTestMember.DTMUTATION = TIMESTAMP('2001-12-24 00:00:00')";
        lExpected[2] = "COUNT(tblTestMember.TESTMEMBERID)";
        lExpected[3] = "SELECT " + lColumnList
                + " FROM tblTestMember WHERE tblTestMember.SNAME = ? AND tblTestMember.TESTMEMBERID = ?";
        lExpected[4] = "SELECT " + lColumnList + " FROM tblTestMember";
        lExpected[5] = "SELECT "
                + lColumnList
                + " FROM tblTestMember WHERE tblTestMember.SNAME = 'Luthiger' AND tblTestMember.TESTMEMBERID = 12 AND tblTestMember.DTMUTATION = TIMESTAMP('2001-12-24 00:00:00')";

        Iterator<Object> lTestObjects = home.getTestObjects();
        assertNotNull("TestObjects not null", lTestObjects);

        i = 0;
        for (lTestObjects = home.getTestObjects(); lTestObjects.hasNext();) {
            assertEquals("testObjects " + i, lExpected[i], lTestObjects.next());
            i++;
        }
    }

    @Test
    public void testSetCheck() {

        final String lName = "Dummy";
        final String lFirstname = "Fi";
        DomainObject lNew = null;

        try {
            lNew = data.getSimpleHome().create();
        } catch (final org.hip.kernel.bom.BOMException exc) {
            fail("testSetCheck create " + exc.getMessage());
        }

        try {
            lNew.set(KEY_NAME, lName);
            lNew.set("Firstname", lFirstname);
            lNew.set("Double", new Float(13.11));
        } catch (final org.hip.kernel.bom.SettingException exc) {
            fail("testSetCheck set " + exc.getMessage());
        }

        try {
            lNew.set("Double", "13.11");
            fail("testSetCheck set with false type");
        } catch (final SettingException exc) {
        }
    }

    @Test
    public void testDelete() throws Exception {
        final String lExpectedName1 = "Test_Name_1";
        final String lExpectedName2 = "Test_Name_2";

        data.createTestEntry(lExpectedName1);
        data.createTestEntry(lExpectedName2);

        final DomainObjectHome lHome = data.getSimpleHome();
        assertEquals("Number after insert", 2, lHome.getCount());

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_NAME, lExpectedName1);

        lHome.delete(lKey, true);
        assertEquals("Number after delete", 1, lHome.getCount());

        assertEquals("Remaining entry", lExpectedName2, lHome.select().next().get(KEY_NAME));
    }

    @Test
    public void testMax() throws VException, SQLException {
        final double[] lExpected = { 12.45, 112.331967 };

        final DomainObjectHome lHome = data.getSimpleHome();
        assertEquals("number before insert", 0, lHome.getCount());

        DomainObject lNew = lHome.create();
        lNew.set("Name", "Test1");
        lNew.set("Firstname", "1");
        lNew.set("Mail", "dummy1@aktion-hip.ch");
        lNew.set("Sex", new Integer(1));
        lNew.set("Amount", new Float(lExpected[0]));
        lNew.set("Double", new Float(13.11));
        lNew.insert(true);
        lNew.release();

        lNew = lHome.create();
        lNew.set("Name", "Test2");
        lNew.set("Firstname", "2");
        lNew.set("Mail", "dummy2@aktion-hip.ch");
        lNew.set("Sex", new Integer(1));
        lNew.set("Amount", new Float(10));
        lNew.set("Double", new Float(lExpected[1]));
        lNew.insert(true);
        lNew.release();

        lNew = lHome.create();
        lNew.set("Name", "Test3");
        lNew.set("Firstname", "3");
        lNew.set("Mail", "dummy3@aktion-hip.ch");
        lNew.set("Sex", new Integer(1));
        lNew.set("Amount", new Float(5.4451));
        lNew.set("Double", new Float(89.4));
        lNew.insert(true);
        lNew.release();

        assertEquals("number after insert", 3, lHome.getCount());
        assertEquals("Maximum value insertet 1", lExpected[0], lHome.getMax("Amount").doubleValue(), 0.0001);
        assertEquals("Maximum value insertet 2", lExpected[1], lHome.getMax("Double").doubleValue(), 0.0001);

        final String[] lColumns = { "Amount", "Double" };
        final Collection<Object> lResult = lHome.getModified(new ModifierStrategy(lColumns, ModifierStrategy.MAX));
        final Iterator<Object> lValues = lResult.iterator();
        for (int i = 0; i < lExpected.length; i++) {
            assertEquals("Maximum value retrieved " + i, lExpected[i], ((BigDecimal) lValues.next()).doubleValue(),
                    0.0001);
        }
    }

    @Test
    public void testCreateSelectUnion() throws VException {
        final String lExpectedName = "Test_Name";
        final String lExpected = "(SELECT tblTestMember.SNAME, tblTestMember.SPASSWORD, tblTestMember.SFIRSTNAME, tblTestMember.DTMUTATION, tblTestMember.TESTMEMBERID FROM tblTestMember WHERE tblTestMember.SNAME = 'Test_Name')";
        final UnionHomeSub lUnionHome = new UnionHomeSub();

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_NAME, lExpectedName);

        home.createSelectString(lUnionHome, lKey);
        assertEquals("union select", lExpected, lUnionHome.getSQL());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, VException, SQLException {
        DomainObjectHome lHome = (Test2DomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl");
        assertEquals("count 0", 0, lHome.getCount());
        DomainObject lNew = lHome.create();
        lNew.set(Test2DomainObjectHomeImpl.KEY_NAME, "Test 1");
        lNew.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(1));
        lNew.insert(true);
        assertEquals("count 1", 1, lHome.getCount());

        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lHome);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lHome = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final DomainObjectHome lRetrieved = (DomainObjectHome) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        assertEquals("count 2", 1, lRetrieved.getCount());
        lNew = lRetrieved.create();
        lNew.set(Test2DomainObjectHomeImpl.KEY_NAME, "Test 2");
        lNew.set(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
        lNew.insert(true);
        assertEquals("count 3", 2, lRetrieved.getCount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSerialization2() throws IOException, ClassNotFoundException {
        DomainObjectHome lHome1 = (Test2DomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl");
        DomainObjectHome lHome2 = (Test2DomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl");
        assertTrue("identity 1", lHome1 == lHome2);

        Collection<DomainObjectHome> lHomes = new Vector<DomainObjectHome>(2);
        lHomes.add(lHome1);
        lHomes.add(lHome2);
        assertEquals("size of collection referencing the same home", 2, lHomes.size());

        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lHomes);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lHomes = null;
        lHome1 = null;
        lHome2 = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final Vector<DomainObjectHome> lRetrieved = (Vector<DomainObjectHome>) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        assertEquals("size of collection retrieved", 2, lRetrieved.size());
        assertTrue("identity 2", lRetrieved.elementAt(0) == lRetrieved.elementAt(1));

    }

}
