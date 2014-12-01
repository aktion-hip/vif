/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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

package org.hip.vif.admin.admin.tasks;

import java.util.Collection;
import java.util.Collections;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.ui.UpgradeView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.interfaces.IProgressStepper;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.hip.vif.core.service.UpgradeRegistry;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

/** Task to manage the application's upgrade.
 *
 * @author Luthiger Created: 16.02.2012 */
@UseCaseController
public class UpgradeTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory
            .getLogger(UpgradeTask.class);

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_UPGRADE;
    }

    @Override
    protected Component runChecked() {
        emptyContextMenu();
        return new UpgradeView(BOMHelper.getAppVersionHome().getVersion(),
                UpgradeRegistry.INSTANCE.getSoftwareVersion(),
                new UpgradeThread());
    }

    // --- thread to process the upgrade ---

    public class UpgradeThread extends Thread {
        private String fromVersion;
        private String toVersion;
        private ProgressBar progress;
        private Collection<IVIFUpgrade> failurs;

        /** Method to initialize the upgrad process and start the thread.
         *
         * @param inFromVersion String
         * @param inToVersion String
         * @param inProgress {@link ProgressBar}
         * @return Collection&lt;IVIFUpgrade> the collection of failed upgrades */
        public Collection<IVIFUpgrade> upgrade(final String inFromVersion,
                final String inToVersion, final ProgressBar inProgress) {
            fromVersion = inFromVersion;
            toVersion = inToVersion;
            progress = inProgress;
            failurs = Collections.emptyList();

            start();

            try {
                join();
            } catch (final InterruptedException exc) {
                LOG.error("Interrupted the upgrade thread.", exc); //$NON-NLS-1$
            }

            UI.getCurrent().setPollInterval(-1);
            if (failurs.isEmpty()) {
                showNotification(Activator.getMessages().getMessage("admin.upgrade.feedback.success")); //$NON-NLS-1$
                sendEvent(UpgradeTask.class);
                return Collections.emptyList();
            }
            return failurs;
        }

        @Override
        public void run() {
            final IProgressStepper lStepper = new IProgressStepper() {
                @Override
                public void nextStep(final int inActStep, final int inMaxSteps) {
                    final float lDelta = 1f / inMaxSteps;

                    UI.getCurrent().access(new Runnable() {
                        @Override
                        public void run() {
                            progress.setValue(new Float(inActStep * lDelta));
                        }
                    });
                }
            };

            failurs = UpgradeRegistry.INSTANCE.upgrade(fromVersion, toVersion,
                    lStepper);
        }
    }

}
