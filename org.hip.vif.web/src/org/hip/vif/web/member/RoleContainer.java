/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.web.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.code.CodeListNotFoundException;
import org.hip.vif.core.code.Role;

import com.vaadin.data.util.BeanItemContainer;

/** Container (i.e. view model) for the member's roles.
 *
 * @author Luthiger Created: 20.10.2011 */
@SuppressWarnings("serial")
public class RoleContainer extends BeanItemContainer<RoleWrapper> {
    private static final String[] SORT_ORDER = new String[] { "elementID" }; //$NON-NLS-1$
    public static final String FIELD_LABEL = "label"; //$NON-NLS-1$

    /** Private constructor. */
    private RoleContainer() {
        super(RoleWrapper.class);
    }

    /** Factory method: Creates and returns an instance of <code>RoleContainer</code>.
     *
     * @param inRoles Collection of {@link Role}
     * @param inLanguage String
     * @param inGroupSpecific Collection<String> ids of group specific roles
     * @return {@link RoleContainer}
     * @throws CodeListNotFoundException */
    public static RoleContainer createData(final Collection<Role> inRoles, final String inLanguage,
            final Collection<String> inGroupSpecific) throws CodeListNotFoundException {
        return initialize(getSelected(inRoles), inLanguage, inGroupSpecific);
    }

    /** Factory method: Creates and returns an empty instance of <code>RoleContainer</code> (i.e. no roles selected).
     *
     * @param inLanguage String
     * @param inGroupSpecific Collection<String> ids of group specific roles
     * @return {@link RoleContainer}
     * @throws CodeListNotFoundException */
    public static RoleContainer createData(final String inLanguage, final Collection<String> inGroupSpecific)
            throws CodeListNotFoundException {
        return initialize(new ArrayList<String>(), inLanguage, inGroupSpecific);
    }

    private static RoleContainer initialize(final Collection<String> inSelected, final String inLanguage,
            final Collection<String> inGroupSpecific) throws CodeListNotFoundException {
        final CodeList lList = CodeListHome.instance().getCodeList(Role.class, inLanguage);

        final RoleContainer out = new RoleContainer();
        for (final String lElementID : lList.getElementIDs()) {
            out.addItem(RoleWrapper.createItem(lElementID, lList.getLabel(lElementID), inSelected.contains(lElementID),
                    inGroupSpecific.contains(lElementID)));
        }
        out.sort(SORT_ORDER, new boolean[] { true });
        return out;
    }

    private static Collection<String> getSelected(final Collection<Role> inRoles) {
        final Collection<String> out = new ArrayList<String>(inRoles.size());
        for (final Role lRole : inRoles) {
            out.add(lRole.getElementID());
        }
        return out;
    }

    /** @return List of {@link RoleWrapper} the selected roles */
    public List<RoleWrapper> getSelected() {
        final List<RoleWrapper> out = new ArrayList<RoleWrapper>();
        for (final RoleWrapper lRole : getItemIds()) {
            if (lRole.getChecked()) {
                out.add(lRole);
            }
        }
        return out;
    }

    /** @return List of element IDs of the selected roles */
    public Collection<String> getSelectedIDs() {
        final Collection<String> out = new ArrayList<String>();
        for (final RoleWrapper lRole : getItemIds()) {
            if (lRole.getChecked()) {
                out.add(lRole.getElementID());
            }
        }
        return out;
    }

    /** Sets the selected items to this model.
     *
     * @param inSelected */
    @SuppressWarnings("unchecked")
    public void setSelected(final Object inSelected) {
        if (inSelected instanceof Collection<?>) {
            final Collection<RoleWrapper> lSelected = (Collection<RoleWrapper>) inSelected;
            for (final RoleWrapper lRole : lSelected) {
                lRole.setChecked(true);
            }
            for (final RoleWrapper lRole : getItemIds()) {
                if (!lSelected.contains(lRole)) {
                    lRole.setChecked(false);
                }
            }
        }
    }

    /** @return List of {@link RoleWrapper} the roles that should be displayed disabled */
    public Collection<RoleWrapper> getDisabled() {
        final Collection<RoleWrapper> out = new ArrayList<RoleWrapper>();
        for (final RoleWrapper lRole : getItemIds()) {
            if (lRole.getGroupSpecific()) {
                out.add(lRole);
            }
        }
        return out;
    }

    /** Check the roles for the actual state concerning selected roles.
     *
     * @return boolean <code>true</code> if at least one role is selected, else <code>false</code> */
    public boolean hasChecked() {
        for (final RoleWrapper lRole : getItemIds()) {
            if (lRole.getChecked()) {
                return true;
            }
        }
        return false;
    }

}
