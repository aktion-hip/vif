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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;

/**
 * Home for <code>TextQuestion</code> models.
 *
 * @author Luthiger
 * Created: 28.07.2010
 */
@SuppressWarnings("serial")
public class TextQuestionHome extends DomainObjectHomeImpl {
	public final static String KEY_TEXTID = "TextID";
	public final static String KEY_QUESTIONID = "QuestionID";
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.TextQuestion";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TextQuestion' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_TEXTID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_QUESTIONID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_TEXTID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextQuestion' columnName='TextID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_QUESTIONID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextQuestion' columnName='QuestionID'/>	" +
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
	 * Creates a new link entry.
	 * 
	 * @param inTextID Long
	 * @param inQuestionID String
	 * @throws VException
	 * @throws SQLException
	 */
	public void createEntry(Long inTextID, String inQuestionID) throws VException, SQLException {
		DomainObject lModel = create();
		lModel.set(TextQuestionHome.KEY_TEXTID, inTextID);
		lModel.set(TextQuestionHome.KEY_QUESTIONID, new Long(inQuestionID));
		lModel.insert(true);
	}
	
	/**
	 * Deletes all entries that link the text entry with the specified ID to questions.
	 * 
	 * @param inTextID String
	 * @throws VException
	 * @throws SQLException
	 */
	public void deleteByText(Long inTextID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextQuestionHome.KEY_TEXTID, inTextID);
		delete(lKey, true);
	}

}
