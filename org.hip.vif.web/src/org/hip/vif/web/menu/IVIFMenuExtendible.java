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

/**
 * Interface for VIF menu variants that are extendible.<br />
 * Bundles can provide menu contributions to extendible menus.
 * 
 * @author Luthiger
 * Created: 28.10.2011
 */
public interface IVIFMenuExtendible extends IVIFMenuItem {
	
	/**
	 * @return String the extendible menu's ID. 
	 * Must be unique for that bundles that want to make contributions to this menu can look it up.
	 */
	String getMenuID();
		
	/**
	 * @return {@link ExtendibleMenuMarker}[] the array of markers this menu defines to which a contribution
	 * can be attached.
	 */
	ExtendibleMenuMarker[] getMarkers();

}
