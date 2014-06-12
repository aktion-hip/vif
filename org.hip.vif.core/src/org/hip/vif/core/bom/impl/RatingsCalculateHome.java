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
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;

/**
 * Special table join to calculate the ratings of a rated member. 
 *
 * @author Luthiger
 * Created: 17.09.2009
 */
public class RatingsCalculateHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.RatingsCalculate";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='RatingsCalculate' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_CORRECTNESS + "' modifier='SUM' as='Sum1' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' modifier='SUM' as='Sum2' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_ETIQUETTE + "' modifier='SUM' as='Sum3' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_CORRECTNESS + "' modifier='AVG' template='{0} * 100' as='Mean1' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' modifier='AVG' template='{0} * 100' as='Mean2' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_ETIQUETTE + "' modifier='AVG' template='{0} * 100' as='Mean3' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_CORRECTNESS + "' modifier='COUNT' as='Count1' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' modifier='COUNT' as='Count2' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_ETIQUETTE + "' modifier='COUNT' as='Count3' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<hidden columnName='" + RatingsHome.KEY_RATED_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<hidden columnName='" + RatingsHome.KEY_RATINGEVENTS_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
		"		<hidden columnName='" + RatingEventsHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingEvents'/>	\n" +
		"		<hidden columnName='" + RatingEventsHome.KEY_COMPLETED + "' domainObject='org.hip.vif.core.bom.impl.RatingEvents'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='EQUI_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.RatingEvents'/>	" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.Ratings'/>	" + 
		"		<joinCondition>	" +
		"			<columnDef columnName='" + RatingEventsHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.RatingEvents'/>	\n" +
		"			<columnDef columnName='" + RatingsHome.KEY_RATINGEVENTS_ID + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	\n" +
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
	 * Returns the specified member's ratings as rated person.
	 * 
	 * @param inMemberID Long the rated member's ID
	 * @return QueryResult the ratings of the rated person.
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getRatingsOf(Long inMemberID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATED_ID, inMemberID);
		lKey.setValue(RatingEventsHome.KEY_COMPLETED, new Integer(0), "!=", BinaryBooleanOperator.AND);
		return select(lKey);
	}

}
