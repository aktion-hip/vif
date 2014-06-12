package org.hip.kernel.bom.impl;

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

/**
 *  SimpleFilter which replaces some characters with entities. Needed to
 *  avoid complication in an xml-parser.
 * 
 * 	@author	Benno Luthiger
 */
public class XMLCharacterFilter {
	/**
	 * Default character filter to process &lt; and &amp;.
	 */	
	public final static XMLCharacterFilter DEFAULT_FILTER = getFilter();

	//member
	private final String CDATA_BEGIN = "<![CDATA[";
	private final String CDATA_END = "]]>";
	private String[][] 	charsToReplace;
	private boolean 	useCDATA = false;
	
	/**
	 * Constructor
	 * 
	 * @param inCharsToReplace String[][] a field (dimension: numberOfCharsToReplace x 2) 
	 * containing the characters to replace and the entities which replace them.
	 */
	public XMLCharacterFilter(String[][] inCharsToReplace) {
		super();
		charsToReplace = inCharsToReplace;
	}
	
	/**
	 * Constructor
	 *
	 * @param inCharsToReplace String[][] a field (dimension: numberOfCharsToReplace x 2) 
	 * containing the characters to replace and the entities which replace them.
	 * @param inUseCDATA boolean <code>true</code> if <code>&lt;![CDATA[...]]></code> has to be used to process the strings.
	 */
	public XMLCharacterFilter(String[][] inCharsToReplace, boolean inUseCDATA) {
		this(inCharsToReplace);
		charsToReplace = inCharsToReplace;
		useCDATA = inUseCDATA;
	}
	
	/**
	 * Do the filtering.
	 * 
	 * @param inString java.lang.String the string to clean
	 * @return java.lang.String the processed string
	 */
	public String filter(String inString) {	
		StringBuffer outValue = new StringBuffer();
		char c;
		String temp = "";
		
		if (inString == null || charsToReplace == null){
			return "";
		}
	
		if (useCDATA){
			outValue.append(CDATA_BEGIN);
		}
	
		for (int i=0; i<inString.length(); i++){
			c = inString.charAt(i);
			temp = String.valueOf(c);
			for (int k=0; k < charsToReplace.length; k++) {
				if (c == charsToReplace[k][0].charAt(0)) {
					temp = charsToReplace[k][1];
					break;
				}
			}
			outValue.append(temp);
		}
	
		if (useCDATA){
			outValue.append(CDATA_END);
		}
		
		return outValue.toString();
	}
	
	/**
	 * Returns Field with chars to replace.
	 * 
	 * @return String[][] a field (dimension: numberOfCharstoReplace x 2) 
	 * containing the characters to replace and the entities which replace them.
	 *
	 */
	public String[][] getCharsToReplace() {
		return charsToReplace;
	}
	
	/**
	 * Set Field with chars to replace.
	 * 
	 * @param inCharsToReplace String[][] a field (dimension: numberOfCharstoReplace x 2) 
	 * containing the characters to replace and the entities which replace them.
	 *
	 */
	public void setCharsToReplace(String[][] inCharsToReplace) {
		charsToReplace = inCharsToReplace;
	}
	
	/**
	 * Setter for <code>useCDATA</code> field.
	 * 
	 * @param inUseCD boolean <code>true</code> if <code>&lt;![CDATA[...]]></code> has to be used to process the strings.
	 */
	public void useCDATA(boolean inUseCD) {
		useCDATA = inUseCD;
	}
	
	private static XMLCharacterFilter getFilter() {
		String[][] lChars = new String[2][2];
		
		//initialize the field with the characters to replace
		lChars[0][0] = "<";
		lChars[0][1] = "&lt;";
		lChars[1][0] = "&";
		lChars[1][1] = "&amp;";
		return new XMLCharacterFilter(lChars, false);
	}
}
