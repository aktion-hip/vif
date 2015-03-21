package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Vector;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PlacefillerCollection;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.GroupAdminHome;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger Created on Nov 9, 2004 */
public class NestedParticipantsOfGroupHomeTest {
    private static DataHouseKeeper data;
    private KeyObject key;

    @SuppressWarnings("serial")
    private class NestedParticipantsOfGroupHomeSub extends NestedParticipantsOfGroupHome {
        public void initSelect(final KeyObject inKey, final PlacefillerCollection inPlacefillers) {
            try {
                key = inKey;
                select(inKey, inPlacefillers);
            } catch (final Exception exc) {
                fail(exc.getMessage());
            }
        }

        @Override
        protected Vector<Object> createTestObjects() {
            final Vector<Object> outTests = new Vector<Object>();
            try {
                outTests.add(createSelectString(key));
            } catch (final Exception exc) {
                fail(exc.getMessage());
            }
            return outTests;
        }
    }

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Test
    public void testSQLSelect() throws Exception {
        String lExpected = "";
        if (data.isDBMySQL()) {
            lExpected = "SELECT tblParticipant.GROUPID, (NOW() >= tblParticipant.DTSUSPENDFROM) AS TestSuspended1, (NOW() <= tblParticipant.DTSUSPENDTO) AS TestSuspended2, Admins.MEMBERID AS GroupAdminID, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SCITY, tblMember.SZIP, tblMember.SMAIL, tblMember.BSEX FROM tblParticipant INNER JOIN tblMember ON tblParticipant.MEMBERID = tblMember.MEMBERID LEFT JOIN (SELECT tblGroupAdmin.GROUPID, tblGroupAdmin.MEMBERID FROM tblGroupAdmin WHERE tblGroupAdmin.GROUPID = 13) AS Admins ON tblParticipant.MEMBERID = Admins.MEMBERID WHERE tblParticipant.GROUPID = 13";
        }

        final DomainObjectHome lPlacefillerHome = data.getGroupAdminHome();
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(GroupAdminHome.KEY_GROUP_ID, new Integer(13));

        final PlacefillerCollection lPlacefillers = new PlacefillerCollection();
        lPlacefillers.add(lPlacefillerHome, lKey, NestedParticipantsOfGroupHome.NESTED_ALIAS);

        final NestedParticipantsOfGroupHomeSub lHome = new NestedParticipantsOfGroupHomeSub();
        if (!data.isEmbedded()) {
            lHome.initSelect(lKey, lPlacefillers);

            assertEquals("SQL select", lExpected, lHome.getTestObjects().next());
        }
    }

}
