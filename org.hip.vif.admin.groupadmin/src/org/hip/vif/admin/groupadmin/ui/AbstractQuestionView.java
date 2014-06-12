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

package org.hip.vif.admin.groupadmin.ui;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Base class for views displaying parts of a question.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@SuppressWarnings("serial")
public class AbstractQuestionView extends AbstractContributionView {

	/**
	 * Renders the display of the whole question context.
	 * 
	 * @param inQuestion
	 *            {@link Question}
	 * @param inTitle
	 *            String
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inAuthors
	 *            {@link QueryResult}
	 * @param inReviewers
	 *            {@link QueryResult}
	 * @param inMessages
	 *            {@link IMessages}
	 * @param inIsGuest
	 *            boolean
	 * @param inTask
	 *            {@link IPluggableWithLookup}
	 * @return {@link Component} the view component with the rendered question
	 * @throws VException
	 * @throws SQLException
	 */
	protected Component createQuestion(final Question inQuestion,
			final String inTitle, final CodeList inCodeList,
			final QueryResult inAuthors, final QueryResult inReviewers,
			final IMessages inMessages, final IPluggableWithLookup inTask)
			throws VException, SQLException {
		final VerticalLayout outLayout = new VerticalLayout();

		// question title and state
		final HorizontalLayout lTitleView = new HorizontalLayout();
		lTitleView.setStyleName("vif-title-bar"); //$NON-NLS-1$
		lTitleView.setSpacing(true);

		lTitleView
				.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
						String.format(VIFViewHelper.TMPL_TITLE,
								"vif-title", inTitle), ContentMode.HTML))); //$NON-NLS-1$
		final String lState = getState(
				BeanWrapperHelper.getString(QuestionHome.KEY_STATE, inQuestion),
				inCodeList, inMessages);
		final Label lNote = new Label(String.format(VIFViewHelper.TMPL_TITLE,
				"vif-note", lState), ContentMode.HTML); //$NON-NLS-1$
		lTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(lNote));
		lTitleView.setComponentAlignment(lNote, Alignment.BOTTOM_RIGHT);

		outLayout.addComponent(lTitleView);

		addProperQuestion(inQuestion, inMessages, outLayout);

		// author/reviewer
		final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inAuthors, inReviewers, inTask);
		outLayout
				.addComponent(lRenderer.render(
						inMessages
								.getMessage("ui.discussion.question.view.label.author"), //$NON-NLS-1$
						inMessages
								.getMessage("ui.discussion.question.view.label.reviewer"), //$NON-NLS-1$
						BeanWrapperHelper.getFormattedDate(
								QuestionHome.KEY_MUTATION, inQuestion))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return outLayout;
	}

}
