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
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadTextHome;

/**
 * Home of models to download files.
 *
 * @author Luthiger
 * Created: 19.09.2010
 */
@SuppressWarnings("serial")
public class DownloadTextHomeImpl extends DomainObjectHomeImpl implements DownloadTextHome {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.DownloadTextImpl";
	
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='DownloadTextImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='DownloadID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_LABEL + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='sLabel'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_UUID + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='sUUID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_MIME + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='sMime'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_DOCTYPE + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='sDocType'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TEXTID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='TextID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_MEMBERID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblDownloadText' columnName='MemberID'/>	" +
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

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.DownloadTextHome#getDownloads(Long)
	 */
	public QueryResult getDownloads(Long inTextID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(DownloadTextHome.KEY_TEXTID, inTextID);
		return select(lKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.DownloadTextHome#getDownload(java.lang.String)
	 */
	public DownloadText getDownload(String inDownloadID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(DownloadTextHome.KEY_ID, Long.parseLong(inDownloadID));
		return (DownloadText) findByKey(lKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.DownloadTextHome#deleteDownload(java.lang.String)
	 */
	public void deleteDownload(String inDownloadID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(DownloadTextHome.KEY_ID, Long.parseLong(inDownloadID));
		delete(lKey, true);
	}

}
