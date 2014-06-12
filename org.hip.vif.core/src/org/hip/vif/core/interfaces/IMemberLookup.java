/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.interfaces;

/**
 * Interface for member lookup service.
 * Partlets that provide member lookup must do this by registering a service that implements this interface. 
 *
 * @author Luthiger
 * Created: 01.03.2009
 */
public interface IMemberLookup {
	/**
	 * The task to show the search form. 
	 * 
	 * @return String Name of class. The referenced class must implement <code>org.hip.vif.registry.IPluggableTask</code>. 
	 */
	public String showSearchFormTask();
	
	/**
	 * The task to show the list of entries produced by the search query. 
	 * This selection list is displayed if the search query returns more the one entry and allows the user to select the appropriate entry.
	 * 
	 * @return String Name of class. The referenced class must implement <code>org.hip.vif.registry.IPluggableTask</code>. 
	 */
	public String showSelectionListTask();
	
	/**
	 * The task to show a member entry.
	 * 
	 * @return String Name of class. The referenced class must implement <code>org.hip.vif.registry.IPluggableTask</code>. 
	 */
	public String showMemberEntryTask();
	
	/**
	 * The task to display the member's ratings.
	 * 
	 * @return String Name of class. The referenced class must implement <code>org.hip.vif.registry.IPluggableTask</code>.
	 */
	public String showMemberRatingsTask();

}
