/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;

/**
 * RoleHome is responsible to manage instances of class org.hip.vif.bom.Role.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Role
 */
public interface RoleHome extends DomainObjectHome {
	public final static String KEY_ID = "ID";
	public final static String KEY_CODE_ID = "CodeID";
	public final static String KEY_DESCRIPTION = "Description";
	public final static String KEY_GROUP_SPECIFIC = "GroupSpecific";

	public final static int ROLE_SU = 1;
	public final static int ROLE_ADMIN = 2;
	public final static int ROLE_GROUP_ADMIN = 3;
	public final static int ROLE_PARTICIPANT = 4;
	public final static int ROLE_MEMBER = 5;
	public final static int ROLE_GUEST = 6;
	public final static int ROLE_EXCLUDED = 7;

	/**
	 * Retrieves all roles that are group specific.
	 * 
	 * @return Collection of IDs of those roles that are group specific
	 * @throws VException
	 * @throws SQLException
	 */
	Collection<String> getGroupSpecificIDs() throws VException, SQLException;

}
