package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.CompletionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 12.08.2003
 * @author Luthiger
 */
public class CompletionHomeImplTest {
	private static String EXPECTED = "Completion 3";
	
	private static DataHouseKeeper data;
	private Long[] questionIDs = new Long[2];
	private Long[] completionIDs = new Long[7];

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		int lCountQuestions = data.getQuestionHome().getCount();
		int lCountCompletions = data.getCompletionHome().getCount();
			
		questionIDs[0] = data.createQuestion("This is question 1", "1");
		questionIDs[1] = data.createQuestion("This is question 2", "1.1");
		assertEquals("count questions", lCountQuestions+2, data.getQuestionHome().getCount());

		completionIDs[0] = data.createCompletion("Completion 1", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[1] = data.createCompletion("Completion 2", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[2] = data.createCompletion(EXPECTED, questionIDs[0], WorkflowAwareContribution.S_PRIVATE);
		completionIDs[3] = data.createCompletion("Completion 4", questionIDs[0], WorkflowAwareContribution.S_WAITING_FOR_REVIEW);
		completionIDs[4] = data.createCompletion("Completion 5", questionIDs[0], WorkflowAwareContribution.S_OPEN);
		completionIDs[5] = data.createCompletion("Completion 6", questionIDs[1], WorkflowAwareContribution.S_ANSWERED);
		completionIDs[6] = data.createCompletion("Completion 7", questionIDs[1], WorkflowAwareContribution.S_PRIVATE);
		assertEquals("count completions", lCountCompletions+7, data.getCompletionHome().getCount());
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}

	@Test
	public void testGetCompletions() throws Exception {
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[2], completionIDs[3], completionIDs[4]};
		checkQueryResult(lExpected1, data.getCompletionHome().getCompletions(questionIDs[0]), CompletionHome.KEY_ID, "question 1");
		
		Long[] lExpected2 = {completionIDs[5], completionIDs[6]};
		checkQueryResult(lExpected2, data.getCompletionHome().getCompletions(questionIDs[1]), CompletionHome.KEY_ID, "question 2");
	}

	@Test
	public void testGetPublished() throws Exception {
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[4]};
		checkQueryResult(lExpected1, data.getCompletionHome().getPublished(questionIDs[0]), CompletionHome.KEY_ID, "question 1");
		
		Long[] lExpected2 = {completionIDs[5]};
		checkQueryResult(lExpected2, data.getCompletionHome().getPublished(questionIDs[1]), CompletionHome.KEY_ID, "question 2");
	}

	@Test
	public void testGetCompletion() throws Exception {
		assertEquals("get completion", EXPECTED, data.getCompletionHome().getCompletion(completionIDs[2]).get(CompletionHome.KEY_COMPLETION));
	}

	@Test
	public void testGetSiblingCompletions() throws Exception {
		Long[] lExpected1 = {completionIDs[0], completionIDs[1], completionIDs[3], completionIDs[4]};
		checkQueryResult(lExpected1, data.getCompletionHome().getSiblingCompletions(questionIDs[0], completionIDs[2]), CompletionHome.KEY_ID, "question 1");
		
		Long[] lExpected2 = {completionIDs[6]};
		checkQueryResult(lExpected2, data.getCompletionHome().getSiblingCompletions(questionIDs[1], completionIDs[5]), CompletionHome.KEY_ID, "question 2");
	}

	private void checkQueryResult(Long[] inExpected, QueryResult inResult, String inColumn, String inAssert) throws Exception {
		Collection<Long> lExpected = new Vector<Long>(Arrays.asList(inExpected));
		int i = 0;
		while (inResult.hasMoreElements()) {
			GeneralDomainObject lObject = inResult.nextAsDomainObject();				
			assertTrue(inAssert, lExpected.contains(lObject.get(inColumn)));
			i++;
		}
		assertEquals(inAssert + " (count)", inExpected.length, i);
	}
}
