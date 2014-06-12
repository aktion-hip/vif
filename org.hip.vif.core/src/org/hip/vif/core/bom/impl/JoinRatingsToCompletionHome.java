/*
	This package is part of the application VIF.
	Copyright (C) 2009, Benno Luthiger

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
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;

/**
 * Home to retrieve all completions that have to be rated during the same event.
 *
 * @author Luthiger
 * Created: 30.08.2009
 */
public class JoinRatingsToCompletionHome extends JoinedDomainObjectHomeImpl {

	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinRatingsToCompletion";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinRatingsToCompletion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + RatingsCompletionHome.KEY_RATINGEVENTS_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingsCompletion'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_COMPLETION + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.RatingsCompletion'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	" +
		"		<joinCondition>	" +
		"			<columnDef columnName='" + RatingsCompletionHome.KEY_COMPLETION_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingsCompletion'/>	\n" +
		"			<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		</joinCondition>	" +
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"				<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"	    </joinDef>	\n" +
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
	 * Returns all completions that have to be rated during the specified rating events ID.
	 * 
	 * @param inRatingEventsID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getCompletionsToBeRated(Long inRatingEventsID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATINGEVENTS_ID, inRatingEventsID);
		return select(lKey);		
	}
	
}
