/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.core.util;

import org.hip.vif.core.ApplicationConstants;

/**
 * Helper class to build xml for VIF views.
 *
 * @author Luthiger
 * Created: 16.07.2010
 */
public class XMLBuilder {
	private static final String TMPL_TAG = "<%s>%s</%s>";

	private String rootNodeBegin = ApplicationConstants.ROOT_BEGIN;
	private String rootNodeEnd = ApplicationConstants.ROOT_END;
	private StringBuilder xml;

	/**
	 * No argument constructor.
	 */
	public XMLBuilder() {
		xml = new StringBuilder();
	}
	
	/**
	 * @param inXML String initial content.
	 */
	public XMLBuilder(String inXML) {
		xml = new StringBuilder(inXML);
	}

	/**
	 * @param inXML StringBuilder initial content.
	 */
	public XMLBuilder(StringBuilder inXML) {
		xml = inXML;
	}

	/**
	 * Adds the specified content.
	 * 
	 * @param inXML String to add.
	 * @return {@link XMLBuilder}
	 */
	public XMLBuilder inject(String inXML) {
		xml.append(inXML);
		return this;
	}
	
	/**
	 * Creates a node with the specified tag and content and adds it.
	 * 
	 * @param inTag String
	 * @param inContent Object
	 * @return {@link XMLBuilder}
	 */
	public XMLBuilder inject(String inTag, Object inContent) {
		xml.append(createNode(inTag, inContent));
		return this;
	}
	
	/**
	 * Adds the specified content.
	 * 
	 * @param inXML StringBuilder to add.
	 * @return {@link XMLBuilder}
	 */
	public XMLBuilder inject(StringBuilder inXML) {
		xml.append(inXML);
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(ApplicationConstants.HEADER);
		out.append(rootNodeBegin);
		out.append(xml);
		out.append(rootNodeEnd);
		return new String(out);
	}
	
	/**
	 * Convenience method.
	 * 
	 * @param inTag String
	 * @param inContent Object
	 * @return String <code>&lt;inTag>inContent&lt;/inTag></code>
	 */
	public static String createNode(String inTag, Object inContent) {
		return String.format(TMPL_TAG, inTag, inContent, inTag);
	}
	
	/**
	 * Sets an alternative root node name, default is <code>Root</code>.
	 * 
	 * @param inRootNodeName String
	 */
	public void setRootNodeName(String inRootNodeName) {
		rootNodeBegin = String.format("<%s>", inRootNodeName);
		rootNodeEnd = String.format("</%s>", inRootNodeName);
	}

}
