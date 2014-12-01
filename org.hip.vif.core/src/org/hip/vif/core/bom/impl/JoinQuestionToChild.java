/*
	This package is part of the persistency layer of the application VIF.
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

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/** This class implements the join between the question hierachy and questions.
 *
 * @author: Benno Luthiger */
@SuppressWarnings("serial")
public class JoinQuestionToChild extends DomainObjectImpl implements QuestionHierarchyEntry {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinQuestionToChildHome";

    /** Constructor for JoinQuestionToChild. */
    public JoinQuestionToChild() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
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
        inVisitor.visitChild(this);
    }
}
