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

package org.hip.vif.forum.groups.ui;

import java.sql.SQLException;
import java.util.List;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.CompletionsHelper;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/** The reviewers view of a question or completion.
 *
 * @author Luthiger Created: 27.07.2011 */
@SuppressWarnings("serial")
public class ContributionReView extends AbstractQuestionView {

    /** Show the completion to review.
     *
     * @param inCompletion {@link Completion}
     * @param inGroup {@link Group}
     * @param inQuestion {@link Question} the question the completion belongs to
     * @param inCompletions {@link QueryResult} already published completions belonging to the same question
     * @param inBibliography {@link QueryResult} already published bibliography belonging to the same question
     * @param inAuthors {@link QueryResult}
     * @param inReviewers {@link QueryResult}
     * @param inCodeList {@link CodeList}
     * @param inTask {@link IPluggableWithLookup} the view's controlling task
     * @throws VException
     * @throws SQLException */
    public ContributionReView(final Completion inCompletion, final Group inGroup, final Question inQuestion,
            final QueryResult inCompletions,
            final QueryResult inBibliography, final QueryResult inAuthors, final QueryResult inReviewers,
            final CodeList inCodeList,
            final IPluggableWithLookup inTask) throws VException, SQLException {

        final IMessages lMessages = Activator.getMessages();
        final AbstractLayoutInitializer lInitializer = new LayoutInitializerQuery(inCompletions);
        final VerticalLayout lLayout = lInitializer.getLayout(inGroup, inQuestion, inBibliography, inAuthors,
                inReviewers, inCodeList, inTask, lMessages, "ui.question.view.title.question"); //$NON-NLS-1$

        // the new completion
        lLayout.addComponent(createCompletion(inCompletion, inCodeList, lMessages));
    }

    /** Show the question to review.
     *
     * @param inQuestion {@link Question} the new question
     * @param inAuthors {@link QueryResult} the new question's authors
     * @param inReviewers {@link QueryResult} the new question's reviewers
     * @param inParent {@link Question} the parent
     * @param inGroup {@link Group}
     * @param inParentAuthors {@link QueryResult} the parent's authors
     * @param inParentReviewers {@link QueryResult} the parent's reviewers
     * @param inCompletions {@link QueryResult}
     * @param inBibliography {@link QueryResult}
     * @throws VException
     * @throws SQLException */
    public ContributionReView(final Question inQuestion, final QueryResult inAuthors, final QueryResult inReviewers,
            final Question inParent, final Group inGroup, final QueryResult inParentAuthors,
            final QueryResult inParentReviewers,
            final List<CompletionsHelper.Completion> inCompletions, final QueryResult inBibliography,
            final CodeList inCodeList,
            final IPluggableWithLookup inTask) throws VException, SQLException {

        final IMessages lMessages = Activator.getMessages();
        final AbstractLayoutInitializer lInitializer = new LayoutInitializerList(inCompletions);
        final VerticalLayout lLayout = lInitializer.getLayout(inGroup, inQuestion, inBibliography, inAuthors,
                inReviewers, inCodeList, inTask, lMessages, "ui.question.view.title.question.parent"); //$NON-NLS-1$

        // the new question
        final String lLabel = String
                .format(lMessages.getMessage("ui.question.view.title.question.new"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
        lLayout.addComponent(createQuestion(inQuestion, lLabel, inCodeList, inAuthors, inReviewers, lMessages, false,
                inTask));
    }

    // --- inner classes ---

    private abstract class AbstractLayoutInitializer {
        VerticalLayout getLayout(final Group inGroup, final Question inQuestion, final QueryResult inBibliography,
                final QueryResult inAuthors, final QueryResult inReviewers, final CodeList inCodeList,
                final IPluggableWithLookup inTask, final IMessages inMessages, final String inKeyTitle)
                throws VException, SQLException {
            final VerticalLayout outLayout = new VerticalLayout();
            setCompositionRoot(outLayout);

            outLayout.setStyleName("vif-view"); //$NON-NLS-1$
            final String lTitle = String.format(
                    inMessages.getMessage("ui.question.view.title.page"), inGroup.get(GroupHome.KEY_NAME).toString()); //$NON-NLS-1$
            outLayout.addComponent(new Label(
                    String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$

            // question
            final String lLabel = String.format(inMessages.getMessage(inKeyTitle),
                    BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
            outLayout.addComponent(createQuestion(inQuestion, lLabel, inCodeList, inAuthors, inReviewers, inMessages,
                    false, inTask));

            // completions
            handleCompletions(outLayout, inCodeList, inMessages, inTask);

            // texts
            createBibliography(outLayout, inBibliography, inMessages, inTask);

            outLayout.addComponent(RiplaViewHelper.createSpacer());
            return outLayout;
        }

        abstract protected void handleCompletions(VerticalLayout inLayout, CodeList inCodeList, IMessages inMessages,
                IPluggableWithLookup inTask) throws VException, SQLException;
    }

    private class LayoutInitializerList extends AbstractLayoutInitializer {
        private final List<CompletionsHelper.Completion> completions;

        LayoutInitializerList(final List<CompletionsHelper.Completion> inCompletions) {
            completions = inCompletions;
        }

        @Override
        protected void handleCompletions(final VerticalLayout inLayout, final CodeList inCodeList,
                final IMessages inMessages, final IPluggableWithLookup inTask) throws VException, SQLException {
            for (final CompletionsHelper.Completion lCompletion : completions) {
                inLayout.addComponent(createCompletion(lCompletion, inCodeList, inMessages, false, inTask));
            }
        }
    }

    private class LayoutInitializerQuery extends AbstractLayoutInitializer {
        private final QueryResult completions;

        LayoutInitializerQuery(final QueryResult inCompletions) {
            completions = inCompletions;
        }

        @Override
        protected void handleCompletions(final VerticalLayout inLayout, final CodeList inCodeList,
                final IMessages inMessages, final IPluggableWithLookup inTask) throws VException, SQLException {
            while (completions.hasMoreElements()) {
                inLayout.addComponent(createCompletion(completions.next(), inCodeList, inMessages, false, inTask));
            }
        }

    }

}
