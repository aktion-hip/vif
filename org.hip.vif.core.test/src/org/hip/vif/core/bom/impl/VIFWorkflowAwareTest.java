package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 20, 2004
 */
public class VIFWorkflowAwareTest {	
	private Long questionID;
	private Long completionID;
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
		createNodes();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestion();
		data.deleteAllFromCompletion();
	}
	
	@Test
	public void testGetNodeID() throws Exception {
		Long lNodeID = new Long(questionID);
		assertEquals("node ID 1", lNodeID, ((VIFWorkflowAware)data.getQuestionHome().getQuestion(questionID)).getNodeID());
		assertEquals("node ID 2", lNodeID, ((VIFWorkflowAware)data.getCompletionHome().getCompletion(completionID)).getNodeID());
	}

	@Test
	public void testIsNode() throws Exception {
		assertTrue("is node", ((VIFWorkflowAware)data.getQuestionHome().getQuestion(questionID)).isNode());
		assertFalse("not node", ((VIFWorkflowAware)data.getCompletionHome().getCompletion(completionID)).isNode());
	}
	
	private void createNodes() throws Exception {
		questionID = data.createQuestion("Q1", "8.8");
		completionID = data.createCompletion("C1", questionID);
	}
}
