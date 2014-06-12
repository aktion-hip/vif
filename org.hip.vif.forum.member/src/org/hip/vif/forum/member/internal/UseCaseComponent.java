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

package org.hip.vif.forum.member.internal;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.Constants;
import org.hip.vif.forum.member.tasks.PersonalDataEditTask;
import org.hip.vif.forum.member.tasks.PwrdEditTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.menu.VIFMenuComposite;
import org.hip.vif.web.util.ContextMenuItem;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * This bundle's service provider for <code>IUseCaseForum</code>.
 * 
 * @author Luthiger
 * Created: 06.10.2011
 */
public class UseCaseComponent implements IUseCaseForum {
	private static final IMessages MESSAGES = Activator.getMessages();
	private static final IVIFContextMenuItem EDIT_PWRD = new ContextMenuItem(PwrdEditTask.class, 
																		"context.menu.member.pwrd",  //$NON-NLS-1$
																		Constants.PERMISSION_EDIT_DATA,
																		false, false, false, 
																		new String[] {}, MESSAGES);
	private static final IVIFContextMenuItem EDIT_DATA = new ContextMenuItem(PersonalDataEditTask.class, 
			"component.menu.title",  //$NON-NLS-1$
			Constants.PERMISSION_EDIT_DATA,
			false, false, false, 
			new String[] {}, MESSAGES);
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getMenu()
	 */
	public IVIFMenuItem getMenu() {
		VIFMenuComposite outMenu = new VIFMenuComposite(Activator.getMessages().getMessage("component.menu.title"), 50); //$NON-NLS-1$
		outMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(PersonalDataEditTask.class));
		outMenu.setPermission(Constants.PERMISSION_EDIT_DATA);
		
		VIFMenuComposite lSubMenu = new VIFMenuComposite(Activator.getMessages().getMessage("context.menu.member.pwrd"), 10); //$NON-NLS-1$
		lSubMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(PwrdEditTask.class));
		lSubMenu.setPermission(Constants.PERMISSION_EDIT_DATA);
		
		outMenu.add(lSubMenu);
		return outMenu;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IUseCase#getTaskClasses()
	 */
	public Package getTaskClasses() {
		return PersonalDataEditTask.class.getPackage();
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
		return new IMenuSet[] {new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_EDIT_DATA;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {EDIT_DATA};
			}
		},
		new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_EDIT_PWRD;
			}
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {EDIT_PWRD};
			}
		}};
	}

}
