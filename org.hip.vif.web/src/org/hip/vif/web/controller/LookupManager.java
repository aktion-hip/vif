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

package org.hip.vif.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.hip.vif.web.interfaces.ILookupWindow;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class to manage the lookup windows and tasks.
 * 
 * @author Luthiger
 * Created: 09.06.2011
 */
public enum LookupManager {
	INSTANCE;
	
	private final static Logger LOG = LoggerFactory.getLogger(LookupManager.class); 

	private Map<LookupType, ILookupWindow> lookupMap = new HashMap<LinkButtonHelper.LookupType, ILookupWindow>();
	
	/**
	 * The service's binding method.
	 * 
	 * @param inLookup {@link ILookupWindow}
	 */
	public void setLookup(ILookupWindow inLookup) {
		if (lookupMap.get(inLookup.getType()) == null) {
			lookupMap.put(inLookup.getType(), inLookup);
		}
		LOG.debug("Setting content lookup of type {}.", inLookup.getType()); //$NON-NLS-1$
	}

	/**
	 * The service's unbinding method.
	 * 
	 * @param inLookup {@link ILookupWindow}
	 */
	public void unsetLookup(ILookupWindow inLookup) {
		lookupMap.remove(inLookup.getType());
		LOG.debug("Removing content lookup of type {}.", inLookup.getType()); //$NON-NLS-1$
	}
	
	/**
	 * Returns the lookup window of the specified type.
	 * 
	 * @param inType {@link LookupType}
	 * @return {@link ILookupWindow}
	 */
	public ILookupWindow getLookup(LookupType inType) {
		return lookupMap.get(inType);
	}

}
