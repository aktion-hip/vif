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

package org.hip.vif.web.internal.controller;

import org.hip.vif.core.interfaces.IPermissionRecords;
import org.hip.vif.web.controller.PermissionRegistry;

/**
 * The client class for service components implementing the <code>IPermissionRecords</code> interface.
 * 
 * @author Luthiger
 * Created: 30.12.2011
 */
public class PermissionsController {
	
	/**
	 * Bundles can define permissions and make them available in the application.
	 * 
	 * @param inPermissions {@link IPermissionRecords}
	 */
	public void registerPermissions(IPermissionRecords inPermissions) {
		if (inPermissions == null) return;
		PermissionRegistry.INSTANCE.registerPermissions(inPermissions);
	}

}
