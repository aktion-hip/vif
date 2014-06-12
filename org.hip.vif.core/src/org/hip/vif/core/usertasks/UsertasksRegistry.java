/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.core.usertasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Component;

/**
 * The registry for user tasks.
 * 
 * @author Luthiger
 * Created: 23.12.2011
 */
public enum UsertasksRegistry {
	INSTANCE;
	
	private Map<String, IUserTask> registry = new Hashtable<String, IUserTask>();
	
	/**
	 * Checks whether the user with the specified ID has open tasks.
	 * 
	 * @param inMemberID Long
	 * @return boolean <code>true</code> if there are open tasks for the user, else <code>false</code>.
	 * @throws SQLException 
	 * @throws VException 
	 */
	public boolean hasOpenTasks(Long inMemberID) throws VException, SQLException {
		for (IUserTask lTask : registry.values()) {
			if (lTask.isOpen(inMemberID)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the collection of views of open tasks for that the user can finish those tasks.
	 *  
	 * @param inMemberID Long
	 * @param inEventAdmin 
	 * @return {@link Collection} of <code>Component</code>
	 * @throws SQLException 
	 * @throws VException 
	 */
	public Collection<Component> getTasksViews(Long inMemberID, EventAdmin inEventAdmin) throws Exception {
		Collection<Component> outViews = new Vector<Component>();
		for (IUserTask lTask : registry.values()) {
			if (lTask.isOpen(inMemberID)) {
				lTask.setEventAdmin(inEventAdmin);
				outViews.addAll(lTask.createUserTaskViews(inMemberID));
			}
		}
		return outViews;
	}

	/**
	 * Register the specified task to this registry. 
	 * 
	 * @param inUserTask IUserTask
	 */
	public void registerUserTask(IUserTask inUserTask) {
		String lId = inUserTask.getId();
		if (registry.get(lId) == null) {
			registry.put(lId, inUserTask);
		}
	}

	/**
	 * Unregister.
	 * 
	 * @param inUserTask IUserTask
	 */
	public void unregisterUserTask(IUserTask inUserTask) {
		registry.remove(inUserTask);
	}

}
