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

package org.hip.vif.admin.member.tasks;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.mail.CreateMemberMail;
import org.hip.vif.admin.member.ui.MemberEditView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.web.member.RoleContainer;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/** Task to create a new member entry.
 *
 * @author Luthiger Created: 22.10.2011 */
@UseCaseController
public class MemberNewTask extends AbstractMemberTask {
    private static final Logger LOG = LoggerFactory.getLogger(MemberNewTask.class);

    @Override
    protected Component runChecked() throws RiplaException {
        emptyContextMenu();

        final IMessages lMessages = Activator.getMessages();
        try {
            if (MemberUtility.INSTANCE.getActiveAuthenticator().isExternal()) {
                return new DefaultVIFView(lMessages.getMessage("errmsg.member.new")); //$NON-NLS-1$
            }
            return new MemberEditView((Member) BOMHelper.getMemberHome().create(),
                    RoleContainer.createData(getAppLocale().getLanguage(), BOMHelper.getRoleHome()
                            .getGroupSpecificIDs()),
                            this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    @Override
    protected Long getMemberID() {
        return 0l;
    }

    @Override
    public boolean saveMember(final Member inMember, final RoleContainer inRoles) throws ExternIDNotUniqueException {
        try {
            final String lPwrd = createPassword();
            inMember.set(MemberHome.KEY_PASSWORD, MemberUtility.INSTANCE.getActiveAuthenticator().encrypt(lPwrd));
            inMember.ucNew(inRoles.getSelectedIDs());

            final CreateMemberMail lMemberMail = new CreateMemberMail((VIFMember) inMember, inMember.get(
                    MemberHome.KEY_USER_ID).toString(), lPwrd);
            lMemberMail.send();

            refreshAndNotify(true, getNotificationMessage(inMember, lPwrd));
            return true;
        } catch (final ExternIDNotUniqueException exc) {
            throw exc;
        } catch (final MailGenerationException exc) {
            Notification.show(
                    Activator.getMessages().getMessage("errmsg.member.pwrd.no.mail"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            sendEvent(MemberSearchTask.class);
            return true;
        } catch (final Exception exc) {
            LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
        }
        return false;
    }

    private String getNotificationMessage(final Member inMember, final String inPwrd) throws VException {
        final IMessages lMessages = Activator.getMessages();
        final String lUserID = inMember.get(MemberHome.KEY_USER_ID).toString();
        final StringBuilder outNotification = new StringBuilder(lMessages.getFormattedMessage(
                "msg.member.data.saved", lUserID)); //$NON-NLS-1$
        if (displayPassword()) {
            outNotification
            .append(" ").append(lMessages.getFormattedMessage("msg.member.data.saved.add", lUserID, inPwrd)); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return new String(outNotification);
    }

}
