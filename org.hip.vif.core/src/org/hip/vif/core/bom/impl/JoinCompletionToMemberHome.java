/*
	This package is part of the administration of the application VIF.
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
import java.sql.Timestamp;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * Home of join from the Completion BOM to the Author/Reviewer and Member BOM.
 * This home can be used to retrieve information about completions and authors
 * of a specified question.
 * 
 * Created on 29.05.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
public class JoinCompletionToMemberHome extends JoinedDomainObjectHomeImpl {
	public static final String KEY_ALIAS_MEMBER_ID = "MemberID";
		
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinCompletionToMember";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinCompletionToMember' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_COMPLETION + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_MUTATION + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_CREATED + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_ID + "' alias='" + KEY_ALIAS_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_NAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_FIRSTNAME + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"		<columnDef columnName='" + MemberHome.KEY_MAIL + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			<columnDef columnName='" + CompletionAuthorReviewerHome.KEY_COMPLETION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"				<columnDef columnName='" + MemberHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"		</joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * JoinCompletionToMemberHome default constructor.
	 * 
	 */
	public JoinCompletionToMemberHome() {
		super();
	}

	/**
	 * Returns the name of the objects which the this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return JOIN_CLASS_NAME;
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
	 * Returns a set of completions belonging to the question with the specified id.
	 * The completions are in original order.
	 * 
	 * @param inQuestionID
	 * @return QueryResult
	 * @throws BOMChangeValueException
	 */
	public QueryResult select(Long inQuestionID) throws BOMChangeValueException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
			lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
			OrderObject lOrder = new OrderObjectImpl();
			lOrder.setValue(CompletionHome.KEY_ID, 0);
			return select(lKey, lOrder);
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}
	
	/**
	 * Returns the completions being in the specified states belonging to the specified question.<br />
	 * Note: only the completions' authors are retrieved.
	 * 
	 * @param inQuestionID Long
	 * @param inState String[]
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult select(Long inQuestionID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		lKey.setValue(BOMHelper.getKeyStates(CompletionHome.KEY_STATE, inState));
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(CompletionHome.KEY_ID, 0);
		return select(lKey, lOrder);
	}
	
	/**
	 * Returns the completions being in the specified states belonging to the specified question.<br />
	 * Note: the completions' may be returned twice: first with the author's date and in addition 
	 * with the reviewer's data.
	 * 
	 * @param inQuestionID Long
	 * @param inState String[]
	 * @return {@link QueryResult}
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectWithResponsibles(Long inQuestionID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		lKey.setValue(BOMHelper.getKeyStates(CompletionHome.KEY_STATE, inState));
		KeyObject lResponsible = new KeyObjectImpl();
		lResponsible.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lResponsible.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue(), "=", BinaryBooleanOperator.OR);
		lKey.setValue(lResponsible);
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(CompletionHome.KEY_ID, 0);
		return select(lKey, lOrder);
	}
	
	/**
	 * Returns the question's published completions.<br />
	 * Note: only the completions' authors are retrieved.
	 * 
	 * @param inQuestionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectPublished(Long inQuestionID) throws VException, SQLException {
		return select(inQuestionID, WorkflowAwareContribution.STATES_PUBLISHED);
	}
	
	/**
	 * Returns the question's published completions.<br />
	 * Note: the completions' may be returned twice: 
	 * first with the author's date and in addition with the reviewer's data.
	 * 
	 * @param inQuestionID Long
	 * @return {@link QueryResult}
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectPublishedWithResponsibles(Long inQuestionID) throws VException, SQLException {
		return selectWithResponsibles(inQuestionID, WorkflowAwareContribution.STATES_PUBLISHED);
	}
	
	/**
	 * Returns the Author's view of the question's completions, i.e. all completions 
	 * the specified user authors together with all completions having the specified states.
	 * 
	 * @param inQuestionID Long
	 * @param inAuthorID Long
	 * @param inState String[]
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectAuthorView(Long inQuestionID, Long inAuthorID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		//SELECT ... WHERE tblCompletion.QUESTIONID = inQuestionID
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		//  AND (tblCompletion.NSTATE = '4' OR tblCompletion.NSTATE = '6' OR tblCompletion.NSTATE = '5'
		KeyObject lKeyStates = BOMHelper.getKeyStates(CompletionHome.KEY_STATE, inState);
		//       OR (tblCompletionAuthorReviewer.NTYPE = 0 AND tblCompletionAuthorReviewer.MEMBERID = 1))
		lKeyStates.setValue(createAuthorKey(inAuthorID), BinaryBooleanOperator.OR);
		//  AND (tblCompletionAuthorReviewer.NTYPE = 0) 
		KeyObject lKeyAuthor = new KeyObjectImpl();
		lKeyAuthor.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(lKeyStates);
		lKey.setValue(lKeyAuthor, BinaryBooleanOperator.AND);
		//  ORDER BY tblCompletion.COMPLETIONID
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(CompletionHome.KEY_ID, 0);
		return select(lKey, lOrder);
	}

	/**
	 * Returns the Author's view of completions, i.e. all completions she authors except the deleted ones
	 * together with all published (i.e. open or settled) completions.
	 * 
	 * @param inQuestionID
	 * @param inAuthorID
	 * @return
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthorView(Long inQuestionID, Long inAuthorID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		//SELECT ... WHERE tblCompletion.QUESTIONID = inQuestionID
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		//  AND (tblCompletion.NSTATE = '4' OR tblCompletion.NSTATE = '6' OR tblCompletion.NSTATE = '5'
		KeyObject lKeyStates = BOMHelper.getKeyStates(CompletionHome.KEY_STATE, WorkflowAwareContribution.STATES_PUBLISHED);
		//       OR (tblCompletionAuthorReviewer.NTYPE = 0 AND tblCompletionAuthorReviewer.MEMBERID = 1 
		KeyObject lAuthorKey = createAuthorKey(inAuthorID);
		//           AND tblCompletion.NSTATE != '8'))
		lAuthorKey.setValue(CompletionHome.KEY_STATE, WorkflowAwareContribution.S_DELETED, "!=");
		lKeyStates.setValue(lAuthorKey, BinaryBooleanOperator.OR);
		//  AND (tblCompletionAuthorReviewer.NTYPE = 0) 
		KeyObject lKeyAuthor = new KeyObjectImpl();
		lKeyAuthor.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKey.setValue(lKeyStates);
		lKey.setValue(lKeyAuthor, BinaryBooleanOperator.AND);
		//  ORDER BY tblCompletion.COMPLETIONID
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(CompletionHome.KEY_ID, 0);
		return select(lKey, lOrder);
	}
	
	/**
	 * Returns the GroupAdmin's view of completions, i.e. all completions she authors
	 * together with all published (i.e. open or settled) completions.
	 * <b>Note:</b> this method doesn't verify whether the specified user is actually the GroupAdmin.
	 * 
	 * @param inQuestionID Long
	 * @param inAuthorID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getGroupAdminView(Long inQuestionID, Long inAuthorID) throws VException, SQLException {
		return selectAuthorView(inQuestionID, inAuthorID, WorkflowAwareContribution.STATES_PUBLISHED);
	}
	
	/**
	 * Returns the Author's view of the completion's siblings, i.e. all 
	 * published completions of the specified question together with all
	 * completions authored by the specified user except the specified completion.
	 * 
	 * @param inQuestionID Long
	 * @param inAuthorID Long
	 * @param inCompletionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectAuthorViewOfSiblings(Long inQuestionID, Long inAuthorID, Long inCompletionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		//all completions of the specified question
		lKey.setValue(CompletionHome.KEY_QUESTION_ID, inQuestionID);
		//except the specified completion
		lKey.setValue(CompletionHome.KEY_ID, inCompletionID, "!=");
		
		//published states AND completion.type = author		
		KeyObject lPublishedStates = BOMHelper.getKeyPublished(CompletionHome.KEY_STATE);
		lPublishedStates.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		
		//unpublished states AND (completion.type = author of member=author)
		KeyObject lUnpublishedStates = BOMHelper.getKeyStates(CompletionHome.KEY_STATE, WorkflowAwareContribution.STATES_UNPUBLISHED);
		lUnpublishedStates.setValue(createAuthorKey(inAuthorID));
		
		lPublishedStates.setValue(lUnpublishedStates, BinaryBooleanOperator.OR);
		lKey.setValue(lPublishedStates);
		return select(lKey);
	}
	
	/**
	 * Returns the completions of the specified question according to 
	 * the role of the specified member.<br/>
	 * IF <code>inActorID</code> is SU or ADMIN, all completions (of the question) are returned.<br/>
	 * IF <code>inActorID</code> is GROUP_ADMIN, all open completions and all completions the specified user has authored (of the question) are returned.<br/>
	 * ELSE, all published completions (of the question) are returned.
	 * 
	 * @param inQuestionID Long
	 * @param inActorID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getFiltered(Long inQuestionID, Long inActorID) throws Exception {
		int lRole = ((MemberHome)BOMHelper.getMemberCacheHome()).getMember(inActorID.toString()).getBestRole();
		switch (lRole) {
			case RoleHome.ROLE_SU :
			case RoleHome.ROLE_ADMIN :
				//returns all
				return select(inQuestionID);
			case RoleHome.ROLE_GROUP_ADMIN :
				//returns owned or published
				return getGroupAdminView(inQuestionID, inActorID);
			default :
				//returns published
				return selectPublished(inQuestionID);
		}
	}
	
	/**
	 * Creates a key to select entries the specified actor is author of.
	 * 
	 * @param inAuthorID Long
	 * @return KeyObject 
	 * @throws VException
	 */
	public KeyObject createAuthorKey(Long inAuthorID) throws VException {
		KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		outKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
		return outKey;
	}
	
	/**
	 * Returns all reviewers that requests for review older then the specified date.
	 * 
	 * @param inStaleDate {@link Timestamp} requests older than this date are stale
	 * @return {@link QueryResult}
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectStaleWaitingForReview(Timestamp inStaleDate) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_STATE, new Integer(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lKey.setValue(ResponsibleHome.KEY_TYPE, new Integer(ResponsibleHome.Type.REVIEWER.getValue()), "=", BinaryBooleanOperator.AND);
		lKey.setValue(ResponsibleHome.KEY_CREATED, inStaleDate, "<", BinaryBooleanOperator.AND);
		return select(lKey);
	}

}
