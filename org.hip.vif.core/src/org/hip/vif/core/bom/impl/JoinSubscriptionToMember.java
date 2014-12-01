/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/** Model joining the subscription model with the member model.
 *
 * @author Benno Luthiger Created on Feb 15, 2004 */
@SuppressWarnings("serial")
public class JoinSubscriptionToMember extends DomainObjectImpl implements QuestionHierarchyEntry {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinSubscriptionToMemberHome";

    /** JoinSubscriptionToMember constructor. */
    public JoinSubscriptionToMember() {
        super();
    }

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** Sets the specified visitor (see visitor pattern).
     * 
     * @param inVisitor QuestionHierarchyVisitor
     * @throws VException
     * @throws SQLException */
    @Override
    public void accept(final QuestionHierarchyVisitor inVisitor) throws VException, SQLException {
        inVisitor.visitSubscriber(this);
    }
}
