package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.impl.AbstractHtmlView;

/**
 * Concrete implementation of an HtmlView for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestHtmlView extends AbstractHtmlView {
	/**
	 * TestHtmlView constructor with specified Context.
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	protected TestHtmlView(org.hip.kernel.servlet.Context inContext) {
		super(inContext);
	}

	/**
	 * @return java.lang.String
	 */
	public String getReadHTML() {
		return readHTML();
	}
	
	/**
	 * Returns the relative path and name of the HTML or XSL file (relative to the root of the websites)
	 *
	 * @return java.lang.String
	 */
	protected String getXMLName() {
		return "VIFTest.html";
	}
}
