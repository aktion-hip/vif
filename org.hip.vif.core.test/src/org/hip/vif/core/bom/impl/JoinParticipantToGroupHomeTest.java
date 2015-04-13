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

import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.util.StringTokenizer;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author lbenno */
public class JoinParticipantToGroupHomeTest {
    private static DataHouseKeeper data;

    private static final String DFT_SORT = GroupHome.KEY_NAME + ", " + GroupHome.KEY_ID;

    // private static final String DFT_SORT = GroupHome.KEY_NAME + ", " + NestedGroupHome.KEY_GROUP_ID;

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
    public void test() throws VException, SQLException {

        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ParticipantHome.KEY_MEMBER_ID, 1l);
        lKey.setValue(createKey(VIFGroupWorkflow.ENLISTABLE_STATES), BinaryBooleanOperator.AND);
        final JoinParticipantToGroupHome lGroupHome = BOMHelper.getJoinParticipantToGroupHome();
        final QueryResult lResult = lGroupHome.select(lKey, createOrder(DFT_SORT, false));
        assertFalse(lResult.hasMoreElements());

    }

    private KeyObject createKey(final Integer... inStates) throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        for (int i = 0; i < inStates.length; i++) {
            outKey.setValue(GroupHome.KEY_STATE, inStates[i], "=", BinaryBooleanOperator.OR); //$NON-NLS-1$
        }
        return outKey;
    }

    private OrderObject createOrder(final String inOrder,
            final boolean inDescending) throws VException {
        final OrderObject outOrder = new OrderObjectImpl();
        final StringTokenizer lTokens = new StringTokenizer(inOrder, ","); //$NON-NLS-1$
        int i = 0; // NOPMD
        while (lTokens.hasMoreTokens()) {
            outOrder.setValue(lTokens.nextToken().trim(), inDescending, i++);
        }
        return outOrder;
    }

}
