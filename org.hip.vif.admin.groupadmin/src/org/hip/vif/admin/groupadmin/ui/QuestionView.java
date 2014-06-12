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
import java.util.List;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper.Completion;
import org.hip.vif.admin.groupadmin.data.QuestionContainer;
import org.hip.vif.admin.groupadmin.data.QuestionWrapper;
import org.hip.vif.admin.groupadmin.tasks.AdminQuestionShowTask;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.util.Dialog;
import org.hip.vif.web.util.Dialog.DialogWindow;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.interfaces.IPluggable;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the data of a question.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@SuppressWarnings("serial")
public class QuestionView extends AbstractQuestionView {

	/**
	 * Constructor to view the question.
	 * 
	 * @param inGroup
	 *            {@link Group}
	 * @param inQuestion
	 *            {@link Question}
	 * @param inParent
	 *            {@link QuestionContainer}
	 * @param inChildren
	 *            {@link QuestionContainer}
	 * @param inAuthors
	 *            {@link QueryResult}
	 * @param inReviewers
	 *            {@link QueryResult}
	 * @param inCompletions
	 *            {@link List} of <code>CompletionsHelper.Completion</code>
	 * @param inBibliography
	 *            {@link QueryResult}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inTask
	 *            {@link AdminQuestionShowTask}
	 * @throws VException
	 * @throws SQLException
	 */
	public QuestionView(final Group inGroup, final Question inQuestion,
			final QuestionContainer inParent,
			final QuestionContainer inChildren, final QueryResult inAuthors,
			final QueryResult inReviewers,
			final List<CompletionsHelper.Completion> inCompletions,
			final QueryResult inBibliography, final CodeList inCodeList,
			final AdminQuestionShowTask inTask) throws VException, SQLException {

		final IMessages lMessages = Activator.getMessages();
		final VerticalLayout lLayout = createLayout(inGroup, lMessages);

		if (((VIFWorkflowAware) inQuestion).isPrivate()) {
			lLayout.addComponent(createQuestionActionButtons(inTask, lMessages));
		}

		// question
		final String lLabel = String
				.format(lMessages
						.getMessage("ui.discussion.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
		lLayout.addComponent(createQuestion(inQuestion, lLabel, inCodeList,
				inAuthors, inReviewers, lMessages, inTask));

		// completions
		for (final Completion lCompletion : inCompletions) {
			lLayout.addComponent(createCompletion(lCompletion, inCodeList,
					lMessages, inTask));
		}

		// texts
		createBibliography(lLayout, inBibliography, lMessages, inTask);

		lLayout.addComponent(RiplaViewHelper.createSpacer());

		// parent question
		if (!inQuestion.isRoot()) {
			lLayout.addComponent(createParent(inParent, inTask, lMessages));
		}
		// follow up questions
		if (!inChildren.isEmpty()) {
			lLayout.addComponent(createChildren(inChildren, inTask, lMessages));
		}
	}

	/**
	 * @param inGroup
	 * @param inMessages
	 * @return VerticalLayout
	 */
	private VerticalLayout createLayout(final Group inGroup,
			final IMessages inMessages) {
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		final String lTitle = inMessages.getFormattedMessage(
				"ui.discussion.question.view.title.page", //$NON-NLS-1$
				BeanWrapperHelper.getLong(GroupHome.KEY_ID, inGroup),
				BeanWrapperHelper.getString(GroupHome.KEY_NAME, inGroup));
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
				"vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
		return lLayout;
	}

	private Component createQuestionActionButtons(
			final AdminQuestionShowTask inTask, final IMessages inMessages) {
		final DialogWindow lDialogPublish = Dialog
				.openQuestion(
						inMessages.getMessage("ui.discussion.dialog.title"), inMessages.getMessage("ui.discussion.dialog.question.publish"), new Dialog.ICommand() { //$NON-NLS-1$ //$NON-NLS-2$
							@Override
							public void execute() {
								if (!inTask.publish()) {
									Notification.show(
											inMessages
													.getMessage("errmsg.contribution.publish"), Type.WARNING_MESSAGE); //$NON-NLS-1$
								}
							}
						});
		final DialogWindow lDialogDelete = Dialog
				.openQuestion(
						inMessages.getMessage("ui.discussion.dialog.title"), inMessages.getMessage("ui.discussion.dialog.question.delete"), new Dialog.ICommand() { //$NON-NLS-1$ //$NON-NLS-2$
							@Override
							public void execute() {
								if (!inTask.delete()) {
									Notification.show(
											inMessages
													.getMessage("errmsg.contribution.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
								}
							}
						});

		final HorizontalLayout outButtons = new HorizontalLayout();
		outButtons.setSpacing(true);
		outButtons.setWidth("100%"); //$NON-NLS-1$

		final Button lEdit = new Button(
				inMessages.getMessage("ui.discussion.question.button.edit")); //$NON-NLS-1$
		lEdit.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				inTask.editQuestion();
			}
		});
		outButtons.addComponent(lEdit);

		final Button lPublish = new Button(
				inMessages
						.getMessage("ui.discussion.contribution.button.publish")); //$NON-NLS-1$
		lPublish.addClickListener(Dialog.createClickListener(lDialogPublish,
				this));
		outButtons.addComponent(lPublish);

		final Button lDelete = new Button(
				inMessages
						.getMessage("ui.discussion.contribution.button.delete")); //$NON-NLS-1$
		lDelete.addClickListener(Dialog
				.createClickListener(lDialogDelete, this));
		outButtons.addComponent(lDelete);

		outButtons.setExpandRatio(lEdit, 1);
		outButtons.setComponentAlignment(lEdit, Alignment.MIDDLE_RIGHT);
		return outButtons;
	}

	private Component createParent(final QuestionContainer inParent,
			final IPluggable inTask, final IMessages inMessages) {
		return createTable(
				inMessages
						.getMessage("ui.discussion.question.view.question.parent"), inParent, inTask); //$NON-NLS-1$
	}

	private Component createChildren(final QuestionContainer inChildren,
			final IPluggable inTask, final IMessages inMessages)
			throws VException, SQLException {
		return createTable(
				inMessages
						.getMessage("ui.discussion.question.view.question.follow.up"), inChildren, inTask); //$NON-NLS-1$
	}

	private VerticalLayout createTable(final String inCaption,
			final Container inDataSource, final IPluggable inTask) {
		final VerticalLayout out = new VerticalLayout();
		out.setStyleName("vif-question-table"); //$NON-NLS-1$

		out.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
				"vif-caption", inCaption), ContentMode.HTML)); //$NON-NLS-1$

		final Table lTable = new Table();
		lTable.setStyleName("vif-table"); //$NON-NLS-1$
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.setPageLength(0);

		lTable.setContainerDataSource(inDataSource);
		lTable.setVisibleColumns(QuestionContainer.NATURAL_COL_ORDER);
		lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
				QuestionContainer.COL_HEADERS, Activator.getMessages()));
		lTable.addGeneratedColumn(QuestionContainer.QUESTION,
				new Table.ColumnGenerator() {
					@Override
					public Component generateCell(final Table inSource,
							final Object inItemId, final Object inColumnId) {
						return new Label(((QuestionWrapper) inItemId)
								.getQuestion(), ContentMode.HTML);
					}
				});
		lTable.setColumnExpandRatio(QuestionContainer.QUESTION, 1);

		lTable.addValueChangeListener((ValueChangeListener) inTask);

		out.addComponent(lTable);
		return out;
	}

}
