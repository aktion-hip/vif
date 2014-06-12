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
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

/**
 * CompletionHome is responsible to manage instances
 * of class org.hip.vif.bom.Completion.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Completion
 */
public interface CompletionHome extends DomainObjectHome {
	public final static String KEY_ID 			= "ID";
	public final static String KEY_COMPLETION	= "Completion";
	public final static String KEY_STATE 		= "State";
	public final static String KEY_QUESTION_ID	= "QuestionID";
	public final static String KEY_MUTATION		= "Mutation";
	
	/**
	 * Returns a set of completions belonging to the question with the specified id.
	 * The completions are chronological order.
	 * 
	 * @param inQuestionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	QueryResult getCompletions(Long inQuestionID) throws VException, SQLException;
	QueryResult getCompletions(String inQuestionID) throws VException, SQLException;

	/**
	 * Returns the completion identified by the specified id.
	 * 
	 * @param inCompletionID Long
	 * @return Completion
	 * @throws VException
	 * @throws SQLException
	 */
	Completion getCompletion(Long inCompletionID) throws VException, SQLException;
	Completion getCompletion(String inCompletionID) throws VException, SQLException;

	/**
	 * Returns all completions belonging to the specified question
	 * except the specified completion, i.e. all siblings of the specified completion.
	 * 
	 * @param inQuestionID Long
	 * @param inCompletionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	QueryResult getSiblingCompletions(Long inQuestionID, Long inCompletionID) throws VException, SQLException;	
	QueryResult getSiblingCompletions(String inQuestionID, String inCompletionID) throws VException, SQLException;	

	/**
	 * Returns a set of published completions belonging to the question with 
	 * the specified id. The completions are ordered according to their primary key.
	 * 
	 * @param inQuestionID Long
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	QueryResult getPublished(Long inQuestionID) throws VException, SQLException;
	QueryResult getPublished(String inQuestionID) throws VException, SQLException;
}
