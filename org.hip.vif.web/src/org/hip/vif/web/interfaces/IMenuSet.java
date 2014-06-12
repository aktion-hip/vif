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

/**
 * Interface for a specific context menu, i.e. a context menu identified by a set ID and made up of a set of menu items.
 * 
 * @author Luthiger
 * Created: 26.06.2011
 */
public interface IMenuSet {
	
	/**
	 * @return String the menu set's ID. Must be unique within a bundle.
	 */
	String getSetID();

	/**
	 * @return {@link IVIFContextMenuItem}[] the set of context menu configurations that make up the specific context menu.
	 */
	IVIFContextMenuItem[] getContextMenuItems();
	
}
