/*
 This package is part of the administration of the application VIF.
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

import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;

/** Home to retrieve all completions of a group's questions and its authors/reviewers.
 *
 * @author Luthiger Created: 20.09.2009 */
@SuppressWarnings("serial")
public class JoinQuestionToCompletionAndContributorsHome extends JoinQuestionToContributorsHome {
    private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToCompletionAndContributors";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='JoinQuestionToCompletionAndContributors' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_COMPLETION
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_QUESTION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_MUTATION
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + CompletionHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + ResponsibleHome.KEY_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + MemberHome.KEY_NAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + MemberHome.KEY_FIRSTNAME
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
                    +
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + CompletionHome.KEY_QUESTION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "		</joinCondition>	\n"
                    +
                    "		<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "			<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "			<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "			<joinCondition>	\n"
                    +
                    "				<columnDef columnName='"
                    + CompletionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n"
                    +
                    "				<columnDef columnName='"
                    + CompletionAuthorReviewerHome.KEY_COMPLETION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "			</joinCondition>	\n"
                    +
                    "			<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "				<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "				<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
                    +
                    "				<joinCondition>	\n"
                    +
                    "					<columnDef columnName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n"
                    +
                    "					<columnDef columnName='"
                    + MemberHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
                    "				</joinCondition>	\n" +
                    "			</joinDef>	\n" +
                    "		</joinDef>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

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
