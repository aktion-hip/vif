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

package org.hip.vif.core.bom;

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.QuestionHierarchyHomeImpl.ChildrenChecker;

/**
 * QuestionHierarchyHome is responsible to manage instances
 * of class QuestionHierarchy.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.QuestionHierarchy
 */
public interface QuestionHierarchyHome extends DomainObjectHome {
	public final static String KEY_PARENT_ID			= "ParentID";
	public final static String KEY_CHILD_ID 			= "ChildID";
	public final static String KEY_GROUP_ID 			= "GroupID";

	/**
	 * Returns the parent of the specified question.
	 * 
	 * @param inQuestionID Long
	 * @return org.hip.vif.bom.QuestionHierarchy
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	QuestionHierarchy getParent(Long inQuestionID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	QuestionHierarchy getParent(String inQuestionID) throws VException, SQLException;
	
	/**
	 * Returns the childs of the specified question.
	 * 
	 * @param inQuestionID java.lang.Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	QueryResult getChilds(Long inQuestionID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	QueryResult getChilds(String inQuestionID) throws VException, SQLException;
	
	/**
	 * Returns the siblings of the specified question including the node itself.
	 * 
	 * @param inQuestionID Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	QueryResult getSiblings(Long inQuestionID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	QueryResult getSiblings(String inQuestionID) throws VException, SQLException;

	/**
	 * Returns the parent question of the specified question.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return org.hip.vif.bom.Question
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	Question getParentQuestion(Long inQuestionID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	Question getParentQuestion(String inQuestionID) throws VException, SQLException;

	/**
	 * Counts the specified question's childs.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return int
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	int countChilds(Long inQuestionID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	int countChilds(String inQuestionID) throws VException, SQLException;
	
	/**
	 * Adds a new entry to the question hierarchy.
	 * 
	 * @param inParentID java.lang.Long
	 * @param inChildID java.lang.Long
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	void ucNew(Long inParentID, Long inChildID, Long inGroupID) throws VException, SQLException;

	/**
	 * Returns true if the specified question has a parent.
	 * 
	 * @param inChildID
	 * @return boolean True, if the specified question has a parent.
	 * @throws VException
	 * @throws SQLException
	 */
	boolean hasParent(Long inChildID) throws VException, SQLException;
	
	/**
	 * Checks the visibility for guests of the specified question
	 * at the specified depth.
	 * 
	 * @param inQuestionID String
	 * @param inDepth Long
	 * @return boolean true, is question is visible
	 */
	boolean isVisibleForGuestDepth(Long inQuestionID, Long inDepth);
	/**
	 * @deprecated
	 */
	boolean isVisibleForGuestDepth(String inQuestionID, Long inDepth);
	
	/**
	 * Creates a <code>ChildrenChecker</code> instance to check the parent-child relations within a discussion group. 
	 * 
	 * @param inGroupID String the discussion group to check for the hierarchy
	 * @return {@link ChildrenChecker}
	 * @throws VException
	 * @throws SQLException
	 */
	ChildrenChecker getChildrenChecker(Long inGroupID) throws VException, SQLException;
	/**
	 * @deprecated
	 */
	ChildrenChecker getChildrenChecker(String inGroupID) throws VException, SQLException;
	
}
