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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;

/**
 * Home for rating events models.
 *
 * @author Luthiger
 * Created: 29.08.2009
 */
public class RatingEventsHome extends DomainObjectHomeImpl {
	public final static String KEY_ID = "RatingEventsID";
	public final static String KEY_COMPLETED = "Completed";
	public final static String KEY_CREATION = "Creation";
	
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.RatingEvents";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='RatingEvents' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatingEvents' columnName='RatingEventsID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_COMPLETED + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatingEvents' columnName='bCompleted'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_CREATION + "' valueType='Timestamp' propertyType='simple'>	" +
		"			<mappingDef tableName='tblRatingEvents' columnName='dtCreation'/>	" +
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
	 * Returns the model with the specified ID.
	 * 
	 * @param inEventsID Long
	 * @return RatingEvents
	 * @throws VException
	 */
	public RatingEvents getRatingEvents(Long inEventsID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingEventsHome.KEY_ID, inEventsID);
		return (RatingEvents) findByKey(lKey);
	}

}
