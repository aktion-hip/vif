/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.member.IActor;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/**
 * Helper class for all issues concerning OSGi roles and user amdin.
 * 
 * @author lbenno
 */
public final class RoleHelper {

	private RoleHelper() {
		// prevent instatiation
	}

	/**
	 * Creates OSGi roles for the VIF application.
	 * 
	 * @param inUserAdmin
	 *            {@link UserAdmin}
	 * @throws SQLException
	 * @throws VException
	 */
	public static void createVIFRoles(final UserAdmin inUserAdmin)
			throws SQLException, VException {
		final QueryResult lRoles = BOMHelper.getRoleHome().select();
		while (lRoles.hasMoreElements()) {
			final String lGroupName = (String) lRoles.nextAsDomainObject().get(
					RoleHome.KEY_DESCRIPTION);
			final Group lGroup = (Group) inUserAdmin.createRole(lGroupName,
					org.osgi.service.useradmin.Role.GROUP);
			lGroup.addMember(inUserAdmin
					.getRole(org.osgi.service.useradmin.Role.USER_ANYONE));
		}
	}

	/**
	 * Creates a OSGi user object for the specified VIF actor and assigns it the
	 * OSGi roles according to the user's VIF roles.
	 * 
	 * @param inActor
	 *            {@link IActor} the VIF actor object
	 * @param inUserAdmin
	 *            {@link UserAdmin}
	 * @throws VException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static void mapUserToRole(final IActor inActor,
			final UserAdmin inUserAdmin) throws VException, SQLException {
		// create user
		final String lUserId = inActor.getUserID();
		final User lUser = (User) inUserAdmin.createRole(lUserId,
				org.osgi.service.useradmin.Role.USER);
		lUser.getProperties().put("vif.user", lUserId);

		// map to group
		final Collection<String> lRoleIds = getRoleIds(inActor.getActorID());
		final QueryResult lRoles = BOMHelper.getRoleHome().select();
		while (lRoles.hasMoreElements()) {
			final GeneralDomainObject lRole = lRoles.nextAsDomainObject();
			final Group lGroup = (Group) inUserAdmin.getRole((String) lRole
					.get(RoleHome.KEY_DESCRIPTION));
			if (lGroup != null
					&& lRoleIds.contains(lRole.get(RoleHome.KEY_CODE_ID))) {
				lGroup.addRequiredMember(lUser);
			}
		}

	}

	private static Collection<String> getRoleIds(final Long inMemberID)
			throws VException {
		final Collection<Role> lRoles = BOMHelper.getLinkMemberRoleHome()
				.getRolesOf(inMemberID);
		final Collection<String> out = new ArrayList<String>(lRoles.size());
		for (final Role lRole : lRoles) {
			out.add(lRole.getElementID());
		}
		return out;
	}

}
