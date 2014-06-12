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
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.AbstractDomainObjectHome;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.LinkMemberRole;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.RolesCheck;

/**
 * This domain object home implements the LinkMemberRoleHome interface.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.LinkMemberRoleHome
 */
public class LinkMemberRoleHomeImpl extends DomainObjectHomeImpl implements LinkMemberRoleHome {
		
	/*  Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String LINK_CLASS_NAME = "org.hip.vif.core.bom.impl.LinkMemberRoleImpl";
	
	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<objectDef objectName='LinkMemberRole' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_MEMBER_ID + "'/>	\n" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_ROLE_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_MEMBER_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblLinkMemberRole' columnName='MemberID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_ROLE_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblLinkMemberRole' columnName='RoleID'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";

	/**
	 * @see GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return LINK_CLASS_NAME;
	}

	/**
	 * @see AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/**
	 * Returns all roles associated with the specified member
	 * 
	 * @param inMemberID java.lang.Long ID of the members entry
	 * @return org.hip.vif.util.Roles A collection containing the associated roles
	 * @throws org.hip.kernel.bom.BOMInvalidKeyException
	 */
	public Collection<Role> getRolesOf(Long inMemberID) throws BOMInvalidKeyException {
		KeyObject lKey = new KeyObjectImpl();
		try {
			lKey.setValue(KEY_MEMBER_ID, inMemberID);
			QueryResult lRoles = select(lKey);
			Collection<Role> outRoles = new Vector<Role>();
			while (lRoles.hasMoreElements()) {
				outRoles.add(new Role(lRoles.next().get(KEY_ROLE_ID).toString()));
			}
			return outRoles;
		}
		catch (VException exc) {
			throw new BOMInvalidKeyException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMInvalidKeyException(exc.getMessage());
		}
	}
	
	/**
	 * Checkes whether the associated roles have changed by comparing the
	 * old roles with the new ones.
	 * 
	 * @param inMemberID java.lang.Long ID of the members entry
	 * @param inNewRoles java.lang.String[] Array containing the IDs of the new roles
	 * @return org.hip.vif.util.RolesCheck
	 * @throws org.hip.kernel.bom.BOMInvalidKeyException
	 */
	public RolesCheck checkRolesOf(Long inMemberID, String[] inNewRoles) throws BOMInvalidKeyException {
		return new RolesCheck(getRolesOf(inMemberID), inNewRoles);
	}
	public RolesCheck checkRolesOf(Long inMemberID, Collection<String> inNewRoles) throws BOMInvalidKeyException {
		return new RolesCheck(getRolesOf(inMemberID), inNewRoles);
	}
	
	/**
	 * Associates the indicated roles to the specified member
	 * 
	 * @param inMemberID java.lang.Long
	 * @param inRoles Object[]
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public void associateRoles(Long inMemberID, Object[] inRoles) throws BOMChangeValueException {
		for (int i = 0; i < inRoles.length; i++) {
			associateRole(inMemberID, Integer.valueOf((String)inRoles[i]));
		}
	}
	public void associateRoles(Long inMemberID, Collection<String> inRoles) throws BOMChangeValueException {
		for (String lRoleID : inRoles) {
			associateRole(inMemberID, Integer.valueOf(lRoleID));
		}
	}

	/**
	 * Delete all roles associated to the specified member
	 * 
	 * @param inMemberID java.lang.Long
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public void deleteRolesOf(Long inMemberID) throws BOMChangeValueException {
		try {
			KeyObject lMemberKey = new KeyObjectImpl();
			lMemberKey.setValue(KEY_MEMBER_ID, inMemberID);		
			delete(lMemberKey, true);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Creates a GroupAdmin Role for the specified member
	 * 
	 * @param inMemberID Long
	 * @throws BOMChangeValueException
	 */
	public void createGroupAdminRole(Long inMemberID) throws BOMChangeValueException {
		associateRole(inMemberID, ROLE_GROUP_ADMIN);
	}
	
	/**
	 * Creates a Participant Role for the specified member
	 * 
	 * @param inMemberID Long
	 * @throws BOMChangeValueException
	 */
	public void createParticipantRole(Long inMemberID) throws BOMChangeValueException {
		associateRole(inMemberID, ROLE_PARTICIPANT);
	}
	
	private void associateRole(Long inMemberID, Integer inRole) throws BOMChangeValueException {
		try {
			LinkMemberRole lLink = (LinkMemberRole)create();
			lLink.set(KEY_ROLE_ID, inRole);
			lLink.set(KEY_MEMBER_ID, inMemberID);
			lLink.insert(true);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Deletes the group admin role of the specified member.
	 * 
	 * @param inMemberID Long
	 * @throws BOMChangeValueException
	 */
	public void deleteGroupAdminRole(Long inMemberID) throws BOMChangeValueException {
		deleteRole(inMemberID, ROLE_GROUP_ADMIN);
	}
	
	/**
	 * Deletes the participant role of the specified member.
	 * 
	 * @param inMemberID Long
	 * @throws BOMChangeValueException
	 */
	public void deleteParticipantRole(Long inMemberID) throws BOMChangeValueException {
		deleteRole(inMemberID, ROLE_PARTICIPANT);
	}
	
	/**
	 * Deletes the specified role of the specified member.
	 * 
	 * @param inMemberID Long
	 * @param inRole Integer
	 * @throws BOMChangeValueException
	 */
	public void deleteRole(Long inMemberID, Integer inRole) throws BOMChangeValueException {
		try {
			KeyObject lMemberKey = new KeyObjectImpl();
			lMemberKey.setValue(KEY_MEMBER_ID, inMemberID);
			lMemberKey.setValue(KEY_ROLE_ID, inRole);
			delete(lMemberKey, true);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	private boolean hasRole(Long inMemberID, Integer inRole) throws BOMChangeValueException {
		try {
			RolesCheck lRolesCheck = new RolesCheck(getRolesOf(inMemberID), new String[] {});
			return lRolesCheck.hasRole(inRole);
		}
		catch (BOMInvalidKeyException exc) {
			throw new BOMChangeValueException(exc.getMessage());			
		}
	}
	
	/**
	 * Checks whether the specified member has the PARTICIPANT role.
	 * 
	 * @param inMemberID Long
	 * @return boolean true, if the specified member is PARTICIPANT
	 * @throws BOMChangeValueException
	 */
	public boolean hasRoleParticipant(Long inMemberID) throws BOMChangeValueException {
		return hasRole(inMemberID, ROLE_PARTICIPANT);
	}
	
	/**
	 * Checks whether the specified member has the GROUP_ADMIN role.
	 * 
	 * @param inMemberID Long
	 * @return boolean true, if the specified member is GROUP_ADMIN
	 * @throws BOMChangeValueException
	 */
	public boolean hasRoleGroupAdmin(Long inMemberID) throws BOMChangeValueException {
		return hasRole(inMemberID, ROLE_GROUP_ADMIN);
	}

	public boolean updateRoles(Long inMemberID, Collection<String> inRoles) throws BOMChangeValueException {
		try {
			RolesCheck lRolesCheck = checkRolesOf(inMemberID, inRoles);
			if (lRolesCheck.hasChanged()) {
				deleteRolesOf(inMemberID);
				associateRoles(inMemberID, lRolesCheck.getNewRoles());
			}
			return lRolesCheck.hasChanged();
		} 
		catch (BOMInvalidKeyException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
}
