/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.core.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHierarchy;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;

/**
 * Checks the state of a collection of questions.
 * 
 * @author Benno Luthiger
 * Created on Apr 15, 2005
 */
public class QuestionStateChecker implements INodeCheckedProcessor {
	private Integer[] states;
	private INodeProcessor action;

	/**
	 * Checks whether all children of the specified question are in the specified state.
	 * This method returns false as soon it encounters the first question not in that state.
	 * 
	 * @param inQuestionID Long
	 * @param inStates Integer[]
	 * @return boolean true if all answered.
	 */
	public static boolean checkStateOfChilds(Long inQuestionID, Integer[] inStates) {
		Collection<String> lStates = new Vector<String>(inStates.length);
		for (Integer lState : inStates) {
			lStates.add(lState.toString());
		}
		try {
			QuestionHome lHome = (QuestionHome)BOMHelper.getQuestionHome();
			QueryResult lResult = ((QuestionHierarchyHome)BOMHelper.getQuestionHierarchyHome()).getChilds(inQuestionID);
			while (lResult.hasMoreElements()) {
				String lChildID = ((QuestionHierarchy)lResult.next()).get(QuestionHierarchyHome.KEY_CHILD_ID).toString();
				if (!lStates.contains(lHome.getQuestion(lChildID).get(QuestionHome.KEY_STATE).toString())) {
					return false;
				}
			}
			return true;
		}
		catch (VException exc) {
			return false;
		}
		catch (SQLException exc) {
			return false;
		}
	}
	
	/**
	 * @see INodeCheckedProcessor#checkPreCondition(Long)
	 */
	public boolean checkPreCondition(Long inQuestionID) {
		return checkStateOfChilds(inQuestionID, states);
	}
	
	/**
	 * @see INodeCheckedProcessor#doAction(Long)
	 */
	public void doAction(Long inQuestionID) throws WorkflowException, VException, SQLException {
		if (action == null) return;
		action.processNode(inQuestionID);
	}
	
	/**
	 * Sets the states do be checked while visiting.
	 * 
	 * @param inStates String[]
	 */
	public void setStates(Integer[] inStates) {
		states = inStates;
	}
	
	/**
	 * Sets the action performed if the check was ok.
	 * 
	 * @param inAction IQuestionLevelAction
	 */
	public void setAction(INodeProcessor inAction) {
		action = inAction;
	}
}
