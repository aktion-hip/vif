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

import java.util.List;

import org.hip.vif.web.util.UseCaseHelper;

/**
 * Interface for elements in the VIF menu.<br />
 * This interface defines a composite, i.e. the menu element can contain sub menu elements of the same structure.
 * 
 * @author Luthiger
 * Created: 30.10.2011
 */
public interface IVIFMenuElement {
	
	/**
	 * @return String caption in Vaadin menu
	 */
	String getLabel();
	
	/**
	 * Returns the fully qualified name of the task to be executed when the menu item is clicked.<br />
	 * Use <pre>UseCaseHelper.createFullyQualifiedTaskName(MyTask.class)</pre> for a consistent naming.
	 * 
	 * @return String the task's fully qualified name, i.e. <code>org.hip.vif.mybundle/mytask</code>, may be <code>null</code>.
	 * @see UseCaseHelper#createFullyQualifiedControllerName(Class)
	 */
	String getTaskName();

	/**
	 * @return List<IEthMenuItem> the menu item's sub menu. May be <code>Collections.emptyList()</code>.
	 */
	public List<IVIFMenuItem> getSubMenu();
	
	/**
	 * Returns the permission the user (i.e. role) needs to make the contribution visible and selectable.
	 * 
	 * @return String the menu permission or an empty string for no permission needed
	 */
	String getPermission();

}
