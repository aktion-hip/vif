/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2010, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.vif.core.bom.TextHistoryHome;
import org.hip.vif.core.bom.TextHome;

/** Home of <code>Text</code> history.
 *
 * @author Luthiger Created: 08.07.2010 */
@SuppressWarnings("serial")
public class TextHistoryHomeImpl extends DomainObjectHomeImpl implements TextHistoryHome {
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.TextHistoryImpl";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	" +
                    "<objectDef objectName='TextHistoryImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
                    "	<keyDefs>	" +
                    "		<keyDef>	" +
                    "			<keyItemDef seq='0' keyPropertyName='" + TextHome.KEY_ID + "'/>	" +
                    "			<keyItemDef seq='1' keyPropertyName='" + TextHome.KEY_VERSION + "'/>	" +
                    "			<keyItemDef seq='2' keyPropertyName='" + TextHome.KEY_FROM + "'/>	" +
                    "			<keyItemDef seq='3' keyPropertyName='" + TextHome.KEY_TO + "'/>	" +
                    "		</keyDef>	" +
                    "	</keyDefs>	" +
                    "	<propertyDefs>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_ID + "' valueType='Number' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='TextID'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_VERSION
                    + "' valueType='Number' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='nVersion'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_TITLE
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sTitle'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_AUTHOR
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sAuthor'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_COAUTHORS
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sCoAuthors'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_SUBTITLE
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sSubtitle'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_YEAR
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sYear'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_PUBLICATION
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sPublication'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_PAGES
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sPages'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_VOLUME
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='nVolume'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_NUMBER
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='nNumber'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_PUBLISHER
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sPublisher'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_PLACE
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sPlace'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_REFERENCE
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sReference'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_REMARK
                    + "' valueType='String' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='sRemark'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_TYPE
                    + "' valueType='Number' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='nType'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_STATE
                    + "' valueType='Number' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='nState'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_FROM
                    + "' valueType='Timestamp' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='dtFrom'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + TextHome.KEY_TO
                    + "' valueType='Timestamp' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='dtTo'/>	" +
                    "		</propertyDef>	" +
                    "		<propertyDef propertyName='" + KEY_MEMBER_ID + "' valueType='Number' propertyType='simple'>	" +
                    "			<mappingDef tableName='tblTextVersionHistory' columnName='MemberID'/>	" +
                    "		</propertyDef>	" +
                    "	</propertyDefs>	" +
                    "</objectDef>";

    /** TextHistoryHomeImpl constructor. */
    public TextHistoryHomeImpl() {
        super();
    }

    /** Returns the name of the objects which this home can create.
     *
     * @return java.lang.String */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** Returns the object definition string of the class managed by this home.
     *
     * @return java.lang.String */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

}
