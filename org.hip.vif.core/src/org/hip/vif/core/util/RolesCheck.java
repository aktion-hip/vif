/*
	This package is part of the administration of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.vif.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.vif.core.code.Role;

/**
 * Parameter object
 * 
 * @author Benno Luthiger
 */
public class RolesCheck {
	//3: Group-Admin, 4: Participant
	private static final String[] IGNORED_IDS = {"3", "4"};

	private boolean changed = false;
	private Collection<String> newRoles;
	private Collection<String> oldRoles;
	private String oldRolesString;

	/**
	 * RolesCheck constructor.
	 * 
	 * @param inOldRoles Collection<Role> the collection of old roles
	 * @param inNewRoles String[] the array of new role names
	 */
	public RolesCheck(Collection<Role> inOldRoles, String[] inNewRoles) {
		this(inOldRoles, new Vector<String>(Arrays.asList(inNewRoles)));
	}
	
	/**
	 * RolesCheck constructor.
	 * 
	 * @param inOldRoles Collection<Role> the collection of old roles
	 * @param inNewRoles Collection<String> the collection of new roles
	 */
	public RolesCheck(Collection<Role> inOldRoles, Collection<String> inNewRoles) {
		init(inOldRoles);
		newRoles = inNewRoles;
		changed = checkRoles();
	}
	
	/**
	 * Returns true if the comparison of old and new roles proved differences.
	 * 
	 * @return boolean Default: false
	 */
	public boolean hasChanged() {
		return changed;
	}
	
	public String getOldRoles() {
		return oldRolesString;
	}
	
	public Object[] getNewRoles() {
		return (new ArrayList<String>(newRoles)).toArray();
	}
	
	private void init(Collection<Role> inRoles) {
		StringBuffer lIDs = new StringBuffer();
		boolean lFirst = true;
		oldRoles = new Vector<String>();
		for (Role lRole : inRoles) {
			if (!lFirst) {
				lIDs.append(", ");
			}
			lFirst = false;
			
			String lElementID = lRole.getElementID();
			oldRoles.add(lElementID);
			lIDs.append(lElementID);
		}
		oldRolesString = new String(lIDs);
	}
	
	private boolean checkRoles() {
		for (int i = 0; i < IGNORED_IDS.length; i++) {
			if (oldRoles.contains(IGNORED_IDS[i])) {
				if (!newRoles.contains(IGNORED_IDS[i])) {
					newRoles.add(IGNORED_IDS[i]);
				}
			}
		}
		if (oldRoles.size() != newRoles.size()) {
			return true;
		}
		return !oldRoles.containsAll(newRoles);
	}
	
	/**
	 * Checks for the existence of the specified role (RoleID) in the old roles collection.
	 * 
	 * @param inRole Integer role ID
	 * @return boolean <code>true</code> if the specified role is contained in this object's collection of old roles.
	 */
	public boolean hasRole(Integer inRole) {
		return oldRoles.contains(inRole.toString());
	}
	
}
