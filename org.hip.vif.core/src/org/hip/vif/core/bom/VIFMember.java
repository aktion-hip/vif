/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

package org.hip.vif.core.bom;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;

/**
 * Interface for all VIF member domain objects.
 * 
 * Created on 14.08.2003
 * @author Luthiger
 */
public interface VIFMember extends DomainObject {

	/**
	 * Does this member represent the same member as the specified member?
	 * 
	 * @param inObject VIFMember
	 * @return boolean
	 */
	boolean isSameMember(VIFMember inObject);
	
	/**
	 * Does this member represent the same member as the entry with the specified
	 * member ID?
	 * 
	 * @param inMemberID Long
	 * @return boolean
	 */
	boolean isSameMember(Long inMemberID);

	/**
	 * Returns the ID
	 * 
	 * @return Long
	 * @throws GettingException
	 */
	Long getMemberID() throws GettingException;

	/**
	 * Returns mail address
	 * 
	 * @return String
	 * @throws GettingException
	 */
	String getMailAddress() throws GettingException;

	/**
	 * Returns the gender of the member.
	 * 
	 * @return boolean True, if member is female.
	 * @throws GettingException
	 */
	boolean isFemale() throws GettingException;
	
	/**
	 * Returns the member's full name, i.e. <code>firstname name</code>.
	 * 
	 * @return java.lang.String
	 * @throws GettingException
	 */
	String getFullName() throws GettingException;
	
	/**
	 * Returns whether the gender of the member is known.
	 * 
	 * @return boolean <code>true</code> if the gender of the member is known.
	 */
	boolean hasKnownSex();
}
