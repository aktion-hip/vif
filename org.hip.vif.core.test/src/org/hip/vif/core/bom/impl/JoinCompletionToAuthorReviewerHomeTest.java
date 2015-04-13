/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author lbenno */
public class JoinCompletionToAuthorReviewerHomeTest {
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
    public void testGetAuthors() throws Exception {
        final String[] lContained = new String[2];
        final Long lCompletionID = Long.valueOf(42);
        final String[] lMemberIDs = data.create3Members();
        final JoinCompletionToAuthorReviewerHome lJoinHome = data.getJoinCompletionToAuthorReviewerHome();
        QueryResult lResult = lJoinHome.getAuthors(lCompletionID.toString());

        assertNull("Check before", lResult.next());

        data.createCompletionProducer(lCompletionID, Long.parseLong(lMemberIDs[0]), true);
        lContained[0] = retrieveUserID(lMemberIDs[0]);
        lResult = lJoinHome.getAuthors(lCompletionID.toString());
        assertEquals(1, countEntries(lResult));

        data.createCompletionProducer(lCompletionID, Long.parseLong(lMemberIDs[1]), false);
        final String lNotContained = retrieveUserID(lMemberIDs[1]);
        lResult = lJoinHome.getAuthors(lCompletionID.toString());
        assertEquals(1, countEntries(lResult));

        data.createCompletionProducer(lCompletionID, Long.parseLong(lMemberIDs[2]), true);
        lContained[1] = retrieveUserID(lMemberIDs[2]);
        lResult = lJoinHome.getAuthors(lCompletionID.toString());
        assertEquals(2, countEntries(lResult));

        final Collection<Object> lUserIDs = new ArrayList<Object>();
        lResult = lJoinHome.getAuthors(lCompletionID.toString());
        while (lResult.hasMoreElements()) {
            lUserIDs.add(lResult.next().get(MemberHome.KEY_USER_ID));
        }

        assertTrue(lUserIDs.contains(lContained[0]));
        assertTrue(lUserIDs.contains(lContained[1]));
        assertFalse(lUserIDs.contains(lNotContained));
    }

    private int countEntries(final QueryResult lResult) {
        int outCount = 0;
        try {
            while (lResult.hasMoreElements()) {
                lResult.next();
                outCount++;
            }
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
        return outCount;
    }

    private String retrieveUserID(final String inMemberID) throws Exception {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(MemberHome.KEY_ID, new Integer(inMemberID));
        return (String) data.getMemberHome().findByKey(lKey).get(MemberHome.KEY_USER_ID);
    }

}
