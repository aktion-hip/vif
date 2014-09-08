/**
    This package is part of the application VIF.
    Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.member.mail;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.web.mail.AbstractMail;

/** Mail sent to the user after resetting the password.
 *
 * Created on 14.08.2003
 *
 * @author Luthiger */
public class PasswordResetMail extends AbstractMail {
    private final static String KEY_RESET = "mail.passwort.reset"; //$NON-NLS-1$
    private final String password;

    /** PasswordResetMail constructor.
     *
     * @param inContext Context
     * @param inMember VIFMember
     * @param inBody StringBuffer
     * @throws VException
     * @throws IOException */
    public PasswordResetMail(final VIFMember inMember, final String inPassword) throws VException, IOException {
        super(inMember);
        password = inPassword;
    }

    /** Hook for subclasses
     *
     * @return StringBuilder */
    @Override
    protected StringBuilder getBody() {
        return new StringBuilder(getFormattedMessage(Activator.getMessages(), KEY_RESET, password));
    }

    @Override
    protected StringBuilder getBodyHtml() {
        return getBody();
    }

}
