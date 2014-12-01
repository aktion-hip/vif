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

package org.hip.vif.admin.member.tasks;

import org.hip.vif.admin.member.internal.LookupMemberComponent;
import org.hip.vif.admin.member.ui.MemberView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.web.member.RoleContainer;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to prepare the view to display the member's data.<br />
 * This task is part of the member lookup.
 *
 * @author Luthiger Created: 26.10.2011
 * @see LookupMemberComponent */
@UseCaseController
public class MemberViewTask extends AbstractWebController {

    @Override
    protected String needsPermission() {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            final Long lMemberID = getModelIdFromParameter();
            final Member lMember = BOMHelper.getMemberCacheHome().getMember(lMemberID);
            return new MemberView(lMember,
                    RoleContainer.createData(BOMHelper.getLinkMemberRoleHome().getRolesOf(lMemberID), getAppLocale()
                            .getLanguage(), BOMHelper.getRoleHome().getGroupSpecificIDs()),
                    new RatingsHelper(lMemberID));
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

}
