/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.forum.groups.tasks; // NOPMD

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.bom.impl.ContributionDeletionHandler;
import org.hip.vif.core.bom.impl.QuestionBranchIterator;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.core.exc.NoReviewerException;
import org.hip.vif.core.interfaces.IReviewable;
import org.hip.vif.core.util.QuestionHierarchyEntry;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.data.ContributionWrapper.EntryType;
import org.hip.vif.forum.groups.ui.ContributionsListView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.mail.NoReviewerNotification;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.RequestForReviewMail;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

/** Task to display the author's yet unpublished and pending contributions for that they can be deleted or selected to be
 * reviewed.
 *
 * Created on 11.08.2003
 *
 * @author Luthiger */
@SuppressWarnings("serial")
@UseCaseController
public class ContributionsListTask extends ContributionsWorkflowTask implements ValueChangeListener { // NOPMD
    private static final Logger LOG = LoggerFactory.getLogger(ContributionsListTask.class);

    private ContributionContainer contributions;
    private ContributionsListView contributionsList;

    @Override
    public Component runChecked() throws RiplaException { // NOPMD
        final Long lActorID = getActor().getActorID();
        final Long lGroupID = getGroupID();
        try {
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
            final boolean lNeedsReview = lGroup.needsReview();
            loadContextMenu(lNeedsReview ? Constants.MENU_SET_ID_GROUP_CONTENT : Constants.MENU_SET_ID_CONTRIBUTE);

            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            final QueryResult lQuestions = BOMHelper.getJoinAuthorReviewerToQuestionHome()
                    .getAuthorsUnpublishedQuestions(lActorID, lGroupID);
            final QueryResult lCompletions = BOMHelper.getJoinAuthorReviewerToCompletionHome()
                    .getAuthorsUnpublishedCompletions(lActorID, lGroupID);
            final QueryResult lTexts = VifBOMHelper.getJoinAuthorReviewerToTextHome().getAuthorsUnpublishedTexts(
                    lActorID);
            contributions = ContributionContainer.createData(lQuestions, lCompletions, lTexts, lCodeList);
            contributionsList = new ContributionsListView(contributions, getMember(),
                    BeanWrapperHelper.getString(GroupHome.KEY_NAME, lGroup), lNeedsReview, this);
            return contributionsList;
        } catch (final Exception exc) { // NOPMD
            throw createContactAdminException(exc);
        }
    }

    /** Callback function: Deletes the selected contributions. */
    public void deleteContributions() { // NOPMD
        final IMessages lMessages = Activator.getMessages();
        final QuestionBranchIterator lIterator = new QuestionBranchIterator();
        final ContributionDeletionHandler lVisitor = new ContributionDeletionHandler();
        Type lNotificationType = Type.HUMANIZED_MESSAGE;

        int lDeletedCount = 0;
        try {
            // we iterate twice: first processing questions only
            for (final ContributionWrapper lContribution : contributions.getItemIds()) {
                if (EntryType.QUESTION.equals(lContribution.getEntryType())) {
                    lIterator.start(Long.parseLong(lContribution.getID()), true, true, lVisitor);
                    lDeletedCount++;
                }
            }
            // then: processing completions and texts
            for (final ContributionWrapper lContribution : contributions.getItemIds()) {
                if (!EntryType.QUESTION.equals(lContribution.getEntryType())) {
                    // completions: they may be already deleted as nodes of some deleted questions
                    if (EntryType.COMPLETION.equals(lContribution.getEntryType())) {
                        final Long lCompletionID = Long.parseLong(lContribution.getID());
                        if (!lIterator.checkCompletion(lCompletionID)) {
                            ((QuestionHierarchyEntry) BOMHelper.getJoinCompletionToQuestionHome().getCompletion(
                                    lCompletionID)).accept(lVisitor);
                            lDeletedCount++;
                        }
                    }
                    // texts
                    else {
                        ((QuestionHierarchyEntry) BOMHelper.getTextHome().getText(lContribution.getID()))
                        .accept(lVisitor);
                        lDeletedCount++;
                    }
                }
            }

            updateStateContributions(lVisitor.getContributions(), getActor().getActorID(),
                    WorkflowAwareContribution.TRANS_DELETE);
        } catch (final SQLException exc) {
            lNotificationType = Type.ERROR_MESSAGE;
            LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
        } catch (final WorkflowException exc) {
            lNotificationType = Type.ERROR_MESSAGE;
            LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
        } catch (final VException exc) {
            lNotificationType = Type.ERROR_MESSAGE;
            LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
        } finally {
            sendEvent(ContributionsListTask.class);

            // display notification
            String lMessage = ""; //$NON-NLS-1$
            if (lNotificationType == Type.ERROR_MESSAGE) {
                lMessage = lMessages.getMessage("errmsg.delete"); //$NON-NLS-1$
            }
            else {
                lMessage = lMessages
                        .getMessage(lDeletedCount == 1 ? "msg.contributions.deleteS" : "msg.contributions.deleteP"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            showNotification(lMessage, lNotificationType);
        }
    }

    @Override
    protected ContributionContainer getContributions() { // NOPMD
        return contributions;
    }

    /** Callback function: Sets contributions into the 'requesting review' state and sends mail to random reviewer. */
    public void requestReview() {
        final IMessages lMessages = Activator.getMessages();
        Type lNotificationType = Type.TRAY_NOTIFICATION;
        String lMessage = ""; //$NON-NLS-1$
        try {
            final AuthorsContributionsHandler lContributionsHandler = new AuthorsContributionsHandler(
                    new ContributionsHelper(contributions));
            final Collection<VIFWorkflowAware> lContributions = lContributionsHandler.getContributions();

            final boolean lFoundPrivate = updateStates(lContributions, getActor().getActorID());
            final VIFMember lReviewer = BOMHelper.getJoinParticipantToMemberHome().getRandomParticipant(getGroupID(),
                    getActor().getActorID(),
                    WorkflowAwareContribution.convertToReviewable(lContributions));

            // set reviewer to contributions
            final Long lReviewerID = Long.parseLong(lReviewer.getMemberID().toString());
            for (final VIFWorkflowAware lContribution : lContributions) {
                ((IReviewable) lContribution).setReviewer(lReviewerID);
            }

            // send mail
            notificator.reset();
            createNotification(lContributions);
            final RequestForReviewMail lMail = new RequestForReviewMail(lReviewer, (VIFMember) BOMHelper
                    .getMemberCacheHome().getMember(getActor().getActorID()),
                    notificator.getNotificationText(), notificator.getNotificationTextHtml());
            lMail.send();

            final String lReviewerMailAddress = lMail.getMailTo();
            lMessage = lMessages
                    .getFormattedMessage(
                            "msg.contributions.request", String.valueOf(lContributionsHandler.getContributions().size()), lReviewerMailAddress); //$NON-NLS-1$
            if (lFoundPrivate) {
                lMessage += " " + lMessages.getMessage("msg.contributions.nopublish"); // NOPMD //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (final NoReviewerException exc) {
            lMessage = lMessages.getMessage("errmsg.no.reviewer"); //$NON-NLS-1$
            lNotificationType = Type.ERROR_MESSAGE;
            sendWarningMail();
            LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
        } catch (final MailGenerationException exc) {
            lMessage = lMessages.getMessage("errmsg.noMail"); //$NON-NLS-1$
            lNotificationType = Type.ERROR_MESSAGE;
            LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
        } catch (final SQLException | IOException | VException | WorkflowException exc) {
            lMessage = lMessages.getMessage("errmsg.general"); //$NON-NLS-1$
            lNotificationType = Type.ERROR_MESSAGE;
            LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
        } finally {
            // shows the updated list of pending contributions
            sendEvent(ContributionsListTask.class);

            showNotification(lMessage, lNotificationType);
        }
    }

    /** Notifies the group administration that there are no reviewers available. */
    private void sendWarningMail() {
        final Long lGroupID = getGroupID();
        try {
            final QueryResult lAdmins = BOMHelper.getJoinGroupAdminToMemberHome().select(lGroupID);
            final NoReviewerNotification lMail = new NoReviewerNotification(AddressAdapter.fill(lAdmins,
                    MemberHome.KEY_MAIL), getGroupName(lGroupID));
            lMail.send();
        } catch (final VException exc) {
            LOG.error("Error encountered while sending a mail to group adminstrators.", exc); //$NON-NLS-1$
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) { // NOPMD
        final Property<?> lProperty = inEvent.getProperty();
        if (contributionsList.checkSelectionSource(lProperty)) {
            if (lProperty instanceof Table) {
                final Object lValue = ((Table) lProperty).getValue();
                if (lValue instanceof ContributionWrapper) {
                    final ContributionWrapper lContribution = (ContributionWrapper) lValue;
                    final String lID = lContribution.getID();

                    switch (lContribution.getEntryType()) {
                    case QUESTION:
                        setQuestionID(Long.parseLong(lID));
                        sendEvent(QuestionEditTask.class);
                        break;
                    case COMPLETION:
                        setCompletionID(Long.parseLong(lID));
                        sendEvent(CompletionEditTask.class);
                        break;
                    case TEXT:
                        setTextID(lID);
                        sendEvent(BibliographyEditUnpublishedTask.class);
                        break;
                    default:
                        // do nothing
                    }
                }
            }
        }
    }

}
