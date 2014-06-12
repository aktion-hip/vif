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

package org.hip.vif.web.util;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;

import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Luthiger
 * Created: 12.02.2012
 */
public class PasswordInputChecker {	
	private PasswordField newPwrd1;
	private PasswordField newPwrd2;
	private Window window;

	/**
	 * Constructor.
	 * 
	 * @param inNew1 {@link PasswordField} password
	 * @param inNew2 {@link PasswordField} confirmation
	 * @param inWindow {@link Window}
	 */
	public PasswordInputChecker(PasswordField inNew1, PasswordField inNew2, Window inWindow) {
		newPwrd1 = inNew1;
		newPwrd2 = inNew2;
		window = inWindow;
	}

	/**
	 * Checks the entered passwords for length and confirmation.
	 * 
	 * @return boolean <code>true</code> if the password check yields ok
	 */
	public boolean checkInput() {
		IMessages lMessages = Activator.getMessages();
		if (newPwrd1.getValue().toString().length() < Constants.MIN_PWRD_LENGTH) {
			window.showNotification(lMessages.getMessage("errmsg.pwrd.length"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			return false;
		}
		if (!newPwrd1.getValue().equals(newPwrd2.getValue())) {
			window.showNotification(lMessages.getMessage("errmsg.pwrd.confirm"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			return false;
		}
		return true;
	}
}
