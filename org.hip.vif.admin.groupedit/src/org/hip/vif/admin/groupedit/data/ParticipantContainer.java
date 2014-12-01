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

package org.hip.vif.admin.groupedit.data;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

import com.vaadin.data.util.BeanItemContainer;

/** @author Luthiger Created: 18.11.2011 */
@SuppressWarnings("serial")
public class ParticipantContainer extends BeanItemContainer<ParticipantBean> {
    public static final String ENTRY_CHECK = "chk"; //$NON-NLS-1$
    public static final String ENTRY_CHECKED = "checked"; //$NON-NLS-1$
    public static final String PARTICIPANT_PLACE = "place"; //$NON-NLS-1$
    public static final String PARTICIPANT_MAIL = "mail"; //$NON-NLS-1$
    public static final Object[] NATURAL_COL_ORDER = new Object[] {
        "userID", "name", PARTICIPANT_PLACE, PARTICIPANT_MAIL, "isAdmin", "isActive" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    public static final String[] COL_HEADERS = new String[] {
        "container.member.headers.userid", "container.member.headers.name", "container.member.headers.place", "container.member.headers.mail", "container.participants.headers.admin", "container.participants.headers.active" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private ParticipantContainer() {
        super(ParticipantBean.class);
    }

    /** Factory method.
     *
     * @param inParticipants {@link QueryResult}
     * @return {@link ParticipantContainer}
     * @throws VException
     * @throws SQLException */
    public static ParticipantContainer createData(final QueryResult inParticipants) throws VException, SQLException {
        final ParticipantContainer out = new ParticipantContainer();
        while (inParticipants.hasMoreElements()) {
            out.addItem(ParticipantBean.createItem(inParticipants.nextAsDomainObject()));
        }
        return out;
    }

}
