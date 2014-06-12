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

package org.hip.vif.forum.member.tasks;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.Constants;
import org.hip.vif.forum.member.ui.EditPwrdView;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Task to edit the password.
 * 
 * @author Luthiger
 * Created: 12.10.2011
 */
@Partlet
public class PwrdEditTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(PwrdEditTask.class);
	
	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_EDIT_DATA;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		loadContextMenu(Constants.MENU_SET_ID_EDIT_DATA);
		
		if (MemberUtility.INSTANCE.getActiveAuthenticator().isExternal()) {
			return new EditPwrdView();
		}
		return new EditPwrdView(getActor().getUserID(), this);
	}

	/**
	 * Save the values entered in the view.
	 * 
	 * @param inOld
	 * @param inNewInit
	 * @param inNewConfirm
	 * @return boolean <code>true</code> if the new password has been saved successfully
	 * @throws InvalidAuthenticationException
	 */
	public boolean savePwrd(String inOld, String inNewInit, String inNewConfirm) throws InvalidAuthenticationException {
		try {
			Member lMember = BOMHelper.getMemberCacheHome().getActor();
			lMember.checkAuthentication(inOld, getAppLocale());
			lMember.savePwrd(inNewInit);
			showNotification(Activator.getMessages().getMessage("msg.task.pwrd.changed"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(PersonalDataEditTask.class);
			return true;
		} 
		catch (InvalidAuthenticationException exc) {
			//rethrow to handle this case in view
			throw exc;
		}
		catch (Exception exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		}
		
		return false;
	}

}
