/*
 This package is part of the application VIF.
 Copyright (C) 2009, Benno Luthiger

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
import org.hip.vif.core.bom.MemberHome;

/**
 * Retrieves the name of the rated person for specified ratings.
 * Can be used to retrieve all open ratings for a specified participant (i.e. rater) too.
 *
 * @author Luthiger
 * Created: 29.08.2009
 */
public class JoinRatingsToRaterHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinRatingsToRater";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='JoinRatingsToRater' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_RATINGEVENTS_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_RATER_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_RATED_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_CORRECTNESS + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_ETIQUETTE + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + RatingsHome.KEY_REMARK + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	" + 
		"		<joinCondition>	" +
		"			<columnDef columnName='" + RatingsHome.KEY_RATED_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"			<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
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
	 * Returns the rating model with the specified rating ID and for the specified rater ID.
	 * 
	 * @param inRatingID Long
	 * @param inMemberID Long
	 * @return JoinRatingsToRater
	 * @throws VException
	 * @throws SQLException
	 */
	public JoinRatingsToRater getRating(Long inRatingID, Long inMemberID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATINGEVENTS_ID, inRatingID);
		lKey.setValue(RatingsHome.KEY_RATER_ID, inMemberID);
		QueryResult lResult = select(lKey);
		return (JoinRatingsToRater) lResult.nextAsDomainObject();
	}

}
