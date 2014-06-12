package org.hip.vif.core.bom.impl;

/*
	This package is part of the application VIF
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.impl.JoinCompletionToQuestion;
import org.hip.vif.core.bom.impl.JoinQuestionToChild;
import org.hip.vif.core.bom.impl.JoinSubscriptionToMember;

/**
 * A Visitor for DomainObjects in the QuestionsHierarchy.
 * 
 * @author Benno Luthiger
 * Created on Mar 12, 2004
 */
public interface QuestionHierarchyVisitor {

	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion Completion
	 * @throws VException
	 * @throws SQLException
	 */
	void visitCompletion(Completion inCompletion) throws VException, SQLException;
		
	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion JoinCompletionToQuestion
	 * @throws VException
	 * @throws SQLException
	 */
	void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException;
	
	/**
	 * Execute the visit of a Question entry in the QuestionHierarchy.
	 * 
	 * @param inQuestion Question
	 * @throws VException
	 * @throws SQLException
	 */
	void visitQuestion(Question inQuestion) throws VException, SQLException;
	
	/**
	 * Execute the visit of a Text entry.
	 * 
	 * @param inText {@link Text}
	 * @throws VException
	 * @throws SQLException
	 */
	void visitText(Text inText) throws VException, SQLException;
	
	/**
	 * Execute the visit of a JoinSubscriptionToMember entry in the QuestionHierarchy.
	 * 
	 * @param inSubscriber JoinSubscriptionToMember
	 * @throws VException
	 * @throws SQLException
	 */
	void visitSubscriber(JoinSubscriptionToMember inSubscriber) throws VException, SQLException;
	
	/**
	 * Execute the visit of a JoinQuestionToChild entry in the QuestionHierarchy.
	 * 
	 * @param inChild JoinQuestionToChild
	 * @throws VException
	 * @throws SQLException
	 */
	void visitChild(JoinQuestionToChild inChild) throws VException, SQLException;
}
