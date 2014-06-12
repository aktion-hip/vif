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
package org.hip.vif.admin.groupadmin.tasks;

import java.sql.SQLException;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper;
import org.hip.vif.admin.groupadmin.data.QuestionContainer;
import org.hip.vif.admin.groupadmin.data.QuestionWrapper;
import org.hip.vif.admin.groupadmin.ui.QuestionView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.bom.VifBOMHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * Shows the content of a question, i.e. the question, its remark and the
 * completions added.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@SuppressWarnings("serial")
@UseCaseController
public class AdminQuestionShowTask extends AbstractAdminTask implements
		Property.ValueChangeListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminQuestionShowTask.class);

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_QUESTION_PUBLISH;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_QUESTION_SHOW);

			final Long lActorID = getActor().getActorID();
			final Long lGroupID = getGroupID();
			final Long lQuestionID = getQuestionID();
			final String lQuestionIDs = lQuestionID.toString();
			final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
					lQuestionID);
			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			return new QuestionView(
					VifBOMHelper.getGroupHome().getGroup(lGroupID),
					lQuestion,
					lQuestion.isRoot() ? QuestionContainer.createEmpty()
							: QuestionContainer.createData(BOMHelper
									.getQuestionHierarchyHome()
									.getParentQuestion(lQuestionID), lCodeList),
					QuestionContainer.createData(
							BOMHelper.getJoinQuestionToChildHome()
									.getChildsFiltered(lQuestionID, lActorID),
							lCodeList), BOMHelper
							.getJoinQuestionToAuthorReviewerHome().getAuthors(
									lQuestionIDs), BOMHelper
							.getJoinQuestionToAuthorReviewerHome()
							.getReviewers(lQuestionIDs), CompletionsHelper
							.getNormalizedCompletions(BOMHelper
									.getJoinCompletionToMemberHome()
									.getFiltered(lQuestionID, lActorID)),
					BOMHelper.getJoinQuestionToTextHome().selectPublished(
							lQuestionID), lCodeList, this);
		}
		catch (final Exception exc) {
			throw createContactAdminException(exc);
		}
	}

	@Override
	public void valueChange(final ValueChangeEvent inEvent) {
		final Object lItem = inEvent.getProperty().getValue();
		if (lItem instanceof QuestionWrapper) {
			setQuestionID(((QuestionWrapper) lItem).getQuestionID());
			sendEvent(AdminQuestionShowTask.class);
		}
	}

	/**
	 * Callback method to edit the actual question.
	 */
	public void editQuestion() {
		sendEvent(AdminQuestionEditTask.class);
	}

	/**
	 * Callback method to publish the actual question.
	 * 
	 * @return {@link Boolean} <code>true</code> if successful
	 */
	public boolean publish() {
		return doStateTransition(
				WorkflowAwareContribution.TRANS_ADMIN_PUBLISH,
				"admin.msg.question.publish", "Error encountered while publishing the question!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Callback method to publish the delete question.
	 * 
	 * @return {@link Boolean} <code>true</code> if successful
	 */
	public boolean delete() {
		return doStateTransition(
				WorkflowAwareContribution.TRANS_ADMIN_DELETE2,
				"admin.msg.question.delete", "Error encountered while deleting the question!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean doStateTransition(final String inTransition,
			final String inMsgKey, final String inErrMsg) {
		try {
			final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
					getQuestionID());
			((WorkflowAware) lQuestion).doTransition(inTransition,
					new Object[] { getActor().getActorID() });
			showNotification(Activator.getMessages().getFormattedMessage(
					inMsgKey,
					BeanWrapperHelper.getString(
							QuestionHome.KEY_QUESTION_DECIMAL, lQuestion)));
			sendEvent(AdminQuestionListTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error(inErrMsg, exc);
		}
		catch (final SQLException exc) {
			LOG.error(inErrMsg, exc);
		}
		catch (final WorkflowException exc) {
			LOG.error(inErrMsg, exc);
		}
		return false;
	}

	/**
	 * Callback method to edit the completion with the specified ID.
	 * 
	 * @param inCompletionID
	 *            Long
	 */
	public void editCompletion(final Long inCompletionID) {
		setCompletionID(inCompletionID);
		sendEvent(AdminCompletionEditTask.class);
	}

	/**
	 * Callback method to publish the completion with the specified ID.
	 * 
	 * @param inCompletionID
	 *            Long
	 * @return boolean <code>true</code> is successful
	 */
	public boolean publishCompletion(final Long inCompletionID) {
		return doStateTransition(
				inCompletionID,
				WorkflowAwareContribution.TRANS_ADMIN_PUBLISH,
				"admin.msg.completion.publish", "Error encountered while publishing the completion!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Callback method to delete the completion with the specified ID.
	 * 
	 * @param inCompletionID
	 *            Long
	 * @return boolean <code>true</code> is successful
	 */
	public boolean deleteCompletion(final Long inCompletionID) {
		return doStateTransition(
				inCompletionID,
				WorkflowAwareContribution.TRANS_DELETE,
				"admin.msg.completion.delete", "Error encountered while deleting the completion!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean doStateTransition(final Long inCompletionID,
			final String inTransition, final String inMsgKey,
			final String inErrMsg) {
		try {
			final Completion lCompletion = BOMHelper.getCompletionHome()
					.getCompletion(inCompletionID);
			((WorkflowAware) lCompletion).doTransition(inTransition,
					new Object[] { getActor().getActorID() });
			showNotification(Activator.getMessages().getMessage(inMsgKey));
			sendEvent(AdminQuestionShowTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error(inErrMsg, exc);
		}
		catch (final SQLException exc) {
			LOG.error(inErrMsg, exc);
		}
		catch (final WorkflowException exc) {
			LOG.error(inErrMsg, exc);
		}
		return false;
	}

}
