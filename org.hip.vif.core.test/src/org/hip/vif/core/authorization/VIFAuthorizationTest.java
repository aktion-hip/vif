package org.hip.vif.core.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.bom.VIFAuthorization;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger */
public class VIFAuthorizationTest {
    private final static String XML1_EXPECTED =
            "<Authorization>\n" +
                    "<Roles>\n" +
                    "<Role roleID=\"1\" specific=\"0\">SU</Role>\n" +
                    "</Roles>\n" +
                    "<Permissions>";
    private final static String XML2_EXPECTED =
            "<Authorization>\n" +
                    "<Roles>\n" +
                    "<Role roleID=\"3\" specific=\"1\">Group-Administrator</Role>\n" +
                    "</Roles>\n" +
                    "<Permissions>";

    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(true);
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllInAll();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testDo() throws Exception {
        final Long[] lMemberIDs = data.create2MembersAndRoleAndPermissions();
        final VIFAuthorization lAuthorization1 = new VIFAuthorization(lMemberIDs[0]);
        final VIFAuthorization lAuthorization2 = new VIFAuthorization(lMemberIDs[1]);
        assertTrue("check role 1", lAuthorization1.checkRoles(new String[] { String.valueOf(RolesConstants.SU) }));
        assertTrue(
                "check role 2",
                lAuthorization1.checkRoles(new String[] { String.valueOf(RolesConstants.MEMBER),
                        String.valueOf(RolesConstants.SU) }));
        assertTrue("check role 3", !lAuthorization1.checkRoles(new String[] { String.valueOf(RolesConstants.MEMBER) }));
        assertTrue("check role 4",
                lAuthorization2.checkRoles(new String[] { String.valueOf(RolesConstants.GROUP_ADMINISTRATOR) }));
        assertTrue("check role 5", lAuthorization2.checkRoles(new String[] {}));

        assertTrue("check permission 1",
                lAuthorization1.checkPermissions(new String[] { DataHouseKeeper.PERMISSION_LABEL_1 }));
        assertTrue(
                "check permission 2",
                lAuthorization1.checkPermissions(new String[] { DataHouseKeeper.PERMISSION_LABEL_1,
                        DataHouseKeeper.PERMISSION_LABEL_2 }));
        assertTrue("check permission 3",
                lAuthorization1.checkPermissions(new String[] { DataHouseKeeper.PERMISSION_LABEL_2 }));
        assertTrue("check permission 4", !lAuthorization1.checkPermissions(new String[] { "dummy" }));
        assertTrue("check permission 5",
                lAuthorization2.checkPermissions(new String[] { DataHouseKeeper.PERMISSION_LABEL_1 }));
        assertTrue("check permission 6",
                !lAuthorization2.checkPermissions(new String[] { DataHouseKeeper.PERMISSION_LABEL_2 }));

        assertTrue("check permission 7", lAuthorization1.hasPermission(DataHouseKeeper.PERMISSION_LABEL_1));
        assertTrue("check permission 8", lAuthorization1.hasPermission(""));
        assertTrue("check permission 9", lAuthorization1.hasPermission(DataHouseKeeper.PERMISSION_LABEL_2));
        assertTrue("check permission 10", !lAuthorization1.hasPermission("dummy"));
        assertTrue("check permission 11", lAuthorization2.hasPermission(DataHouseKeeper.PERMISSION_LABEL_1));
        assertTrue("check permission 12", !lAuthorization2.hasPermission(DataHouseKeeper.PERMISSION_LABEL_2));

        // String[] lPermissions = lAuthorization1.getPermittedPermissions();
        final Collection<String> lVPermissions = lAuthorization1.getPermissions();
        assertEquals("permitted permissions size", 2, lVPermissions.size());
        assertTrue("permitted permissions 1", lVPermissions.contains(DataHouseKeeper.PERMISSION_LABEL_1));
        assertTrue("permitted permissions 2", lVPermissions.contains(DataHouseKeeper.PERMISSION_LABEL_2));

        assertEquals("XML 1", XML1_EXPECTED, lAuthorization1.toXML().substring(0, XML1_EXPECTED.length()));
        assertEquals("XML 2", XML2_EXPECTED, lAuthorization2.toXML().substring(0, XML2_EXPECTED.length()));

        assertTrue("not equals", !lAuthorization1.equals(lAuthorization2));
        final VIFAuthorization lAuthorization3 = new VIFAuthorization(lMemberIDs[0]);
        assertTrue("equals", lAuthorization1.equals(lAuthorization3));
        assertEquals("equal hash code", lAuthorization1.hashCode(), lAuthorization3.hashCode());

        assertCollection(new String[] { "testPermission1", "testPermission2" }, lAuthorization1.getPermissions());
        assertCollection(new String[] { "testPermission1" }, lAuthorization2.getPermissions());
        assertCollection(new String[] { "testPermission1", "testPermission2" }, lAuthorization3.getPermissions());
    }

    private void assertCollection(final String[] inExpected, final Collection<String> inActual) {
        for (final String lExpected : inExpected) {
            assertTrue(inActual.contains(lExpected));
        }
        assertEquals(inExpected.length, inActual.size());
    }
}
