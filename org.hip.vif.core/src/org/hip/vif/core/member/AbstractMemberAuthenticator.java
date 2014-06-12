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
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.interfaces.IActorManager;
import org.hip.vif.core.service.MemberUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all classes implementing the <code>Authenticator</code>
 * interface.
 * 
 * @author Luthiger 15.07.2007
 * @see org.hip.vif.core.member.IAuthenticator
 */
public abstract class AbstractMemberAuthenticator {
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractMemberAuthenticator.class);

	protected MemberHome authenticationHome;
	protected MemberHome cacheHome;

	public AbstractMemberAuthenticator() throws VException {
		final IMemberSearcher lSearcher = MemberUtility.INSTANCE
				.getActiveMemberSearcher();
		authenticationHome = lSearcher.getMemberAuthenticationHome();
		cacheHome = lSearcher.getMemberCacheHome();
	}

	protected Member updateMemberCache(final IMemberInformation inInformation,
			final String inUserID) throws SQLException, VException {
		final Member outMember = cacheHome.updateMemberCache(inInformation);
		final Long lMemberID = new Long(outMember.get(MemberHome.KEY_ID)
				.toString());

		// We have to update the update the roles the external member has. At
		// least, the person should be associated as RoleHome.ROLE_MEMBER.
		final Collection<Integer> lRoles = new Vector<Integer>();
		lRoles.add(RoleHome.ROLE_MEMBER);

		// The first person logging in gets SU role
		if (cacheHome.getCount() == 1) {
			lRoles.add(RoleHome.ROLE_SU);
		}
		linkToRoles(lMemberID, lRoles);

		return outMember;
	}

	private void linkToRoles(final Long inMemberID,
			final Collection<Integer> inRoles) throws VException {
		final LinkMemberRoleHome lLinkHome = BOMHelper.getLinkMemberRoleHome();

		// check the required roles against the existing roles
		final Collection<Role> lExistingRoles = lLinkHome
				.getRolesOf(inMemberID);
		for (final Role lRole : lExistingRoles) {
			inRoles.remove(new Integer(lRole.getElementID()));
		}
		if (inRoles.size() == 0)
			return;

		final String[] lRoles = new String[inRoles.size()];
		int i = 0;
		for (final Integer lRole : inRoles) {
			lRoles[i++] = lRole.toString();
		}
		lLinkHome.associateRoles(inMemberID, lRoles);
	}

	protected void setActorToContext(final Long inMemberID,
			final String inUserID) throws VException {
		final IActorManager lActorManager = MemberUtility.INSTANCE
				.getActorManager();
		if (lActorManager != null) {
			lActorManager.setActorToContext(inMemberID, inUserID);
		} else {
			LOG.warn("Unable to create actor instance because no IActorManager has been registered!");
		}
	}

	/**
	 * The original SU is stored in the cache home only. So, let's check that.
	 * 
	 * @param inUserID
	 *            String
	 * @param inPassword
	 *            String
	 * @param inContext
	 *            VIFContext
	 * @param inException
	 *            InvalidAuthenticationException
	 * @return Member
	 * @throws InvalidAuthenticationException
	 * @throws VException
	 */
	protected Member checkSU(final String inUserID, final String inPassword,
			final InvalidAuthenticationException inException)
			throws InvalidAuthenticationException, VException {
		if (inUserID.trim().length() * inPassword.trim().length() == 0) {
			throw inException;
		}

		return cacheHome.checkAuthentication(inUserID, inPassword);
	}

}
