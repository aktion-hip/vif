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
package org.hip.vif.forum.usersettings.rating;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.SQLNull;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.bom.impl.JoinRatingsToRater;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.mail.AbstractNotification;
import org.hip.vif.core.usertasks.IUserTask;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.data.RatingsContainer;
import org.hip.vif.forum.usersettings.data.RatingsContainer.RatingItem;
import org.hip.vif.forum.usersettings.tasks.ShowCompletedRatingTask;
import org.hip.vif.forum.usersettings.tasks.UserTasksManageTask;
import org.hip.vif.forum.usersettings.ui.ShowRatingView;
import org.hip.vif.web.tasks.AbstractUserTask;
import org.hip.vif.web.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * User task: after publishing a contribution, the author and reviewer have to rate the interaction.
 *
 * @author Luthiger
 * Created: 27.08.2009
 */
public class RatingUserTask extends AbstractUserTask implements IUserTask {
	private static final Logger LOG = LoggerFactory.getLogger(RatingUserTask.class);

	private Long memberID;

	/* (non-Javadoc)
	 * @see org.hip.vif.usertasks.IUserTask#getId()
	 */
	public String getId() {
		return RatingUserTask.class.getName();
	}

	/**
	 * Checks whether the specified user has open rating tasks.
	 * 
	 * @param inMemberID Long
	 * @return boolean <code>true</code> if the task is open for the user, else <code>false</code>.
	 * @throws VException 
	 */
	public boolean isOpen(Long inMemberID) throws VException, SQLException {
		return BOMHelper.getRatingsHome().getCount(createKeyOpenTasks(inMemberID)) != 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.usertasks.IUserTask#createUserTaskViews(org.hip.vif.servlets.VIFContext, java.lang.Long)
	 */
	public Collection<Component> createUserTaskViews(Long inMemberID) throws Exception {
		memberID = inMemberID;
		Collection<Component> outViews = new Vector<Component>();
		
		//for all the member's open rating tasks:
		QueryResult lQuery = BOMHelper.getJoinRatingsToRaterHome().select(createKeyOpenTasks(inMemberID));
		while (lQuery.hasMoreElements()) {
			JoinRatingsToRater lRating = (JoinRatingsToRater)lQuery.nextAsDomainObject();
			outViews.add(new ShowRatingView(lRating, lRating.getFullName(), lRating.getQuestionsToBeRated(), lRating.getCompletionsToBeRated(), lRating.getTextsToBeRated(), this));
		}
		return outViews;
	}
	
	/**
	 * SELECT * FROM tblRatings WHERE RaterID = 1 AND nEfficiency IS NULL AND nEtiquette IS NULL
	 * 
	 * @param inMemberID
	 * @return KeyObject
	 * @throws VException
	 */
	private KeyObject createKeyOpenTasks(Long inMemberID) throws VException {
		KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(RatingsHome.KEY_RATER_ID, inMemberID);
		outKey.setValue(RatingsHome.KEY_EFFICIENCY, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND); //$NON-NLS-1$
		outKey.setValue(RatingsHome.KEY_ETIQUETTE, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND); //$NON-NLS-1$
		outKey.setValue(RatingsHome.KEY_CORRECTNESS, SQLNull.getInstance(), "IS", BinaryBooleanOperator.AND); //$NON-NLS-1$
		return outKey;
	}

	/**
	 * Callback method to save ratings.
	 * 
	 * @param inCorrectness {@link RatingItem#RatingItem}
	 * @param inEfficiency {@link RatingItem#RatingItem}
	 * @param inEtiquette {@link RatingItem#RatingItem}
	 * @param inRemark String
	 * @param inRatingEventsID Long
	 * @return boolean <code>true</code> if successful
	 */
	public boolean saveRatings(RatingsContainer.RatingItem inCorrectness, RatingsContainer.RatingItem inEfficiency, RatingsContainer.RatingItem inEtiquette, String inRemark, Long inRatingEventsID ) {
		try {
			RatingsHome lHome = BOMHelper.getRatingsHome();
			Long lRatedID = lHome.saveRating(inRatingEventsID, memberID, 
					inCorrectness.getId(), inEfficiency.getId(), inEtiquette.getId(), inRemark);
			
			if (lHome.checkRatingCompleted(inRatingEventsID, memberID)) {
				//marks the rating process as completed
				BOMHelper.getRatingEventsHome().getRatingEvents(inRatingEventsID).setCompleted();

				//notifies contributors about completions of rating process
				String lURL = RequestHandler.createRequestedURL(ShowCompletedRatingTask.class, true, ApplicationConstants.KEY_RATING_ID, inRatingEventsID);
				RatingCompletedMail lMail = new RatingCompletedMail(InternetAddress.parse(getMailAddresses(memberID, lRatedID)), 
																	Activator.getMessages(), lURL);
				lMail.send();
			}
			showNotification(Activator.getMessages().getMessage("msg.ratings.feedback.success")); //$NON-NLS-1$
			sendEvent(UserTasksManageTask.class);
			return true;
		}
		catch (Exception exc) {
			LOG.error("Error encountered while saving the ratings!", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	private String getMailAddresses(Long inRaterID, Long inRatedID) throws Exception {
		MemberHome lHome = BOMHelper.getMemberCacheHome();
		VIFMember lMember = (VIFMember) lHome.getMember(inRaterID);
		StringBuilder outAddresses = new StringBuilder(lMember.getMailAddress());
		outAddresses.append(", "); //$NON-NLS-1$
		lMember = (VIFMember) lHome.getMember(inRatedID);
		outAddresses.append(lMember.getMailAddress());
		return new String(outAddresses);
	}

// --- private class ---

	private static class RatingCompletedMail extends AbstractNotification {
		private final static String KEY_SUBJECT 	= "usersettings.ratings.mail.subject"; //$NON-NLS-1$
		private final static String KEY_HELLO 		= "usersettings.ratings.mail.hello"; //$NON-NLS-1$
		private final static String KEY_BODY 		= "usersettings.ratings.mail.body"; //$NON-NLS-1$
		private final static String KEY_TERM 		= "usersettings.ratings.mail.term"; //$NON-NLS-1$
		private static final String TMPL_URL = "<a href=\"%s\">%s</a>"; //$NON-NLS-1$

		private IMessages messages;
		private String url;

		RatingCompletedMail(InternetAddress[] inReceiverMails, IMessages inMessages, String inURL) {
			super(inReceiverMails, false);
			messages = inMessages;
			url = inURL;
		}

		@Override
		protected StringBuilder getBody() {
			StringBuilder outBody = new StringBuilder(getMessage(messages, KEY_HELLO));
			outBody.append("\n\n").append(getFormattedMessage(messages, KEY_BODY, url)); //$NON-NLS-1$
			outBody.append(getMailGreetings());
			return outBody;
		}
		
		@Override
		protected StringBuilder getBodyHtml() {
			String lUrl = String.format(TMPL_URL, url, getMessage(messages, KEY_TERM));
			StringBuilder outBody = new StringBuilder();
			outBody.append("<p>").append(getMessage(messages, KEY_HELLO)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
			outBody.append("<p>").append(getFormattedMessage(messages, KEY_BODY, lUrl)).append("</p>"); //$NON-NLS-1$ //$NON-NLS-2$
			outBody.append("<p>").append(getMailGreetingsHtml()); //$NON-NLS-1$
			return outBody;
		}

		@Override
		protected String getSubjectText() {
			return new String(getSubjectID().append(" ").append(getMessage(messages, KEY_SUBJECT))); //$NON-NLS-1$
		}
	}
	
}
