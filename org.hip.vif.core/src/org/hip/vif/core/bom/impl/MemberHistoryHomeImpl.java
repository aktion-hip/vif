/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
import org.hip.vif.core.bom.MemberHistoryHome;
import org.hip.vif.core.bom.MemberHome;

/** This domain object home implements the MemberHistoryHome interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.MemberHistoryHome */
@SuppressWarnings("serial")
public class MemberHistoryHomeImpl extends DomainObjectHomeImpl implements MemberHistoryHome {
    /** Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>; */
    private final static String MEMBER_HYSTORY_CLASS_NAME = "org.hip.vif.core.bom.impl.MemberHistoryImpl";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.
    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
                    "<objectDef objectName='MemberHistory' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='" + MemberHome.KEY_ID + "'/>	\n" +
                    "			<keyItemDef seq='1' keyPropertyName='" + MemberHome.KEY_MUTATION + "'/>	\n" +
                    "			<keyItemDef seq='2' keyPropertyName='" + KEY_VALID_TO + "'/>	\n" +
                    "		</keyDef>	\n" +
                    "	</keyDefs>	\n" +
                    "	<propertyDefs>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_ID
                    + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='MEMBERID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_MUTATION
                    + "' valueType='Timestamp' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='DTFROM'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_VALID_TO
                    + "' valueType='Timestamp' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='DTTO'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_USER_ID
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SUSERID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_NAME
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SNAME'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_FIRSTNAME
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SFIRSTNAME'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_STREET
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SSTREET'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_ZIP
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SZIP'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_CITY
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SCITY'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_PHONE
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='STEL'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_FAX
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SFAX'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_MAIL
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SMAIL'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_SEX
                    + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='BSEX'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_LANGUAGE
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SLANGUAGE'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + MemberHome.KEY_SETTINGS
                    + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SSETTINGS'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_REMARKS + "' valueType='String' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='SREMARKS'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_EDITOR_ID + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblMemberHistory' columnName='EditorID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** MemberHistoryHomeImpl default constructor */
    public MemberHistoryHomeImpl() {
        super();
    }

    /** Returns the MemberHistory class name
     *
     * @return java.lang.String */
    @Override
    public String getObjectClassName() {
        return MEMBER_HYSTORY_CLASS_NAME;
    }

    /** Every class must provide some meta-data required by the lightweight DomainObject framework. This method returns
     * the definition string (in form of xml).
     *
     * @return java.lang.String */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }
}
