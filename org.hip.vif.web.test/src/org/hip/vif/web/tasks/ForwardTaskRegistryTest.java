package org.hip.vif.web.tasks;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Note: needs special Target Platform for testing in place.
 */
public class ForwardTaskRegistryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetTask() throws Exception {
		String lTaskName = ForwardControllerRegistry.INSTANCE.getTarget(ForwardControllerRegistry.FORWARD_REQUEST_LIST);
		Assert.assertEquals("org.hip.vif.web/org.hip.vif.web.tasks.ForwardTaskRegistry$ForwardRequestsList", lTaskName);
	}

}
