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

package org.hip.vif.admin.admin.internal;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.tasks.SendMailTask;
import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.interfaces.IPermissionRecord;
import org.hip.vif.core.interfaces.IPermissionRecords;
import org.hip.vif.core.util.PermissionRecord;
import org.ripla.interfaces.IControllerSet;
import org.ripla.interfaces.IMenuItem;
import org.ripla.util.ExtendibleMenuMarker;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.menu.AbstractExtendibleMenu;
import org.ripla.web.services.IUseCase;
import org.ripla.web.util.UseCaseHelper;

/** This bundle's service instance for <code>IUseCaseAdmin</code>.
 *
 * @author Luthiger Created: 28.10.2011 */
public class UseCaseComponent implements IUseCase, IPermissionRecords {

    @Override
    public Package getControllerClasses() {
        return SendMailTask.class.getPackage();
    }

    @Override
    public IControllerSet getControllerSet() {
        return UseCaseHelper.EMPTY_CONTROLLER_SET;
    }

    @Override
    public IMenuItem getMenu() {
        return new ExtendibleMenu();
    }

    @Override
    public IMenuSet[] getContextMenus() {
        return UseCaseHelper.EMPTY_CONTEXT_MENU_SET;
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
            return 50;
        }

        @Override
        public ExtendibleMenuMarker[] getMarkers() {
            return new ExtendibleMenuMarker[] {
                    new ExtendibleMenuMarker(
                            Constants.EXTENDIBLE_MENU_POSITION_START),
                    new ExtendibleMenuMarker(
                            Constants.EXTENDIBLE_MENU_POSITION_ADDITIONS),
                    new ExtendibleMenuMarker(
                            Constants.EXTENDIBLE_MENU_POSITION_END) };
        }

        @Override
        public String getTag() {
            return "vif.admin.menu";
        }
    }

    // ---

    @Override
    public IPermissionRecord[] getPermissionRecords() {
        final IPermissionRecord[] outRecords = new IPermissionRecord[6];
        outRecords[0] = new PermissionRecord(
                Constants.PERMISSION_REFRESH_INDEX, "Administration: Refresh the index for fulltext search.", //$NON-NLS-1$
                new int[] { RolesConstants.SU, RolesConstants.ADMINISTRATOR });
        outRecords[1] = new PermissionRecord(Constants.PERMISSION_SELECT_SKIN, "Administration: Select the skin.", //$NON-NLS-1$
                new int[] { RolesConstants.SU, RolesConstants.ADMINISTRATOR });
        outRecords[2] = new PermissionRecord(Constants.PERMISSION_SEND_MAIL,
                "Administration: Send mail to members or participants.", //$NON-NLS-1$
                new int[] { RolesConstants.ADMINISTRATOR,
                        RolesConstants.GROUP_ADMINISTRATOR });
        outRecords[3] = new PermissionRecord(Constants.PERMISSION_PRINT_GROUP,
                "Administration: Print content of discussion group.", //$NON-NLS-1$
                new int[] { RolesConstants.ADMINISTRATOR,
                        RolesConstants.GROUP_ADMINISTRATOR });
        outRecords[4] = new PermissionRecord(
                Constants.PERMISSION_CONFIGURATION, "Administration: Application configuration.", //$NON-NLS-1$
                new int[] { RolesConstants.SU, RolesConstants.ADMINISTRATOR });
        outRecords[5] = new PermissionRecord(Constants.PERMISSION_UPGRADE, "Administration: Application upgrade.", //$NON-NLS-1$
                new int[] { RolesConstants.SU, RolesConstants.ADMINISTRATOR });
        return outRecords;
    }

}
