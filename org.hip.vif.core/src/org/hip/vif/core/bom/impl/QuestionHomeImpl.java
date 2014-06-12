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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OutlinedQueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.RoleHome;

/**
 * This domain object home implements the QuestionHome interface.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.QuestionHome
 */
@SuppressWarnings("serial")
public class QuestionHomeImpl extends DomainObjectHomeImpl implements QuestionHome {
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionImpl";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<objectDef objectName='Question' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='QuestionID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_QUESTION_DECIMAL + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='sQuestionID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_QUESTION + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='sQuestion'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_REMARK + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='sRemark'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_STATE + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='nState'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_MUTATION + "' valueType='Timestamp' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='dtMutation'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_GROUP_ID + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='GroupID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_ROOT_QUESTION + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestion' columnName='bRootQuestion'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/**
	 * Counts all questions belonging to the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public int getCountOfGroup(String inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
		return getCount(lKey);
	}
	
	/**
	 * Returns all questions belonging to the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @param inOrder org.hip.kernel.bomOrderObject
	 * @return org.hip.kernel.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult selectOfGroup(String inGroupID, OrderObject inOrder) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
		return select(lKey, inOrder);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.QuestionHome#selectOfGroupFiltered(java.lang.String, org.hip.kernel.bom.OrderObject, java.lang.Long)
	 */
	public OutlinedQueryResult selectOfGroupFiltered(String inGroupID, OrderObject inOrder, Long inActorID) throws Exception {
		KeyObject lKey = new KeyObjectImpl();
		int lRole = ((MemberHome)BOMHelper.getMemberCacheHome()).getMember(inActorID.toString()).getBestRole();
		switch (lRole) {
			case RoleHome.ROLE_SU :
			case RoleHome.ROLE_ADMIN :
				//returns all
				break;
			case RoleHome.ROLE_GROUP_ADMIN :
				//returns published and private owned by group admin
				return ((JoinAuthorReviewerToQuestionHome)BOMHelper.getJoinAuthorReviewerToQuestionHome()).getGroupAdminView(inActorID, inGroupID);
			default :
				//returns published only
				lKey.setValue(BOMHelper.getKeyPublished(QuestionHome.KEY_STATE));
				break;
		}
		lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
		return new OutlinedQueryResult(getCount(lKey), select(lKey, inOrder));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.QuestionHome#selectOfGroupFiltered(java.lang.Long, org.hip.kernel.bom.OrderObject, java.lang.Long)
	 */
	public QueryResult selectOfGroupFiltered(Long inGroupID, OrderObject inOrder, Long inActorID) throws Exception {
		KeyObject lKey = new KeyObjectImpl();
		int lRole = ((MemberHome)BOMHelper.getMemberCacheHome()).getMember(inActorID.toString()).getBestRole();
		switch (lRole) {
		case RoleHome.ROLE_SU :
		case RoleHome.ROLE_ADMIN :
			//returns all
			break;
		case RoleHome.ROLE_GROUP_ADMIN :
			//returns published and private owned by group admin
			return ((JoinAuthorReviewerToQuestionHome)BOMHelper.getJoinAuthorReviewerToQuestionHome()).getGroupAdminView(inActorID, inGroupID);
		default :
			//returns published only
			lKey.setValue(BOMHelper.getKeyPublished(QuestionHome.KEY_STATE));
			break;
		}
		lKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		return select(lKey, inOrder);
	}
	
	/**
	 * Returns the group's published questions.
	 * 
	 * @param inGroupID String
	 * @param inOrder OrderObject
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectOfGroupPublished(Long inGroupID, OrderObject inOrder) throws VException, SQLException {
		return selectOfGroupFiltered(inGroupID, inOrder, WorkflowAwareContribution.STATES_PUBLISHED);
	}
	
	private QueryResult selectOfGroupFiltered(Long inGroupID, OrderObject inOrder, Integer[] inState) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(BOMHelper.getKeyStates(QuestionHome.KEY_STATE, inState));
		return select(lKey, inOrder);
	}

	/**
	 * Returns the question identified by the specified id.
	 * 
	 * @param inQuestionID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public Question getQuestion(String inQuestionID) throws VException, SQLException {
		return getQuestion(new Long(inQuestionID));
	}
	public Question getQuestion(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, inQuestionID);
		return (Question)findByKey(lKey);
	}
	
	/**
	 * Returns the root questions of the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult getRootQuestions(String inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(inGroupID));
		lKey.setValue(QuestionHome.KEY_ROOT_QUESTION, QuestionHome.IS_ROOT);
		return select(lKey);
	}
	
	/**
	 * Checks whether the specified question is a root.
	 * 
	 * @param inQuestionID String
	 * @return boolean true, if the question is root in the hierarchy
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean isRoot(String inQuestionID) throws VException, SQLException {
		return QuestionHome.IS_ROOT.equals(new Long(getQuestion(inQuestionID).get(QuestionHome.KEY_ROOT_QUESTION).toString()));
	}
	
	/**
	 * Checks whether the specified group has any questions other then deleted ones.
	 * 
	 * @param inGroupID String the group's ID
	 * @return boolean <code>true</code> if the group has any questions other then deleted ones.
	 * @throws VException 
	 * @throws SQLException 
	 */
	public boolean hasQuestionsInGroup(String inGroupID) throws VException, SQLException {
		return hasQuestionsInGroup(new Long(inGroupID));
	}
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.QuestionHome#hasQuestionsInGroup(java.lang.Long)
	 */
	public boolean hasQuestionsInGroup(Long inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(QuestionHome.KEY_STATE, new Long(WorkflowAwareContribution.S_DELETED), "!=");
		return getCount(lKey) != 0;
	}

}
