/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.core.mail;

import java.io.IOException;

import org.hip.vif.core.Activator;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.PreferencesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class providing functionality for mails and notifications.
 * 
 * @author Luthiger
 * Created: 26.12.2011
 */
public class MailUtils {
	private static final Logger LOG = LoggerFactory.getLogger(MailUtils.class);
	
	private static final String NL = System.getProperty("line.separator");
	private final static String KEY_GREETINGS 	= "org.hip.vif.msg.mail.greetings";

	private static final IMessages MESSAGES = Activator.getMessages();
	
	/**
	 * Convenience method to create the greetings of the mail sent (plain text version).
	 * 
	 * @return StringBuilder
	 */
	public static StringBuilder getMailGreetings() {
		StringBuilder outGreetings = new StringBuilder();
		outGreetings.append(NL).append(NL).append(MESSAGES.getMessage(KEY_GREETINGS));
		return outGreetings.append(NL).append(NL).append(getNaming());
	}
	
	/**
	 * Convenience method to create the greetings of the mail sent (html version).
	 * 
	 * @return StringBuilder
	 */
	public static StringBuilder getMailGreetingsHtml() {
		StringBuilder outGreetings = new StringBuilder("<p>");
		outGreetings.append(MESSAGES.getMessage(KEY_GREETINGS));
		return outGreetings.append("<br/><i>").append(getNaming()).append("</i></p>");
	}
	
	private static String getNaming() {
		try {
			return PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_MAIL_NAMING);
		}
		catch (IOException exc) {
			LOG.error("Configuration error!", exc);
		}
		return "";
	}

}
