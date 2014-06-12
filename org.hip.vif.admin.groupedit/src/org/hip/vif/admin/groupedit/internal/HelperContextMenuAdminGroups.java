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

package org.hip.vif.admin.groupedit.internal;

import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.tasks.GroupEditTask;
import org.hip.vif.admin.groupedit.tasks.GroupNewTask;
import org.hip.vif.admin.groupedit.tasks.ParticipantListTask;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.hip.vif.web.util.ContextMenuItem;

/**
 * Helper class to create the context menus.
 * 
 * @author Luthiger
 * Created: 11.11.2011
 */
public class HelperContextMenuAdminGroups {
	private static final IMessages MESSAGES = Activator.getMessages();
	private static final IVIFContextMenuItem NEW_GROUP = new ContextMenuItem(GroupNewTask.class, 
			"context.menu.groupedit.new", Constants.PERMISSION_GROUP_CREATE,  //$NON-NLS-1$
			false, false, false, new String[] {}, MESSAGES
			);
	private static final IVIFContextMenuItem EDIT_GROUP = new ContextMenuItem(GroupEditTask.class, 
			"context.menu.groupedit.edit", Constants.PERMISSION_GROUPS_EDIT,  //$NON-NLS-1$
			false, false, false, new String[] {}, MESSAGES
			);
	private static final IVIFContextMenuItem LIST_PARTICIPANTS = new ContextMenuItem(ParticipantListTask.class, 
			"context.menu.list.participants", Constants.PERMISSION_LIST_PARTICIPANTS,  //$NON-NLS-1$
			false, false, false, new String[] {}, MESSAGES
			);
	
// ---
	
	public static IMenuSet createContextMenuSet1() {
		return new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_DEFAULT;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {NEW_GROUP};
			}
		};
	}
	
	public static IMenuSet createContextMenuSet2() {
		return new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_GROUP_SHOW;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {NEW_GROUP, LIST_PARTICIPANTS};
			}
		};
	}
	
	public static IMenuSet createContextMenuSet3() {
		return new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_SHOW_PARTICIPANTS;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {NEW_GROUP, EDIT_GROUP};
			}
		};
	}

}
