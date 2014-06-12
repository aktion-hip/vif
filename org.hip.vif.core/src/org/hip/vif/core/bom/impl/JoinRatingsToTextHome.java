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
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.TextHome;

/**
 * Home to retrieve all bibliography entries that have to be rated during the same event.
 *
 * @author Luthiger
 * Created: 07.08.2010
 */
public class JoinRatingsToTextHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinRatingsToText";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinRatingsToText' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + RatingsTextHome.KEY_RATINGEVENTS_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingsText'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		<columnDef columnName='" + TextHome.KEY_REFERENCE + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.RatingsText'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.TextImpl'/>	" +
		"		<joinCondition>	" +
		"			<columnDef columnName='" + RatingsTextHome.KEY_TEXT_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingsText'/>	\n" +
		"			<columnDef columnName='" + TextHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
		"		</joinCondition>	" +
		"		<joinCondition operatorType='AND'>	" +
		"			<columnDef columnName='" + RatingsTextHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.RatingsText'/>	\n" +
		"			<columnDef columnName='" + TextHome.KEY_VERSION + "' domainObject='org.hip.vif.core.bom.impl.TextImpl'/>	\n" +
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
	 * Returns all bibliography entries that have to be rated during the specified rating events ID.
	 * 
	 * @param inRatingEventsID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getTextsToBeRated(Long inRatingEventsID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATINGEVENTS_ID, inRatingEventsID);
		return select(lKey);
	}

}
