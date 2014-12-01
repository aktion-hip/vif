package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHistoryHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** Created on 29.05.2003
 *
 * @author Luthiger */
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
    public void testUcNew() throws Exception {
        final CompletionHome lCompletionHome = data.getCompletionHome();
        final int lInitialCompletions = lCompletionHome.getCount();
        final int lInitialAuthors = BOMHelper.getCompletionAuthorReviewerHome().getCount();

        final Completion lCompletion = (Completion) lCompletionHome.create();
        lCompletion.ucNew("New contribution.", "66", new Long(77));

        assertEquals("count contribution", lInitialCompletions + 1, lCompletionHome.getCount());
        assertEquals("count authors", lInitialAuthors + 1, BOMHelper.getCompletionAuthorReviewerHome().getCount());
    }

    @Test
    public void testUcSave() throws Exception {
        final String lCompletion1 = "Contribution1";
        final String lCompletion2 = "Contribution2";
        final Long lAuthor1 = new Long(77);
        final Long lAuthor2 = new Long(7);

        final CompletionHome lCompletionHome = BOMHelper.getCompletionHome();
        final CompletionAuthorReviewerHome lCARHome = BOMHelper.getCompletionAuthorReviewerHome();
        final CompletionHistoryHome lHistoryHome = BOMHelper.getCompletionHistoryHome();
        final int lInitialHistories = lHistoryHome.getCount();

        Completion lCompletion = (Completion) lCompletionHome.create();
        final Long lCompletionID = lCompletion.ucNew(lCompletion1, "21", lAuthor1);

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
        lCompletion = (Completion) lCompletionHome.findByKey(lKey);

        assertEquals("contribution 1", lCompletion1, lCompletion.get(CompletionHome.KEY_COMPLETION));

        lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor1);
        lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletion.get(CompletionHome.KEY_ID));

        DomainObject lCAR = lCARHome.findByKey(lKey);
        assertNotNull("contribution-author-reviewer 1", lCAR);

        lCompletion.ucSave(lCompletion2, "2", lAuthor2);
        assertEquals("count history 1", lInitialHistories + 1, lHistoryHome.getCount());
        try {
            lCAR = lCARHome.findByKey(lKey);
            fail("shouldn't get here 1");
        } catch (final BOMNotFoundException exc) {
            // left empty intentionally
        }

        lKey = new KeyObjectImpl();
        lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
        lCompletion = (Completion) lCompletionHome.findByKey(lKey);
        assertEquals("contribution 2", lCompletion2, lCompletion.get(CompletionHome.KEY_COMPLETION));

        lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor2);
        lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletion.get(CompletionHome.KEY_ID));

        lCAR = lCARHome.findByKey(lKey);
        assertNotNull("contribution-author-reviewer 2", lCAR);

        // save again with identical values, therefore, no update is made
        lCompletion.ucSave(lCompletion2, "2", lAuthor2);

        assertEquals("count history 2", lInitialHistories + 1, lHistoryHome.getCount());
        lCAR = lCARHome.findByKey(lKey);
        assertNotNull("contribution-author-reviewer 3", lCAR);
    }

    @Test
    public void testGetOwningQuestion() throws Exception {
        final String lExpected1 = "1";
        final String lExpected2 = "1.1";

        final int lCountQuestions = data.getQuestionHome().getCount();
        final Long lQuestionID1 = data.createQuestion("question 1", lExpected1);
        final Long lQuestionID2 = data.createQuestion("question 2", lExpected2);
        assertEquals("count questions", lCountQuestions + 2, data.getQuestionHome().getCount());

        final CompletionHome lHome = data.getCompletionHome();
        final int lCountCompletions = lHome.getCount();
        final Long lCompletionID1 = data.createCompletion("completion 1", lQuestionID1);
        final Long lCompletionID2 = data.createCompletion("completion 2", lQuestionID1);
        final Long lCompletionID3 = data.createCompletion("completion 3", lQuestionID1);
        final Long lCompletionID4 = data.createCompletion("completion 4", lQuestionID2);
        assertEquals("count completions", lCountCompletions + 4, lHome.getCount());

        checkOwningQuestion(lCompletionID1, lExpected1, "question 1 owns completion 1");
        checkOwningQuestion(lCompletionID2, lExpected1, "question 1 owns completion 2");
        checkOwningQuestion(lCompletionID3, lExpected1, "question 1 owns completion 3");
        checkOwningQuestion(lCompletionID4, lExpected2, "question 2 owns completion 4");
    }

    private void checkOwningQuestion(final Long inCompletionID, final String inExpected, final String inAssert) {
        try {
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
            final DomainObject lQuestion = ((Completion) data.getCompletionHome().findByKey(lKey)).getOwningQuestion();
            assertEquals(inAssert, inExpected, lQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL));
        } catch (final Exception exc) {
            fail(exc.getMessage());
        }
    }

}
