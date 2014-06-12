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

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.RoleHome;

/**
 * Home of join from the question hierarchy to child questions.
 * This home can be used to retrieve the data of child questions to
 * the specified question.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class JoinQuestionToChildHome extends JoinedDomainObjectHomeImpl {
	//	Every home has to know the class it handles. They provide access to
	//	this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToChild";

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
		"<joinedObjectDef objectName='JoinQuestionToChild' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
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
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionHierarchyImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='" + QuestionHierarchyHome.KEY_CHILD_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionHierarchyImpl'/>	\n" +
		"			<columnDef columnName='" + QuestionHome.KEY_ID + "' domainObject='org.hip.vif.core.bom.impl.QuestionImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";

	/**
	 * Constructor for JoinQuestionToChildHome.
	 */
	public JoinQuestionToChildHome() {
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
	 * Returns the childs of the specified question.
	 * 
	 * @param inQuestionID Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult getChilds(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, inQuestionID);
		return select(lKey);
	}
	
	/**
	 * Returns all childs of the specified question being in the specified states.
	 * 
	 * @param inQuestionID Long
	 * @param inState Integer[]
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getChilds(Long inQuestionID, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, inQuestionID);
		lKey.setValue(BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState));
		return select(lKey);
	}
	
	/**
	 * Returns all published childs of the specified question.
	 * 
	 * @param inQuestionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult getPublishedChilds(Long inQuestionID) throws VException, SQLException {
		return getChilds(inQuestionID, WorkflowAwareContribution.STATES_PUBLISHED);
	}

	/**
	 * Returns the childs of the specified question depending of the specified
	 * member's role.
	 * IF <code>inActorID</code> is SU or ADMIN, all childs of the question are returned.<br/>
	 * IF <code>inActorID</code> is GROUP_ADMIN, all open childs and all childs the specified user has authored (of the question) are returned.<br/>
	 * ELSE, all published childs of the question are returned.
	 * 
	 * @param inQuestionID Long
	 * @param inActorID Long
	 * @return QueryResult
	 * @throws Exception
	 */
	public QueryResult getChildsFiltered(Long inQuestionID, Long inActorID) throws Exception {
		int lRole = ((MemberHome)BOMHelper.getMemberCacheHome()).getMember(inActorID.toString()).getBestRole();
		switch (lRole) {
			case RoleHome.ROLE_SU :
			case RoleHome.ROLE_ADMIN :
				//returns all
				return getChilds(inQuestionID);
			case RoleHome.ROLE_GROUP_ADMIN :
				//returns owned or published
				return ((JoinQuestionToChildAndAuthorHome)BOMHelper.getJoinQuestionToChildAndAuthorHome()).getAuthorView(inQuestionID, inActorID, WorkflowAwareContribution.STATES_PUBLISHED);
			default :
				//returns published
				return getPublishedChilds(inQuestionID);
		}
	}
	
	/**
	 * Returns the siblings of the specified question, including the question itself.
	 * 
	 * @param inQuestionID Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult getSiblings(Long inQuestionID) throws VException, SQLException {
		try {
			Long lParentID = new Long((((QuestionHierarchyHome)BOMHelper.getQuestionHierarchyHome()).getParent(inQuestionID).get(QuestionHierarchyHome.KEY_PARENT_ID)).toString());
			return getChilds(lParentID);
		}
		catch (BOMNotFoundException exc) {
			//if the root to the specified question cannot be found, the question is root itself
			QuestionHome lQuestionHome = (QuestionHome)BOMHelper.getQuestionHome();
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(QuestionHome.KEY_ID, inQuestionID);			
			String lGroupID = lQuestionHome.findByKey(lKey).get(QuestionHome.KEY_GROUP_ID).toString();			
			return lQuestionHome.getRootQuestions(lGroupID);
		}
	}
}
