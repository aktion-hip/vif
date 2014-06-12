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

import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.tasks.MemberNewTask;
import org.hip.vif.admin.member.tasks.MemberSearchTask;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.menu.VIFMenuComposite;
import org.hip.vif.web.util.UseCaseHelper;

/**
 * Worker class for this bundle's use case component. 
 * 
 * @author Luthiger
 * Created: 16.10.2011
 */
public class ComponentHelper {
	
	/**
	 * Creates this bundle's menu entry.
	 * 
	 * @return {@link IVIFMenuItem}
	 */
	static IVIFMenuItem createMenu() {
		VIFMenuComposite outMenu = new VIFMenuComposite(Activator.getMessages().getMessage("component.menu.title"), 10); //$NON-NLS-1$
		outMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(MemberSearchTask.class));
		outMenu.setPermission(Constants.PERMISSION_SEARCH);
		
		VIFMenuComposite lSubMenu = new VIFMenuComposite(Activator.getMessages().getMessage("context.menu.members.new"), 10); //$NON-NLS-1$
		lSubMenu.setTaskName(UseCaseHelper.createFullyQualifiedTaskName(MemberNewTask.class));
		outMenu.add(lSubMenu);
		return outMenu;
	}

}
