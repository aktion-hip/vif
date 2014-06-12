package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.impl.AbstractXSLQueryResultView;
import org.hip.kernel.util.XMLRepresentation;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.Page;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.impl.XMLSerializer;

/**
 * Concrete implementation of an XSLQueryResultView for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestXSLQueryResultView extends AbstractXSLQueryResultView {
	private static final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private static final String ROOT_BEGIN 	= "<Root>";
	private static final String ROOT_END 	= "</Root>";
	private static final String PAGE_KEY = "testPage";
	private static final String XSL_FILE = "SimpleTransform.xsl";
	
	/**
	 * TestXSLQueryResultView constructor comment.
	 * @param inResult org.hip.kernel.bom.QueryResult
	 */
	public TestXSLQueryResultView(org.hip.kernel.bom.QueryResult inResult) {
		super(inResult);
	}
	
	/**
	 * TestXSLQueryResultView constructor comment.
	 * @param inContext org.hip.kernel.servlet.Context
	 * @param inResult org.hip.kernel.bom.QueryResult
	 */
	public TestXSLQueryResultView(org.hip.kernel.servlet.Context inContext, org.hip.kernel.bom.QueryResult inResult) {
		super(inContext, inResult);
	
		setPageSize(4);
	//	setNumberOfRows(inNumberOfRows);
		setNewPage();
	
		setSerialized(getPage(), getLastPageNumber());
		getContext().set(PAGE_KEY, getPage());
		prepareTransformation(new XMLRepresentation(getPage().getSerialized()));
	}
	
	/**
	 * TestXSLQueryResultView constructor
	 *
	 * @param inContext org.hip.kernel.servlet.Context
	 * @param inResult org.hip.kernel.bom.QueryResult
	 * @param inNumberOfRows int
	 */
	public TestXSLQueryResultView(Context inContext, QueryResult inResult, int inNumberOfRows) {
		super(inContext, inResult);
	
		setPageSize(4);
		setNumberOfRows(inNumberOfRows);
		setNewPage();
	
		setSerialized(getPage(), getLastPageNumber());
		getContext().set(PAGE_KEY, getPage());
		prepareTransformation(new XMLRepresentation(getPage().getSerialized()));
	}
	
	/**
	 * Returns the page for the page to read a page from the context or write a
	 * page to the context.
	 *
	 * @param java.lang.String
	 */
	protected String getPageKey() {
		return PAGE_KEY;
	}
	
	/**
	 * Returns the relative path and name of the HTML or XSL file (relative to the root of the websites)
	 *
	 * @return java.lang.String
	 */
	protected String getXMLName() {
		return XSL_FILE;
	}
	
	/**
	 * Set the serialized string
	 *
	 * @param inPage org.hip.kernel.bom.Page
	 * @param inLastPageNumber int 
	 */
	protected void setSerialized(Page inPage, int inLastPageNumber) {
		StringBuffer lXML = new StringBuffer(HEADER + ROOT_BEGIN);
		DomainObjectIterator lDomainObjects = inPage.getObjects().elements();
		while (lDomainObjects.hasMoreElements()) {
			DomainObject lObject = (DomainObject)lDomainObjects.nextElement();
			XMLSerializer lSerializer = new XMLSerializer();
			lObject.accept(lSerializer);
			lXML.append(lSerializer.toString());
		}
		lXML.append(ROOT_END);
		inPage.setSerialized(new String(lXML));
	}
}
