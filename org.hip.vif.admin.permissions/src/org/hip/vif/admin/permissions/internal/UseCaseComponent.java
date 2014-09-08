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

package org.hip.vif.admin.permissions.internal;

import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.tasks.PermissionsEditTask;
import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.interfaces.IPermissionRecord;
import org.hip.vif.core.interfaces.IPermissionRecords;
import org.hip.vif.core.util.PermissionRecord;
import org.ripla.interfaces.IControllerSet;
import org.ripla.interfaces.IMenuItem;
import org.ripla.interfaces.IMessages;
import org.ripla.menu.RiplaMenuComposite;
import org.ripla.web.interfaces.IMenuSet;
import org.ripla.web.services.IUseCase;
import org.ripla.web.util.UseCaseHelper;

/** This bundle's service provider for <code>IUseCaseAdmin</code>.
 *
 * @author Luthiger Created: 14.12.2011 */
public class UseCaseComponent implements IPermissionRecords, IUseCase {

    @Override
    public IMenuItem getMenu() {
        final IMessages lMessages = Activator.getMessages();
        final RiplaMenuComposite outMenu = new RiplaMenuComposite(lMessages.getMessage("component.menu.title"), 40); //$NON-NLS-1$
        outMenu.setControllerName(UseCaseHelper.createFullyQualifiedControllerName(PermissionsEditTask.class));
        outMenu.setPermission(Constants.PERMISSION_EDIT);
        return outMenu;
    }

    @Override
    public Package getControllerClasses() {
        return PermissionsEditTask.class.getPackage();
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
    public IPermissionRecord[] getPermissionRecords() {
        final IPermissionRecord[] outRecords = new IPermissionRecord[1];
        outRecords[0] = new PermissionRecord(Constants.PERMISSION_LOOKUP_MEMBERS, "Forum: Lookup members.", //$NON-NLS-1$
                new int[] { RolesConstants.ADMINISTRATOR, RolesConstants.GROUP_ADMINISTRATOR,
                        RolesConstants.PARTICIPANT, RolesConstants.MEMBER, RolesConstants.EXCLUDED_PARTICIPANT });
        return outRecords;
    }

}
