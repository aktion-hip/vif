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

import org.hip.kernel.mail.MailGenerationException;

/**
 * Interface for basic VIF mails.
 * 
 * Created on 15.08.2003
 * 
 * @author Luthiger
 */
public interface IVIFMail {
	public static final String KEY_MAIL_HOST = "org.hip.vif.mail.host";
	public static final String KEY_MAIL_FROM = "org.hip.vif.mail.address";
	public static final String KEY_SUBJECT_ID = "org.hip.vif.mail.subjectId";

	/**
	 * Sends the mail to the configured address.
	 * 
	 * @throws MailGenerationException
	 */
	void send() throws MailGenerationException;
}
