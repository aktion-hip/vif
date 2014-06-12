/*
	This package is part of the persistency layer of the application VIF.
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

package org.hip.vif.core.bom;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/**
 * Interface for home of the Permission domain object.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Permission
 */
public interface PermissionHome extends DomainObjectHome {
	public final static String KEY_ID 			= "ID";
	public final static String KEY_LABEL 			= "Label";
	public final static String KEY_DESCRIPTION		= "Description";

	/**
	 * Returns the Permission identified by the specified permission ID.
	 * 
	 * @param inPermissionID Long
	 * @return {@link Permission}
	 * @throws org.hip.kernel.bom.BOMInvalidKeyException
	 */
	Permission getPermission(Long inPermissionID) throws BOMInvalidKeyException;
	/**
	 * @param inPermissionID
	 * @return {@link Permission}
	 * @deprecated
	 * @throws BOMInvalidKeyException
	 */
	Permission getPermission(String inPermissionID) throws BOMInvalidKeyException;
	
	/**
	 * Creates a new permission entry.
	 * 
	 * @param inLabel String
	 * @param inDescription String
	 * @return Long The auto-generated value of the new entry.
	 * @throws BOMException
	 * @throws ExternIDNotUniqueException
	 */
	public Long createPermission(String inLabel, String inDescription) throws BOMException, ExternIDNotUniqueException;
	
}
