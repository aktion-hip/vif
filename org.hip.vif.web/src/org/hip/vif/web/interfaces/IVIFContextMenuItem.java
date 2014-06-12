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

package org.hip.vif.web.interfaces;

import org.hip.vif.core.interfaces.IPluggableTask;

/**
 * Interface for an item in the VIF context menu.
 * 
 * @author Luthiger
 * Created: 26.06.2011
 */
public interface IVIFContextMenuItem {
	
	/**
	 * @return Class&lt;? extends IPluggableTask&gt; the task that has to be called when the user clicks the context menu item.
	 */
	Class<? extends IPluggableTask> getTaskClass();
	
	/**
	 * @return String the message id for the localized message to display in the context menu.
	 */
	String getTitleMsg();
	
	/**
	 * @return String The permission a user needs to have for that the task is displayed in the context menu. Empty string of no permission needed.
	 */
	String getMenuPermission();
	
	/**
	 * @return boolean <code>true</code> if the user needs to be group administrator for that the item is displayed in the context menu.
	 */
	boolean needsGroupAmin();
	
	/**
	 * @return boolean <code>true</code> if the user needs to be registered to the discussion group for that the item is displayed in the context menu.
	 */
	boolean needsRegistration();
	
	/**
	 * @return boolean <code>true</code> if the group needs to be of type private for that the item is displayed in the context menu.
	 */
	boolean needsTypePrivate();
	
	/**
	 * @return String[] An array of group states the display of the task in the menu depends on. Empty array if the context menu item should be displayed independent of the group's state.
	 */
	String[] getGroupStates();

}
