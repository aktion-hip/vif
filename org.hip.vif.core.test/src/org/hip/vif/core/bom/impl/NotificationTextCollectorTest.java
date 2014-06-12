package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.util.QuestionHierarchyEntry;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 20, 2004
 */
public class NotificationTextCollectorTest {
	private static DataHouseKeeper data;
		
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestion();
		data.deleteAllFromCompletion();
	}

	@Test
	public void testGetNotificationText() throws Exception {
		String lExpected1 = "Question 8.8: Q1\nRemark: Remark\n\n";
		String lExpected2 = "Completion (8.8): C1\n\n";

		NotificationTextCollector lNotificator = new NotificationTextCollector();
		lNotificator.reset();
		
		Long lQuestionID = data.createQuestion("Q1", "8.8");
		QuestionHierarchyEntry lQuestion = (QuestionHierarchyEntry)data.getQuestionHome().getQuestion(lQuestionID);
		lQuestion.accept(lNotificator);
		assertEquals("notification 1", lExpected1, new String(lNotificator.getNotificationText()));
		
		lNotificator.reset();
		Long lCompletionID = data.createCompletion("C1", lQuestionID);
		QuestionHierarchyEntry lCompletion = (QuestionHierarchyEntry)data.getCompletionHome().getCompletion(lCompletionID);
		lCompletion.accept(lNotificator);
		assertEquals("notification 2", lExpected2, new String(lNotificator.getNotificationText()));
		
		lNotificator.reset();
		lCompletion = (QuestionHierarchyEntry)data.getJoinCompletionToQuestionHome().getCompletion(lCompletionID);
		lCompletion.accept(lNotificator);
		assertEquals("notification 3", lExpected2, new String(lNotificator.getNotificationText()));
	}
	
	@Test
	public void testSetMadeBy() throws Exception {
		String lExpected = "Contributions by Jane Doe\n\n";

		NotificationTextCollector lNotificator = new NotificationTextCollector();
		lNotificator.reset();
		lNotificator.setMadeBy("Jane Doe");
		assertEquals("notification", lExpected, new String(lNotificator.getNotificationText()));
	}
}
