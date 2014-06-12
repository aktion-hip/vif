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

import java.io.IOException;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.data.ConfigurationHelper;
import org.hip.vif.admin.admin.ui.ConfigView;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.DBConnectionProber;
import org.hip.vif.web.components.BlankPopup;
import org.hip.vif.web.tasks.AbstractWebController;
import org.hip.vif.web.tasks.DBAccessWorkflow;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.WorkflowException;
import org.hip.vif.web.util.ConfigurationItem;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.ripla.interfaces.IWorkflowListener;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * The task for the application configuration.<br />
 * Displays the content of <code>vif.properties</code>.
 * 
 * @author Luthiger Created: 06.01.2012
 */
@UseCaseController
public class ConfigTask extends AbstractWebController implements
		IWorkflowListener {

	private ConfigurationHelper configHelper;

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_CONFIGURATION;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			emptyContextMenu();
			return new ConfigView(ConfigurationItem.createConfiguration(), this);
		}
		catch (final IOException exc) {
			throw createContactAdminException(exc);
		}
	}

	/**
	 * Callback method to save the changed configuration settings.
	 * 
	 * @param inConfiguration
	 *            {@link ConfigurationItem}
	 * @return boolean <code>true</code> if the view should display a success
	 *         message
	 */
	public boolean save(final ConfigurationItem inConfiguration) {
		configHelper = new ConfigurationHelper(inConfiguration);
		final boolean outShowSuccess = inConfiguration.saveChanges();

		switch (configHelper.getConfigurationTask()) {
		case DB:
			handleDBAccess(configHelper);
			return false;
		case INDEX:
			showNotification(
					Activator.getMessages().getMessage(
							"admin.config.language.feedback"), Notification.Type.HUMANIZED_MESSAGE); //$NON-NLS-1$
			sendEvent(RefreshIndexTask.class);
			return false;
		case NONE:
		default:
		}
		return outShowSuccess;
	}

	private void handleDBAccess(final ConfigurationHelper inHelper) {
		// finish the new DB access settings
		if (inHelper.isEmbedded()) {
			PreferencesHandler.INSTANCE.setEmbeddedDB();
		}
		DataSourceRegistry.INSTANCE
				.setActiveConfiguration(PreferencesHandler.INSTANCE
						.getDBConfiguration());

		// evaluate the new DB access settings
		final DBConnectionProber lProber = new DBConnectionProber();
		if (lProber.needsDBConfiguration()) {
			handleFailureRestore();
			return;
		}
		if (lProber.needsTableCreation()) {
			try {
				DBAccessWorkflow.getCreateTablesWorkflow(
						ApplicationData.getWindow(), this).startWorkflow();
			}
			catch (final WorkflowException exc) {
				handleFailureRestore();
			}
			return;
		}
		try {
			if (lProber.needsSUCreation()) {
				try {
					DBAccessWorkflow.getCreateSUWorkflow(
							ApplicationData.getWindow(), this).startWorkflow();
				}
				catch (final WorkflowException exc) {
					handleFailureRestore();
				}
				return;
			}
		}
		catch (final Exception exc) {
			handleFailureRestore();
		}
		// in case of a simple switch of a fully configured DB, we only need to
		// do a new login
		displayDBConfigFeedback();
	}

	private void handleFailureRestore() {
		configHelper.restoreDBAccessSettings();
		showNotification(
				Activator.getMessages().getMessage("errmsg.config.db.failed"), Type.WARNING_MESSAGE); //$NON-NLS-1$
		sendEvent(ConfigTask.class);
	}

	@Override
	public void workflowExit(final int inReturnCode, final String inMessage) {
		// error case
		if (inReturnCode == DBAccessWorkflow.ERROR) {
			handleFailureRestore();
			return;
		}
		// normal case
		displayDBConfigFeedback();
	}

	@SuppressWarnings("serial")
	private void displayDBConfigFeedback() {
		final IMessages lMessages = Activator.getMessages();
		final BlankPopup lFeedback = new BlankPopup(
				lMessages.getMessage("admin.config.sub.database"), 320, 180); //$NON-NLS-1$
		lFeedback.setClosable(false);
		lFeedback
				.addComponent(new Label(
						lMessages.getMessage("admin.config.success.db.index"), ContentMode.HTML)); //$NON-NLS-1$
		final Button lOk = new Button(
				lMessages.getMessage("admin.config.button.feedback")); //$NON-NLS-1$
		lOk.setClickShortcut(KeyCode.ENTER);
		lOk.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				logout();
			}
		});
		lFeedback.addComponent(lOk);

		lFeedback.show();
	}

}
