/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.forum.usersettings.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.impl.RatingsHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.ui.ShowCompletedRatingView;
import org.hip.vif.web.tasks.AbstractVIFTask;

import com.vaadin.ui.Component;

/**
 * Task to display the result of the rating process to the involved participants.
 * 
 * @author Luthiger
 * Created: 25.12.2011
 */
 @Partlet
public class ShowCompletedRatingTask extends AbstractVIFTask {
	 
	 /* (non-Javadoc)
	  * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	  */
	 @Override
	 protected String needsPermission() {
		 return ""; //$NON-NLS-1$
	 }

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		try {
			emptyContextMenu();
			
			Long lMemberID = getActor().getActorID();
			IMessages lMessages = Activator.getMessages();

			Long lRatingID = (Long) getParameters().get(ApplicationConstants.KEY_RATING_ID);
			RatingsHome lHome = BOMHelper.getRatingsHome();
			//check whether the user is allowed to see the outcome of this rating
			if (!lHome.isInvolved(lRatingID, lMemberID)) {
				return new ShowCompletedRatingView(lMessages.getMessage("msg.ratings.feedback.permission")); //$NON-NLS-1$
			}
			
			//check whether the rating is completed
			if (!lHome.checkRatingCompleted(lRatingID)) {
				return new ShowCompletedRatingView(lMessages.getMessage("msg.ratings.feedback.pending"));				 //$NON-NLS-1$
			}
			
			//disclose the ratings to the involved parties
			return new ShowCompletedRatingView(getRatings(lRatingID),
					BOMHelper.getJoinRatingsToQuestionHome().getQuestionsToBeRated(lRatingID),
					BOMHelper.getJoinRatingsToCompletionHome().getCompletionsToBeRated(lRatingID),
					BOMHelper.getJoinRatingsToTextHome().getTextsToBeRated(lRatingID),
					this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}
	
	private QueryResult getRatings(Long inRatingID) throws VException, SQLException {
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(RatingsHome.KEY_RATINGEVENTS_ID, inRatingID);
		return BOMHelper.getJoinRatingsToRaterHome().select(lKey);
	}

}