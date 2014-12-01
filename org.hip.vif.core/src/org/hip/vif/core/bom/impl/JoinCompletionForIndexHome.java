/*
 This package is part of the persistency layer of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;

/** Join for completions intended for indexation of content.
 *
 * @author Benno Luthiger Created on 24.09.2005 */
@SuppressWarnings("serial")
public class JoinCompletionForIndexHome extends JoinedDomainObjectHomeImpl {
    public final static String JOIN_QUESTION_ID = "QuestionID";
    public final static String JOIN_GROUP_STATE = "GroupState";
    public final static String JOIN_GROUP_NAME = "GROUPNAME";
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinCompletionForIndex";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	"
                    +
                    "<joinedObjectDef objectName='JoinQuestionForIndex' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	"
                    +
                    "	<columnDefs>	" +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_COMPLETION
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' alias='"
                    + JOIN_QUESTION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_QUESTION_DECIMAL
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_QUESTION
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_NAME
                    + "' as='"
                    + JOIN_GROUP_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_PRIVATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + GroupHome.KEY_STATE
                    + "' alias='"
                    + JOIN_GROUP_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + ResponsibleHome.KEY_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + MemberHome.KEY_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	"
                    +
                    "		<columnDef columnName='"
                    + MemberHome.KEY_FIRSTNAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	"
                    +
                    "	</columnDefs>	"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		<joinCondition>	"
                    +
                    "			<columnDef columnName='"
                    + CompletionHome.KEY_QUESTION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "			<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "		</joinCondition>	"
                    +
                    "		<joinDef joinType='EQUI_JOIN'>	"
                    +
                    "			<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "			<objectDesc objectClassName='org.hip.vif.core.bom.impl.GroupImpl'/>	"
                    +
                    "			<joinCondition>	"
                    +
                    "				<columnDef columnName='"
                    + QuestionHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	"
                    +
                    "				<columnDef columnName='"
                    + GroupHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.GroupImpl'/>	"
                    +
                    "			</joinCondition>	"
                    +
                    "			<joinDef joinType='EQUI_JOIN'>	"
                    +
                    "				<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "				<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	"
                    +
                    "				<joinCondition>	"
                    +
                    "					<columnDef columnName='"
                    + CompletionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	"
                    +
                    "					<columnDef columnName='"
                    + CompletionAuthorReviewerHome.KEY_COMPLETION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	"
                    +
                    "				</joinCondition>	"
                    +
                    "				<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "					<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "					<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
                    +
                    "					<joinCondition>	\n"
                    +
                    "						<columnDef columnName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "						<columnDef columnName='"
                    + MemberHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
                    "					</joinCondition>	\n" +
                    "				</joinDef>	" +
                    "			</joinDef>	" +
                    "		</joinDef>	" +
                    "	</joinDef>	" +
                    "</joinedObjectDef>";

    /** JoinCompletionForIndexHome constructor. */
    public JoinCompletionForIndexHome() {
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

    /** <p>
     * Convenience method: Creates a key to filter all contributions that are either in public groups and joined with
     * authors.
     * </p>
     * <p>
     * i.e. <code>author AND group not private AND question viewable AND group viewable</code>
     * </p>
     * 
     * @param inFieldNameState String Name of the field holding the state information.
     * @return KeyObject
     * @throws VException */
    public static KeyObject getKeyPublicAndAuthor(final String inFieldNameState) throws VException {
        final KeyObject outKey = new KeyObjectImpl();
        outKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        outKey.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PUBLIC, "=", BinaryBooleanOperator.AND);
        outKey.setValue(BOMHelper.getKeyPublished(inFieldNameState));
        outKey.setValue(BOMHelper.getKeyVisibleGroup(JOIN_GROUP_STATE));
        return outKey;
    }
}
