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

package org.hip.vif.admin.member.data;


/**
 * Adapter for instances of <code>Role</code>s.
 * 
 * @author Luthiger
 * Created: 20.10.2011
 * @see org.hip.vif.core.code.Role
 */
public class RoleWrapper {

	private String elementID;
	private String label;
	private boolean checked;
	private boolean isGroupSpecific;

	/**
	 * Private constructor.
	 * 
	 * @param inElementID
	 * @param inLabel
	 * @param inChecked
	 * @param inGroupSpecific
	 */
	private RoleWrapper(String inElementID, String inLabel, boolean inChecked, boolean inGroupSpecific) {
		elementID = inElementID;
		label = inLabel;
		checked = inChecked;
		isGroupSpecific = inGroupSpecific;
	}
	
	/**
	 * Factory method, instance creation.
	 * 
	 * @param inElementID String the key
	 * @param inLabel String
	 * @param inChecked boolean
	 * @param inGroupSpecific boolean
	 * @return {@link RoleWrapper}
	 */
	public static RoleWrapper createItem(String inElementID, String inLabel, boolean inChecked, boolean inGroupSpecific) {
		return new RoleWrapper(inElementID, inLabel, inChecked, inGroupSpecific);
	}
	
	public String getElementID() {
		return elementID;
	}
	
	public boolean getGroupSpecific() {
		return isGroupSpecific;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
}
