package org.hip.vif.core.bom.impl;

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.SubscriptionHome;

/**
 * Home managing models to join subscriptions with members.
 * This home can be used to find all subscribers of a question.
 * 
 * @author Benno Luthiger
 * Created on Feb 15, 2004
 */
public class JoinSubscriptionToMemberHome extends JoinedDomainObjectHomeImpl {
	public final static String KEY_ALIAS_MEMBER_ID = "MemberID";
	
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinSubscriptionToMember";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinSubscriptionToMember' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + SubscriptionHome.KEY_QUESTIONID + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"		<columnDef columnName='" + SubscriptionHome.KEY_LOCAL + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' alias='" + KEY_ALIAS_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_SEX + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + SubscriptionHome.KEY_MEMBERID + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
	
	/**
	 * JoinSubscriptionToMemberHome constructor.
	 */
	public JoinSubscriptionToMemberHome() {
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
