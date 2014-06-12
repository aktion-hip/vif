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
package org.hip.vif.core.interfaces;

import org.hip.vif.core.RolesConstants;

/**
 * Interface for a parameter object to pass the information needed to create a new permission.
 *
 * @author Luthiger
 * Created: 12.07.2009
 */
public interface IPermissionRecord {

	/**
	 * @return String the label of the permission to create.
	 */
	public String getPermissionLabel();

	/**
	 * @return String the description of the permission to create.
	 */
	public String getPermissionDescription();
	
	/**
	 * @return int[] the set of roles (see {@link RolesConstants}) that are permitted, e.g. <code>new int[] {RolesConstants.ADMINISTRATOR, RolesConstants.GROUP_ADMINISTRATOR}</code>.
	 */
	public int[] getPermittedRoles();
}
