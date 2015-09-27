/**
	This package is part of the application VIF.
	Copyright (C) 2012-2015, Benno Luthiger

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

/** Registry for upgraders.
 *
 * @author Luthiger Created: 16.02.2012 */
public enum UpgradeRegistry {
    INSTANCE;

    private final Map<Version, Set<IVIFUpgrade>> registry = new HashMap<Version, Set<IVIFUpgrade>>();

    /** Registers the specified upgrader.
     * 
     * @param inUpgrader {@link IVIFUpgrade} */
    public void registerUpgrader(final IVIFUpgrade inUpgrader) {
        final Version lVersion = Version.parseVersion(inUpgrader.version());
        Set<IVIFUpgrade> lUpgraders = registry.get(lVersion);
        if (lUpgraders == null) {
            lUpgraders = new HashSet<IVIFUpgrade>();
            registry.put(lVersion, lUpgraders);
        }
        lUpgraders.add(inUpgrader);
    }

    /** Unregisters the specified upgrader.
     * 
     * @param inUpgrader {@link IVIFUpgrade} */
    public void unregisterUpgrader(final IVIFUpgrade inUpgrader) {
        final Set<IVIFUpgrade> lUpgraders = registry.get(Version.parseVersion(inUpgrader.version()));
        if (lUpgraders == null) {
            return;
        }
        lUpgraders.remove(inUpgrader);
    }

    /** Executes the upgrade.
     * 
     * @param inFromVersion String the version to start the upgrade
     * @param inToVersion String the version to upgrade to
     * @param inStepper {@link IProgressStepper} for progress indication
     * @return Collection&lt;IVIFUpgrade> the collection of failed upgrades */
    public Collection<IVIFUpgrade> upgrade(final String inFromVersion, final String inToVersion,
            final IProgressStepper inStepper) {
        final Version lFrom = Version.parseVersion(inFromVersion);
        final List<Version> lVersions = new ArrayList<Version>(registry.keySet());
        Collections.sort(lVersions);

        final ProgressIndicator lIndicator = new ProgressIndicator(inStepper);
        final Collection<IVIFUpgrade> lUpgradTasks = new ArrayList<IVIFUpgrade>();
        final Collection<IVIFUpgrade> outFailures = new ArrayList<IVIFUpgrade>();
        for (final Version lVersion : lVersions) {
            if (lFrom.compareTo(lVersion) < 0) {
                final Set<IVIFUpgrade> lUpgrades = registry.get(lVersion);
                for (final IVIFUpgrade lUpgrade : lUpgrades) {
                    lIndicator.addSteps(lUpgrade.getNumberOfSteps());
                    lUpgradTasks.add(lUpgrade);
                }
            }
        }

        for (final IVIFUpgrade lUpgrade : lUpgradTasks) {
            try {
                lUpgrade.execute(lIndicator);
            } catch (final UpgradeException exc) {
                outFailures.add(lUpgrade);
            }
        }
        if (outFailures.isEmpty()) {
            BOMHelper.getAppVersionHome().setVersion(inToVersion);
        }
        return outFailures;
    }

    /** @return String the application's version (i.e. the software version). */
    public String getSoftwareVersion() {
        return ApplicationConstants.APP_VERSION;
    }

    // ---

    /** Helper class for progress indication.
     * 
     * @author Luthiger Created: 17.02.2012 */
    public static class ProgressIndicator {
        private int actStep;
        private int maxSteps;
        private final IProgressStepper stepper;

        private ProgressIndicator(final IProgressStepper inStepper) {
            actStep = 0;
            maxSteps = 0;
            stepper = inStepper;
        }

        private void addSteps(final int inSteps) {
            maxSteps += inSteps;
        }

        public void nextStep() {
            stepper.nextStep(++actStep, maxSteps);
        }
    }

    /** NoOp stepper class for upgrade processes without progress indication. */
    private static class NOOpStepper implements IProgressStepper {
        @Override
        public void nextStep(final int inActStep, final int inMaxSteps) {
            // intentionally left empty
        }
    }

    /** Returns a NoOp stepper, i.e. a stepper class for upgrade processes without progress indication.
     * 
     * @return {@link IProgressStepper} */
    public static IProgressStepper getNOOpStepper() {
        return new NOOpStepper();
    }

    /** Returns a NoOp progress indicator for testing purposes.
     * 
     * @return {@link ProgressIndicator} */
    public static ProgressIndicator getNOOpProgressIndicator() {
        return new ProgressIndicator(new NOOpStepper());
    }

}
