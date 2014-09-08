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

package org.hip.vif.forum.groups.tasks;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.CompletionsHelper;
import org.hip.vif.forum.groups.ui.ContributionReView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to display the reviewer's view of a question.
 *
 * @author Luthiger Created: 29.07.2011 */
@UseCaseController
public class QuestionReviewTask extends AbstractGroupsTask {

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_QUESTION;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            final Long lQuestionID = getQuestionID();
            final String lQuestionIDs = lQuestionID.toString();

            loadContextMenu(Constants.MENU_SET_ID_REVIEW);

            final Question lParent = BOMHelper.getQuestionHierarchyHome().getParentQuestion(lQuestionID);
            final Long lParentID = BeanWrapperHelper.getLong(QuestionHome.KEY_ID, lParent);
            final String lParentIDs = lParentID.toString();

            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new ContributionReView(BOMHelper.getQuestionHome().getQuestion(lQuestionID),
                    getAuthors(lQuestionIDs), getReviewers(lQuestionIDs),
                    lParent, VifBOMHelper.getGroupHome().getGroup(getGroupID()),
                    getAuthors(lParentIDs), getReviewers(lParentIDs),
                    CompletionsHelper.getNormalizedCompletions(getPublishedCompletions(lParentID)),
                    getPublishedBibliography(lParentID),
                    lCodeList, this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

}
