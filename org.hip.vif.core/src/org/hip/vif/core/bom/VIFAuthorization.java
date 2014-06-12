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
package org.hip.vif.core.bom;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.authorization.AbstractVIFAuthorization;
import org.hip.vif.core.bom.impl.JoinMemberToPermission;
import org.hip.vif.core.bom.impl.JoinMemberToPermissionHome;
import org.hip.vif.core.bom.impl.JoinMemberToRole;
import org.hip.vif.core.bom.impl.JoinMemberToRoleHome;
import org.hip.vif.core.bom.impl.JoinRoleToPermission;
import org.hip.vif.core.bom.impl.JoinRoleToPermissionHome;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This class holds a member's authorization data.
 *
 * @author Luthiger
 * Created: 25.11.2007
 */
@SuppressWarnings("serial")
public class VIFAuthorization extends AbstractVIFAuthorization {
	private final static int GUEST_ROLE_ID = 6;

	/**
	 * VIFAuthorization constructor.
	 * 
	 * @param inMemberID Long The id of the member entry.
	 * @throws BOMChangeValueException
	 */
	public VIFAuthorization(Long inMemberID) throws BOMChangeValueException {
		init(inMemberID);
	}

	private void init(Long inMemberID) throws BOMChangeValueException {
		try {
			if (GUEST_ID.equals(inMemberID)) {
				//create permissions for guest (RoleID = 6)
				Integer lRoleID = new Integer(GUEST_ROLE_ID);

				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RoleHome.KEY_ID, lRoleID);
				
				RoleHome lRoleHome = (RoleHome)BOMHelper.getRoleHome();
				Role lRole = (Role)lRoleHome.findByKey(lKey);
				addRole(lRoleID.toString(), (String)lRole.get(RoleHome.KEY_DESCRIPTION), String.valueOf(lRole.get(RoleHome.KEY_GROUP_SPECIFIC)));
				
				lKey = new KeyObjectImpl();
				lKey.setValue(LinkPermissionRoleHome.KEY_ROLE_ID, lRoleID);
				JoinRoleToPermissionHome lJoinHome = (JoinRoleToPermissionHome)BOMHelper.getJoinRoleToPermissionHome();
				QueryResult lSelection = lJoinHome.select(lKey);
				while (lSelection.hasMoreElements()) {
					JoinRoleToPermission lPermission = (JoinRoleToPermission)lSelection.nextAsDomainObject();
					String lPermissionLabel = (String)lPermission.get(JoinRoleToPermissionHome.KEY_ALIAS_PERMISSION_LABEL);
					if (!hasPermission(lPermissionLabel))
						addPermission(lPermissionLabel, String.valueOf(lPermission.get(JoinRoleToPermissionHome.KEY_ALIAS_PERMISSION_ID)));
				}
			}
			else {
				//normal member
				JoinMemberToPermissionHome lToPermissionHome = (JoinMemberToPermissionHome)BOMHelper.getJoinMemberToPermissionHome();
				int i = 0;
				QueryResult lSelection = lToPermissionHome.select(inMemberID);
				while (lSelection.hasMoreElements()) {
					i++;
					JoinMemberToPermission lPermission = (JoinMemberToPermission)lSelection.next();
					String lRoleID = (String)lPermission.get(JoinMemberToPermissionHome.KEY_ALIAS_ROLE_ID);
					if (!checkRole(lRoleID))
						addRole(lRoleID, (String)lPermission.get(RoleHome.KEY_DESCRIPTION), String.valueOf(lPermission.get(RoleHome.KEY_GROUP_SPECIFIC)));
						
					String lPermissionLabel = (String)lPermission.get(JoinMemberToPermissionHome.KEY_ALIAS_PERMISSION_LABEL);
					if (!hasPermission(lPermissionLabel))
						addPermission(lPermissionLabel, String.valueOf(lPermission.get(JoinMemberToPermissionHome.KEY_ALIAS_PERMISSION_ID)));
				}
				
				//if there are no permissions defined yet, add at least roles
				if (i==0) {
					JoinMemberToRoleHome lToRoleHome = (JoinMemberToRoleHome)BOMHelper.getJoinMemberToRoleHome();
					lSelection = lToRoleHome.select(inMemberID);
					while (lSelection.hasMoreElements()) {
						JoinMemberToRole lRole = (JoinMemberToRole)lSelection.next();
						String lRoleID = (String)lRole.get(JoinMemberToRoleHome.KEY_ALIAS_ROLE_ID);
						if (!checkRole(lRoleID))
							addRole(lRoleID, (String)lRole.get(RoleHome.KEY_DESCRIPTION), String.valueOf(lRole.get(RoleHome.KEY_GROUP_SPECIFIC)));
					}
				}
			}
		}
		catch (VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

}
