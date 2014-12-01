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

package org.hip.vif.forum.register.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.forum.register.Activator;
import org.hip.vif.forum.register.Constants;
import org.hip.vif.forum.register.data.GroupContainer;
import org.hip.vif.forum.register.data.GroupWrapper;
import org.hip.vif.forum.register.ui.GroupListRegisterView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/** This task displays the list of available discussion groups for that a member can register to the groups she wants to
 * contribute.
 *
 * @author Luthiger Created: 30.09.2011 */
@UseCaseController
public class RegisterShowListTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterShowListTask.class);

    private static final String DFT_SORT = GroupHome.KEY_NAME + ", " + NestedGroupHome.KEY_GROUP_ID; //$NON-NLS-1$

    private GroupContainer groups;
    private CodeList codeList;
    private KeyObjectImpl key;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_REGISTER;
    }

    @Override
    protected Component runChecked() throws VIFWebException {
        emptyContextMenu();

        final NestedGroupHome lGroupHome = BOMHelper.getNestedGroupHome();

        try {
            final KeyObject lKeySubscribeable = createKey(VIFGroupWorkflow.ENLISTABLE_STATES);
            key = new KeyObjectImpl();
            key.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PUBLIC);
            key.setValue(lKeySubscribeable, BinaryBooleanOperator.AND);

            final Collection<Long> lRegisterings = VifBOMHelper.getParticipantHome().getRegisterings(
                    getActor().getActorID());
            codeList = CodeListHome.instance().getCodeList(GroupState.class, getAppLocale().getLanguage());

            groups = GroupContainer.createData(lGroupHome.select(key, createOrder(DFT_SORT, false)), lRegisterings,
                    codeList);
            return new GroupListRegisterView(groups, this);
        } catch (final SQLException | VException exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Callback function: processes the registerings.
     *
     * @return {@link Feedback} */
    public Feedback saveRegisterings() {
        final Collection<Long> lRegisterings = new Vector<Long>();
        for (final GroupWrapper lGroup : groups.getItemIds()) {
            if (lGroup.isChecked()) {
                lRegisterings.add(lGroup.getGroupID());
            }
        }

        final IMessages lMessages = Activator.getMessages();
        try {
            final Long lActorID = getActor().getActorID();
            final ParticipantHome lHome = VifBOMHelper.getParticipantHome();
            final boolean isParticipantStart = lHome.isParticipant(lActorID);
            final Collection<Long> lForbidden = lHome.saveRegisterings(lActorID, new Vector<Long>(lRegisterings));
            final boolean isParticipantEnd = lHome.isParticipant(lActorID);

            // actualize participant role
            if (isParticipantEnd && !isParticipantStart) {
                BOMHelper.getLinkMemberRoleHome().createParticipantRole(lActorID);
                getActor().refreshAuthorization();
            }
            if (isParticipantStart && !isParticipantEnd) {
                BOMHelper.getLinkMemberRoleHome().deleteParticipantRole(lActorID);
                getActor().refreshAuthorization();
            }
            lRegisterings.addAll(lForbidden);
            groups = GroupContainer.createData(
                    BOMHelper.getNestedGroupHome().select(key, createOrder(DFT_SORT, false)), lRegisterings, codeList);

            if (lForbidden.size() != 0) {
                return new Feedback(
                        lMessages.getMessage("ui.register.feedback.hint"), Notification.Type.WARNING_MESSAGE, groups); //$NON-NLS-1$
            }
            return new Feedback(
                    lMessages.getMessage("ui.register.feedback.saved"), Notification.Type.TRAY_NOTIFICATION, groups); //$NON-NLS-1$
        } catch (final BOMChangeValueException exc) {
            LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
            return new Feedback(lMessages.getMessage("errmsg.general"), Notification.Type.ERROR_MESSAGE, groups); //$NON-NLS-1$
        } catch (final VException exc) {
            LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
            return new Feedback(lMessages.getMessage("errmsg.general"), Notification.Type.ERROR_MESSAGE, groups); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
            return new Feedback(lMessages.getMessage("errmsg.general"), Notification.Type.ERROR_MESSAGE, groups); //$NON-NLS-1$
        }
    }

    // ---

    /** Feedback object. */
    public static class Feedback {
        public String message;
        public Notification.Type notificationType;
        public GroupContainer groups;

        Feedback(final String inMessage, final Notification.Type inNotificationType, final GroupContainer inGroups) {
            message = inMessage;
            notificationType = inNotificationType;
            groups = inGroups;
        }
    }

}
