/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003-2014, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHistory;
import org.hip.vif.core.bom.QuestionHistoryHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/**
 * This domain object implements the Question interface.
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Question
 */
@SuppressWarnings("serial")
public class QuestionImpl extends WorkflowAwareContribution implements
		Question, QuestionHierarchyEntry {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionHomeImpl";

	/**
	 * Constructor for QuestionImpl.
	 */
	public QuestionImpl() throws WorkflowException {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName()
	 */
	@Override
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hip.vif.core.bom.Question#ucNew(java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public Long ucNew(final String inQuestion, final String inRemark,
			final Long inParent, final String inGroup, final Long inAuthorID)
			throws BOMChangeValueException {
		preCheck(inQuestion, inRemark);
		final boolean hasParent = inParent != 0;
		try {
			Long lRootQuestionValue = QuestionHome.NOT_ROOT;

			// calculate the question's decimal id
			final QuestionHierarchyHome lHierarchyHome = BOMHelper
					.getQuestionHierarchyHome();
			String lDecimalID = "";
			if (hasParent) {
				final int lDecimal = lHierarchyHome.countChilds(inParent) + 1;
				lDecimalID += BOMHelper.getQuestionHome().getQuestion(inParent)
						.get(QuestionHome.KEY_QUESTION_DECIMAL)
						+ "." + String.valueOf(lDecimal);
			} else {
				lRootQuestionValue = QuestionHome.IS_ROOT;
				final GroupHome lGroupHome = (GroupHome) VSys.homeManager
						.getHome(GroupImpl.HOME_CLASS_NAME);
				final Group lGroup = lGroupHome.getGroup(inGroup);
				final int lDecimal = lGroup.rootCount() + 1;
				lDecimalID = String.format("%s:%s",
						lGroup.get(GroupHome.KEY_ID), lDecimal);
			}

			// save question
			set(QuestionHome.KEY_QUESTION, inQuestion);
			set(QuestionHome.KEY_REMARK, inRemark);
			set(QuestionHome.KEY_QUESTION_DECIMAL, String.valueOf(lDecimalID));
			set(QuestionHome.KEY_STATE, new Long(
					WorkflowAwareContribution.S_PRIVATE));
			set(QuestionHome.KEY_GROUP_ID, new Long(inGroup));
			// root question: true = 1
			set(QuestionHome.KEY_ROOT_QUESTION, lRootQuestionValue);
			final Long outQuestionID = insert(true);

			// create entry in authors table
			BOMHelper.getQuestionAuthorReviewerHome().setAuthor(inAuthorID,
					outQuestionID);

			// add to hierarchy
			if (hasParent) {
				lHierarchyHome.ucNew(inParent, outQuestionID,
						Long.parseLong(inGroup));
			}
			return outQuestionID;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Use case to save edited question data (without change of state).
	 * 
	 * @param inQuestion
	 *            String
	 * @param inRemark
	 *            String
	 * @param inAuthorID
	 *            Long
	 * @throws BOMChangeValueException
	 */
	@Override
	public void ucSave(final String inQuestion, final String inRemark,
			final Long inAuthorID) throws BOMChangeValueException {
		ucSave(inQuestion, inRemark, null, inAuthorID);
	}

	/**
	 * Use case to save edited question data.
	 * 
	 * @param inQuestion
	 *            java.lang.String
	 * @param inRemark
	 *            java.lang.String
	 * @param inState
	 *            java.lang.String
	 * @param inAuthorID
	 *            java.lang.Long
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	@Override
	public void ucSave(final String inQuestion, final String inRemark,
			final String inState, final Long inAuthorID)
			throws BOMChangeValueException {
		preCheck(inQuestion, inRemark);
		try {
			final Long lQuestionId = new Long(get(QuestionHome.KEY_ID)
					.toString());

			// historize changes
			final QuestionHistory lHistory = (QuestionHistory) BOMHelper
					.getQuestionHistoryHome().create();
			fillHistory(lHistory);

			// update question
			set(QuestionHome.KEY_QUESTION, inQuestion);
			set(QuestionHome.KEY_REMARK, inRemark);
			if (inState != null) {
				set(QuestionHome.KEY_STATE, new Long(inState));
			}

			// save changes only if new values differ from old ones
			if (isChanged()) {
				final Timestamp lMutationDate = new Timestamp(
						System.currentTimeMillis());
				lHistory.set(QuestionHistoryHome.KEY_VALID_TO, lMutationDate);
				lHistory.set(QuestionHistoryHome.KEY_MEMBER_ID, inAuthorID);
				lHistory.insert(true);

				set(QuestionHome.KEY_MUTATION, lMutationDate);
				update(true);

				// update author
				final KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID,
						lQuestionId);
				lKey.setValue(ResponsibleHome.KEY_TYPE,
						ResponsibleHome.Type.AUTHOR.getValue());
				final DomainObject lAuthor = BOMHelper
						.getQuestionAuthorReviewerHome().findByKey(lKey);
				if (!inAuthorID.equals(new Long(lAuthor.get(
						ResponsibleHome.KEY_MEMBER_ID).toString()))) {
					lAuthor.set(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
					lAuthor.update(true);
				}

			}
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Asserts the existence of mandatory input
	 * 
	 * @param inQuestion
	 *            java.lang.String
	 * @param inRemark
	 *            java.lang.String
	 */
	private void preCheck(final String inQuestion, final String inRemark) {
		// pre: Question must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
				!"".equals(inQuestion));
		// pre: Remark must be set
		Assert.assertTrue(AssertLevel.ERROR, this, "preCheck",
				!"".equals(inRemark));
	}

	@Override
	protected String getActualStateValue() throws GettingException {
		return get(QuestionHome.KEY_STATE).toString();
	}

	@Override
	protected void setState(final int inNewState, final Long inAuthorID)
			throws BOMChangeValueException {
		try {
			insertHistoryEntry(BOMHelper.getQuestionHistoryHome().create(),
					QuestionHistoryHome.KEY_VALID_TO,
					QuestionHistoryHome.KEY_MEMBER_ID, inAuthorID);
			set(QuestionHome.KEY_STATE, new Long(inNewState));
		}
		catch (final Exception exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Returns the node ID of this contribution.
	 * 
	 * @return Long
	 * @throws VException
	 */
	@Override
	public Long getNodeID() throws VException {
		return new Long(get(QuestionHome.KEY_ID).toString());
	}

	/**
	 * Returns true if the object is a node.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isNode() {
		return true;
	}

	/**
	 * Sets the specified visitor (see visitor pattern).
	 * 
	 * @param inVisitor
	 *            QuestionHierarchyVisitor
	 * @throws VException
	 * @throws SQLException
	 */
	@Override
	public void accept(final QuestionHierarchyVisitor inVisitor)
			throws VException, SQLException {
		inVisitor.visitQuestion(this);
	}

	/**
	 * Checks whether this question is a root.
	 * 
	 * @return boolean true, if this question is root in the hierarchy
	 * @throws VException
	 */
	@Override
	public boolean isRoot() throws VException {
		return QuestionHome.IS_ROOT.equals(new Long(get(
				QuestionHome.KEY_ROOT_QUESTION).toString()));
	}

	/**
	 * Add this contribution to the full text search index.
	 */
	@Override
	protected void addToIndex() throws WorkflowException {
		final KeyObject lKey = new KeyObjectImpl();
		try {
			lKey.setValue(QuestionHome.KEY_ID, getNodeID());
			final VIFContentIndexer lIndexer = new VIFContentIndexer();
			lIndexer.addQuestionToIndex(lKey);
		}
		catch (final VException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final IOException exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}

	@Override
	protected void removeFromIndex() throws WorkflowException {
		try {
			final VIFContentIndexer lIndexer = new VIFContentIndexer();
			lIndexer.deleteQuestionInIndex(getNodeID().toString());
		}
		catch (final IOException exc) {
			throw new WorkflowException(exc.getMessage());
		}
		catch (final VException exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}

	@Override
	public void setReviewer(final Long inReviewerID) throws VException,
			SQLException {
		BOMHelper.getQuestionAuthorReviewerHome().setReviewer(inReviewerID,
				getNodeID());
	}

	@Override
	public boolean checkRefused(final Long inReviewerID) throws VException,
			SQLException {
		return BOMHelper.getQuestionAuthorReviewerHome().checkRefused(
				inReviewerID, new Long(get(QuestionHome.KEY_ID).toString()));
	}

	@Override
	protected DomainObject getHistoryObject() throws BOMException {
		return BOMHelper.getQuestionHistoryHome().create();
	}

}
