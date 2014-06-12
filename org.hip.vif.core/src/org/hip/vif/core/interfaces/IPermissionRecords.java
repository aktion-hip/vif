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

/**
 * Partlets can define and set new permissions to restrict users from performing the tasks defined in the partlet.
 * This interface defines the service the bundles can use to create bundle defined permissions for the application.
 *
 * @author Luthiger
 * Created: 12.07.2009
 */
public interface IPermissionRecords {
	
	/**
	 * @return IPermissionRecord[] set of {@link IPermissionRecord}, each entry in the array creates a new entry (if not existing yet) in the permission's table.
	 */
	public IPermissionRecord[] getPermissionRecords();
}
