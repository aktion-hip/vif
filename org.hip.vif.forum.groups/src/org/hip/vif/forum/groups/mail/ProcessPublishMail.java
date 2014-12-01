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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.hip.vif.web.util.MailWithLink;
import org.ripla.interfaces.IMessages;

/** Feedback sent to the author informing that the reviewer has published the contributions.<br />
 * The author is invited to rate the reviewer's performance.
 *
 * Created on 15.08.2003
 *
 * @author Luthiger */
public class ProcessPublishMail extends MailWithLink {
    private final static String KEY_SUBJECT = "mail.feedback.subject"; //$NON-NLS-1$
    private final static String KEY_INTRO = "mail.publish.intro"; //$NON-NLS-1$
    private final static String KEY_RATING = "mail.publish.rating"; //$NON-NLS-1$
    private final static String KEY_LINK_TITLE = "mail.publish.title"; //$NON-NLS-1$

    private final StringBuilder body = new StringBuilder();
    private final StringBuilder bodyHtml = new StringBuilder();

    /** ProcessPublishMail constructor.
     *
     * @param inAuthor VIFMember
     * @param inReviewer VIFMember
     * @param inNotificationText StringBuilder
     * @param inNotificationTextHtml StringBuilder
     * @throws VException
     * @throws IOException */
    public ProcessPublishMail(final VIFMember inAuthor, final VIFMember inReviewer,
            final StringBuilder inNotificationText, final StringBuilder inNotificationTextHtml) throws VException,
            IOException {
        super(inAuthor, inReviewer);
        baseURL = createRequestedURL(
                ForwardControllerRegistry.INSTANCE.getController(ForwardControllerRegistry.Alias.FORWARD_RATING_FORM),
                true);
        body.append(inNotificationText);
        bodyHtml.append(inNotificationTextHtml);
    }

    @Override
    protected StringBuilder getBody() {
        final IMessages lMessages = Activator.getMessages();
        final StringBuilder outBody = new StringBuilder(getMessage(lMessages, KEY_INTRO)).append("\n\n").append(body); //$NON-NLS-1$
        return outBody.append(String.format(getMessage(lMessages, KEY_RATING), baseURL)).append("\n"); //$NON-NLS-1$
    }

    @Override
    protected StringBuilder getBodyHtml() {
        final IMessages lMessages = Activator.getMessages();
        final StringBuilder outBody = new StringBuilder("<p>").append(getMessage(lMessages, KEY_INTRO)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(HTML_HR).append(bodyHtml).append(HTML_HR);
        final String lLink = getUrlHtml(baseURL, getFormattedMessage(lMessages, KEY_LINK_TITLE));
        return outBody.append("<p>").append(String.format(getMessage(lMessages, KEY_RATING), lLink)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Returns the subject text, i.e. the text after the subject ID.
     *
     * @return String */
    @Override
    protected String getSubjectText() {
        return getMessage(Activator.getMessages(), KEY_SUBJECT);
    }
}
