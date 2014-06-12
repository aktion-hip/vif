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

package org.hip.vif.admin.groupedit.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.member.IActor;
import org.hip.vif.core.member.MemberBean;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.core.util.ParameterObject;

import com.vaadin.ui.Component;

/**
 * Task to process the selected members to make them group administrators.
 * 
 * @author Luthiger
 * Created: 16.11.2011
 */
@Partlet
public class GroupAdminSelectTask extends ParticipantSelectTask {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Component runChecked() throws VException {
		try {
			ParameterObject lParameters = getParameters();
			Collection<Long> lMemberIDs = getSelectedIDs((Collection<MemberBean>) lParameters.get(Constants.KEY_PARAMETER_RESULT_SELECTION));
			setGroupAdmins(getGroupID(), lMemberIDs);
			registerAsParticipants(getGroupID(), lMemberIDs);
			return forwardToTask(GroupEditTask.class, false);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}
	
	private void setGroupAdmins(Long inGroupID, Collection<Long> inGroupAdmins) throws VException, SQLException {
		boolean refreshAuthorization = false;
		IActor lActor = getActor();
		Long lActorID = lActor.getActorID();
		GroupAdminHome lHome = BOMHelper.getGroupAdminHome();
		Collection<Long> lAdminIDsBefore = getAdminIDsBefore(inGroupID);
		Collection<Long> lAdminIDsAfter = new Vector<Long>();
		
		//process new selected: add group admins not already assigned
		LinkMemberRoleHome lRoleHome = BOMHelper.getLinkMemberRoleHome();
		for (Long lMemberID : inGroupAdmins) {
			lAdminIDsAfter.add(lMemberID);
			if (!lAdminIDsBefore.contains(lMemberID)) {
				lHome.associateGroupAdmin(inGroupID, lMemberID);
				if (!lRoleHome.hasRoleGroupAdmin(lMemberID)) {
					lRoleHome.createGroupAdminRole(lMemberID);
				}
				refreshAuthorization = refreshAuthorization || (lMemberID.equals(lActorID));				
			}
		}
		
		//process old assigned: delete group admins no longer selected
		for (Long lMemberID : lAdminIDsBefore) {
			if (!lAdminIDsAfter.contains(lMemberID)) {
				deleteGroupAdmin(inGroupID, lMemberID);
			}
		}
		
		//if actor is group admin, refresh the authorization to have the menu displayed updated
		if (refreshAuthorization) {
			lActor.refreshAuthorization();
			refreshDash();
		}
	}
	
	private Collection<Long> getAdminIDsBefore(Long inGroupID) throws VException, SQLException {
		Collection<Long> outIDs = new Vector<Long>();
		QueryResult lAdmins = BOMHelper.getJoinGroupAdminToMemberHome().select(inGroupID);
		while (lAdmins.hasMoreElements()) {
			outIDs.add(BeanWrapperHelper.getLong(MemberHome.KEY_ID, lAdmins.nextAsDomainObject()));
		}
		return outIDs;
	}

}
