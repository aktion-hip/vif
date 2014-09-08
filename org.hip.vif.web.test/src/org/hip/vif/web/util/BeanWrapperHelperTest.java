/**
    This package is part of the persistency layer of the application VIF.
    Copyright (C) 2003-2014, Benno Luthiger

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
package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author lbenno */
public class BeanWrapperHelperTest {
    private static DataHouseKeeper data;
    private static final Long TIME = 1070000000000l;

    private String memberID;

    @Before
    public void setUp() throws Exception {
        IndexHouseKeeper.redirectDocRoot(false);

        memberID = data.createMember("testUser", "test@vif.org");
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromMember();
        data.deleteAllFromLinkMemberRole();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public final void testGetValues() throws Exception {
        final Member lMember = data.getMemberHome().getMember(memberID);
        assertEquals("test@vif.org", BeanWrapperHelper.getString(MemberHome.KEY_MAIL, lMember));
        assertEquals("NameTtestUser", BeanWrapperHelper.getString(MemberHome.KEY_NAME, lMember));
        assertEquals(new Long(memberID), BeanWrapperHelper.getLong(MemberHome.KEY_ID, lMember));
        assertEquals(new Integer(1), BeanWrapperHelper.getInteger(MemberHome.KEY_SEX, lMember));

        // retrieving values with false key
        assertEquals("", BeanWrapperHelper.getString("no_key", lMember));
        assertEquals(new Long(0), BeanWrapperHelper.getLong("no_key", lMember));
        assertEquals(new Integer(0), BeanWrapperHelper.getInteger("no_key", lMember));

        // formatted date
        final Timestamp lTime = new Timestamp(TIME);
        lMember.set(MemberHome.KEY_MUTATION, lTime);
        assertEquals("28.11.2003", BeanWrapperHelper.getFormattedDate(MemberHome.KEY_MUTATION, lMember));
    }

}
