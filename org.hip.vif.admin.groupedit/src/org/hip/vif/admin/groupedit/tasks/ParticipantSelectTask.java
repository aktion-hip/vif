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
import java.util.ArrayList;
import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.member.MemberBean;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.ParameterObject;

import com.vaadin.ui.Component;

/**
 * Task to select the participants of a private discussion group.
 * 
 * @author Luthiger
 * Created: 18.11.2011
 */
@Partlet
public class ParticipantSelectTask extends AbstractGroupTask {
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_LIST_PARTICIPANTS;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Component runChecked() throws VException {
		try {
			ParameterObject lParameters = getParameters();
			Collection<Long> lSelected = getSelectedIDs((Collection<MemberBean>) lParameters.get(Constants.KEY_PARAMETER_RESULT_SELECTION));
			registerAsParticipants(getGroupID(), lSelected);
			return forwardToTask(ParticipantListTask.class, false);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}

	@Override
	public boolean save(Group inGroup) throws ExternIDNotUniqueException {
		//intentionally left empty
		return false;
	}

	protected int registerAsParticipants(Long inGroupID, Collection<Long> inMembers) throws VException {
		try {
			ParticipantHome lParticipantHome = BOMHelper.getParticipantHome();
			LinkMemberRoleHome lRoleHome = BOMHelper.getLinkMemberRoleHome();
			boolean lRegistered = false;
			int outRegistered = 0;
			for (Long lMemberID : inMembers) {
				//fill table participant
				lRegistered = lParticipantHome.createChecked(lMemberID, inGroupID);
				if (lRegistered) {
					outRegistered++;
					//fill table Role
					if (!lRoleHome.hasRoleParticipant(lMemberID)) {
						lRoleHome.createParticipantRole(lMemberID);
					}
				}
			}
			return outRegistered;
		}
		catch (WorkflowException exc) {
			throw new VException(exc.getMessage());
		}
		catch (SQLException exc) {
			throw new VException(exc.getMessage());
		}
	}

	protected Collection<Long> getSelectedIDs(Collection<MemberBean> inGroupAdmins) throws VException, SQLException {
		Collection<Long> lIDs = new ArrayList<Long>(inGroupAdmins.size());
		for (MemberBean lMember : inGroupAdmins) {
			lIDs.add(lMember.getMemberID());
		}
		return MemberUtility.INSTANCE.getActiveMemberSearcher().createMemberCacheEntryChecked(lIDs);
	}

}
