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

import java.sql.SQLException;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.data.CompletionsHelper;
import org.hip.vif.forum.groups.ui.QuestionView;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to display a question in a lookup window.
 *
 * @author Luthiger Created: 25.12.2011 */
@UseCaseController
public class QustionLookupTask extends AbstractGroupsTask {

    @Override
    protected String needsPermission() {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {

            final Long lQuestionID = getModelIdFromParameter();
            final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionID);
            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new QuestionView(lQuestion, lCodeList,
                    getAuthors(lQuestionID.toString()),
                    getReviewers(lQuestionID.toString()),
                    CompletionsHelper.getNormalizedCompletions(getPublishedCompletions(lQuestionID)),
                    getPublishedBibliography(lQuestionID));
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

}
