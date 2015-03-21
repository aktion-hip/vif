/**
	This package is part of the application VIF.
	Copyright (C) 2011-2015, Benno Luthiger

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

package org.hip.vif.web.usertasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hip.kernel.exc.VException;

import com.vaadin.ui.Component;

/** The registry for user tasks.
 *
 * @author Luthiger Created: 23.12.2011 */
public enum UsertasksRegistry {
    INSTANCE;

    private final Map<String, IUserTask> registry = new ConcurrentHashMap<String, IUserTask>(); // NOPMD

    /** Checks whether the user with the specified ID has open tasks.
     *
     * @param inMemberID Long
     * @return boolean <code>true</code> if there are open tasks for the user, else <code>false</code>.
     * @throws SQLException
     * @throws VException */
    public boolean hasOpenTasks(final Long inMemberID) throws VException, SQLException {
        for (final IUserTask lTask : registry.values()) {
            if (lTask.isOpen(inMemberID)) {
                return true;
            }
        }
        return false;
    }

    /** Returns the collection of views of open tasks for that the user can finish those tasks.
     *
     * @param inMemberID Long
     * @return {@link Collection} of <code>Component</code>
     * @throws SQLException
     * @throws VException */
    public Collection<Component> getTasksViews(final Long inMemberID) throws VException, SQLException {
        final Collection<Component> outViews = new ArrayList<Component>();
        for (final IUserTask lTask : registry.values()) {
            if (lTask.isOpen(inMemberID)) {
                outViews.addAll(lTask.createUserTaskViews(inMemberID));
            }
        }
        return outViews;
    }

    /** Register the specified task to this registry.
     *
     * @param inUserTask IUserTask */
    public void registerUserTask(final IUserTask inUserTask) {
        final String lId = inUserTask.getId();
        if (registry.get(lId) == null) {
            registry.put(lId, inUserTask);
        }
    }

    /** Unregister.
     *
     * @param inUserTask IUserTask */
    public void unregisterUserTask(final IUserTask inUserTask) {
        registry.remove(inUserTask);
    }

}
