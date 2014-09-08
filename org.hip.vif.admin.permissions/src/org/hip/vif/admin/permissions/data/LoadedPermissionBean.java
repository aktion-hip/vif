/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.admin.permissions.data;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.AlternativeModel;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.PermissionHome;
import org.hip.vif.core.bom.RoleHome;
import org.hip.vif.core.bom.impl.LinkPermissionRoleAlternate;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.util.BeanWrapperHelper;

/**
 * This permission bean class is loaded (i.e. initialized) with the information 
 * about the roles that are linked to the permission.<br />
 * If the role has the permission, the role's value is <code>true</code>.
 * 
 * @author Luthiger
 * Created: 15.12.2011
 */
public class LoadedPermissionBean implements ISelectableBean {
	public enum Role {
		SU(RoleHome.ROLE_SU), ADMIN(RoleHome.ROLE_ADMIN), GROUP_ADMIN(RoleHome.ROLE_GROUP_ADMIN), PARTICIPANT(RoleHome.ROLE_PARTICIPANT), MEMBER(RoleHome.ROLE_MEMBER), GUEST(RoleHome.ROLE_GUEST), EXCLUDED(RoleHome.ROLE_EXCLUDED);
		
		private int id;
		Role(int inID) {
			id = inID;
		}
		public int getID() {
			return id;
		}
	}
	
	private Long permissionID;
	private String label;
	private String description;
	private boolean checked = false;
	private boolean dirty = false;
	
	//roles
	private boolean su = false;
	private boolean admin = false;
	private boolean groupAdmin = false;
	private boolean participant = false;
	private boolean member = false;
	private boolean guest = false;
	private boolean excluded = false;

	/**
	 * Private constructor.
	 * 
	 * @param inPermissionID Long
	 * @param inPermission {@link GeneralDomainObject}
	 * @param inAssociations {@link Collection} of <code>AlternativeModel</code>
	 */
	private LoadedPermissionBean(Long inPermissionID, GeneralDomainObject inPermission, Collection<AlternativeModel> inAssociations) {
		permissionID = inPermissionID;
		label = BeanWrapperHelper.getString(PermissionHome.KEY_LABEL, inPermission);
		description = BeanWrapperHelper.getString(PermissionHome.KEY_DESCRIPTION, inPermission);

		for (AlternativeModel lAssociation : inAssociations) {
			roleMapping((LinkPermissionRoleAlternate) lAssociation);
		}
	}

	private void roleMapping(LinkPermissionRoleAlternate inAssociation) {
		int lRoleID = (int)inAssociation.getRoleID();
		switch (lRoleID) {
		case RoleHome.ROLE_SU:
			su = true;
			break;
		case RoleHome.ROLE_ADMIN:
			admin = true;
			break;
		case RoleHome.ROLE_GROUP_ADMIN:
			groupAdmin = true;
			break;
		case RoleHome.ROLE_PARTICIPANT:
			participant = true;
			break;
		case RoleHome.ROLE_MEMBER:
			member = true;
			break;
		case RoleHome.ROLE_GUEST:
			guest = true;
			break;
		case RoleHome.ROLE_EXCLUDED:
			excluded = true;
			break;
		}
	}

	/**
	 * Factory method, creates a bean instance.
	 * 
	 * @param inPermission {@link GeneralDomainObject}
	 * @param inAssociations {@link Collection} of <code>AlternativeModel</code>
	 * @return {@link LoadedPermissionBean}
	 */
	public static LoadedPermissionBean createItem(GeneralDomainObject inPermission, Collection<AlternativeModel> inAssociations) {
		Long lID = BeanWrapperHelper.getLong(PermissionHome.KEY_ID, inPermission);
		LoadedPermissionBean out = new LoadedPermissionBean(lID, inPermission, getAssociated(BeanWrapperHelper.getLong(PermissionHome.KEY_ID, inPermission), inAssociations));
		return out;
	}
	
	private static Collection<AlternativeModel> getAssociated(Long inPermissionID, Collection<AlternativeModel> inAssociations) {
		Collection<AlternativeModel> outAssociated = new Vector<AlternativeModel>();
		for (AlternativeModel lAssociation : inAssociations) {
			if (((LinkPermissionRoleAlternate)lAssociation).isAssociatedToPermission(inPermissionID)) {
				outAssociated.add(lAssociation);
			}
		}
		return outAssociated;
	}
	
	//getters and setters

	public Long getPermissionID() {
		return permissionID;
	}

	public void setPermissionID(Long inPermissionID) {
		permissionID = inPermissionID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String inLabel) {
		label = inLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String inDescription) {
		description = inDescription;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	//generic role setter and getter
	/**
	 * Sets the value of the specified role: <code>true</code> is associating the permission with the specified role, 
	 * <code>false</code> is removing such an association.
	 * 
	 * @param inRole {@link Role}
	 * @param inIsChecked boolean
	 */
	public void setRoleValue(Role inRole, boolean inIsChecked) {
		dirty = true;
		
		switch (inRole) {
		case SU:
			su = inIsChecked;
			break;
		case ADMIN:
			admin = inIsChecked;
			break;
		case GROUP_ADMIN:
			groupAdmin = inIsChecked;
			break;
		case PARTICIPANT:
			participant = inIsChecked;
			break;
		case MEMBER:
			member = inIsChecked;
			break;
		case GUEST:
			guest = inIsChecked;
			break;
		case EXCLUDED:
			excluded = inIsChecked;
			break;
		}
	}
	
	/**
	 * Returns the association value.
	 * 
	 * @param inRole {@link Role}
	 * @return boolean <code>true</code> if this permission is associated with the specified role, <code>false</code> if not
	 */
	public boolean getRoleValue(Role inRole) {
		switch (inRole) {
		case SU:
			return su;
		case ADMIN:
			return admin;
		case GROUP_ADMIN:
			return groupAdmin;
		case PARTICIPANT:
			return participant;
		case MEMBER:
			return member;
		case GUEST:
			return guest;
		case EXCLUDED:
			return excluded;
		}
		return false;
	}

}
