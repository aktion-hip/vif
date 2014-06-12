package org.hip.vif.core.search;

/*
	This package is part of the application VIF.
	Copyright (C) 2005, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	General Public License for more details.

	You should have received a copy of the GNU General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/**
 * Helper class to create a full text search field gathering the content
 * of various fields.
 * 
 * @author Benno Luthiger
 */
public class FullTextHelper {
	StringBuilder fullText;
	
	public FullTextHelper() {
		fullText = new StringBuilder();
	}
	/**
	 * Adds the String passed to the full text.
	 * 
	 * @param inSearch String must not be <code>null</code>
	 * @return String
	 */
	public String add(String inSearch) {
		fullText.append(inSearch).append(" ");
		return inSearch;
	}
	/**
	 * Adds the Object passed to the full text after checking that it's not <code>null</code>.
	 * 
	 * @param inSearch Object may be null
	 * @return String the passed object's string version or an empty string.
	 */
	public String add(Object inSearch) {
		if (inSearch == null) return "";
		return add(inSearch.toString());
	}
	public String getFullText() {
		return new String(fullText);
	}
}