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
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.bom.impl.OutlinedQueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;

/** Home of join from the question-author/reviewer BOM to the question BOM. This home can be used to retrieve the
 * question data an author or reviewer is responsible to.
 *
 * @author: Benno Luthiger */
@SuppressWarnings("serial")
public class JoinAuthorReviewerToQuestionHome extends JoinedDomainObjectHomeImpl {
    // Every home has to know the class it handles. They provide access to
    // this name through the method <I>getObjectClassName</I>;
    private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinAuthorReviewerToQuestion";

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
                    +
                    "<joinedObjectDef objectName='JoinAuthorReviewerToQuestion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
                    +
                    "	<columnDefs>	\n" +
                    "		<columnDef columnName='"
                    + ResponsibleHome.KEY_MEMBER_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + ResponsibleHome.KEY_TYPE
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_GROUP_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_QUESTION
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_QUESTION_DECIMAL
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_REMARK
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_ROOT_QUESTION
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<columnDef columnName='"
                    + QuestionHome.KEY_STATE
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "	</columnDefs>	\n"
                    +
                    "	<joinDef joinType='EQUI_JOIN'>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n"
                    +
                    "		<joinCondition>	\n"
                    +
                    "			<columnDef columnName='"
                    + QuestionHome.KEY_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n"
                    +
                    "			<columnDef columnName='"
                    + QuestionAuthorReviewerHome.KEY_QUESTION_ID
                    + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
                    "		</joinCondition>	\n" +
                    "	</joinDef>	\n" +
                    "</joinedObjectDef>";

    /** Constructor for JoinQuestionToAuthorReviewer. */
    public JoinAuthorReviewerToQuestionHome() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName() */
    @Override
    public String getObjectClassName() {
        return JOIN_OBJECT_CLASS_NAME;
    }

    /** @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString() */
    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }

    /** Returns all questions in the specified group the specified member has authored.
     * 
     * @param inAuthorID Long
     * @param inGroupID Long
     * @return org.hip.kernel.bom.QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult getAuthorsQuestions(final Long inAuthorID, final Long inGroupID) throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        return getQuestionsResponsibleFor(lKey, inAuthorID, inGroupID);
    }

    /** @deprecated */
    @Deprecated
    public QueryResult getAuthorsQuestions(final Long inAuthorID, final String inGroupID) throws VException,
            SQLException {
        return getAuthorsQuestions(inAuthorID, new Long(inGroupID));
    }

    /** Returns all questions in the specified group the specified member has reviewed.
     * 
     * @param inReviewerID Long
     * @param inGroupID Long
     * @return org.hip.kernel.bom.QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult getReviewersQuestions(final Long inReviewerID, final Long inGroupID) throws VException,
            SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
        return getQuestionsResponsibleFor(lKey, inReviewerID, inGroupID);
    }

    /** @deprecated */
    @Deprecated
    public QueryResult getReviewersQuestions(final Long inReviewerID, final String inGroupID) throws VException,
            SQLException {
        return getReviewersQuestions(inReviewerID, new Long(inGroupID));
    }

    private QueryResult getQuestionsResponsibleFor(final KeyObject inKey, final Long inMemberID, final Long inGroupID)
            throws VException, SQLException {
        inKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
        inKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, false, 0);
        return select(inKey, lOrder);
    }

    /** Returns all questions being in the specified states in the specified group the specified member has authored.
     * 
     * @param inAuthorID Long
     * @param inGroupID Long
     * @param inState java.lang.String[]
     * @return org.hip.kernel.bom.QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult getAuthorsQuestions(final Long inAuthorID, final Long inGroupID, final Integer[] inState)
            throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        return getQuestionsResponsibleFor(lKey, inAuthorID, inGroupID, inState);
    }

    /** @deprecated */
    @Deprecated
    public QueryResult getAuthorsQuestions(final Long inAuthorID, final String inGroupID, final Integer[] inState)
            throws VException, SQLException {
        return getAuthorsQuestions(inAuthorID, new Long(inGroupID), inState);
    }

    /** Returns all unpublished questions in the specified group the specified member has authored.
     * 
     * @param inAuthorID Long
     * @param inGroupID Long
     * @return QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult getAuthorsUnpublishedQuestions(final Long inAuthorID, final Long inGroupID) throws VException,
            SQLException {
        return getAuthorsQuestions(inAuthorID, inGroupID, WorkflowAwareContribution.STATES_UNPUBLISHED);
    }

    /** @deprecated */
    @Deprecated
    public QueryResult getAuthorsUnpublishedQuestions(final Long inAuthorID, final String inGroupID) throws VException,
            SQLException {
        return getAuthorsQuestions(inAuthorID, new Long(inGroupID), WorkflowAwareContribution.STATES_UNPUBLISHED);
    }

    /** Returns all questions being in the specified state in the specified group the specified member has reviewed.
     * 
     * @param inReviewerID Long
     * @param inGroupID Long
     * @param inState java.lang.String[]
     * @return org.hip.kernel.bom.QueryResult
     * @throws VException
     * @throws SQLException */
    public QueryResult getReviewersQuestions(final Long inReviewerID, final Long inGroupID, final Integer[] inState)
            throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
        return getQuestionsResponsibleFor(lKey, inReviewerID, inGroupID, inState);
    }

    /** @deprecated */
    @Deprecated
    public QueryResult getReviewersQuestions(final Long inReviewerID, final String inGroupID, final Integer[] inState)
            throws VException, SQLException {
        return getReviewersQuestions(inReviewerID, new Long(inGroupID), inState);
    }

    private QueryResult getQuestionsResponsibleFor(final KeyObject inKey, final Long inMemberID, final Long inGroupID,
            final Integer[] inState) throws VException, SQLException {
        inKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
        inKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
        inKey.setValue(BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState));
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, false, 0);
        return select(inKey, lOrder);
    }

    /** Returns the Author's view of questions, i.e. all questions the specified user authors together with all questions
     * having the specified states.
     * 
     * @param inAuthorID Long the member ID of the author.
     * @param inGroupID java.lang.String the group the questions belong to.
     * @param inState java.lang.String[] the States the returned questions have if they aren't authored by the user.
     * @return OutlinedQueryResult
     * @throws VException
     * @throws SQLException
     * @deprecated use {@link JoinAuthorReviewerToQuestionHome#getAuthorView(Long, Long, Integer[])} instead */
    @Deprecated
    public OutlinedQueryResult getAuthorView(final Long inAuthorID, final String inGroupID, final Integer[] inState)
            throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        final KeyObject lKeyStates = BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState);
        final KeyObject lKeyAuthor = new KeyObjectImpl();
        lKeyAuthor.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyAuthor.setValue(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
        lKeyStates.setValue(lKeyAuthor, BinaryBooleanOperator.OR);
        lKey.setValue(lKeyStates);
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, false, 0);
        return new OutlinedQueryResult(getCount(lKey), select(lKey, lOrder));
    }

    /** Returns the Author's view of questions, i.e. all questions the specified user authors together with all questions
     * having the specified states.
     * 
     * @param inAuthorID Long
     * @param inGroupID Long
     * @param inState String[] the States the returned questions have if they aren't authored by the user
     * @return {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    public QueryResult getAuthorView(final Long inAuthorID, final Long inGroupID, final Integer[] inState)
            throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        final KeyObject lKeyStates = BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState);
        final KeyObject lKeyAuthor = new KeyObjectImpl();
        lKeyAuthor.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyAuthor.setValue(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
        lKeyStates.setValue(lKeyAuthor, BinaryBooleanOperator.OR);
        lKey.setValue(lKeyStates);
        final OrderObject lOrder = new OrderObjectImpl();
        lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, false, 0);
        return select(lKey, lOrder);
    }

    /** Returns the GroupAdmin's view of questions, i.e. all questions she authors together with all published (i.e. open
     * or settled) questions. <b>Note:</b> this method doesn't verify whether the specified user is actually the
     * GroupAdmin of the specified group.
     * 
     * @param inGroupAdminID Long the member ID of the author.
     * @param inGroupID java.lang.String the group the questions belong to.
     * @return OutlinedQueryResult
     * @throws VException
     * @throws SQLException
     * @deprecated use {@link JoinAuthorReviewerToQuestionHome#getGroupAdminView(Long, Long)} instead */
    @Deprecated
    public OutlinedQueryResult getGroupAdminView(final Long inGroupAdminID, final String inGroupID) throws VException,
            SQLException {
        return getAuthorView(inGroupAdminID, inGroupID, WorkflowAwareContribution.STATES_PUBLISHED);
    }

    /** Returns the GroupAdmin's view of questions, i.e. all questions she authors together with all published (i.e. open
     * or settled) questions. <b>Note:</b> this method doesn't verify whether the specified user is actually the
     * GroupAdmin of the specified group.
     * 
     * @param inGroupAdminID Long
     * @param inGroupID Long
     * @return {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    public QueryResult getGroupAdminView(final Long inGroupAdminID, final Long inGroupID) throws VException,
            SQLException {
        return getAuthorView(inGroupAdminID, inGroupID, WorkflowAwareContribution.STATES_PUBLISHED);
    }

}
