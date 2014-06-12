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

package org.hip.vif.forum.groups.util;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.groups.Activator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for switch buttons [Subcribe] and [Bookmark] on question view.
 * 
 * @author Luthiger
 * Created: 30.08.2011
 */
public class SwitchHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SwitchHelper.class);
	
	private static final String KEY_BOOKMARK_CREATE = "ui.question.view.bookmark.create"; //$NON-NLS-1$
	private static final String KEY_BOOKMARK_DELETE = "ui.question.view.bookmark.delete"; //$NON-NLS-1$
	private static final String KEY_SUBSCRIPTION_CREATE = "ui.question.view.subscription.create"; //$NON-NLS-1$
	private static final String KEY_SUBSCRIPTION_DELETE = "ui.question.view.subscription.delete"; //$NON-NLS-1$
	
	private boolean hasSubscription;
	private boolean hasBookmark;
	private Long questionID;
	private Long actorID;
	
	private IMessages messages;
	private boolean isInitialized;
	
	/**
	 * Constructor for an anonymous instance.
	 */
	public SwitchHelper() {
		isInitialized = false;
	}
	
	/**
	 * Constructor for a personalized instance.
	 * 
	 * @param inHasSubscription boolean
	 * @param inHasBookmark boolean
	 * @param inQuestionID Long
	 * @param inActorID Long
	 */
	public SwitchHelper(boolean inHasSubscription, boolean inHasBookmark, Long inQuestionID, Long inActorID) {
		isInitialized = true;
		messages = Activator.getMessages();
		hasSubscription = inHasSubscription;
		hasBookmark = inHasBookmark;
		questionID = inQuestionID;
		actorID = inActorID;
	}
	
	public boolean isInitialized() {
		return isInitialized;
	}
	
	public boolean hasSubscription() {
		return hasSubscription;
	}
	
	public boolean hasBookmark() {
		return hasBookmark;
	}

	public String getBookmarkCaption() {
		return hasBookmark ? messages.getMessage(KEY_BOOKMARK_DELETE) : messages.getMessage(KEY_BOOKMARK_CREATE);
	}

	public String getSubscriptionCaption() {
		return hasSubscription ? messages.getMessage(KEY_SUBSCRIPTION_DELETE) : messages.getMessage(KEY_SUBSCRIPTION_CREATE);
	}

	/**
	 * Saves the new bookmark.
	 * 
	 * @param inBookmarkText String the bookmark label
	 * @return boolean <code>true</code> if the bookmark could be saved successfully
	 */
	public boolean createBookmark(String inBookmarkText) {
		if (!isInitialized) return false;
		if (hasBookmark) return false;
		try {
			BOMHelper.getBookmarkHome().ucNew(questionID.toString(), actorID, inBookmarkText);
			hasBookmark = !hasBookmark;
			return true;
		} catch (VException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	public boolean deleteBookmark() {
		if (!isInitialized) return false;
		if (!hasBookmark) return false;
		try {
			BOMHelper.getBookmarkHome().delete(questionID, actorID);
			hasBookmark = !hasBookmark;
			return true;
		} catch (VException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
	/**
	 * Saves the new subscription.
	 * 
	 * @param inLocal boolean <code>true</code> if the subscription is only local for the selected question, <code>false</code> if the user subscibed for the whole subtree
	 * @return boolean <code>true</code> if the subscription could be saved successfully
	 */
	public boolean createSubscription(boolean inLocal) {
		if (!isInitialized) return false;
		if (hasSubscription) return false;
		try {
			BOMHelper.getSubscriptionHome().ucNew(questionID.toString(), actorID, inLocal);				
			hasSubscription = !hasSubscription;
			return true;
		} catch (VException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		}
		return false;
	}

	public boolean deleteSubscription() {
		if (!isInitialized) return false;
		if (!hasSubscription) return false;
		try {
			BOMHelper.getSubscriptionHome().delete(questionID, actorID);				
			hasSubscription = !hasSubscription;
			return true;
		} catch (VException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error encountered while saving the bookmark.", exc); //$NON-NLS-1$
		}
		return false;
	}
	
}
