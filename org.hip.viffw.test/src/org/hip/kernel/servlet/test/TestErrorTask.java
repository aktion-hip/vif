package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractTask;
import org.hip.kernel.servlet.impl.AbstractContext;

/**
 * Error Task for testing purpose.
 * 
 * @author: Benno Luthiger
 */
public class TestErrorTask extends AbstractTask {
	/**
	 * Runs this task
	 */
	public void run() throws org.hip.kernel.exc.VException {
		Context lContext = getContext();
		TestHtmlPage lPage = new TestHtmlPage(lContext);
		TestHtmlErrorView lView = new TestHtmlErrorView(lContext, (String)lContext.get(AbstractContext.ERROR_MESSAGE_KEY));
		lPage.add(lView);
		lContext.setView(lPage);
	}
}
