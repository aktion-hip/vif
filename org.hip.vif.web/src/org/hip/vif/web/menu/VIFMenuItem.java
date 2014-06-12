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
package org.hip.vif.web.menu;

import java.util.Collections;
import java.util.List;

/**
 * A menu item implementing the <code>IVIFMenuItem</code> interface.<br />
 * This item implementation doesn't contain sub menus.
 *
 * @author Luthiger
 * @see IVIFMenuItem
 */
public class VIFMenuItem implements IVIFMenuItem {
	private String label;
	private int position;
	private String taskName;
	private String permission;

	/**
	 * Constructor
	 * 
	 * @param inLabel String the label displayed on the menu
	 */
	public VIFMenuItem(String inLabel) {
		this(inLabel, 0);
	}
	
	/**
	 * Constructor
	 * 
	 * @param inLabel String the label displayed on the menu
	 * @param inPosition int the menu's position
	 */
	public VIFMenuItem(String inLabel, int inPosition) {
		label = inLabel;
		position = inPosition;
	}
	
	/**
	 * @return String caption in Vaadin menu
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return int position in Vaadin menu bar.
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets the fully qualified name of the task to be executed when the menu item is clicked.<br />
	 * Use <pre>UseCaseHelper.createFullyQualifiedTaskName(MyTask.class)</pre> for a consistent naming.
	 * 
	 * @param inTaskName
	 */
	public void setTaskName(String inTaskName) {
		taskName = inTaskName;
	}
	
	@Override
	public String getTaskName() {
		return taskName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.menu.IVIFMenuItem#getMenuCommand()
	 */
	public IMenuCommand getMenuCommand() {
		return new IMenuCommand() {
			@Override
			public String getTaskName() {
				return taskName;
			}
		};
	}
	
	/**
	 * Sets the permission the user needs for that the menu item becomes visible (and selectable).
	 * 
	 * @param inPermission String
	 */
	public void setPermission(String inPermission) {
		permission = inPermission;
	}
	
	@Override
	public String getPermission() {
		return permission == null ? "" : permission; //$NON-NLS-1$
	}

	@Override
	public List<IVIFMenuItem> getSubMenu() {
		return Collections.emptyList();
	}

}
