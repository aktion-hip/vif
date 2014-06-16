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
package org.hip.vif.member.external;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFAuthorization;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.AbstractMemberAuthenticator;
import org.hip.vif.core.member.IAuthenticator;

/**
 * <p>
 * Class for authentication of VIF setups where member data is provided through
 * a external database, i.e. not part of the application database.
 * Authentication is done using the <code>MemberHome</code> object, however, the
 * <code>ExtMemberHomeImpl</code> is used.
 * </p>
 * <p>
 * This authenticator assumes passwords encrypted using the <code>Unix</code>
 * encryption algorithm.
 * </p>
 * 
 * @author Luthiger 22.04.2007
 */
public class ExtDBAuthenticator extends AbstractMemberAuthenticator implements
		IAuthenticator {

	public ExtDBAuthenticator() throws VException {
		super();
	}

	@Override
	public void checkAuthentication(final String inUserID,
			final String inPassword) throws InvalidAuthenticationException,
			VException, SQLException {
		Member lMember = null;
		try {
			final Member lExtMember = authenticationHome.checkAuthentication(
					inUserID, inPassword);

			// authentication passed, therefore, store the actor's ID and update
			// the member cache.

			// default ID for guests
			final Long lActorID = VIFAuthorization.GUEST_ID;

			// but if guest login, nothing to cache
			if ("".equals(inUserID)) {
				setActorToContext(lActorID, inUserID);
				return;
			}

			lMember = updateMemberCache(new ExtDBMemberInformation(lExtMember),
					inUserID);
		}
		catch (final InvalidAuthenticationException exc) {
			// I'm the SU, let me in
			lMember = checkSU(inUserID, inPassword, exc);
		}
		setActorToContext(new Long(lMember.get(MemberHome.KEY_ID).toString()),
				inUserID);
	}

	@Override
	public boolean isExternal() {
		return true;
	}

	@Override
	public String encrypt(final String inPassword) {
		return UnixCrypt.crypt(inPassword);
	}

	@Override
	public boolean matches(final String inEncryptedPassword,
			final String inEnteredPassword) {
		return UnixCrypt.matches(inEncryptedPassword, inEnteredPassword);
	}

}
