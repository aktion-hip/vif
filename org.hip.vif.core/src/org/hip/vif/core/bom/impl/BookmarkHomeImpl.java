package org.hip.vif.core.bom.impl;

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

import java.sql.SQLException;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BookmarkHome;

/**
 * Home of the Bookmarks model.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.BookmarkHome
 * Created on Feb 25, 2004
 */
public class BookmarkHomeImpl extends DomainObjectHomeImpl implements BookmarkHome {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.BookmarkImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='BookmarkImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_MEMBERID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_QUESTIONID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_MEMBERID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBookmark' columnName='MemberID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_QUESTIONID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBookmark' columnName='QuestionID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_BOOKMARKTEXT + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBookmark' columnName='sBookmarktext'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * BookmarkHomeImpl constructor.
	 */
	public BookmarkHomeImpl() {
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
	 * Creates a new bookmark with the specified values.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @param inBookmarkText String
	 * @throws VException
	 * @throws SQLException
	 */
	public void ucNew(String inQuestionID, Long inMemberID, String inBookmarkText) throws VException, SQLException {
		DomainObject lSubscription = create();
		lSubscription.set(BookmarkHome.KEY_QUESTIONID, new Integer(inQuestionID));
		lSubscription.set(BookmarkHome.KEY_MEMBERID, inMemberID);
		lSubscription.set(BookmarkHome.KEY_BOOKMARKTEXT, inBookmarkText);
		lSubscription.insert(true);
	}

	/**
	 * Checks whether a bookmark with the specified values exists.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @return boolean
	 * @throws VException
	 */
	public boolean hasBookmark(String inQuestionID, Long inMemberID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(BookmarkHome.KEY_QUESTIONID, new Integer(inQuestionID));
		lKey.setValue(BookmarkHome.KEY_MEMBERID, inMemberID);
		try {
			findByKey(lKey);
			return true;
		}
		catch (BOMNotFoundException exc) {
			return false;
		}
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
		lKey.setValue(BookmarkHome.KEY_QUESTIONID, inQuestionID);
		lKey.setValue(BookmarkHome.KEY_MEMBERID, inMemberID);
		delete(lKey, true);
	}
}
