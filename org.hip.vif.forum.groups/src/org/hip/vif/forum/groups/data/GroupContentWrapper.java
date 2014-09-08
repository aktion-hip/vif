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

package org.hip.vif.forum.groups.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.web.util.BeanWrapperHelper;

/** Wrapper for an entry in a discussion group, i.e. a question.
 *
 * @author Luthiger Created: 22.05.2011 */
public class GroupContentWrapper {
    private static final String TEMPL = "%s: %s [%s]"; //$NON-NLS-1$

    private final Long questionID;
    private final String decimalID;
    private final String question;
    private final String state;

    /** Private constructor. */
    private GroupContentWrapper(final GeneralDomainObject inDomainObject, final CodeList inCodeList) {
        questionID = BeanWrapperHelper.getLong(QuestionHome.KEY_ID, inDomainObject);
        decimalID = BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inDomainObject);
        question = BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION, inDomainObject);
        state = inCodeList.getLabel(BeanWrapperHelper.getString(QuestionHome.KEY_STATE, inDomainObject));
    }

    /** Factory method, creates the instance.
     * 
     * @param inDomainObject {@link GeneralDomainObject}
     * @param inCodeList
     * @return {@link GroupContentWrapper} */
    public static GroupContentWrapper createItem(final GeneralDomainObject inDomainObject, final CodeList inCodeList) {
        return new GroupContentWrapper(inDomainObject, inCodeList);
    }

    public Long getQuestionID() {
        return questionID;
    }

    @Override
    public String toString() {
        return String.format(TEMPL, decimalID, question, state);
    }

}
