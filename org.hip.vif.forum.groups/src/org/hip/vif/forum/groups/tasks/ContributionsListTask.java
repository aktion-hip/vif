/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.mail.MailGenerationException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.annotations.Partlet;
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
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.interfaces.IReviewable;
import org.hip.vif.core.mail.NoReviewerNotification;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.core.util.QuestionHierarchyEntry;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.data.ContributionWrapper.EntryType;
import org.hip.vif.forum.groups.ui.ContributionsListView;
import org.hip.vif.web.util.RequestForReviewMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.Notification;

/**
 * Task to display the author's yet unpublished and pending contributions 
 * for that they can be deleted or selected to be reviewed.
 * 
 * Created on 11.08.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
@Partlet
public class ContributionsListTask extends ContributionsWorkflowTask implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(ContributionsListTask.class);
	
	private ContributionContainer contributions;
	private ContributionsListView contributionsList;
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	public Component runChecked() throws VException {
		Long lActorID = getActor().getActorID();
		Long lGroupID = getGroupID();
		try {
			Group lGroup = BOMHelper.getGroupHome().getGroup(lGroupID);
			boolean lNeedsReview = lGroup.needsReview();
			loadContextMenu(lNeedsReview ? Constants.MENU_SET_ID_GROUP_CONTENT : Constants.MENU_SET_ID_CONTRIBUTE);
			
			CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage());
			QueryResult lQuestions = BOMHelper.getJoinAuthorReviewerToQuestionHome().getAuthorsUnpublishedQuestions(lActorID, lGroupID);
			QueryResult lCompletions = BOMHelper.getJoinAuthorReviewerToCompletionHome().getAuthorsUnpublishedCompletions(lActorID, lGroupID);
			QueryResult lTexts = BOMHelper.getJoinAuthorReviewerToTextHome().getAuthorsUnpublishedTexts(lActorID);
			contributions = ContributionContainer.createData(lQuestions, lCompletions, lTexts, lCodeList);
			contributionsList = new ContributionsListView(contributions, getMember(), 
					BeanWrapperHelper.getString(GroupHome.KEY_NAME, lGroup), lNeedsReview, this);
			return contributionsList;
		}
		catch (VException exc) {
			throw exc;
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}
	
	/**
	 * Callback function: Deletes the selected contributions.
	 */
	public void deleteContributions() {
		IMessages lMessages = Activator.getMessages();
		QuestionBranchIterator lIterator = new QuestionBranchIterator();
		ContributionDeletionHandler lVisitor = new ContributionDeletionHandler();
		int lNotificationType = Notification.TYPE_HUMANIZED_MESSAGE;
		
		int lDeletedCount = 0;
		try {
			//we iterate twice: first processing questions only
			for (ContributionWrapper lContribution : contributions.getItemIds()) {
				if (EntryType.QUESTION.equals(lContribution.getEntryType())) {
					lIterator.start(new Long(lContribution.getID()), true, true, lVisitor);
					lDeletedCount++;
				}
			}
			//then: processing completions and texts
			for (ContributionWrapper lContribution : contributions.getItemIds()) {
				if (! EntryType.QUESTION.equals(lContribution.getEntryType())) {
					//completions: they may be already deleted as nodes of some deleted questions
					if (EntryType.COMPLETION.equals(lContribution.getEntryType())) {
						Long lCompletionID = new Long(lContribution.getID());
						if (!lIterator.checkCompletion(lCompletionID)) {
							((QuestionHierarchyEntry)BOMHelper.getJoinCompletionToQuestionHome().getCompletion(lCompletionID)).accept(lVisitor);
							lDeletedCount++;
						}
					}
					//texts
					else {
						((QuestionHierarchyEntry)BOMHelper.getTextHome().getText(lContribution.getID())).accept(lVisitor);
						lDeletedCount++;
					}
				}
			}		
		
			updateStateContributions(lVisitor.getContributions(), getActor().getActorID(), WorkflowAwareContribution.TRANS_DELETE);
		} catch (SQLException exc) {
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
		} catch (WorkflowException exc) {
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
		} catch (VException exc) {
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while deleting contributions.", exc); //$NON-NLS-1$
		}
		finally {
			sendEvent(ContributionsListTask.class);
			
			//display notification
			String lMessage = ""; //$NON-NLS-1$
			if (lNotificationType == Notification.TYPE_ERROR_MESSAGE) {
				lMessage = lMessages.getMessage("errmsg.delete"); //$NON-NLS-1$
			}
			else {
				lMessage = lMessages.getMessage(lDeletedCount == 1 ? "msg.contributions.deleteS" : "msg.contributions.deleteP"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			showNotification(lMessage, lNotificationType);
		}
	}
	
	@Override
	protected ContributionContainer getContributions() {
		return contributions;
	}
	
	/**
	 * Callback function: Sets contributions into the 'requesting review' state and sends mail to random reviewer.
	 */
	public void requestReview() {
		IMessages lMessages = Activator.getMessages();
		int lNotificationType = Notification.TYPE_TRAY_NOTIFICATION;
		String lMessage = ""; //$NON-NLS-1$
		try {
			AuthorsContributionsHandler lContributionsHandler = new AuthorsContributionsHandler(new ContributionsHelper(contributions));
			Collection<VIFWorkflowAware> lContributions = lContributionsHandler.getContributions();

			boolean lFoundPrivate = updateStates(lContributions, getActor().getActorID());
			VIFMember lReviewer = BOMHelper.getJoinParticipantToMemberHome().getRandomParticipant(getGroupID(), getActor().getActorID(), 
					WorkflowAwareContribution.convertToReviewable(lContributions));			
			
			//set reviewer to contributions
			Long lReviewerID = new Long(lReviewer.getMemberID().toString());
			for (VIFWorkflowAware lContribution : lContributions) {
				((IReviewable)lContribution).setReviewer(lReviewerID);
			}
			
			//send mail
			notificator.reset();
			createNotification(lContributions);
			RequestForReviewMail lMail = new RequestForReviewMail(lReviewer, (VIFMember)BOMHelper.getMemberCacheHome().getMember(getActor().getActorID()), 
					notificator.getNotificationText(), notificator.getNotificationTextHtml());
			lMail.send();
			
			String lReviewerMailAddress = lMail.getMailTo();
			lMessage = lMessages.getFormattedMessage("msg.contributions.request", String.valueOf(lContributionsHandler.getContributions().size()), lReviewerMailAddress); //$NON-NLS-1$
			if (lFoundPrivate) {
				lMessage += " " + lMessages.getMessage("msg.contributions.nopublish"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} 
		catch (NoReviewerException exc) {
			lMessage = lMessages.getMessage("errmsg.no.reviewer"); //$NON-NLS-1$
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			sendWarningMail();
			LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
		}
		catch (MailGenerationException exc) {
			lMessage = lMessages.getMessage("errmsg.noMail"); //$NON-NLS-1$
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
		}
		catch (Exception exc) {
			lMessage = lMessages.getMessage("errmsg.general"); //$NON-NLS-1$
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
		}
		finally {
			//shows the updated list of pending contributions
			sendEvent(ContributionsListTask.class);
			
			showNotification(lMessage, lNotificationType);
		}
	}
	
	/**
	 * Notifies the group administration that there are no reviewers available.
	 */
	private void sendWarningMail() {
		Long lGroupID = getGroupID();
		try {
			QueryResult lAdmins = BOMHelper.getJoinGroupAdminToMemberHome().select(lGroupID);
			NoReviewerNotification lMail = new NoReviewerNotification(AddressAdapter.fill(lAdmins, MemberHome.KEY_MAIL), getGroupName(lGroupID));
			lMail.send();
		} 
		catch (Exception exc) {
			LOG.error("Error encountered while sending a mail to group adminstrators.", exc); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	public void valueChange(ValueChangeEvent inEvent) {
		Property lProperty = inEvent.getProperty();
		if (contributionsList.checkSelectionSource(lProperty)) {
			if (lProperty instanceof Table) {
				Object lValue = ((Table) lProperty).getValue();
				if (lValue instanceof ContributionWrapper) {
					ContributionWrapper lContribution = (ContributionWrapper) lValue;
					String lID = lContribution.getID();
					
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
					}
				}
			}
		}
	}
	
}
