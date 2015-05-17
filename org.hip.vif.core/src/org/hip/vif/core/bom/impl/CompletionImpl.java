/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2002-2015, Benno Luthiger

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
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.CompletionHistoryHome;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.exc.Assert;
import org.hip.vif.core.exc.Assert.AssertLevel;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.QuestionHierarchyEntry;

/** This domain object implements the Completion interface.
 *
 * @author Benno Luthiger */
@SuppressWarnings("serial")
public class CompletionImpl extends WorkflowAwareContribution implements Completion, QuestionHierarchyEntry {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.CompletionHomeImpl";

    /** Constructor for CompletionImpl. */
    public CompletionImpl() throws WorkflowException {
        super();
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }

    @Override
    public Long ucNew(final String inCompletion, final String inQuestion, final Long inAuthorID) // NOPMD
            throws BOMChangeValueException {
        preCheck(inCompletion);

        try {
            final Long lQuestionID = Long.parseLong(inQuestion);
            set(CompletionHome.KEY_COMPLETION, inCompletion);
            set(CompletionHome.KEY_STATE, Long.valueOf(WorkflowAwareContribution.S_PRIVATE));
            set(CompletionHome.KEY_QUESTION_ID, lQuestionID);
            final Long outContributionID = insert(true);

            // create entry in authors table
            ((CompletionAuthorReviewerHomeImpl) BOMHelper.getCompletionAuthorReviewerHome()).setAuthor(inAuthorID,
                    outContributionID);
            return outContributionID;
        } catch (VException | SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    private void preCheck(final String inCompletion) {
        Assert.assertTrue(AssertLevel.ERROR, this, "preCheck", !"".equals(inCompletion));
    }

    /** Use case to save modified contribution data (without change of state).
     *
     * @param inContribution String
     * @param inAuthorID Long
     * @throws BOMChangeValueException */
    @Override
    public void ucSave(final String inContribution, final Long inAuthorID) throws BOMChangeValueException {
        ucSave(inContribution, null, inAuthorID);
    }

    /** Use case to save modified contribution data.
     *
     * @param inContribution java.lang.String
     * @param inState java.lang.String
     * @param inAuthorID java.lang.Long
     * @throws BOMChangeValueException */
    @Override
    public void ucSave(final String inContribution, final String inState, final Long inAuthorID)
            throws BOMChangeValueException {
        preCheck(inContribution);

        try {
            final Long lContributionId = Long.parseLong(get(CompletionHome.KEY_ID).toString());

            // historize changes
            final DomainObject lHistory = createHistory();

            set(CompletionHome.KEY_COMPLETION, inContribution);
            if (inState != null) {
                set(CompletionHome.KEY_STATE, Long.valueOf(inState));
            }

            // save changes only if new values differ from old ones
            if (isChanged()) {
                final Timestamp lMutationDate = new Timestamp(System.currentTimeMillis());
                lHistory.set(CompletionHistoryHome.KEY_VALID_TO, lMutationDate);
                lHistory.set(CompletionHistoryHome.KEY_MEMBER_ID, inAuthorID);
                lHistory.insert(true);

                set(CompletionHome.KEY_MUTATION, lMutationDate);
                update(true);

                // update author
                final KeyObject lKey = new KeyObjectImpl();
                lKey.setValue(CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lContributionId);
                lKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
                final DomainObject lAuthor = BOMHelper.getCompletionAuthorReviewerHome().findByKey(lKey);
                if (!inAuthorID.toString().equals(lAuthor.get(ResponsibleHome.KEY_MEMBER_ID).toString())) {
                    lAuthor.set(ResponsibleHome.KEY_MEMBER_ID, inAuthorID);
                    lAuthor.update(true);
                }
            }
        } catch (VException | SQLException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    @Override
    protected String getActualStateValue() throws GettingException { // NOPMD
        return get(CompletionHome.KEY_STATE).toString();
    }

    @Override
    protected void setState(final int inNewState, final Long inAuthorID) throws BOMChangeValueException { // NOPMD
        try {
            insertHistoryEntry(BOMHelper.getCompletionHistoryHome().create(), CompletionHistoryHome.KEY_VALID_TO,
                    CompletionHistoryHome.KEY_MEMBER_ID, inAuthorID);
            set(CompletionHome.KEY_STATE, Long.valueOf(inNewState));
        } catch (final VException exc) {
            throw new BOMChangeValueException(exc.getMessage(), exc);
        }
    }

    /** Returns the question this completion belongs to.
     *
     * @return Question
     * @throws VException */
    @Override
    public Question getOwningQuestion() throws VException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(QuestionHome.KEY_ID, get(CompletionHome.KEY_QUESTION_ID));
        return (Question) BOMHelper.getQuestionHome().findByKey(lKey);
    }

    /** Returns the decimal ID of the question this completion belongs to.
     *
     * @return String
     * @throws VException */
    @Override
    public String getDecimalID() throws VException {
        return (String) getOwningQuestion().get(QuestionHome.KEY_QUESTION_DECIMAL);
    }

    /** Returns the completion's ID.
     *
     * @return Long
     * @throws VException */
    @Override
    public Long getID() throws VException {
        return Long.parseLong(get(CompletionHome.KEY_ID).toString());
    }

    /** Returns the node ID of this contribution.
     *
     * @return Long
     * @throws VException */
    @Override
    public Long getNodeID() throws VException {
        return Long.parseLong(get(CompletionHome.KEY_QUESTION_ID).toString());
    }

    /** @param inCollector QuestionHierarchyVisitor
     * @throws VException */
    @Override
    public void accept(final QuestionHierarchyVisitor inCollector) throws VException, SQLException {
        inCollector.visitCompletion(this);
    }

    /** Returns true if the object is a node.
     *
     * @return boolean */
    @Override
    public boolean isNode() {
        return false;
    }

    /** Add this contribution to the full text search index. */
    @Override
    protected void addToIndex() throws WorkflowException {
        final KeyObject lKey = new KeyObjectImpl();
        try {
            lKey.setValue(CompletionHome.KEY_ID, getID());
            final VIFContentIndexer lIndexer = new VIFContentIndexer();
            lIndexer.addCompletionToIndex(lKey);
        } catch (VException | SQLException | IOException exc) {
            throw new WorkflowException(exc);
        }
    }

    @Override
    protected void removeFromIndex() throws WorkflowException { // NOPMD
        // Intentionally left empty: The entry is removed when the question is deleted.
    }

    @Override
    public void setReviewer(final Long inReviewerID) throws VException, SQLException { // NOPMD
        BOMHelper.getCompletionAuthorReviewerHome().setReviewer(inReviewerID, getID());
    }

    @Override
    public boolean checkRefused(final Long inReviewerID) throws VException, SQLException { // NOPMD
        return BOMHelper.getCompletionAuthorReviewerHome().checkRefused(inReviewerID, getID());
    }

    @Override
    protected DomainObject getHistoryObject() throws BOMException { // NOPMD
        return BOMHelper.getCompletionHistoryHome().create();
    }

}
