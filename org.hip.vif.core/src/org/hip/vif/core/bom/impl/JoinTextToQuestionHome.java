/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;

/**
 * Home to retrieve all question entries linked to a given text/bibliography entry.
 *
 * @author Luthiger
 * Created: 07.09.2010
 */
public class JoinTextToQuestionHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinTextToQuestion";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinTextToQuestion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + TextQuestionHome.KEY_TEXTID + "' domainObject='org.hip.vif.core.bom.impl.TextQuestion'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextQuestion'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		<joinCondition>	" +
		"			<columnDef columnName='" + TextQuestionHome.KEY_QUESTIONID + "' domainObject='org.hip.vif.core.bom.impl.TextQuestion'/>	" +
		"		    <columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	" +
		"		</joinCondition>	" +
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
	 * Returns the published questions linked to the specified text entry.
	 * 
	 * @param inTextID Long
	 * @return {@link QueryResult}
	 * @throws SQLException 
	 * @throws VException 
	 */
	public QueryResult selectPublished(Long inTextID) throws VException, SQLException {
		return select(inTextID, WorkflowAwareContribution.STATES_PUBLISHED);
	}

	/**
	 * Returns the questions being in the specified states linked with the specified text entry.
	 * 
	 * @param inTextID Long
	 * @param inState Integer[]
	 * @return {@link QueryResult}
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult select(Long inTextID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextQuestionHome.KEY_TEXTID, inTextID);
		lKey.setValue(BOMHelper.getKeyStates(TextHome.KEY_STATE, inState));
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, 0);
		return select(lKey, lOrder);
	}

}
