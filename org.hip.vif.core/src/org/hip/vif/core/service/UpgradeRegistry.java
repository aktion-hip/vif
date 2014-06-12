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

package org.hip.vif.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.exc.UpgradeException;
import org.hip.vif.core.interfaces.IProgressStepper;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.osgi.framework.Version;

/**
 * Registry for upgraders.
 * 
 * @author Luthiger
 * Created: 16.02.2012
 */
public enum UpgradeRegistry {
	INSTANCE;
	
	private Map<Version, Set<IVIFUpgrade>> registry = new HashMap<Version, Set<IVIFUpgrade>>();
	
	/**
	 * Registers the specified upgrader.
	 * 
	 * @param inUpgrader {@link IVIFUpgrade}
	 */
	public void registerUpgrader(IVIFUpgrade inUpgrader) {
		Version lVersion = Version.parseVersion(inUpgrader.version());
		Set<IVIFUpgrade> lUpgraders = registry.get(lVersion);
		if (lUpgraders == null) {
			lUpgraders = new HashSet<IVIFUpgrade>();
			registry.put(lVersion, lUpgraders);
		}
		lUpgraders.add(inUpgrader);
	}
	
	/**
	 * Unregisters the specified upgrader.
	 * 
	 * @param inUpgrader {@link IVIFUpgrade}
	 */
	public void unregisterUpgrader(IVIFUpgrade inUpgrader) {
		Set<IVIFUpgrade> lUpgraders = registry.get(Version.parseVersion(inUpgrader.version()));
		if (lUpgraders == null) {
			return;
		}
		lUpgraders.remove(inUpgrader);
	}
	
	/**
	 * Executes the upgrade.
	 * 
	 * @param inFromVersion String the version to start the upgrade
	 * @param inToVersion String the version to upgrade to
	 * @param inStepper {@link IProgressStepper} for progress indication 
	 * @return Collection&lt;IVIFUpgrade> the collection of failed upgrades
	 */
	public Collection<IVIFUpgrade> upgrade(String inFromVersion, String inToVersion, IProgressStepper inStepper) {
		Version lFrom = Version.parseVersion(inFromVersion);
		List<Version> lVersions = new ArrayList<Version>(registry.keySet());
		Collections.sort(lVersions);
		
		ProgressIndicator lIndicator = new ProgressIndicator(inStepper);
		Collection<IVIFUpgrade> lUpgradTasks = new ArrayList<IVIFUpgrade>();
		Collection<IVIFUpgrade> outFailures = new ArrayList<IVIFUpgrade>();		
		for (Version lVersion : lVersions) {
			if (lFrom.compareTo(lVersion) < 0) {
				Set<IVIFUpgrade> lUpgrades = registry.get(lVersion);
				for (IVIFUpgrade lUpgrade : lUpgrades) {
					lIndicator.addSteps(lUpgrade.getNumberOfSteps());
					lUpgradTasks.add(lUpgrade);
				}
			}
		}
		
		for (IVIFUpgrade lUpgrade : lUpgradTasks) {			
			try {
				lUpgrade.execute(lIndicator);
			}
			catch (UpgradeException exc) {
				outFailures.add(lUpgrade);
			}
		}
		if (outFailures.isEmpty()) {
			BOMHelper.getAppVersionHome().setVersion(inToVersion);
		}
		return outFailures;
	}
	
	/**
	 * @return String the application's version (i.e. the software version).
	 */
	public String getSoftwareVersion() {
		return ApplicationConstants.APP_VERSION;
	}

// ---
	
	/**
	 * Helper class for progress indication.
	 * 
	 * @author Luthiger
	 * Created: 17.02.2012
	 */
	public static class ProgressIndicator {
		private int actStep;
		private int maxSteps;
		private IProgressStepper stepper;

		private ProgressIndicator(IProgressStepper inStepper) {
			actStep = 0;
			maxSteps = 0;
			stepper = inStepper;
		}
		
		private void addSteps(int inSteps) {
			maxSteps += inSteps;
		}
		
		public void nextStep() {
			stepper.nextStep(++actStep, maxSteps);
		}
	}
	
	/**
	 * NoOp stepper class for upgrade processes without progress indication.
	 */
	private static class NOOpStepper implements IProgressStepper {
		public void nextStep(int inActStep, int inMaxSteps) {
			//intentionally left empty
		}
	}

	/**
	 * Returns a NoOp stepper, i.e. a stepper class for upgrade processes without progress indication.
	 * 
	 * @return {@link IProgressStepper}
	 */
	public static IProgressStepper getNOOpStepper() {
		return new NOOpStepper();
	}
	
	/**
	 * Returns a NoOp progress indicator for testing purposes.
	 * 
	 * @return {@link ProgressIndicator}
	 */
	public static ProgressIndicator getNOOpProgressIndicator() {
		return new ProgressIndicator(new NOOpStepper());
	}

}

