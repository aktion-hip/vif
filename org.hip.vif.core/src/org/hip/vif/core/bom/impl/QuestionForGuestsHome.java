/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

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

import java.sql.SQLException;

import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.HavingObjectImpl;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHome;

/**
 * Home for model that displays questions readable for guests.
 * 
 * @author Benno Luthiger
 * Created on Apr 11, 2005
 */
public class QuestionForGuestsHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionForGuests";
	public final static String KEY_DEPTH = "GUEST_DEPTH";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='QuestionForGuests' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_REMARK + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_MUTATION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_ROOT_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' as='" + KEY_DEPTH + "' template='LENGTH({0})-LENGTH(REPLACE({0}, \".\", \"\"))' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"	</columnDefs>	" +
		"	<joinDef joinType='NO_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"	</joinDef>	" +
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
	 * Returns the group's published questions up to the specified depth.
	 * 
	 * @param inGroupID Long
	 * @param inGuestDepth Long
	 * @param inOrder OrderObject
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectOfGroup(Long inGroupID, Long inGuestDepth, OrderObject inOrder) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(BOMHelper.getKeyStates(QuestionHome.KEY_STATE, WorkflowAwareContribution.STATES_PUBLISHED));
		HavingObject lHaving = new HavingObjectImpl();
		lHaving.setValue(KEY_DEPTH, inGuestDepth, "<");
		return select(lKey, inOrder, lHaving);
	}

}
