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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper.EntryType;
import org.hip.vif.admin.groupadmin.ui.PendingListView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.QuestionStateChangeHelper;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/**
 * Shows the list of pending tasks of a group administrator, e.g. the list of
 * questions in the group where a state change has been requested.
 * 
 * @author Luthiger Created: 23.11.2011
 */
@SuppressWarnings("serial")
@UseCaseController
public class AdminShowPendingTask extends AbstractWebController implements
		Property.ValueChangeListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminShowPendingTask.class);

	private ContributionContainer setAnswerables;
	private ContributionContainer setReopen;

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_QUESTION_PUBLISH;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_PENDING);

			final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();

			final KeyObject lKeyAnswerables = new KeyObjectImpl();
			lKeyAnswerables.setValue(QuestionHome.KEY_STATE, new Integer(
					WorkflowAwareContribution.S_ANSWERED_REQUESTED));
			lKeyAnswerables.setValue(QuestionHome.KEY_GROUP_ID, getGroupID());
			final int lCountAnswerables = lQuestionHome
					.getCount(lKeyAnswerables);

			final KeyObject lKeyReopen = new KeyObjectImpl();
			lKeyReopen.setValue(QuestionHome.KEY_STATE, new Integer(
					WorkflowAwareContribution.S_REOPEN_REQUESTED));
			lKeyReopen.setValue(QuestionHome.KEY_GROUP_ID, getGroupID());
			final int lCountReopen = lQuestionHome.getCount(lKeyReopen);

			// If no pending tasks, redisplay the last view with message added.
			if (lCountAnswerables + lCountReopen == 0) {
				return reDisplay(
						Activator.getMessages().getMessage(
								"admin.msg.pending.none"), Notification.Type.HUMANIZED_MESSAGE); //$NON-NLS-1$
			}

			final Group lGroup = VifBOMHelper.getGroupHome().getGroup(
					getGroupID());
			final String lTitle = Activator
					.getMessages()
					.getFormattedMessage(
							"ui.discussion.question.view.title.pending", lGroup.get(GroupHome.KEY_ID), lGroup.get(GroupHome.KEY_NAME)); //$NON-NLS-1$
			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			setAnswerables = ContributionContainer.createQuestions(
					lQuestionHome.select(lKeyAnswerables), lCodeList);
			setReopen = ContributionContainer.createQuestions(
					lQuestionHome.select(lKeyReopen), lCodeList);
			return new PendingListView(setAnswerables, setReopen, lTitle, this);
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	@Override
	public void valueChange(final ValueChangeEvent inEvent) {
		final Object lEntry = inEvent.getProperty().getValue();
		if (lEntry instanceof ContributionWrapper) {
			final ContributionWrapper lContribution = (ContributionWrapper) lEntry;
			if (lContribution.getEntryType().equals(EntryType.QUESTION)) {
				setQuestionID(new Long(lContribution.getID()));
				sendEvent(AdminQuestionShowTask.class);
			}
		}
	}

	/**
	 * Callback method, sets selected questions to state 'answered'.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean processAnswered() {
		final AnsweredProcessor lProcessor = new AnsweredProcessor(
				setAnswerables, getActor().getActorID());
		return lProcessor.process("admin.msg.question.state.changed"); //$NON-NLS-1$
	}

	/**
	 * Callback method, sets selected questions back to state 'open'.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean rejectAnswered() {
		final RejectAnsweredProcessor lProcessor = new RejectAnsweredProcessor(
				setAnswerables, getActor().getActorID());
		return lProcessor.process("admin.msg.question.state.rejected"); //$NON-NLS-1$
	}

	/**
	 * Callback method, sets selected questions to state 'open'.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean processReopen() {
		final ReopenProcessor lProcessor = new ReopenProcessor(setReopen,
				getActor().getActorID());
		return lProcessor.process("admin.msg.question.state.changed"); //$NON-NLS-1$
	}

	/**
	 * Callback method, sets selected questions back to state 'answered'.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean rejectReopen() {
		final RejectReopenProcessor lProcessor = new RejectReopenProcessor(
				setReopen, getActor().getActorID());
		return lProcessor.process("admin.msg.question.state.rejected"); //$NON-NLS-1$
	}

	// ---

	private abstract class Processor {
		private final ContributionContainer contributions;
		private final Object[] args;

		Processor(final ContributionContainer inContributions,
				final Long inActorID) {
			contributions = inContributions;
			args = new Object[] { inActorID };
		}

		boolean process(final String inMsgKey) {
			try {
				final QuestionHome lHome = BOMHelper.getQuestionHome();
				final QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(
						lHome, VifBOMHelper.getGroupHome());
				for (final ContributionWrapper lContribution : contributions
						.getItemIds()) {
					if (lContribution.isChecked()) {
						doTransition(
								(WorkflowAwareContribution) lHome.getQuestion(lContribution
										.getID()), args, lHelper);
					}
				}
				showNotification(Activator.getMessages().getMessage(inMsgKey));
				sendEvent(AdminQuestionListTask.class);
				return true;
			}
			catch (final WorkflowException exc) {
				LOG.error(
						"Error encountered while setting question to state 'answered'!", exc); //$NON-NLS-1$
			}
			catch (final VException exc) {
				LOG.error(
						"Error encountered while setting question to state 'answered'!", exc); //$NON-NLS-1$
			}
			catch (final SQLException exc) {
				LOG.error(
						"Error encountered while setting question to state 'answered'!", exc); //$NON-NLS-1$
			}
			return false;
		}

		abstract protected void doTransition(
				WorkflowAwareContribution inQuestion, Object[] inArgs,
				QuestionStateChangeHelper inHelper) throws WorkflowException,
				VException, SQLException;
	}

	private class AnsweredProcessor extends Processor {
		AnsweredProcessor(final ContributionContainer inContributions,
				final Long inActorID) {
			super(inContributions, inActorID);
		}

		@Override
		protected void doTransition(final WorkflowAwareContribution inQuestion,
				final Object[] inArgs, final QuestionStateChangeHelper inHelper)
				throws WorkflowException, VException, SQLException {
			inHelper.setAnsweredFromRequested(inQuestion, inArgs);
		}
	}

	private class RejectAnsweredProcessor extends Processor {
		RejectAnsweredProcessor(final ContributionContainer inContributions,
				final Long inActorID) {
			super(inContributions, inActorID);
		}

		@Override
		protected void doTransition(final WorkflowAwareContribution inQuestion,
				final Object[] inArgs, final QuestionStateChangeHelper inHelper)
				throws WorkflowException, VException, SQLException {
			inHelper.rejectAnswered(inQuestion, inArgs);
		}
	}

	private class ReopenProcessor extends Processor {
		ReopenProcessor(final ContributionContainer inContributions,
				final Long inActorID) {
			super(inContributions, inActorID);
		}

		@Override
		protected void doTransition(final WorkflowAwareContribution inQuestion,
				final Object[] inArgs, final QuestionStateChangeHelper inHelper)
				throws WorkflowException, VException, SQLException {
			inHelper.setOpenFromRequested(inQuestion, inArgs);
		}
	}

	private class RejectReopenProcessor extends Processor {
		RejectReopenProcessor(final ContributionContainer inContributions,
				final Long inActorID) {
			super(inContributions, inActorID);
		}

		@Override
		protected void doTransition(final WorkflowAwareContribution inQuestion,
				final Object[] inArgs, final QuestionStateChangeHelper inHelper)
				throws WorkflowException, VException, SQLException {
			inHelper.rejectReopen(inQuestion, inArgs);
		}
	}

}
