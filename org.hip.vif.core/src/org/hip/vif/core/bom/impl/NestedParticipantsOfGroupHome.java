/*
 This package is part of the application VIF
 Copyright (C) 2004, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ParticipantHome;

/**
 * Home to list a specified group's participants.
 * 
 * @author Benno Luthiger
 * Created on Nov 7, 2004
 */
@SuppressWarnings("serial")
public class NestedParticipantsOfGroupHome extends JoinedDomainObjectHomeImpl {
	public final static String NESTED_ALIAS = "Admins";
	public final static String KEY_GROUPADMIN = "GroupAdminID";
	public final static String KEY_SUSPENDED_TEST1 = "TestSuspended1";
	public final static String KEY_SUSPENDED_TEST2 = "TestSuspended2";

	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.NestedParticipantsOfGroup";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='NestedParticipantsOfGroup' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + ParticipantHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"		<columnDef columnName='" + ParticipantHome.KEY_SUSPEND_FROM + "' template='NOW() &gt;= {0}' as='" + KEY_SUSPENDED_TEST1 + "' valueType='Number' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"		<columnDef columnName='" + ParticipantHome.KEY_SUSPEND_TO + "' template='NOW() &lt;= {0}' as='" + KEY_SUSPENDED_TEST2 + "' valueType='Number' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"		<columnDef columnName='" + GroupAdminHome.KEY_MEMBER_ID + "' as='" + KEY_GROUPADMIN + "' nestedObject='" + NESTED_ALIAS + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_USER_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_CITY + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ZIP + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_SEX + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + ParticipantHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinDef joinType='LEFT_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"			<objectPlaceholder name='" + NESTED_ALIAS + "' />	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + ParticipantHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n" +
		"				<columnDef columnName='" + GroupAdminHome.KEY_MEMBER_ID + "' nestedObject='" + NESTED_ALIAS + "' domainObject='org.hip.vif.core.bom.impl.GroupAdminImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"		</joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * NestedParticipantsOfGroupHome constructor.
	 */
	public NestedParticipantsOfGroupHome() {
		super();
	}

	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

}
