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

import org.hip.vif.core.usertasks.IUserTask;
import org.hip.vif.core.usertasks.IUserTaskAgent;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.rating.RatingUserTask;
import org.hip.vif.forum.usersettings.tasks.BookmarksManageTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.menu.ExtendibleMenuMarker;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.util.AbstractExtendibleMenu;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service instance for <code>IUseCaseForum</code>.
 * 
 * @author Luthiger
 * Created: 19.12.2011
 */
public class UseCaseComponent implements IUseCaseForum, IUserTaskAgent {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getMenu()
	 */
	public IVIFMenuItem getMenu() {
		return new ExtendibleMenu();
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskClasses()
	 */
	public Package getTaskClasses() {
		return BookmarksManageTask.class.getPackage();
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskSet()
	 */
	public ITaskSet getTaskSet() {
		return UseCaseHelper.EMPTY_TASK_SET;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getContextMenus()
	 */
	public IMenuSet[] getContextMenus() {
		return UseCaseHelper.EMPTY_SUB_MENU_SET;
	}

	public IUserTask getUserTask() {
		return new RatingUserTask();
	}

// --- 

	/**
	 * Definition for an extendible menu.
	 * <p><b>Notes</b> (for providers that want to contribute to this menu):<br />
	 * This menu's ID is <code>adminMenu</code>.<br />
	 * The menu defines the makers <code>{"menuStart", "additions", "menuEnd"}</code>.
	 * </p>
	 * 
	 * @author Luthiger
	 * Created: 29.10.2011
	 */
	public static class ExtendibleMenu extends AbstractExtendibleMenu {

		/*
		 * (non-Javadoc)
		 * @see org.hip.vif.web.menu.IVIFMenuExtendible#getMenuID()
		 */
		public String getMenuID() {
			return Constants.EXTENDIBLE_MENU_ID;
		}

		/*
		 * (non-Javadoc)
		 * @see org.hip.vif.web.menu.IVIFMenuItem#getLabel()
		 */
		public String getLabel() {
			return Activator.getMessages().getMessage("component.menu.title"); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * @see org.hip.vif.web.menu.IVIFMenuItem#getPosition()
		 */
		public int getPosition() {
			return 60;
		}

		/*
		 * (non-Javadoc)
		 * @see org.hip.vif.web.menu.IVIFMenuExtendible#getMarkers()
		 */
		public ExtendibleMenuMarker[] getMarkers() {
			return new ExtendibleMenuMarker[] {
					new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_START),
					new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_ADDITIONS),
					new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_END)
			};
		}
	}

}
