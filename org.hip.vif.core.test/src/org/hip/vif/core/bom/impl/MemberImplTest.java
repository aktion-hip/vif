package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHistoryHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.core.search.VIFIndexing;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger */
public class MemberImplTest {
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
    public void testNew() throws Exception {
        final MemberHome lHomeMember = data.getMemberHome();
        final LinkMemberRoleHome lHomeLink = data.getLinkMemberRoleHome();
        final String[] lExpectedRoles = new String[] { "1", "3" };

        // pre
        data.checkForEmptyTable();

        Member lMember = (Member) lHomeMember.create();
        lMember.ucNew("userT1", "NameT", "VornameT", "StrasseT", "PLZ-T", "StadtT", "", "", "mail@test", "1", "de",
                "123", lExpectedRoles);

        assertEquals("number of members", 1, lHomeMember.getCount());
        assertEquals("number of associated roles", 2, lHomeLink.getCount());

        lMember = (Member) lHomeMember.select().next();
        final Collection<Role> lRoles = lHomeLink.getRolesOf(new Long(lMember.get("ID").toString()));
        int i = 0;
        for (final Role lRole : lRoles) {
            assertEquals("Role id " + i, lExpectedRoles[i++], lRole.getElementID());
        }
    }

    @Test
    public void testNew2() throws Exception {
        final MemberHome lHomeMember = data.getMemberHome();
        final LinkMemberRoleHome lHomeLink = data.getLinkMemberRoleHome();
        final String[] lExpectedRoles = new String[] { "1", "3" };

        // pre
        data.checkForEmptyTable();

        Member lMember = (Member) lHomeMember.create();
        lMember.set(MemberHome.KEY_USER_ID, "userT1");
        lMember.set(MemberHome.KEY_NAME, "NameT");
        lMember.set(MemberHome.KEY_FIRSTNAME, "VornameT");
        lMember.set(MemberHome.KEY_STREET, "StrasseT");
        lMember.set(MemberHome.KEY_ZIP, "PLZ-T");
        lMember.set(MemberHome.KEY_CITY, "StadtT");
        lMember.set(MemberHome.KEY_MAIL, "mail@test");
        lMember.set(MemberHome.KEY_SEX, 1l);
        lMember.set(MemberHome.KEY_LANGUAGE, "de");
        lMember.set(MemberHome.KEY_PASSWORD, "123");
        lMember.ucNew(Arrays.asList(lExpectedRoles));

        assertEquals(1, lHomeMember.getCount());
        assertEquals(2, lHomeLink.getCount());

        lMember = (Member) lHomeMember.select().next();
        final Collection<Role> lRoles = lHomeLink.getRolesOf(new Long(lMember.get("ID").toString()));
        int i = 0;
        for (final Role lRole : lRoles) {
            assertEquals("Role id " + i, lExpectedRoles[i++], lRole.getElementID());
        }
    }

    @Test
    public void testSave() throws Exception {
        final String lNamePre = "Befor";
        final String lNamePost = "After";

        // pre
        data.checkForEmptyTable();

        final String lMemberID = String.valueOf(data.createMember2Roles(lNamePre));
        final MemberHome lMemberHome = new MemberHomeImplSub();

        Member lMember = lMemberHome.getMember(lMemberID);
        assertEquals("Member name 1", lNamePre, lMember.get(MemberHome.KEY_NAME));

        final MemberHistoryHome lHistory = data.getMemberHistoryHome();
        final int lHistoryCount = lHistory.getCount();

        // save with new name
        boolean lChangedRoles = lMember.ucSave(lNamePost, "FiName", "Street", "PLZ", "City", "", "", "test@mail", "1",
                "de", new String[] { "1", "3" }, new Long(21));
        assertEquals("Number of history entries 1", lHistoryCount + 1, lHistory.getCount());
        assertFalse("roles not changed 1", lChangedRoles);

        waitSomeMillies(1000);

        lMember = lMemberHome.getMember(lMemberID);
        assertEquals("Member name 2", lNamePost, lMember.get(MemberHome.KEY_NAME));

        // save with new roles
        lChangedRoles = lMember.ucSave(lNamePost, "FiName", "Street", "PLZ", "City", "", "", "test@mail", "1", "de",
                new String[] { "2", "4" }, new Long(21));
        assertEquals("Number of history entries 2", lHistoryCount + 1, lHistory.getCount());
        assertTrue("roles changed", lChangedRoles);

        waitSomeMillies(1000);

        // save with no changes
        lChangedRoles = lMember.ucSave(lNamePost, "FiName", "Street", "PLZ", "City", "", "", "test@mail", "1", "de",
                new String[] { "2", "4" }, new Long(21));
        assertEquals("Number of history entries 3", lHistoryCount + 1, lHistory.getCount());
        assertFalse("roles not changed 2", lChangedRoles);
    }

    private void waitSomeMillies(final int inMillies) {
        final long lTo = System.currentTimeMillis() + inMillies;

        while (System.currentTimeMillis() < lTo) {
        }
    }

    @Test
    public void testSave2() throws Exception {
        final String lNamePre = "Befor";
        final String lNamePost = "After";

        // pre
        data.checkForEmptyTable();

        final String lMemberID = String.valueOf(data.createMember2Roles(lNamePre));
        final MemberHome lMemberHome = new MemberHomeImplSub();

        Member lMember = lMemberHome.getMember(lMemberID);
        assertEquals(lNamePre, lMember.get(MemberHome.KEY_NAME));

        final MemberHistoryHome lHistory = data.getMemberHistoryHome();
        final int lHistoryCount = lHistory.getCount();

        // save with new name
        lMember.set(MemberHome.KEY_NAME, lNamePost);
        lMember.set(MemberHome.KEY_FIRSTNAME, "FiName");
        lMember.set(MemberHome.KEY_STREET, "Street");
        lMember.set(MemberHome.KEY_ZIP, "PLZ");
        lMember.set(MemberHome.KEY_CITY, "City");
        lMember.set(MemberHome.KEY_PHONE, "");
        lMember.set(MemberHome.KEY_FAX, "");
        lMember.set(MemberHome.KEY_MAIL, "test@mail");
        lMember.set(MemberHome.KEY_SEX, 1l);
        lMember.set(MemberHome.KEY_LANGUAGE, "de");
        lMember.ucSave(new Long(21));
        assertEquals(lHistoryCount + 1, lHistory.getCount());

        waitSomeMillies(1000);

        lMember = lMemberHome.getMember(lMemberID);
        assertEquals(lNamePost, lMember.get(MemberHome.KEY_NAME));

        // save with no changes
        lMember.ucSave(new Long(21));

        assertEquals(lHistoryCount + 1, lHistory.getCount());

        // test mandatory values
        lMember.set(MemberHome.KEY_NAME, null);
        try {
            lMember.ucSave(new Long(21));
            Assert.fail("shouldn't get here");
        } catch (final BOMChangeValueException exc) {
            // intentionally left empty
        }
        lMember.set(MemberHome.KEY_NAME, "");
        try {
            lMember.ucSave(new Long(21));
            Assert.fail("shouldn't get here");
        } catch (final AssertionFailedError exc) {
            // intentionally left empty
        }
    }

    @Test
    public void testSave3() throws Exception {
        final String lNamePre = "Befor";
        final String lNamePost = "After";

        // pre
        data.checkForEmptyTable();

        final String lMemberID = String.valueOf(data.createMember2Roles(lNamePre));
        final MemberHome lMemberHome = new MemberHomeImplSub();

        final Member lMember = lMemberHome.getMember(lMemberID);
        assertEquals(lNamePre, lMember.get(MemberHome.KEY_NAME));

        final MemberHistoryHome lHistory = data.getMemberHistoryHome();
        final int lHistoryCount = lHistory.getCount();

        // save with new name
        lMember.set(MemberHome.KEY_NAME, lNamePost);
        lMember.set(MemberHome.KEY_FIRSTNAME, "FiName");
        lMember.set(MemberHome.KEY_STREET, "Street");
        lMember.set(MemberHome.KEY_ZIP, "PLZ");
        lMember.set(MemberHome.KEY_CITY, "City");
        lMember.set(MemberHome.KEY_PHONE, "");
        lMember.set(MemberHome.KEY_FAX, "");
        lMember.set(MemberHome.KEY_MAIL, "test@mail");
        lMember.set(MemberHome.KEY_SEX, 1l);
        lMember.set(MemberHome.KEY_LANGUAGE, "de");
        boolean lChangedRoles = lMember.ucSave(getRoles(new String[] { "2", "4" }), new Long(21));
        assertEquals(lHistoryCount + 1, lHistory.getCount());
        assertTrue(lChangedRoles);

        // save with no changes
        lChangedRoles = lMember.ucSave(getRoles(new String[] { "2", "4" }), new Long(21));
        assertEquals(lHistoryCount + 1, lHistory.getCount());
        assertFalse(lChangedRoles);

        // test size of roles
        try {
            lMember.ucSave(new Vector<String>(), new Long(21));
            fail("Shouldn't get here!");
        } catch (final AssertionFailedError exc) {
            // intentionally left empty
        }
    }

    private Collection<String> getRoles(final String[] inRoles) {
        return new Vector<String>(Arrays.asList(inRoles));
    }

    @Test
    public void testDelete() throws Exception {
        // pre
        data.checkForEmptyTable();

        final MemberHistoryHome lHistory = data.getMemberHistoryHome();
        final int lHistoryCount = lHistory.getCount();

        final String lMemberID = String.valueOf(data.createMember2Roles());

        final MemberHome lMemberHome = data.getMemberHome();
        final LinkMemberRoleHome lLinkHome = data.getLinkMemberRoleHome();
        assertEquals("Number of members 1", 1, lMemberHome.getCount());
        assertEquals("Number of roles 1", 2, lLinkHome.getCount());

        final Member lMember = lMemberHome.getMember(lMemberID);
        try {
            lMember.delete(true);
            fail("Shouldn't get here");
        } catch (final org.hip.kernel.sys.AssertionFailedError exc) {
            // intentionally left empty
        }
        try {
            lMember.delete();
            fail("Shouldn't get here");
        } catch (final org.hip.kernel.sys.AssertionFailedError exc) {
            // intentionally left empty
        }
        lMember.delete(new Long(21));

        assertEquals("Number of members 2", 0, lMemberHome.getCount());
        assertEquals("Number of roles 2", 2, lLinkHome.getCount());
        assertEquals("Number of history entries", lHistoryCount + 1, lHistory.getCount());
    }

    @Test
    public void testCreateSU() throws Exception {
        // pre
        data.checkForEmptyTable();

        final MemberHome lMemberHome = data.getMemberHome();
        final Member lMember = (Member) lMemberHome.create();
        lMember.ucCreateSU("TestSU", "SU-Pwd", "de");

        assertEquals("Number of members", 1, lMemberHome.getCount());

        final LinkMemberRoleHome lLinkHome = data.getLinkMemberRoleHome();
        assertEquals("Number of roles", 1, lLinkHome.getCount());

        final Collection<Role> lRoles = lLinkHome
                .getRolesOf(new Long(lMemberHome.select().next().get("ID").toString()));
        for (final Role lRole : lRoles) {
            assertEquals("Roles id of SU", "1", lRole.getElementID());
        }
    }

    @Test
    public void testGetBestRole() throws Exception {
        final DomainObjectHome lHome = data.getMemberHome();
        final int lCountMembers = lHome.getCount();
        final int lCountLink = data.getLinkMemberRoleHome().getCount();
        Long lMemberID = data.createMemberRoles("TestMember", new String[] { ApplicationConstants.ROLE_ID_SU,
                ApplicationConstants.ROLE_ID_GROUP_ADMIN });
        assertEquals("Count member 1", lCountMembers + 1, lHome.getCount());
        assertEquals("Count link 1", lCountLink + 2, data.getLinkMemberRoleHome().getCount());
        assertEquals("Best role 1", Integer.parseInt(ApplicationConstants.ROLE_ID_SU),
                ((MemberHome) lHome).getMember(lMemberID.toString()).getBestRole());

        data.deleteAllFromMember();
        data.deleteAllFromLinkMemberRole();
        lMemberID = data.createMemberRoles("TestMember2", new String[] { ApplicationConstants.ROLE_ID_MEMBER,
                ApplicationConstants.ROLE_ID_GROUP_ADMIN, ApplicationConstants.ROLE_ID_PARTICIPANT });
        assertEquals("Count member 2", 1, lHome.getCount());
        assertEquals("Count link 2", 3, data.getLinkMemberRoleHome().getCount());
        assertEquals("Best role 2", Integer.parseInt(ApplicationConstants.ROLE_ID_GROUP_ADMIN), ((MemberHome) lHome)
                .getMember(lMemberID.toString()).getBestRole());
    }

    @Test
    public void testIndexing() throws Exception {
        final String lUserName = "TestIndex";
        final String lUserID = "userTest";

        // pre: create two members and index them
        data.create2Members();
        assertEquals("number of indexed 1", 2, IndexHouseKeeper.countIndexedMembers());
        try {
            search(lUserName);
            fail("shouldn't get here, because we don't expect a hit");
        } catch (final NoHitsException exc) {
            // intentionally left empty
        }

        // main 1: create entry
        final MemberHome lHome = data.getMemberHome();
        Member lMember = (Member) lHome.create();
        lMember.ucNew(lUserID, lUserName, "VornameT", "StrasseT", "PLZ-T", "StadtT", "", "", "mail@test", "1", "de",
                "123", new String[] { "1" });
        assertEquals("number of indexed 2", 3, IndexHouseKeeper.countIndexedMembers());
        assertEquals("number of found 2", 1, search(lUserName).length);

        // main 2: refresh entry
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(MemberHome.KEY_USER_ID, lUserID);
        lMember = (Member) lHome.findByKey(lKey);
        lMember.ucSave(lUserName, "VornameT2", "StrasseT2", "PLZ-T2", "StadtT2", "", "", "mail@test", "1", "de",
                new Long(44));
        assertEquals("number of indexed 3", 3, IndexHouseKeeper.countIndexedMembers());
        assertEquals("number of found 3", 1, search(lUserName).length);

        // main 3: delete entry
        data.deleteAllFromMemberHistory();
        lMember = (Member) lHome.findByKey(lKey);
        lMember.delete(new Long(55));
        assertEquals("number of indexed 4", 2, IndexHouseKeeper.countIndexedMembers());
        try {
            search(lUserName);
            fail("shouldn't get here, because we don't expect a hit");
        } catch (final NoHitsException exc) {
            // intentionally left empty
        }
    }

    private Document[] search(final String inQuery) throws CorruptIndexException, IOException, NoHitsException,
    ParseException {
        final IndexReader lReader = VIFIndexing.INSTANCE.createMemberIndexReader();
        final QueryParser lParser = new QueryParser(AbstractSearching.IndexField.MEMBER_NAME.fieldName,
                IndexHouseKeeper.getAnalyzer());
        return IndexHouseKeeper.search(lParser.parse(inQuery), lReader);
    }

    @Test
    public void testUserSettings() throws Exception {
        final String lLanguageKey = "language";

        final String lMemberID = data.createMember();
        final MemberHome lHome = data.getMemberHome();
        final Member lMember1 = lHome.getMember(lMemberID);
        assertNull(lMember1.getUserSettings(lLanguageKey));

        lMember1.setUserSettings(lLanguageKey, "de");

        final Member lMember2 = lHome.getMember(lMemberID);
        assertEquals("de", lMember2.getUserSettings(lLanguageKey));
    }

    // --- private classes ---

    /** We need to creat special member objects for testing purposes, see
     * <code>org.hip.vif.bom.impl.test.TestMemberImpl</code>. */
    @SuppressWarnings("serial")
    private class MemberHomeImplSub extends MemberHomeImpl {
        private final static String MEMBER_CLASS_NAME = "org.hip.vif.core.bom.impl.TestMemberImpl";

        @Override
        public String getObjectClassName() {
            return MEMBER_CLASS_NAME;
        }
    }

}
