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

package org.hip.vif.web.dash;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.web.controller.ControllerHelper;
import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.internal.menu.MenuFactory;
import org.hip.vif.web.layout.VIFApplication;
import org.hip.vif.web.menu.IMenuCommand;

import com.vaadin.ui.Component;

/**
 * @author Luthiger
 * Created: 15.05.2011
 */
@SuppressWarnings("serial")
public class AdminDashBoard extends VIFDashBoard {
	private Map<Integer, IMenuCommand> menuMap;

	public AdminDashBoard(VIFApplication inApplication) {
		super(inApplication);
	}

	@Override
	protected Collection<MenuFactory> getMenus() {
		return ControllerHelper.INSTANCE.getAdminMenus();
	}

	@Override
	protected Map<Integer, IMenuCommand> getMenuMap() {
		if (menuMap == null) {
			menuMap = new HashMap<Integer, IMenuCommand>();
		}
		return menuMap;
	}

	@Override
	public Component getContentComponent(String inTaskName) throws NoTaskFoundException {
		return TaskManager.INSTANCE.getAdminContent(inTaskName);
	}
	
}
