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
 * Interface for VIF skins.
 * 
 * @author Luthiger
 * Created: 02.01.2012
 */
public interface ISkin {

	/**
	 * @return String this skin bundle's ID, i.e. symbolic name: <code>bundleContext.getBundle().getSymbolicName()</code>.
	 */
	String getSkinID();
	
	/**
	 * 
	 * @return String the name of the skin, displayed in the skin select view
	 */
	String getSkinName();

	/**
	 * @return String Welcome title for the application's forum part.
	 */
	String getWelcomeForum();
	
	/**
	 * @return String Welcome title for the application's admin part.
	 */
	String getWelcomeAdmin();
	
	/**
	 * Create the skin's header component.<br />
	 * Note: the application's layout has 80px height reserved (and full width) for the header.
	 * 
	 * @return {@link Component}
	 */
	Component getHeader();
	
	/**
	 * Create the skin's footer component.<br />
	 * 
	 * @return {@link Component}
	 */
	Component getFooter();

}
