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

package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;

/**
 * Abstract class providing basic functionality for all member domain objects,
 * e.g. joins to the member table.
 * 
 * Created on 14.08.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
public abstract class AbstractMember extends DomainObjectImpl implements VIFMember {
	private final String TMPL = "%s %s";
	
	/**
	 * Does this member represent the same member as the specified member?
	 * 
	 * @param inObject VIFMember
	 * @return boolean
	 */
	public boolean isSameMember(VIFMember inObject) {
		try {
			if (inObject.get(MemberHome.KEY_ID).toString().equals(getMemberID().toString()))
				return true;
			return false;
		}
		catch (Exception exc) {
			return false;
		}
	}
	
	/**
	 * Does this member represent the same member as the entry with the specified
	 * member ID?
	 * 
	 * @param inMemberID Long
	 * @return boolean
	 */
	public boolean isSameMember(Long inMemberID) {
		try {
			if (getMemberID().longValue() == inMemberID.longValue())
				return true;
			return false;
		}
		catch (Exception exc) {
			return false;
		}
	}
	
	/**
	 * Returns the ID
	 * 
	 * @return Long
	 * @throws GettingException
	 */
	public Long getMemberID() throws GettingException {
		return new Long(get(MemberHome.KEY_ID).toString());
	}
	
	/**
	 * Returns mail address
	 * 
	 * @return String
	 * @throws GettingException
	 */
	public String getMailAddress() throws GettingException {
		return (String)get(MemberHome.KEY_MAIL);
	}
	
	public boolean hasKnownSex() {
		try {
			return ((Number)get(MemberHome.KEY_SEX)).intValue() >= 0;
		} catch (GettingException exc) {
			// intentionally left empty
		}
		return false;
	}

	/**
	 * Returns the gender of the member.
	 * 
	 * @return boolean True, if member is female.
	 * @throws GettingException
	 */
	public boolean isFemale() throws GettingException {
		return ((Number)get(MemberHome.KEY_SEX)).intValue() == 1;
	}

	/**
	 * Returns the member's full name, i.e. <code>firstname name</code>.
	 * 
	 * @return java.lang.String
	 * @throws GettingException
	 */
	public String getFullName() throws GettingException {
		return String.format(TMPL, (String)get(MemberHome.KEY_FIRSTNAME), (String)get(MemberHome.KEY_NAME));
	}
}
