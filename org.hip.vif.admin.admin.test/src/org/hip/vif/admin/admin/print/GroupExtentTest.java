package org.hip.vif.admin.admin.print;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.hip.vif.markup.serializer.MarkupToHtmlSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 01.01.2012 */
public class GroupExtentTest {
    private static DataHouseKeeper data;
    private static org.hip.vif.web.DataHouseKeeper dataG;

    private final Object[] actorID = new Object[] { new Long(96) };

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
        dataG.deleteAllInAll();
        IndexHouseKeeper.deleteTestIndexDir();
    }

    @Test
    public void testCreation() throws Exception {

        // preparation: create group and open it
        final Long lGroupID = dataG.createGroup();
        final Group lGroup = dataG.getGroupHome().getGroup(lGroupID);
        ((WorkflowAware) lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN,
                new Object[] { new GroupStateChangeParameters() });

        // preparation: create two members who will be the authors of the question
        final String[] lAuthorIDs = data.create2Members();

        // three questions: root and two children
        final String lQuestion1 = "Root question for test.";
        final String lQuestion2 = "For test: First follow up question.";
        final String lQuestion3 = "For test: Second follow up question.";

        final Long lQuestionID1 = createQuestion(lQuestion1, "1:1", lGroupID, true, lAuthorIDs[0]);
        createQuestion(lQuestion2, "1:1.1", lGroupID, false, lAuthorIDs[1]);
        final Long lQuestionID3 = createQuestion(lQuestion3, "1:1.2", lGroupID, false, lAuthorIDs[0]);

        // three completions
        final String lCompletion1 = "Root has one completion.";
        final String lCompletion2 = "Root has another completion.";
        final String lCompletion3 = "A completion for a follow up question.";

        Long lCompletionID = data.createCompletion(lCompletion1, lQuestionID1, WorkflowAwareContribution.S_OPEN);
        data.createCompletionProducer(lCompletionID, new Long(lAuthorIDs[1]), true);

        lCompletionID = data.createCompletion(lCompletion2, lQuestionID1, WorkflowAwareContribution.S_OPEN);
        data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[1]), true);
        data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[0]), false);

        lCompletionID = data.createCompletion(lCompletion3, lQuestionID3, WorkflowAwareContribution.S_OPEN);
        data.createCompletionProducer(new Long(lCompletionID), new Long(lAuthorIDs[0]), true);

        // start the test
        final GroupExtent lExtent = new GroupExtent(new Long(lGroupID));

        final XMLSerializer lSerializer = new MarkupToHtmlSerializer();
        lExtent.getGroup().accept(lSerializer);
        String lSerialized = lSerializer.toString();
        System.out.println(lSerialized);
        assertEquals("Group tag", 2, count(Pattern.compile("<?Group>").matcher(lSerialized)));
        assertEquals("Group description node", 1, count(Pattern
                .compile("<Description><p>Group Nr. 1</p></Description>").matcher(lSerialized)));

        lSerialized = serialize(lExtent.getQuestions(), lSerializer);
        System.out.println(lSerialized);
        assertEquals("number of entries", 3, count(Pattern.compile("<propertySet>").matcher(lSerialized)));

        String lNode = "<Question><p>%s</p></Question>";
        assertEquals("question 1", 1, count(Pattern.compile(String.format(lNode, lQuestion1)).matcher(lSerialized)));
        assertEquals("question 2", 1, count(Pattern.compile(String.format(lNode, lQuestion2)).matcher(lSerialized)));
        assertEquals("question 3", 1, count(Pattern.compile(String.format(lNode, lQuestion3)).matcher(lSerialized)));

        lSerialized = serialize(lExtent.getCompletions(), lSerializer);
        System.out.println(lSerialized);
        lNode = "<Completion><p>%s</p></Completion>";
        assertEquals("number of entries", 4, count(Pattern.compile("<propertySet>").matcher(lSerialized)));
        assertEquals("completion 1", 1, count(Pattern.compile(String.format(lNode, lCompletion1)).matcher(lSerialized)));
        assertEquals("completion 2", 2, count(Pattern.compile(String.format(lNode, lCompletion2)).matcher(lSerialized)));
        assertEquals("completion 3", 1, count(Pattern.compile(String.format(lNode, lCompletion3)).matcher(lSerialized)));
    }

    private int count(final Matcher inMatcher) {
        int i = 0;
        while (inMatcher.find()) {
            i++;
        }
        return i;
    }

    private String serialize(final QueryResult inResult, final XMLSerializer inSerializer) throws BOMException,
    SQLException {
        inSerializer.clear();
        while (inResult.hasMoreElements()) {
            inResult.next().accept(inSerializer);
        }
        return inSerializer.toString();
    }

    private Long createQuestion(final String inQuestion, final String inDecimal, final Long inGroupID,
            final boolean isRoot, final String inAuthorID) throws VException, SQLException, WorkflowException {
        final Long outQuestionID = data.createQuestion(inQuestion, inDecimal, inGroupID, isRoot);
        final Question lQuestion = data.getQuestionHome().getQuestion(outQuestionID);
        ((WorkflowAwareContribution) lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, actorID);
        data.createQuestionProducer(new Long(outQuestionID), new Long(inAuthorID), true);
        return outQuestionID;
    }

}
