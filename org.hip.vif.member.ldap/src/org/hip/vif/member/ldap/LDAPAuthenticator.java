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
package org.hip.vif.member.ldap;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFAuthorization;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.AbstractMemberAuthenticator;
import org.hip.vif.core.member.IAuthenticator;

/**
 * Authenticator for setups where member entries are provided by a LDAP server.
 *
 * @author Luthiger
 * 22.04.2007
 */
public class LDAPAuthenticator extends AbstractMemberAuthenticator implements IAuthenticator {	

	public LDAPAuthenticator() throws VException {
		super();
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.member.Authenticator#checkAuthentication(java.lang.String, java.lang.String, org.hip.vif.servlets.VIFContext)
	 */
	public void checkAuthentication(String inUserID, String inPassword) throws InvalidAuthenticationException, VException, SQLException {
		Member lMember = null;
		try {
			authenticationHome.checkAuthentication(inUserID, inPassword);
			
			//authentication passed, therefore, update the member cache:
			//default ID for guests
			Long lActorID = VIFAuthorization.GUEST_ID;
			
			//but if guest login, nothing to cache
			if ("".equals(inUserID)) {
				setActorToContext(lActorID, inUserID);
				return;
			}
	
			lMember = updateMemberCache(new LDAPMemberInformation(authenticationHome.getMemberByUserID(inUserID)), inUserID);
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

	public String encrypt(String inPassword) {
		// not used in this case
		return "";
	}

	public boolean matches(String inEncryptedPassword, String inEnteredPassword) {
		// not used in this case
		return false;
	}

}
