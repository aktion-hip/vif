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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.code.CodeListNotFoundException;
import org.hip.vif.core.code.Role;

import com.vaadin.data.util.BeanItemContainer;

/**
 * Container (i.e. view model) for the member's roles.
 * 
 * @author Luthiger
 * Created: 20.10.2011
 */
@SuppressWarnings("serial")
public class RoleContainer extends BeanItemContainer<RoleWrapper> {
	private static final String[] SORT_ORDER = new String[] {"elementID"}; //$NON-NLS-1$
	public static final String FIELD_LABEL = "label"; //$NON-NLS-1$
	
	/**
	 * Private constructor.
	 */
	private RoleContainer() {
		super(RoleWrapper.class);
	}
	
	/**
	 * Factory method: Creates and returns an instance of <code>RoleContainer</code>.
	 * 
	 * @param inRoles Collection of {@link Role}
	 * @param inLanguage String
	 * @param inGroupSpecific Collection<String> ids of group specific roles
	 * @return {@link RoleContainer}
	 * @throws CodeListNotFoundException
	 */
	public static RoleContainer createData(Collection<Role> inRoles, String inLanguage, Collection<String> inGroupSpecific) throws CodeListNotFoundException {
		return initialize(getSelected(inRoles), inLanguage, inGroupSpecific);
	}

	/**
	 * Factory method: Creates and returns an empty instance of <code>RoleContainer</code> (i.e. no roles selected).
	 * 
	 * @param inLanguage String 
	 * @param inGroupSpecific Collection<String> ids of group specific roles
	 * @return {@link RoleContainer}
	 * @throws CodeListNotFoundException
	 */
	public static RoleContainer createData(String inLanguage, Collection<String> inGroupSpecific) throws CodeListNotFoundException {
		return initialize(new Vector<String>(), inLanguage, inGroupSpecific);
	}
	
	private static RoleContainer initialize(Collection<String> lSelected, String inLanguage, Collection<String> inGroupSpecific) throws CodeListNotFoundException {
		CodeList lList = CodeListHome.instance().getCodeList(Role.class, inLanguage);
		
		RoleContainer out = new RoleContainer();
		for (String lElementID : lList.getElementIDs()) {
			out.addItem(RoleWrapper.createItem(lElementID, lList.getLabel(lElementID), lSelected.contains(lElementID), inGroupSpecific.contains(lElementID)));
		}
		out.sort(SORT_ORDER, new boolean[] {true});
		return out;
	}
	
	
	private static Collection<String> getSelected(Collection<Role> inRoles) {
		Collection<String> out = new Vector<String>(inRoles.size());
		for (Role lRole : inRoles) {
			out.add(lRole.getElementID());
		}
		return out;
	}
	
	/**
	 * @return List of {@link RoleWrapper} the selected roles
	 */
	public List<RoleWrapper> getSelected() {
		List<RoleWrapper> out = new Vector<RoleWrapper>();
		for (RoleWrapper lRole : getItemIds()) {
			if (lRole.getChecked()) {
				out.add(lRole);
			}
		}
		return out;
	}
	
	/**
	 * @return List of element IDs of the selected roles
	 */
	public Collection<String> getSelectedIDs() {
		Collection<String> out = new Vector<String>();
		for (RoleWrapper lRole : getItemIds()) {
			if (lRole.getChecked()) {
				out.add(lRole.getElementID());
			}
		}
		return out;
	}

	/**
	 * Sets the selected items to this model.
	 * 
	 * @param inSelected
	 */
	@SuppressWarnings("unchecked")
	public void setSelected(Object inSelected) {
		if (inSelected instanceof Collection<?>) {
			Collection<RoleWrapper> lSelected = (Collection<RoleWrapper>) inSelected;
			for (RoleWrapper lRole : lSelected) {
				lRole.setChecked(true);
			}
			for (RoleWrapper lRole : getItemIds()) {
				if (!lSelected.contains(lRole)) {
					lRole.setChecked(false);
				}
			}
		}
	}
	
	/**
	 * @return List of {@link RoleWrapper} the roles that should be displayed disabled
	 */
	public Collection<RoleWrapper> getDisabled() {
		Collection<RoleWrapper> out = new Vector<RoleWrapper>();
		for (RoleWrapper lRole : getItemIds()) {
			if (lRole.getGroupSpecific()) {
				out.add(lRole);
			}
		}
		return out;
	}
	
	/**
	 * Check the roles for the actual state concerning selected roles. 
	 * 
	 * @return boolean <code>true</code> if at least one role is selected, else <code>false</code>
	 */
	public boolean hasChecked() {
		for (RoleWrapper lRole : getItemIds()) {
			if (lRole.getChecked()) {
				return true;
			}
		}
		return false;
	}
	
}
