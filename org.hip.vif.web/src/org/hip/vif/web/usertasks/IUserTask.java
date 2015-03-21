/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
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
package org.hip.vif.web.usertasks;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.vif.web.interfaces.IPluggableWithLookup;

import com.vaadin.ui.Component;

/** Interface for user tasks, i.e. open tasks a participant has to finish.
 *
 * @author Luthiger Created: 27.08.2009 */
public interface IUserTask extends IPluggableWithLookup {

    /** @return String the user task's ID, used for registration. Must be unique. */
    String getId();

    /** Checks whether this task is open for the user with the specified ID.
     *
     * @param inMemberID Long
     * @return boolean <code>true</code> if the task is open for the user, else <code>false</code>.
     * @throws VException
     * @throws SQLException */
    boolean isOpen(Long inMemberID) throws VException, SQLException;

    /** Creates the view(s) displaying the form for that the user can finish the task.
     *
     * @param inMemberID Long
     * @return {@link Collection} of <code>Component</code>
     * @throws VException
     * @throws SQLException */
    Collection<Component> createUserTaskViews(Long inMemberID) throws VException, SQLException;

}
