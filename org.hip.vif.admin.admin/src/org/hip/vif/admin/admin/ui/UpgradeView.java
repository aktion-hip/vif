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

package org.hip.vif.admin.admin.ui;

import java.util.Collection;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.tasks.UpgradeTask;
import org.hip.vif.admin.admin.tasks.UpgradeTask.UpgradeThread;
import org.hip.vif.core.interfaces.IVIFUpgrade;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.LabelValueTable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

/**
 * View to start the registered upgrade tasks.
 * 
 * @author Luthiger Created: 16.02.2012
 */
@SuppressWarnings("serial")
public class UpgradeView extends AbstractAdminView {
	private static final String TMPL_FEEDBACK = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$
	private static final int POLL_RUN = 500;
	private static final int POLL_SLEEP = 5000;

	/**
	 * UpgradeView constructor.
	 * 
	 * @param inVersionInstance
	 *            String the instance's version (i.e. the version of the tables)
	 * @param inVersionSoftware
	 *            String the version according to the installed version
	 * @param inThread
	 *            {@link UpgradeTask} the thread managing the upgrade
	 */
	public UpgradeView(final String inVersionInstance,
			final String inVersionSoftware, final UpgradeThread inThread) {
		final IMessages lMessages = Activator.getMessages();

		final VerticalLayout lLayout = initLayout(lMessages,
				"admin.menu.upgrade"); //$NON-NLS-1$

		final Label lFeedbackMsg = new Label(
				String.format(TMPL_FEEDBACK,
						lMessages.getMessage("admin.upgrade.feedback.failure")), ContentMode.HTML); //$NON-NLS-1$
		lFeedbackMsg.setVisible(false);
		lLayout.addComponent(lFeedbackMsg);
		final Label lFailures = new Label("", ContentMode.HTML); //$NON-NLS-1$
		lLayout.addComponent(lFailures);
		lFailures.setVisible(false);

		final LabelValueTable lTable = new LabelValueTable();
		lTable.addRow(
				lMessages.getMessage("admin.upgrade.version.instance"), inVersionInstance); //$NON-NLS-1$
		lTable.addRow(
				lMessages.getMessage("admin.upgrade.version.app"), inVersionSoftware); //$NON-NLS-1$
		lLayout.addComponent(lTable);
		lLayout.addComponent(RiplaViewHelper.createSpacer());

		final Button lUpgrade = new Button(
				lMessages.getMessage("admin.menu.upgrade")); //$NON-NLS-1$
		lLayout.addComponent(lUpgrade);

		lLayout.addComponent(RiplaViewHelper.createSpacer());
		final ProgressBar lProgress = new ProgressBar(new Float(0.0));
		lProgress.setWidth(200, Unit.PIXELS);
		// lProgress.setPollingInterval(POLL_RUN);
		lProgress.setVisible(false);
		lLayout.addComponent(lProgress);

		lUpgrade.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				lProgress.setVisible(true);
				final Collection<IVIFUpgrade> lFailed = inThread.upgrade(
						inVersionInstance, inVersionSoftware, lProgress);
				lProgress.setVisible(false);
				// lProgress.setPollingInterval(POLL_SLEEP);
				if (lFailed.isEmpty()) {
					lFeedbackMsg.setVisible(false);
					lFailures.setVisible(false);
				} else {
					lFeedbackMsg.setVisible(true);
					lFailures.setPropertyDataSource(new ObjectProperty<String>(
							renderFailures(lFailed), String.class));
					lFailures.setVisible(true);
				}
			}
		});
	}

	protected String renderFailures(final Collection<IVIFUpgrade> inFailed) {
		final StringBuilder out = new StringBuilder("<ul>"); //$NON-NLS-1$
		for (final IVIFUpgrade lUpgrade : inFailed) {
			out.append("<li>").append(lUpgrade.getDescription()).append("</li>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		out.append("</ul>"); //$NON-NLS-1$
		return new String(out);
	}
}
