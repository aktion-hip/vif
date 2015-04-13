/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
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
package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.ui.ContributionReView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to display a completion.
 *
 * @author Luthiger Created: 13.03.2008 */
@UseCaseController
public class CompletionShowTask extends AbstractGroupsTask { // NOPMD

    @Override
    protected String needsPermission() { // NOPMD
        return Constants.PERMISSION_NEW_COMPLETION;
    }

    @Override
    protected Component runChecked() throws RiplaException { // NOPMD
        try {
            final Long lCompletionID = getCompletionID();
            final String lCompletionIDs = lCompletionID.toString();
            final Long lQuestionID = getQuestionID();
            final String lQuestionIDs = lQuestionID.toString();
            final Long lGroupID = getGroupID();

            // get completion
            final Completion lCompletion = BOMHelper.getCompletionHome().getCompletion(lCompletionID);
            final QueryResult lSiblings = BOMHelper.getJoinCompletionToMemberHome().selectAuthorViewOfSiblings(
                    lQuestionID, getActor().getActorID(), lCompletionID);

            // get question
            final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionIDs);

            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new ContributionReView(lCompletion, getCompletionAuthors(lCompletionIDs),
                    getCompletionReviewers(lCompletionIDs),
                    VifBOMHelper.getGroupHome().getGroup(lGroupID), lQuestion, lSiblings,
                    getPublishedBibliography(lQuestionID),
                    getAuthors(lQuestionIDs), getReviewers(lQuestionIDs),
                    lCodeList, this);
        } catch (final SQLException | VException exc) {
            throw createContactAdminException(exc);
        }
    }

}
