/*
	This package is part of the application VIF.
	Copyright (C) 2007, Benno Luthiger

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
package org.hip.vif.core.member;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;

/**
 * Interface for classes responsible for copying member information from an external store to the members cache.
 *
 * @author Luthiger
 * 23.04.2007
 */
public interface IMemberInformation {

	/**
	 * Updates the specified member object with this object's member data.  
	 * 
	 * @param inMember Member the model to update
	 * @throws SQLException
	 * @throws VException
	 */
	void update(Member inMember) throws SQLException, VException;
	
	/**
	 * Initializes the specified member object with this object's member data and triggers the insert method. 
	 * 
	 * @param inMember Member the model to insert
	 * @return Long The auto-generated value of the new entry or 0, if there's no autoincrement column.
	 * @throws SQLException
	 * @throws VException
	 */
	Long insert(Member inMember) throws SQLException, VException;
	
	/**
	 * The user ID is the string the user enters to authenticate.
	 * 
	 * @return String the user's ID.
	 */
	String getUserID();
}
