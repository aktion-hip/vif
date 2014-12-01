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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.vif.core.bom.Role;

/** This domain object implements the Role interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Role */
@SuppressWarnings("serial")
public class RoleImpl extends DomainObjectImpl implements Role {

    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.RoleHomeImpl";

    /** Constructor for RoleImpl. */
    public RoleImpl() {
        super();
    }

    /** @see GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

}
