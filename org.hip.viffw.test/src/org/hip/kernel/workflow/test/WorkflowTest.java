package org.hip.kernel.workflow.test;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.workflow.WorkflowAware;
import org.junit.Test;

/**
 * Simple test case for testing the workflow package.
 * 
 * Created on 03.07.2003
 * @author Benno Luthiger
 * (inspired by itools by Juan David Ib��ez Palomar <jdavid@itaapy.com>)
 */
public class WorkflowTest {

	@Test
	public void testDo() throws Exception {
		WorkflowAware lDocument = new TestDocument();
		
		assertEquals("enters 0", 		0, ((TestDocument)lDocument).getNumberOfEnters());
		assertEquals("leaves 0", 		0, ((TestDocument)lDocument).getNumberOfLeaves());
		assertEquals("transitions 0", 	0, ((TestDocument)lDocument).getNumberOfTransitions());
		
		//do workflow
		assertEquals("State 1", TestDocument.STATE_PRIVATE, lDocument.getStateName());
		System.out.println("State now (1): " + lDocument.getStateName());
		System.out.println("do first request transition...");
		lDocument.doTransition(TestDocument.TRANS_REQUEST, new Object[] {"First Request"});
		assertEquals("enters 1", 		1, ((TestDocument)lDocument).getNumberOfEnters());
		assertEquals("leaves 1", 		1, ((TestDocument)lDocument).getNumberOfLeaves());
		assertEquals("transitions 1", 	1, ((TestDocument)lDocument).getNumberOfTransitions());
		
		assertEquals("State 2", TestDocument.STATE_PENDING, lDocument.getStateName());
		System.out.println("State now (2): " + lDocument.getStateName());
		System.out.println("do first reject transition...");
		lDocument.doTransition(TestDocument.TRANS_REJECT, new Object[] {"First Reject"});
		assertEquals("enters 2", 		2, ((TestDocument)lDocument).getNumberOfEnters());
		assertEquals("leaves 2", 		2, ((TestDocument)lDocument).getNumberOfLeaves());
		assertEquals("transitions 2", 	2, ((TestDocument)lDocument).getNumberOfTransitions());
		
		assertEquals("State 3", TestDocument.STATE_PRIVATE, lDocument.getStateName());
		System.out.println("State now (3): " + lDocument.getStateName());
		System.out.println("do second request transition...");
		lDocument.doTransition(TestDocument.TRANS_REQUEST, new Object[] {"Second Request"});
		assertEquals("enters 3", 		3, ((TestDocument)lDocument).getNumberOfEnters());
		assertEquals("leaves 3", 		3, ((TestDocument)lDocument).getNumberOfLeaves());
		assertEquals("transitions 3", 	3, ((TestDocument)lDocument).getNumberOfTransitions());
		
		assertEquals("State 4", TestDocument.STATE_PENDING, lDocument.getStateName());
		System.out.println("State now (4): " + lDocument.getStateName());
		System.out.println("do final accept transition...");
		lDocument.doTransition(TestDocument.TRANS_ACCEPT, new Object[] {"Final Accept"});
		assertEquals("enters 4", 		4, ((TestDocument)lDocument).getNumberOfEnters());
		assertEquals("leaves 4", 		4, ((TestDocument)lDocument).getNumberOfLeaves());
		assertEquals("transitions 4", 	4, ((TestDocument)lDocument).getNumberOfTransitions());
		
		assertEquals("State 5", TestDocument.STATE_PUBLIC, lDocument.getStateName());
		System.out.println("State now (5): " + lDocument.getStateName());
	}
	
	@Test
	public void testReBind() throws Exception {
		TestDocument lDocument = new TestDocument();
		lDocument.reBind(TestDocument.STATE_PUBLIC);
		assertEquals("State", TestDocument.STATE_PUBLIC, lDocument.getStateName());		
	}
}
