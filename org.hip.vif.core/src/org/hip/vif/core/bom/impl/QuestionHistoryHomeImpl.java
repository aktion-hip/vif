/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2002, Benno Luthiger

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
import org.hip.vif.core.bom.QuestionHistoryHome;
import org.hip.vif.core.bom.QuestionHome;

/** This domain object home implements the QuestionHistoryHome interface.
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.QuestionHistoryHome */
@SuppressWarnings("serial")
public class QuestionHistoryHomeImpl extends DomainObjectHomeImpl implements QuestionHistoryHome {

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionHistoryImpl";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.
    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<objectDef objectName='QuestionHistory' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
                    +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='"
                    + QuestionHome.KEY_ID
                    + "'/>	\n"
                    +
                    "			<keyItemDef seq='1' keyPropertyName='"
                    + QuestionHome.KEY_MUTATION
                    + "'/>	\n"
                    +
                    "			<keyItemDef seq='2' keyPropertyName='"
                    + KEY_VALID_TO
                    + "'/>	\n"
                    +
                    "		</keyDef>	\n"
                    +
                    "	</keyDefs>	\n"
                    +
                    "	<propertyDefs>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_ID
                    + "' valueType='Number' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='QuestionID'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_MUTATION
                    + "' valueType='Timestamp' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='dtFrom'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + KEY_VALID_TO
                    + "' valueType='Timestamp' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='dtTo'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_QUESTION_DECIMAL
                    + "' valueType='String' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='sQuestionID'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_QUESTION
                    + "' valueType='String' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='sQuestion'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_REMARK
                    + "' valueType='String' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='sRemark'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_STATE
                    + "' valueType='Number' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='nState'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_GROUP_ID
                    + "' valueType='Number' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='GroupID'/>	\n"
                    +
                    "		</propertyDef>	\n"
                    +
                    "		<propertyDef propertyName='"
                    + QuestionHome.KEY_ROOT_QUESTION
                    + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='bRootQuestion'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_MEMBER_ID + "' valueType='Number' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblQuestionHistory' columnName='MemberID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** Constructor for QuestionHistoryHomeImpl. */
    public QuestionHistoryHomeImpl() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return OBJECT_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }
}
