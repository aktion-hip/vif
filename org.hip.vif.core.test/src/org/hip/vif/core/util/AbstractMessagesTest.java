package org.hip.vif.core.util;

import java.util.Locale;

import junit.framework.Assert;

import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.ApplicationData;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractMessagesTest {
	private static final String MSG_KEY = "test.msg.1";
	
	@BeforeClass
	public static void init() {
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
	}

	@Test
	public void getMessage() throws Exception {
		IMessages lMessages = new Messages();
		
		ApplicationData.initLocale(Locale.ENGLISH);
		Assert.assertEquals("Test message in English.", lMessages.getMessage(MSG_KEY));

		ApplicationData.initLocale(Locale.GERMAN);
		Assert.assertEquals("Test Meldung auf Deutsch.", lMessages.getMessage(MSG_KEY));
		
		ApplicationData.initLocale(Locale.ENGLISH);
		Assert.assertEquals("Test message in English.", lMessages.getMessage(MSG_KEY));
	}
	
// ---
	
	private class Messages extends AbstractMessages {
		@Override
		protected ClassLoader getLoader() {
			return getClass().getClassLoader();
		}
		@Override
		protected String getBaseName() {
			return "testMessages";
		}
	}

}
