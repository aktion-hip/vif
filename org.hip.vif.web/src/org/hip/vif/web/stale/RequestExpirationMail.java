/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.web.stale;

import java.io.IOException;
import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.mail.AbstractMail;
import org.hip.vif.markup.TextParser;
import org.hip.vif.web.Activator;
import org.hip.vif.web.stale.StaleRequestHelper.Collector;

/**
 * Mail to notify the reviewer that an author's request for review has been expired.
 *
 * @author Luthiger
 * Created: 10.10.2010
 */
public class RequestExpirationMail extends AbstractMail {
	private static final String KEY_INTRO = "mail.expire.info"; //$NON-NLS-1$
	private static final String KEY_INSERT_S = "mail.expire.contributions"; //$NON-NLS-1$
	private static final String KEY_INSERT_P = "mail.expire.contributionp"; //$NON-NLS-1$

	private IMessages messages = Activator.getMessages();
	private Collection<Collector> entries;

	/**
	 * Send a notification that the request for review has expired.
	 * 
	 * @param inReceiver VIFMember
	 * @param inEntries Collection<Collector>
	 * @throws VException
	 * @throws IOException
	 */
	public RequestExpirationMail(VIFMember inReceiver, Collection<Collector> inEntries) throws VException, IOException {
		super(inReceiver);
		entries = inEntries;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.mail.AbstractMail#getBody()
	 */
	@Override
	protected StringBuilder getBody() {
		int i = 0;
		StringBuilder out = new StringBuilder();
		for (Collector lEntry : entries) {
			out.append(lEntry.getContributionType()).append("\n").append(lEntry.getContributionTitle()).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
			i++;
		}
		return new StringBuilder(messages.getFormattedMessage(KEY_INTRO, getInsert(i))).append("\n\n").append(out); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.mail.AbstractMail#getBodyHtml()
	 */
	@Override
	protected StringBuilder getBodyHtml() {
		TextParser lParser = new TextParser();
		int i = 0;
		StringBuilder out = new StringBuilder();
		for (Collector lEntry : entries) {
			out.append("<p><b>").append(lEntry.getContributionType()).append("</b></p>").append(lParser.parseToHtml(lEntry.getContributionTitle())); //$NON-NLS-1$ //$NON-NLS-2$
			i++;
		}
		return new StringBuilder("<p>").append(messages.getFormattedMessage(KEY_INTRO, getInsert(i))).append("</p>").append(out); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private String getInsert(int inNumber) {
		return inNumber==1 ? messages.getMessage(KEY_INSERT_S) : messages.getMessage(KEY_INSERT_P);
	}

}
