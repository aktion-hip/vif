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

import com.vaadin.ui.Component;

/**
 * Interface for dash board components.
 * 
 * @author Luthiger
 * Created: 28.09.2011
 */
public interface IDashBoard {

	/**
	 * Sets the dash boards main component.
	 * 
	 * @param inComponent {@link Component}
	 */
	void setContentView(Component inComponent);
	
	/**
	 * Displays the application's default view.
	 */
	void showDefault();
}
