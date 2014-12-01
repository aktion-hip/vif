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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.Constants;
import org.hip.vif.forum.member.ui.EditPwrdView;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/** Task to edit the password.
 *
 * @author Luthiger Created: 12.10.2011 */
@UseCaseController
public class PwrdEditTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(PwrdEditTask.class);

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_DATA;
    }

    @Override
    protected Component runChecked() throws VIFWebException {
        loadContextMenu(Constants.MENU_SET_ID_EDIT_DATA);

        try {
            if (MemberUtility.INSTANCE.getActiveAuthenticator().isExternal()) {
                return new EditPwrdView();
            }
            return new EditPwrdView(getActor().getUserID(), this);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Save the values entered in the view.
     *
     * @param inOld
     * @param inNewInit
     * @param inNewConfirm
     * @return boolean <code>true</code> if the new password has been saved successfully
     * @throws InvalidAuthenticationException */
    public boolean savePwrd(final String inOld, final String inNewInit, final String inNewConfirm)
            throws InvalidAuthenticationException {
        try {
            final Member lMember = BOMHelper.getMemberCacheHome().getMember(getActor().getActorID());
            lMember.checkAuthentication(inOld, getAppLocale());
            lMember.savePwrd(inNewInit);
            Notification.show(Activator.getMessages().getMessage("msg.task.pwrd.changed"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(PersonalDataEditTask.class);
            return true;
        } catch (final InvalidAuthenticationException exc) {
            // rethrow to handle this case in view
            throw exc;
        } catch (final Exception exc) {
            LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
        }

        return false;
    }

}
