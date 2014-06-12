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

package org.hip.vif.admin.groupedit.tasks;

import java.sql.SQLException;

import javax.mail.internet.AddressException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.ui.GroupStateChangeNotificationView;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.mail.GroupStateChangeNotification;
import org.hip.vif.core.util.ParameterObject;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Controller for the view to ask the administrator whether to notify the participants about the group's state change or not.
 * 
 * @author Luthiger
 * Created: 13.11.2011
 */
@Partlet
public class GroupStateChangeNotificationTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(GroupStateChangeNotificationTask.class);
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_GROUPS_EDIT;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		emptyContextMenu();
		ParameterObject lParameters = getParameters();
		return new GroupStateChangeNotificationView(lParameters.get(Constants.KEY_PARAMETER_GROUP_NAME).toString(), 
				lParameters.get(Constants.KEY_PARAMETER_SUBJECT).toString(), 
				lParameters.get(Constants.KEY_PARAMETER_BODY).toString(),
				this);
	}

	/**
	 * Callback method, sends notification mail.
	 * 
	 * @param inSubject String
	 * @param inBody String
	 * @return boolean <code>true</code> if successful
	 */
	public boolean doNotification(String inSubject, String inBody) {
		try {
			Group lGroup = BOMHelper.getGroupHome().getGroup(getGroupID());
			try {
				GroupStateChangeNotification lMail = new GroupStateChangeNotification(lGroup.getParticipantsMail(), true);
				lMail.setSubject(inSubject);
				lMail.setBody(inBody);
				lMail.send();
				showNotification(Activator.getMessages().getMessage("admin.group.state.notification.sent"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			}
			catch (MailGenerationException exc) {
				LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
			}
			sendEvent(GroupShowListTask.class);
			return true;
		}
		catch (VException exc) {
			LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
		}
		catch (AddressException exc) {
			LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
		}
		catch (SQLException exc) {
			LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
		}		
		return false;
	}

	/**
	 * Callback method, continues without sending notification mail.
	 */
	public void doContinue() {
		sendEvent(GroupShowListTask.class);
	}

}
