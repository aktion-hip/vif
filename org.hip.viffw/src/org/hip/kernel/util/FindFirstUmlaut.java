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
package org.hip.kernel.util;

/**
 * This class is a utility class.
 * The class searchs for umlauts in a specified string.
 * The position and value of the first found umlaut are remembered.
 *
 * @author	Benno Luthiger
 */
public class FindFirstUmlaut {
	private int position = -1;
	private boolean found = false;
	private String foundUmlaut;
	private String foundCorrespondingUmlaut;
	private String starting;
	private String ending;
	
	// we only compar uppercases
	private static String[] umlautsShort = {"Ä", "Ö", "Ü"};
	private static String[] umlautsLong = {"AE", "OE", "UE"};
	
	/**
	 * FindFirstUmlaut constructor for the specified string.
	 */
	public FindFirstUmlaut(String inSearchString) {
		super();
	
		checkForUmlauts(inSearchString, umlautsShort);
		checkForUmlauts(inSearchString, umlautsLong);
	}
	private void checkForUmlauts(String inSearchString, String[] inUmlauts) {
		
		int lFoundPosition;
		for (int i = 0; i < inUmlauts.length; i++) {
			if ((lFoundPosition = inSearchString.indexOf(inUmlauts[i])) >= 0) {
				if ((position < 0) || ((position > 0) & (lFoundPosition < position))) {
					position = lFoundPosition;
					foundUmlaut = umlautsShort[i];
					foundCorrespondingUmlaut = umlautsLong[i];
					starting = inSearchString.substring(0, lFoundPosition);
					ending = inSearchString.substring(lFoundPosition + inUmlauts[i].length());
					found = true;
				} // if
			} // if
		}	// for
	}
	public String getEnding() {
		return ending;
	}
	public String getFoundCorresponding() {
		return foundCorrespondingUmlaut;
	}
	public String getFoundUmlaut() {
		return foundUmlaut;
	}
	public int getPosition() {
		return position;
	}
	public String getStarting() {
		return starting;
	}
	public boolean hasUmlauts() {
		return found;
	}
	
	/**
	 * @return java.lang.String
	 */
	public String toString() {
		String lMessage = "";
		if (hasUmlauts()) {
			lMessage = "Umlaut found at position " + getPosition();
		}
		else {
			lMessage = "No umlaut found";
		}
		return Debug.classMarkupString(this, lMessage);
	}
}
