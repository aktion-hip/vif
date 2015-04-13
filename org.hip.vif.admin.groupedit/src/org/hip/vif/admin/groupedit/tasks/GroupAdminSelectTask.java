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

package org.hip.vif.admin.groupedit.tasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.member.IActor;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.MemberBean;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.util.ParameterObject;

import com.vaadin.ui.Component;

/** Task to process the selected members to make them group administrators.
 *
 * @author Luthiger Created: 16.11.2011 */
@UseCaseController
public class GroupAdminSelectTask extends ParticipantSelectTask { // NOPMD

    @SuppressWarnings("unchecked")
    @Override
    protected Component runChecked() throws RiplaException { // NOPMD
        try {
            final ParameterObject lParameters = getParameters();
            final Collection<Long> lMemberIDs = getSelectedIDs((Collection<MemberBean>) lParameters
                    .get(Constants.KEY_PARAMETER_RESULT_SELECTION));
            setGroupAdmins(getGroupID(), lMemberIDs);
            registerAsParticipants(getGroupID(), lMemberIDs);
            return forwardTo(GroupEditTask.class);
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    private void setGroupAdmins(final Long inGroupID, final Collection<Long> inGroupAdmins) throws VException,
            SQLException {
        boolean refreshAuthorization = false;
        final IActor lActor = getActor();
        final Long lActorID = lActor.getActorID();
        final GroupAdminHome lHome = BOMHelper.getGroupAdminHome();
        final Collection<Long> lAdminIDsBefore = getAdminIDsBefore(inGroupID);
        final Collection<Long> lAdminIDsAfter = new ArrayList<Long>();

        // process new selected: add group admins not already assigned
        final LinkMemberRoleHome lRoleHome = BOMHelper.getLinkMemberRoleHome();
        for (final Long lMemberID : inGroupAdmins) {
            lAdminIDsAfter.add(lMemberID);
            if (!lAdminIDsBefore.contains(lMemberID)) {
                lHome.associateGroupAdmin(inGroupID, lMemberID);
                if (!lRoleHome.hasRoleGroupAdmin(lMemberID)) {
                    lRoleHome.createGroupAdminRole(lMemberID);
                }
                refreshAuthorization = refreshAuthorization || lMemberID.equals(lActorID);
            }
        }

        // process old assigned: delete group admins no longer selected
        for (final Long lMemberID : lAdminIDsBefore) {
            if (!lAdminIDsAfter.contains(lMemberID)) {
                deleteGroupAdmin(inGroupID, lMemberID);
            }
        }

        // if actor is group admin, refresh the authorization to have the menu displayed updated
        if (refreshAuthorization) {
            lActor.refreshAuthorization();
            refreshBody();
        }
    }

    private Collection<Long> getAdminIDsBefore(final Long inGroupID) throws VException, SQLException {
        final Collection<Long> outIDs = new ArrayList<Long>();
        final QueryResult lAdmins = BOMHelper.getJoinGroupAdminToMemberHome().select(inGroupID);
        while (lAdmins.hasMoreElements()) {
            outIDs.add(BeanWrapperHelper.getLong(MemberHome.KEY_ID, lAdmins.nextAsDomainObject()));
        }
        return outIDs;
    }

}
