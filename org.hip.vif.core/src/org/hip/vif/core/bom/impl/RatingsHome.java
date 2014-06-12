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
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.SQLNull;
import org.hip.kernel.exc.VException;

/**
 * Home for ratings models.
 *
 * @author Luthiger
 * Created: 29.08.2009
 */
@SuppressWarnings("serial")
public class RatingsHome extends DomainObjectHomeImpl {
	public final static String KEY_RATINGEVENTS_ID = "RatingEventsID";
	public final static String KEY_RATER_ID = "RaterID";
	public final static String KEY_RATED_ID = "RatedID";
	public final static String KEY_ISAUTHOR = "IsAuthor";
	public final static String KEY_CORRECTNESS = "Correctness";
	public final static String KEY_EFFICIENCY = "Efficiency";
	public final static String KEY_ETIQUETTE = "Etiquette";
	public final static String KEY_REMARK = "Remark";

	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.Ratings";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='Ratings' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_RATINGEVENTS_ID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_RATER_ID + "'/>	" +
		"			<keyItemDef seq='2' keyPropertyName='" + KEY_RATED_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_RATINGEVENTS_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='RatingEventsID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_RATER_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='RaterID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_RATED_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='RatedID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_ISAUTHOR + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='nIsAuthor'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_CORRECTNESS + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='nCorrectness'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_EFFICIENCY + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='nEfficiency'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_ETIQUETTE + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='nEtiquette'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_REMARK + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatings' columnName='sRemark'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

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
	 * Saves the specified values to the rating with the specified ID.
	 * 
	 * @param inRatingEventsID Long the rating's ID
	 * @param inRaterID Long the rater's entry's ID
	 * @param inCorrectness int
	 * @param inEfficiency int 
	 * @param inEtiquette int
	 * @param inRemark String
	 * @return Long the rated person's ID
	 * @throws VException
	 * @throws SQLException
	 */
	public Long saveRating(Long inRatingEventsID, Long inRaterID, int inCorrectness, int inEfficiency, int inEtiquette, String inRemark) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_RATINGEVENTS_ID, inRatingEventsID);
		lKey.setValue(KEY_RATER_ID, inRaterID);
		Ratings lRatings = (Ratings)findByKey(lKey);
		Long outRatedID = (Long) lRatings.get(KEY_RATED_ID);
		lRatings.set(KEY_CORRECTNESS, inCorrectness);
		lRatings.set(KEY_EFFICIENCY, inEfficiency);
		lRatings.set(KEY_ETIQUETTE, inEtiquette);
		lRatings.set(KEY_REMARK, inRemark);
		lRatings.update(true);
		return outRatedID;
	}

	/**
	 * Checks whether the rating with the specified ID is completed (by both parties),
	 * i.e. counts the number of open ratings with the specified rating ID for the specified rated member. 
	 * 
	 * @param inRatingEventsID Long the rating's ID
	 * @param inRatedID Long the member entry's ID of the rated member
	 * @return boolean <code>true</code> if the rating is completed.
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean checkRatingCompleted(Long inRatingEventsID, Long inRatedID) throws VException, SQLException {
		KeyObject lKey = prepareKey(inRatingEventsID);
		lKey.setValue(KEY_RATED_ID, inRatedID);
		return getCount(lKey) == 0;
	}
	
	/**
	 * Checks whether the rating with the specified ID is completed (by both parties).
	 * 
	 * @param inRatingEventsID Long the rating's ID
	 * @return boolean <code>true</code> if the rating is completed.
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean checkRatingCompleted(Long inRatingEventsID) throws VException, SQLException {
		KeyObject lKey = prepareKey(inRatingEventsID);
		return getCount(lKey) == 0;
	}
	
	private KeyObject prepareKey(Long inRatingEventsID) throws VException {
		KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(KEY_RATINGEVENTS_ID, inRatingEventsID);
		outKey.setValue(KEY_CORRECTNESS, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND);
		outKey.setValue(KEY_EFFICIENCY, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND);
		outKey.setValue(KEY_ETIQUETTE, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND);
		return outKey;
	}
	
	/**
	 * Checks whether the specified member is involved in the rating process of the specified rating event.
	 *  
	 * @param inRatingEventsID Long the rating's ID
	 * @param inMemberID Long the member entry's ID 
	 * @return boolean <code>true</code> if the specified member is either rater/rated.
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean isInvolved(Long inRatingEventsID, Long inMemberID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_RATINGEVENTS_ID, inRatingEventsID);
		lKey.setValue(KEY_RATER_ID, inMemberID);
		return getCount(lKey) > 0;
	}

}
