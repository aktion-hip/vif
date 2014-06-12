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

package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * Home of join from the GroupAdmin BOM to the Member BOM.
 * This join can be used to retrieve the member data of all group administrators
 * for a specified discussion group.
 *  
 * Created on 24.07.2002
 * @author Benno Luthiger
 */
public class JoinGroupAdminToMemberHome extends JoinedDomainObjectHomeImpl {
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinGroupAdminToMember";

	private final static String XML_OBJECT_DEF =
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinGroupAdminToMember' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + GroupAdminHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_USER_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_SEX + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + GroupAdminHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return JOIN_OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/**
	 * Returns the group administrators (i.e. members) for the specified group.
	 * 
	 * @param inGroupID java.math.BigDecimal
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public QueryResult select(Long inGroupID) throws BOMChangeValueException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
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
