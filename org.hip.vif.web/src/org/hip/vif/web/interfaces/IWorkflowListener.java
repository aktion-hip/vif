/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.web.interfaces;

import org.hip.vif.web.tasks.DBAccessWorkflow.ReturnCode;

/**
 * Interface for classes listening to steps of the DB access configuration workflow. 
 * 
 * @author Luthiger
 * Created: 14.02.2012
 */
public interface IWorkflowListener {
	
	/**
	 * Event signaling the workflow exit.
	 * 
	 * @param inReturnCode {@link ReturnCode} the code for the workflow exit
	 * @param inMessage String the exit message, in case of a workflow error
	 */
	void workflowExit(ReturnCode inReturnCode, String inMessage);

}
