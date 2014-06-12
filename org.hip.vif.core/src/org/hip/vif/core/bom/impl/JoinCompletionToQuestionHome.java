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
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;

/**
 * This join home can be used to retrieve all completions belonging to the
 * same question.
 * 
 * @author Benno Luthiger
 * Created on Mar 13, 2004
 */
public class JoinCompletionToQuestionHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinCompletionToQuestion";
	private final static String XML_OBJECT_DEF = 
	"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
	"<joinedObjectDef objectName='JoinCompletionToQuestion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
	"	<columnDefs>	\n" +
	"		<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		<columnDef columnName='" + CompletionHome.KEY_COMPLETION + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		<columnDef columnName='" + CompletionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
	"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
	"	</columnDefs>	\n" +
	"	<joinDef joinType='EQUI_JOIN'>	\n" +
	"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
	"		<joinCondition>	\n" +
	"			<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
	"			<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
	"		</joinCondition>	\n" +
	"	</joinDef>	\n" +
	"</joinedObjectDef>";

	/**
	 * JoinCompletionToQuestionHome constructor.
	 */
	public JoinCompletionToQuestionHome() {
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
	
	/**
	 * Returns all completions belonging to the specified question.
	 * 
	 * @param inQuestionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getCompletions(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		return select(lKey);
	}
	
	/**
	 * Returns the specified completion.
	 * 
	 * @param inCompletionID Long
	 * @return JoinCompletionToQuestion
	 * @throws VException
	 * @throws SQLException
	 */
	public JoinCompletionToQuestion getCompletion(Long inCompletionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
		return (JoinCompletionToQuestion)findByKey(lKey);
	}
	
	
	/**
	 * Returns the group's ID the specified completion belongs to.
	 * 
	 * @param inCompletionID Long
	 * @return Long group ID
	 * @throws VException 
	 */
	public Long getGroupID(Long inCompletionID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, inCompletionID);
		ReadOnlyDomainObject lEntry = findByKey(lKey);
		return new Long(lEntry.get(QuestionHome.KEY_GROUP_ID).toString());
	}

}
