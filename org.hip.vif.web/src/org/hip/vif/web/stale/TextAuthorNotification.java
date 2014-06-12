/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.web.stale;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.mail.AbstractMail;
import org.hip.vif.web.Activator;

/**
 * Mail to the author of a text contribution (e.g. <code>TextImpl</code>) to notify him that he should trigger 
 * the request for review again.
 * 
 * @author Luthiger
 * Created: 03.03.2012
 */
public class TextAuthorNotification extends AbstractMail {
	private static final String KEY_INTRO = "mail.author.notify.intro"; //$NON-NLS-1$
	private static final String KEY_TEXT = "mail.author.notify.hint"; //$NON-NLS-1$
	
	private StringBuilder body = new StringBuilder();
	private StringBuilder bodyHtml = new StringBuilder();
	private IMessages messages = Activator.getMessages();
	
	/**
	 * TextAuthorNotification constructor.
	 * 
	 * @param inReceiver {@link VIFMember} the receiver of the mail
	 * @param inNotificator {@link StaleTextCollector} the object containing the collected text to send in the mail's body
	 * @throws VException
	 * @throws IOException
	 */
	public TextAuthorNotification(VIFMember inReceiver, StaleTextCollector inNotificator) throws VException, IOException {
		super(inReceiver);
		body.append(inNotificator.getNotificationText());
		bodyHtml.append(inNotificator.getNotificationTextHtml());
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.core.mail.AbstractMail#getBody()
	 */
	@Override
	protected StringBuilder getBody() {
		StringBuilder outBody = new StringBuilder(messages.getMessage(KEY_INTRO));
		outBody.append(body).append("\n"); //$NON-NLS-1$
		outBody.append(messages.getMessage(KEY_TEXT));
		return outBody;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.core.mail.AbstractMail#getBodyHtml()
	 */
	@Override
	protected StringBuilder getBodyHtml() {
		StringBuilder outBody = new StringBuilder("<p>").append(messages.getMessage(KEY_INTRO)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		outBody.append(HTML_HR).append(bodyHtml).append(HTML_HR).append("<p>"); //$NON-NLS-1$
		outBody.append(messages.getMessage(KEY_TEXT)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
		return outBody;
	}

}
