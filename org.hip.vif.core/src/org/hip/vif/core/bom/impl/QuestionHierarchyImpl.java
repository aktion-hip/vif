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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchy;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;

/** This domain object implements the QuestionHierarchy interface.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.QuestionHierarchy */
@SuppressWarnings("serial")
public class QuestionHierarchyImpl extends DomainObjectImpl implements QuestionHierarchy {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionHierarchyHomeImpl";

    /** Constructor for QuestionHierarchyImpl. */
    public QuestionHierarchyImpl() {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    /** Returns the question associated with this QuestionHiearchy entry.
     * 
     * @return Question
     * @throws VException
     * @throws SQLException */
    @Override
    public Question getAssociatedQuestion() throws VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_ID, get(QuestionHierarchyHome.KEY_CHILD_ID));
        return (Question) BOMHelper.getQuestionHome().findByKey(lKey);
    }
}
