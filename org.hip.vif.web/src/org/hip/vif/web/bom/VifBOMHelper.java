/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.bom;

import org.hip.kernel.sys.VSys;
import org.hip.vif.web.bom.impl.GroupImpl;
import org.hip.vif.web.bom.impl.ParticipantImpl;
import org.hip.vif.web.bom.impl.TextImpl;

/** Helper class to return the various domain object homes (that need <code>IMessages</code>).
 *
 * @author lbenno */
public final class VifBOMHelper {

    private VifBOMHelper() {
        // prevent instantiation
    }

    /** Returns the GroupHome instance.
     *
     * @return {@link GroupHome} */
    public static GroupHome getGroupHome() {
        return (GroupHome) VSys.homeManager.getHome(GroupImpl.HOME_CLASS_NAME);
    }

    /** Returns the TextHome instance.
     *
     * @return {@link TextHome} */
    public static TextHome getTextHome() {
        return (TextHome) VSys.homeManager.getHome(TextImpl.HOME_CLASS_NAME);
    }

    /** @return {@link ParticipantHome} */
    public static ParticipantHome getParticipantHome() {
        return (ParticipantHome) VSys.homeManager.getHome(ParticipantImpl.HOME_CLASS_NAME);
    }

    /** @return {@link JoinQuestionToTextHome} */
    public static JoinQuestionToTextHome getJoinQuestionToTextHome() {
        return (JoinQuestionToTextHome) VSys.homeManager
                .getHome(JoinQuestionToText.HOME_CLASS_NAME);
    }

}
