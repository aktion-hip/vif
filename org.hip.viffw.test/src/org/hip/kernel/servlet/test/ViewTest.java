package org.hip.kernel.servlet.test;

import static org.junit.Assert.*;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class ViewTest {

	@Test
	public void testDo() {
		Context lContext = new TestContext();
		TestView lView = new TestView(lContext);
		assertEquals("language 1", "de", lView.getLanguage());
	
		lContext.setLanguage("en");
		assertEquals("language 2", VSys.dftLanguage, lView.getLanguage());
	}
	
	@Test
	public void testLocale() {
		TestView lView = new TestView();
		assertEquals("locale 1", "en_US", lView.getLocale().toString());

		Context lContext = new TestContext();
		lView = new TestView(lContext);
		assertEquals("locale 2", "de_CH", lView.getLocale().toString());

		lContext.setLanguage("en");
		lView = new TestView(lContext);
		assertEquals("locale 3", "en_CH", lView.getLocale().toString());
	}
}
