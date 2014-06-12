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

package org.hip.vif.forum.register.tasks;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.register.Activator;
import org.hip.vif.forum.register.Constants;
import org.hip.vif.forum.register.data.GroupContainer;
import org.hip.vif.forum.register.data.GroupWrapper;
import org.hip.vif.forum.register.ui.GroupListRegisterView;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * This task displays the list of available discussion groups for
 * that a member can register to the groups she wants to contribute.
 *
 * @author Luthiger
 * Created: 30.09.2011
 */
@Partlet
public class RegisterShowListTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(RegisterShowListTask.class);
	
	private static final String DFT_SORT = GroupHome.KEY_NAME + ", " + NestedGroupHome.KEY_GROUP_ID; //$NON-NLS-1$
	
	private GroupContainer groups;
	private CodeList codeList;
	private KeyObjectImpl key;
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_REGISTER;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		emptyContextMenu();
		
		NestedGroupHome lGroupHome = BOMHelper.getNestedGroupHome();
		
		KeyObject lKeySubscribeable = createKey(VIFGroupWorkflow.ENLISTABLE_STATES);
		key = new KeyObjectImpl();
		key.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PUBLIC);
		key.setValue(lKeySubscribeable, BinaryBooleanOperator.AND);

		Collection<Long> lRegisterings = BOMHelper.getParticipantHome().getRegisterings(getActor().getActorID());
		codeList = CodeListHome.instance().getCodeList(GroupState.class, getAppLocale().getLanguage());
		
		try {
			groups = GroupContainer.createData(lGroupHome.select(key, createOrder(DFT_SORT, false)), lRegisterings, codeList);
			return new GroupListRegisterView(groups, this);
		} 
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}

	/**
	 * Callback function: processes the registerings.
	 * 
	 * @return {@link Feedback}
	 */
	public Feedback saveRegisterings() {
		Collection<Long> lRegisterings = new Vector<Long>();
		for (GroupWrapper lGroup : groups.getItemIds()) {
			if (lGroup.isChecked()) {
				lRegisterings.add(lGroup.getGroupID());
			}
		}		
		
		IMessages lMessages = Activator.getMessages();
		try {
			Long lActorID = getActor().getActorID();
			ParticipantHome lHome = BOMHelper.getParticipantHome();
			boolean isParticipantStart = lHome.isParticipant(lActorID);
			Collection<Long> lForbidden = lHome.saveRegisterings(lActorID, new Vector<Long>(lRegisterings));
			boolean isParticipantEnd = lHome.isParticipant(lActorID);
			
			//actualize participant role
			if (isParticipantEnd && !isParticipantStart) {
				BOMHelper.getLinkMemberRoleHome().createParticipantRole(lActorID);
				getActor().refreshAuthorization();
			}
			if (isParticipantStart && !isParticipantEnd) {
				BOMHelper.getLinkMemberRoleHome().deleteParticipantRole(lActorID);
				getActor().refreshAuthorization();
			}
			lRegisterings.addAll(lForbidden);
			groups = GroupContainer.createData(BOMHelper.getNestedGroupHome().select(key, createOrder(DFT_SORT, false)), lRegisterings, codeList);
			
			if (lForbidden.size() != 0) {
				return new Feedback(lMessages.getMessage("ui.register.feedback.hint"), Notification.TYPE_WARNING_MESSAGE, groups); //$NON-NLS-1$
			}
			return new Feedback(lMessages.getMessage("ui.register.feedback.saved"), Notification.TYPE_TRAY_NOTIFICATION, groups);			 //$NON-NLS-1$
		} 
		catch (BOMChangeValueException exc) {
			LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
			return new Feedback(lMessages.getMessage("errmsg.general"), Notification.TYPE_ERROR_MESSAGE, groups); //$NON-NLS-1$
		} 
		catch (VException exc) {
			LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
			return new Feedback(lMessages.getMessage("errmsg.general"), Notification.TYPE_ERROR_MESSAGE, groups); //$NON-NLS-1$
		} 
		catch (SQLException exc) {
			LOG.error("Error while saving the registerings!", exc); //$NON-NLS-1$
			return new Feedback(lMessages.getMessage("errmsg.general"), Notification.TYPE_ERROR_MESSAGE, groups); //$NON-NLS-1$
		}
	}
	
// ---
	
	/**
	 * Feedback object.
	 */
	public static class Feedback {
		public String message;
		public int notificationType;
		public GroupContainer groups;

		Feedback(String inMessage, int inNotificationType, GroupContainer inGroups) {
			message = inMessage;
			notificationType = inNotificationType;
			groups = inGroups;
		}
	}

}
