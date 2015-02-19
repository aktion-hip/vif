package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Collections;
import java.util.Vector;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.IGetValueStrategy;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.AbstractCriteriumRenderer;
import org.hip.kernel.bom.impl.BetweenObjectImpl;
import org.hip.kernel.bom.impl.CriteriaStackFactory;
import org.hip.kernel.bom.impl.InObjectImpl;
import org.hip.kernel.bom.impl.KeyCriterionImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PreparedValueStrategy;
import org.hip.kernel.bom.impl.SQLRange;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.SortableItem;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author: Benno Luthiger */
public class KeyObjectImplTest {
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
    public void testCreate() throws VException {
        final DomainObjectHome lHome = data.getSimpleHome();

        final KeyObject lKey1 = new KeyObjectImpl();
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(12));
        assertNotNull("not null 1", lKey1);
        assertEquals("key 1", "tblTest.FAMOUNT = 12", new String(lKey1.render2(lHome)));

        final KeyObject lKey2 = new KeyObjectImpl();
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_DOUBLE, new Integer(100));
        lKey2.setValue(lKey1);
        assertNotNull("not null 2", lKey2);
        assertEquals("size", 2, lKey2.size());
        assertEquals("key 2",
                "tblTest.FDOUBLE = 100 AND (tblTest.FAMOUNT = 12)",
                new String(lKey2.render2(lHome)));

        assertTrue("primary key", lKey2.isPrimaryKey());
        lKey2.setSchemaName("dummy");
        assertTrue("not primary key", !lKey2.isPrimaryKey());

        final KeyObject lKey3 = new KeyObjectImpl();
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(101));
        lKey3.setValue(lKey1);
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(100));
        assertEquals("key 3",
                "tblTest.FAMOUNT = 101 AND (tblTest.FAMOUNT = 12) AND tblTest.FAMOUNT = 100",
                new String(lKey3.render2(lHome)));

        assertTrue("equals", lKey2.equals(lKey2));
        assertEquals("equal hash code", lKey2.hashCode(), lKey2.hashCode());
        assertTrue("not equals", !lKey2.equals(lKey1));
        assertTrue("not equal hash code", lKey2.hashCode() != lKey1.hashCode());

        // empty KeyObjects
        final KeyObject lKey4 = new KeyObjectImpl();
        assertTrue("empty key", lKey4.getItems2().isEmpty());
        assertEquals("key 4", "", new String(lKey4.render2(lHome)));

        KeyObject lKey = new KeyObjectImpl();
        final Date lDate1 = Date.valueOf("1989-08-20");
        final Date lDate2 = Date.valueOf("1999-04-24");
        final SQLRange lBetween = new BetweenObjectImpl(lDate1, lDate2);
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, lBetween);
        assertEquals("key 5",
                "tblTest.DTMUTATION BETWEEN DATE('1989-08-20') AND DATE('1999-04-24')",
                new String(lKey.render2(lHome)));

        lKey = new KeyObjectImpl();
        final SQLRange lIn = new InObjectImpl<Integer>(new Integer[] { new Integer(65), new Integer(77),
                new Integer(87), new Integer(99) });
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, lIn);
        assertEquals("key 6",
                "tblTest.FAMOUNT IN (65, 77, 87, 99)",
                new String(lKey.render2(lHome)));

        // if we pass a collection, the key object creates a InObjectImpl() internally
        final Vector<Integer> lValues = new Vector<Integer>();
        Collections.addAll(lValues, new Integer(65), new Integer(77), new Integer(87), new Integer(99));
        lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, lValues);
        assertEquals("tblTest.FAMOUNT IN (65, 77, 87, 99)", new String(lKey.render2(lHome)));
    }

    @Test
    public void testRender() throws VException {
        final DomainObjectHome lHome = data.getSimpleHome();

        KeyObject lKey1 = new KeyObjectImpl();
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Ha%", "LIKE");
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Ha%", "LIKE", BinaryBooleanOperator.OR);
        assertEquals("key 1",
                "tblTest.SNAME LIKE 'Ha%' OR tblTest.SFIRSTNAME LIKE 'Ha%'",
                new String(lKey1.render2(lHome)));

        KeyObject lKey2 = new KeyObjectImpl();
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "A%", "LIKE");
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "B%", "LIKE");

        lKey1.setValue(lKey2, BinaryBooleanOperator.OR);
        assertEquals(
                "key 2",
                "tblTest.SNAME LIKE 'Ha%' OR tblTest.SFIRSTNAME LIKE 'Ha%' OR (tblTest.SNAME LIKE 'A%' AND tblTest.SFIRSTNAME LIKE 'B%')",
                new String(lKey1.render2(lHome)));

        final KeyObject lKey3 = new KeyObjectImpl();
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Ha%", "LIKE");
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Ha%", "LIKE", BinaryBooleanOperator.OR);
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(87));
        assertEquals("key 3",
                "(tblTest.SNAME LIKE 'Ha%' OR tblTest.SFIRSTNAME LIKE 'Ha%') AND tblTest.FAMOUNT = 87",
                new String(lKey3.render2(lHome)));

        // test of ldap style key rendering
        lKey1 = new KeyObjectImpl();
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "Ha*");
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Ha*", "=", BinaryBooleanOperator.OR);

        lKey2 = new KeyObjectImpl();
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_NAME, "A*");
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "B*");

        lKey1.setValue(lKey2, BinaryBooleanOperator.OR);

        lKey1.setCriteriumRenderer(new LDAPCriteriumRenderer());
        lKey1.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.LDAP));
        lKey1.setLevelReturnFormatter(KeyCriterionImpl.LEVEL_STRAIGHT);
        assertEquals("ldap key 3",
                "|(tblTest.SNAME='Ha*')(tblTest.SFIRSTNAME='Ha*')(&(tblTest.SNAME='A*')(tblTest.SFIRSTNAME='B*'))",
                new String(lKey1.render2(lHome)));
    }

    @Test
    public void testRenderPrepared() throws VException {
        final DomainObjectHome lHome = data.getSimpleHome();
        final IGetValueStrategy lGetValueStrategy = new PreparedValueStrategy();

        KeyObject lKey1 = new KeyObjectImpl();
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(12));
        lKey1.setGetValueStrategy(lGetValueStrategy);
        assertEquals("key 1", "tblTest.FAMOUNT = ?", new String(lKey1.render2(lHome)));

        final KeyObject lKey2 = new KeyObjectImpl();
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_DOUBLE, new Integer(100));
        lKey2.setValue(lKey1);
        lKey2.setGetValueStrategy(lGetValueStrategy);
        assertEquals("key 2",
                "tblTest.FDOUBLE = ? AND (tblTest.FAMOUNT = ?)",
                new String(lKey2.render2(lHome)));

        KeyObject lKey3 = new KeyObjectImpl();
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(101));
        lKey3.setValue(lKey1);
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(100));
        lKey3.setGetValueStrategy(lGetValueStrategy);
        assertEquals("key 3",
                "tblTest.FAMOUNT = ? AND (tblTest.FAMOUNT = ?) AND tblTest.FAMOUNT = ?",
                new String(lKey3.render2(lHome)));

        lKey1 = new KeyObjectImpl();
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new Integer(12));
        lKey1.setValue(Test2DomainObjectHomeImpl.KEY_DOUBLE, new Integer(20));
        lKey3 = new KeyObjectImpl();
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City");
        lKey3.setValue(Test2DomainObjectHomeImpl.KEY_PLZ, "PLZ");
        lKey3.setValue(lKey1);
        lKey3.setGetValueStrategy(lGetValueStrategy);
        lKey3.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.FLAT_JOIN, ", "));
        assertEquals("key 4",
                "tblTest.SCITY = ?, tblTest.SPLZ = ?, (tblTest.FAMOUNT = ?, tblTest.FDOUBLE = ?)",
                new String(lKey3.render2(lHome)));

        KeyObject lKey = new KeyObjectImpl();
        final Date lDate1 = Date.valueOf("1989-08-20");
        final Date lDate2 = Date.valueOf("1999-04-24");
        final SQLRange lBetween = new BetweenObjectImpl(lDate1, lDate2);
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, lBetween);
        lKey.setGetValueStrategy(lGetValueStrategy);
        assertEquals("key 5",
                "tblTest.DTMUTATION BETWEEN ? AND ?",
                new String(lKey.render2(lHome)));

        lKey = new KeyObjectImpl();
        final SQLRange lIn = new InObjectImpl<Integer>(new Integer[] { new Integer(65), new Integer(77),
                new Integer(87), new Integer(99) });
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, lIn);
        lKey.setGetValueStrategy(lGetValueStrategy);
        assertEquals("key 6",
                "tblTest.FAMOUNT IN (?, ?, ?, ?)",
                new String(lKey.render2(lHome)));
    }

    @Test
    public void testSort() throws VException {
        final String[] lExpected = { "field1", "__keyObject(VIF)", "field2", "field3" };
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(lExpected[0], new Integer(12), "=", BinaryBooleanOperator.AND);
        final KeyObject lKeyInner = new KeyObjectImpl();
        lKeyInner.setValue(lExpected[2], "Test", "=");
        lKeyInner.setValue(lExpected[3], "test", "=", BinaryBooleanOperator.OR);
        lKey.setValue(lKeyInner, BinaryBooleanOperator.AND);

        int i = 0;
        for (final SortableItem lItem : lKey.getItems2()) {
            assertEquals(lExpected[i], ((KeyCriterion) lItem).getName());
            i++;
        }
    }

    // -- private classes --

    private class LDAPCriteriumRenderer extends AbstractCriteriumRenderer {
        @Override
        public StringBuffer render() {
            return new StringBuffer(render2());
        }

        @Override
        public StringBuilder render2() {
            final StringBuilder outSQL = new StringBuilder(operand1);
            outSQL.append(comparison).append(operand2);
            return outSQL;
        }
    }

}
