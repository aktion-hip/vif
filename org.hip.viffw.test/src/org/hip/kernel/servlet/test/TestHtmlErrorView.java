package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractHtmlView;

/**
 * Error View for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestHtmlErrorView extends AbstractHtmlView {
	private final static String HTML_ERROR1 = "This is an error message in an error view!</br>\n";
	private final static String HTML_ERROR2 = "</br>\n";
	
	/**
	 * TestHtmlView constructor comment.
	 */
	protected TestHtmlErrorView() {
		super();
	}
	
	/**
	 * TestHtmlView constructor with specified Context.
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	protected TestHtmlErrorView(Context inContext, String inErrorMessage) {
		super(inContext);
		setHTMLString(HTML_ERROR1 + inErrorMessage + HTML_ERROR2);
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
