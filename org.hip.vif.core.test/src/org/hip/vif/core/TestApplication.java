package org.hip.vif.core;

import java.util.Locale;

import org.hip.vif.core.service.ApplicationData;

import com.vaadin.Application;

/**
 * Usage: <pre>@BeforeClass 
 *public static void init() {
 *  TestApplication lApp = new TestApplication();
 *  lApp.start(null, null, new TestAppContext());
 *  ApplicationData.create(lApp);
 *  ApplicationData.initLocale(Locale.GERMAN, "VIFMessages");
 *}</pre>
 *
 * @author Luthiger
 * Created: 17.02.2012
 */
@SuppressWarnings("serial")
public class TestApplication extends Application {

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * Convenience method, initializes the <code>ApplicationData</code> for testing purposes.
	 * 
	 * @param inLocale
	 */
	public static void initialize(Locale inLocale) {
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(inLocale);
	}
}
