package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.impl.AbstractXSLView;
import org.hip.kernel.util.XMLRepresentation;


/**
 * Concrete implementation of an XSLView for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestXSLView extends AbstractXSLView {
	private static final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private static final String ROOT_BEGIN 	= "<Root>";
	private static final String ROOT_END 	= "</Root>";
	public static final String XSL_FILE = "SimpleTransform.xsl";
	public static final String XSL_FILE2 = "SimpleTransform2.xsl";
	private boolean variation = false;
	
	/**
	 * TestXSLView constructor with specified Context.
	 *
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	public TestXSLView(Context inContext) {
		this(inContext, false);
	}

	public TestXSLView(Context inContext, boolean inVariation) {
		super(inContext);
		variation = inVariation;
		prepareTransformation(createXML());
	}

	private XMLRepresentation createXML() {
		String lNL = System.getProperty("line.separator");
		StringBuffer lXML = new StringBuffer(HEADER + lNL);
		lXML.append(ROOT_BEGIN + lNL);
		lXML.append("<firstLine>This is the first line.</firstLine>\n");
		lXML.append("<secondLine>This line follows the first.</secondLine>\n");
		lXML.append(ROOT_END);
		XMLRepresentation outXML = new XMLRepresentation(new String(lXML));
	
		//write the XML to the output
		traceXML(outXML.reveal());
		
		return outXML;
	}
	
	/**
	 * Returns the relative path and name of the HTML or XSL file (relative to the root of the websites)
	 *
	 * @return java.lang.String
	 */
	protected String getXMLName() {
		return variation ? XSL_FILE2 : XSL_FILE;
	}
}
