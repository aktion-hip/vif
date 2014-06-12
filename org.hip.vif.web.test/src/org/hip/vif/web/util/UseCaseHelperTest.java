package org.hip.vif.web.util;

import junit.framework.Assert;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Component;

/**
 * Note: needs special Target Platform for testing in place.
 * 
 * @author Luthiger
 * Created: 26.12.2011
 */
public class UseCaseHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCreateFullyQualifiedTaskName() {
		Assert.assertEquals("org.hip.vif.web/org.hip.vif.web.util.UseCaseHelperTest$UCHTask", 
				UseCaseHelper.createFullyQualifiedTaskName(UCHTask.class));
	}

	@Test
	public final void testCreateFullyQualifiedID() {
		Assert.assertEquals("org.hip.vif.web/test.it", 
				UseCaseHelper.createFullyQualifiedID("test.it", getClass()));
	}

// ---
	
	private static class UCHTask implements IPluggableTask {	
		@Override
		public Component run() throws VException {
			return null;
		}
		@Override
		public void setEventAdmin(EventAdmin inEventAdmin) {}
		@Override
		public void requestLookup(LookupType inType, Long inID) {}
		@Override
		public void requestLookup(LookupType inType, String inTextID) {}		
	}
	
}
