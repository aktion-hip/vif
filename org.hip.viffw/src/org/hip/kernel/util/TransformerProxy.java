package org.hip.kernel.util;

/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2002, Benno Luthiger

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hip.kernel.servlet.ISourceCreatorStrategy;
import org.hip.kernel.servlet.impl.FullyQualifiedNameStrategy;
import org.hip.kernel.sys.VObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Proxy object for xsl transformation.
 * An instance of this class holds the information 
 * needed to process a transformation.
 * 
 * Created on 25.10.2002
 * @author Benno Luthiger
 */
public class TransformerProxy extends VObject implements Serializable {
	//constants
	private static final String WARNINGS_ELEMENT = "warnings";
	private static final String WARNING_ELEMENT  = "warning";
	private static final String MESSAGES_ELEMENT = "messages";
	private static final String MESSAGE_ELEMENT  = "message";
	private static final String SESSION_ID		 = "global.sessionID";

	// Cache for XSL templates.
	protected static Hashtable<String, Templates> cTemplates = new Hashtable<String, Templates>(199);
	
	protected XMLRepresentation xmlToTransform;
	protected String xslFileName;
	protected HashMap<String, Object> stylesheetParameters;
	protected ISourceCreatorStrategy sourceStrategy = null;

	/**
	 * Constructor for TransformerProxy.
	 * 
	 * @param inXSLName String Name of XSLT file.
	 * @param inXML XMLRepresentation XML to transform.
	 * @param inStylesheetParameters HashMap<String, Object> the parameters for the XSLT stylesheet, may be <code>null</code>.
	 */
	public TransformerProxy(String inXSLName, XMLRepresentation inXML, HashMap<String, Object> inStylesheetParameters) {
		this(new FullyQualifiedNameStrategy(inXSLName), inXML, inStylesheetParameters);
		xslFileName = inXSLName;
	}
	
	public TransformerProxy(ISourceCreatorStrategy inStrategy, XMLRepresentation inXML, HashMap<String, Object> inStylesheetParameters) {
		sourceStrategy = inStrategy;
		xmlToTransform = inXML;
		stylesheetParameters = inStylesheetParameters;
	}
	

	/**
	 * Synchronized access to the template cache.
	 * 
	 * @return Transformer
	 * @throws XSLProcessingException
	 */
	private synchronized Transformer getTransformer() throws XSLProcessingException {
		
		//pre: stylesheet has to be defined
		if ((sourceStrategy == null) || ("".equals(sourceStrategy.getResourceId()))) {
			throw new Error("Missing stylesheet for transformation.");
		}
		
		Transformer outTransformer;
		Templates lTemplates;
		String lResourceId = sourceStrategy.getResourceId();
		try {
			if ((lTemplates = cTemplates.get(lResourceId)) == null) {
				lTemplates = TransformerFactory.newInstance().newTemplates(sourceStrategy.createSource());
				cTemplates.put(lResourceId, lTemplates);
			}
			outTransformer = lTemplates.newTransformer();
		}
		catch (TransformerConfigurationException exc) {
			throw new XSLProcessingException("Configuration exception while precompiling the XSL file " + exc.toString());
		}
		catch (IOException exc) {			
			throw new XSLProcessingException("Configuration exception while precompiling the XSL file " + exc.toString());
		}
		return outTransformer;
	}

	/**
	 * Performs the transformation to the specified output stream.<br/>
	 * <b>Note:</b> Use this method to output bit streams.<br/> 
	 * To render a view with the correct encodings set, use <code>renderToWriter(PrintWriter, String)</code> instead.
	 * 
	 * @param java.io.OutputStream
	 * @param inSessionID java.lang.String
	 * @throws org.hip.kernel.util.XSLProcessingException
	 */
	public void renderToStream(OutputStream inStream, String inSessionID) throws XSLProcessingException {
		Source lXMLSource = new DOMSource(xmlToTransform.reveal());
		StreamResult lResult = new StreamResult(inStream);
		try {
			Transformer lTransformer = getTransformer();
			if (inSessionID.length() != 0) {
				lTransformer.setParameter(SESSION_ID, ";jsessionid=" + inSessionID);
			}
			setStylesheetParameters(lTransformer, stylesheetParameters);
			lTransformer.transform(lXMLSource, lResult);
		}
		catch (TransformerException exc) {
			throw new XSLProcessingException("Transformer exception while performing the XSL transformation " + exc.toString());
		} 
	}
	
	/**
	 * Performs the transformation to the specified print writer.
	 * 
	 * @param inWriter PrintWriter
	 * @param inSessionID String
	 * @throws XSLProcessingException
	 */
	public void renderToWriter(PrintWriter inWriter, String inSessionID) throws XSLProcessingException {
		Source lXMLSource = new DOMSource(xmlToTransform.reveal());
		StreamResult lResult = new StreamResult(inWriter);
		try {
			Transformer lTransformer = getTransformer();
			if (inSessionID.length() != 0) {
				lTransformer.setParameter(SESSION_ID, ";jsessionid=" + inSessionID);
			}
			setStylesheetParameters(lTransformer, stylesheetParameters);
			lTransformer.transform(lXMLSource, lResult);
		}
		catch (TransformerException exc) {
			throw new XSLProcessingException("Transformer exception while performing the XSL transformation " + exc.toString());
		} 
	}
	
	/**
	 * Includes an error message to the XML.
	 * The message is included in the form
	 * <warnings>
	 *		<warning>inErrorMessage</warning>
	 * </warnings>
	 *
	 * @param inErrorMessage java.lang.String
	 */
	public void includeErrorMessage(String inErrorMessage) {
		includeInXML(WARNINGS_ELEMENT, WARNING_ELEMENT, inErrorMessage);
	}
	
	/**
	 * Includes a message to the XML.
	 *
	 * @param inGroupingTag java.lang.String
	 * @param inElementTag java.lang.String
	 * @param inMessage java.lang.String
	 */
	private void includeInXML(String inGroupingTag, String inElementTag, String inMessage) {
		Document lXML = xmlToTransform.reveal();
	
		Node lWarnings = null;
		NodeList lNodeList = lXML.getElementsByTagName(inGroupingTag);
		if (lNodeList.getLength() > 0) {
			lWarnings = lNodeList.item(0);
		}
		else {
			lWarnings = lXML.createElement(inGroupingTag);
			lXML.getDocumentElement().appendChild(lWarnings);
		}
		Element lWarning = lXML.createElement(inElementTag);
		lWarning.appendChild(lXML.createTextNode(inMessage));
		lWarnings.appendChild(lWarning);
		
		xmlToTransform = new XMLRepresentation(lXML);
	}
	
	/**
	 * Includes a message to the XML.
	 * The message is included in the form
	 * <messages>
	 *		<message>inMessage</message>
	 * </messages>
	 *
	 * @param inMessage java.lang.String
	 */
	public void includeMessage(String inMessage) {
		includeInXML(MESSAGES_ELEMENT, MESSAGE_ELEMENT, inMessage);
	}
	
	/**
	 * Removes the messages from the XML.
	 */
	public void clearMessages() {
		clearFromXML(MESSAGES_ELEMENT);
	}
	
	/**
	 * Removes the error messages from the XML.
	 */
	public void clearErrorMessages() {
		clearFromXML(WARNINGS_ELEMENT);
	}
	
	/**
	 * Removes the node with the specified tag from the XML.
	 * 
	 * @param inGroupingTag java.lang.String
	 */
	private void clearFromXML(String inGroupingTag) {
		Document lXML = xmlToTransform.reveal();
		NodeList lNodeList = lXML.getElementsByTagName(inGroupingTag);
		if (lNodeList.getLength() > 0) {
			Node lMessages = lNodeList.item(0);
			lXML.getDocumentElement().removeChild(lMessages);
			xmlToTransform = new XMLRepresentation(lXML);
		}
	}
	
	/**
	 * Sets the stylesheet parameters to the specified <code>Transformer</code>.
	 * 
	 * @param inTransformer Transformer
	 * @param inStylesheetParameters HashMap<String, Object>
	 */
	protected void setStylesheetParameters(Transformer inTransformer, HashMap<String, Object> inStylesheetParameters) {
		if (inStylesheetParameters == null) return;
		
		for (String lKey : inStylesheetParameters.keySet()) {
			inTransformer.setParameter(lKey, inStylesheetParameters.get(lKey));
		}
	}
	
}
