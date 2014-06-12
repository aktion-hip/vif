/*
	This package is part of the administration of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
import java.sql.Timestamp;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;

/**
 * Home to retrieve a text's authors/reviewers.
 *
 * @author Luthiger
 * Created: 09.10.2010
 */
@SuppressWarnings("serial")
public class JoinTextToMemberHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinTextToMember";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinTextToMember' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_TITLE + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_REFERENCE + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_CREATED + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + TextHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"			<columnDef columnName='" + TextAuthorReviewerHome.KEY_TEXT_ID + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinCondition operatorType='AND'>	" +
		"			<columnDef columnName='" + TextHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"			<columnDef columnName='" + TextAuthorReviewerHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"		</joinCondition>	" +		
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.TextAuthorReviewerImpl'/>	\n" +
		"				<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"		</joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

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
	
	/**
	 * Returns all reviewers with requests for review older then the specified date.
	 * 
	 * @param inStaleDate {@link Timestamp} requests older than this date are stale
	 * @return {@link QueryResult}
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectStaleWaitingForReview(Timestamp inStaleDate) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_STATE, new Integer(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lKey.setValue(ResponsibleHome.KEY_TYPE, new Integer(ResponsibleHome.Type.REVIEWER.getValue()), "=", BinaryBooleanOperator.AND);
		lKey.setValue(ResponsibleHome.KEY_CREATED, inStaleDate, "<", BinaryBooleanOperator.AND);
		return select(lKey);
	}

}
