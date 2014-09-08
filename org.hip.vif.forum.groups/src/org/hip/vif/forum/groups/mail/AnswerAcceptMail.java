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
import org.hip.vif.web.mail.AbstractMail;

/** Feedback sent to the author informing that the reviewer accepted the task.
 *
 * Created on 15.08.2003
 *
 * @author Luthiger */
public class AnswerAcceptMail extends AbstractMail {
    private final static String KEY_SUBJECT = "mail.feedback.subject"; //$NON-NLS-1$
    private final static String KEY_INTRO = "mail.accept.intro"; //$NON-NLS-1$

    private final StringBuilder body = new StringBuilder();
    private final StringBuilder bodyHtml = new StringBuilder();

    /** AnswerAcceptMail constructor.
     *
     * @param inAuthor VIFMember
     * @param inReviewer VIFMember
     * @param inNotificationText StringBuilder
     * @param inNotificationTextHtml StringBuilder
     * @throws VException
     * @throws IOException */
    public AnswerAcceptMail(final VIFMember inAuthor, final VIFMember inReviewer,
            final StringBuilder inNotificationText, final StringBuilder inNotificationTextHtml) throws VException,
            IOException {
        super(inAuthor, inReviewer);
        body.append(inNotificationText);
        bodyHtml.append(inNotificationTextHtml);
    }

    @Override
    protected StringBuilder getBody() {
        return new StringBuilder(getFormattedMessage(Activator.getMessages(), KEY_INTRO, senderFullName))
                .append("\n\n").append(body); //$NON-NLS-1$
    }

    @Override
    protected StringBuilder getBodyHtml() {
        final StringBuilder out = new StringBuilder("<p>").append(getFormattedMessage(Activator.getMessages(), KEY_INTRO, senderFullName)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        return out.append(HTML_HR).append(bodyHtml).append(HTML_HR);
    }

    /** Returns the subject text, i.e. the text after the subject ID.
     *
     * @return String */
    @Override
    protected String getSubjectText() {
        return getMessage(Activator.getMessages(), KEY_SUBJECT);
    }
}
