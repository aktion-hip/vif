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

package org.hip.vif.web.util;

import java.util.Collections;
import java.util.List;

import org.hip.vif.web.menu.IVIFMenuExtendible;
import org.hip.vif.web.menu.IVIFMenuItem;

/**
 * Base class for the implementations of <code>IVIFMenuExtendible</code> provided by bundles.
 * 
 * @author Luthiger
 * Created: 28.10.2011
 */
public abstract class AbstractExtendibleMenu implements IVIFMenuExtendible {

	/**
	 * This implementation returns an empty list (i.e. <code>Collections.emptyList()</code>).
	 * The sub menu is made by menu contributions in an extendible way.
	 * 
	 * @return List<IVIFMenuItem>
	 */
	@Override
	public List<IVIFMenuItem> getSubMenu() {
		return Collections.emptyList();
	}
	
	/**
	 * This implementation returns <code>null</code>.
	 * 
	 * @return String
	 */
	@Override
	public String getTaskName() {
		return null;
	}
	
	@Override
	public String getPermission() {
		return ""; //$NON-NLS-1$
	}

}
