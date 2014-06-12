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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper.Completion;
import org.hip.vif.admin.groupadmin.tasks.AbstractAdminTask;
import org.hip.vif.admin.groupadmin.tasks.AdminQuestionShowTask;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BibliographyFormatter;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.RichTextSanitizer;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.Dialog;
import org.ripla.web.util.Dialog.AbstractDialogWindow;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Base class for views that display contributions. This class provides
 * functionality to create view components that can placed on the views.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@SuppressWarnings("serial")
public class AbstractContributionView extends CustomComponent {

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
	protected void addProperQuestion(final DomainObject inQuestion,
			final IMessages inMessages, final VerticalLayout inLayout) {
		// the proper question
		inLayout.addComponent(new Label(BeanWrapperHelper.getString(
				QuestionHome.KEY_QUESTION, inQuestion), ContentMode.HTML));

		// the question's substantiation
		inLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-subtitle", inMessages.getMessage("ui.discussion.question.view.label.remark")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
		inLayout.addComponent(new Label(BeanWrapperHelper.getString(
				QuestionHome.KEY_REMARK, inQuestion), ContentMode.HTML));
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
	protected String getState(String inState, final CodeList inCodeList,
			final IMessages inMessages) {
		if (inState == null || inState.length() == 0) {
			inState = "1"; //$NON-NLS-1$
		}
		return String
				.format(inMessages
						.getMessage("ui.discussion.question.view.label.state"), inCodeList.getLabel(inState)); //$NON-NLS-1$
	}

	/**
	 * Renders the specified completion.
	 * 
	 * @param inCompletion
	 *            {@link Completion}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inMessages
	 *            {@link IMessages}
	 * @param inTask
	 *            {@link IPluggable}
	 * @return {@link Component}
	 * @throws VException
	 * @throws SQLException
	 */
	protected Component createCompletion(final Completion inCompletion,
			final CodeList inCodeList, final IMessages inMessages,
			final IPluggableWithLookup inTask) throws VException, SQLException {
		final VerticalLayout outLayout = createCompletion(inCompletion,
				inCodeList, inMessages, inTask,
				"ui.discussion.question.view.label.completion", "vif-subtitle"); //$NON-NLS-1$ //$NON-NLS-2$

		// author/reviewer
		final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inCompletion.getAuthors(),
						inCompletion.getReviewers(), inTask);
		outLayout
				.addComponent(lRenderer.render(
						inMessages
								.getMessage("ui.discussion.question.view.label.author"), //$NON-NLS-1$
						inMessages
								.getMessage("ui.discussion.question.view.label.reviewer"), //$NON-NLS-1$
						inCompletion.getFormattedDate())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return outLayout;
	}

	protected Component createCompletion(
			final GeneralDomainObject inCompletion, final CodeList inCodeList,
			final IMessages inMessages, final IPluggable inTask)
			throws VException, SQLException {
		final VerticalLayout outLayout = createCompletion(inCompletion,
				inCodeList, inMessages, inTask,
				"ui.discussion.question.view.label.completion", "vif-subtitle"); //$NON-NLS-1$ //$NON-NLS-2$
		return outLayout;
	}

	private VerticalLayout createCompletion(final Completion inCompletion,
			final CodeList inCodeList, final IMessages inMessages,
			final IPluggable inTask, final String inMsgKey, final String inStyle) {
		return createCompletion(inCompletion.getCompletionID(),
				inCompletion.getCompletionText(), inCompletion.getState(),
				inCodeList, inTask, inMessages, inMsgKey, inStyle);
	}

	private VerticalLayout createCompletion(
			final GeneralDomainObject inCompletion, final CodeList inCodeList,
			final IMessages inMessages, final IPluggable inTask,
			final String inMsgKey, final String inStyle) {
		return createCompletion(BeanWrapperHelper.getLong(
				CompletionHome.KEY_ID, inCompletion),
				BeanWrapperHelper.getString(CompletionHome.KEY_COMPLETION,
						inCompletion), BeanWrapperHelper.getString(
						CompletionHome.KEY_STATE, inCompletion), inCodeList,
				inTask, inMessages, inMsgKey, inStyle);
	}

	private VerticalLayout createCompletion(final Long inCompletionID,
			final String inCompletionText, final String inCompletionState,
			final CodeList inCodeList, final IPluggable inTask,
			final IMessages inMessages, final String inMsgKey,
			final String inStyle) {
		final VerticalLayout outLayout = new VerticalLayout();
		outLayout.setStyleName("vif-completion"); //$NON-NLS-1$

		// label and state
		final HorizontalLayout lTitleView = new HorizontalLayout();
		lTitleView.setStyleName("vif-title-bar"); //$NON-NLS-1$
		lTitleView.setSpacing(true);

		lTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(new Label(
				String.format(VIFViewHelper.TMPL_TITLE, inStyle,
						inMessages.getMessage(inMsgKey)), ContentMode.HTML))); //$NON-NLS-1$ //$NON-NLS-1$
		final boolean lEditable = inCompletionState == null
				|| inCompletionState.length() == 0
				|| WorkflowAwareContribution.STATE_PRIVATE
						.equals(inCompletionState);
		final String lStyle = lEditable ? "vif-note-emphasized" : "vif-note"; //$NON-NLS-1$ //$NON-NLS-2$
		final Label lNote = new Label(String.format(VIFViewHelper.TMPL_TITLE,
				lStyle, getState(inCompletionState, inCodeList, inMessages)),
				ContentMode.HTML); //$NON-NLS-1$
		lTitleView.addComponent(RiplaViewHelper.makeUndefinedWidth(lNote));
		if (lEditable) {
			final Button lEdit = new Button(
					inMessages
							.getMessage("ui.discussion.completion.button.edit")); //$NON-NLS-1$
			lEdit.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (inTask instanceof AdminQuestionShowTask) {
						((AdminQuestionShowTask) inTask)
								.editCompletion(inCompletionID);
					}
				}
			});
			lTitleView.addComponent(lEdit);

			final AbstractDialogWindow lDialogPublish = Dialog
					.openQuestion(
							inMessages.getMessage("ui.discussion.dialog.title"), inMessages.getMessage("ui.discussion.dialog.completion.publish"), new Dialog.ICommand() { //$NON-NLS-1$ //$NON-NLS-2$
								@Override
								public void execute() {
									if (inTask instanceof AdminQuestionShowTask) {
										if (!((AdminQuestionShowTask) inTask)
												.publishCompletion(inCompletionID)) {
											Notification.show(
													inMessages
															.getMessage("errmsg.contribution.publish"), Type.WARNING_MESSAGE); //$NON-NLS-1$
										}
									}
								}
							});
			final AbstractDialogWindow lDialogDelete = Dialog
					.openQuestion(
							inMessages.getMessage("ui.discussion.dialog.title"), inMessages.getMessage("ui.discussion.dialog.completion.delete"), new Dialog.ICommand() { //$NON-NLS-1$ //$NON-NLS-2$
								@Override
								public void execute() {
									if (inTask instanceof AdminQuestionShowTask) {
										if (!((AdminQuestionShowTask) inTask)
												.deleteCompletion(inCompletionID)) {
											Notification.show(
													inMessages
															.getMessage("errmsg.contribution.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
										}
									}
								}
							});

			final Button lPublish = new Button(
					inMessages
							.getMessage("ui.discussion.contribution.button.publish")); //$NON-NLS-1$
			lPublish.addClickListener(Dialog.createClickListener(
					lDialogPublish, this));
			lTitleView.addComponent(lPublish);

			final Button lDelete = new Button(
					inMessages
							.getMessage("ui.discussion.contribution.button.delete")); //$NON-NLS-1$
			lDelete.addClickListener(Dialog.createClickListener(lDialogDelete,
					this));
			lTitleView.addComponent(lDelete);
		}
		outLayout.addComponent(lTitleView);

		// the completion
		outLayout.addComponent(new Label(inCompletionText, ContentMode.HTML));
		return outLayout;
	}

	/**
	 * Renders the set of bibliographical entries assigned to the question.
	 * 
	 * @param inLayout
	 *            {@link VerticalLayout}
	 * @param inBibliography
	 *            {@link QueryResult}
	 * @param inMessages
	 *            {@link IMessages}
	 * @param inTask
	 *            {@link AbstractAdminTask}
	 * @throws VException
	 * @throws SQLException
	 */
	protected void createBibliography(final VerticalLayout inLayout,
			final QueryResult inBibliography, final IMessages inMessages,
			final AbstractAdminTask inTask) throws VException, SQLException {
		final VerticalLayout lBibliography = new VerticalLayout();
		lBibliography.setStyleName("vif-title-bar"); //$NON-NLS-1$
		lBibliography
				.addComponent(new Label(
						String.format(
								VIFViewHelper.TMPL_TITLE,
								"vif-subtitle", inMessages.getMessage("ui.discussion.question.view.label.bibliography")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		boolean hasBibliography = false;
		while (inBibliography.hasMoreElements()) {
			lBibliography.addComponent(createBibliography(
					inBibliography.next(), inTask, inMessages));
			hasBibliography = true;
		}

		if (hasBibliography) {
			inLayout.addComponent(lBibliography);
		}
	}

	private Component createBibliography(
			final GeneralDomainObject inBibliography,
			final AbstractAdminTask inTask, final IMessages inMessages)
			throws VException {
		final HorizontalLayout out = new HorizontalLayout();
		out.setWidth("100%"); //$NON-NLS-1$
		out.setStyleName("vif-bibliography"); //$NON-NLS-1$

		final Long lBiblioID = BeanWrapperHelper.getLong(TextHome.KEY_ID,
				inBibliography);

		final Button lRemove = new Button(
				inMessages.getMessage("ui.bibliography.link.button.remove")); //$NON-NLS-1$
		lRemove.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				if (!inTask.unlinkBibliography(lBiblioID)) {
					Notification.show(
							inMessages.getMessage("errmsg.biblio.remove"), Type.WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		out.addComponent(lRemove);

		final Button lLink = LinkButtonHelper.createLinkButton(
				BeanWrapperHelper.getString(TextHome.KEY_REFERENCE,
						inBibliography),
				LinkButtonHelper.LookupType.BIBLIOGRAPHY, lBiblioID, inTask);
		lLink.setStyleName(BaseTheme.BUTTON_LINK);
		out.addComponent(lLink);

		final BibliographyFormatter lFormatter = new BibliographyFormatter(
				new BibliographyAdapter(inBibliography,
						TextHome.KEY_BIBLIO_TYPE));
		final Label lLabel = new Label(lFormatter.renderHtml(),
				ContentMode.HTML);
		lLabel.setWidth("100%"); //$NON-NLS-1$
		out.addComponent(lLabel);
		out.setExpandRatio(lLabel, 1.0f);

		return out;
	}

	/**
	 * Checks the user input.
	 * 
	 * @param inText
	 *            String the user input in the editor area
	 * @return boolean <code>true</code> if the user inputed white space only
	 */
	protected boolean checkEditorInput(final String inText) {
		return RichTextSanitizer.checkInputEmpty(inText);
	}

}
