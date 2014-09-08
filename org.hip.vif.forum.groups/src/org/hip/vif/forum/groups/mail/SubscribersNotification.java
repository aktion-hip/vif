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

import java.util.Collection;

import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.web.mail.AbstractNotification;

/** Notifies the subscribers about changes in the discussion group.
 *
 * @author Benno Luthiger Created on Feb 22, 2004 */
public class SubscribersNotification extends AbstractNotification {
    // constants
    private static final String KEY_SUBJECT = "mail.notification.subject"; //$NON-NLS-1$

    private String groupName = ""; //$NON-NLS-1$
    private final StringBuilder body;
    private final StringBuilder bodyHtml;

    /** SubscribersNotification constructor
     *
     * @param inReceiverMails InternetAddress[]
     * @param inBody StringBuilder
     * @param inBodyHtml StringBuilder
     * @param inGroupName String
     * @param isBcc boolean */
    public SubscribersNotification(final Collection<AddressAdapter> inReceiverMails, final StringBuilder inBody,
            final StringBuilder inBodyHtml, final String inGroupName, final boolean isBcc) {
        super(inReceiverMails, isBcc);
        body = inBody;
        bodyHtml = inBodyHtml;
        groupName = inGroupName;
    }

    @Override
    protected StringBuilder getBody() {
        return body;
    }

    @Override
    protected StringBuilder getBodyHtml() {
        return bodyHtml;
    }

    @Override
    protected String getSubjectText() {
        final StringBuffer outSubject = getSubjectID();
        outSubject.append(" ").append(getFormattedMessage(Activator.getMessages(), KEY_SUBJECT, groupName)); //$NON-NLS-1$
        return new String(outSubject);
    }
}
