/*
	This package is part of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.vif.core.authorization;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Base class for member authorization.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.authorization.IAuthorization
 */
@SuppressWarnings("serial")
public abstract class AbstractVIFAuthorization implements IAuthorization, Serializable {
	public final static Long GUEST_ID = new Long(-1);
	
	private Collection<String> processedRoles = new Vector<String>();
	private Collection<String> processedPermissions = new Vector<String>();
	private String rolesXML = "";
	private String permissionsXML = "";
	
	protected boolean checkRole(String inRoleID) {
		if ("".equals(inRoleID)) return false;
		return processedRoles.contains(inRoleID);
	}
	
	/**
	 * Does this authorized person have the specified permission?
	 * 
	 * @param inPermissionLabel java.lang.String
	 * @return boolean
	 */
	public boolean hasPermission(String inPermissionLabel) {
		if ("".equals(inPermissionLabel)) return true;
		return processedPermissions.contains(inPermissionLabel);
	}
	
	protected void addRole(String inRoleID, String inLabel, String inSpecific) {
		processedRoles.add(inRoleID);
		rolesXML += "<Role roleID=\"" + inRoleID + "\" specific=\"" + inSpecific + "\">" + inLabel + "</Role>\n";
	}
	
	protected void addPermission(String inLabel, String inPermissionID) {
		processedPermissions.add(inLabel);
		permissionsXML += "<Permission permissionID=\"" + inPermissionID + "\">" + inLabel + "</Permission>\n";
	}
	
	/**
	 * Checks the authorization against the specified roles.
	 * 
	 * @param inRoleIDs java.lang.String Array of role ids.
	 * @return boolean <code>true</code> if the actor has one of the specified roles. If <code>inRoleIDs.length == 0</code>, <code>true</code> is returned. 
	 */
	public boolean checkRoles(String[] inRoleIDs) {
		//pre
		if (inRoleIDs == null) return false;
		if (inRoleIDs.length == 0) return true;
		
		for (int i = 0; i < inRoleIDs.length; i++)
			if (checkRole(inRoleIDs[i])) return true;
			
		return false;
	}
		
	/**
	 * Checks the authorization against the specified permissions.
	 * 
	 * @param inPermissionLabels java.lang.String Array of permission labels.
	 * @return boolean
	 */
	public boolean checkPermissions(String[] inPermissionLabels) {
		//pre
		if (inPermissionLabels == null) return false;
		if (inPermissionLabels.length == 0) return true;
		
		for (int i = 0; i < inPermissionLabels.length; i++)
			if (hasPermission(inPermissionLabels[i])) return true;
			
		return false;
	}
	
	/**
	 * Returns the Authorization as XML serialized.
	 * 
	 * @return java.lang.String
	 */
	public String toXML() {
		return "<Authorization>\n" +
			"<Roles>\n" + rolesXML + "</Roles>\n" + 
			"<Permissions>\n" + permissionsXML + "</Permissions>\n" + 
			"</Authorization>";
	}
	
	/**
	 * Returns the roles the user has permission for.
	 * 
	 * @return Collection<String> Array of roles.
	 * @see org.hip.vif.servlets.VIFContext#ROLE_ID_SU etc.
	 */
	public Collection<String> getPermittedRoles() {
		return processedRoles;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.authorization.Authorization#getPermissions()
	 */
	public Collection<String> getPermissions() {
		return Collections.unmodifiableCollection(processedPermissions);
	}
		
	/**
	 * Compares this object against the specified object.
	 * 
	 * @param inObject java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (getClass() != inObject.getClass()) return false;
		
		AbstractVIFAuthorization lToCompare = (AbstractVIFAuthorization)inObject;
		return equalCollections(getPermittedRoles(), lToCompare.getPermittedRoles()) &&
			equalCollections(getPermissions(), lToCompare.getPermissions());
	}
	
	/**
	 * Returns this object's hash code.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return processedPermissions.hashCode() ^ processedRoles.hashCode();
	}

	private boolean equalCollections(Collection<String> inCollection1, Collection<String> inCollection2) {
		if (inCollection1.size() != inCollection2.size()) return false;
		return inCollection1.containsAll(inCollection2);
	}	
}
