package org.hip.kernel.util;

/*
	This package is part of the xml extendsions used for the application VIF.
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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.hip.kernel.exc.VError;
import org.w3c.dom.Document;

/**
 * An instance of this class represents an XML.
 * The representation is indifferent concerning the form of the XML, e.g.
 * the XML may be present either as String or as org.w3c.dom.Document.
 * The XML represented here can only be accessed as Document.
 * 
 * @author: Benno Luthiger
 * @see: org.w3c.dom.Document
 */
public class XMLRepresentation implements Serializable {
	private String xmlAsString;
	private Document document;
	
	/**
	 * XMLRepresentation constructor for xml-Document.
	 *
	 * @param inXML org.w3c.dom.Document
	 */
	public XMLRepresentation(String inXML) {
		super();
		xmlAsString = inXML;
		document = null;
	}

	/**
	 * XMLRepresentation constructor for xml-Document.
	 *
	 * @param inXML org.w3c.dom.Document
	 */
	public XMLRepresentation(Document inXML) {
		super();
		xmlAsString = "";
		document = inXML;
	}

	/**
	 * Returns the XML as Document.
	 *
	 * @return org.w3c.dom.Document
	 */
	public Document reveal() {
		if (document == null) {
			StringReader lReader = new StringReader(xmlAsString);
			try {
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(lReader));
			}
			catch (SAXException exc) {
				throw createError(exc);
			}
			catch (IOException exc) {
				throw new Error("Problems encountered while creating the DOM : " + exc.toString());
			}
			catch (ParserConfigurationException exc) {
				throw createError(exc);
			}
		}
		return document;
	}

	private Error createError(Throwable exc) {
		VError outError = new TransformationError("Problems encountered while creating the DOM : " + exc.toString());
		outError.setRootCause(exc);
		return outError;
	}
	
	
}
