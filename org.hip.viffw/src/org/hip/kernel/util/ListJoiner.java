/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.kernel.util;

import java.util.Collection;
import java.util.Vector;

/**
 * Helper class to join list entries that may be empty.
 * Empty entries are disregarded when the list is joined.
 *
 * @author Luthiger
 * Created on 29.05.2007
 */
public class ListJoiner {
	private Collection<String> entries = new Vector<String>();

	/**
	 * Adds an entry to the list.
	 * 
	 * @param inMainValue String May be empty <code>String</code>.
	 */
	public void addEntry(String inMainValue) {
		String lTrimmed = inMainValue.trim();
		if (lTrimmed.length() > 0) {
			entries.add(lTrimmed);
		}
	}
	
	/**
	 * Adds a formatted entry to the list.
	 * 
	 * @param inMainValue String May be empty <code>String</code>.
	 * @param inFormatString String format string, e.g. "%1$s %2$s"
	 * @param inValues String... additional values for the formatted string entry.
	 * @see String#format(String, Object[])
	 */
	public void addEntry(String inMainValue, String inFormatString, String... inValues) {
		String lTrimmed = inMainValue.trim();
		if (lTrimmed.length() > 0) {
			Object[] lArgs = new String[inValues.length + 1];
			lArgs[0] = lTrimmed;
			System.arraycopy(inValues, 0, lArgs, 1, inValues.length);
			entries.add(String.format(inFormatString, lArgs).trim());
		}
	}

	/**
	 * Joins the list using the specified separator (and a space added) between the entries.
	 * 
	 * @param inSeparator String
	 * @return String the consolidated list joined with the separator
	 */
	public String joinSpaced(String inSeparator) {
		return join(inSeparator, " ");		
	}
	
	/**
	 * Joins the list using the specified separator between the entries.
	 * 
	 * @param inSeparator
	 * @return the consolidated list joined with the separator
	 */
	public String join(String inSeparator) {
		return join(inSeparator, "");		
	}
	
	private String join(String inSeparator, String inAdd) {
		StringBuffer outJoined = new StringBuffer();
		boolean isFirst = true;
		for (String lEntry: entries) {
			if (!isFirst) {
				outJoined.append(inSeparator).append(inAdd);
			}
			isFirst = false;
			outJoined.append(lEntry);
		}
		return new String(outJoined);		
	}
	
}
