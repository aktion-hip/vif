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

package org.hip.vif.admin.member.tasks;

import java.io.IOException;
import java.util.Random;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.data.RoleContainer;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.tasks.AbstractWebController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Abstract base class for member tasks.
 *
 * @author Luthiger Created: 22.10.2011 */
public abstract class AbstractMemberTask extends AbstractWebController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMemberTask.class);

    private static final char[] saltChars = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./".toCharArray()); //$NON-NLS-1$

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_SEARCH;
    }

    /** Callback method, saves the changed member data.
     *
     * @param inMember {@link Member}
     * @param inRoles {@link RoleContainer}
     * @return boolean <code>true</code> if successful
     * @throws ExternIDNotUniqueException */
    abstract public boolean saveMember(Member inMember, RoleContainer inRoles) throws ExternIDNotUniqueException;

    /** Notify the user and forward to the next task.
     *
     * @param inChangedRoles boolean
     * @param inFeedback String
     * @throws BOMChangeValueException
     * @throws GettingException */
    protected void refreshAndNotify(final boolean inChangedRoles, final String inFeedback)
            throws BOMChangeValueException, VException {
        if (isActorChanged() && inChangedRoles) {
            getActor().refreshAuthorization();
        }
        showNotification(inFeedback);
        sendEvent(MemberSearchTask.class);
    }

    /** Subclasses may override.
     *
     * @param inMember {@link Member}
     * @return String the notification message to signal successful data procession
     * @throws VException */
    protected String getNotificationMessage(final Member inMember) throws VException {
        return Activator.getMessages().getFormattedMessage(
                "msg.member.data.saved", inMember.get(MemberHome.KEY_USER_ID)); //$NON-NLS-1$
    }

    /** @return boolean <code>true</code> if the actor (i.e. the actual user) is changing his own member entry */
    private boolean isActorChanged() {
        return getActor().getActorID().equals(getMemberID());
    }

    abstract protected Long getMemberID();

    /** Creates a new password.
     *
     * @return java.lang.String The new password. */
    protected String createPassword() {
        final Random randomGenerator = new Random();
        final int numSaltChars = saltChars.length;
        final StringBuffer outPassword = new StringBuffer();
        for (int i = 0; i < 6; i++)
            outPassword.append(saltChars[Math.abs(randomGenerator.nextInt()) % numSaltChars]);

        return new String(outPassword);
    }

    /** Checks whether the password should be displayed on the administration screen.
     *
     * @return boolean <code>true</true> if setting for password display is activated */
    protected boolean displayPassword() {
        try {
            return Boolean.parseBoolean(PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_PW_DISPLAY));
        } catch (final IOException exc) {
            LOG.error("Error encountered while retrieving the preferences!", exc); //$NON-NLS-1$
            return false;
        }
    }

    /** Subclasses may override.
     *
     * @return boolean <code>true</code> if the password have been reseted successfully */
    public boolean resetPW() {
        return false;
    }

}
