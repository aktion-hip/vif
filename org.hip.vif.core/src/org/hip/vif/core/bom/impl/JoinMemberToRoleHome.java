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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * Home of join from the Member BOM to the Roles BOM
 * 
 * Created on 09.08.2002
 * @author Benno Luthiger
 */
public class JoinMemberToRoleHome extends JoinedDomainObjectHomeImpl {
	public final static String KEY_ALIAS_ROLE_INT_ID = "RoleIntID";
	public final static String KEY_ALIAS_ROLE_ID = "RoleID";
	
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinMemberToRole";

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
		"<joinedObjectDef objectName='JoinMemberToRole' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + LinkMemberRoleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.LinkMemberRoleImpl'/>	\n" +
		"		<columnDef columnName='" + RoleHome.KEY_ID + "' alias='" + KEY_ALIAS_ROLE_INT_ID + "' domainObject='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"		<columnDef columnName='" + RoleHome.KEY_CODE_ID + "' alias='" + KEY_ALIAS_ROLE_ID + "' domainObject='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"		<columnDef columnName='" + RoleHome.KEY_DESCRIPTION + "' domainObject='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"		<columnDef columnName='" + RoleHome.KEY_GROUP_SPECIFIC + "' domainObject='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.LinkMemberRoleImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + LinkMemberRoleHome.KEY_ROLE_ID + "' domainObject='org.hip.vif.core.bom.impl.LinkMemberRoleImpl'/>	\n" +
		"			<columnDef columnName='" + RoleHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.RoleImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * Constructor for JoinMemberToRoleHome.
	 */
	public JoinMemberToRoleHome() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return JOIN_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Returns the roles for the specified member.
	 * 
	 * @param inMemberID Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public QueryResult select(Long inMemberID) throws BOMChangeValueException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(LinkMemberRoleHome.KEY_MEMBER_ID, inMemberID);
			return select(lKey);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
}
