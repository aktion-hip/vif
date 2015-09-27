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
import java.util.Dictionary;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkPermissionRoleHome;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.member.IActor;
import org.hip.vif.web.Constants;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/** Helper class for all issues concerning OSGi roles and user amdin.
 *
 * @author lbenno */
public final class RoleHelper {

    private RoleHelper() {
        // prevent instantiation
    }

    /** Create OSGi roles and permissions for the VIF application.
     *
     * @param inUserAdmin
     * @throws SQLException
     * @throws VException */
    @SuppressWarnings("unchecked")
    public static void createRolesAndPermissions(final UserAdmin inUserAdmin) throws SQLException, VException {
        // first we create the roles as top level OSGi groups
        if (createVIFRoles(inUserAdmin)) {
            // leave, if roles and permissions are already initialized
            return;
        }

        // then we create the permissions as second level OSGi groups
        final LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
        final QueryResult lPermissions = BOMHelper.getPermissionHome().select();
        while (lPermissions.hasMoreElements()) {
            final GeneralDomainObject lPermissionBOM = lPermissions.next();
            // create the permission group, identified by the permission label
            final Group lPermission = (Group) inUserAdmin.createRole(
                    (String) lPermissionBOM.get(PermissionHome.KEY_LABEL), org.osgi.service.useradmin.Role.GROUP);
            if (lPermission != null) {
                final Dictionary<String, String> lProperties = lPermission.getProperties();
                lProperties.put(Constants.PERMISSION_DESCRIPTION_KEY,
                        (String) lPermissionBOM.get(PermissionHome.KEY_DESCRIPTION));
                // retrieve the roles the permission is linked to
                final Collection<Role> lRoles = lLinkHome.getRolesOf(Long.parseLong(lPermissionBOM.get(
                        PermissionHome.KEY_ID).toString()));
                addRoles(inUserAdmin, lPermission, lRoles);
            }
        }
    }

    /** We link the permission groups as members of the role groups.
     *
     * @param inUserAdmin {@link UserAdmin}
     * @param inPermission {@link Group} the permission group to link to the role group
     * @param inRoles Collection of VIF roles the permission group has to be added as member */
    private static void addRoles(final UserAdmin inUserAdmin, final Group inPermission,
            final Collection<Role> inRoles) {
        for (final Role lRole : inRoles) {
            final org.osgi.service.useradmin.Role lOSGiRole = inUserAdmin.getRole(lRole.getElementID());
            if (lOSGiRole != null) {
                inPermission.addMember(lOSGiRole);
            }
        }
    }

    /** Method to refresh the permissions, e.g. after the su changed the settings linking permissions to roles.
     *
     *
     * @param inUserAdmin {@link UserAdmin}
     * @throws VException
     * @throws SQLException */
    @SuppressWarnings("unchecked")
    public static void refreshPermissions(final UserAdmin inUserAdmin) throws VException, SQLException {
        final LinkPermissionRoleHome lLinkHome = BOMHelper.getLinkPermissionRoleHome();
        final QueryResult lPermissions = BOMHelper.getPermissionHome().select();
        while (lPermissions.hasMoreElements()) {
            final GeneralDomainObject lPermissionBOM = lPermissions.next();
            // create the permission group, identified by the permission label
            final Group lPermission = (Group) inUserAdmin
                    .getRole((String) lPermissionBOM.get(PermissionHome.KEY_LABEL));
            if (lPermission != null) {
                final Dictionary<String, String> lProperties = lPermission.getProperties();
                lProperties.put(Constants.PERMISSION_DESCRIPTION_KEY,
                        (String) lPermissionBOM.get(PermissionHome.KEY_DESCRIPTION));
                // retrieve the roles the permission is linked to
                final Collection<Role> lRoles = lLinkHome.getRolesOf(Long.parseLong(lPermissionBOM.get(
                        PermissionHome.KEY_ID).toString()));
                final Collection<org.osgi.service.useradmin.Role> lRolesNew = getRolesNew(lRoles, inUserAdmin);
                final org.osgi.service.useradmin.Role[] lRolesOld = lPermission.getMembers();
                if (lRolesOld != null) {
                    for (final org.osgi.service.useradmin.Role lRoleOld : lRolesOld) {
                        // if the new role is an old role, we ignore it
                        if (lRolesNew.contains(lRoleOld)) {
                            lRolesNew.remove(lRoleOld);
                        }
                        // else, the old role has to be removed
                        else {
                            lPermission.removeMember(lRoleOld);
                        }
                    }
                }
                // finally, add the remaining new roles
                for (final org.osgi.service.useradmin.Role lRoleNew : lRolesNew) {
                    lPermission.addMember(lRoleNew);
                }

            }
        }
    }

    private static Collection<org.osgi.service.useradmin.Role> getRolesNew(final Collection<Role> inRoles,
            final UserAdmin inUserAdmin) {
        final Collection<org.osgi.service.useradmin.Role> out = new ArrayList<org.osgi.service.useradmin.Role>(
                inRoles.size());
        for (final Role lRole : inRoles) {
            out.add(inUserAdmin.getRole(lRole.getElementID()));
        }
        return out;
    }

    /** Create the VIF roles as top level OSGi groups.<br />
     * Note: The role groups are identified by the <code>CodeID</code>.
     *
     * @param inUserAdmin {@link UserAdmin}
     * @return boolean <code>true</code> if the roles and permissions are already initialized
     * @throws SQLException
     * @throws VException */
    private static boolean createVIFRoles(final UserAdmin inUserAdmin)
            throws SQLException, VException {
        final QueryResult lRoles = BOMHelper.getRoleHome().select();
        boolean lDoCheck = true;
        while (lRoles.hasMoreElements()) {
            final String lGroupName = (String) lRoles.nextAsDomainObject().get(
                    RoleHome.KEY_CODE_ID);
            if (lDoCheck && inUserAdmin.getRole(lGroupName) != null) {
                return true;
            }
            lDoCheck = false;
            inUserAdmin.createRole(lGroupName, org.osgi.service.useradmin.Role.GROUP);

        }
        return false;
    }

    /** Creates a OSGi user object for the specified VIF actor and assigns it the OSGi roles according to the user's VIF
     * roles.
     *
     * @param inActor {@link IActor} the VIF actor object
     * @param inUserAdmin {@link UserAdmin}
     * @throws VException
     * @throws SQLException */
    @SuppressWarnings("unchecked")
    public static void mapUserToRole(final IActor inActor,
            final UserAdmin inUserAdmin) throws VException, SQLException {
        // create user
        final String lUserId = inActor.getUserID();
        User lUser = (User) inUserAdmin.getRole(lUserId);
        if (lUser == null) {
            lUser = (User) inUserAdmin.createRole(lUserId,
                    org.osgi.service.useradmin.Role.USER);
        }
        if (lUser == null) {
            throw new VException(String.format("Could not get OSGi user object for '%s'!", lUserId));
        }
        lUser.getProperties().put(ApplicationConstants.VIF_USER, lUserId);

        // map to group
        final Collection<String> lRoleIds = getRoleIds(inActor.getActorID());
        final QueryResult lRoles = BOMHelper.getRoleHome().select();
        while (lRoles.hasMoreElements()) {
            final GeneralDomainObject lRole = lRoles.nextAsDomainObject();
            if (lRoleIds.contains(lRole.get(RoleHome.KEY_CODE_ID))) {
                final Group lGroup = (Group) inUserAdmin.getRole((String) lRole
                        .get(RoleHome.KEY_CODE_ID));
                if (lGroup != null) {
                    lGroup.addMember(lUser);
                }
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
