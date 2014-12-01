package org.hip.vif.web.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** <b>Note:</b> this is the workflow aware part of <code>org.hip.vif.core.bom.impl.QuestionImplTest</code>
 *
 * @author: Benno Luthiger */
public class QuestionImplTest {
    private static DataHouseKeeper data;
    private static org.hip.vif.web.DataHouseKeeper dataG;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
        dataG = org.hip.vif.web.DataHouseKeeper.getInstance();
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
        // IndexHouseKeeper.redirectDocRoot(true);

        final Object[] lActorID = new Object[] { new Long(96) };
        assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());

        // preparation: create group and open it
        final Long lGroupID = data.createGroup();
        final Group lGroup = dataG.getGroupHome().getGroup(lGroupID);
        ((WorkflowAware) lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN,
                new Object[] { new GroupStateChangeParameters() });

        // preparation: create two members who will be the authors of the question
        final String[] lAuthorIDs = data.create2Members();

        // create new question and link it to the admin
        Long lQuestionID = data.createQuestion("Question1", "2:5.1", lGroupID, WorkflowAwareContribution.S_PRIVATE,
                false);
        data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[0]), true);
        assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());

        pause(900);
        // the question is indexed after publishing
        Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
        assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());

        // the index entry is deleted after deletion of the question
        lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE2, lActorID);
        assertEquals("number of indexed 3", 0, IndexHouseKeeper.countIndexedContents());

        // create another question and link it to the author
        lQuestionID = data.createQuestion("Question2", "2:5.2", lGroupID, WorkflowAwareContribution.S_PRIVATE, false);
        data.createQuestionProducer(new Long(lQuestionID), new Long(lAuthorIDs[1]), true);
        assertEquals("number of indexed 4", 0, IndexHouseKeeper.countIndexedContents());

        pause(900);
        // request and accept review
        lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_REQUEST, lActorID);
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ACCEPT, lActorID);
        data.deleteAllFromQuestionHistory();
        assertEquals("number of indexed 5", 0, IndexHouseKeeper.countIndexedContents());

        // now we publish the reviewed question
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_PUBLISH, lActorID);
        assertEquals("number of indexed 6", 1, IndexHouseKeeper.countIndexedContents());
        data.deleteAllFromQuestionHistory();

        // setting the question's state to answered and reopening the question doesn't change the indexing
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
        assertEquals("number of indexed 7", 1, IndexHouseKeeper.countIndexedContents());
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_REOPEN1, lActorID);
        data.deleteAllFromQuestionHistory();
        assertEquals("number of indexed 8", 1, IndexHouseKeeper.countIndexedContents());

        // now we delete the answered question
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, lActorID);
        data.deleteAllFromQuestionHistory();
        ((WorkflowAware) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_DELETE1, lActorID);
        assertEquals("number of indexed 9", 0, IndexHouseKeeper.countIndexedContents());
    }

    private void pause(final long inMillis) throws InterruptedException {
        synchronized (this) {
            wait(inMillis);
        }
    }

}
