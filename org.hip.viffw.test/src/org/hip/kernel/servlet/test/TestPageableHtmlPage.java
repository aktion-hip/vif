package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractPageableHtmlPage;

/**
 * Concrete implementation of an PageableHtmlPage for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestPageableHtmlPage extends AbstractPageableHtmlPage {

	/**
	 * TestPageableHtmlPage constructor comment.
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public TestPageableHtmlPage(Context inContext) {
		super(inContext);
	}

	/**
	 * Returns the relative path and name of the HTML or XSL file (relative to the root of the websites)
	 *
	 * @return java.lang.String
	 */
	protected String getXMLName() {
		return null;
	}
}
