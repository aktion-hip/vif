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

package org.hip.vif.admin.groupadmin.internal;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.tasks.AdminGroupShowListTask;
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

/**
 * This bundle's service provider for <code>IUseCaseAdmin</code>.
 * 
 * @author Luthiger Created: 19.11.2011
 */
public class UseCaseComponent implements IUseCase, IPermissionRecords {

	@Override
	public IMenuItem getMenu() {
		final IMessages lMessages = Activator.getMessages();
		final RiplaMenuComposite outMenu = new RiplaMenuComposite(
				lMessages.getMessage("component.menu.title"), 30); //$NON-NLS-1$
		outMenu.setControllerName(UseCaseHelper
				.createFullyQualifiedControllerName(AdminGroupShowListTask.class));
		outMenu.setPermission(Constants.PERMISSION_GROUPS_ADMIN);
		return outMenu;
	}

	@Override
	public Package getControllerClasses() {
		return AdminGroupShowListTask.class.getPackage();
	}

	@Override
	public IControllerSet getControllerSet() {
		return UseCaseHelper.EMPTY_CONTROLLER_SET;
	}

	@Override
	public IMenuSet[] getContextMenus() {
		return new IMenuSet[] {
				HelperContextMenuAdminDiscussion.createContextMenuSet1(),
				HelperContextMenuAdminDiscussion.createContextMenuSet2(),
				HelperContextMenuAdminDiscussion.createContextMenuSet3(),
				HelperContextMenuAdminDiscussion.createContextMenuSet4(),
				HelperContextMenuAdminDiscussion.createContextMenuSet5(),
				HelperContextMenuAdminDiscussion.createContextMenuSet6() };
	}

	@Override
	public IPermissionRecord[] getPermissionRecords() {
		final IPermissionRecord[] outRecords = new IPermissionRecord[1];
		outRecords[0] = new PermissionRecord(
				Constants.PERMISSION_EDIT_BIBLIOGRAPHY,
				"Administration: Create bibliography entry.", //$NON-NLS-1$
				new int[] { RolesConstants.ADMINISTRATOR,
						RolesConstants.GROUP_ADMINISTRATOR });
		return outRecords;
	}

}
