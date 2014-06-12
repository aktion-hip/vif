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
package org.hip.vif.forum.groups.mail;

import java.util.Collection;

import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.mail.AbstractNotification;
import org.hip.vif.forum.groups.Activator;

/**
 * Notifies the subscribers about changes in the discussion group.
 * 
 * @author Benno Luthiger
 * Created on Feb 22, 2004
 */
public class SubscribersNotification extends AbstractNotification {
	//constants
	private static final String KEY_SUBJECT = "mail.notification.subject"; //$NON-NLS-1$
	
	private String groupName = ""; //$NON-NLS-1$
	private StringBuilder body;
	private StringBuilder bodyHtml;

	/**
	 * SubscribersNotification constructor
	 * 
	 * @param inReceiverMails InternetAddress[]
	 * @param inBody StringBuilder
	 * @param inBodyHtml StringBuilder
	 * @param inGroupName String
	 * @param isBcc boolean
	 */
	public SubscribersNotification(Collection<AddressAdapter> inReceiverMails, StringBuilder inBody, StringBuilder inBodyHtml, String inGroupName, boolean isBcc) {
		super(inReceiverMails, isBcc);
		body = inBody;
		bodyHtml = inBodyHtml;
		groupName = inGroupName;
	}
	
	/**
	 * @see org.hip.vif.core.mail.AbstractNotification#getBody()
	 */
	protected StringBuilder getBody() {
		return body;
	}
	protected StringBuilder getBodyHtml() {
		return bodyHtml;
	}
	
	/**
	 * @see org.hip.vif.core.mail.AbstractNotification#getSubjectText()
	 */
	protected String getSubjectText() {
		StringBuffer outSubject = getSubjectID();
		outSubject.append(" ").append(getFormattedMessage(Activator.getMessages(), KEY_SUBJECT, groupName)); //$NON-NLS-1$
		return new String(outSubject);
	}
}
