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

package org.hip.vif.core.interfaces;

import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.osgi.service.event.EventAdmin;

/**
 * Interface for tasks that can send events using the OSGi <code>EventAdmin</code>.
 * 
 * @author Luthiger
 * Created: 24.12.2011
 */
public interface IEventableTask {

	/**
	 * Sets the OSGi event admin to enable event handling.
	 * 
	 * @param inEventAdmin {@link EventAdmin}
	 */
	void setEventAdmin(EventAdmin inEventAdmin);

	/**
	 * Request the lookup task.
	 * 
	 * @param inType {@link LookupType} the type of lookup
	 * @param inID Long the ID of the item to display in the lookup
	 */
	void requestLookup(LinkButtonHelper.LookupType inType, Long inID);
	
	/**
	 * Request the lookup task for bibliography entries.
	 * 
	 * @param inType  {@link LookupType} the type of lookup
	 * @param inTextID String ID-Version
	 */
	void requestLookup(LinkButtonHelper.LookupType inType, String inTextID);

}
