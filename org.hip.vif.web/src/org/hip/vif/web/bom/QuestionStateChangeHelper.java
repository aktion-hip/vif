/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
package org.hip.vif.web.bom;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.hip.vif.core.util.INodeCheckedProcessor;
import org.hip.vif.core.util.INodeProcessor;
import org.hip.vif.core.util.QuestionStateChecker;
import org.hip.vif.core.util.QuestionTreeIterator;

/**
 * Helper class to change a question's state (e.g. to 'answered' or to 'open').
 * 
 * @author Benno Luthiger Created on Jul 11, 2005
 */
public class QuestionStateChangeHelper extends VObject {
	private final QuestionHome questionHome;
	private final GroupHome groupHome;

	private class SetAnsweredFromOpenAction extends Object implements
			INodeProcessor {
		protected Object[] args;

		public SetAnsweredFromOpenAction(final Object[] inArgs) {
			super();
			args = inArgs;
		}

		@Override
		public void processNode(final Long inQuestionID)
				throws WorkflowException, VException, SQLException {
			((WorkflowAwareContribution) questionHome.getQuestion(inQuestionID
					.toString())).doTransition(
					WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, args);
		}
	}

	// private class SetAnsweredFromRequestedAction extends
	// SetAnsweredFromOpenAction {
	// public SetAnsweredFromRequestedAction(Object[] inArgs) {
	// super(inArgs);
	// }
	// public void processNode(Long inQuestionID) throws WorkflowException,
	// VException, SQLException {
	// ((WorkflowAwareContribution)questionHome.getQuestion(inQuestionID.toString())).doTransition(WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED1,
	// args);
	// }
	// }

	private class ReopenFromAnsweredProcessor extends Object implements
			INodeCheckedProcessor {
		protected Object[] args;

		public ReopenFromAnsweredProcessor(final Object[] inArgs) {
			super();
			args = inArgs;
		}

		@Override
		public boolean checkPreCondition(final Long inQuestionID) {
			try {
				return WorkflowAwareContribution.STATE_ANSWERED
						.equals(questionHome
								.getQuestion(inQuestionID.toString())
								.get(QuestionHome.KEY_STATE).toString());
			}
			catch (final Exception exc) {
				return false;
			}
		}

		@Override
		public void doAction(final Long inQuestionID) throws WorkflowException,
				VException, SQLException {
			((WorkflowAwareContribution) questionHome.getQuestion(inQuestionID
					.toString())).doTransition(
					WorkflowAwareContribution.TRANS_ADMIN_REOPEN1, args);
		}
	}

	// private class ReopenFromRequestedProcessor extends
	// ReopenFromAnsweredProcessor {
	// public ReopenFromRequestedProcessor(Object[] inArgs) {
	// super(inArgs);
	// }
	// public void doAction(Long inQuestionID) throws WorkflowException,
	// VException, SQLException {
	// ((WorkflowAwareContribution)questionHome.getQuestion(inQuestionID.toString())).doTransition(WorkflowAwareContribution.TRANS_ADMIN_REOPEN2,
	// args);
	// }
	// }

	/**
	 * QuestionStateChangeHelper default constructor.
	 */
	public QuestionStateChangeHelper(final QuestionHome inQuestionHome,
			final GroupHome inGroupHome) {
		super();
		questionHome = inQuestionHome;
		groupHome = inGroupHome;
	}

	/**
	 * Group admins can set the question's state to 'answered' directly.
	 * 
	 * @param inQuestion
	 *            WorkflowAwareContribution
	 * @param inArgs
	 *            Object[]
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	public void setAnsweredFromOpen(final WorkflowAwareContribution inQuestion,
			final Object[] inArgs) throws WorkflowException, VException,
			SQLException {
		inQuestion.doTransition(
				WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED2, inArgs);
		setAnsweredAncestors(new SetAnsweredFromOpenAction(inArgs), inQuestion,
				inArgs);
	}

	/**
	 * Set the question's state to 'answered' after this state change has been
	 * requested.
	 * 
	 * @param inQuestion
	 *            WorkflowAwareContribution
	 * @param inArgs
	 *            Object[]
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	public void setAnsweredFromRequested(
			final WorkflowAwareContribution inQuestion, final Object[] inArgs)
			throws WorkflowException, VException, SQLException {
		inQuestion.doTransition(
				WorkflowAwareContribution.TRANS_ADMIN_SET_ANSWERED1, inArgs);
		setAnsweredAncestors(new SetAnsweredFromOpenAction(inArgs), inQuestion,
				inArgs);
	}

	/**
	 * Climbs the tree as far as possible and sets the nodes to answered. If
	 * root node is reached, the group state is changed too.
	 * 
	 * @param inAction
	 * @param inContext
	 * @param inQuestion
	 * @param inArgs
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	private void setAnsweredAncestors(final SetAnsweredFromOpenAction inAction,
			final WorkflowAwareContribution inQuestion, final Object[] inArgs)
			throws WorkflowException, VException, SQLException {
		final QuestionStateChecker lChecker = new QuestionStateChecker();
		lChecker.setAction(inAction);
		lChecker.setStates(WorkflowAwareContribution.STATES_ANSWERED);

		final QuestionTreeIterator lTree = new QuestionTreeIterator(new Long(
				inQuestion.get(QuestionHome.KEY_ID).toString()));
		final GroupStateChangeParameters lParameters = new GroupStateChangeParameters();
		changeGroupStateChecked(lTree.start(false, lChecker),
				VIFGroupWorkflow.TRANS_SETTLE, new Object[] { lParameters });
	}

	/**
	 * Rejects setting the state of the specified question to 'answered' and
	 * thus set's the question's state to 'open'.
	 * 
	 * @param inQuestion
	 * @param inArgs
	 * @throws WorkflowException
	 */
	public void rejectAnswered(final WorkflowAwareContribution inQuestion,
			final Object[] inArgs) throws WorkflowException {
		inQuestion.doTransition(WorkflowAwareContribution.TRANS_ADMIN_REJECT,
				inArgs);
	}

	/**
	 * Group admins can reopen questions directly.
	 * 
	 * @param inQuestion
	 *            WorkflowAwareContribution
	 * @param inArgs
	 *            Object[]
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	public void setOpenFromAnswered(final WorkflowAwareContribution inQuestion,
			final Object[] inArgs) throws WorkflowException, VException,
			SQLException {
		inQuestion.doTransition(WorkflowAwareContribution.TRANS_ADMIN_REOPEN1,
				inArgs);
		reopenAncestors(new ReopenFromAnsweredProcessor(inArgs), inQuestion,
				inArgs);
	}

	/**
	 * Reopens a question after this state change has been requested.
	 * 
	 * @param inQuestion
	 * @param inArgs
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	public void setOpenFromRequested(
			final WorkflowAwareContribution inQuestion, final Object[] inArgs)
			throws WorkflowException, VException, SQLException {
		inQuestion.doTransition(WorkflowAwareContribution.TRANS_ADMIN_REOPEN2,
				inArgs);
		reopenAncestors(new ReopenFromAnsweredProcessor(inArgs), inQuestion,
				inArgs);
	}

	/**
	 * Climbs the tree as far as possible and sets the nodes to open. If root
	 * node is reached, the group state is changed too.
	 * 
	 * @param inVisitor
	 *            ReopeningProcessor
	 * @param inQuestion
	 * @param inArgs
	 * @throws WorkflowException
	 * @throws VException
	 * @throws SQLException
	 */
	private void reopenAncestors(final ReopenFromAnsweredProcessor inVisitor,
			final WorkflowAwareContribution inQuestion, final Object[] inArgs)
			throws WorkflowException, VException, SQLException {
		final QuestionTreeIterator lTree = new QuestionTreeIterator(new Long(
				inQuestion.get(QuestionHome.KEY_ID).toString()));
		final GroupStateChangeParameters lParameters = new GroupStateChangeParameters();
		changeGroupStateChecked(lTree.start(false, inVisitor),
				VIFGroupWorkflow.TRANS_REACTIVATE2,
				new Object[] { lParameters });
	}

	/**
	 * Rejects reopening of the specified question and thus set's the question's
	 * state to 'answered'.
	 * 
	 * @param inQuestion
	 *            WorkflowAwareContribution
	 * @param inArgs
	 *            Object[]
	 * @throws WorkflowException
	 */
	public void rejectReopen(final WorkflowAwareContribution inQuestion,
			final Object[] inArgs) throws WorkflowException {
		inQuestion.doTransition(
				WorkflowAwareContribution.TRANS_ADMIN_REJECT_REOPEN, inArgs);
	}

	/**
	 * Change the state of the discussion group the question with the specified
	 * ID is root. The state change only happens if the question is a root
	 * question.
	 * 
	 * @param inQuestionID
	 *            String
	 * @param inTransition
	 *            String
	 * @param inArgs
	 *            Object[]
	 * @throws VException
	 * @throws SQLException
	 * @throws WorkflowException
	 */
	private void changeGroupStateChecked(final Long inQuestionID,
			final String inTransition, final Object[] inArgs)
			throws VException, SQLException, WorkflowException {
		if (inQuestionID == null)
			return;
		final Question lQuestion = questionHome.getQuestion(inQuestionID
				.toString());
		if (lQuestion.isRoot()) {
			((WorkflowAware) groupHome.getGroup(lQuestion.get(
					QuestionHome.KEY_GROUP_ID).toString())).doTransition(
					inTransition, inArgs);
		}
	}

}
