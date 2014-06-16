/**
	This package is part of the application VIF.
	Copyright (C) 2007-2014, Benno Luthiger

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
package org.hip.vif.core.member;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.InvalidAuthenticationException;

/**
 * Interface for classes used for authentication of persons logging into the
 * application.
 * 
 * @author Luthiger 22.04.2007
 */
public interface IAuthenticator {

	/**
	 * Check whether the specified user is authenticated
	 * 
	 * @param inUserID
	 *            java.lang.String
	 * @param inPassword
	 *            java.lang.String
	 * @throws InvalidAuthenticationException
	 * @throws VException
	 * @throws SQLException
	 */
	public void checkAuthentication(String inUserID, String inPassword)
			throws InvalidAuthenticationException, VException, SQLException;

	/**
	 * Checks whether forum authentication happens within the system or is
	 * external.
	 * 
	 * @return boolean <code>true</code> if this <code>Authenticator</code> is
	 *         external.
	 */
	public boolean isExternal();

	/**
	 * Check that the entered password, when encrypted, matches the encrypted
	 * password.
	 * 
	 * @param inEncryptedPassword
	 *            String the encrypted password to match.
	 * @param inEnteredPassword
	 *            String the entered password.
	 * @return boolean <code>true</code> if both password match and, therefore,
	 *         the entered password is correct.
	 */
	public boolean matches(String inEncryptedPassword, String inEnteredPassword);

	/**
	 * Encrypts the password.
	 * 
	 * @param inPasswore
	 *            String the clear text password.
	 * @return String the encrypted password.
	 */
	public String encrypt(String inPassword);

}
