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

package org.hip.vif.admin.permissions.internal;

import org.hip.vif.admin.permissions.Activator;
import org.hip.vif.admin.permissions.Constants;
import org.hip.vif.admin.permissions.tasks.PermissionsEditTask;
import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.interfaces.IPermissionRecord;
import org.hip.vif.core.interfaces.IPermissionRecords;
import org.hip.vif.core.util.PermissionRecord;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseAdmin;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.menu.VIFMenuComposite;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service provider for <code>IUseCaseAdmin</code>.
 * 
 * @author Luthiger
 * Created: 14.12.2011
 */
public class UseCaseComponent implements IPermissionRecords, IUseCaseAdmin {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getMenu()
	 */
	public IVIFMenuItem getMenu() {
		IMessages lMessages = Activator.getMessages();
		VIFMenuComposite outMenu = new VIFMenuComposite(lMessages.getMessage("component.menu.title"), 40); //$NON-NLS-1$
		outMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(PermissionsEditTask.class));
		outMenu.setPermission(Constants.PERMISSION_EDIT);
		return outMenu;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskClasses()
	 */
	public Package getTaskClasses() {
		return PermissionsEditTask.class.getPackage();
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

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IPermissionRecords#getPermissionRecords()
	 */
	public IPermissionRecord[] getPermissionRecords() {
		IPermissionRecord[] outRecords = new IPermissionRecord[1];
		outRecords[0] = new PermissionRecord(Constants.PERMISSION_LOOKUP_MEMBERS, "Forum: Lookup members.",  //$NON-NLS-1$
											 new int[] {RolesConstants.ADMINISTRATOR, RolesConstants.GROUP_ADMINISTRATOR, RolesConstants.PARTICIPANT, RolesConstants.MEMBER, RolesConstants.EXCLUDED_PARTICIPANT});
		return outRecords;
	}

}
