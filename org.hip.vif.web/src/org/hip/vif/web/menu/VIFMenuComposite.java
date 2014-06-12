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

import java.util.ArrayList;
import java.util.List;


/**
 * A menu item implementation containing sub menus.
 *
 * @author Luthiger
 */
public class VIFMenuComposite extends VIFMenuItem implements IVIFMenuItem {
	private List<IVIFMenuItem> subMenu = new ArrayList<IVIFMenuItem>();

	/**
	 * Constructor
	 * 
	 * @param inLabel String the label displayed on the menu
	 */
	public VIFMenuComposite(String inLabel) {
		super(inLabel);
	}

	/**
	 * Constructor
	 * 
	 * @param inLabel String the label displayed on the menu
	 * @param inPosition int the menu's position
	 */
	public VIFMenuComposite(String inLabel, int inPosition) {
		super(inLabel, inPosition);
	}

	/**
	 * Adding the sub menu.
	 * 
	 * @param inSubMenu {@link VIFMenuComposite}
	 */
	public void add(VIFMenuComposite inSubMenu) {
		subMenu.add(inSubMenu);
	}
	
	/**
	 * Remove a menu item.
	 * 
	 * @param inSubMenu {@link VIFMenuItem}
	 */
	public void remove(VIFMenuItem inSubMenu) {
		subMenu.remove(inSubMenu);
	}
	
	/**
	 * Returns the sub menu, i.e. a list of <code>IVIFMenuItem</code>s.
	 * 
	 * @return List of {@link IVIFMenuItem}
	 */
	public List<IVIFMenuItem> getSubMenu() {
		return subMenu;
	}

}
