/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.admin.admin.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.data.GroupContainer;
import org.hip.vif.admin.admin.data.GroupWrapper;
import org.hip.vif.admin.admin.ui.SendMailView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.JoinParticipantToMemberHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.util.HtmlCleaner;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.mail.AbstractNotification;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Shows the form to enter mail subject and text for a mail to the members or participants of selected groups.
 *
 * @author Luthiger Created: 24.07.2009 */
@UseCaseController
public class SendMailTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory
            .getLogger(SendMailTask.class);

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_SEND_MAIL;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        emptyContextMenu();
        final GroupHome lGroupHome = VifBOMHelper.getGroupHome();
        try {
            final Long lActorID = getActor().getActorID();
            final boolean isAdmin = isAdmin(lActorID);
            return new SendMailView(
                    GroupContainer.createData(lGroupHome
                            .selectForAdministration(lActorID, createOrder()),
                            isAdmin), this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    protected boolean isAdmin(final Long inActorID) throws Exception {
        final int lRole = BOMHelper.getMemberCacheHome()
                .getMember(inActorID.toString()).getBestRole();
        if (lRole == RoleHome.ROLE_SU || lRole == RoleHome.ROLE_ADMIN) {
            return true;
        }
        return false;
    }

    protected OrderObject createOrder() throws VException {
        final OrderObject outOrder = new OrderObjectImpl();
        outOrder.setValue(GroupHome.KEY_NAME, 0);
        return outOrder;
    }

    /** Callback method, process the input and send the mails.
     * 
     * @param inSelected Collection<GroupWrapper> the groups to whose members the mails should be sent
     * @param inSubject String plain text
     * @param inBody String html (rich text)
     * @return boolean <code>true</code> if the mails could be processed successfully */
    public boolean processGroups(final Collection<GroupWrapper> inSelected,
            final String inSubject, final String inBody) {
        // retrieve mail addresses
        MailCollector lMails = new MailCollector();
        try {
            if (sendAll(inSelected)) {
                // send mail to all members
                lMails = getMailsFromAll(lMails);
            } else {
                // send mail to the participants in the groups
                lMails = getMailsFromGroups(inSelected, lMails);
            }
            // send mail
            final Mail lMail = new Mail(
                    InternetAddress.parse(lMails.getMails()), inSubject, inBody);
            lMail.send();

            // redisplay this task with empty fields
            showNotification(
                    Activator.getMessages().getMessage("admin.send.mail.msg.success"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(SendMailTask.class);
            return true;

        } catch (final Exception exc) {
            LOG.error("Error encountered while preparing the mail.", exc); //$NON-NLS-1$
        }
        return false;
    }

    boolean sendAll(final Collection<GroupWrapper> inSelected) {
        for (final GroupWrapper lGroupWrapper : inSelected) {
            if (lGroupWrapper.getGroupID() == 0l) {
                return true;
            }
        }
        return false;
    }

    private MailCollector getMailsFromAll(final MailCollector inMails)
            throws Exception {
        final MemberHome lHome = BOMHelper.getMemberCacheHome();
        final QueryResult lMembers = lHome.select();
        while (lMembers.hasMoreElements()) {
            inMails.addMail(lMembers.nextAsDomainObject()
                    .get(MemberHome.KEY_MAIL).toString());
        }
        return inMails;
    }

    private MailCollector getMailsFromGroups(
            final Collection<GroupWrapper> inSelected,
            final MailCollector inMails) throws VException, SQLException {
        final JoinParticipantToMemberHome lHome = BOMHelper
                .getJoinParticipantToMemberHome();
        for (final GroupWrapper lGroup : inSelected) {
            final QueryResult lMembers = lHome.select(lGroup.getGroupID());
            while (lMembers.hasMoreElements()) {
                inMails.addMail(lMembers.nextAsDomainObject()
                        .get(MemberHome.KEY_MAIL).toString());
            }
        }
        return inMails;
    }

    // --- private classes ---

    private class MailCollector {
        private final static String SEPARATOR = ", "; //$NON-NLS-1$
        private final Collection<String> lMails = new Vector<String>();
        private final StringBuilder lAllMails = new StringBuilder();

        public void addMail(final String inMail) {
            if (lMails.contains(inMail))
                return;
            lMails.add(inMail);
            lAllMails
                    .append(lAllMails.length() == 0 ? "" : SEPARATOR).append(inMail); //$NON-NLS-1$
        }

        public String getMails() {
            return new String(lAllMails);
        }
    }

    private class Mail extends AbstractNotification {
        private final String subject;
        private final String body;
        private final String bodyHtml;

        public Mail(final InternetAddress[] inMailAddresses,
                final String inSubject, final String inBody) {
            super(inMailAddresses, true);
            subject = inSubject;
            bodyHtml = HtmlCleaner.cleanUp(inBody);
            body = HtmlCleaner.toPlain(bodyHtml);
        }

        @Override
        protected StringBuilder getBody() {
            return new StringBuilder(body);
        }

        @Override
        protected StringBuilder getBodyHtml() {
            return new StringBuilder(bodyHtml);
        }

        @Override
        protected String getSubjectText() {
            final StringBuffer outSubject = getSubjectID();
            return new String(outSubject.append(" ").append(subject)); //$NON-NLS-1$
        }
    }

}
