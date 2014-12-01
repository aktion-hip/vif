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

package org.hip.vif.forum.member.tasks;

import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.RatingsHelper;
import org.hip.vif.forum.member.Constants;
import org.hip.vif.forum.member.ui.EditPersonalDataView;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/** Shows the personal data for that it can be edited.
 *
 * @author Luthiger Created: 06.10.2011 */
@UseCaseController
public class PersonalDataEditTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(PersonalDataEditTask.class);

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_DATA;
    }

    @Override
    protected Component runChecked() throws VIFWebException {
        loadContextMenu(Constants.MENU_SET_ID_EDIT_PWRD);
        try {
            final Long lActorID = getActor().getActorID();
            return new EditPersonalDataView(BOMHelper.getMemberCacheHome().getMember(lActorID),
                    new RatingsHelper(lActorID), this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Persist the changes made to the member data.
     *
     * @param inMember {@link Member}
     * @return boolean <code>true</code> if the data has been saved successfully */
    public boolean saveMember(final Member inMember) {
        try {
            inMember.ucSave(getActor().getActorID());
            return true;
        } catch (final BOMChangeValueException exc) {
            LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
        }
        return false;
    }

}
