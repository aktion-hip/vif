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

import java.sql.SQLException;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Permission;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/**
 * This domain object implements the Permission interface.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Permission
 */
@SuppressWarnings("serial")
public class PermissionImpl extends DomainObjectImpl implements Permission {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.PermissionHomeImpl";

	/**
	 * Constructor for PermissionImpl.
	 */
	public PermissionImpl() {
		super();
	}

	/**
	 * @see GeneralDomainObject#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	/**
	 * Create a new permission
	 * 
	 * @param inLabel java.lang.String
	 * @param inDescription java.lang.String
	 * @return Long The auto-generated value of the new entry 
	 * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException 
	 * @exception org.hip.vif.core.exc.bom.impl.ExternIDNotUniqueException
	 */ 
	public Long ucNew(String inLabel, String inDescription) throws BOMChangeValueException, ExternIDNotUniqueException {
		//pre: Label must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck", !"".equals(inLabel));

		PermissionHome lHome = (PermissionHome)BOMHelper.getPermissionHome();
		KeyObject lKey = new KeyObjectImpl();
		try {
			lKey.setValue(PermissionHome.KEY_LABEL, inLabel);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		
		//check whether the key exists yet
		try {
			lHome.findByKey(lKey);
		}
		catch (BOMNotFoundException exc) {
			try {
				set(PermissionHome.KEY_LABEL, inLabel);
				set(PermissionHome.KEY_DESCRIPTION, inDescription);
				return insert(true);
			}
			catch (VException exc2) {
				throw new BOMChangeValueException(exc.getMessage());
			}
			catch (SQLException exc2) {
				throw new BOMChangeValueException(exc.getMessage());
			}
		}
		catch (BOMInvalidKeyException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		
		//shouldn't reach
		throw new ExternIDNotUniqueException("Label " + inLabel + " not unique!");
	}
}
