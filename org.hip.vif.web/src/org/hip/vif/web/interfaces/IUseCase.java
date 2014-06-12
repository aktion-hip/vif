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

import org.hip.vif.web.menu.IVIFMenuItem;

/**
 * Interface for VIF use cases
 * 
 * @author Luthiger
 * Created: 15.05.2011
 */
public interface IUseCase {

	/**
	 * @return {@link IVIFMenuItem} the contribution to the menu.
	 */
	public IVIFMenuItem getMenu();
	
	/**
	 * <p>This method tells the application which package within the bundle contributes this bundle's 
	 * task classes to the application.</p>
	 * <p>For that the task classes within the specified package can be looked up and registered,
	 * they have to implement the <code>IPluggableTask</code> interface. 
	 * In addition, they have to be annotated by <code>@Partlet</code>.</p> 
	 * <p>This method can be used in combination with <code>IUseCase.getTaskSet()</code>.</p>
	 * 
	 * @return {@link Package} the package within the bundle that provides the task classes.
	 */
	Package getTaskClasses();
	
	/**
	 * <p>This method contributes this bundle's set of tasks to the application.</p>
	 * <p>This method can be used in combination with <code>IUseCase.getTaskClasses()</code>.</p>
	 * <p>Note: The task classes provided by this method are 'hard wired', therefore, they don't have to be
	 * annotated by <code>@Partlet</code>.</p> 
	 * 
	 * @return {@link ITaskSet} the set of tasks defined in the bundle.
	 */
	ITaskSet getTaskSet();
	
	/**
	 * @return the set of context menu items defined in the bundle.
	 */
	IMenuSet[] getContextMenus();
	
}
