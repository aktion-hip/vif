package org.hip.kernel.code;

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

import java.net.URL;
import java.util.Hashtable;

/**
 * Home of <code>CodeList</code>s.
 *
 * @author: Benno Luthiger
 */
public final class CodeListHome {

  	//instance attributes
  	private Hashtable<String, Hashtable<String, CodeList>> codeListHashtablesPerLanguage;

  	//class attributes
  	private static CodeListHome theCodeHome;
  	
	/**
	 * CodeListHome singleton constructor.
	 */
	private CodeListHome() {   
		codeListHashtablesPerLanguage = new Hashtable<String, Hashtable<String, CodeList>>(5);
	}
	
	/**
	 * Returns an instance of the CodeList the specified CodeClass belongs to.
	 *
	 * @return org.hip.kernel.code.CodeList
	 * @param inCodeClass java.lang.Class
	 * @param inLanguage java.lang.String
	 * @exception org.hip.kernel.code.CodeListNotFoundException The exception description.
	 */
	public CodeList getCodeList(Class<?> inCodeClass, String inLanguage) throws CodeListNotFoundException {
		try {
			String lCodeID = inCodeClass.getField("CODEID").get(new String()).toString();
			URL lUrl = inCodeClass.getResource("/" + lCodeID + CodeListFactory.FILE_EXTENSION);
			return getCodeList(lCodeID, inLanguage, lUrl);
		} 
		catch (Exception exc) {
			throw new CodeListNotFoundException();	
		}
	}
	
	public CodeList getCodeList(String inCodeID, String inLanguage, URL inUrl) throws CodeListNotFoundException {
	  	CodeList outCodeList;
	  	Hashtable<String, CodeList> lTableOfCodeLists;
	  	lTableOfCodeLists = codeListHashtablesPerLanguage.get(inLanguage);
	  	
	  	if (lTableOfCodeLists == null) {
			lTableOfCodeLists = new Hashtable<String, CodeList>(11);
			codeListHashtablesPerLanguage.put(inLanguage, lTableOfCodeLists);
	  	}
	  	
	  	outCodeList = lTableOfCodeLists.get(inCodeID);
	  	if (outCodeList == null) {
			try {
		  		outCodeList = new CodeListFactory().createCodeList(inCodeID, inLanguage, inUrl);
			} 
			catch (Exception exc) {
		  		// do something special?
			}
			
			if (outCodeList != null) {
				lTableOfCodeLists.put(inCodeID, outCodeList);
			}
	  	}
	  	
	  	return outCodeList;
	}
	
	/**
	 * Get the prepared CodeList for given CodeID, or prepare one, if it doesn't exists yet.
	 *
	 * @param String codeID
	 * @param String inLanguage  as defined by java.util.Locale
	 * @return the CodeList which belongs to the codeID
	 */
	public CodeList getCodeList(String inCodeID, String inLanguage) throws CodeListNotFoundException {
		return getCodeList(inCodeID, inLanguage, null);
	}
	
	/**
	 * Return the singleton object.
	 *
	 * @return the CodeListHome
	 */
	public static CodeListHome instance() {		
	  	if (theCodeHome == null) {
			theCodeHome = new CodeListHome();
	  	}	  
	  	return theCodeHome;
	}
	
}
