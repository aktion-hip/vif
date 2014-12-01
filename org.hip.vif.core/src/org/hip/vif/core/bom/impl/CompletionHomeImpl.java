/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;

/** This domain object home implements the CompletionHome interface.
 *
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.CompletionHome */
@SuppressWarnings("serial")
public class CompletionHomeImpl extends DomainObjectHomeImpl implements CompletionHome {

    /*
     * Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>;
     */
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.CompletionImpl";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
                    "<objectDef objectName='Completion' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
                    "	<keyDefs>	\n" +
                    "		<keyDef>	\n" +
                    "			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	\n" +
                    "		</keyDef>	\n" +
                    "	</keyDefs>	\n" +
                    "	<propertyDefs>	\n" +
                    "		<propertyDef propertyName='" + KEY_ID + "' valueType='Long' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblCompletion' columnName='CompletionID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_COMPLETION + "' valueType='String' propertyType='simple'>	\n"
                    +
                    "			<mappingDef tableName='tblCompletion' columnName='sCompletion'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_STATE + "' valueType='Long' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblCompletion' columnName='nState'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_QUESTION_ID + "' valueType='Long' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblCompletion' columnName='QuestionID'/>	\n" +
                    "		</propertyDef>	\n" +
                    "		<propertyDef propertyName='" + KEY_MUTATION
                    + "' valueType='Timestamp' propertyType='simple'>	\n" +
                    "			<mappingDef tableName='tblCompletion' columnName='dtMutation'/>	\n" +
                    "		</propertyDef>	\n" +
                    "	</propertyDefs>	\n" +
                    "</objectDef>";

    /** Constructor for CompletionHomeImpl. */
    public CompletionHomeImpl() {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.CompletionHome#getCompletions(java.lang.String)
     */
    @Override
    public QueryResult getCompletions(final String inQuestionID) throws VException, SQLException {
        return getCompletions(new Long(inQuestionID));
    }

    @Override
    public QueryResult getCompletions(final Long inQuestionID) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_QUESTION_ID, inQuestionID);
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(KEY_ID, 0);
        return select(lKey, lOrder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.CompletionHome#getPublished(java.lang.String)
     */
    @Override
    public QueryResult getPublished(final String inQuestionID) throws VException, SQLException {
        return getPublished(new Long(inQuestionID));
    }

    @Override
    public QueryResult getPublished(final Long inQuestionID) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_QUESTION_ID, inQuestionID);
        lKey.setValue(BOMHelper.getKeyPublished(KEY_STATE));
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(KEY_ID, 0);
        return select(lKey, lOrder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.CompletionHome#getCompletion(java.lang.String)
     */
    @Override
    public Completion getCompletion(final String inCompletionID) throws VException, SQLException {
        return getCompletion(new Long(inCompletionID));
    }

    @Override
    public Completion getCompletion(final Long inCompletionID) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_ID, inCompletionID);
        return (Completion) findByKey(lKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.vif.core.bom.CompletionHome#getSiblingCompletions(java.lang.String, java.lang.String)
     */
    @Override
    public QueryResult getSiblingCompletions(final String inQuestionID, final String inCompletionID) throws VException,
            SQLException {
        return getSiblingCompletions(new Long(inQuestionID), new Long(inCompletionID));
    }

    @Override
    public QueryResult getSiblingCompletions(final Long inQuestionID, final Long inCompletionID) throws VException,
            SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(KEY_QUESTION_ID, inQuestionID);
        lKey.setValue(KEY_ID, inCompletionID, "!=");
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(KEY_MUTATION, 0);
        return select(lKey, lOrder);
    }
}
