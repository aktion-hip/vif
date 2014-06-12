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

package org.hip.vif.forum.groups.internal;

import org.hip.vif.core.RolesConstants;
import org.hip.vif.core.interfaces.IPermissionRecord;
import org.hip.vif.core.interfaces.IPermissionRecords;
import org.hip.vif.core.util.PermissionRecord;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.internal.submenu.HelperContextMenuForumGroups;
import org.hip.vif.forum.groups.tasks.GroupShowListTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service provider for <code>IUseCaseForum</code>.
 * 
 * @author Luthiger
 * Created: 19.05.2011
 */
public class UseCaseComponent implements IUseCaseForum, IPermissionRecords {

	/* (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IUseCase#getMenu()
	 */
	public IVIFMenuItem getMenu() {
		return ComponentHelper.createMenu();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getSubMenus()
	 */
	public IMenuSet[] getContextMenus() {
		return new IMenuSet[] {HelperContextMenuForumGroups.createContextMenuSet1(),
				HelperContextMenuForumGroups.createContextMenuSet2(),
				HelperContextMenuForumGroups.createContextMenuSet3(),
				HelperContextMenuForumGroups.createContextMenuSet4(),
				HelperContextMenuForumGroups.createContextMenuSet5(),
				HelperContextMenuForumGroups.createContextMenuSet6()
				};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskClasses()
	 */
	public Package getTaskClasses() {
		return GroupShowListTask.class.getPackage();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskSet()
	 */
	public ITaskSet getTaskSet() {
		return UseCaseHelper.EMPTY_TASK_SET;
	}
	
// ---

	public IPermissionRecord[] getPermissionRecords() {
		IPermissionRecord[] outRecords = new IPermissionRecord[1];
		outRecords[0] = new PermissionRecord(Constants.PERMISSION_EDIT_BIBLIOGRAPHY, "Forum: Edit bibliography entry.",  //$NON-NLS-1$
												new int[] {RolesConstants.ADMINISTRATOR, RolesConstants.GROUP_ADMINISTRATOR, RolesConstants.PARTICIPANT});
		return outRecords;
	}
	
}
