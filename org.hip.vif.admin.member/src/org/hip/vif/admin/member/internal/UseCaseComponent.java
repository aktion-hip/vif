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

import org.hip.vif.admin.member.tasks.MemberSearchTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseAdmin;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service instance for <code>IUseCaseAdmin</code>.
 * 
 * @author Luthiger
 * Created: 16.10.2011
 */
public class UseCaseComponent implements IUseCaseAdmin {

	public IVIFMenuItem getMenu() {
		return ComponentHelper.createMenu();
	}
	
	public IMenuSet[] getContextMenus() {
		return new IMenuSet[] {HelperContextMenuAdminMembers.createContextMenuSet1(),
				HelperContextMenuAdminMembers.createContextMenuSet2()};
	}

	public Package getTaskClasses() {
		return MemberSearchTask.class.getPackage();
	}

	public ITaskSet getTaskSet() {
		return UseCaseHelper.EMPTY_TASK_SET;
	}

}
