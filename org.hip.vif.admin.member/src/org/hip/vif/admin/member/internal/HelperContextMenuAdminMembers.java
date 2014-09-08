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

package org.hip.vif.admin.member.internal;

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.tasks.MemberNewTask;
import org.hip.vif.admin.member.tasks.MemberShowListTask;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IContextMenuItem;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.ContextMenuItem;

/** Helper class to create the context menus.
 *
 * @author Luthiger Created: 18.10.2011 */
public class HelperContextMenuAdminMembers {
    private static final IMessages MESSAGES = Activator.getMessages();
    private static final IContextMenuItem LIST_MEMBERS = new ContextMenuItem(MemberShowListTask.class,
            "context.menu.members.showList", Constants.PERMISSION_SEARCH, MESSAGES); //$NON-NLS-1$
    private static final IContextMenuItem NEW_MEMBER = new ContextMenuItem(MemberNewTask.class,
            "context.menu.members.new", Constants.PERMISSION_SEARCH, MESSAGES); //$NON-NLS-1$

    // ---

    public static IMenuSet createContextMenuSet1() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_DEFAULT;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { LIST_MEMBERS, NEW_MEMBER };
            }
        };
    }

    public static IMenuSet createContextMenuSet2() {
        return new IMenuSet() {
            @Override
            public String getSetID() {
                return Constants.MENU_SET_ID_LIST;
            }

            @Override
            public IContextMenuItem[] getContextMenuItems() {
                return new IContextMenuItem[] { NEW_MEMBER };
            }
        };
    }

}
