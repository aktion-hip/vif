/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
package org.hip.vif.core.bom;

import java.util.Collection;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.vif.core.code.Role;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.RolesCheck;

/** LinkMemberRoleHome is responsible to manage instances of class org.hip.vif.bom.LinkMemberRole.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.LinkMemberRole */
public interface LinkMemberRoleHome extends DomainObjectHome {
    public final static String KEY_MEMBER_ID = "MemberID";
    public final static String KEY_ROLE_ID = "RoleID";
    public final static Integer ROLE_GROUP_ADMIN = new Integer(3);
    public final static Integer ROLE_PARTICIPANT = new Integer(4);

    /** Returns all roles associated with the specified member
     * 
     * @param inMemberID java.lang.Long ID of the members entry
     * @return Collection<Role> A collection containing the associated roles
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    Collection<Role> getRolesOf(Long inMemberID) throws BOMInvalidKeyException;

    /** Checkes whether the associated roles have changed by comparing the old roles with the new ones.
     * 
     * @param inMemberID java.lang.Long ID of the members entry
     * @param inNewRoles java.lang.String[] Array containing the IDs of the new roles
     * @return org.hip.vif.util.RolesCheck
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    RolesCheck checkRolesOf(Long inMemberID, String[] inNewRoles) throws BOMInvalidKeyException;

    RolesCheck checkRolesOf(Long inMemberID, Collection<String> inNewRoles) throws BOMInvalidKeyException;

    /** Associates the indicated roles to the specified member
     * 
     * @param inMemberID java.lang.Long
     * @param inRoles Object[]
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    void associateRoles(Long inMemberID, Object[] inRoles) throws BOMChangeValueException;

    void associateRoles(Long inMemberID, Collection<String> inRoles) throws BOMChangeValueException;

    /** Delete all roles associated to the specified member
     * 
     * @param inMemberID java.lang.Long
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    void deleteRolesOf(Long inMemberID) throws BOMChangeValueException;

    /** Creates a GroupAdmin Role for the specified member
     * 
     * @param inMemberID Long
     * @throws BOMChangeValueException */
    void createGroupAdminRole(Long inMemberID) throws BOMChangeValueException;

    /** Creates a Participant Role for the specified member
     * 
     * @param inMemberID Long
     * @throws BOMChangeValueException */
    void createParticipantRole(Long inMemberID) throws BOMChangeValueException;

    /** Deletes the group admin role of the specified member.
     * 
     * @param inMemberID Long
     * @throws BOMChangeValueException */
    void deleteGroupAdminRole(Long inMemberID) throws BOMChangeValueException;

    /** Deletes the participant role of the specified member.
     * 
     * @param inMemberID Long
     * @throws BOMChangeValueException */
    void deleteParticipantRole(Long inMemberID) throws BOMChangeValueException;

    /** Checks whether the specified member has the PARTICIPANT role.
     * 
     * @param inMemberID Long
     * @return boolean true, if the specified member is PARTICIPANT
     * @throws BOMChangeValueException */
    boolean hasRoleParticipant(Long inMemberID) throws BOMChangeValueException;

    /** Checks whether the specified member has the GROUP_ADMIN role.
     * 
     * @param inMemberID Long
     * @return boolean true, if the specified member is GROUP_ADMIN
     * @throws BOMChangeValueException */
    boolean hasRoleGroupAdmin(Long inMemberID) throws BOMChangeValueException;

    /** Updates the specified member's roles.
     * 
     * @param inMemberID Long
     * @param inRoles {@link Collection} of the roles' id selected for the member
     * @return boolean <code>true</code> if the member's new roles are different from the old ones
     * @throws BOMChangeValueException */
    boolean updateRoles(Long inMemberID, Collection<String> inRoles) throws BOMChangeValueException;

}
