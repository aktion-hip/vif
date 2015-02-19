package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.JoinedDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author: Benno Luthiger */
public class JoinedDomainObjectHomeImplTest {
    private static TestJoinedDomainObjectHomeImpl home;
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
        home = (TestJoinedDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestJoinedDomainObjectHomeImpl");
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromSimple();
        data.deleteAllFromLink();
        System.out.println("Deleted all entries in tblTest and tblGroupAdmin.");
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
    public void testGetObjectDef() throws VException {
        final ObjectDef lObjectDef = home.getObjectDef();
        assertNotNull("testGetObjectDef", lObjectDef);

        final String[] lExpected = { "version", "keyDefs", "propertyDefs", "parent", "objectName", "baseDir" };
        final Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

        int i = 0;
        for (final String lName : lObjectDef.getPropertyNames2()) {
            assertTrue("testGetObjectDef 1." + i++, lVExpected.contains(lName));
        }

        assertEquals("porperty version", "1.0", lObjectDef.get("version"));
        assertEquals("porperty keyDefs", null, lObjectDef.get("keyDefs"));
        assertEquals("porperty parent", "org.hip.kernel.bom.ReadOnlyDomainObject", lObjectDef.get("parent"));
        assertEquals("porperty objectName", "TestJoin1", lObjectDef.get("objectName"));

        final String[] lExpected2 = { "Name", "MemberID", "Mutation", "FirstName" };
        final Vector<String> lVExpected2 = new Vector<String>(Arrays.asList(lExpected2));
        i = 0;
        for (final PropertyDef lPropertyDef : lObjectDef.getPropertyDefs2()) {
            assertTrue("testGetObjectDef 2." + i++, lVExpected2.contains(lPropertyDef.getName()));
        }
    }

    @Test
    public void testObjects() {
        final String[] lObtained = new String[4];

        final String[] lExpectedCols = { "tblTestMember.TESTMEMBERID", "tblTestMember.SNAME",
                "tblTestMember.SFIRSTNAME", "tblTestMember.DTMUTATION" };
        final Vector<String> lVExpectedCols = new Vector<String>(Arrays.asList(lExpectedCols));

        int i = 0;
        for (final Iterator<Object> lTestObjects = home.getTestObjects(); lTestObjects.hasNext(); i++) {
            lObtained[i] = (String) lTestObjects.next();
        }

        // ordering may be OS-dependent, so get the actual lists
        // check whether the actual ColumnList contains the expected items
        String lColumnList = createColumnList(home.getObjectDef());
        StringTokenizer lStringTokenizer = new StringTokenizer(lColumnList, ",");
        i = 0;
        while (lStringTokenizer.hasMoreTokens()) {
            i++;
            assertTrue("ColumnList " + i, lVExpectedCols.contains(lStringTokenizer.nextToken().trim()));
        }
        assertEquals("ColumnList size", lVExpectedCols.size(), i);

        lColumnList = lObtained[2];
        lColumnList = lColumnList.substring(7, lColumnList.indexOf("FROM") - 1);
        lStringTokenizer = new StringTokenizer(lColumnList, ",");
        i = 0;
        while (lStringTokenizer.hasMoreTokens()) {
            i++;
            assertTrue("ColumnList " + i, lVExpectedCols.contains(lStringTokenizer.nextToken().trim()));
        }
        assertEquals("ColumnList size", lVExpectedCols.size(), i);

        // get the actual list of WHEREs
        String lWhereList = lObtained[1];
        lWhereList = lWhereList.substring(lWhereList.indexOf("WHERE") + 6);

        final String[] lExpected = new String[4];
        if (data.isDBMySQL()) {
            lExpected[0] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember INNER JOIN tblGroupAdmin ON tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID";
            lExpected[1] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember INNER JOIN tblGroupAdmin ON tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID WHERE "
                    + lWhereList;
            lExpected[2] = "SELECT "
                    + lColumnList
                    + " FROM tblTestMember INNER JOIN tblGroupAdmin ON tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID";
            lExpected[3] = "SELECT "
                    + lColumnList
                    + " FROM tblTestMember INNER JOIN tblGroupAdmin ON tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID WHERE "
                    + lWhereList;
        }
        else if (data.isDBOracle()) {
            lExpected[0] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember, tblGroupAdmin WHERE tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID";
            lExpected[1] = "SELECT COUNT(tblTestMember.TESTMEMBERID) FROM tblTestMember, tblGroupAdmin WHERE "
                    + lWhereList;
            lExpected[2] = "SELECT "
                    + lColumnList
                    + " FROM tblTestMember, tblGroupAdmin WHERE tblTestMember.TESTMEMBERID = tblGroupAdmin.MEMBERID";
            lExpected[3] = "SELECT " + lColumnList + " FROM tblTestMember, tblGroupAdmin WHERE " + lWhereList;
        }

        for (i = lObtained.length - 1; i >= 0; i--) {
            assertEquals("testObjects " + i, lExpected[i], lObtained[i]);
        }
    }

    @Test
    public void testSelect() throws SQLException, VException {
        // select all testIDs which are member of the specified group
        final String[][] lNames = { { "first", "1" }, { "second", "2" }, { "third", "3" }, { "forth", "4" } };
        final Long[] lKeys = new Long[4];

        // create 4 test entries
        for (int i = 0; i < lNames.length; i++) {
            lKeys[i] = data.createTestEntry(lNames[i][0], lNames[i][1]);
        }

        data.createTestLinkEntry(lKeys[0].intValue(), 1);
        data.createTestLinkEntry(lKeys[1].intValue(), 1);
        data.createTestLinkEntry(lKeys[2].intValue(), 1);
        data.createTestLinkEntry(lKeys[3].intValue(), 1);
        data.createTestLinkEntry(lKeys[0].intValue(), 2);

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue("GroupID", new Integer(1));

        Test2JoinedDomainObjectHomeImpl lHome = (Test2JoinedDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2JoinedDomainObjectHomeImpl");
        QueryResult lQueryResult = lHome.select(lKey);
        assertEquals("number of selected 1", 4, setBOF(lQueryResult));

        lKey = new KeyObjectImpl();
        lKey.setValue("GroupID", new Integer(2));

        lHome = (Test2JoinedDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2JoinedDomainObjectHomeImpl");
        lQueryResult = lHome.select(lKey);
        assertEquals("number of selected 2", 1, setBOF(lQueryResult));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, SQLException, VException {
        // first, we set up some relevant data in the tables
        final String[][] lNames = { { "first", "1" }, { "second", "2" }, { "third", "3" }, { "forth", "4" } };
        final Long[] lKeys = new Long[4];

        for (int i = 0; i < lNames.length; i++) {
            lKeys[i] = data.createTestEntry(lNames[i][0], lNames[i][1]);
        }

        data.createTestLinkEntry(lKeys[0].intValue(), 1);
        data.createTestLinkEntry(lKeys[1].intValue(), 1);
        data.createTestLinkEntry(lKeys[2].intValue(), 1);
        data.createTestLinkEntry(lKeys[3].intValue(), 1);
        data.createTestLinkEntry(lKeys[0].intValue(), 2);

        Test2JoinedDomainObjectHomeImpl lHome = (Test2JoinedDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.Test2JoinedDomainObjectHomeImpl");

        // second, we test the joins we expect
        final KeyObject lKey1 = new KeyObjectImpl();
        lKey1.setValue("GroupID", new Integer(1));

        final KeyObject lKey2 = new KeyObjectImpl();
        lKey2.setValue("GroupID", new Integer(2));

        QueryResult lResult = lHome.select(lKey1);
        assertEquals("number of joined 1", 4, setBOF(lResult));

        lResult = lHome.select(lKey2);
        assertEquals("number of joined 2", 1, setBOF(lResult));

        // here, we serialize
        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lHome);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lHome = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final JoinedDomainObjectHome lRetrieved = (JoinedDomainObjectHome) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        // at last, we test the behavior of the retrieved home
        lResult = lRetrieved.select(lKey1);
        assertEquals("number of joined 3", 4, setBOF(lResult));

        lResult = lRetrieved.select(lKey2);
        assertEquals("number of joined 4", 1, setBOF(lResult));
    }

    private int setBOF(final QueryResult inResult) throws BOMException, SQLException {
        int i = 0;
        while (inResult.hasMoreElements()) {
            inResult.next();
            i++;
        }
        return i;
    }

}
