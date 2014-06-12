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

package org.hip.vif.core.interfaces;

import org.hip.vif.core.exc.UpgradeException;
import org.hip.vif.core.service.UpgradeRegistry.ProgressIndicator;

/**
 * Interface for upgraders, i.e. classes that can be executed to upgrade the application's database.
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
public interface IVIFUpgrade {
	
	/**
	 * Returns the application's version this upgrader registers to.<br />
	 * Use the version notation <code>Max.Minor</code> (e.g. <code>1.1</code>).
	 * 
	 * @return String the version to which this instance upgrades
	 */
	String version();
	
	/**
	 * Executes the upgrade.
	 * 
	 * @param inIndicator {@link ProgressIndicator} for progress indication
	 * @throws UpgradeException
	 */
	void execute(ProgressIndicator inIndicator) throws UpgradeException;

	/**
	 * @return String a description of the upgrade
	 */
	String getDescription();

	/**
	 * Returns the number of discrete steps the upgrade is divided into. 
	 * The step number can be used for the process indicator.
	 * 
	 * @return int the number of steps 
	 */
	int getNumberOfSteps();

}
