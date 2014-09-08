/*
	This package is part of the application VIF.
	Copyright (C) 2008, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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
package org.hip.vif.core.mail;

import java.io.IOException;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailClient;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.mail.VMultiPartMessage;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.service.PreferencesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** <p>
 * Abstract class providing generic functionality for mails sent to notify a set of users.<br/>
 * Implementations of this class can be used to send mails to a set of persons.
 * </p>
 *
 * @author Benno Luthiger
 * @deprecated Use <code>org.hip.vif.web.mail.AbstractNotification</code> instead. */
@Deprecated
public abstract class AbstractNotification extends VObject implements IVIFMail {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNotification.class);

    // constants
    private static final String HTML_BODY = "<div style=\"font-family:Verdana,helvetica,arial,sans-serif;font-size:11pt;\">%s</div>";
    protected static final String DEFAULT_SUBJECT_ID = "[VIF]";

    protected String mailFrom = "";
    protected InternetAddress[] mailTo;
    protected boolean isBcc = true;
    private String host = "";

    /** AbstractNotification constructor
     * 
     * @param inReceiverMails InternetAddress[]
     * @param isBcc boolean <code>True</code> if receiver addresses are set in the mailBcc, else they are set in the
     *            mailTo */
    public AbstractNotification(final InternetAddress[] inReceiverMails, final boolean isBcc) {
        super();
        init(inReceiverMails, isBcc);
    }

    /** Alternative constructor with adapter classes for <code></code>
     * 
     * @param inReceiverMails Collection of {@link AddressAdapter}
     * @param isBCC boolean <code>True</code> if receiver addresses are set in the mailBcc, else they are set in the
     *            mailTo */
    public AbstractNotification(final Collection<AddressAdapter> inReceiverMails, final boolean isBCC) {
        super();

        int i = 0;
        final InternetAddress[] lAddresses = new InternetAddress[inReceiverMails.size()];
        for (final AddressAdapter lAddress : inReceiverMails) {
            lAddresses[i++] = lAddress.getInternetAddress();
        }
        init(lAddresses, isBCC);
    }

    private void init(final InternetAddress[] inReceiverMails, final boolean isBcc) {
        mailTo = inReceiverMails;
        this.isBcc = isBcc;
        host = getPreference(PreferencesHandler.KEY_MAIL_HOST);
        mailFrom = getPreference(PreferencesHandler.KEY_MAIL_ADDRESS);
    }

    protected static String getPreference(final String inKey) {
        try {
            return PreferencesHandler.INSTANCE.get(inKey);
        } catch (final IOException exc) {
            LOG.error("Error encountered while retrieving the application preferences!", exc);
        }
        return "";
    }

    /** Sends the mail to the configured addresses.
     * 
     * @throws MailGenerationException */
    @Override
    public void send() throws MailGenerationException {
        try {
            final VMultiPartMessage lMessage = new VMultiPartMessage();
            lMessage.setSubject(getSubjectText());
            final InternetAddress lDummyAddress = new InternetAddress(mailFrom);
            lMessage.setFromAddress(lDummyAddress);
            if (isBcc) {
                lMessage.setBccAddresses(mailTo);
                lMessage.setToAddresses(new InternetAddress[] { lDummyAddress });
            }
            else {
                lMessage.setToAddresses(mailTo);
            }
            lMessage.addPart(new String(getBody()));
            lMessage.addHtmlPart(String.format(HTML_BODY, getBodyHtml()));

            if (host == null || host.length() == 0) {
                throw new MailGenerationException("No mail host defined!");
            }
            final MailClient lMailer = new MailClient();
            lMailer.setMailHost(host);
            lMailer.sendMail(lMessage);
        } catch (final VException exc) {
            throw new MailGenerationException(exc.getMessage());
        } catch (final AddressException exc) {
            throw new MailGenerationException(exc.getMessage());
        } catch (final MessagingException exc) {
            throw new MailGenerationException(exc.getMessage());
        } catch (final NamingException exc) {
            throw new MailGenerationException(exc.getMessage());
        }
    }

    /** Hook for subclasses
     * 
     * @return StringBuilder */
    protected abstract StringBuilder getBody();

    /** Hook for subclasses
     * 
     * @return String the body formatted as html */
    protected StringBuilder getBodyHtml() {
        return getBody();
    }

    /** Hook for subclasses
     * 
     * @return String */
    protected abstract String getSubjectText();

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
     * @param inKey java.lang.String
     * @param inArg Object */
    protected String getFormattedMessage(final IMessages inMessages, final String inKey, final Object inArg) {
        return inMessages.getFormattedMessage(inKey, inArg);
    }

    protected static StringBuffer getSubjectID() {
        final String out = getPreference(PreferencesHandler.KEY_MAIL_SUBJECT_ID);
        return new StringBuffer(out.length() == 0 ? DEFAULT_SUBJECT_ID : out);
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

}
