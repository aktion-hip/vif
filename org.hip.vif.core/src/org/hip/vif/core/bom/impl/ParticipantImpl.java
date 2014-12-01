/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Participant;
import org.hip.vif.core.bom.ParticipantHome;

/** This domain object implements the Participant interface.
 *
 * Created on 01.11.2002
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Participant */
@SuppressWarnings("serial")
public class ParticipantImpl extends DomainObjectImpl implements Participant {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.ParticipantHomeImpl";

    /** Constructor for ParticipantImpl. */
    public ParticipantImpl() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** Suspends the participation for the specified period.
     * 
     * @param inFrom Timestamp
     * @param inTo Timestamp
     * @throws VException
     * @throws SQLException */
    @Override
    public void suspend(final Timestamp inFrom, final Timestamp inTo) throws VException, SQLException {
        set(ParticipantHome.KEY_SUSPEND_FROM, inFrom);
        set(ParticipantHome.KEY_SUSPEND_TO, inTo);
        update(true);
    }
}
