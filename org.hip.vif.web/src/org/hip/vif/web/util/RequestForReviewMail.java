/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.web.util;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.web.Activator;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.ripla.interfaces.IMessages;

/** Mail sent to the potential reviewer to request a review.
 *
 * Created on 15.08.2003
 *
 * @author Luthiger */
public class RequestForReviewMail extends MailWithLink {
    private final static String KEY_SUBJECT = "mail.review.subject"; //$NON-NLS-1$
    private final static String KEY_INTRO = "mail.review.intro"; //$NON-NLS-1$
    private final static String KEY_LINK_TITLE = "mail.review.title"; //$NON-NLS-1$
    private final static String KEY_TEXT1 = "mail.review.text1"; //$NON-NLS-1$
    private final static String KEY_TEXT2 = "mail.review.text2"; //$NON-NLS-1$
    private final static String KEY_TEXT3 = "mail.review.text3"; //$NON-NLS-1$

    private final IMessages messages = Activator.getMessages();

    private final StringBuilder body = new StringBuilder();
    private final StringBuilder bodyHtml = new StringBuilder();

    /** RequestForReviewMail constructor.
     *
     * @param inReviewer VIFMember
     * @param inAuthor VIFMember
     * @param inNotificationText StringBuilder
     * @param inNotificationTextHtml StringBuilder
     * @throws VException
     * @throws IOException */
    public RequestForReviewMail(final VIFMember inReviewer, final VIFMember inAuthor,
            final StringBuilder inNotificationText, final StringBuilder inNotificationTextHtml) throws VException,
            IOException {
        super(inReviewer, inAuthor);
        body.append(inNotificationText);
        bodyHtml.append(inNotificationTextHtml);
        // TODO: URL is not ok:
        // /forum?request=3Dorg.hip.vif.forum.groups/org.hip.vif.forum.groups.tasks.RequestsListTask&groupID=3D41
        baseURL = createRequestedURL(
                ForwardControllerRegistry.INSTANCE.getController(ForwardControllerRegistry.Alias.FORWARD_REQUEST_LIST),
                true);
    }

    /** @see org.hip.vif.web.mail.AbstractMail#getBody() */
    @Override
    protected StringBuilder getBody() {
        final StringBuilder outBody = new StringBuilder(messages.getMessage(KEY_INTRO));
        outBody.append("\n\n").append(body).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
        if (baseURL == null) {
            outBody.append(messages.getMessage(KEY_TEXT2));
        }
        else {
            outBody.append(messages.getFormattedMessage(KEY_TEXT1, baseURL));
        }
        outBody.append("\n").append(messages.getMessage(KEY_TEXT3)); //$NON-NLS-1$
        return outBody;
    }

    @Override
    protected StringBuilder getBodyHtml() {
        final StringBuilder outBody = new StringBuilder("<p>").append(messages.getMessage(KEY_INTRO)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(HTML_HR).append(bodyHtml).append(HTML_HR).append("<p>"); //$NON-NLS-1$
        if (baseURL == null) {
            outBody.append(messages.getMessage(KEY_TEXT2));
        }
        else {
            final String lLink = getUrlHtml(baseURL, messages.getMessage(KEY_LINK_TITLE));
            outBody.append(messages.getFormattedMessage(KEY_TEXT1, lLink));
        }
        outBody.append("<br/>").append(messages.getMessage(KEY_TEXT3)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        return outBody;
    }

    /** Returns the subject text, i.e. the text after the subject ID.
     *
     * @return String */
    @Override
    protected String getSubjectText() {
        return messages.getMessage(KEY_SUBJECT);
    }
}
