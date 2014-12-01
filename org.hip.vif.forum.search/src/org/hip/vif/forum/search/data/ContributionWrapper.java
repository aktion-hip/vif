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

package org.hip.vif.forum.search.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.ContentHitsObject;
import org.hip.vif.web.util.BeanWrapperHelper;

/** Wrapper/adapter for <code>ContentHitsObject</code> (returned by a content search).
 *
 * @author Luthiger Created: 04.11.2011 */
public class ContributionWrapper {
    private final Long questionID;
    private final Long groupID;
    private final String decimalID;
    private final String question;
    private final String author;
    private final String group;

    private ContributionWrapper(final ContentHitsObject inItem) {
        questionID = BeanWrapperHelper.getLong(AbstractSearching.IndexField.CONTRIBUTION_ID.fieldName, inItem);
        groupID = BeanWrapperHelper.getLong(AbstractSearching.IndexField.GROUP_ID.fieldName, inItem);
        decimalID = BeanWrapperHelper.getString(AbstractSearching.IndexField.DECIMAL_ID.fieldName, inItem);
        question = BeanWrapperHelper.getPlain(AbstractSearching.IndexField.QUESTION_TEXT.fieldName, inItem);
        author = BeanWrapperHelper.getString(AbstractSearching.IndexField.AUTHOR_NAME.fieldName, inItem);
        group = BeanWrapperHelper.getString(AbstractSearching.IndexField.GROUP_NAME.fieldName, inItem);
    }

    /** Static factory method.
     * 
     * @param inGeneralDomainObject {@link GeneralDomainObject}
     * @return {@link ContributionWrapper} */
    public static ContributionWrapper createItem(final GeneralDomainObject inGeneralDomainObject) {
        return new ContributionWrapper((ContentHitsObject) inGeneralDomainObject);
    }

    public Long getQuestionID() {
        return questionID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public String getDecimalID() {
        return decimalID;
    }

    public String getQuestion() {
        return question;
    }

    public String getAuthor() {
        return author;
    }

    public String getGroup() {
        return group;
    }

}
