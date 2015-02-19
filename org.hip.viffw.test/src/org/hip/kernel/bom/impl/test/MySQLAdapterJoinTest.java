package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DBAdapterJoin;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.GroupByObjectImpl;
import org.hip.kernel.bom.impl.HavingObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.LimitObjectImpl;
import org.hip.kernel.bom.impl.MySQLAdapterJoin;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.bom.model.JoinDefDef;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.impl.JoinedObjectDefGenerator;
import org.hip.kernel.exc.VException;
import org.hip.kernel.test.VSysHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/** MySQLAdapterJoinTest.java
 *
 * Created on 09.09.2002
 *
 * @author Benno Luthiger */
public class MySQLAdapterJoinTest {

    private final static String PLACEHOLDER_NAME = "insert";
    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='TestJoin2' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>																							 \n"
                    +
                    "		<columnDef columnName='TestID' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	  \n"
                    +
                    "		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n"
                    +
                    "		<columnDef columnName='Firstname' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	 \n"
                    +
                    "		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n"
                    +
                    "		<columnDef columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n"
                    +
                    "	</columnDefs>																							\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>														 \n"
                    +
                    "		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>					  \n"
                    +
                    "		<objectPlaceholder name='"
                    + PLACEHOLDER_NAME
                    + "' />					\n"
                    +
                    "		<joinCondition>																						 \n"
                    +
                    "			<columnDef columnName='TestID' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	 \n"
                    +
                    "			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	 \n"
                    +
                    "		</joinCondition>																						\n" +
                    "	</joinDef>																								   \n" +
                    "</joinedObjectDef>";

    @SuppressWarnings("serial")
    private class JoinedDomainObjectHome extends Test2JoinedDomainObjectHomeImpl {
        private JoinedDomainObjectHome() {
            super();
        }

        @Override
        public String getObjectDefString() {
            return super.getObjectDefString();
        }
    }

    @SuppressWarnings("serial")
    private class NestedDomainObjectHome extends TestNestedDomainObjectHomeImpl {
        private NestedDomainObjectHome() {
            super();
        }

        @Override
        public String getObjectDefString() {
            return super.getObjectDefString();
        }
    }

    @BeforeClass
    public static void before() {
        VSysHelper.INSTANCE.copyToRunning();
    }

    @AfterClass
    public static void after() {
        VSysHelper.INSTANCE.deleteRunning();
    }

    @Test
    public void testCreateCountAllString() {
        final String lExpected1 = "SELECT COUNT(tblTest.TESTID) FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lExpected2 = "SELECT COUNT(tblTest.TESTID) FROM tblTest LEFT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lExpected3 = "SELECT COUNT(tblTest.TESTID) FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";

        final JoinedDomainObjectHome lHome = new JoinedDomainObjectHome();
        final String lObjecDefString = lHome.getObjectDefString();
        try {
            JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                    lObjecDefString);
            DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            String lSQL = lAdapter.createCountAllSQL();
            assertEquals("createCountAllSQL equi", lExpected1, lSQL);

            lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
            lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "LEFT_JOIN");
            lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            lSQL = lAdapter.createCountAllSQL();
            assertEquals("createCountAllSQL left", lExpected2, lSQL);

            lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
            lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "RIGHT_JOIN");
            lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            lSQL = lAdapter.createCountAllSQL();
            assertEquals("createCountAllSQL right", lExpected3, lSQL);
        } catch (final SAXException exc) {
            fail(exc.getMessage());
        } catch (final VException exc) {
            fail(exc.getMessage());
        }

        try {
            final JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                    lObjecDefString);
            lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "dummy");
            final DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            lAdapter.createCountAllSQL();
            fail("getSLQ should fail because of false join type");
        } catch (final SAXException exc) {
            fail(exc.getMessage());
        } catch (final SettingException exc) {
            fail(exc.getMessage());
        } catch (final BOMException exc) {
            // left blank intentionally
        }
    }

    @Test
    public void testCreateSelectAllSQL() {
        final String lExpected1 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lExpected2 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest LEFT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lExpected3 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID";

        final JoinedDomainObjectHome lHome = new JoinedDomainObjectHome();
        final String lObjecDefString = lHome.getObjectDefString();
        try {
            JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                    lObjecDefString);
            DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            String lSQL = lAdapter.createSelectAllSQL();
            assertEquals("createSelectAllSQL equi", lExpected1, lSQL);

            lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
            lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "LEFT_JOIN");
            lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            lSQL = lAdapter.createSelectAllSQL();
            assertEquals("createSelectAllSQL left", lExpected2, lSQL);

            lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
            lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "RIGHT_JOIN");
            lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            lSQL = lAdapter.createSelectAllSQL();
            assertEquals("createSelectAllSQL right", lExpected3, lSQL);
        } catch (final SAXException exc) {
            fail(exc.getMessage());
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testReset() {
        final String lExpected1 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN (SELECT a, b FROM c WHERE a = 81) ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lExpected2 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN (SELECT x, y, z FROM c WHERE z = 82) ON tblTest.TESTID = tblGroupAdmin.MEMBERID";
        final String lPart1 = "(SELECT a, b FROM c WHERE a = 81)";
        final String lPart2 = "(SELECT x, y, z FROM c WHERE z = 82)";
        try {
            final JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                    XML_OBJECT_DEF);
            lJoinedObjectDef.getJoinDef().fillPlaceholder(PLACEHOLDER_NAME, lPart1);

            final DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            assertEquals("createSelectAllSQL with placeholder 1", lExpected1, lAdapter.createSelectAllSQL());

            lJoinedObjectDef.getJoinDef().fillPlaceholder(PLACEHOLDER_NAME, lPart2);
            assertEquals("createSelectAllSQL with placeholder 1 again", lExpected1, lAdapter.createSelectAllSQL());

            lAdapter.reset();
            assertEquals("createSelectAllSQL with placeholder 2", lExpected2, lAdapter.createSelectAllSQL());
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testCreateCountSQL() throws Exception {
        final String lExpected1 = "SELECT COUNT(tblTest.TESTID) FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected2 = "SELECT COUNT(tblTest.TESTID) FROM tblTest LEFT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected3 = "SELECT COUNT(tblTest.TESTID) FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";
        final String lExpected4 = "SELECT COUNT(tblTest.TESTID) FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') GROUP BY tblTest.DTMUTATION";
        final String lExpected5 = "SELECT DISTINCT COUNT(tblTest.TESTID) FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00')";

        final JoinedDomainObjectHome lHome = new JoinedDomainObjectHome();
        final String lObjecDefString = lHome.getObjectDefString();

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue("TestID", new BigDecimal(12));
        lKey.setValue("Name", "Nova");

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        lKey.setValue("Mutation", new Timestamp(lCalender.getTimeInMillis()));

        JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                lObjecDefString);
        DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        String lSQL = lAdapter.createCountSQL(lKey, lHome);
        assertEquals("createCountSQL equi", lExpected1, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "LEFT_JOIN");
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createCountSQL(lKey, lHome);
        assertEquals("createCountSQL left", lExpected2, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "RIGHT_JOIN");
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createCountSQL(lKey, lHome);
        assertEquals("createCountSQL right", lExpected3, lSQL);

        lKey.setDistinct(true);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        assertEquals("createCountSQL distinct", lExpected5, lAdapter.createCountSQL(lKey, lHome));
        lKey.setDistinct(false);

        final GroupByObject lGroupBy = new GroupByObjectImpl();
        lGroupBy.setValue("Mutation", false, 0);
        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "RIGHT_JOIN");
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createCountSQL(lKey, new HavingObjectImpl(), lGroupBy, lHome);
        assertEquals("createCountSQL with GROUP BY", lExpected4, lSQL);
    }

    @Test
    public void testCreateSelectSQL() throws Exception {
        final String lExpected1 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') ORDER BY tblTest.SNAME DESC, tblTest.DTMUTATION";
        final String lExpected2 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest LEFT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') ORDER BY tblTest.SNAME DESC, tblTest.DTMUTATION";
        final String lExpected3 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest RIGHT JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID = 12 AND tblTest.SNAME = 'Nova' AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00') ORDER BY tblTest.SNAME DESC, tblTest.DTMUTATION";
        final String lExpected4 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 AND tblTest.SNAME = 'Nova'";
        final String lExpected5 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 OR tblTest.SNAME = 'Nova'";
        final String lExpected6 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 OR tblTest.SNAME = 'Nova' HAVING tblGroupAdmin.GROUPID > 2";
        final String lExpected7 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 OR tblTest.SNAME = 'Nova' GROUP BY tblTest.DTMUTATION HAVING tblGroupAdmin.GROUPID > 2";
        final String lExpected8 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID GROUP BY tblTest.DTMUTATION";
        final String lExpected9 = "SELECT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 OR tblTest.SNAME = 'Nova' LIMIT 10 OFFSET 75";
        final String lExpected10 = "SELECT DISTINCT tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.DTMUTATION, tblGroupAdmin.GROUPID FROM tblTest INNER JOIN tblGroupAdmin ON tblTest.TESTID = tblGroupAdmin.MEMBERID WHERE tblTest.TESTID <= 12 OR tblTest.SNAME = 'Nova'";

        final JoinedDomainObjectHome lHome = new JoinedDomainObjectHome();
        final String lObjecDefString = lHome.getObjectDefString();

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue("TestID", new BigDecimal(12));
        lKey.setValue("Name", "Nova");

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        lKey.setValue("Mutation", new Timestamp(lCalender.getTimeInMillis()));

        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue("Name", true, 1);
        lOrder.setValue("Mutation", false, 2);

        JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                lObjecDefString);
        DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        String lSQL = lAdapter.createSelectSQL(lKey, lOrder, lHome);
        assertEquals("createSelectSQL equi", lExpected1, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "LEFT_JOIN");
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(lKey, lOrder, lHome);
        assertEquals("createSelectSQL left", lExpected2, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lJoinedObjectDef.getJoinDef().set(JoinDefDef.joinType, "RIGHT_JOIN");
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(lKey, lOrder, lHome);
        assertEquals("createSelectSQL right", lExpected3, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lKey = new KeyObjectImpl();
        lKey.setValue("TestID", new BigDecimal(12), "<=");
        lKey.setValue("Name", "Nova");
        lSQL = lAdapter.createSelectSQL(lKey, lHome);
        assertEquals("createSelectSQL with comparison", lExpected4, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lKey = new KeyObjectImpl();
        lKey.setValue("TestID", new BigDecimal(12), "<=");
        lKey.setValue("Name", "Nova", "=", BinaryBooleanOperator.OR);
        lSQL = lAdapter.createSelectSQL(lKey, lHome);
        assertEquals("createSelectSQL with link", lExpected5, lSQL);

        lKey.setDistinct(true);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        assertEquals("createSelectSQL with distinct rows", lExpected10, lAdapter.createSelectSQL(lKey, lHome));
        lKey.setDistinct(false);

        final HavingObject lHaving = new HavingObjectImpl();
        lHaving.setValue("GroupID", new Integer(2), ">");
        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(lKey, new OrderObjectImpl(), lHaving, lHome);
        assertEquals("createSelectSQL with HAVING", lExpected6, lSQL);

        final GroupByObject lGroupBy = new GroupByObjectImpl();
        lGroupBy.setValue("Mutation", false, 0);
        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(lKey, new OrderObjectImpl(), lHaving, lGroupBy, lHome);
        assertEquals("createSelectSQL with GROUP BY", lExpected7, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(new KeyObjectImpl(), new OrderObjectImpl(), new HavingObjectImpl(), lGroupBy,
                lHome);
        assertEquals("createSelectSQL with GROUP BY and empty key", lExpected8, lSQL);

        lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(lObjecDefString);
        lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
        lSQL = lAdapter.createSelectSQL(lKey, new LimitObjectImpl(10, 75), lHome);
        assertEquals("createSelectSQL with LIMIT", lExpected9, lSQL);
    }

    @Test
    public void testNestedSelectSQL() {
        final String lExpected = "SELECT tblGroup.SNAME, tblGroup.GROUPID, tblGroup.SDESCRIPTION, count.REGISTERED FROM tblGroup INNER JOIN (SELECT tblParticipant.GROUPID, COUNT(tblParticipant.MEMBERID) AS REGISTERED FROM tblParticipant GROUP BY tblParticipant.GROUPID) count ON tblGroup.GROUPID = count.GROUPID";
        try {
            final NestedDomainObjectHome lHome = new NestedDomainObjectHome();
            final String lObjecDefString = lHome.getObjectDefString();

            final JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(
                    lObjecDefString);
            final DBAdapterJoin lAdapter = new MySQLAdapterJoin(lJoinedObjectDef);
            final String lSQL = lAdapter.createSelectAllSQL();
            assertEquals("create nested", lExpected, lSQL);
        } catch (final SAXException exc) {
            fail(exc.getMessage());
        } catch (final VException exc) {
            fail(exc.getMessage());
        }
    }
}
