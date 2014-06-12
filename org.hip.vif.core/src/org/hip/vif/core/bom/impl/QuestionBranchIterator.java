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

package org.hip.vif.core.bom.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/**
 * Iterator searching the Questions hierarchy down to the leaves.
 * 
 * @author Benno Luthiger
 * Created on Mar 12, 2004
 */
public class QuestionBranchIterator extends VObject {
	private Long startID = null;
	private Collection<Long> questionMemory = new Vector<Long>();
	private Collection<Object> completionMemory = new Vector<Object>();
	private boolean processCompletions = false;

	/**
	 * QuestionBranchIterator constructor.
	 */
	public QuestionBranchIterator() {
		super();
	}

	/**
	 * QuestionBranchIterator constuctor with specified ID of the node the iterator has to start.
	 * 
	 * @param inStartID Long
	 */
	public QuestionBranchIterator(Long inStartID) {
		super();
		startID = inStartID;
	}

	/**
	 * Starts the iteration at the specified node id.
	 * 
	 * @param inStartID Long ID of the node the iteration starts.
	 * @param inStartHere boolean true if the iterator has to process the start node too, false if processing begins at parent node.
	 * @param inProcessCompletions boolean true if the iterator has to process completions too.
	 * @param inVisitor DomainObjectVisitor used for processing the iterated nodes.
	 * @throws VException
	 * @throws SQLException
	 */
	public void start(Long inStartID, boolean inStartHere, boolean inProcessCompletions, QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
		startID = inStartID;
		processCompletions = inProcessCompletions;
		start(inStartHere, inVisitor);
	}

	/**
	 * Starts the iteration.
	 * 
	 * @param inStartHere boolean true if the iterator has to process the start node too, false if processing begins at parent node.
	 * @param inVisitor DomainObjectVisitor used for processing the iterated nodes.
	 * @throws VException
	 * @throws SQLException
	 */
	public void start(boolean inStartHere, QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
		//iterate only with defined start id
		if (startID == null) {
			return;
		}
		
		Long lCurrent = startID;
		if (inStartHere) {
			if (iteratedNodeBefore(lCurrent)) return;
			
			((QuestionHierarchyEntry)((QuestionHome)BOMHelper.getQuestionHome()).getQuestion(lCurrent.toString())).accept(inVisitor);
			if (processCompletions) {
				processCompletions(lCurrent, inVisitor);
			}
			questionMemory.add(lCurrent);
		}
		
		JoinQuestionToChildHome lHome = (JoinQuestionToChildHome)BOMHelper.getJoinQuestionToChildHome();
		QueryResult lChilds = lHome.getChilds(lCurrent);
		while (lChilds.hasMoreElements()) {
			recurse((JoinQuestionToChild)lChilds.nextAsDomainObject(), inVisitor);
		}
		
	}
	
	private void recurse(JoinQuestionToChild inCurrent, QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
		Long lCurrentID = new Long(inCurrent.get(QuestionHome.KEY_ID).toString());
		
		//return if this node has been processed before
		if (iteratedNodeBefore(lCurrentID)) return;
		
		//process the current
		((QuestionHierarchyEntry)inCurrent).accept(inVisitor);
		if (processCompletions) {
			processCompletions(lCurrentID, inVisitor);
		}
		questionMemory.add(lCurrentID);
		
		//iterate further
		JoinQuestionToChildHome lHome = (JoinQuestionToChildHome)BOMHelper.getJoinQuestionToChildHome();
		QueryResult lChilds = lHome.getChilds(lCurrentID);
		while (lChilds.hasMoreElements()) {
			recurse((JoinQuestionToChild)lChilds.nextAsDomainObject(), inVisitor);
		}
	}
	
	private boolean iteratedNodeBefore(Long inIDtoTest) {
		return questionMemory.contains(inIDtoTest);
	}
	
	private void processCompletions(Long inQuestionID, QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
		QueryResult lCompletions = ((JoinCompletionToQuestionHome)BOMHelper.getJoinCompletionToQuestionHome()).getCompletions(inQuestionID);
		while (lCompletions.hasMoreElements()) {
			QuestionHierarchyEntry lCompletion = ((QuestionHierarchyEntry)lCompletions.next());
			lCompletion.accept(inVisitor);
			completionMemory.add(((DomainObject)lCompletion).get(CompletionHome.KEY_ID));
		}		
	}
	
	/**
	 * Checks whether the specified completion has been visited before.
	 * 
	 * @param inComletionID Long
	 * @return boolean true if the specified completion has been visited before.
	 */
	public boolean checkCompletion(Long inComletionID) {
		return completionMemory.contains(inComletionID);
	}
}
