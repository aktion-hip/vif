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

package org.hip.vif.forum.usersettings.internal;

import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.rating.RatingUserTask;
import org.hip.vif.forum.usersettings.tasks.BookmarksManageTask;
import org.hip.vif.web.usertasks.IUserTask;
import org.hip.vif.web.usertasks.IUserTaskAgent;
import org.ripla.interfaces.IControllerSet;
import org.ripla.interfaces.IMenuItem;
import org.ripla.util.ExtendibleMenuMarker;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.AbstractExtendibleMenu;
import org.ripla.web.services.IUseCase;
import org.ripla.web.util.UseCaseHelper;

/** This bundle's service instance for <code>IUseCase</code>.
 *
 * @author Luthiger Created: 19.12.2011 */
public class UseCaseComponent implements IUseCase, IUserTaskAgent {

    @Override
    public IMenuItem getMenu() {
        return new ExtendibleMenu();
    }

    @Override
    public Package getControllerClasses() {
        return BookmarksManageTask.class.getPackage();
    }

    @Override
    public IControllerSet getControllerSet() {
        return UseCaseHelper.EMPTY_CONTROLLER_SET;
    }

    @Override
    public IMenuSet[] getContextMenus() {
        return UseCaseHelper.EMPTY_CONTEXT_MENU_SET;
    }

    @Override
    public IUserTask getUserTask() {
        return new RatingUserTask();
    }

    // ---

    /** Definition for an extendible menu.
     * <p>
     * <b>Notes</b> (for providers that want to contribute to this menu):<br />
     * This menu's ID is <code>adminMenu</code>.<br />
     * The menu defines the makers <code>{"menuStart", "additions", "menuEnd"}</code>.
     * </p>
     *
     * @author Luthiger Created: 29.10.2011 */
    public static class ExtendibleMenu extends AbstractExtendibleMenu {

        @Override
        public String getMenuID() {
            return Constants.EXTENDIBLE_MENU_ID;
        }

        @Override
        public String getLabel() {
            return Activator.getMessages().getMessage("component.menu.title"); //$NON-NLS-1$
        }

        @Override
        public int getPosition() {
            return 60;
        }

        @Override
        public ExtendibleMenuMarker[] getMarkers() {
            return new ExtendibleMenuMarker[] {
                    new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_START),
                    new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_ADDITIONS),
                    new ExtendibleMenuMarker(Constants.EXTENDIBLE_MENU_POSITION_END)
            };
        }

        @Override
        public String getTag() {
            return "vif.forum.menu";
        }
    }

}
