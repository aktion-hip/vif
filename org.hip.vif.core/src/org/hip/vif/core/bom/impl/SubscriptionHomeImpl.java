/*
	This package is part of the persistency layer of the application VIF.
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

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.SubscriptionHome;

/**
 * Implementation of the home managing the Subscription models.
 * 
 * @author Benno Luthiger
 * Created on Feb 14, 2004
 */
@SuppressWarnings("serial")
public class SubscriptionHomeImpl extends DomainObjectHomeImpl implements SubscriptionHome {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.SubscriptionImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='SubscriptionImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_MEMBERID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_QUESTIONID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_MEMBERID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblSubscription' columnName='MemberID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_QUESTIONID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblSubscription' columnName='QuestionID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_LOCAL + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblSubscription' columnName='bLocal'/>	" +
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
	 * Creates a new subscription with the specified values.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @param inLocal boolean
	 * @throws VException
	 * @throws SQLException
	 */
	public void ucNew(String inQuestionID, Long inMemberID, boolean inLocal) throws VException, SQLException {
		DomainObject lSubscription = create();
		lSubscription.set(SubscriptionHome.KEY_QUESTIONID, new Integer(inQuestionID));
		lSubscription.set(SubscriptionHome.KEY_MEMBERID, inMemberID);
		lSubscription.set(SubscriptionHome.KEY_LOCAL, inLocal ? SubscriptionHome.IS_LOCAL : SubscriptionHome.IS_SUBTREE);
		lSubscription.insert(true);
	}
	
	/**
	 * Checks whether a subscription with the specified values exists.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @return boolean
	 * @throws VException
	 */
	public boolean hasSubscription(Long inQuestionID, Long inMemberID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(SubscriptionHome.KEY_QUESTIONID, inQuestionID);
		lKey.setValue(SubscriptionHome.KEY_MEMBERID, inMemberID);
		try {
			findByKey(lKey);
			return true;
		}
		catch (BOMNotFoundException exc) {
			return false;
		}
	}
	public boolean hasSubscription(String inQuestionID, Long inMemberID) throws VException {
		return hasSubscription(new Long(inQuestionID), inMemberID);
	}
	
	/**
	 * Deletes the entry with the specified key.
	 * 
	 * @param inQuestionID Long
	 * @param inMemberID Long
	 * @throws VException
	 * @throws SQLException
	 */
	public void delete(Long inQuestionID, Long inMemberID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(SubscriptionHome.KEY_QUESTIONID, inQuestionID);
		lKey.setValue(SubscriptionHome.KEY_MEMBERID, inMemberID);
		delete(lKey, true);
	}
	
	/**
	 * Updates the range of the specified subscription.
	 * 
	 * @param inQuestionID Long
	 * @param inMemberID Long
	 * @param isLocal boolean If true, the range is set to local.
	 * @throws VException
	 * @throws SQLException
	 */
	public void updateRange(Long inQuestionID, Long inMemberID, boolean isLocal) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(SubscriptionHome.KEY_QUESTIONID, inQuestionID);
		lKey.setValue(SubscriptionHome.KEY_MEMBERID, inMemberID);
		DomainObject lSubscription = findByKey(lKey);
		lSubscription.set(KEY_LOCAL, isLocal ? IS_LOCAL: IS_SUBTREE);
		lSubscription.update(true);
	}
}
