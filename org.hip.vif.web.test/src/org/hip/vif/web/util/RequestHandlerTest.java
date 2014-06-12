package org.hip.vif.web.util;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.tasks.BackTask;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Note: needs special Target Platform for testing in place.
 * 
 * @author Luthiger
 * Created: 26.12.2011
 */
public class RequestHandlerTest {
	private static final String REQUEST_URL = "http://my.test.app/admin";
	
	@BeforeClass
	public static void init() {
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
		ApplicationData.setRequestURL(REQUEST_URL);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetMainURL() throws Exception {
		assertEquals("http://my.test.app/forum", RequestHandler.getMainForumURL());
		assertEquals("http://my.test.app/admin", RequestHandler.getMainAdminURL());
	}
	
	@Test
	public void testCreateRequestedURL() throws Exception {
		ApplicationData.setGroupID(68l);
		assertEquals("http://my.test.app/forum?request=org.hip.vif.web/org.hip.vif.web.tasks.BackTask&groupID=68", RequestHandler.createRequestedURL(BackTask.class, true));
		assertEquals("http://my.test.app/forum?request=org.hip.vif.web/org.hip.vif.web.tasks.BackTask&myParameter=31", RequestHandler.createRequestedURL(BackTask.class, true, "myParameter", 31l));
	}

}
