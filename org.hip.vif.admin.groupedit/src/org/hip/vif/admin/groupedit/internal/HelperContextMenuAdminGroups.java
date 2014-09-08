/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.groupedit.internal;

import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.tasks.GroupEditTask;
import org.hip.vif.admin.groupedit.tasks.GroupNewTask;
import org.hip.vif.admin.groupedit.tasks.ParticipantListTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.ContextMenuItem;

/** Helper class to create the context menus.
 *
 * @author Luthiger Created: 11.11.2011 */
public class HelperContextMenuAdminGroups {
    private static final IMessages MESSAGES = Activator.getMessages();
    private static final IContextMenuItem NEW_GROUP = new ContextMenuItem(GroupNewTask.class,
            "context.menu.groupedit.new", Constants.PERMISSION_GROUP_CREATE, //$NON-NLS-1$
            MESSAGES);
    private static final IContextMenuItem EDIT_GROUP = new ContextMenuItem(GroupEditTask.class,
            "context.menu.groupedit.edit", Constants.PERMISSION_GROUPS_EDIT, //$NON-NLS-1$
            MESSAGES);
    private static final IContextMenuItem LIST_PARTICIPANTS = new ContextMenuItem(ParticipantListTask.class,
            "context.menu.list.participants", Constants.PERMISSION_LIST_PARTICIPANTS, //$NON-NLS-1$
            MESSAGES);

    // ---

    public static IMenuSet createContextMenuSet1() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_DEFAULT;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { NEW_GROUP };
            }
        };
    }

    public static IMenuSet createContextMenuSet2() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_GROUP_SHOW;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { NEW_GROUP, LIST_PARTICIPANTS };
            }
        };
    }

    public static IMenuSet createContextMenuSet3() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_SHOW_PARTICIPANTS;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { NEW_GROUP, EDIT_GROUP };
            }
        };
    }

}
