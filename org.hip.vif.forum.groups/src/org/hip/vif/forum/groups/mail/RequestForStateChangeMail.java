/**
 This package is part of the application VIF.
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
package org.hip.vif.forum.groups.mail;

import java.io.IOException;
import java.util.Collection;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.util.ListJoiner;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.web.util.MailWithLink;
import org.ripla.interfaces.IMessages;

/** Sends mails to the group administration when a participant requests a question's state changed to "answered" or
 * "open".
 *
 * @author Benno Luthiger Created on Apr 15, 2005 */
public class RequestForStateChangeMail extends MailWithLink {
    private final static String KEY_SUBJECT = "mail.change.state.subject"; //$NON-NLS-1$
    private final static String KEY_INTRO = "mail.change.state.intro"; //$NON-NLS-1$
    private final static String KEY_LINK = "mail.change.state.link"; //$NON-NLS-1$

    private String groupName = ""; //$NON-NLS-1$
    private final String message;
    private final IMessages messages = Activator.getMessages();

    /** RequestForSettlementMail constructor.
     *
     * @param inAdmin VIFMember
     * @param inAdditional Collection<GeneralDomainObject> of additional group admins (VIFMember)
     * @param inRequestor VIFMember
     * @param inGroupName String
     * @param inMessage String
     * @throws VException
     * @throws IOException */
    public RequestForStateChangeMail(final VIFMember inAdmin, final Collection<GeneralDomainObject> inAdditional,
            final VIFMember inRequestor, final String inGroupName, final String inMessage) throws VException,
            IOException {
        super(inAdmin, inRequestor);
        addMailToAddress(getAdditionAddresses(inAdditional));
        groupName = inGroupName;
        message = inMessage;
        baseURL = createRequestedURL(
                ForwardTaskRegistry.INSTANCE.getTask(ForwardTaskRegistry.FORWARD_GROUP_ADMIN_PENDING), true);
    }

    @Override
    protected StringBuilder getBody() {
        final StringBuilder outBody = new StringBuilder(message);
        outBody.append("\n").append(getFormattedMessage(messages, KEY_LINK, baseURL)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        return outBody;
    }

    @Override
    protected StringBuilder getBodyHtml() {
        final StringBuilder outBody = new StringBuilder("<p>").append(message); //$NON-NLS-1$
        outBody.append("<br/>").append(getFormattedMessage(messages, KEY_LINK, baseURL)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        return outBody;
    }

    /** Returns the subject text, i.e. the text after the subject ID.
     *
     * @return String */
    @Override
    protected String getSubjectText() {
        return getMessage(messages, KEY_SUBJECT);
    }

    /** Override the super classes method. */
    @Override
    protected StringBuilder createMailAddress() throws MailGenerationException {
        return new StringBuilder(getFormattedMessage(messages, KEY_INTRO, groupName));
    }

    private String getAdditionAddresses(final Collection<GeneralDomainObject> inAdditional) throws VException {
        final ListJoiner outAddresses = new ListJoiner();
        for (final GeneralDomainObject lObject : inAdditional) {
            outAddresses.addEntry(((VIFMember) lObject).getMailAddress());
        }
        return outAddresses.joinSpaced(","); //$NON-NLS-1$
    }
}
