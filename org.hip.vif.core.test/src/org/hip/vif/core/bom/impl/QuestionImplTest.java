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
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author: Benno Luthiger */
public class QuestionImplTest {
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
        final String lQuestion1 = "Question1";
        final String lRemark1 = "Remark1";
        final String lQuestion2 = "Question2";
        final String lRemark2 = "Remark2";

        final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
        final int lInitialQuestions = lQuestionHome.getCount();
        final int lInitialAuthors = BOMHelper.getQuestionAuthorReviewerHome().getCount();
        final int lInitialHierarchy = BOMHelper.getQuestionHierarchyHome().getCount();
        final Long lGroupID = BOMHelper.getGroupHome().createNew("testGroup1", "Group for testing", "1", "2", "3",
                false);

        Question lQuestion = (Question) BOMHelper.getQuestionHome().create();
        Long lQuestionID = lQuestion.ucNew(lQuestion1, lRemark1, 0l, lGroupID.toString(), new Long(77));

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
        lQuestion = (Question) lQuestionHome.findByKey(lKey);
        final Long lQuestionID1 = new Long(lQuestion.get(QuestionHome.KEY_ID).toString());

        assertEquals("count questions 1", lInitialQuestions + 1, lQuestionHome.getCount());
        assertEquals("count authors 1", lInitialAuthors + 1, BOMHelper.getQuestionAuthorReviewerHome().getCount());
        assertEquals("count hierarchy 1", lInitialHierarchy, BOMHelper.getQuestionHierarchyHome().getCount());
        assertEquals("root question", QuestionHome.IS_ROOT.toString(), lQuestion.get(QuestionHome.KEY_ROOT_QUESTION)
                .toString());

        lQuestion = (Question) BOMHelper.getQuestionHome().create();
        lQuestionID = lQuestion.ucNew(lQuestion2, lRemark2, lQuestionID1, lGroupID.toString(), new Long(77));

        lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
        lQuestion = (Question) lQuestionHome.findByKey(lKey);

        assertEquals("count questions 2", lInitialQuestions + 2, lQuestionHome.getCount());
        assertEquals("count authors 2", lInitialAuthors + 2, BOMHelper.getQuestionAuthorReviewerHome().getCount());
        assertEquals("count hierarchy 2", lInitialHierarchy + 1, BOMHelper.getQuestionHierarchyHome().getCount());
        assertEquals("not root question", QuestionHome.NOT_ROOT.toString(),
                lQuestion.get(QuestionHome.KEY_ROOT_QUESTION).toString());
    }

    @Test
    public void testUcSave() throws Exception {
        final String lQuestion1 = "Question1";
        final String lRemark1 = "Remark1";
        final String lQuestion2 = "Question2";
        final String lRemark2 = "Remark2";
        final Long lAuthor1 = new Long(77);
        final Long lAuthor2 = new Long(7);

        final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
        final QuestionAuthorReviewerHome lQARHome = BOMHelper.getQuestionAuthorReviewerHome();
        final int lInitialHistory = BOMHelper.getQuestionHistoryHome().getCount();
        final Long lGroupID = BOMHelper.getGroupHome().createNew("testGroup1", "Group for testing", "1", "2", "3",
                false);

        Question lQuestion = (Question) lQuestionHome.create();
        final Long lQuestionID = lQuestion.ucNew(lQuestion1, lRemark1, 0l, lGroupID.toString(), lAuthor1);

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
        lQuestion = (Question) lQuestionHome.findByKey(lKey);

        assertEquals("question 1", lQuestion1, lQuestion.get(QuestionHome.KEY_QUESTION));

        lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor1);
        lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestion.get(QuestionHome.KEY_ID));

        DomainObject lQAR = lQARHome.findByKey(lKey);
        assertNotNull("question-author-reviewer 1", lQAR);

        lQuestion.ucSave(lQuestion2, lRemark2, "3", lAuthor2);
        assertEquals("question 2", lQuestion2, lQuestion.get(QuestionHome.KEY_QUESTION));

        assertEquals("count history 1", lInitialHistory + 1, BOMHelper.getQuestionHistoryHome().getCount());
        try {
            lQAR = lQARHome.findByKey(lKey);
            fail("shouldn't get here 1");
        } catch (final BOMNotFoundException exc) {
            // left empty intentionally
        }

        lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthor2);
        lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestion.get(QuestionHome.KEY_ID));

        lQAR = lQARHome.findByKey(lKey);
        assertNotNull("question-author-reviewer 2", lQAR);

        // save again with identical values, therefore, no update is made
        lQuestion.ucSave(lQuestion2, lRemark2, "3", lAuthor1);

        assertEquals("count history 2", lInitialHistory + 1, BOMHelper.getQuestionHistoryHome().getCount());

        lQAR = lQARHome.findByKey(lKey);
        assertNotNull("question-author-reviewer 3", lQAR);
    }

}
