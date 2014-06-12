/*
	This package is part of the application VIF.
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
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;

/**
 * Home of join from the question hierarchy to a child questions and 
 * author/reviewer table.
 * This home can be used to retrieve the data of child questions to
 * the specified question being authored by the specified member.
 * 
 * Created on 12.08.2003
 * @author Benno Luthiger
 */
@SuppressWarnings("serial")
public class JoinQuestionToChildAndAuthorHome extends JoinedDomainObjectHomeImpl {
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToChildAndAuthor";

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
		"<joinedObjectDef objectName='JoinQuestionToChildAndAuthor' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='" + QuestionHierarchyHome.KEY_PARENT_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionHierarchyImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION_DECIMAL + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_REMARK + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_ROOT_QUESTION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_GROUP_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_STATE + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + QuestionHome.KEY_MUTATION + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_MEMBER_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"		<columnDef columnName='" + ResponsibleHome.KEY_TYPE + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionHierarchyImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + QuestionHierarchyHome.KEY_CHILD_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionHierarchyImpl'/>	\n" +
		"			<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"				<columnDef columnName='" + QuestionAuthorReviewerHome.KEY_QUESTION_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionAuthorReviewerImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"		</joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * JoinQuestionToChildAndAuthorHome default constructor.
	 * 
	 */
	public JoinQuestionToChildAndAuthorHome() {
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
	 * <p>Returns all child questions to the specified question 
	 * that are either authored by the specified member or in the specified states.</p>
	 * 
	 * @param inQuestionID Long
	 * @param inAuthorID Integer
	 * @param inState Integer[]
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getAuthorView(Long inQuestionID, Long inAuthorID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		//K1: QuestionHierarchy.ParentID = inQuestionID AND QuestionAuthorReviewer.Type = 0 (i.e. we only select questions by authors, not by reviewers)
		lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, inQuestionID);
		lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		//K2Question.State = published OR QuestionAuthorReviewer.MemberID = inAuthorID (i.e. we only select published questions or questions authored by inAuthorID)
		KeyObject lKeyStates = BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState);
		lKeyStates.setValue(ResponsibleHome.KEY_MEMBER_ID, inAuthorID, "=", BinaryBooleanOperator.OR);
		//K1 AND K2
		lKey.setValue(lKeyStates);
		return select(lKey);
	}
	
	/**
     * Returns the published contributions of the specified group.
	 * 
	 * @param inGroupID String
	 * @param inOrder OrderObject
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectOfGroupPublished(String inGroupID, OrderObject inOrder) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
		lKey.setValue(BOMHelper.getKeyStates(QuestionHome.KEY_STATE, WorkflowAwareContribution.STATES_PUBLISHED));
		return select(lKey, inOrder);
	}
}
