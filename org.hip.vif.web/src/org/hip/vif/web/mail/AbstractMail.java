/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.ExceptionCallback;
import org.hip.kernel.mail.MailClient;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.mail.VMultiPartMessage;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/** <p>
 * Abstract class providing basic functionality to send mails.<br/>
 * Implementations of the class can be used to send mails to a single person with at most one single <code>cc</code>.
 * </p>
 *
 * Created on 14.08.2003
 *
 * @author Luthiger */
public abstract class AbstractMail extends VObject implements IVIFMail {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMail.class);

    private static final String HTML_BODY = "<div style=\"font-family:Verdana,helvetica,arial,sans-serif;font-size:11pt;\">%s</div>";
    protected static final String HTML_HR = "<hr noshade width=\"200\" size=\"1\" align=\"left\" />";

    private final static String KEY_FEMALE = "org.hip.vif.msg.mail.address.female";
    private final static String KEY_MALE = "org.hip.vif.msg.mail.address.male";
    private final static String KEY_UNKNOWN_SEX = "org.hip.vif.msg.mail.address.unknown";

    protected final static String REQUEST_URL_TMPL = "%s?request%s&groupID=%s";

    private String subject;
    private String subjectID;
    private String mailTo;
    private String mailFrom;
    private String mailCC;
    private VIFMember receiver;
    private final IMessages messages = Activator.getMessages();
    protected String baseURL;
    protected String senderFullName = "";
    private String host = "";

    /** AbstractMail constructor.
     *
     * @param inReceiver VIFMember The member receiving the mail.
     * @throws VException
     * @throws IOException */
    public AbstractMail(final VIFMember inReceiver) throws VException,
            IOException {
        super();
        init(inReceiver, null);
    }

    /** AbstractMail constructor.
     *
     * @param inReceiver VIFMember The member receiving the mail.
     * @param inSender VIFMember The member sending the mail.
     * @throws VException
     * @throws IOException */
    public AbstractMail(final VIFMember inReceiver, final VIFMember inSender)
            throws VException, IOException {
        super();
        init(inReceiver, inSender);
    }

    private void init(final VIFMember inReceiver, final VIFMember inSender)
            throws VException, IOException {
        receiver = inReceiver;

        final PreferencesHandler lPreferences = PreferencesHandler.INSTANCE;
        subject = lPreferences.get(PreferencesHandler.KEY_MAIL_SUBJECT_TEXT);
        subjectID = lPreferences.get(PreferencesHandler.KEY_MAIL_SUBJECT_ID);
        mailFrom = lPreferences.get(PreferencesHandler.KEY_MAIL_ADDRESS);
        host = lPreferences.get(PreferencesHandler.KEY_MAIL_HOST);

        mailTo = receiver.getMailAddress();
        if (inSender != null) {
            mailCC = inSender.getMailAddress();
            senderFullName = inSender.getFullName();
        }
    }

    /** Sends the mail to the configured address.
     *
     * @throws MailGenerationException */
    @Override
    public void send() throws MailGenerationException {
        try {
            final StringBuilder lSubject = new StringBuilder(subjectID).append(
                    " ").append(getSubjectText());
            final StringBuilder lAddress = createMailAddress();

            final InternetAddress lAddressFrom = new InternetAddress(mailFrom);
            final VMultiPartMessage lMessage = new VMultiPartMessage();
            lMessage.setSubject(new String(lSubject));
            lMessage.setFromAddress(lAddressFrom);
            lMessage.setToAddresses(InternetAddress.parse(mailTo));
            if (mailCC != null) {
                lMessage.setCcAddresses(InternetAddress.parse(mailCC));
            }

            // plain
            StringBuilder lBody = new StringBuilder();
            lBody.append(lAddress).append("\n\n").append(getBody())
                    .append(getMailGreetings());
            lMessage.addPart(new String(lBody));
            // html
            lBody = new StringBuilder();
            lBody.append("<p>").append(lAddress).append("</p>")
                    .append(getBodyHtml()).append(getMailGreetingsHtml());
            lMessage.addHtmlPart(String.format(HTML_BODY, lBody));

            if (host == null || host.length() == 0) {
                throw new MailGenerationException("No mail host defined!");
            }
            final MailClient lMailer = new MailClient();
            lMailer.setMailHost(host);

            lMailer.sendMail(lMessage, new ExceptionCallback() {
                @Override
                public void handleException(final MessagingException inExc) {
                    LOG.error("Unable to send mail!", inExc);
                    Notification.show(
                            messages.getMessage("errmsg.mail.send.exc"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            });
        } catch (VException | MessagingException | NamingException exc) {
            throw new MailGenerationException(exc.getMessage());
        }
    }

    public String getMailTo() {
        return mailTo;
    }

    /** Returns a message to a given ID, language dependent
     *
     * @return java.lang.String
     * @param inMsgId java.lang.String */
    protected String getMessage(final IMessages inMessages, final String inKey) {
        return inMessages.getMessage(inKey);
    }

    /** Returns a localized message to a given ID and formats it with the specified argument.
     *
     * @return java.lang.String
     * @param inMsgId java.lang.String
     * @param inArg Object */
    protected String getFormattedMessage(final IMessages inMessages,
            final String inKey, final Object... inArgs) {
        return inMessages.getFormattedMessage(inKey, inArgs);
    }

    /** Hook for subclasses
     *
     * @return StringBuilder */
    protected abstract StringBuilder getBody();

    protected abstract StringBuilder getBodyHtml();

    /** Creates the address of the mail, e.g. Dear XY, Subclasses may overwrite this method.
     *
     * @return StringBuilder
     * @throws MailGenerationException */
    protected StringBuilder createMailAddress() throws MailGenerationException {
        try {
            final StringBuilder outAddress = new StringBuilder();
            if (receiver.hasKnownSex()) {
                if (receiver.isFemale()) {
                    outAddress.append(messages.getMessage(KEY_FEMALE));
                } else {
                    outAddress.append(messages.getMessage(KEY_MALE));
                }
            } else {
                outAddress.append(messages.getMessage(KEY_UNKNOWN_SEX));
            }
            return outAddress.append(" ").append(receiver.getFullName());
        } catch (final VException exc) {
            throw new MailGenerationException(exc.getMessage());
        }
    }

    /** Creates the greetings of the mail. Subclasses may overwrite this method.
     *
     * @return StringBuffer */
    protected StringBuilder getMailGreetings() {
        return MailUtils.getMailGreetings();
    }

    protected StringBuilder getMailGreetingsHtml() {
        return MailUtils.getMailGreetingsHtml();
    }

    /** Returns the subject text, i.e. the text after the subject ID. Subclasses may overwrite this method.
     *
     * @return String */
    protected String getSubjectText() {
        return subject;
    }

    /** Add additional mail address(es) If more then one, addresses must be comma separated.
     *
     * @param inAdress String Addition mail address(es) */
    protected void addMailToAddress(final String inAddress) {
        if (inAddress.length() == 0) {
            return;
        }

        mailTo += ", " + inAddress;
    }

    protected String getUrlHtml(final String inUrl, final String inLabel) {
        return String.format("<a href=\"%s\">%s</a>", inUrl, inLabel);
    }

}
