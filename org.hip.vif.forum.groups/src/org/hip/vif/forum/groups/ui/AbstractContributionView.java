/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.data.CompletionsHelper.Completion;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Base class for views that display contributions. This class provides
 * functionality to create view components that can placed on the views.
 * 
 * @author Luthiger Created: 14.07.2011
 */
@SuppressWarnings("serial")
public abstract class AbstractContributionView extends CustomComponent {

	/**
	 * Renders the proper question on the specified layout.
	 * 
	 * @param inQuestion
	 *            {@link DomainObject} the business object containing the
	 *            question information
	 * @param inMessages
	 *            {@link IMessages}
	 * @param inLayout
	 *            {@link VerticalLayout} the layout the question view should be
	 *            added to
	 */
	protected void addProperQuestion(DomainObject inQuestion,
			IMessages inMessages, VerticalLayout inLayout) {
		// the proper question
		inLayout.addComponent(new Label(BeanWrapperHelper.getString(
				QuestionHome.KEY_QUESTION, inQuestion), Label.CONTENT_XHTML));

		// the question's substantiation
		inLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-subtitle", inMessages.getMessage("ui.question.view.label.remark")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		inLayout.addComponent(new Label(BeanWrapperHelper.getString(
				QuestionHome.KEY_REMARK, inQuestion), Label.CONTENT_XHTML));
	}

	/**
	 * Renders the specified completion (for a public view).
	 * 
	 * @param inCompletion
	 *            {@link GeneralDomainObject} the object containing the
	 *            completion information
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inMessages
	 *            {@link IMessages}
	 * @param inIsGuest
	 *            boolean <code>true</code> if the actor has guest permissions
	 *            only
	 * @param inTask
	 *            {@link IPluggableTask} the view's task
	 * @return {@link Component} the component containing the rendered view of
	 *         the completion
	 * @throws VException
	 * @throws SQLException
	 */
	protected Component createCompletion(GeneralDomainObject inCompletion,
			CodeList inCodeList, IMessages inMessages, boolean inIsGuest,
			IPluggableTask inTask) throws VException, SQLException {
		VerticalLayout outLayout = createCompletion(inCompletion, inCodeList,
				inMessages, "ui.question.view.label.completion", "vif-subtitle"); //$NON-NLS-1$ //$NON-NLS-2$

		// author/reviewer
		AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inCompletion, inTask, !inIsGuest);
		outLayout.addComponent(lRenderer.render(inMessages
				.getMessage("ui.question.view.label.author"), //$NON-NLS-1$
				inMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
				BeanWrapperHelper.getFormattedDate(CompletionHome.KEY_MUTATION,
						inCompletion))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return outLayout;
	}

	/*
	 * @param inCompletion {@link Completion} the object containing the
	 * completion information
	 */
	protected Component createCompletion(Completion inCompletion,
			CodeList inCodeList, IMessages inMessages, boolean inIsGuest,
			IPluggableTask inTask) throws VException, SQLException {
		VerticalLayout outLayout = createCompletion(inCompletion, inCodeList,
				inMessages, "ui.question.view.label.completion", "vif-subtitle"); //$NON-NLS-1$ //$NON-NLS-2$

		// author/reviewer
		AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inCompletion.getAuthors(),
						inCompletion.getReviewers(), inTask, !inIsGuest);
		outLayout.addComponent(lRenderer.render(
				inMessages.getMessage("ui.question.view.label.author"), //$NON-NLS-1$
				inMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
				inCompletion.getFormattedDate())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return outLayout;
	}

	protected Component createCompletionPlain(Completion inCompletion,
			CodeList inCodeList, IMessages inMessages) throws VException,
			SQLException {
		VerticalLayout outLayout = createCompletion(inCompletion, inCodeList,
				inMessages, "ui.question.view.label.completion", "vif-subtitle"); //$NON-NLS-1$ //$NON-NLS-2$

		// author/reviewer
		AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inCompletion.getAuthors(),
						inCompletion.getReviewers());
		outLayout.addComponent(lRenderer.render(
				inMessages.getMessage("ui.question.view.label.author"), //$NON-NLS-1$
				inMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
				inCompletion.getFormattedDate())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return outLayout;
	}

	/**
	 * Renders the specified completion (review process, unpublished state).
	 * 
	 * @param inCompletion
	 *            {@link Completion}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inMessages
	 *            {@link IMessages}
	 * @return {@link Component} the component containing the rendered view of
	 *         the completion
	 */
	protected Component createCompletion(Completion inCompletion,
			CodeList inCodeList, IMessages inMessages) {
		return createCompletion(inCompletion, inCodeList, inMessages,
				"ui.question.view.label.completion.new", "vif-title"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected Component createCompletion(GeneralDomainObject inCompletion,
			CodeList inCodeList, IMessages inMessages) {
		return createCompletion(inCompletion, inCodeList, inMessages,
				"ui.question.view.label.completion.new", "vif-title"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private VerticalLayout createCompletion(GeneralDomainObject inCompletion,
			CodeList inCodeList, IMessages inMessages, String inMsgKey,
			String inStyle) {
		return createCompletion(BeanWrapperHelper.getString(
				CompletionHome.KEY_COMPLETION, inCompletion),
				BeanWrapperHelper.getString(CompletionHome.KEY_STATE,
						inCompletion), inCodeList, inMessages, inMsgKey,
				inStyle);
	}

	private VerticalLayout createCompletion(Completion inCompletion,
			CodeList inCodeList, IMessages inMessages, String inMsgKey,
			String inStyle) {
		return createCompletion(inCompletion.getCompletionText(),
				inCompletion.getState(), inCodeList, inMessages, inMsgKey,
				inStyle);
	}

	private VerticalLayout createCompletion(String inCompletionText,
			String inCompletionState, CodeList inCodeList,
			IMessages inMessages, String inMsgKey, String inStyle) {
		VerticalLayout outLayout = new VerticalLayout();
		outLayout.setStyleName("vif-completion"); //$NON-NLS-1$

		// label and state
		HorizontalLayout lTitleView = new HorizontalLayout();
		lTitleView.setStyleName("vif-title-bar"); //$NON-NLS-1$
		lTitleView.setSpacing(true);

		lTitleView
				.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
						String.format(VIFViewHelper.TMPL_TITLE, inStyle,
								inMessages.getMessage(inMsgKey)),
						Label.CONTENT_XHTML))); //$NON-NLS-1$ //$NON-NLS-1$
		if (WorkflowAwareContribution.isUnpublished(inCompletionState)) {
			Label lNote = new Label(
					String.format(
							VIFViewHelper.TMPL_TITLE,
							"vif-note", getState(inCompletionState, inCodeList, inMessages)), Label.CONTENT_XHTML); //$NON-NLS-1$
			lTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(lNote));
		}
		outLayout.addComponent(lTitleView);

		// the completion
		outLayout
				.addComponent(new Label(inCompletionText, Label.CONTENT_XHTML));
		return outLayout;
	}

	/**
	 * Translates the contribution's state code to a human readable state
	 * information.
	 * 
	 * @param inState
	 *            String the state code
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inMessages
	 *            {@link IMessages}
	 * @return String the contribution's state properly translated
	 */
	protected String getState(String inState, CodeList inCodeList,
			IMessages inMessages) {
		if (inState == null || inState.length() == 0) {
			inState = "1"; //$NON-NLS-1$
		}
		return String
				.format(inMessages.getMessage("ui.question.view.label.state"), inCodeList.getLabel(inState)); //$NON-NLS-1$
	}

	/**
	 * Checks the user input.
	 * 
	 * @param inText
	 *            String the user input in the editor area
	 * @return boolean <code>true</code> if the user inputed white space only
	 */
	protected boolean checkEditorInput(String inText) {
		return RichTextSanitizer.checkInputEmpty(inText);
	}

}