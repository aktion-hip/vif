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

package org.hip.vif.web.internal.menu;

import java.util.Collection;
import java.util.Vector;

import org.hip.vif.web.menu.IExtendibleMenuContribution;
import org.hip.vif.web.menu.IVIFMenuExtendible;

/**
 * Helper class for handling extendible menus (i.e. <code>IVIFMenuExtendible</code>) and their contributions.<br />
 * This class contains all contributions for the same menu, identified by <code>IExtendibleMenuContribution.getExtendibleMenuID()</code>.
 * 
 * @author Luthiger
 * Created: 29.10.2011
 * @see IVIFMenuExtendible
 */
public class ExtendibleMenuHandler {
	private Collection<IExtendibleMenuContribution> contributions = new Vector<IExtendibleMenuContribution>();
	
	/**
	 * Constructor 
	 * 
	 * @param inContribution {@link IExtendibleMenuContribution}
	 */
	public ExtendibleMenuHandler(IExtendibleMenuContribution inContribution) {
		contributions.add(inContribution);
	}

	/**
	 * Returns the factory to create the extendible menu.
	 * 
	 * @return {@link MenuFactory}
	 */
	public MenuFactory getMenuFactory(IVIFMenuExtendible inExtendibleMenu) {
		return new ExtendibleMenuFactory(inExtendibleMenu, contributions);
	}

	/**
	 * @param inContribution {@link IExtendibleMenuContribution} adds the menu contribution
	 */
	public void addContribution(IExtendibleMenuContribution inContribution) {
		contributions.add(inContribution);		
	}

	/**
	 * @param inContribution {@link IExtendibleMenuContribution} removes the menu contribution
	 */
	public void removeContribution(IExtendibleMenuContribution inContribution) {
		contributions.remove(inContribution);
	}

}
