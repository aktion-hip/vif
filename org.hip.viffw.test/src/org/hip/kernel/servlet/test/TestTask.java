package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractTask;

/**
 * Concrete implementation of a Task for testing purpose.
 * 
 * @author: Benno Luthiger
 */
public class TestTask extends AbstractTask {
/**
 * TestTask default constructor.
 */
public TestTask() {
	super();
}
/**
 * Runs this task
 */
public void run() throws org.hip.kernel.exc.VException {
	Context lContext = getContext();
	TestHtmlPage lPage = new TestHtmlPage(lContext);
	TestXSLView lView = new TestXSLView(lContext);
	lPage.add(lView);
	lContext.setView(lPage);
}
}
