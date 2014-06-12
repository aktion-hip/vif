/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.vif.core.util;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.util.XMLRepresentation;
import org.hip.vif.core.ApplicationConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Helper class to set and read the user settings XML in a DB text field.
 * The XML has the following form:<pre>&lt;UserSettings>
 *   &lt;language value="de" />
 *   &lt;key value="myValue" />
 *&lt;/UserSettings></pre>
 * 
 * @author Luthiger
 * Created: 12.01.2012
 */
public class UserSettings {
	private static final String ROOT_NODE = "UserSettings";
	private static final String ATTRIBUTE_NAME = "value";
	private static final String DFT_XML = String.format("%s<%s></%s>", ApplicationConstants.HEADER, ROOT_NODE, ROOT_NODE);

	private Document xml;

	/**
	 * Constructor
	 * 
	 * @param inXML String the user settings stored in the text field, may be <code>null</code>
	 */
	public UserSettings(String inXML) {
		if (inXML == null) {			
			xml = new XMLRepresentation(DFT_XML).reveal();
		}
		else if (inXML.trim().length() == 0) {
			xml = new XMLRepresentation(DFT_XML).reveal();			
		}
		else {
			xml = new XMLRepresentation(inXML).reveal();
		}
	}
	
	/**
	 * Retrieves the list of available settings, i.e. the keys of the actual user setting instance.
	 * 
	 * @return Collection<String> the user setting keys
	 */
	public Collection<String> getKeys() {
		Collection<String> outKeys = new Vector<String>();
		NodeList lNodes = xml.getDocumentElement().getChildNodes();
		for (int i = 0; i < lNodes.getLength(); i++) {
			outKeys.add(lNodes.item(i).getNodeName());
		}
		return outKeys;
	}
	
	/**
	 * Retrieves the value of the specified key from the user settings.
	 * 
	 * @param inKey String
	 * @return String the value, may be <code>null</code> if there's no setting with the specified key
	 */
	public String getValue(String inKey) {
		NodeList lNodes = xml.getElementsByTagName(inKey);
		if (lNodes.getLength() == 0) {
			return null;
		}
		return lNodes.item(0).getAttributes().getNamedItem(ATTRIBUTE_NAME).getNodeValue();
	}
	
	/**
	 * Sets the specified value for the specified key.<br />
	 * Note: This is a replacement operation. 
	 * I.e. if the user settings contain already a setting with the specified key, the value of this key is replaced. 
	 * 
	 * @param inKey String
	 * @param inValue String
	 */
	public void setValue(String inKey, String inValue) {
		NodeList lNodes = xml.getElementsByTagName(inKey);
		if (lNodes.getLength() == 0) {
			Element lSetting = xml.createElement(inKey);
			lSetting.setAttribute(ATTRIBUTE_NAME, inValue);
			xml.getDocumentElement().appendChild(lSetting);
		}
		else {
			((Element)lNodes.item(0)).setAttribute(ATTRIBUTE_NAME, inValue);
		}
	}
	
	/**
	 * Serializes the XML DOM to an XML string that can be persisted.
	 * 
	 * @return String the user settings as serialized XML 
	 * @throws ClassCastException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public String toXML() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		DOMImplementationLS lRegistry = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
		LSSerializer lWriter = lRegistry.createLSSerializer();
		return lWriter.writeToString(xml);
	}

}
