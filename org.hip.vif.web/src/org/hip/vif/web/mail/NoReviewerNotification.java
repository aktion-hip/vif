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

import java.util.Collection;

import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Activator;
import org.ripla.interfaces.IMessages;

/**
 * Mail to notify the group administrations about missing reviewers.
 * 
 * @author Luthiger Created: 20.10.2010
 */
public class NoReviewerNotification extends AbstractNotification {
	private final static String KEY_GREETINGS = "org.hip.vif.msg.mail.greetings";
	private static final String KEY_SUBJECT = "org.hip.vif.msg.mail.noreviewer.subject";
	private static final String KEY_BODY = "org.hip.vif.msg.mail.noreviewer.body";

	private final String groupName;
	private final IMessages messages = Activator.getMessages();

	/**
	 * Notifies the group administration that there are no reviewers available.
	 * 
	 * @param inReceiverMails
	 *            Collection<AddressAdapter> the group administrators mail
	 *            addresses
	 * @param inGroupName
	 *            String
	 */
	public NoReviewerNotification(
			final Collection<AddressAdapter> inReceiverMails,
			final String inGroupName) {
		super(inReceiverMails, false);
		groupName = inGroupName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.mail.AbstractNotification#getBody()
	 */
	@Override
	protected StringBuilder getBody() {
		final StringBuilder outBody = new StringBuilder(
				messages.getFormattedMessage(KEY_BODY, groupName));
		outBody.append("\n\n").append(messages.getMessage(KEY_GREETINGS));

		final String lNaming = getPreference(PreferencesHandler.KEY_MAIL_NAMING);
		outBody.append("\n\n").append(lNaming);
		return outBody;
	}

	@Override
	protected StringBuilder getBodyHtml() {
		return new StringBuilder("<p>").append(
				new String(getBody()).replace("\n", "<br/>")).append("</p>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.mail.AbstractNotification#getSubjectText()
	 */
	@Override
	protected String getSubjectText() {
		return new String(getSubjectID().append(" ").append(
				messages.getMessage(KEY_SUBJECT)));
	}

}
