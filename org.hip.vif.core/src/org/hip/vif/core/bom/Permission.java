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

import org.hip.kernel.bom.DomainObject;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/** This interface defines the behavior of the Permission domain object. A Permission specifies the actions a Member can
 * carry out.
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Role
 * @see org.hip.vif.core.bom.Member */
public interface Permission extends DomainObject {

    /** Create a new permission
     *
     * @param inLabel java.lang.String
     * @param inDescription java.lang.String
     * @return Long The auto-generated value of the new entry
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException
     * @exception org.hip.vif.core.exc.bom.impl.ExternIDNotUniqueException */
    public Long ucNew(String inLabel, String inDescription) throws BOMChangeValueException, ExternIDNotUniqueException;
}
