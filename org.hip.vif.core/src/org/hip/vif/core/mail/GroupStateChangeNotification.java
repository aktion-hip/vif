/*
	This package is part of the application VIF.
	Copyright (C) 2004, Benno Luthiger

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

import javax.mail.internet.InternetAddress;

import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.core.Activator;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.PreferencesHandler;

/**
 * Mail to notify the participants about state changes of the discussion group. 
 * 
 * @author Benno Luthiger
 * Created on Jan 12, 2004
 */
public class GroupStateChangeNotification extends AbstractNotification implements IVIFMail {
	//constants
	private final static String KEY_GREETINGS 	= "org.hip.vif.msg.mail.greetings";
	private final static String KEY_SUBJECT		= "org.hip.vif.msg.notification.subject";
	private final static String KEY_ADDRESS 	= "org.hip.vif.msg.notification.address";
	private final static String KEY_MESSAGE1 	= "org.hip.vif.msg.notification.msg";
	private static IMessages messages = Activator.getMessages();

	private String subject = "";
	private String body = "";

	/**
	 * GroupStateChangeNotification constructor. This constructor should be used
	 * if the mail subject and body are set manually.
	 * 
	 * @param inReceiverMails InternetAddress[]
	 * @param isBcc boolean True if receiver addresses are set in the mailBcc, else they are set in the mailTo
	 * @throws MailGenerationException
	 */
	public GroupStateChangeNotification(InternetAddress[] inReceiverMails, boolean isBcc) throws MailGenerationException {
		super(inReceiverMails, isBcc);
	}

	/**
	 * GroupStateChangeNotification constructor to create the automatic notification mail.
	 * 
	 * @param inReceiverMails InternetAddress[]
	 * @param inMessage String The message explaining the new state of the group.
	 * @param inGroupName String
	 * @param isBcc boolean True if receiver addresses are set in the mailBcc, else they are set in the mailTo
	 * @throws MailGenerationException
	 */
	public GroupStateChangeNotification(InternetAddress[] inReceiverMails, String inMessage, String inGroupName, boolean isBcc) throws MailGenerationException {
		super(inReceiverMails, isBcc);
		subject = getSubject(inGroupName);
		body = getBody(inGroupName, inMessage);
	}
	
	/**
	 * Creates the default subject text for the notification of group state changes.
	 * 
	 * @param inGroupName String
	 * @return String
	 */
	public static String getSubject(String inGroupName) {
		StringBuilder outSubject = new StringBuilder(getSubjectID());
		outSubject.append(" ").append(messages.getFormattedMessage(KEY_SUBJECT, inGroupName));
		return new String(outSubject);
	}
	
	protected String getSubjectText() {
		return subject;
	}
	
	/**
	 * Creates the default message for the notification of group state changes.
	 * 
	 * @param inGroupName String
	 * @param inMessage String The message explaining the new state of the group.
	 * @return String
	 */
	public static String getBody(String inGroupName, String inMessage) {
		StringBuilder outBody = new StringBuilder(messages.getFormattedMessage(KEY_ADDRESS, inGroupName));
		outBody.append("<p>").append(messages.getFormattedMessage(KEY_MESSAGE1, inGroupName));
		outBody.append("<br/>").append(inMessage).append("</p>");
		outBody.append("<p>").append(messages.getMessage(KEY_GREETINGS)).append("<br/>");
		
		String lNaming = getPreference(PreferencesHandler.KEY_MAIL_NAMING);
		outBody.append(lNaming).append("</p>");
		return new String(outBody);
	}
	
	protected StringBuilder getBody() {
		return new StringBuilder(body);
	}

	/**
	 * Method to set the mail's message body manually.
	 * 
	 * @param inBody String
	 */
	public void setBody(String inBody) {
		body = inBody;
	}
	
	/**
	 * Method to set the mail's subject manually.
	 * 
	 * @param inSubject String
	 */
	public void setSubject(String inSubject) {
		subject = inSubject;
	}
}
