/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

import org.hip.vif.web.Activator;
import org.hip.vif.web.Constants;
import org.ripla.interfaces.IMessages;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;

/** @author Luthiger Created: 12.02.2012 */
public class PasswordInputChecker {
    private final PasswordField newPwrd1;
    private final PasswordField newPwrd2;

    /** Constructor.
     *
     * @param inNew1 {@link PasswordField} password
     * @param inNew2 {@link PasswordField} confirmation */
    public PasswordInputChecker(final PasswordField inNew1, final PasswordField inNew2) {
        newPwrd1 = inNew1;
        newPwrd2 = inNew2;
    }

    /** Checks the entered passwords for length and confirmation.
     *
     * @return boolean <code>true</code> if the password check yields ok */
    public boolean checkInput() {
        final IMessages lMessages = Activator.getMessages();
        if (newPwrd1.getValue().toString().length() < Constants.MIN_PWRD_LENGTH) {
            Notification.show(lMessages.getMessage("errmsg.pwrd.length"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            return false;
        }
        if (!newPwrd1.getValue().equals(newPwrd2.getValue())) {
            Notification.show(lMessages.getMessage("errmsg.pwrd.confirm"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            return false;
        }
        return true;
    }
}
