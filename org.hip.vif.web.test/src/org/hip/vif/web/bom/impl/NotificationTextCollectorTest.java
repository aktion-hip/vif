package org.hip.vif.web.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.util.QuestionHierarchyEntry;
import org.hip.vif.web.bom.NotificationTextCollector;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Benno Luthiger */
public class NotificationTextCollectorTest {
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        data.deleteAllFromQuestion();
        data.deleteAllFromCompletion();
    }

    @Test
    public void testGetNotificationText() throws Exception {
        final String lExpected1 = "Question 8.8: Q1\nRemark: Remark\n\n";
        final String lExpected2 = "Completion (8.8): C1\n\n";

        final NotificationTextCollector lNotificator = new NotificationTextCollector();
        lNotificator.reset();

        final Long lQuestionID = data.createQuestion("Q1", "8.8");
        final QuestionHierarchyEntry lQuestion = (QuestionHierarchyEntry) data.getQuestionHome().getQuestion(
                lQuestionID);
        lQuestion.accept(lNotificator);
        assertEquals("notification 1", lExpected1, new String(lNotificator.getNotificationText()));

        lNotificator.reset();
        final Long lCompletionID = data.createCompletion("C1", lQuestionID);
        QuestionHierarchyEntry lCompletion = (QuestionHierarchyEntry) data.getCompletionHome().getCompletion(
                lCompletionID);
        lCompletion.accept(lNotificator);
        assertEquals("notification 2", lExpected2, new String(lNotificator.getNotificationText()));

        lNotificator.reset();
        lCompletion = data.getJoinCompletionToQuestionHome().getCompletion(lCompletionID);
        lCompletion.accept(lNotificator);
        assertEquals("notification 3", lExpected2, new String(lNotificator.getNotificationText()));
    }

    @Test
    public void testSetMadeBy() throws Exception {
        final String lExpected = "Contributions by Jane Doe\n\n";

        final NotificationTextCollector lNotificator = new NotificationTextCollector();
        lNotificator.reset();
        lNotificator.setMadeBy("Jane Doe");
        assertEquals("notification", lExpected, new String(lNotificator.getNotificationText()));
    }
}
