package org.hip.vif.core.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.impl.JoinCompletionToQuestion;
import org.hip.vif.core.bom.impl.JoinQuestionToChild;
import org.hip.vif.core.bom.impl.JoinSubscriptionToMember;
import org.hip.vif.core.bom.impl.QuestionHierarchyVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 19.10.2010 */
public class QuestionHierachyAdapterTest {
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
        data.deleteAllFromQuestion();
        data.deleteAllFromCompletion();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testAdapt() throws Exception {
        final Long lQuestionID = data.createQuestion("Test question", "1:1");
        final Long lCompletionID = data.createCompletion("Test completion", lQuestionID);

        final TestVisitor lVisitor = new TestVisitor();

        QuestionHierachyAdapter lAdapter = new QuestionHierachyAdapter(data.getQuestionHome().getQuestion(lQuestionID));
        lAdapter.accept(lVisitor);

        lAdapter = new QuestionHierachyAdapter(data.getCompletionHome().getCompletion(lCompletionID));
        lAdapter.accept(lVisitor);

        lAdapter = new QuestionHierachyAdapter(null);
        lAdapter.accept(lVisitor);

        lAdapter = new QuestionHierachyAdapter("Test");
        lAdapter.accept(lVisitor);

        lAdapter = new QuestionHierachyAdapter(this);
        lAdapter.accept(lVisitor);

        final Collection<String> lVisited = lVisitor.getVisited();
        assertEquals(2, lVisited.size());
        assertTrue(lVisited.contains("org.hip.vif.core.bom.impl.CompletionImpl"));
        assertTrue(lVisited.contains("org.hip.vif.core.bom.impl.QuestionImpl"));
    }

    // ---

    private class TestVisitor implements QuestionHierarchyVisitor {
        private final Collection<String> visited = new Vector<String>();

        TestVisitor() {
        }

        @Override
        public void visitCompletion(final Completion inCompletion) throws VException, SQLException {
            visited.add(inCompletion.getClass().getName());
        }

        @Override
        public void visitCompletion(final JoinCompletionToQuestion inCompletion) throws VException, SQLException {
            visited.add(inCompletion.getClass().getName());
        }

        @Override
        public void visitQuestion(final Question inQuestion) throws VException, SQLException {
            visited.add(inQuestion.getClass().getName());
        }

        @Override
        public void visitText(final Text inText) throws VException, SQLException {
            visited.add(inText.getClass().getName());
        }

        @Override
        public void visitSubscriber(final JoinSubscriptionToMember inSubscriber) throws VException, SQLException {
            visited.add(inSubscriber.getClass().getName());
        }

        @Override
        public void visitChild(final JoinQuestionToChild inChild) throws VException, SQLException {
            visited.add(inChild.getClass().getName());
        }

        Collection<String> getVisited() {
            return visited;
        }
    }

}
