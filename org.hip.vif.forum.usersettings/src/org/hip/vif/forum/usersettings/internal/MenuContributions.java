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

package org.hip.vif.forum.usersettings.internal;

import java.util.Collections;
import java.util.List;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.tasks.BookmarksManageTask;
import org.hip.vif.forum.usersettings.tasks.SubscriptionsManageTask;
import org.hip.vif.forum.usersettings.tasks.UserTasksManageTask;
import org.hip.vif.web.menu.ExtendibleMenuMarker.Position;
import org.hip.vif.web.menu.ExtendibleMenuMarker.PositionType;
import org.hip.vif.web.menu.IExtendibleMenuContribution;
import org.hip.vif.web.menu.IExtendibleMenuContributions;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service instance for <code>IExtendibleMenuContributions</code>, 
 * i.e. the contributions to an extendible menu.
 * 
 * @author Luthiger
 * Created: 19.12.2011
 */
public class MenuContributions implements IExtendibleMenuContributions {
	private static final IMessages MESSAGES = Activator.getMessages();
	
	//define menu contributions
	private static IExtendibleMenuContribution MANAGE_SUBSCRIPTIONS_TASK = new IExtendibleMenuContribution() {
		public String getExtendibleMenuID() {
			return Constants.EXTENDIBLE_MENU_ID;
		}
		public String getLabel() {
			return MESSAGES.getMessage("usersettings.menu.subscription"); //$NON-NLS-1$
		}
		public String getTaskName() {
			return UseCaseHelper.createFullyQualifiedTaskName(SubscriptionsManageTask.class);
		}

		public String getPermission() {
			return Constants.PERMISSION_MANAGE_SUBSCRIPTIONS;
		}
		public Position getPosition() {
			return new Position(PositionType.APPEND, Constants.EXTENDIBLE_MENU_POSITION_START);
		}
		public List<IVIFMenuItem> getSubMenu() {
			return Collections.emptyList();
		}		
	};
	private static IExtendibleMenuContribution MANAGE_BOOKMARK_TASK = new IExtendibleMenuContribution() {
		public String getExtendibleMenuID() {
			return Constants.EXTENDIBLE_MENU_ID;
		}
		public String getLabel() {
			return MESSAGES.getMessage("usersettings.menu.bookmarks"); //$NON-NLS-1$
		}
		public String getTaskName() {
			return UseCaseHelper.createFullyQualifiedTaskName(BookmarksManageTask.class);
		}		
		public String getPermission() {
			return Constants.PERMISSION_MANAGE_BOOKMARKS;
		}
		public Position getPosition() {
			return new Position(PositionType.APPEND, Constants.EXTENDIBLE_MENU_POSITION_START);
		}
		public List<IVIFMenuItem> getSubMenu() {
			return Collections.emptyList();
		}		
	};
	private static IExtendibleMenuContribution MANAGE_USER_TASK = new IExtendibleMenuContribution() {
		public String getExtendibleMenuID() {
			return Constants.EXTENDIBLE_MENU_ID;
		}
		public String getLabel() {
			return MESSAGES.getMessage("usersettings.menu.open.tasks"); //$NON-NLS-1$
		}
		public String getTaskName() {
			return UseCaseHelper.createFullyQualifiedTaskName(UserTasksManageTask.class);
		}
		public String getPermission() {
			return ""; //$NON-NLS-1$
		}
		public Position getPosition() {
			return new Position(PositionType.APPEND, Constants.EXTENDIBLE_MENU_POSITION_START);
		}
		public List<IVIFMenuItem> getSubMenu() {
			return Collections.emptyList();
		}		
	};
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.menu.IExtendibleMenuContributions#getContributions()
	 */
	public IExtendibleMenuContribution[] getContributions() {
		return new IExtendibleMenuContribution[] {MANAGE_BOOKMARK_TASK, MANAGE_SUBSCRIPTIONS_TASK, MANAGE_USER_TASK};
	}

}
