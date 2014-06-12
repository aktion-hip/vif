/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.AlternativeModel;

/**
 * Alternative model for LinkPermissionRole.
 *
 * @author Luthiger
 * Created: 04.01.2009
 */
public class LinkPermissionRoleAlternate implements AlternativeModel {
	public final static String ALTERNATE_HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.LinkPermissionRoleHomeAlternate";

	private long permissionID;
	private long roleID;

	/**
	 * Constructor
	 * 
	 * @param inPermissionID long
	 * @param inRoleID long
	 */
	public LinkPermissionRoleAlternate(long inPermissionID, long inRoleID) {
		permissionID = inPermissionID;
		roleID = inRoleID;
	}

	public long getPermissionID() {
		return permissionID;
	}

	public long getRoleID() {
		return roleID;
	}

	/**
	 * Checks whether this association belongs to the specified permission. 
	 * 
	 * @param inPermissionID long
	 * @return boolean <code>true</code> if this association belongs to the specified permission
	 */
	public boolean isAssociatedToPermission(long inPermissionID) {
		return permissionID == inPermissionID;
	}
	
}