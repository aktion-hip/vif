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

import java.io.IOException;
import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PlacefillerCollection;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.data.ParticipantBean;
import org.hip.vif.admin.groupedit.data.ParticipantContainer;
import org.hip.vif.admin.groupedit.ui.ParticipantListView;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.impl.NestedParticipantsOfGroup;
import org.hip.vif.core.bom.impl.NestedParticipantsOfGroupHome;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Controller for the view displaying the list of participants of a discussion group.
 * 
 * @author Luthiger
 * Created: 16.11.2011
 */
@SuppressWarnings("serial")
@Partlet
public class ParticipantListTask extends AbstractVIFTask implements Property.ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(ParticipantListTask.class);
	
	private ParticipantContainer participants;

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
	@Override
	protected Component runChecked() throws VException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_SHOW_PARTICIPANTS);
			
			NestedParticipantsOfGroupHome lParticipantsHome = getNestedParticipantsHome();
			
			Long lGroupID = getGroupID();
			String lGroupName = ""; //$NON-NLS-1$
			boolean lEnableDelete = false;
			if (lGroupID.intValue() > 0) {
				Group lGroup = BOMHelper.getGroupHome().getGroup(lGroupID);
				if (lGroup != null) {
					lGroupName = (String)lGroup.get(GroupHome.KEY_NAME);
					lEnableDelete = lGroup.isPrivate() || lGroup.isCreated();
				}
			}
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(GroupAdminHome.KEY_GROUP_ID, lGroupID);
			
			PlacefillerCollection lPlacefillers = new PlacefillerCollection();
			lPlacefillers.add(BOMHelper.getGroupAdminHome(), lKey, NestedParticipantsOfGroupHome.NESTED_ALIAS);
			
			lKey = new KeyObjectImpl();
			lKey.setValue(ParticipantHome.KEY_GROUP_ID, lGroupID);
			participants = ParticipantContainer.createData(lParticipantsHome.select(lKey, createOrder(MemberHome.KEY_USER_ID, false), lPlacefillers));
			return new ParticipantListView(participants, lGroupName, lEnableDelete, this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (IOException exc) {
			throw createContactAdminException(exc);
		}
	}

	private NestedParticipantsOfGroupHome getNestedParticipantsHome() throws IOException {
		if (PreferencesHandler.INSTANCE.isDerbyDB()) {
			return (NestedParticipantsOfGroupHome)VSys.homeManager.getHome(NestedParticipantsOfGroup.HOME_CLASS_NAME2);
		}
		return BOMHelper.getNestedParticipantsOfGroupHome();
	}

	public void valueChange(ValueChangeEvent inEvent) {
		Object lMember = inEvent.getProperty().getValue();
		if (lMember instanceof ParticipantBean) {
			requestLookup(LookupType.MEMBER, ((ParticipantBean) lMember).getMemberID());
		}
	}

	/**
	 * Callback method, removed the selected participants from the group.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean deleteParticipants() {
		try {
			ParticipantHome lParticipantHome = BOMHelper.getParticipantHome();
			LinkMemberRoleHome lRoleHome = BOMHelper.getLinkMemberRoleHome();
			Long lGroupID = getGroupID();
			
			int lCount = 0;
			for (ParticipantBean lParticipant : participants.getItemIds()) {
				if (lParticipant.isChecked()) {
					Long lMemberID = lParticipant.getMemberID();
					lParticipantHome.removeParticipant(lGroupID, lMemberID);
					
					//actualize the member's role if needed
					if (!lParticipantHome.isParticipant(lMemberID)) {
						lRoleHome.deleteParticipantRole(lMemberID);
					}
					lCount++;
				}
			}
			showNotification(Activator.getMessages().getFormattedMessage("ui.participants.feedback.remove", lCount), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(ParticipantListTask.class);
			return true;
		}
		catch (VException exc) {
			LOG.error("Error encoutered while removing participants from group!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
