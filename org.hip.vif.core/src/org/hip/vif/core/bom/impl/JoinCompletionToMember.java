/*
	This package is part of the administration of the application VIF.
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

import org.hip.kernel.bom.impl.DomainObjectImpl;

/** This class implements the join between the domain objects Completion and Member via CompletionAuthorReviewer. It can
 * be used to retriev all Contributions and their authors and reviewers belonging to the same question.
 *
 * Created on 29.05.2003
 * 
 * @author Luthiger */
@SuppressWarnings("serial")
public class JoinCompletionToMember extends DomainObjectImpl {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinCompletionToMemberHome";

    /** JoinCompletionToMember default constructor. */
    public JoinCompletionToMember() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
     */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }
}
