package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.bom.impl.SetOperatorHomeImpl;
import org.hip.kernel.sys.VSys;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger Created on Oct 31, 2004 */
public class SetOperatorHomeImplTest {
    private static DataHouseKeeper data;

    private class UnionHomeSub extends SetOperatorHomeImpl {
        public UnionHomeSub() {
            super(SetOperatorHome.UNION);
        }

        public String getSQL() {
            return createSelect();
        }

        public String getOrderBy(final OrderObject inOrder) {
            return createOrderBy(inOrder);
        }
    }

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromSimple();
    }

    @Test
    public void testSelect() {
        final String lExpected = "(SELECT tblTestMember.SNAME, tblTestMember.SPASSWORD, tblTestMember.SFIRSTNAME, tblTestMember.DTMUTATION, tblTestMember.TESTMEMBERID FROM tblTestMember WHERE tblTestMember.SNAME = 'NameOfFirstSelect')\n"
                +
                " UNION (SELECT tblTest.FDOUBLE, tblTest.SSTREET, tblTest.FAMOUNT, tblTest.SPASSWORD, tblTest.SFAX, tblTest.SCITY, tblTest.TESTID, tblTest.SNAME, tblTest.SFIRSTNAME, tblTest.SLANGUAGE, tblTest.SPLZ, tblTest.SMAIL, tblTest.DTMUTATION, tblTest.STEL, tblTest.BSEX FROM tblTest WHERE tblTest.SNAME = 'NameOfSecondSelect')";
        final TestDomainObjectHomeImpl lHome = (TestDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestDomainObjectHomeImpl");
        final Test2DomainObjectHomeImpl lHome2 = data.getSimpleHome();
        try {
            final UnionHomeSub lUnionHome = new UnionHomeSub();

            KeyObject lKey = new KeyObjectImpl();
            lKey.setValue("Name", "NameOfFirstSelect");

            lUnionHome.addSet(lHome, lKey);

            lKey = new KeyObjectImpl();
            lKey.setValue("Name", "NameOfSecondSelect");

            lUnionHome.addSet(lHome2, lKey);

            assertEquals("union select", lExpected, lUnionHome.getSQL());
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testOrderBy() {
        final String lExpected = " ORDER BY SNAME, DTMUTATION";
        final TestDomainObjectHomeImpl lHome = (TestDomainObjectHomeImpl) VSys.homeManager
                .getHome("org.hip.kernel.bom.impl.test.TestDomainObjectHomeImpl");
        try {
            final OrderObject lOrder = new OrderObjectImpl();
            lOrder.setValue("Name", 0);
            lOrder.setValue("Mutation", 1);

            final UnionHomeSub lUnionHome = new UnionHomeSub();
            lUnionHome.addSet(lHome, lOrder);
            assertEquals("order by", lExpected, lUnionHome.getOrderBy(lOrder));
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
    }
}
