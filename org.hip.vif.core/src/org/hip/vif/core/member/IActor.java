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

package org.hip.vif.core.member;

import org.hip.vif.core.authorization.IAuthorization;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * Interface for actor classes.
 * 
 * @author Luthiger
 * Created: 12.05.2011
 */
public interface IActor {

	/**
	 * Returns the MemberID, i.e. the unique key of the Member table.
	 * 
	 * @return java.lang.Long The unique identification of the member
	 */
	Long getActorID();
	
	/**
	 * Gets the user's login id.
	 * 
	 * @return java.lang.String
	 */
	String getUserID();
	
	/**
	 * Returns the actor's authorization.
	 * 
	 * @return Authorization
	 */
	IAuthorization getAuthorization();

	/**
	 * Checks whether the actor is participant of the specified group.
	 * 
	 * @param inGroupID Long
	 * @return boolean
	 * @throws BOMChangeValueException
	 */
	boolean isRegistered(Long inGroupID) throws BOMChangeValueException;
	
	/**
	 * Checks whether the actor is administering the specified group.
	 * 
	 * @param inGroupID Long
	 * @return boolean
	 * @throws BOMChangeValueException
	 */
	boolean isGroupAdmin(Long inGroupID) throws BOMChangeValueException;
	
	/**
	 * Refreshes the actor's authorization, i.g. after she has registered and,
	 * therefore, became participant.
	 * 
	 * @throws BOMChangeValueException
	 */
	void refreshAuthorization() throws BOMChangeValueException;

	/**
	 * Checks whether this actor is guest or not.
	 * 
	 * @return boolean, true, if this actor is logged in as guest.
	 */
	boolean isGuest();
	
}
