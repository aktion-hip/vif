package org.hip.vif.core.util;

import static org.junit.Assert.*;

import java.util.EmptyStackException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Component;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class TaskStackTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testDo() {
		TaskStack lStack = new TaskStack(4);
		assertTrue(lStack.empty());
		
		try {
			lStack.peek();
			fail("shouldn't get here");
		}
		catch (EmptyStackException exc) {
			//intentionally left empty
		}
		try {
			lStack.pop();
			fail("shouldn't get here");
		}
		catch (EmptyStackException exc) {
			//intentionally left empty
		}
		
		//push one task
		lStack.push(new TestTask());
		assertFalse(lStack.empty());
		
		assertNotNull(lStack.peek());

		//pop one item
		assertNotNull(lStack.pop());
		assertTrue(lStack.empty());
		
		TestTask lTask = new TestTask();
		//push the same task twice
		lStack.push(lTask);
		assertFalse(lStack.empty());
		lStack.push(lTask);
		assertFalse(lStack.empty());
		
		//after popping once, the stack is empty
		assertNotNull(lStack.pop());
		assertTrue(lStack.empty());
		
		//pushing 5 tasks
		lStack.push(new TestTask());
		lStack.push(new TestTask());
		lStack.push(new TestTask());
		lStack.push(new TestTask());
		lStack.push(new TestTask());
		assertFalse(lStack.empty());
		
		//we can pop only 4
		assertNotNull(lStack.pop());
		assertNotNull(lStack.pop());
		assertNotNull(lStack.pop());
		assertNotNull(lStack.pop());
		assertTrue(lStack.empty());
	}
	
//	---
	
	private static class TestTask implements IPluggableTask {
		@Override
		public void setEventAdmin(EventAdmin inEventAdmin) {
		}
		@Override
		public void requestLookup(LookupType inType, Long inID) {
		}
		@Override
		public void requestLookup(LookupType inType, String inTextID) {
		}
		@Override
		public Component run() throws VException {
			return null;
		}		
	}

}
