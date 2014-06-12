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

import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;

/**
 * Interface for a conditional node processor.
 * The action on the node is done only when the node meets the per-condition.
 * 
 * @author Benno Luthiger
 * Created on Apr 16, 2005
 */
public interface INodeCheckedProcessor {
	/**
	 * Checks the pre-condition for the action to be processed
	 * on the node (i.e. question) with the given ID.
	 * 
	 * @param inQuestionID Long
	 * @return true, if the pre-condition is met.
	 */
	boolean checkPreCondition(Long inQuestionID);
	
	/**
	 * Process the node with the given ID.
	 * 
	 * @param inQuestionID Long
	 * @throws SQLException
	 * @throws VException
	 * @throws WorkflowException
	 */
	void doAction(Long inQuestionID) throws WorkflowException, VException, SQLException;
}
