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
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.admin.groupadmin.data.ContributionWrapper.EntryType;
import org.hip.vif.admin.groupadmin.ui.ContributionsListView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.bom.impl.ContributionDeletionHandler;
import org.hip.vif.core.bom.impl.JoinCompletionToQuestionHome;
import org.hip.vif.core.bom.impl.QuestionBranchIterator;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.core.util.QuestionHierarchyEntry;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/** Task for group administrators to display the contributions they created for that they can be published.
 *
 * @author Luthiger Created: 23.11.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class AdminContributionsPublishablesTask extends AbstractWebController
        implements Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory
            .getLogger(AdminContributionsPublishablesTask.class);

    private ContributionContainer contributions;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_QUESTION_PUBLISH;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            loadContextMenu(Constants.MENU_SET_ID_PUBLISH);

            final Long lActorID = getActor().getActorID();
            final Long lGroupID = getGroupID();

            final CodeList lCodeList = CodeListHome.instance().getCodeList(
                    QuestionState.class, getAppLocale().getLanguage());
            final QueryResult lQuestions = BOMHelper
                    .getJoinAuthorReviewerToQuestionHome()
                    .getAuthorsUnpublishedQuestions(lActorID, lGroupID);
            final QueryResult lCompletions = BOMHelper
                    .getJoinAuthorReviewerToCompletionHome()
                    .getAuthorsUnpublishedCompletions(lActorID, lGroupID);
            final QueryResult lTexts = BOMHelper
                    .getJoinAuthorReviewerToTextHome()
                    .getAuthorsUnpublishedTexts(lActorID);
            contributions = ContributionContainer.createData(lQuestions,
                    lCompletions, lTexts, lCodeList);

            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
            if (!contributions.hasItems()) {
                final String lMessage = Activator
                        .getMessages()
                        .getFormattedMessage(
                                "ui.contributions.process.no.pending", lGroup.get(GroupHome.KEY_ID), lGroup.get(GroupHome.KEY_NAME)); //$NON-NLS-1$
                return reDisplay(lMessage, Notification.Type.HUMANIZED_MESSAGE);
            }

            final String lTitle = Activator
                    .getMessages()
                    .getFormattedMessage(
                            "ui.contributions.process.title", lGroup.get(GroupHome.KEY_ID), lGroup.get(GroupHome.KEY_NAME)); //$NON-NLS-1$
            return new ContributionsListView(contributions, lTitle, this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Callback method to publish the selected contributions.
     * 
     * @return boolean <code>true</code> if successful */
    public boolean publishContributions() {
        final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
        final CompletionHome lCompletionHome = BOMHelper.getCompletionHome();
        final TextHome lTextHome = BOMHelper.getTextHome();

        try {
            final Object[] lArguments = new Object[] { getActor().getActorID() };

            int lNumberOfPublished = 0;
            boolean lHasUnpublishables = false;

            // we iterate twice: first processing questions only
            for (final ContributionWrapper lContribution : contributions
                    .getItemIds()) {
                if (lContribution.isChecked()) {
                    if (EntryType.QUESTION.equals(lContribution.getEntryType())) {
                        ((WorkflowAware) lQuestionHome
                                .getQuestion(lContribution.getID()))
                                .doTransition(
                                        WorkflowAwareContribution.TRANS_ADMIN_PUBLISH,
                                        lArguments);
                        lNumberOfPublished++;
                    }
                }
            }
            // then: processing completions and texts
            for (final ContributionWrapper lContribution : contributions
                    .getItemIds()) {
                if (lContribution.isChecked()) {
                    if (!EntryType.QUESTION
                            .equals(lContribution.getEntryType())) {
                        if (EntryType.COMPLETION.equals(lContribution
                                .getEntryType())) {
                            final Completion lCompletion = lCompletionHome
                                    .getCompletion(lContribution.getID());
                            // completions can be published only if their owning
                            // question is published
                            if (((VIFWorkflowAware) lCompletion
                                    .getOwningQuestion()).isPublished()) {
                                ((WorkflowAware) lCompletion)
                                        .doTransition(
                                                WorkflowAwareContribution.TRANS_ADMIN_PUBLISH,
                                                lArguments);
                                lNumberOfPublished++;
                            } else {
                                lHasUnpublishables = true;
                            }
                        } else {
                            final WorkflowAware lBibliography = (WorkflowAware) lTextHome
                                    .getText(lContribution.getID());
                            lBibliography
                                    .doTransition(
                                            WorkflowAwareContribution.TRANS_ADMIN_PUBLISH,
                                            lArguments);
                            lNumberOfPublished++;
                        }
                    }
                }
            }

            final IMessages lMessages = Activator.getMessages();
            String lMessage = lMessages.getFormattedMessage("admin.msg.contributions.publish", lNumberOfPublished); //$NON-NLS-1$
            if (lHasUnpublishables) {
                lMessage = String
                        .format("%s<br />%s", lMessage, lMessages.getMessage("admin.msg.contributions.nopublish")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            showNotification(lMessage);
            sendEvent(AdminContributionsPublishablesTask.class);
            return true;
        } catch (final WorkflowException exc) {
            LOG.error("Error encountered while publishing contributions!", exc); //$NON-NLS-1$
        } catch (final VException exc) {
            LOG.error("Error encountered while publishing contributions!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while publishing contributions!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Callback method to delete the selected contributions.
     * 
     * @return boolean <code>true</code> if successful */
    public boolean deleteContributions() {
        final QuestionBranchIterator lIterator = new QuestionBranchIterator();
        final ContributionDeletionHandler lVisitor = new ContributionDeletionHandler();
        final JoinCompletionToQuestionHome lCompletionHome = BOMHelper
                .getJoinCompletionToQuestionHome();
        final TextHome lTextHome = BOMHelper.getTextHome();

        try {
            int lDeletedCount = 0;
            // we iterate twice: first processing questions only
            for (final ContributionWrapper lContribution : contributions
                    .getItemIds()) {
                if (EntryType.QUESTION.equals(lContribution.getEntryType())) {
                    lIterator.start(new Long(lContribution.getID()), true,
                            true, lVisitor);
                    lDeletedCount++;
                }
            }
            // then: processing completions and texts
            for (final ContributionWrapper lContribution : contributions
                    .getItemIds()) {
                if (!EntryType.QUESTION.equals(lContribution.getEntryType())) {
                    // completions: they may be already deleted as nodes of some
                    // deleted questions
                    if (EntryType.COMPLETION.equals(lContribution
                            .getEntryType())) {
                        final Long lCompletionID = new Long(
                                lContribution.getID());
                        if (!lIterator.checkCompletion(lCompletionID)) {
                            ((QuestionHierarchyEntry) lCompletionHome
                                    .getCompletion(lCompletionID))
                                    .accept(lVisitor);
                            lDeletedCount++;
                        }
                    }
                    // texts
                    else {
                        ((QuestionHierarchyEntry) lTextHome
                                .getText(lContribution.getID()))
                                .accept(lVisitor);
                        lDeletedCount++;
                    }
                }
            }

            updateStateContributions(lVisitor.getContributions(), getActor()
                    .getActorID(), WorkflowAwareContribution.TRANS_DELETE);

            final String lMsgKey = lDeletedCount == 1 ? "admin.msg.contributions.delete.ok1" : "admin.msg.contributions.delete.okP"; //$NON-NLS-1$ //$NON-NLS-2$
            showNotification(Activator.getMessages().getMessage(lMsgKey));
            sendEvent(AdminContributionsPublishablesTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encounteres while deleting the contribution!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encounteres while deleting the contribution!", exc); //$NON-NLS-1$
        } catch (final WorkflowException exc) {
            LOG.error("Error encounteres while deleting the contribution!", exc); //$NON-NLS-1$
        }
        return false;
    }

    private void updateStateContributions(
            final Collection<VIFWorkflowAware> inContributions,
            final Long inAuthorID, final String inTransition)
            throws SQLException, WorkflowException, VException {
        for (final VIFWorkflowAware lContribution : inContributions) {
            ((WorkflowAware) lContribution).doTransition(inTransition,
                    new Object[] { inAuthorID });
        }
    }

    /** Callback method to process click event on table of contributions. Displays the selected contribution in edit
     * mode. */
    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lSelected = inEvent.getProperty().getValue();
        if (lSelected instanceof ContributionWrapper) {
            final ContributionWrapper lContribution = (ContributionWrapper) lSelected;
            switch (lContribution.getEntryType()) {
            case QUESTION:
                setQuestionID(new Long(lContribution.getID()));
                sendEvent(AdminQuestionEditTask2.class);
                break;
            case COMPLETION:
                setCompletionID(new Long(lContribution.getID()));
                sendEvent(AdminCompletionEditTask.class);
                break;
            case TEXT:
                setTextID(lContribution.getID());
                sendEvent(BibliographyEditUnpublishedTask.class);
                break;

            }
        }
    }

}
