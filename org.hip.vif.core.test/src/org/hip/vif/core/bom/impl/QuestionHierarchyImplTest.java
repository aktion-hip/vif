package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchy;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 */
public class QuestionHierarchyImplTest {
	Long[] questionIDs;
	Collection<String> expected;
	private static DataHouseKeeper data;

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
	}
	
	@Test
	public void testGetAssociatedQuestion() throws Exception {
		QuestionHierarchyHome lHome = data.getQuestionHierarchyHome();
		QueryResult lChilds = lHome.getChilds(questionIDs[3]);
		
		int lCount = 0;
		while (lChilds.hasMoreElements()) {
			Question lQuestion = ((QuestionHierarchy)lChilds.next()).getAssociatedQuestion();
			assertTrue("child " + String.valueOf(++lCount), expected.contains(lQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL)));
		}
		assertEquals("number of childs", 3, lCount);
	}

	private Long[] createHierachy() throws Exception {
		Long[] outIDs = new Long[11];
		String[] lExpected = new String[] {"1.3.1", "1.3.2", "1.3.3"};
		expected = new Vector<String>(Arrays.asList(lExpected));
		
		outIDs[0] = new Long(data.createQuestion("Q1", "1"));
		outIDs[1] = new Long(data.createQuestion("Q2", "1.1"));
		outIDs[2] = new Long(data.createQuestion("Q3", "1.2"));
		outIDs[3] = new Long(data.createQuestion("Q4", "1.3"));
		outIDs[4] = new Long(data.createQuestion("Q5", "1.1.1"));
		outIDs[5] = new Long(data.createQuestion("Q6", "1.1.2"));
		outIDs[6] = new Long(data.createQuestion("Q7", "1.2.1"));
		outIDs[7] = new Long(data.createQuestion("Q8", "1.2.2"));
		outIDs[8] = new Long(data.createQuestion("Q9", lExpected[0]));
		outIDs[9] = new Long(data.createQuestion("Q10", lExpected[1]));
		outIDs[10] = new Long(data.createQuestion("Q11", lExpected[2]));			
		
		Long lGroupID = 6l;
		
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
