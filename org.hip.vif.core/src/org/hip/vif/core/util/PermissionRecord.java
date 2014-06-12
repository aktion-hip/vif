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
package org.hip.vif.core.util;

import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.interfaces.IPermissionRecord;

/**
 * Parameter object to create a new entry in the permissions table and to associate it with specified roles.
 *
 * @author Luthiger
 * Created: 12.07.2009
 * @see RolesConstants
 */
public class PermissionRecord implements IPermissionRecord {
	private String permissionLabel;
	private String permissionDescription;
	private int[] permittedRoles;

	/**
	 * PermissionRecord constructor.
	 * 
	 * @param inPermissionLabel String
	 * @param inPermissionDescription String
	 * @param inPermittedRoles int[]
	 */
	public PermissionRecord(String inPermissionLabel, String inPermissionDescription, int[] inPermittedRoles) {
		permissionLabel = inPermissionLabel;
		permissionDescription = inPermissionDescription;
		permittedRoles = inPermittedRoles;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.interfaces.IPermissionRecord#getPermissionLabel()
	 */
	public String getPermissionLabel() {
		return permissionLabel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.interfaces.IPermissionRecord#getPermissionDescription()
	 */
	public String getPermissionDescription() {
		return permissionDescription;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.interfaces.IPermissionRecord#getPermittedRoles()
	 */
	public int[] getPermittedRoles() {
		return permittedRoles;
	}
}
