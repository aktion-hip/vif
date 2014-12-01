/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Responsible;
import org.hip.vif.core.bom.ResponsibleHome;

/** Created on 15.08.2003
 * 
 * @author Luthiger */
@SuppressWarnings("serial")
public abstract class AbstractResponsibleHome extends DomainObjectHomeImpl implements ResponsibleHome {

    /** AbstractResponsibleHome default constructor. */
    public AbstractResponsibleHome() {
        super();
    }

    /** Returns the entry identified by the specified values, i.e. either author or reviewer.
     * 
     * @param inContributionID String
     * @param inMemberID Long
     * @return Responsible
     * @throws VException
     * @throws SQLException */
    @Override
    public Responsible getResponsible(final String inContributionID, final Long inMemberID) throws VException,
            SQLException {
        final KeyObject lKey = getContributionKey(new Integer(inContributionID));
        lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, inMemberID);
        return (Responsible) findByKey(lKey);
    }

    /** Returns the responsible author.
     * 
     * @param inContributionID String
     * @return Responsible
     * @throws VException
     * @throws SQLException */
    @Override
    public Responsible getAuthor(final String inContributionID) throws VException, SQLException {
        final KeyObject lKey = getContributionKey(new Integer(inContributionID));
        lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        return (Responsible) findByKey(lKey);
    }

    /** Hook for subclasses. Returns a KeyObject initialized with the contribution ID.
     * 
     * @param inContributionID Integer
     * @return KeyObject
     * @throws VException */
    protected abstract KeyObject getContributionKey(Integer inContributionID) throws VException;
}
