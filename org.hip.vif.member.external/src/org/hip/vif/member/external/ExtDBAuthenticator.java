/*
	This package is part of the application VIF.
	Copyright (C) 2007, Benno Luthiger

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
 * <p>Class for authentication of VIF setups where member data is provided through a external database, i.e. not part of the application database.
 * Authentication is done using the <code>MemberHome</code> object, however, the <code>ExtMemberHomeImpl</code> is used.</p>
 * <p>This authenticator assumes passwords encrypted using the <code>Unix</code> encryption algorithm.</p>
 *
 * @author Luthiger
 * 22.04.2007
 */
public class ExtDBAuthenticator extends AbstractMemberAuthenticator implements IAuthenticator {
	
	public ExtDBAuthenticator() throws VException {
		super();
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.member.Authenticator#checkAuthentication(java.lang.String, java.lang.String, org.hip.vif.servlets.VIFContext)
	 */
	public void checkAuthentication(String inUserID, String inPassword) throws InvalidAuthenticationException, VException, SQLException {
		Member lMember = null;
		try {
			Member lExtMember = authenticationHome.checkAuthentication(inUserID, inPassword);
			
			//authentication passed, therefore, store the actor's ID and update the member cache.
			
			//default ID for guests
			Long lActorID = VIFAuthorization.GUEST_ID;
			
			//but if guest login, nothing to cache
			if ("".equals(inUserID)) {
				setActorToContext(lActorID, inUserID);
				return;
			}
	
			lMember = updateMemberCache(new ExtDBMemberInformation(lExtMember), inUserID);
		}
		catch (InvalidAuthenticationException exc) {
			// I'm the SU, let me in
			lMember = checkSU(inUserID, inPassword, exc);
		}
		setActorToContext(new Long(lMember.get(MemberHome.KEY_ID).toString()), inUserID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.member.Authenticator#isExternal()
	 */
	public boolean isExternal() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.member.IAuthenticator#encrypt(java.lang.String)
	 */
	public String encrypt(String inPassword) {
		return UnixCrypt.crypt(inPassword);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.member.IAuthenticator#matches(java.lang.String, java.lang.String)
	 */
	public boolean matches(String inEncryptedPassword, String inEnteredPassword) {
		return UnixCrypt.matches(inEncryptedPassword, inEnteredPassword);
	}

}
