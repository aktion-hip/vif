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

package org.hip.vif.forum.usersettings.tasks;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.usertasks.UsertasksRegistry;
import org.hip.vif.forum.usersettings.ui.UserTasksView;
import org.hip.vif.web.tasks.AbstractVIFTask;

import com.vaadin.ui.Component;

/**
 * Task that displays the open user tasks, e.g. the rating form.
 * forum?requestType=master&body=org.hip.vif.forum.usersettings.manageUserTasks
 * 
 * @author Luthiger
 * Created: 19.12.2011
 */
@Partlet
public class UserTasksManageTask extends AbstractVIFTask {
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		try {
			emptyContextMenu();
			
			Long lMemberID = getActor().getActorID();
			if (!UsertasksRegistry.INSTANCE.hasOpenTasks(lMemberID)) {
				VIFMember lMember = (VIFMember)BOMHelper.getMemberCacheHome().getMember(lMemberID);
				return new UserTasksView(lMember.getFullName());
			}
			
			UserTasksView out = new UserTasksView();
			for (Component lComponent : UsertasksRegistry.INSTANCE.getTasksViews(lMemberID, getEventAdmin())) {
				out.addComponent(lComponent);
			}
			return out;
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}

}
