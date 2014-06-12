package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.impl.AbstractHtmlPage;

/**
 * Concrete implementation of an HtmlPage for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestHtmlPage extends AbstractHtmlPage {
	
	public TestHtmlPage() {}

	/**
	 * TestHtmlPage constructor comment.
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public TestHtmlPage(org.hip.kernel.servlet.Context inContext) {
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
