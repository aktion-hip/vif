/**
	This package is part of the application VIF.
	Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.member;

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
 * Class for authentication of standard VIF setups, i.e. members are stored in
 * the internal member table. Authentication is done using the
 * <code>MemberHome</code> object.
 * </p>
 * <p>
 * This authenticator assumes passwords encrypted using the <code>Unix</code>
 * encryption algorithm.
 * </p>
 * 
 * @author Luthiger
 * @see org.hip.vif.member.UnixCrypt
 */
public class StandardAuthenticator extends AbstractMemberAuthenticator
		implements IAuthenticator {

	public StandardAuthenticator() throws VException {
		super();
	}

	@Override
	public void checkAuthentication(final String inUserID,
			final String inPassword) throws VException, SQLException,
			InvalidAuthenticationException {
		final Member lMember = cacheHome.checkAuthentication(inUserID,
				inPassword);
		// authorization passed, thus, set actor to context, but beware of
		// guests.
		final Long lActorID = lMember == null ? VIFAuthorization.GUEST_ID
				: new Long(lMember.get(MemberHome.KEY_ID).toString());
		setActorToContext(lActorID, inUserID);
	}

	@Override
	public boolean isExternal() {
		return false;
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
