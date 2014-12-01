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
package org.hip.vif.web.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.hip.vif.web.bom.VifBOMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** Note: this test is an taken from org.hip.vif.core.test/org.hip.vif.core.bom.impl.CompletionImplTest because the test
 * needs a WorkflowAware group.
 *
 * @author lbenno */
public class CompletionImplTest {
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
    public void testIndexing() throws Exception {
        final Object[] lActorID = new Object[] { new Long(96) };

        // preparation: create group and open it
        final Long lGroupID = data.createGroup();
        final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
        ((WorkflowAware) lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN,
                new Object[] { new GroupStateChangeParameters() });

        // preparation: create two members who will be the authors of the question
        final String[] lAuthorIDs = data.create2Members();

        // preparation: create new question, link it to the admin and open it
        Long lQuestionID = data.createQuestion("Question1", "2:6.4", lGroupID, WorkflowAwareContribution.S_PRIVATE,
                false);
        data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[0]), true);
        Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
        assertEquals("number of indexed 1", 1, IndexHouseKeeper.countIndexedContents());

        // now, we create the completion
        String lCompletionText = "New completion 1.";
        final CompletionHome lHome = data.getCompletionHome();
        Completion lCompletion = (Completion) lHome.create();
        Long lCompletionID = lCompletion.ucNew(lCompletionText, lQuestionID.toString(), new Long(lAuthorIDs[1]));

        lCompletion = getCompletion(lCompletionID, lQuestionID.toString(), lHome);
        ((WorkflowAware) lCompletion).doTransition(WorkflowAwareContribution.TRANS_REQUEST, lActorID);
        data.deleteAllFromCompletionHistory();
        ((WorkflowAware) lCompletion).doTransition(WorkflowAwareContribution.TRANS_ACCEPT, lActorID);
        data.deleteAllFromCompletionHistory();
        assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());

        // the new completion is indexed after it is published
        ((WorkflowAware) lCompletion).doTransition(WorkflowAwareContribution.TRANS_PUBLISH, lActorID);
        data.deleteAllFromCompletionHistory();
        assertEquals("number of indexed 3", 2, IndexHouseKeeper.countIndexedContents());

        // deleting the question deletes the index entry of the completion too
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE1, lActorID);
        assertEquals("number of indexed 4", 0, IndexHouseKeeper.countIndexedContents());

        // now we create a question and add a completion before we publish them
        lQuestionID = data.createQuestion("Question2", "2:6.5", new Long(lGroupID),
                WorkflowAwareContribution.S_PRIVATE, false);
        data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[1]), true);
        lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
        lCompletionText = "New completion 2.";
        lCompletion = (Completion) lHome.create();
        lCompletionID = lCompletion.ucNew(lCompletionText, lQuestionID.toString(), new Long(lAuthorIDs[1]));
        assertEquals("number of indexed 5", 0, IndexHouseKeeper.countIndexedContents());

        // publication of both question and completion as group admin
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
        lCompletion = getCompletion(lCompletionID, lQuestionID.toString(), lHome);
        ((WorkflowAware) lCompletion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
        assertEquals("number of indexed 6", 2, IndexHouseKeeper.countIndexedContents());

        // delete both question and completion
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE2, lActorID);
        assertEquals("number of indexed 7", 0, IndexHouseKeeper.countIndexedContents());
    }

    private Completion getCompletion(final Long inCompletionID, final String inQuestionID, final CompletionHome inHome)
            throws VException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
        lKey.setValue(CompletionHome.KEY_QUESTION_ID, new Long(inQuestionID));
        return (Completion) inHome.findByKey(lKey);
    }

}
