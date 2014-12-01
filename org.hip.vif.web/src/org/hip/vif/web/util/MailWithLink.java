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

package org.hip.vif.web.util;

import java.io.IOException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.web.mail.AbstractMail;
import org.ripla.web.interfaces.IPluggable;

/** Special base class for mails that contains a link.<br />
 * This class provides the method <code>createRequestedURL()</code>.
 *
 * @author Luthiger Created: 28.09.2011 */
public abstract class MailWithLink extends AbstractMail {

    /** @param inReceiver {@link VIFMember}
     * @throws VException
     * @throws IOException */
    public MailWithLink(final VIFMember inReceiver) throws VException, IOException {
        super(inReceiver);
    }

    /** @param inReceiver {@link VIFMember}
     * @param inSender {@link VIFMember}
     * @throws VException
     * @throws IOException */
    public MailWithLink(final VIFMember inReceiver, final VIFMember inSender) throws VException, IOException {
        super(inReceiver, inSender);
    }

    /** Creates the URL to the view of the specified task, e.g.
     * <code>http://localhost:8084/forum?request=org.hip.vif.forum.groups/org.hip.vif.groups.tasks.RequestsListTask&groupID=21</code>
     * .
     *
     * @param inController {@link IPluggable}
     * @param inIsForum boolean <code>true</code> if the requested url should call the forum application,
     *            <code>false</code> for the admin application
     * @return String the bookmarkable URL to the view of the specified task */
    protected String createRequestedURL(final Class<? extends IPluggable> inController, final boolean inIsForum) {
        return VIFRequestHandler.createRequestedURL(inController, inIsForum);
    }

    /** @return String e.g. <code>http://localhost:8084/forum</code> */
    protected String getForumAppURL() {
        return VIFAppHelper.getMainForumURL();
    }

}
