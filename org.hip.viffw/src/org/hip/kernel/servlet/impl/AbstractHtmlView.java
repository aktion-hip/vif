package org.hip.kernel.servlet.impl;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import org.hip.kernel.exc.VError;
import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.HtmlView;
import org.hip.kernel.servlet.RequestException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.util.TransformerProxy;
import org.hip.kernel.util.XSLProcessingException;

/**
 * Baseclass of all html views
 *
 * @author Benno Luthiger
 */
abstract public class AbstractHtmlView extends AbstractView implements HtmlView {
	//constants
	public static final String DOCS_ROOT_PROPERTY_NAME = "org.hip.vif.docs.root";
	public static final String WEB_ROOT_PROPERTY_NAME = "org.hip.vif.website.root";

	//class attributes
	protected static String DOCS_ROOT = "";
	protected static String WEB_ROOT = "";
	
	//instance attributes
	private String htmlString = null;
	private TransformerProxy xsltTransformer = null;
	private String subAppPath = "";
	
	/**
	 * AbstractHtmlView default constructor.
	 */
	protected AbstractHtmlView() {
		this(null);
	}
	
	/**
	 * Initializes properties of this view.
	 *
	 * @param inContext org.hip.kernel.servlet.Context
	 */
	protected AbstractHtmlView(Context inContext) {
		super(inContext);
		if (inContext != null)
			subAppPath = inContext.getServletPath() + "/";
		this.initialize();
	}
	
	/**
	 * Returns the html-representation of this view as string. If it wasn't set
	 * this method returns null.
	 * 
	 * @return java.lang.String - html-string of view. 
	 */
	public String getHTMLString() {
		return htmlString;
	}
	
	private String getRelativeHTMLName() {
		return DOCS_ROOT + getSubAppPath() + this.getLanguage() + "/" + getXMLName();
	}
	
	/**
	 * The path to the sub application in the same webapp of the servlet-engine.
	 * Subclasses can overwrite this method.
	 * 
	 * @return String
	 */
	protected String getSubAppPath() {
		return subAppPath;
	}
	
	/**
	 * Returns the relative path and name of the HTML or XSL file (relative to the root of the websites)
	 *
	 * @return java.lang.String
	 */
	abstract protected String getXMLName();
	
	/**
	 * Initializes the root directories of the websites and the documents 
	 * (relative and absolute paths).
	 * These directories are defined externally in the vif.properties file.
	 *
	 * @see org.hip.kernel.sys.VSys
	 */
	private void initialize() {
		try {
			DOCS_ROOT = VSys.getVSysCanonicalPath(DOCS_ROOT_PROPERTY_NAME);
			if (DOCS_ROOT.length() == 0) {
				String lProperty = ServletContainer.getInstance().getBasePath();
				if (lProperty != null) {
					DOCS_ROOT = VSys.getVSysCanonicalPath(DOCS_ROOT_PROPERTY_NAME, lProperty);
				}
			}
			WEB_ROOT = VSys.getVSysProperties().getProperty(WEB_ROOT_PROPERTY_NAME);
		} 
		catch (Exception exc) {
			throw new VError("AbstractHtmlView.initialize(): error during getting web and docs root properties" );
		}	
	}
	
	/**
	 * Reads HTML file of the view.
	 *
	 * @return java.lang.String
	 */
	protected String readHTML() {
		File lHtmlFile;
		FileReader lHtmlInReader = null;
		char[] lBuffer;
		int lSize = 0;
		int c = 0;
		
		try{
			lHtmlFile = new File(getRelativeHTMLName());
			lSize = (int)lHtmlFile.length();
			lHtmlInReader = new FileReader(lHtmlFile);
			lBuffer = new char[lSize];
	
			while (c < lSize){
				c += lHtmlInReader.read(lBuffer, c, lSize-c);
			}
	
			return new String(lBuffer);
		}
		catch (Exception exc){
			VSys.err.println(exc.getMessage());
		}
		finally{
			try{
				if (lHtmlInReader != null){
					lHtmlInReader.close();
				}
			}
			catch (Exception exc){
				VSys.err.println(exc.getMessage());
			}
		}
		
		return null;
	}
	
	/**
	 * Writes the view as html-String to the passed servlet output stream.<br/>
	 * <b>Note:</b> Use this method to output bit streams.<br/> 
	 * To render the view with the correct encodings set, use <code>renderToWriter(PrintWriter, String)</code> instead.
	 *
	 * @param inStream javax.servlet.ServletOutputStream - Output stream of servlet response
	 * @param inSessionID java.lang.String
	 * @throws org.hip.kernel.servlet.RequestException
	 */
	public void renderToStream(ServletOutputStream inStream, String inSessionID) throws RequestException {
		try {
			renderToWriter(new PrintWriter(new OutputStreamWriter(inStream, "UTF-8")), inSessionID);
		} 
		catch (UnsupportedEncodingException exc) {
			throw new RequestException(exc.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.servlet.HtmlView#renderToStream(java.io.PrintWriter, java.lang.String)
	 */
	public void renderToWriter(PrintWriter inWriter, String inSessionID) throws RequestException {
		try {
			if (xsltTransformer == null) {
				if (htmlString == null) {
					inWriter.println("No HTML for view!");
				}
				else {
					inWriter.print(htmlString);
				}
				return;
			}
			xsltTransformer.renderToWriter(inWriter, inSessionID);
		}
		catch (XSLProcessingException exc) {
			throw new RequestException(exc.getMessage());
		}
	}
	
	/**
	 * Sets the HTML representation of this view. 
	 * 
	 * @param inHTMLString java.lang.String - html-representation of this view.
	 */
	public void setHTMLString(String inHTMLString) {
		htmlString = inHTMLString;
	}
	
	/**
	 * Sets the Transformer to generate this view.
	 * 
	 * @param inTransformer org.hip.kernel.util.TransformerProxy
	 */
	public void setTransformer(TransformerProxy inTransformer) {
		xsltTransformer = inTransformer;
	}
	
	/**
	 * Returns the Transformer which generates this view. 
	 * If it wasn't set the method returns null.
	 * 
	 * @return org.hip.kernel.util.TransformerProxy
	 */
	public TransformerProxy getTransformer() {
		return xsltTransformer;
	}

	/**
	 * Two views are equal if their html strings are equal.
	 */
	public boolean equals(Object inObject) {
		if (this == inObject)
			return true;
		if (inObject == null)
			return false;
		if (getClass() != inObject.getClass())
			return false;
		final AbstractHtmlView lOther = (AbstractHtmlView) inObject;
		if (htmlString == null) {
			if (lOther.getHTMLString() != null)
				return false;
		} 
		else if (!htmlString.equals(lOther.getHTMLString()))
			return false;
		return true;
	}	
	
}
