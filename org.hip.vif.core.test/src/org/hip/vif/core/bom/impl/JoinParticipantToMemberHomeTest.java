package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.JoinParticipantToMemberHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Participant;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.exc.NoReviewerException;
import org.hip.vif.core.interfaces.IReviewable;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** Created on 14.08.2003
 * 
 * @author Luthiger */
public class JoinParticipantToMemberHomeTest {
    private static DataHouseKeeper data;

    @SuppressWarnings("serial")
    private class JoinParticipantToMemberHomeSub extends JoinParticipantToMemberHome {
        @Override
        public Vector<Object> createTestObjects() {
            final Vector<Object> outTest = new Vector<Object>();
            try {
                final KeyObject lKey = new KeyObjectImpl();
                lKey.setValue(ParticipantHome.KEY_GROUP_ID, new Integer(32));
                outTest.add(createSelectString(lKey));
            } catch (final VException exc) {
                fail(exc.getMessage());
            }
            return outTest;
        }

        @Override
        public VIFMember randomIteration(final KeyObject inKey, final int inNumberOfParticipants,
                final Long inAuthorID, final Collection<IReviewable> inContributions, final Long inGroupID)
                throws VException, SQLException, NoReviewerException {
            return super.randomIteration(inKey, inNumberOfParticipants, inAuthorID, inContributions, inGroupID);
        }
    }

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
    public void testObjects() {
        String lExpected = "";
        if (data.isDBMySQL()) {
            lExpected = "SELECT tblParticipant.GROUPID, tblParticipant.DTSUSPENDFROM, tblParticipant.DTSUSPENDTO, tblMember.MEMBERID, tblMember.SUSERID, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL, tblMember.BSEX FROM tblParticipant INNER JOIN tblMember ON tblParticipant.MEMBERID = tblMember.MEMBERID WHERE tblParticipant.GROUPID = 32";
        }
        else if (data.isDBOracle()) {
            lExpected = "";
        }
        final JoinParticipantToMemberHome lSubHome = new JoinParticipantToMemberHomeSub();
        final Iterator<Object> lTest = lSubHome.getTestObjects();
        assertEquals("test object", lExpected, lTest.next());
    }

    @Test
    public void testSelectActive() throws Exception {
        int lCount = data.getGroupHome().getCount();
        final Long[] lGroupIDs = data.create2Groups();
        assertEquals("count 1", lCount + 2, data.getGroupHome().getCount());

        lCount = data.getMemberHome().getCount();
        final String lMemberID1 = data.createMember("1");
        final String lMemberID2 = data.createMember("2");
        final String lMemberID3 = data.createMember("3");
        final String lMemberID4 = data.createMember("4");
        final String lMemberID5 = data.createMember("5");
        assertEquals("count 2", lCount + 5, data.getMemberHome().getCount());

        final ParticipantHome lHome = data.getParticipantHome();
        lCount = lHome.getCount();
        lHome.create(lMemberID1, lGroupIDs[0].toString());
        lHome.create(lMemberID2, lGroupIDs[0].toString());
        lHome.create(lMemberID3, lGroupIDs[0].toString());
        lHome.create(lMemberID4, lGroupIDs[0].toString());
        lHome.create(lMemberID3, lGroupIDs[1].toString());
        lHome.create(lMemberID4, lGroupIDs[1].toString());
        lHome.create(lMemberID5, lGroupIDs[1].toString());
        assertEquals("count 3", lCount + 7, data.getParticipantHome().getCount());

        String[] lExpected = new String[] { lMemberID1, lMemberID2, lMemberID3, lMemberID4 };
        data.checkQueryResult(lExpected, data.getJoinParticipantToMemberHome().selectActive(new Long(lGroupIDs[0])),
                MemberHome.KEY_ID, "group 1");
        lExpected = new String[] { lMemberID3, lMemberID4, lMemberID5 };
        data.checkQueryResult(lExpected, data.getJoinParticipantToMemberHome().selectActive(new Long(lGroupIDs[1])),
                MemberHome.KEY_ID, "group 2");

        final Timestamp lFrom = new Timestamp(System.currentTimeMillis() - 100000000);
        final Timestamp lTo = new Timestamp(System.currentTimeMillis() + 100000000);
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ParticipantHome.KEY_MEMBER_ID, new Integer(lMemberID5));
        lKey.setValue(ParticipantHome.KEY_GROUP_ID, lGroupIDs[1]);
        ((Participant) data.getParticipantHome().findByKey(lKey)).suspend(lFrom, lTo);
        lExpected = new String[] { lMemberID3, lMemberID4 };
        data.checkQueryResult(lExpected, data.getJoinParticipantToMemberHome().selectActive(new Long(lGroupIDs[1])),
                MemberHome.KEY_ID, "suspended in group 2");
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

        final Collection<String> lMails = data.getJoinParticipantToMemberHome().getParticipantsMail(new Long(lGroupID));
        assertEquals("number of mails", 4, lMails.size());
        for (int i = 0; i < lExpected.length; i++) {
            assertTrue("contains " + String.valueOf(i), lMails.contains(lExpected[i]));
        }
    }

    // number of participants < THRESHOLD
    @Test
    public void testGetRandomParticipant() throws Exception {
        final Long lGroupID = data.createGroup();
        final String[] lMemberIDs = data.create3Members();
        final Long lAuthorID = new Long(lMemberIDs[0]);
        final Collection<String> lPotential = new Vector<String>();
        lPotential.add(lMemberIDs[1]);
        lPotential.add(lMemberIDs[2]);

        data.getParticipantHome().create(lMemberIDs[0], lGroupID.toString());
        data.getParticipantHome().create(lMemberIDs[1], lGroupID.toString());
        data.getParticipantHome().create(lMemberIDs[2], lGroupID.toString());

        final JoinParticipantToMemberHome lHome = data.getJoinParticipantToMemberHome();
        VIFMember lReviewer;

        // note: JoinParticipantToMemberHome.getRandomParticipant() may occasionally throw a false NoReviewerException
        // in case of very few participants.
        try {
            lReviewer = lHome.getRandomParticipant(new Long(lGroupID), lAuthorID, new Vector<IReviewable>());
            final String lReviewerID = lReviewer.getMemberID().toString();
            assertTrue(lPotential.contains(lReviewerID));
            assertFalse(lReviewerID.equals(lMemberIDs[0]));
        } catch (final NoReviewerException exc) {
            // intentionally left empty
        }

        // now we set the participants suspend date to provoke a NoReviewerException
        final Timestamp lFrom = new Timestamp(System.currentTimeMillis() - 3600000);
        final Timestamp lTo = new Timestamp(System.currentTimeMillis() + 3600000);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[1]), lFrom, lTo);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[2]), lFrom, lTo);
        try {
            lHome.getRandomParticipant(new Long(lGroupID), lAuthorID, new Vector<IReviewable>());
            fail("shouldn't get here");
        } catch (final NoReviewerException exc) {
            // intentionally left empty
        }

        // test with reviewers that refused
        final Timestamp lZero = new Timestamp(1000);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[1]), lZero, lZero);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[2]), lZero, lZero);
        // contribution a member refused to review
        final Collection<IReviewable> lContributions = new Vector<IReviewable>();
        lContributions.add(createContribution(new Long(lMemberIDs[2])));
        try {
            lReviewer = lHome.getRandomParticipant(new Long(lGroupID), lAuthorID, lContributions);
            assertEquals(lMemberIDs[1], lReviewer.getMemberID().toString());
        } catch (final NoReviewerException exc) {
            // intentionally left empty
        }
    }

    // number of participants > THRESHOLD
    @Test
    public void testGetRandomParticipant2() throws Exception {
        final Long lGroupID = data.createGroup();
        final String[] lMemberIDs = data.create3Members();
        final Long lAuthorID = new Long(lMemberIDs[0]);
        final Collection<String> lPotential = new Vector<String>();
        lPotential.add(lMemberIDs[1]);
        lPotential.add(lMemberIDs[2]);

        data.getParticipantHome().create(lMemberIDs[0], lGroupID.toString());
        data.getParticipantHome().create(lMemberIDs[1], lGroupID.toString());
        data.getParticipantHome().create(lMemberIDs[2], lGroupID.toString());

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ParticipantHome.KEY_GROUP_ID, lGroupID);
        lKey.setValue(getKeyActive());

        final JoinParticipantToMemberHomeSub lHome = new JoinParticipantToMemberHomeSub();
        VIFMember lReviewer = lHome.randomIteration(lKey, lHome.getCount(lKey), lAuthorID, new Vector<IReviewable>(),
                new Long(lGroupID));
        final String lReviewerID = lReviewer.getMemberID().toString();
        assertTrue(lPotential.contains(lReviewerID));
        assertFalse(lReviewerID.equals(lMemberIDs[0]));

        // now we set the participants suspend date to provoke a NoReviewerException
        final Timestamp lFrom = new Timestamp(System.currentTimeMillis() - 3600000);
        final Timestamp lTo = new Timestamp(System.currentTimeMillis() + 3600000);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[1]), lFrom, lTo);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[2]), lFrom, lTo);
        try {
            lHome.randomIteration(lKey, lHome.getCount(lKey), lAuthorID, new Vector<IReviewable>(), new Long(lGroupID));
            fail("shouldn't get here");
        } catch (final NoReviewerException exc) {
            // intentionally left empty
        }

        // test with reviewers that refused
        final Timestamp lZero = new Timestamp(1000);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[1]), lZero, lZero);
        data.getParticipantHome().suspendParticipation(new Long(lMemberIDs[2]), lZero, lZero);
        // contribution a member refused to review
        final Collection<IReviewable> lContributions = new Vector<IReviewable>();
        lContributions.add(createContribution(new Long(lMemberIDs[2])));
        lReviewer = lHome.randomIteration(lKey, lHome.getCount(lKey), lAuthorID, lContributions, new Long(lGroupID));
        assertEquals(lMemberIDs[1], lReviewer.getMemberID().toString());
    }

    private IReviewable createContribution(final Long inReviewerID) throws VException, SQLException {
        final Long lContributionID = new Long(data.createQuestion("Test contribution", "1:1"));
        final QuestionAuthorReviewerHome lHome = BOMHelper.getQuestionAuthorReviewerHome();
        lHome.setReviewer(inReviewerID, lContributionID);
        lHome.removeReviewer(inReviewerID, lContributionID);
        return (IReviewable) data.getQuestionHome().getQuestion(lContributionID.toString());
    }

    private KeyObject getKeyActive() throws VException {
        final Timestamp lNow = new Timestamp(System.currentTimeMillis());
        final KeyObject outKey = new KeyObjectImpl();
        outKey.setValue(ParticipantHome.KEY_SUSPEND_FROM, lNow, ">", BinaryBooleanOperator.OR);
        outKey.setValue(ParticipantHome.KEY_SUSPEND_TO, lNow, "<", BinaryBooleanOperator.OR);
        return outKey;
    }

}
