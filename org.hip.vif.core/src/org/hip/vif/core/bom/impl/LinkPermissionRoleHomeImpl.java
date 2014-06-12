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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bitmap.IDPosition;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.AbstractDomainObjectHome;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;
import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.bom.LinkPermissionRole;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This domain object home implements the LinkPermissionRoleHome interface.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.LinkPermissionRoleHome
 */
public class LinkPermissionRoleHomeImpl extends DomainObjectHomeImpl implements LinkPermissionRoleHome {
	
	/* Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String LINK_PERMISSION_ROLE_CLASS_NAME = "org.hip.vif.core.bom.impl.LinkPermissionRoleImpl";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<objectDef objectName='LinkPermissionRole' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_PERMISSION_ID + "'/>	\n" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_ROLE_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_PERMISSION_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblLinkPermissionRole' columnName='PERMISSIONID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_ROLE_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblLinkPermissionRole' columnName='ROLEID'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";
		
	/**
	 * Constructor for LinkPermissionRoleHomeImpl.
	 */
	public LinkPermissionRoleHomeImpl() {
		super();
	}

	/**
	 * @see GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return LINK_PERMISSION_ROLE_CLASS_NAME;
	}

	/**
	 * @see AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Retrieves the DomainObject identified by the specified IDPosition
	 * 
	 * @param inPosition org.hip.kernel.bitmap.IDPosition
	 * @return org.hip.kernel.bom.DomainObject
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public DomainObject getEntry(IDPosition inPosition) throws BOMChangeValueException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_PERMISSION_ID, new Long(inPosition.getRowID()));
			lKey.setValue(KEY_ROLE_ID, new Long(inPosition.getColumnID()));
			
			return findByKey(lKey);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	public DomainObject getEntry(Long inPermissionID, int inRoleID) throws BOMChangeValueException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_PERMISSION_ID, inPermissionID);
			lKey.setValue(KEY_ROLE_ID, new Long(inRoleID));
			return findByKey(lKey);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Deletes the specified associations of permissions with roles.
	 * 
	 * @param inPositions Collection<IDPosition>
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public void delete(Collection<IDPosition> inPositions) throws BOMChangeValueException {
		try {
			for (IDPosition lPosition : inPositions) {
				getEntry(lPosition).delete(true);
			}
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	public void delete(Long inPermissionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_PERMISSION_ID, inPermissionID);
		delete(lKey, true);
	}
	
	/**
	 * Creates the specified associations of permissions with roles.
	 * 
	 * @param inPositions Collection<IDPosition>
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public void create(Collection<IDPosition> inPositions) throws BOMChangeValueException {
		try {
			LinkPermissionRole lLink = (LinkPermissionRole)create();
			for (IDPosition lPosition : inPositions) {
				lLink.setVirgin();
				lLink.set(KEY_PERMISSION_ID, new BigDecimal(lPosition.getRowID()));
				lLink.set(KEY_ROLE_ID, new BigDecimal(lPosition.getColumnID()));
				lLink.insert(true);
			}
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Associates the specified permission with the specified role.
	 * 
	 * @param inPermissionID Long the permission entry's id
	 * @param inRoleID int, the role entry's id, see {@link RolesConstants}. 
	 * @throws BOMChangeValueException
	 */
	public void createLink(Long inPermissionID, int inRoleID) throws BOMChangeValueException {
		try {
			LinkPermissionRole lLink = (LinkPermissionRole) create();
			lLink.set(KEY_PERMISSION_ID, inPermissionID);
			lLink.set(KEY_ROLE_ID, inRoleID);
			lLink.insert(true);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
}
