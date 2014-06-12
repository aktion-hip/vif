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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.adapters.AddressAdapter;
import org.hip.vif.core.adapters.QuestionHierachyAdapter;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.bom.impl.NotificationTextCollector;
import org.hip.vif.core.bom.impl.RatingEvents;
import org.hip.vif.core.bom.impl.RatingEventsHome;
import org.hip.vif.core.bom.impl.Ratings;
import org.hip.vif.core.bom.impl.RatingsCompletionHome;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.core.bom.impl.RatingsQuestionHome;
import org.hip.vif.core.bom.impl.RatingsTextHome;
import org.hip.vif.core.bom.impl.SubscriberCollector;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.mail.IVIFMail;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.QuestionTreeIterator;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.mail.ProcessPublishMail;
import org.hip.vif.forum.groups.mail.SubscribersNotification;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;

/**
 * Abstract task providing general functionality to process contributions
 * in the workflow.
 * 
 * Created on 11.08.2003
 * @author Luthiger
 */
public abstract class ContributionsWorkflowTask extends AbstractVIFTask {
	private static final Logger LOG = LoggerFactory.getLogger(ContributionsWorkflowTask.class);
	
	protected NotificationTextCollector notificator;

	/**
	 * ContributionsWorkflowTask default constructor.
	 * 
	 */
	public ContributionsWorkflowTask() {
		super();
		notificator = new NotificationTextCollector();
	}

	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return Constants.PERMISSION_REVIEW_PROCESS;
	}

	/**
	 * Returns the name of the specified discussion group.
	 * 
	 * @param inGroupID Long
	 * @return String
	 */
	protected String getGroupName(Long inGroupID) {
		try {
			Group lGroup = BOMHelper.getGroupHome().getGroup(inGroupID);
			return lGroup.get(GroupHome.KEY_NAME).toString();
		} 
		catch (VException exc) {
			return inGroupID.toString();
		}
	}
	
	/**
	 * Update the contributions states: state transition to <code>RequestReview</code>.
	 * 
	 * @param inContributions
	 * @param inAuthorID
	 * @return boolean <code>true</code> if the user selected completions belonging to questions that are still private. Such completions can't be processed. 
	 * @throws SQLException
	 * @throws WorkflowException
	 * @throws VException
	 */
	protected boolean updateStates(Collection<VIFWorkflowAware> inContributions, Long inAuthorID) throws SQLException, WorkflowException, VException {
		boolean outFoundPrivate = false;
		String lTransition = WorkflowAwareContribution.TRANS_REQUEST;
		for (VIFWorkflowAware lContribution : inContributions) {
			if (lContribution.isNode()) {
				((WorkflowAware)lContribution).doTransition(lTransition, new Object[] {inAuthorID});
			}
			else {
				//completions can be reviewed only if the owning question is not private
				Question lQuestion = ((Completion)lContribution).getOwningQuestion();
				if (((VIFWorkflowAware)lQuestion).isPrivate()) {
					outFoundPrivate = true;
				}
				else {
					((WorkflowAware)lContribution).doTransition(lTransition, new Object[] {inAuthorID});
				}
			}
		}
		return outFoundPrivate;
	}

	/**
	 * Publish the selected contributions: used in groups that don't need reviews (number of reviewers = 0)
	 * 
	 * @param isReviewed boolean <code>true</code> if the contribution is reviewed (because the group has reviewing enabled), 
	 * else <code>false</code> for unreviewed contributions.
	 */
	public void publishContribution(boolean isReviewed) {
		IMessages lMessages = Activator.getMessages();
		String lMessage = lMessages.getMessage("msg.feedback.general"); //$NON-NLS-1$
		int lNotificationType = Notification.TYPE_HUMANIZED_MESSAGE;
		
		try {
			ContributionsHelper lContributions = new ContributionsHelper(getContributions());
			AuthorsContributionsHandler lContributionsHandler = new AuthorsContributionsHandler(lContributions);
			Long lReviewerID = getActor().getActorID();
			updateStateContributions(lContributionsHandler.getContributions(), lReviewerID, 
					isReviewed ? WorkflowAwareContribution.TRANS_PUBLISH : WorkflowAwareContribution.TRANS_ADMIN_PUBLISH);
			
			//prepare mails
			VIFMember lReviewer = (VIFMember)BOMHelper.getMemberCacheHome().getMember(lReviewerID.toString());
			StringBuilder lNotificationBody = new StringBuilder();
			StringBuilder lNotificationBodyHtml = new StringBuilder();
			Collection<VIFMember> lResponsibles = new Vector<VIFMember>();
			for (AuthorsContributions lAuthorsContributions : lContributionsHandler.values()) {
				VIFMember lAuthor = lAuthorsContributions.getAuthor();
				
				notificator.reset();
				createNotification(lAuthorsContributions.getContributions());
				IVIFMail lMail = new ProcessPublishMail(lAuthor, lReviewer, notificator.getNotificationText(), notificator.getNotificationTextHtml());
				lNotificationBody.append(notificator.getNotificationTextWithIntro());
				lNotificationBodyHtml.append(notificator.getNotificationTextHtmlWithIntro());
				lResponsibles.add(lAuthor);
				lMail.send();

				prepareRatings(lAuthor, lReviewer, lContributions.questionIDs, lContributions.completionIDs, lContributions.bibliographyIDs);
			}
			lResponsibles.add(lReviewer);
			doNotification(new ChangedNodes(lContributionsHandler.getContributions()), lResponsibles,  
					lNotificationBody, lNotificationBodyHtml, getGroupName(getGroupID()));					
		} 
		catch (Exception exc) {
			lMessage = lMessages.getMessage("errmsg.general"); //$NON-NLS-1$
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
		}
		finally {
			sendEvent(this.getClass());
			showNotification(lMessage, lNotificationType);
		}
	}
	
	private void prepareRatings(VIFMember inAuthor, VIFMember inReviewer, Collection<String> inQuestionIDs, Collection<String> inCompletionIDs, Collection<String> inBibliographyIDs) throws SQLException, VException {
		//insert into RatingEvents
		RatingEvents lEvent = (RatingEvents) BOMHelper.getRatingEventsHome().create();
		lEvent.set(RatingEventsHome.KEY_COMPLETED, new Integer(0));
		Long lRatingID = lEvent.insert(true);
		
		//insert into Ratings
		RatingsHome lRatingsHome = BOMHelper.getRatingsHome();
		createRatingEntry((Ratings)lRatingsHome.create(), lRatingID, inAuthor, inReviewer, true);
		createRatingEntry((Ratings)lRatingsHome.create(), lRatingID, inReviewer, inAuthor, false);
		
		//insert into RatingsQuestion 
		RatingsQuestionHome lQuestionHome = BOMHelper.getRatingsQuestionHome();
		for (String lQuestionID : inQuestionIDs) {
			createRatingContributionEntry(lQuestionHome.create(), lRatingID, RatingsQuestionHome.KEY_QUESTION_ID, lQuestionID);
		}
		
		//insert into RatingsCompletion 
		RatingsCompletionHome lCompletionHome = BOMHelper.getRatingsCompletionHome();
		for (String lCompletionID : inCompletionIDs) {
			createRatingContributionEntry(lCompletionHome.create(), lRatingID, RatingsCompletionHome.KEY_COMPLETION_ID, lCompletionID);
		}
		
		//insert into RatingsText
		RatingsTextHome lTextHome = BOMHelper.getRatingsTextHome();
		for (String lBibliographyID : inBibliographyIDs) {
			DomainObject lEntry = lTextHome.create();
			lEntry.set(RatingsTextHome.KEY_RATINGEVENTS_ID, lRatingID);
			String[] lIDParts = lBibliographyID.split(Text.DELIMITER_ID_VERSION);
			lEntry.set(RatingsTextHome.KEY_TEXT_ID, new Long(lIDParts[0]));
			lEntry.set(RatingsTextHome.KEY_VERSION, new Long(lIDParts[1]));
			lEntry.insert(true);
		}
	}

	private void createRatingEntry(Ratings inRating, Long lRatingID, VIFMember inRater, VIFMember inRated, boolean isAuthor) throws SQLException, VException {
		inRating.set(RatingsHome.KEY_RATINGEVENTS_ID, lRatingID);
		inRating.set(RatingsHome.KEY_RATER_ID, inRater.getMemberID());
		inRating.set(RatingsHome.KEY_RATED_ID, inRated.getMemberID());
		inRating.set(RatingsHome.KEY_ISAUTHOR, new Integer(isAuthor ? 1 : 0));
		inRating.insert(true);
	}

	private void createRatingContributionEntry(DomainObject inEntry, Long inRatingID, String inName, String inContributionID) throws SQLException, VException {
		inEntry.set(RatingsQuestionHome.KEY_RATINGEVENTS_ID, inRatingID);
		inEntry.set(inName, new Long(inContributionID));
		inEntry.insert(true);
	}
	
	/**
	 * Generic processing of reviewer actions.
	 * 
	 * @param inData {@link ContributionContainer} the selected contributions
	 * @param inTransition String the state change to apply on the contributions
	 * @param inMail {@link IMailCreator} create the mail to send
	 * @param inReviewerState {@link IReviewerStateHandler} handle the reviewer state
	 */
	protected void doProcess(ContributionContainer inData, String inTransition, IMailCreator inMail, IReviewerStateHandler inReviewerState) {
		IMessages lMessages = Activator.getMessages();
		String lMessage = lMessages.getMessage("msg.feedback.general"); //$NON-NLS-1$
		int lNotificationType = Notification.TYPE_HUMANIZED_MESSAGE;
		
		ContributionsHelper lContributions = new ContributionsHelper(inData);
		try {
			AuthorsContributionsHandler lContributionsHandler = new AuthorsContributionsHandler(lContributions);
			Long lReviewerID = getActor().getActorID();
			updateStateContributions(lContributionsHandler.getContributions(), lReviewerID, inTransition);
			inReviewerState.handleReviewerState(lReviewerID, lContributions);
			VIFMember lReviewer = (VIFMember)BOMHelper.getMemberCacheHome().getMember(lReviewerID.toString());
			for (AuthorsContributions lAuthorsContributions : lContributionsHandler.values()) {
				VIFMember lAuthor = lAuthorsContributions.getAuthor();
				notificator.reset();
				createNotification(lAuthorsContributions.getContributions());
				IVIFMail lMail = inMail.createMail(lAuthor, lReviewer, notificator.getNotificationText(), notificator.getNotificationTextHtml());
				lMail.send();					
			}
		} catch (Exception exc) {
			lMessage = lMessages.getMessage("errmsg.general"); //$NON-NLS-1$
			lNotificationType = Notification.TYPE_ERROR_MESSAGE;
			LOG.error("Error encountered while requesting a review for contributions.", exc); //$NON-NLS-1$
		}
		finally {
			sendEvent(this.getClass());
			showNotification(lMessage, lNotificationType);
		}
	}
	
	/**
	 * @return {@link ContributionContainer} the data ready for publication.
	 */
	protected abstract ContributionContainer getContributions();
	
	protected Member getMember() throws Exception {
		return MemberUtility.INSTANCE.getActiveMemberSearcher().getMemberCacheHome().getActor();
	}
	
	/**
	 * Updates the state of the contributions performing the specified transition.
	 * 
	 * @param inContributions Collection the contributions to update
	 * @param inAuthorID Long
	 * @param inTransition String
	 * @throws SQLException
	 * @throws WorkflowException
	 * @throws VException
	 */
	protected void updateStateContributions(Collection<VIFWorkflowAware> inContributions, Long inAuthorID, String inTransition) throws SQLException, WorkflowException, VException {
		for (VIFWorkflowAware lContribution : inContributions) {
			((WorkflowAware)lContribution).doTransition(inTransition, new Object[] {inAuthorID});
		}
	}

	/**
	 * Sets the refused flag to all contributions the specified member
	 * has been asked to review.
	 * 
	 * @param inActorID Long
	 * @param inQuestionIDs String[]
	 * @param inCompletionIDs String[]
	 * @throws VException
	 * @throws SQLException
	 */
	protected void updateReviewerState(Long inActorID, ContributionsHelper inContributions) throws VException, SQLException {
		ResponsibleHome lHome = (ResponsibleHome)BOMHelper.getQuestionAuthorReviewerHome();
		for (String lID : inContributions.questionIDs) {
			lHome.getResponsible(lID, inActorID).setRefused();
		}
	
		lHome = (ResponsibleHome)BOMHelper.getCompletionAuthorReviewerHome();
		for (String lID : inContributions.completionIDs) {
			lHome.getResponsible(lID, inActorID).setRefused();
		}
		
		lHome = (ResponsibleHome)BOMHelper.getTextAuthorReviewerHome();
		for (String lID : inContributions.bibliographyIDs) {
			lHome.getResponsible(lID, inActorID).setRefused();
		}
	}
	
	/**
	 * Creates the mail body sent to the subscribers or the reviewer in the notification mail.
	 * 
	 * @param inContributions Collection<VIFWorkflowAware>
	 * @throws VException
	 * @throws SQLException
	 */
	protected void createNotification(Collection<VIFWorkflowAware> inContributions) throws VException, SQLException {
		for (VIFWorkflowAware lContribution : inContributions) {
			QuestionHierachyAdapter lAdapter = new QuestionHierachyAdapter(lContribution);
			lAdapter.accept(notificator);
		}
	}
	
	/**
	 * Creates the mail body sent to the subscribers in the notification mail.
	 * 
	 * @param inContributions Collection
	 * @param inAuthorName String
	 * @throws VException
	 * @throws SQLException
	 */
	protected void createNotification(Collection<VIFWorkflowAware> inContributions, String inAuthorName) throws VException, SQLException {
		notificator.setMadeBy(inAuthorName);
		createNotification(inContributions);
	}

	/**
	 * Notify the subscribers about the publication
	 * 
	 * @param inChangedNodes ChangedNodes
	 * @param inResponsibles Collection<VIFMember>
	 * @param inMailBody StringBuilder
	 * @param inMailBodyHtml StringBuilder
	 * @param inGroupName String
	 * @throws VException
	 * @throws SQLException
	 */
	protected void doNotification(ChangedNodes inChangedNodes, Collection<VIFMember> inResponsibles, 
			StringBuilder inMailBody, StringBuilder inMailBodyHtml, String inGroupName) 
			throws VException, SQLException {
		//- get the mail addresses of the subscribers
		//-- from the nodes above
		SubscriberCollector lCollector = new SubscriberCollector(true);
		QuestionTreeIterator lTreeIterator = new QuestionTreeIterator();
		for (Long lNodeID : inChangedNodes.getChangedNodeIDs()) {
			lTreeIterator.start(lNodeID, false, lCollector);
		}
						
		//-- from the actual nodes
		lCollector.updateSelectionFilter(false);
		for (VIFWorkflowAware lContribution : inChangedNodes.getChangedNodes()) {
			QuestionHierachyAdapter lAdapter = new QuestionHierachyAdapter(lContribution);
			lAdapter.accept(lCollector);
		}
						
		//-- remove the author's and reviewer's mail addresses from the list
		for (VIFMember lMember : inResponsibles) {
			lCollector.checkFor(lMember.getMailAddress());
		}

		//- send the mails
		SubscribersNotification lNotification = new SubscribersNotification(AddressAdapter.parse(lCollector.getMailAddresses()), inMailBody, inMailBodyHtml, inGroupName, true);
		lNotification.send();
	}
	
//	--- private classes ---	
	
	/**
	 * Helper inner class to create a collection of all nodes which have changed,
	 * either because the node (i.e. the question) or some completions in it have been published.
	 * 
	 * @author Benno Luthiger
	 * Created on Mar 3, 2004
	 */
	protected class ChangedNodes extends Object {
		private Collection<Long> changedNodeIDs;
		private Collection<VIFWorkflowAware> changedNodes;
		
		/**
		 * ChangedNodes constuctor.
		 * 
		 * @param inContributions Collection
		 * @throws VException
		 */
		public ChangedNodes(Collection<VIFWorkflowAware> inContributions) throws VException {
			super();
			init(inContributions);
		}
		/**
		 * @return Collection of IDs (BigDecimal) of changed nodes
		 */
		public Collection<Long> getChangedNodeIDs() {
			return changedNodeIDs;
		}
		/**
		 * @return Collection of changed nodes (Question)
		 */
		public Collection<VIFWorkflowAware> getChangedNodes() {
			return changedNodes;
		}
		private void init(Collection<VIFWorkflowAware> inContributions) throws VException {
			changedNodeIDs = new Vector<Long>();
			changedNodes = new Vector<VIFWorkflowAware>();
			for (VIFWorkflowAware lContribution : inContributions) {
				
				Long lContributionID = lContribution.getNodeID();
				if (!changedNodeIDs.contains(lContributionID)) {
					changedNodeIDs.add(lContributionID);
					if (lContribution.isNode()) {
						changedNodes.add(lContribution);
					}
					else {
						changedNodes.add((VIFWorkflowAware) ((Completion)lContribution).getOwningQuestion());
					}
				}
			}
		}
	}
	
	/**
	 * Helper inner class to attach the contributions to their authors.
	 * 
	 * @author Benno Luthiger
	 * Created on Mar 5, 2004
	 */
	protected class AuthorsContributionsHandler extends Object {
		private HashMap<Long, AuthorsContributions> authorsContributions = new HashMap<Long, AuthorsContributions>();
		
		/**
		 * AuthorsContributionsHandler constructor.
		 * 
		 * @param inQuestionIDs Collection<String>
		 * @param inCompletionIDs Collection<String>
		 * @param inBibliographyIDs Collection<String>
		 * @throws VException
		 * @throws SQLException
		 */
		public AuthorsContributionsHandler(Collection<String> inQuestionIDs, Collection<String> inCompletionIDs, Collection<String> inBibliographyIDs) throws VException, SQLException {
			init(inQuestionIDs, inCompletionIDs, inBibliographyIDs);
		}
		
		public AuthorsContributionsHandler(ContributionsHelper inContributions) throws VException, SQLException {
			init(inContributions.questionIDs, inContributions.completionIDs, inContributions.bibliographyIDs);
		}

		private void init(Collection<String> inQuestionIDs, Collection<String> inCompletionIDs, Collection<String> inBibliographyIDs) throws VException, SQLException {
			ResponsibleHome lHome = (ResponsibleHome)BOMHelper.getQuestionAuthorReviewerHome();
			for (String lID : inQuestionIDs) {
				Long lAuthorID = lHome.getAuthor(lID).getResponsibleID();
				VIFWorkflowAware lContribution = (VIFWorkflowAware) BOMHelper.getQuestionHome().getQuestion(lID);
				processContribution(lAuthorID, lContribution);
			}
			
			lHome = (ResponsibleHome)BOMHelper.getCompletionAuthorReviewerHome();			
			for (String lID : inCompletionIDs) {
				Long lAuthorID = lHome.getAuthor(lID).getResponsibleID();
				//DomainObject lContribution = getJoinCompletionToQuestionHome().getCompletion(new BigDecimal(inCompletionIDs[i])); 
				VIFWorkflowAware lContribution = (VIFWorkflowAware) BOMHelper.getCompletionHome().getCompletion(lID);
				processContribution(lAuthorID, lContribution);
			}
			
			lHome = (ResponsibleHome)BOMHelper.getTextAuthorReviewerHome();
			for (String lID : inBibliographyIDs) {
				Long lAuthorID = lHome.getAuthor(lID).getResponsibleID();
				VIFWorkflowAware lContribution = (VIFWorkflowAware) BOMHelper.getTextHome().getText(lID);
				processContribution(lAuthorID, lContribution);
			}
			
		}

		private void processContribution(Long lAuthorID, VIFWorkflowAware lContribution) {
			if (authorsContributions.containsKey(lAuthorID)) {
				authorsContributions.get(lAuthorID).add(lContribution);
			}
			else {
				AuthorsContributions lAuthorsContributions = new AuthorsContributions(lAuthorID, lContribution);
				authorsContributions.put(lAuthorID, lAuthorsContributions);
			}
		}
		
		/**
		 * Returns an Iterator over the authors responsible for the contributions.
		 * 
		 * @return Iterator over AuthorsContributions
		 * @see org.hip.vif.forum.groups.tasks.tasks.impl.ContributionsWorkflowTask#AuthorsContributions
		 */
		public Collection<AuthorsContributions> values() {
			return authorsContributions.values();
		}
		/**
		 * Returns a Collection of contributions
		 * 
		 * @return Collection<VIFWorkflowAware>
		 */
		public Collection<VIFWorkflowAware> getContributions() {
			Collection<VIFWorkflowAware> outContributions = new Vector<VIFWorkflowAware>();
			for (AuthorsContributions lAuthor : values()) {
				outContributions.addAll(lAuthor.getContributions());
			}
			return outContributions;
		}
	}
	
	/**
	 * Helper inner class: an author with its contributions.
	 * 
	 * @author Benno Luthiger
	 * Created on Mar 5, 2004
	 */
	protected class AuthorsContributions extends Object {
		private Collection<VIFWorkflowAware> contributions = new Vector<VIFWorkflowAware>();
		private Long authorID;
		public AuthorsContributions(Long inAuthorID, VIFWorkflowAware inContribution) {
			super();
			authorID = inAuthorID;
			contributions.add(inContribution);
		}
		public void add(VIFWorkflowAware inContribution) {
			contributions.add(inContribution);
		}
		/**
		 * Returns a Collection of contributions
		 * 
		 * @return Collection of <code>VIFWorkflowAware</code>
		 */
		public Collection<VIFWorkflowAware> getContributions() {
			return contributions;
		}
		/**
		 * Returns the author of the contributions.
		 * 
		 * @return VIFMember
		 * @throws VException
		 */
		public VIFMember getAuthor() throws Exception {
			return (VIFMember)BOMHelper.getMemberCacheHome().getMember(authorID);
		}
	}
	
	/**
	 * Parameter object
	 *
	 * @author Luthiger
	 * Created: 18.07.2010
	 */
	protected static class ContributionsHelper extends Object {
		Collection<String> questionIDs = new Vector<String>();
		Collection<String> completionIDs = new Vector<String>();
		Collection<String> bibliographyIDs = new Vector<String>();

		ContributionsHelper(ContributionContainer inData) {
			for (ContributionWrapper lContribution : inData.getItemIds()) {
				if (!lContribution.isChecked()) continue;
				
				switch (lContribution.getEntryType()) {
				case QUESTION:
					questionIDs.add(lContribution.getID());
					break;
				case COMPLETION:
					completionIDs.add(lContribution.getID());
					break;
				case TEXT:
					bibliographyIDs.add(lContribution.getID());
				}
			}
		}

		/**
		 * @return boolean <code>true</code> if user selected more then one entry
		 */
		boolean hasMultipleSelection() {
			return questionIDs.size() + completionIDs.size() + bibliographyIDs.size() > 1;
		}
	}

// --- interfaces for generic contribution processing ---
	
	protected static interface IMailCreator {
		IVIFMail createMail(VIFMember inAuthor, VIFMember inReviewer, 
				StringBuilder inNotificationText, StringBuilder inNotificationTextHtml) 
		throws VException, IOException;
	}
	
	protected static interface IReviewerStateHandler {
		void handleReviewerState(Long inActorID, ContributionsHelper inContributions) throws VException, SQLException;
	}

}
