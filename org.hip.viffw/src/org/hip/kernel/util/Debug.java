package org.hip.kernel.util;

/*
	This package is part of the framework used for the application VIF.
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

import java.util.StringTokenizer;

import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.KeyObject;

/**
 * This class offers various static methods with debugging functionality
 * 
 * @author: Benno Luthiger
 */
public class Debug {

	/**
	 * Debug default constructor.
	 */
	public Debug() {
		super();
	}
	
	/**
	 * @return java.lang.String <DomainObjectName Key=inKey />
	 */
	public static String classBOMMarkupString(String inBOMName, NameValueList inKey) {
		StringBuffer lKeys = new StringBuffer("<");
		lKeys.append(inBOMName).append(">\n").append("\t<Keys>\n");
		
		for (NameValue lNameValue : inKey.getNameValues2()) {
			lKeys.append("\t\t<Key ").append(lNameValue.getName()).append("=");
			lKeys.append(((lNameValue.getValue() == null ? "null" : lNameValue.getValue().toString()))).append(" />\n");
		}
		lKeys.append("\t</Keys>\n");
		lKeys.append("</").append(inBOMName).append(">");
			
		return new String(lKeys);
	}
	
	/**
	 * @return java.lang.String <DomainObjectName Key=inKey />
	 */
	public static String classBOMMarkupString(String inBOMName, KeyObject inKey) {
		StringBuffer lKeys = new StringBuffer("<");
		lKeys.append(inBOMName).append(">\n").append("\t<Keys>\n");
		
		for (SortableItem lCriterion : inKey.getItems2()) {
			lKeys.append("\t\t<Key ").append(((KeyCriterion)lCriterion).getName()).append("=");
			lKeys.append((((KeyCriterion)lCriterion).getValue() == null ? "null" : ((KeyCriterion)lCriterion).getValue().toString())).append(" />\n");
		}
		lKeys.append("\t</Keys>\n");
		lKeys.append("</").append(inBOMName).append(">");
			
		return new String(lKeys);
	}
	
	/**
	 * @return java.lang.String <Classname inMessage />
	 */
	public static String classMarkupString(Object inObject, String inMessage) {
		return "< " + inObject.getClass().getName() + " " + inMessage + " />";
	}
	
	/**
	 * @return java.lang.String 
	 * <Classname inAttributes>
	 * 		<inMarkups>
	 * </Classname>
	 */
	public static String classMarkupString(Object inObject, String inAttributes, String inMarkups) {
		String lName = inObject.getClass().getName();
		StringBuffer outMarkup = new StringBuffer("<" + lName + " " + inAttributes + ">\n");
		StringTokenizer lTokens = new StringTokenizer(inMarkups, "\n");
		while (lTokens.hasMoreTokens()) 
			outMarkup.append("\t" + lTokens.nextToken() + "\n");
		outMarkup.append("</" + lName + ">");
		return new String(outMarkup);
	}
	
	/**
	 * @return java.lang.String <MetaModelObjectName attName attType/>
	 */
	public static String classMetaModelMarkupString(Object inObject, Object[][] inDef) {
		StringBuffer lDefs = new StringBuffer("");
		for (int i = 0; i < inDef.length; i++) {
			lDefs.append("\t<Attribute name=\"" + inDef[i][0].toString() + "\" type=\"" + inDef[i][1].toString() + "\"/>\n");
		}
			
		return "<" + inObject.getClass().getName() + ">\n" + new String(lDefs) + "</" + inObject.getClass().getName() + ">";
	}
	
	/**
	 * @return java.lang.String <MetaModelObjectName attName attType/>
	 */
	public static String classMetaModelMarkupString(Object inObject, String[][] inDef) {
		StringBuffer lDefs = new StringBuffer("");
		for (int i = 0; i <= inDef.length; i++) {
			lDefs.append("\t<Attribute name=\"" + inDef[i][0] + "\" type=\"" + inDef[i][1] + "\"/>\n");
		}
			
		return "<" + inObject.getClass().getName() + ">\n" + new String(lDefs) + "</" + inObject.getClass().getName() + ">";
	}
	
	/**
	 * @return java.lang.String <Classname>inMessage</Classname>
	 */
	public static String classMultilineMarkupString(Object inObject, String inMessage) {
		String lObjectName = inObject.getClass().getName();
		return "<" + lObjectName + ">\n" + inMessage + "</" + lObjectName + ">";
	}
	
	/**
	 * @return java.lang.String Classname [inMessage]
	 */
	public static String classTaggedString(Object inObject, String inMessage) {
		return inObject.getClass().getName() + " [" + inMessage + "]";
	}
}
