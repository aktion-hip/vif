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

package org.hip.vif.admin.member.tasks;

import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.admin.member.mail.PasswordResetMail;
import org.hip.vif.admin.member.ui.MemberEditView;
import org.hip.vif.admin.member.ui.MemberView;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.RatingsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Shows the member data for that it can be edited.
 * 
 * @author Luthiger
 * Created: 19.10.2011
 */
@Partlet
public class MemberShowTask extends AbstractMemberTask {
	private static final Logger LOG = LoggerFactory.getLogger(MemberShowTask.class);
	Long memberID;

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_DEFAULT);
			
			memberID = (Long) getParameters().get(Constants.KEY_PARAMETER_MEMBER);
			
			IMemberHelper lMember = retrieveMember(memberID);
			if (isExternal()) {
				return new MemberView(lMember.getMember(), 
						RoleContainer.createData(BOMHelper.getLinkMemberRoleHome().getRolesOf(memberID), getAppLocale().getLanguage(), BOMHelper.getRoleHome().getGroupSpecificIDs()),
						new RatingsHelper(memberID), this);
				
			}
			return new MemberEditView(lMember.getMember(),
					RoleContainer.createData(BOMHelper.getLinkMemberRoleHome().getRolesOf(memberID), getAppLocale().getLanguage(), BOMHelper.getRoleHome().getGroupSpecificIDs()),
					new RatingsHelper(memberID), this);
		} 
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}
	
	private boolean isExternal() throws VException {
		return MemberUtility.INSTANCE.getActiveAuthenticator().isExternal();
	}

	@Override
	public boolean saveMember(Member inMember, RoleContainer inRoles) throws ExternIDNotUniqueException {
		try {
			boolean lChangedRoles = inMember.ucSave(inRoles.getSelectedIDs(), getActor().getActorID());
			refreshAndNotify(lChangedRoles, getNotificationMessage(inMember));
			return true;
		} 
		catch (BOMChangeValueException exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		} 
		catch (VException exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Callback method, saves the changed member's roles.
	 * 
	 * @param inMember {@link Member} 
	 * @param inRoles {@link RoleContainer}
	 * @return boolean <code>true</code> if the roles have successfully been saved
	 */
	public boolean saveRoles(Member inMember, RoleContainer inRoles) {
		try {
			LinkMemberRoleHome lLinkHome = (LinkMemberRoleHome)BOMHelper.getLinkMemberRoleHome();
			boolean lChangedRoles = lLinkHome.updateRoles(memberID, inRoles.getSelectedIDs());
			refreshAndNotify(lChangedRoles, getNotificationMessage(inMember));
			return true;
		} 
		catch (BOMChangeValueException exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		} 
		catch (VException exc) {
			LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	protected IMemberHelper retrieveMember(Long inMemberID) throws Exception {
		return new MemberHelper(inMemberID);
	}
	
	@Override
	protected Long getMemberID() {
		return memberID;
	}
	
	@Override
	public boolean resetPW() {
		try {
			if (isExternal()) return false;
			
			//create password, save and mail it to the user
			Member lMember = BOMHelper.getMemberHome().getMember(getMemberID());
			String lPassword = createPassword();
			lMember.savePwrd(lPassword);
			PasswordResetMail lMail = new PasswordResetMail((VIFMember)lMember, lPassword);
			lMail.send();
			refreshAndNotify(false, getNotificationMessage(lMember, lPassword));
			return true;
		}
		catch (MailGenerationException exc) {
			showNotification(Activator.getMessages().getMessage("errmsg.member.pwrd.no.mail"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			sendEvent(MemberSearchTask.class);			
			return true;
		}
		catch (Exception exc) {
			LOG.error("Error while reseting the member's password.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	private String getNotificationMessage(Member inMember, String inPwrd) throws VException {
		IMessages lMessages = Activator.getMessages();
		String lUserID = inMember.get(MemberHome.KEY_USER_ID).toString();
		StringBuilder outNotification = new StringBuilder(lMessages.getFormattedMessage("msg.member.reset.pw", lUserID)); //$NON-NLS-1$
		if (displayPassword()) {
			outNotification.append(" ").append(lMessages.getFormattedMessage("msg.member.data.saved.add", lUserID, inPwrd)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return new String(outNotification);
	}
	
// --- private classes ---
	
	protected interface IMemberHelper {
		Member getMember();
		Long getMemberID();
	}
	
	private class MemberHelper implements IMemberHelper {
		private Long memberID;
		private Member member;
		public MemberHelper(Long inMemberID) throws Exception {
			memberID = MemberUtility.INSTANCE.getActiveMemberSearcher().getAssociatedCacheID(inMemberID);
			member = BOMHelper.getMemberCacheHome().getMember(memberID);
		}
		public Member getMember() {
			return member;
		}
		public Long getMemberID() {
			return memberID;
		}
	}
	
}
