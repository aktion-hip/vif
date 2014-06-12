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

import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.GroupByObjectImpl;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;

/**
 * Home to retrieve the grouped ratings of the member's efficiency.
 *
 * @author Luthiger
 * Created: 17.09.2009
 */
public class RatingsCountEfficiencyHome extends JoinedDomainObjectHomeImpl {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.RatingsCountEfficiency";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='RatingsCountEfficiency' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
		"		<columnDef columnName='" + RatingsHome.KEY_EFFICIENCY + "' modifier='COUNT' as='Count' valueType='Number' domainObject='org.hip.vif.core.bom.impl.Ratings'/>	" +
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
	 * Returns the specified person's ratings.
	 * 
	 * @param inMemberID Long the rated person's ID
	 * @return QueryResult the number of positive, neutral and negative ratings. 
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getRatingsOf(Long inMemberID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATED_ID, inMemberID);
		lKey.setValue(RatingEventsHome.KEY_COMPLETED, new Integer(0), "!=", BinaryBooleanOperator.AND);
		GroupByObject lGroup = new GroupByObjectImpl();
		lGroup.setValue(getFieldName(), 1);
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(getFieldName(), 1);
		return select(lKey, lOrder, null, lGroup);
	}
	
	protected String getFieldName() {
		return RatingsHome.KEY_EFFICIENCY;
	}

}
