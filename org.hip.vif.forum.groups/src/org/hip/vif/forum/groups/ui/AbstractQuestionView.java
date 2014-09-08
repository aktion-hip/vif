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

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.BibliographyFormatter;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/** Base class for views displaying parts of a question.
 *
 * @author Luthiger Created: 22.07.2011 */
@SuppressWarnings("serial")
public abstract class AbstractQuestionView extends AbstractContributionView {

    /** Renders the display of the whole question context.
     *
     * @param inQuestion {@link Question}
     * @param inTitle String
     * @param inCodeList {@link CodeList}
     * @param inAuthors {@link QueryResult}
     * @param inReviewers {@link QueryResult}
     * @param inMessages {@link IMessages}
     * @param inIsGuest boolean
     * @param inTask {@link IPluggableWithLookup}
     * @return {@link Component} the view component with the rendered question
     * @throws VException
     * @throws SQLException */
    protected Component createQuestion(final Question inQuestion, final String inTitle,
            final CodeList inCodeList, final QueryResult inAuthors,
            final QueryResult inReviewers, final IMessages inMessages, final boolean inIsGuest,
            final IPluggableWithLookup inTask) throws VException, SQLException {
        final HorizontalLayout lTitleView = new HorizontalLayout();
        lTitleView.setSpacing(true);
        final VerticalLayout outLayout = createQuestionLayout(inQuestion, inTitle,
                lTitleView, inCodeList, inMessages);

        // author/reviewer
        final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
                .createRenderer(inAuthors, inReviewers, inTask, !inIsGuest);
        outLayout.addComponent(lRenderer.render(inMessages
                .getMessage("ui.question.view.label.author"), //$NON-NLS-1$
                inMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
                BeanWrapperHelper.getFormattedDate(QuestionHome.KEY_MUTATION,
                        inQuestion))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        return outLayout;
    }

    /** Renders the question for the lookup view.
     *
     * @param inQuestion {@link Question}
     * @param inTitle String
     * @param inCodeList {@link CodeList}
     * @param inAuthors {@link QueryResult}
     * @param inReviewers {@link QueryResult}
     * @param inMessages {@link IMessages}
     * @return {@link Component} the view component with the rendered question
     * @throws VException
     * @throws SQLException */
    protected Component createQuestion(final Question inQuestion, final String inTitle,
            final CodeList inCodeList, final QueryResult inAuthors,
            final QueryResult inReviewers, final IMessages inMessages) throws VException,
            SQLException {
        final HorizontalLayout lTitleView = new HorizontalLayout();
        lTitleView.setWidth("100%"); //$NON-NLS-1$
        final VerticalLayout outLayout = createQuestionLayout(inQuestion, inTitle,
                lTitleView, inCodeList, inMessages);

        // author/reviewer
        final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
                .createRenderer(inAuthors, inReviewers);
        outLayout.addComponent(lRenderer.render(inMessages
                .getMessage("ui.question.view.label.author"), //$NON-NLS-1$
                inMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
                BeanWrapperHelper.getFormattedDate(QuestionHome.KEY_MUTATION,
                        inQuestion))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        return outLayout;
    }

    private VerticalLayout createQuestionLayout(final Question inQuestion,
            final String inTitle, final HorizontalLayout inTitleView, final CodeList inCodeList,
            final IMessages inMessages) {
        final VerticalLayout outLayout = new VerticalLayout();
        outLayout.setWidth("100%"); //$NON-NLS-1$

        // question title and state
        inTitleView.setStyleName("vif-title-bar"); //$NON-NLS-1$

        final Label lLabel = new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", inTitle), ContentMode.HTML); //$NON-NLS-1$
        inTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(lLabel)); //$NON-NLS-1$
        final String lState = getState(
                BeanWrapperHelper.getString(QuestionHome.KEY_STATE, inQuestion),
                inCodeList, inMessages);
        final Label lNote = new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-note", lState), ContentMode.HTML); //$NON-NLS-1$
        lNote.setWidth("100%"); //$NON-NLS-1$
        inTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(lNote));
        inTitleView.setComponentAlignment(lNote, Alignment.BOTTOM_LEFT);
        inTitleView.setExpandRatio(lLabel, 1);
        inTitleView.setExpandRatio(lNote, 6);

        outLayout.addComponent(inTitleView);

        addProperQuestion(inQuestion, inMessages, outLayout);
        return outLayout;
    }

    /** Renders the set of bibliographical entries assigned to the question (for the normal view).
     *
     * @param inLayout {@link VerticalLayout}
     * @param inBibliography {@link QueryResult}
     * @param inMessages {@link IMessages}
     * @param inTask {@link IPluggableWithLookup}
     * @throws VException
     * @throws SQLException */
    protected void createBibliography(final VerticalLayout inLayout,
            final QueryResult inBibliography, final IMessages inMessages,
            final IPluggableWithLookup inTask) throws VException, SQLException {
        createBiblioLayout(inLayout, inBibliography, inMessages, inTask,
                new IComponentCreator() {
                    @Override
                    public Component createComponent(
                            final GeneralDomainObject inBibliography,
                            final IPluggableWithLookup inTask) throws VException {
                        return createBibliography(inBibliography, inTask);
                    }
                });
    }

    /** Renders the set of bibliographical entries assigned to the question (for the lookup view).
     *
     * @param inLayout {@link VerticalLayout}
     * @param inBibliography {@link QueryResult}
     * @param inMessages {@link IMessages}
     * @throws VException
     * @throws SQLException */
    protected void createBibliography(final VerticalLayout inLayout,
            final QueryResult inBibliography, final IMessages inMessages)
            throws VException, SQLException {
        createBiblioLayout(inLayout, inBibliography, inMessages, null,
                new IComponentCreator() {
                    @Override
                    public Component createComponent(
                            final GeneralDomainObject inBibliography,
                            final IPluggableWithLookup inTask) throws VException {
                        return createBibliography(inBibliography);
                    }
                });
    }

    private void createBiblioLayout(final VerticalLayout inLayout,
            final QueryResult inBibliography, final IMessages inMessages,
            final IPluggableWithLookup inTask, final IComponentCreator inCreator)
            throws VException, SQLException {
        final VerticalLayout lBibliography = new VerticalLayout();
        lBibliography.setStyleName("vif-title-bar"); //$NON-NLS-1$
        lBibliography
                .addComponent(new Label(
                        String.format(
                                VIFViewHelper.TMPL_TITLE,
                                "vif-subtitle", inMessages.getMessage("ui.question.view.label.bibliography")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        boolean hasBibliography = false;
        while (inBibliography.hasMoreElements()) {
            lBibliography.addComponent(inCreator.createComponent(
                    inBibliography.next(), inTask));
            hasBibliography = true;
        }

        if (hasBibliography) {
            inLayout.addComponent(lBibliography);
        }
    }

    private Component createBibliography(final GeneralDomainObject inBibliography,
            final IPluggableWithLookup inTask) throws VException {
        final HorizontalLayout out = createBiblioLayout();

        final Button lLink = LinkButtonHelper.createLinkButton(BeanWrapperHelper
                .getString(TextHome.KEY_REFERENCE, inBibliography),
                LookupType.BIBLIOGRAPHY, BeanWrapperHelper.getLong(
                        TextHome.KEY_ID, inBibliography), inTask);
        lLink.setStyleName(BaseTheme.BUTTON_LINK);
        out.addComponent(lLink);

        addBibliographyText(inBibliography, out);
        return out;
    }

    private Component createBibliography(final GeneralDomainObject inBibliography)
            throws VException {
        final HorizontalLayout out = createBiblioLayout();
        addBibliographyText(inBibliography, out);
        return out;
    }

    private HorizontalLayout createBiblioLayout() {
        final HorizontalLayout out = new HorizontalLayout();
        out.setWidth("100%"); //$NON-NLS-1$
        out.setStyleName("vif-bibliography"); //$NON-NLS-1$
        return out;
    }

    private void addBibliographyText(final GeneralDomainObject inBibliography,
            final HorizontalLayout inLayout) throws VException {
        final BibliographyFormatter lFormatter = new BibliographyFormatter(
                new BibliographyAdapter(inBibliography,
                        TextHome.KEY_BIBLIO_TYPE));
        final Label lLabel = new Label(lFormatter.renderHtml(), ContentMode.HTML);
        lLabel.setWidth("100%"); //$NON-NLS-1$
        inLayout.addComponent(lLabel);
        inLayout.setExpandRatio(lLabel, 1.0f);
    }

    private static interface IComponentCreator {
        Component createComponent(GeneralDomainObject inModel,
                IPluggableWithLookup inTask) throws VException;
    }

}
