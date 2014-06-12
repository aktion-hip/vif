package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContributionDeletionHandlerTest {
	private static DataHouseKeeper data;
	Long[] questionIDs;
	String[] expected = {"1", "1.1", "1.2", "1.3", "1.1.1", "1.1.2", "1.2.1", "1.2.2", "1.3.1", "1.3.2", "1.3.3"};
	
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();		
	}

	@Before
	public void setUp() throws Exception {
		questionIDs = createHierachy();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestionHierarchy();
		data.deleteAllFromQuestion();
		data.deleteAllFromCompletion();
	}

	@Test
	public void testGetContributions() throws Exception {
		Long lExpectedCompletion = data.createCompletion("00001", questionIDs[3]);

		Collection<String> lExpected = Arrays.asList(expected);
		QuestionBranchIterator lIterator = new QuestionBranchIterator(new Long(questionIDs[0]));
		ContributionDeletionHandler lVisitor = new ContributionDeletionHandler();
		lIterator.start(true, lVisitor);
		
		int lCount = 0;
		for (Iterator<?> lContribution = lVisitor.getContributions().iterator(); lContribution.hasNext();) {
			DomainObject lObject = (DomainObject) lContribution.next();
			assertTrue("Question " + String.valueOf(++lCount), lExpected.contains(lObject.get(QuestionHome.KEY_QUESTION_DECIMAL)));
		}
		assertEquals("number of selected 1", expected.length, lCount);
		
		Vector<Long> lExpected2 = new Vector<Long>();
		lExpected2.add(lExpectedCompletion);
		lExpected2.add(questionIDs[3]);
		lExpected2.add(questionIDs[8]);
		lExpected2.add(questionIDs[9]);
		lExpected2.add(questionIDs[10]);
		lIterator = new QuestionBranchIterator();
		lVisitor = new ContributionDeletionHandler();
		lIterator.start(new Long(questionIDs[3]), true, true, lVisitor);
		lCount = 0;
		for (Iterator<?> lContribution = lVisitor.getContributions().iterator(); lContribution.hasNext();) {
			DomainObject lObject = (DomainObject) lContribution.next();
			assertTrue("Contribution " + String.valueOf(++lCount), lExpected2.contains(lObject.get("ID")));
		}
		assertEquals("number of selected 2", 5, lCount);			
	}
	
	private Long[] createHierachy() throws Exception {
		Long lGroupID = 2l;
		Long[] outIDs = new Long[11];

		outIDs[0] = data.createQuestion("Q1", expected[0]);
		outIDs[1] = data.createQuestion("Q2", expected[1]);
		outIDs[2] = data.createQuestion("Q3", expected[2]);
		outIDs[3] = data.createQuestion("Q4", expected[3]);
		outIDs[4] = data.createQuestion("Q5", expected[4]);
		outIDs[5] = data.createQuestion("Q6", expected[5]);
		outIDs[6] = data.createQuestion("Q7", expected[6]);
		outIDs[7] = data.createQuestion("Q8", expected[7]);
		outIDs[8] = data.createQuestion("Q9", expected[8]);
		outIDs[9] = data.createQuestion("Q10", expected[9]);
		outIDs[10] = data.createQuestion("Q11", expected[10]);			
		
		data.createQuestionHierachy(outIDs[0], outIDs[1], lGroupID);
		data.createQuestionHierachy(outIDs[0], outIDs[2], lGroupID);
		data.createQuestionHierachy(outIDs[0], outIDs[3], lGroupID);
		
		data.createQuestionHierachy(outIDs[1], outIDs[4], lGroupID);
		data.createQuestionHierachy(outIDs[1], outIDs[5], lGroupID);
		
		data.createQuestionHierachy(outIDs[2], outIDs[6], lGroupID);
		data.createQuestionHierachy(outIDs[2], outIDs[7], lGroupID);

		data.createQuestionHierachy(outIDs[3], outIDs[8], lGroupID);
		data.createQuestionHierachy(outIDs[3], outIDs[9], lGroupID);
		data.createQuestionHierachy(outIDs[3], outIDs[10], lGroupID);

		return outIDs;
	}
}
