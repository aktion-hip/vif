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

package org.hip.vif.admin.member.internal;

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.tasks.MemberNewTask;
import org.hip.vif.admin.member.tasks.MemberShowListTask;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.hip.vif.web.util.ContextMenuItem;

/**
 * Helper class to create the context menus.
 * 
 * @author Luthiger
 * Created: 18.10.2011
 */
public class HelperContextMenuAdminMembers {
	private static final IMessages MESSAGES = Activator.getMessages();
	private static final IVIFContextMenuItem LIST_MEMBERS = new ContextMenuItem(MemberShowListTask.class, 
			"context.menu.members.showList", Constants.PERMISSION_SEARCH,  //$NON-NLS-1$
			false, false, false, new String[] {}, MESSAGES
	);
	private static final IVIFContextMenuItem NEW_MEMBER = new ContextMenuItem(MemberNewTask.class, 
			"context.menu.members.new", Constants.PERMISSION_SEARCH,  //$NON-NLS-1$
			false, false, false, new String[] {}, MESSAGES
			);
	
// ---
	
	public static IMenuSet createContextMenuSet1() {
		return new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_DEFAULT;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {LIST_MEMBERS, NEW_MEMBER};
			}
		};
	}
	
	public static IMenuSet createContextMenuSet2() {
		return new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_LIST;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {NEW_MEMBER};
			}
		};
	}

}
