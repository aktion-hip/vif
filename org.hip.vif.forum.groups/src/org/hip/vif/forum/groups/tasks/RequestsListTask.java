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

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.mail.AnswerAcceptMail;
import org.hip.vif.forum.groups.mail.AnswerRefuseMail;
import org.hip.vif.forum.groups.mail.ProcessGiveBackMail;
import org.hip.vif.forum.groups.ui.RequestsListView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.mail.IVIFMail;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

/** <p>
 * Task displaying the reviewer's list of contributions for that she can process them.
 * </p>
 * <p>
 * Handling of 'requests for review': 'accept request' or 'refuse'<br/>
 * Handling of 'under revision': 'publish' or 'give back'
 * </p>
 *
 * Created on 15.08.2003
 *
 * @author Luthiger */
@SuppressWarnings("serial")
@UseCaseController
public class RequestsListTask extends ContributionsWorkflowTask implements ValueChangeListener { // NOPMD
    private static final Logger LOG = LoggerFactory.getLogger(RequestsListTask.class);

    private ContributionContainer[] contributions;
    private RequestsListView contributionsList;

    @Override
    public Component runChecked() throws RiplaException { // NOPMD
        final Long lGroupID = getGroupID();
        final Long lActorID = getActor().getActorID();

        try {
            loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);
            if (!VifBOMHelper.getGroupHome().getGroup(lGroupID).isActive()) {
                return reDisplay(Activator.getMessages().getMessage("errmsg.not.active2"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            final Integer[] lForReview = new Integer[] { WorkflowAwareContribution.S_WAITING_FOR_REVIEW,
                    WorkflowAwareContribution.S_UNDER_REVISION };
            final QueryResult lQuestions = BOMHelper.getJoinAuthorReviewerToQuestionHome().getReviewersQuestions(
                    lActorID, lGroupID, lForReview);
            final QueryResult lCompletions = BOMHelper.getJoinAuthorReviewerToCompletionHome().getReviewersCompletions(
                    lActorID, lGroupID, lForReview);
            final QueryResult lTexts = VifBOMHelper.getJoinAuthorReviewerToTextHome().getReviewersUnpublishedTexts(
                    lActorID);
            contributions = ContributionContainer.createDataSets(lQuestions, lCompletions, lTexts, lCodeList);
            contributionsList = new RequestsListView(contributions[0], contributions[1], getMember(),
                    BeanWrapperHelper.getString(GroupHome.KEY_NAME, VifBOMHelper.getGroupHome().getGroup(lGroupID)),
                    this);
            return contributionsList;
        } catch (final Exception exc) { // NOPMD
            throw createContactAdminException(exc);
        }
    }

    @Override
    protected ContributionContainer getContributions() { // NOPMD
        return contributions[1];
    }

    /** Callback method: handles 'accept request for review' */
    public void acceptRequest() {
        doProcess(contributions[0], WorkflowAwareContribution.TRANS_ACCEPT,
                new IMailCreator() {
                    @Override
                    public IVIFMail createMail(final VIFMember inAuthor, final VIFMember inReviewer, // NOPMD
                            final StringBuilder inNotificationText, final StringBuilder inNotificationTextHtml)
                                    throws VException, IOException {
                        return new AnswerAcceptMail(inAuthor, inReviewer, notificator.getNotificationText(),
                                notificator.getNotificationTextHtml());
                    }
                },
                new IReviewerStateHandler() {
                    @Override
                    public void handleReviewerState(final Long inActorID, final ContributionsHelper inContributions) // NOPMD
                            throws VException, SQLException {
                        // intentionally left empty
                    }
                });
    }

    /** Callback method: handles 'refuse request for review' */
    public void refuseRequest() {
        doProcess(contributions[0], WorkflowAwareContribution.TRANS_REJECT_REVIEW,
                new IMailCreator() {
                    @Override
                    public IVIFMail createMail(final VIFMember inAuthor, final VIFMember inReviewer, // NOPMD
                            final StringBuilder inNotificationText,
                            final StringBuilder inNotificationTextHtml) throws VException, IOException {
                        return new AnswerRefuseMail(inAuthor, inReviewer, notificator.getNotificationText(),
                                notificator.getNotificationTextHtml());
                    }
                },
                new IReviewerStateHandler() {
                    @Override
                    public void handleReviewerState(final Long inActorID, final ContributionsHelper inContributions) // NOPMD
                            throws VException, SQLException {
                        updateReviewerState(inActorID, inContributions);
                    }
                });
    }

    /** Callback method: handles 'give back review' (after accepting review request in first round) */
    public void giveBack() {
        doProcess(contributions[1], WorkflowAwareContribution.TRANS_GIVE_BACK_REVIEW,
                new IMailCreator() {
                    @Override
                    public IVIFMail createMail(final VIFMember inAuthor, final VIFMember inReviewer, // NOPMD
                            final StringBuilder inNotificationText, final StringBuilder inNotificationTextHtml)
                                    throws VException, IOException {
                        return new ProcessGiveBackMail(inAuthor, inReviewer, notificator.getNotificationText(),
                                notificator.getNotificationTextHtml());
                    }
                },
                new IReviewerStateHandler() {
                    @Override
                    public void handleReviewerState(final Long inActorID, final ContributionsHelper inContributions) // NOPMD
                            throws VException, SQLException {
                        updateReviewerState(inActorID, inContributions);
                    }
                });
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
                        sendEvent(QuestionReviewTask.class);
                        break;
                    case COMPLETION:
                        final Long lCompletionID = Long.parseLong(lID);
                        setCompletionID(lCompletionID);
                        final Long lQuestionID = getOwningQuestionID(lCompletionID);
                        if (lQuestionID != null) {
                            setQuestionID(getOwningQuestionID(lCompletionID));
                        }
                        sendEvent(CompletionReviewTask.class);
                        break;
                    case TEXT:
                        setTextID(lID);
                        requestLookup(LookupType.BIBLIOGRAPHY, lID);
                        break;
                    default:
                        // do nothing
                    }
                }
            }
        }
    }

    private Long getOwningQuestionID(final Long inCompletionID) {
        try {
            final Completion lCompletion = BOMHelper.getCompletionHome().getCompletion(inCompletionID.toString());
            return BeanWrapperHelper.getLong(CompletionHome.KEY_QUESTION_ID, lCompletion);
        } catch (final VException exc) {
            LOG.error("Error while retrieving the completion's owning question!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while retrieving the completion's owning question!", exc); //$NON-NLS-1$
        }
        return null;
    }

}
