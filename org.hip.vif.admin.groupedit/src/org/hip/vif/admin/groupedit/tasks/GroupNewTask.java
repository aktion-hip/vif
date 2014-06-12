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

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.ui.GroupView;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Task for creating new discussion groups.
 * 
 * @author Luthiger
 * Created: 06.11.2011
 */
@Partlet
public class GroupNewTask extends AbstractGroupTask {
	private static final Logger LOG = LoggerFactory.getLogger(GroupNewTask.class);
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_GROUP_CREATE;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		emptyContextMenu();
		
		Group lGroup = (Group)BOMHelper.getGroupHome().create();
		//set default values
		lGroup.set(GroupHome.KEY_PRIVATE, 0l);
		lGroup.set(GroupHome.KEY_REVIEWERS, 1l);
		lGroup.set(GroupHome.KEY_GUEST_DEPTH, 0l);
		lGroup.set(GroupHome.KEY_MIN_GROUP_SIZE, 10l);
		return new GroupView(lGroup, this);
	}

	/**
	 * Callback method, saves the changed group data.
	 * 
	 * @param inGroup {@link Group}
	 * @return boolean <code>true</code> if the group entry has been created successfully
	 * @throws ExternIDNotUniqueException 
	 */
	public boolean save(Group inGroup) throws ExternIDNotUniqueException {
		try {
			Long lGroupID = inGroup.ucNew();
			setGroupID(lGroupID);
			showNotification(Activator.getMessages().getMessage("admin.group.new.ok"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(GroupEditTask.class);
			return true;
		} 
		catch (BOMChangeValueException exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		} 
		return false;
	}

}
