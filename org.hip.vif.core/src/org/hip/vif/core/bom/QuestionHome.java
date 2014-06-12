package org.hip.vif.core.bom;

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

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OutlinedQueryResult;
import org.hip.kernel.exc.VException;

/**
 * QuestionHome is responsible to manage instances
 * of class org.hip.vif.bom.Question.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Question
 */
public interface QuestionHome extends DomainObjectHome {
	public final static String KEY_ID 					= "ID";
	public final static String KEY_GROUP_ID 			= "GroupID";
	public final static String KEY_QUESTION_DECIMAL 	= "DecimalID";
	public final static String KEY_QUESTION 			= "Question";
	public final static String KEY_REMARK 				= "Remark";
	public final static String KEY_ROOT_QUESTION 		= "RootQuestion";
	public final static String KEY_STATE 				= "State";
	public final static String KEY_MUTATION 			= "Mutation";
	
	public final static Long IS_ROOT 	= new Long(1);
	public final static Long NOT_ROOT	= new Long(0);
	
	/**
	 * Counts all questions belonging to the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	int getCountOfGroup(String inGroupID) throws VException, SQLException;
	
	/**
	 * Returns all questions belonging to the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @param inOrder org.hip.kernel.bomOrderObject
	 * @return org.hip.kernel.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	QueryResult selectOfGroup(String inGroupID, OrderObject inOrder) throws VException, SQLException;

	/**
	 * Returns the questions of the specified group according to the role of the specified
	 * member.<br/>
	 * IF <code>inActorID</code> is SU or ADMIN, all questions (belonging to the group) are returned.<br/>
	 * IF <code>inActorID</code> is GROUP_ADMIN, all open questions and all questions the specified user has authored (belonging to the group) are returned.<br/>
	 * ELSE, all published questions (belonging to the group) are returned.
	 * 
	 * @param inGroupID String
	 * @param inOrder OrderObject
	 * @param inActorID Long
	 * @return OutlinedQueryResult
	 * @throws Exception
	 * @deprecated use {@link QuestionHome#selectOfGroupFiltered(Long inGroupID, OrderObject inOrder, Long inActorID)} instead
	 */
	OutlinedQueryResult selectOfGroupFiltered(String inGroupID, OrderObject inOrder, Long inActorID) throws Exception;
	
	/**
	 * Returns the questions of the specified group according to the role of the specified
	 * member.<br/>
	 * IF <code>inActorID</code> is SU or ADMIN, all questions (belonging to the group) are returned.<br/>
	 * IF <code>inActorID</code> is GROUP_ADMIN, all open questions and all questions the specified user has authored (belonging to the group) are returned.<br/>
	 * ELSE, all published questions (belonging to the group) are returned.
	 * 
	 * @param inGroupID Long
	 * @param inOrder {@link OrderObject}
	 * @param inActorID Long
	 * @return {@link QueryResult}
	 * @throws Exception
	 */
	QueryResult selectOfGroupFiltered(Long inGroupID, OrderObject inOrder, Long inActorID) throws Exception;

	/**
	 * Returns the group's published questions.
	 * 
	 * @param inGroupID Long
	 * @param inOrder OrderObject
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	QueryResult selectOfGroupPublished(Long inGroupID, OrderObject inOrder) throws VException, SQLException;

	/**
	 * Returns the question identified by the specified id.
	 * 
	 * @param inQuestionID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	Question getQuestion(String inQuestionID) throws VException, SQLException;
	Question getQuestion(Long inQuestionID) throws VException, SQLException;

	/**
	 * Returns the root questions of the specified group.
	 * 
	 * @param inGroupID java.lang.String
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	QueryResult getRootQuestions(String inGroupID) throws VException, SQLException;
	
	/**
	 * Checks whether the specified group has any questions other then deleted ones.
	 * 
	 * @param inGroupID String the group's ID
	 * @return boolean <code>true</code> if the group has any questions other then deleted ones.
	 * @throws VException 
	 * @throws SQLException 
	 */
	boolean hasQuestionsInGroup(String inGroupID) throws VException, SQLException;
	
	/**
	 * Checks whether the specified group has any questions other then deleted ones.
	 * 
	 * @param inGroupID Long
	 * @return boolean <code>true</code> if the group has any questions other then deleted ones.
	 * @throws VException
	 * @throws SQLException
	 */
	boolean hasQuestionsInGroup(Long inGroupID) throws VException, SQLException;	
}
