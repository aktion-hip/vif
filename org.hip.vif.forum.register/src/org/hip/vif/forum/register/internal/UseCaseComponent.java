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

package org.hip.vif.forum.register.internal;

import org.hip.vif.forum.register.Activator;
import org.hip.vif.forum.register.Constants;
import org.hip.vif.forum.register.tasks.RegisterShowListTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.menu.VIFMenuComposite;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service provider for <code>IUseCaseForum</code>.
 * 
 * @author Luthiger
 * Created: 30.09.2011
 */
public class UseCaseComponent implements IUseCaseForum {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getMenu()
	 */
	public IVIFMenuItem getMenu() {
		VIFMenuComposite outMenu = new VIFMenuComposite(Activator.getMessages().getMessage("component.menu.title"), 30); //$NON-NLS-1$
		outMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(RegisterShowListTask.class));
		outMenu.setPermission(Constants.PERMISSION_REGISTER);
		return outMenu;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskClasses()
	 */
	public Package getTaskClasses() {
		return RegisterShowListTask.class.getPackage();
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

}
