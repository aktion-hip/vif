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
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;

/**
 * Home of join from the question-author/reviewer BOM to the completion BOM.
 * This home can be used to retrieve the question data an author or reviewer
 * is responsible to.
 * 
 * @author: Benno Luthiger
 */
public class JoinAuthorReviewerToCompletionHome extends JoinedDomainObjectHomeImpl {
	public final static String KEY_ALIAS_QUESTION_ID = "QuestionIntID";

	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinAuthorReviewerToCompletion";

	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='JoinAuthorReviewerToCompletion' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_COMPLETION + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + CompletionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + CompletionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			<columnDef columnName='" + CompletionAuthorReviewerHome.KEY_COMPLETION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionAuthorReviewerImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"				<columnDef columnName='" + CompletionHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.CompletionImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"	    </joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
		
	/**
	 * Constructor for JoinQuestionToAuthorReviewer.
	 */
	public JoinAuthorReviewerToCompletionHome() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return JOIN_OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/**
	 * Returns all completions in the specified group the specified member 
	 * has authored.
	 * 
	 * @param inAuthorID java.lang.Long
	 * @param inGroupID java.lang.String
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthorsCompletions(Long inAuthorID, Long inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		return getCompletionsResponsibleFor(lKey, inAuthorID, inGroupID);
	}
	/**
	 * @deprecated
	 */
	public QueryResult getAuthorsCompletions(Long inAuthorID, String inGroupID) throws VException, SQLException {
		return getAuthorsCompletions(inAuthorID, new Long(inGroupID));
	}

	/**
	 * Returns all completions in the specified group the specified member 
	 * has reviewed.
	 * 
	 * @param inReviewerID java.lang.Long
	 * @param inGroupID java.lang.String
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getReviewersCompletions(Long inReviewerID, Long inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		return getCompletionsResponsibleFor(lKey, inReviewerID, inGroupID);
	}
	/**
	 * @deprecated
	 */
	public QueryResult getReviewersCompletions(Long inReviewerID, String inGroupID) throws VException, SQLException {
		return getReviewersCompletions(inReviewerID, new Long(inGroupID));
	}
	
	private QueryResult getCompletionsResponsibleFor(KeyObject inKey, Long inMemberID, Long inGroupID) throws VException, SQLException {
		inKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
		inKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		return select(inKey);
	}
	
	/**
	 * Returns all completions being in the specified state in the specified group 
	 * the specified member has authored.
	 * 
	 * @param inAuthorID Long
	 * @param inGroupID Long
	 * @param inState java.lang.String[]
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthorsCompletions(Long inAuthorID, Long inGroupID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		return getContributionsResponsibleFor(lKey, inAuthorID, inGroupID, inState);
	}
	/**
	 * @deprecated
	 */
	public QueryResult getAuthorsCompletions(Long inAuthorID, String inGroupID, Integer[] inState) throws VException, SQLException {
		return getAuthorsCompletions(inAuthorID, new Long(inGroupID), inState);
	}
	
	/**
	 * Returns the specified author's yet unpublished completions in the specified group.
	 * 
	 * @param inAuthorID Long
	 * @param inGroupID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthorsUnpublishedCompletions(Long inAuthorID, Long inGroupID) throws VException, SQLException {
		return getAuthorsCompletions(inAuthorID, inGroupID, WorkflowAwareContribution.STATES_UNPUBLISHED);
	}
	/**
	 * @deprecated
	 */
	public QueryResult getAuthorsUnpublishedCompletions(Long inAuthorID, String inGroupID) throws VException, SQLException {
		return getAuthorsUnpublishedCompletions(inAuthorID, new Long(inGroupID));
	}
	
	/**
	 * Returns all completions being in the specified state in the specified group 
	 * the specified member has reviewed.
	 * 
	 * @param inReviewerID Long
	 * @param inGroupID Long
	 * @param inState java.lang.Integer[]
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getReviewersCompletions(Long inReviewerID, Long inGroupID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		return getContributionsResponsibleFor(lKey, inReviewerID, inGroupID, inState);
	}
	/**
	 * @deprecated
	 */
	public QueryResult getReviewersCompletions(Long inReviewerID, String inGroupID, Integer[] inState) throws VException, SQLException {
		return getReviewersCompletions(inReviewerID, new Long(inGroupID), inState);
	}
	
	private QueryResult getContributionsResponsibleFor(KeyObject inKey, Long inMemberID, Long inGroupID, Integer[] inState) throws VException, SQLException {
		inKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
		inKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		inKey.setValue(BOMHelper.getKeyStates(CompletionHome.KEY_STATE, inState));
		return select(inKey);
	}
	
}
