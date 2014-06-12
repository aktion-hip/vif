/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.core.internal.service;

import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.interfaces.IVIFUpgradeService;
import org.hip.vif.core.service.UpgradeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client for the application's upgrade service <code>IVIFUpgradeService</code>.
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
public class UpgradeService {
	private static final Logger LOG = LoggerFactory.getLogger(UpgradeService.class);
	
	/**
	 * Binding the provided upgrade services.
	 * 
	 * @param inService {@link IVIFUpgradeService}
	 */
	public void registerUpgrader(IVIFUpgradeService inService) {
		UpgradeRegistry lRegistry = UpgradeRegistry.INSTANCE;
		for (IVIFUpgrade lUpgrade : inService.getUpgraders()) {
			lRegistry.registerUpgrader(lUpgrade);
		}
		LOG.debug("Registered upgrade service {}.", inService);
	}

	/**
	 * Unbinding the provided upgrade services.
	 * 
	 * @param inService {@link IVIFUpgradeService}
	 */
	public void unregisterUpgrader(IVIFUpgradeService inService) {
		UpgradeRegistry lRegistry = UpgradeRegistry.INSTANCE;
		for (IVIFUpgrade lUpgrade : inService.getUpgraders()) {
			lRegistry.unregisterUpgrader(lUpgrade);
		}
		LOG.debug("Unregistered upgrade service {}.", inService);
	}

}