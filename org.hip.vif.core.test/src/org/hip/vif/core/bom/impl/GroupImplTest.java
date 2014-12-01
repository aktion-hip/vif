package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import javax.mail.internet.InternetAddress;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.AssertionFailedError;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** GroupImplTest.java
 *
 *
 * @author Benno Luthiger */
public class GroupImplTest {
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
        final GroupHome lHome = data.getGroupHome();
        final String lGroupID = "testGroup";

        final int lNumberBefore = lHome.getCount();
        Group lGroup = (Group) lHome.create();

        // normal creation
        lGroup.ucNew(lGroupID, "Group for testing", "1", "2", "3", false);
        assertEquals("new 1", lNumberBefore + 1, lHome.getCount());

        // missing group ID
        lGroup = (Group) lHome.create();
        try {
            lGroup.ucNew("", "Group for testing", "1", "2", "3", false);
            fail("new 2: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // missing group name
        lGroup = (Group) lHome.create();
        try {
            lGroup.ucNew(lGroupID + "1", "", "1", "2", "3", false);
            fail("new 3: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // both missing
        lGroup = (Group) lHome.create();
        try {
            lGroup.ucNew("", "", "1", "2", "3", false);
            fail("new 4: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // create group with group ID which exists yet
        lGroup = (Group) lHome.create();
        try {
            lGroup.ucNew(lGroupID, "Group for testing 2", "1", "2", "3", false);
            fail("new 5: shouldn't get here");
        } catch (final ExternIDNotUniqueException exc) {
            // left blank intentionally
        }
        assertEquals("number 2", lNumberBefore + 1, lHome.getCount());

        lGroup = (Group) lHome.create();
        lGroup.ucNew(lGroupID + "2", "Group for testing 2", "1", "2", "3", false);
        assertEquals("number 3", lNumberBefore + 2, lHome.getCount());
    }

    @Test
    public void testNew2() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        final String lGroupID = "testGroup";

        final int lNumberBefore = lHome.getCount();
        Group lGroup = (Group) lHome.create();
        // normal creation
        lGroup.set(GroupHome.KEY_NAME, lGroupID);
        lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing");
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        lGroup.ucNew();
        assertEquals("new 1", lNumberBefore + 1, lHome.getCount());

        // missing group ID
        lGroup = (Group) lHome.create();
        lGroup.set(GroupHome.KEY_NAME, ""); // empty mandatory field
        lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing");
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        try {
            lGroup.ucNew();
            fail("new 2: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // missing group name
        lGroup = (Group) lHome.create();
        lGroup.set(GroupHome.KEY_NAME, lGroupID + "1");
        lGroup.set(GroupHome.KEY_DESCRIPTION, ""); // empty mandatory field
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        try {
            lGroup.ucNew();
            fail("new 3: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // both missing
        lGroup = (Group) lHome.create();
        lGroup.set(GroupHome.KEY_NAME, ""); // empty mandatory field
        lGroup.set(GroupHome.KEY_DESCRIPTION, ""); // empty mandatory field
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        try {
            lGroup.ucNew();
            fail("new 4: shouldn't get here");
        } catch (final AssertionFailedError err) {
            // left blank intentionally
        }

        // create group with group ID which exists yet
        lGroup = (Group) lHome.create();
        lGroup.set(GroupHome.KEY_NAME, lGroupID); // existing group name
        lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing 2");
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        try {
            lGroup.ucNew();
            fail("new 5: shouldn't get here");
        } catch (final ExternIDNotUniqueException exc) {
            // left blank intentionally
        }
        assertEquals("number 2", lNumberBefore + 1, lHome.getCount());

        lGroup = (Group) lHome.create();
        lGroup.set(GroupHome.KEY_NAME, lGroupID + "2");
        lGroup.set(GroupHome.KEY_DESCRIPTION, "Group for testing 2");
        lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
        lGroup.set(GroupHome.KEY_GUEST_DEPTH, 2l);
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 3l);
        lGroup.set(GroupHome.KEY_PRIVATE, 0l);
        lGroup.ucNew();
        assertEquals("number 3", lNumberBefore + 2, lHome.getCount());
    }

    @Test
    public void testRootCount() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        final QuestionHome lQuestionHome = data.getQuestionHome();

        final int lGroups0 = lHome.getCount();
        final Long lGroupID = data.createGroup();
        assertEquals("added group for count test", lGroups0 + 1, lHome.getCount());

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(GroupHome.KEY_ID, lGroupID);
        final Group lGroup = (Group) lHome.findByKey(lKey);

        final int lLinks0 = lQuestionHome.getCount();
        assertEquals("number of roots 0", 0, lGroup.rootCount());

        insertQuestion(lGroupID, 1, false);
        assertEquals("number of links 1", lLinks0 + 1, lQuestionHome.getCount());
        assertEquals("number of roots 1", 0, lGroup.rootCount());

        insertQuestion(lGroupID, 2, true);
        assertEquals("number of links 2", lLinks0 + 2, lQuestionHome.getCount());
        assertEquals("number of roots 2", 1, lGroup.rootCount());

        insertQuestion(lGroupID, 3, false);
        assertEquals("number of links 3", lLinks0 + 3, lQuestionHome.getCount());
        assertEquals("number of roots 3", 1, lGroup.rootCount());

        insertQuestion(lGroupID, 4, true);
        assertEquals("number of links 4", lLinks0 + 4, lQuestionHome.getCount());
        assertEquals("number of roots 4", 2, lGroup.rootCount());
    }

    private void insertQuestion(final Long inGroupID, final int inQuestionID, final boolean isRoot) {
        try {
            final DomainObject lQuestion = BOMHelper.getQuestionHome().create();

            // lQuestion.set(QuestionHome.KEY_ID, new Long(inQuestionID));
            lQuestion.set(QuestionHome.KEY_GROUP_ID, inGroupID);
            lQuestion.set(QuestionHome.KEY_ROOT_QUESTION, (isRoot ? new Long(1) : new Long(0)));
            lQuestion.set(QuestionHome.KEY_QUESTION, "Question");
            lQuestion.set(QuestionHome.KEY_QUESTION_DECIMAL, "1.1");
            lQuestion.set(QuestionHome.KEY_REMARK, "Remark");
            lQuestion.insert(true);
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testGetCloseTransition() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        final Group lGroup = (Group) lHome.create();

        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_OPEN));
        assertEquals("transition 1", VIFGroupWorkflow.TRANS_CLOSE1, lGroup.getCloseTransition());

        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_ACTIVE));
        assertEquals("transition 2", VIFGroupWorkflow.TRANS_CLOSE2, lGroup.getCloseTransition());

        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SETTLED));
        assertEquals("transition 3", VIFGroupWorkflow.TRANS_CLOSE3, lGroup.getCloseTransition());
    }

    @Test
    public void testGetReactivateTransition() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        Group lGroup = (Group) lHome.create();

        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SUSPENDED));
        assertEquals("transition 1", VIFGroupWorkflow.TRANS_REACTIVATE1, lGroup.getReactivateTransition());

        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_SETTLED));
        assertEquals("transition 2", VIFGroupWorkflow.TRANS_REACTIVATE2, lGroup.getReactivateTransition());

        final Long lGroupID = data.createGroup();
        create3Participants(lGroupID);

        lGroup = lHome.getGroup(lGroupID);
        lGroup.set(GroupHome.KEY_STATE, new Long(VIFGroupWorkflow.STATE_CLOSED));
        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, new Long(1));
        assertEquals("transition 3", VIFGroupWorkflow.TRANS_REACTIVATE3, lGroup.getReactivateTransition());

        lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, new Long(5));
        assertEquals("transition 4", VIFGroupWorkflow.TRANS_REOPEN, lGroup.getReactivateTransition());
    }

    private void create3Participants(final Long inGroupID) throws VException, SQLException {
        final ParticipantHome lParticipantHome = data.getParticipantHome();

        final DomainObject lParticipant = lParticipantHome.create();
        lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(11));
        lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
        lParticipant.insert(true);

        lParticipant.setVirgin();
        lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(12));
        lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
        lParticipant.insert(true);

        lParticipant.setVirgin();
        lParticipant.set(ParticipantHome.KEY_MEMBER_ID, new Long(13));
        lParticipant.set(ParticipantHome.KEY_GROUP_ID, inGroupID);
        lParticipant.insert(true);
    }

    @Test
    public void testGetParticipantsMail() throws Exception {
        final String[] lExpected = new String[] { "mail1@test", "mail2@test", "mail3@test", "mail4@test" };
        final Long lGroupID = data.createGroup();
        final String lMemberID1 = data.createMember("1", lExpected[0]);
        final String lMemberID2 = data.createMember("2", lExpected[1]);
        final String lMemberID3 = data.createMember("3", lExpected[2]);
        final String lMemberID4 = data.createMember("4", lExpected[3]);

        final ParticipantHome lHome = data.getParticipantHome();
        lHome.create(lMemberID1, lGroupID.toString());
        lHome.create(lMemberID2, lGroupID.toString());
        lHome.create(lMemberID3, lGroupID.toString());
        lHome.create(lMemberID4, lGroupID.toString());

        final InternetAddress[] lMails = data.getGroupHome().getGroup(lGroupID).getParticipantsMail();
        assertEquals("number of mails", 4, lMails.length);
        for (int i = 0; i < lMails.length; i++) {
            assertEquals("mail " + String.valueOf(i), lExpected[i], lMails[i].toString());
        }
    }

    @Test
    public void testIsParticipant() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        final Long lGroupID = data.createGroup();
        final String lMemberID = data.createMember();

        assertFalse("not participant", lHome.getGroup(lGroupID).isParticipant(new Long(lMemberID)));

        data.getParticipantHome().create(lMemberID, lGroupID.toString());
        assertTrue("participant", lHome.getGroup(lGroupID).isParticipant(new Long(lMemberID)));
    }

    @Test
    public void testIsPrivate() throws Exception {
        final GroupHome lHome = data.getGroupHome();
        final Long lGroupID = data.createGroup();
        Group lGroup = lHome.getGroup(lGroupID);
        assertFalse("not private", lGroup.isPrivate());

        lGroup.set(GroupHome.KEY_PRIVATE, new Long(1));
        lGroup.update(true);

        lGroup = lHome.getGroup(lGroupID);
        assertTrue("private", lGroup.isPrivate());
    }

}
