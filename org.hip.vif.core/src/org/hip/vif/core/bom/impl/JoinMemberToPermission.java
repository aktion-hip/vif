package org.hip.vif.core.bom.impl;

/*
	This package is part of application VIF.
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

import org.hip.kernel.bom.impl.DomainObjectImpl;

/**
 * This class implements the join between the domain objects
 * Member, Role and Permission.
 * 
 * @author Benno Luthiger
 */
public class JoinMemberToPermission extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinMemberToPermissionHome";

	/**
	 * Constructor for JoinMemberToPermission.
	 */
	public JoinMemberToPermission() {
		super();
	}

	/**
	 * @see GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

}
