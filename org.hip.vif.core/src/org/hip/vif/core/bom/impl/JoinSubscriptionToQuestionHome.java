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
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.SubscriptionHome;

/**
 * Home managing models to join subscriptions with questions.
 * This home can be used to find all questions a member has subscribed to.
 * 
 * @author Benno Luthiger
 * Created on Feb 29, 2004
 */
public class JoinSubscriptionToQuestionHome extends JoinedDomainObjectHomeImpl {
	public final static String KEY_ALIAS_QUESTION_ID = "QuestionID";

	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinSubscriptionToQuestion";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinSubscriptionToQuestion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + SubscriptionHome.KEY_MEMBERID + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"		<columnDef columnName='" + SubscriptionHome.KEY_LOCAL + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_ID + "' alias='" + KEY_ALIAS_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_REMARK + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<joinCondition>	" +
		"			<columnDef columnName='" + SubscriptionHome.KEY_QUESTIONID + "' domainObject='org.hip.vif.core.bom.impl.SubscriptionImpl'/>	\n" +
		"			<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		</joinCondition>	" +
		"	</joinDef>	" +
		"</joinedObjectDef>";

	/**
	 * JoinSubscriptionToQuestionHome constructor.
	 */
	public JoinSubscriptionToQuestionHome() {
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
