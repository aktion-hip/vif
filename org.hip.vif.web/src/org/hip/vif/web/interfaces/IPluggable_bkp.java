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
package org.hip.vif.web.interfaces;

import org.ripla.interfaces.IMessages;

/**
 * Base interface for classes extending a VIF extension point for pluggables.
 * 
 * @author Luthiger Created: 16.07.2009
 */
public interface IPluggable_bkp {

	/**
	 * @return boolean <code>true</code> if this bundle contributes to the
	 *         administration part of the forum.
	 */
	public boolean isAdmin();

	/**
	 * Returns the namespace identifier of the bundle implementing the extension
	 * point.
	 * 
	 * @return String
	 */
	public String getNamespaceID();

	/**
	 * Returns the IDs of the pluggable extensions the bundle provides.
	 * 
	 * @return String[] array of extension IDs.
	 */
	public String[] getPluggableExtensionIDs();

	/**
	 * @return IMessages access to the localized messages provided by this
	 *         bundle.
	 */
	public IMessages getMessages();

}
