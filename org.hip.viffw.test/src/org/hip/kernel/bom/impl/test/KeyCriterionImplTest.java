package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.hip.kernel.exc.VException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 14.10.2006 */
public class KeyCriterionImplTest {
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromSimple();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRender() throws VException {
        final DomainObjectHome lHome = data.getSimpleHome();

        KeyCriterion lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Adam", "=",
                BinaryBooleanOperator.AND, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        assertEquals("render 1", "tblTest.SFIRSTNAME = 'Adam'", new String(lCriterion.render(lHome)));

        lCriterion = new KeyCriterionImpl(createKey(), BinaryBooleanOperator.AND);
        assertEquals(
                "render 2",
                "(tblTest.SFIRSTNAME = 'Seconda' AND tblTest.BSEX = 0 AND tblTest.FAMOUNT = 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00'))",
                new String(lCriterion.render(lHome)));

        lCriterion = new KeyCriterionImpl(createKey2(), BinaryBooleanOperator.AND);
        assertEquals(
                "render 3",
                "((tblTest.SFIRSTNAME LIKE 'Test%' OR tblTest.SFIRSTNAME LIKE 'test%') AND tblTest.FAMOUNT > 24 AND tblTest.DTMUTATION = TIMESTAMP('2002-02-01 10:00:00'))",
                new String(lCriterion.render(lHome)));

        final Date lDate1 = Date.valueOf("1989-08-20");
        final Date lDate2 = Date.valueOf("1999-04-24");
        final BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate1, lDate2);
        lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_MUTATION, lBetween, "",
                BinaryBooleanOperator.AND, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        assertEquals("render 4",
                "tblTest.DTMUTATION BETWEEN DATE('1989-08-20') AND DATE('1999-04-24')",
                new String(lCriterion.render(lHome)));

        @SuppressWarnings("rawtypes")
        final InObjectImpl lIn = new InObjectImpl(new Object[] { new Integer(10), new Integer(13), new Integer(16),
                new Integer(20) });
        lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_AMOUNT, lIn, "", BinaryBooleanOperator.AND,
                KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        assertEquals("render 5",
                "tblTest.FAMOUNT IN (10, 13, 16, 20)",
                new String(lCriterion.render(lHome)));

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Adam", "=");
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "Sun City", "=", BinaryBooleanOperator.XOR);
        lCriterion = new KeyCriterionImpl(lKey, BinaryBooleanOperator.AND);
        assertEquals("render 6",
                "(tblTest.SFIRSTNAME = 'Adam' XOR tblTest.SCITY = 'Sun City')",
                lCriterion.render(lHome).toString());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRenderPrepared() throws VException {
        final DomainObjectHome lHome = data.getSimpleHome();
        final IGetValueStrategy lGetValueStrategy = new PreparedValueStrategy();

        KeyCriterion lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Adam", "=",
                BinaryBooleanOperator.AND, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        assertEquals("render 1", "tblTest.SFIRSTNAME = ?", new String(lCriterion.render(lHome)));

        lCriterion = new KeyCriterionImpl(createKey(), BinaryBooleanOperator.AND);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        assertEquals("render 2",
                "(tblTest.SFIRSTNAME = ? AND tblTest.BSEX = ? AND tblTest.FAMOUNT = ? AND tblTest.DTMUTATION = ?)",
                new String(lCriterion.render(lHome)));

        lCriterion = new KeyCriterionImpl(createKey2(), BinaryBooleanOperator.AND);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        assertEquals(
                "render 3",
                "((tblTest.SFIRSTNAME LIKE ? OR tblTest.SFIRSTNAME LIKE ?) AND tblTest.FAMOUNT > ? AND tblTest.DTMUTATION = ?)",
                new String(lCriterion.render(lHome)));

        final Date lDate1 = Date.valueOf("1989-08-20");
        final Date lDate2 = Date.valueOf("1999-04-24");
        final BetweenObjectImpl lBetween = new BetweenObjectImpl(lDate1, lDate2);
        lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_MUTATION, lBetween, "",
                BinaryBooleanOperator.AND, KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        assertEquals("render 4",
                "tblTest.DTMUTATION BETWEEN ? AND ?",
                new String(lCriterion.render(lHome)));

        final InObjectImpl lIn = new InObjectImpl(new Object[] { new Integer(10), new Integer(13), new Integer(16),
                new Integer(20) });
        lCriterion = new KeyCriterionImpl(Test2DomainObjectHomeImpl.KEY_AMOUNT, lIn, "", BinaryBooleanOperator.AND,
                KeyCriterionImpl.COLUMN_MODIFIER_DFT);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        assertEquals("render 5",
                "tblTest.FAMOUNT IN (?, ?, ?, ?)",
                new String(lCriterion.render(lHome)));

        final KeyObject lKey2 = new KeyObjectImpl();
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_CITY, "City");
        lKey2.setValue(Test2DomainObjectHomeImpl.KEY_PLZ, "PLZ");
        final KeyObject lKey = createKey();
        lKey.setValue(lKey2);
        lCriterion = new KeyCriterionImpl(lKey, BinaryBooleanOperator.AND);
        lCriterion.setGetValueStrategy(lGetValueStrategy);
        lCriterion.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.FLAT_JOIN, ", "));
        lCriterion.setLevelReturnFormatter(KeyCriterionImpl.LEVEL_STRAIGHT);
        assertEquals(
                "render 6",
                "tblTest.SFIRSTNAME = ?, tblTest.BSEX = ?, tblTest.FAMOUNT = ?, tblTest.DTMUTATION = ?, tblTest.SCITY = ?, tblTest.SPLZ = ?",
                new String(lCriterion.render(lHome)));
    }

    @Test
    public void testRender2() throws VException {
        // LDAP style key rendering
        final DomainObjectHome lHome = data.getSimpleHome();

        final KeyCriterion lCriterion = new KeyCriterionImpl(createKeyLDAP(), BinaryBooleanOperator.AND);
        lCriterion.setCriteriaStackFactory(new CriteriaStackFactory(CriteriaStackFactory.StackType.LDAP));
        lCriterion.setCriteriumRenderer(new LDAPRenderStrategy());
        lCriterion.setLevelReturnFormatter(KeyCriterionImpl.LEVEL_STRAIGHT);
        assertEquals(
                "render",
                "&(|(tblTest.SFIRSTNAME='Test*')(tblTest.SFIRSTNAME='test*'))(tblTest.FAMOUNT>24)(tblTest.DTMUTATION=TIMESTAMP('2002-02-01 10:00:00'))",
                new String(lCriterion.render(lHome)));
    }

    private KeyObject createKey() throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Seconda");
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_SEX, new Integer(0));
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24));

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
        return outKey;
    }

    private KeyObject createKey2() throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Test%", "LIKE");
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "test%", "LIKE", BinaryBooleanOperator.OR);
        outKey.setValue(lKey);
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24), ">");

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
        return outKey;
    }

    private KeyObject createKeyLDAP() throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "Test*");
        lKey.setValue(Test2DomainObjectHomeImpl.KEY_FIRSTNAME, "test*", "=", BinaryBooleanOperator.OR);
        outKey.setValue(lKey);
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_AMOUNT, new BigDecimal(24), ">");

        final Calendar lCalender = GregorianCalendar.getInstance();
        lCalender.set(2002, 1, 1, 10, 0, 0);
        lCalender.getTime();
        outKey.setValue(Test2DomainObjectHomeImpl.KEY_MUTATION, new Timestamp(lCalender.getTimeInMillis()));
        return outKey;
    }

    // -- private classes --

    private class LDAPRenderStrategy extends AbstractCriteriumRenderer {
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
