/**
	This package is part of the application VIF.
	Copyright (C) 2011-2015, Benno Luthiger

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
package org.hip.vif.admin.admin.tasks;

import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.vif.admin.admin.Constants;
import org.hip.vif.admin.admin.data.GroupContainer;
import org.hip.vif.admin.admin.print.GroupExtent;
import org.hip.vif.admin.admin.ui.PrintGroupView;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to display the form where the group admin can start the print out of selected groups.
 *
 * @author Luthiger Created: 30.12.2011 */
@UseCaseController
public class PrintGroupTask extends SendMailTask {

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_PRINT_GROUP;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        emptyContextMenu();
        final GroupHome lGroupHome = VifBOMHelper.getGroupHome();
        try {
            final Long lActorID = getActor().getActorID();
            return new PrintGroupView(
                    GroupContainer.createData(lGroupHome
                            .selectForAdministration(lActorID, createOrder()),
                            isAdmin(lActorID)),
                    this);
        } catch (final Exception exc) { // NOPMD
            throw createContactAdminException(exc);
        }
    }

    /** Helper method: returns a collection of all groups.
     *
     * @return Collection&lt;GroupExtent>
     * @throws Exception */
    public Collection<GroupExtent> getAllGroups() throws Exception { // NOPMD
        final Collection<GroupExtent> outGroups = new ArrayList<GroupExtent>();
        final QueryResult lGroups = VifBOMHelper
                .getGroupHome()
                .selectForAdministration(getActor().getActorID(), createOrder());
        while (lGroups.hasMoreElements()) {
            outGroups.add(new GroupExtent(BeanWrapperHelper.getLong(
                    GroupHome.KEY_ID, lGroups.next())));
        }
        return outGroups;
    }

}
