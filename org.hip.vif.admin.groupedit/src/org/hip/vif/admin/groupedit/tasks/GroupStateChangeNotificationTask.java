/**
    This package is part of the application VIF.
    Copyright (C) 2011-2014, Benno Luthiger

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
import org.hip.vif.core.bom.Group;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.mail.GroupStateChangeNotification;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Controller for the view to ask the administrator whether to notify the participants about the group's state change or
 * not.
 *
 * @author Luthiger Created: 13.11.2011 */
@UseCaseController
public class GroupStateChangeNotificationTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(GroupStateChangeNotificationTask.class);

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_GROUPS_EDIT;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        emptyContextMenu();
        final ParameterObject lParameters = getParameters();
        return new GroupStateChangeNotificationView(lParameters.get(Constants.KEY_PARAMETER_GROUP_NAME).toString(),
                lParameters.get(Constants.KEY_PARAMETER_SUBJECT).toString(),
                lParameters.get(Constants.KEY_PARAMETER_BODY).toString(),
                this);
    }

    /** Callback method, sends notification mail.
     *
     * @param inSubject String
     * @param inBody String
     * @return boolean <code>true</code> if successful */
    public boolean doNotification(final String inSubject, final String inBody) {
        try {
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(getGroupID());
            try {
                final GroupStateChangeNotification lMail = new GroupStateChangeNotification(
                        lGroup.getParticipantsMail(), true);
                lMail.setSubject(inSubject);
                lMail.setBody(inBody);
                lMail.send();
                showNotification(
                        Activator.getMessages().getMessage("admin.group.state.notification.sent"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            } catch (final MailGenerationException exc) {
                LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
            }
            sendEvent(GroupShowListTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
        } catch (final AddressException exc) {
            LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while preparing the state change notification!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Callback method, continues without sending notification mail. */
    public void doContinue() {
        sendEvent(GroupShowListTask.class);
    }

}
