/**
 This package is part of the application VIF.
 Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.member.mail;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.hip.vif.web.util.MailWithLink;
import org.ripla.interfaces.IMessages;

/** Mail sent to the new member including userID and password.
 *
 * Created on 15.08.2003
 *
 * @author Luthiger */
public class CreateMemberMail extends MailWithLink {
    private static final String TMPL_LINK = "<a href=\"%s\">%s</a>"; //$NON-NLS-1$
    private final static String KEY_PASSWORD = "ui.member.editor.label.password"; //$NON-NLS-1$
    private final static String KEY_USER_ID = "ui.member.editor.label.userid"; //$NON-NLS-1$
    private final static String KEY_MAIL1 = "mail.member.mail1"; //$NON-NLS-1$
    private final static String KEY_MAIL2 = "mail.member.mail2"; //$NON-NLS-1$
    private final static String KEY_FORUM_URL = "mail.member.forum"; //$NON-NLS-1$
    private final static String KEY_PWCHNGE_URL = "mail.member.pwrd.change"; //$NON-NLS-1$

    private final static String INDENT = "     "; //$NON-NLS-1$

    private final String userID;
    private final String password;
    private final String forumName;
    private final String urlForumChangePW;
    private final String urlForumMain;

    /** CreateMemberMail default constructor.
     *
     * @param inMember
     * @throws VException
     * @throws IOException */
    public CreateMemberMail(final VIFMember inMember, final String inUserID, final String inPassword)
            throws VException, IOException {
        super(inMember);
        userID = inUserID;
        password = inPassword;
        forumName = PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_FORUM_NAME);
        urlForumMain = getForumAppURL();
        urlForumChangePW = createRequestedURL(
                ForwardControllerRegistry.INSTANCE.getController(ForwardControllerRegistry.Alias.FORWARD_PWCHNGE_FORM),
                true);
    }

    /** @see org.hip.vif.mail.AbstractMail#getBody() */
    @Override
    protected StringBuilder getBody() {
        final IMessages lMessages = Activator.getMessages();
        final StringBuilder outBody = new StringBuilder(getFormattedMessage(lMessages, KEY_MAIL1, forumName));
        outBody.append("\n\n").append(getMessage(lMessages, KEY_MAIL2)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(INDENT).append(getMessage(lMessages, KEY_USER_ID)).append(": ").append(userID).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(INDENT).append(getMessage(lMessages, KEY_PASSWORD)).append(": ").append(password).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(getFormattedMessage(lMessages, KEY_FORUM_URL, urlForumMain)).append("\n"); //$NON-NLS-1$
        outBody.append(getFormattedMessage(lMessages, KEY_PWCHNGE_URL, urlForumChangePW)).append("\n"); //$NON-NLS-1$
        return outBody;
    }

    @Override
    protected StringBuilder getBodyHtml() {
        final IMessages lMessages = Activator.getMessages();
        final StringBuilder outBody = new StringBuilder("<p>").append(getFormattedMessage(lMessages, KEY_MAIL1, forumName)); //$NON-NLS-1$
        outBody.append("</p><p>").append(getMessage(lMessages, KEY_MAIL2)).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(INDENT).append(getMessage(lMessages, KEY_USER_ID)).append(": ").append(userID).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(INDENT).append(getMessage(lMessages, KEY_PASSWORD))
                .append(": ").append(password).append("</p><p>"); //$NON-NLS-1$ //$NON-NLS-2$
        outBody.append(getFormattedMessage(lMessages, KEY_FORUM_URL, renderClickable(urlForumMain, urlForumMain)))
                .append("<br/>"); //$NON-NLS-1$
        outBody.append(
                getFormattedMessage(lMessages, KEY_PWCHNGE_URL,
                        renderClickable(urlForumChangePW, getMessage(lMessages, "mail.member.pwrd.label")))).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
        return outBody;
    }

    private String renderClickable(final Object... inArgs) {
        return String.format(TMPL_LINK, inArgs);
    }

}
