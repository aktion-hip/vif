package org.hip.kernel.bitmap;

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

import org.hip.kernel.sys.VObject;

/**
 * Simple object representing a position identified by two Strings.
 * This object is useful for manipulation linking DomainObjects.
 * 
 * @author Benno Luthiger
 */
public class IDPosition extends VObject {
	private String rowID;
	private String columnID;
	
	/**
	 * Constructor for IDPosition with specified IDs.
	 * 
	 * @param inRowID java.lang.String
	 * @param inColumnID java.lang.String
	 */
	public IDPosition(String inRowID, String inColumnID) {
		super();
		rowID = inRowID;
		columnID = inColumnID;
	}
	
	public String getRowID() {
		return rowID;
	}
	
	public String getColumnID() {
		return columnID;
	}	

	/**
	 * Returns a hash code value for this bit pattern.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return rowID.hashCode() ^ columnID.hashCode();
	}
	
	/**
	 * Compares this object against the specified object.
	 * 
	 * @param inObject java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (getClass() != inObject.getClass()) return false;
		
		IDPosition lToCompare = (IDPosition)inObject;
		return (rowID.equals(lToCompare.getRowID())) && 
				(columnID.equals(lToCompare.getColumnID()));
	}
	
	/**
	 * Returns a string representation of this bit pattern.
	 * 
	 * @param java.lang.String
	 */
	public String toString() {
		return "<Position rowID='" + rowID +
				"' columnID='" + columnID + "'/>";
	}
}
