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

package org.hip.vif.forum.usersettings.data;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.usersettings.Activator;

import com.vaadin.ui.AbstractSelect;

/**
 * Container for the rating items used in the rating form's option group.
 * 
 * @author Luthiger
 * Created: 23.12.2011
 */
@SuppressWarnings("serial")
public class RatingsContainer extends AbstractSelect {
	
	private RatingsContainer() {
		IMessages lMessages = Activator.getMessages();
		addItem(new RatingItem(1, lMessages.getMessage("ui.rating.label.positive"))); //$NON-NLS-1$
		addItem(new RatingItem(0, lMessages.getMessage("ui.rating.label.neutral"))); //$NON-NLS-1$
		addItem(new RatingItem(-1, lMessages.getMessage("ui.rating.label.negative"))); //$NON-NLS-1$
	}
	
	/**
	 * Factory method, creates the data source for the option group.
	 * 
	 * @return {@link RatingsContainer}
	 */
	public static RatingsContainer getRatingsContainer() {
		return new RatingsContainer();
	}
	
// ---
	
	public static class RatingItem {
		private int id;
		private String caption;

		RatingItem(int inID, String inCaption) {
			id = inID;
			caption = inCaption;
		}
		
		public int getId() {
			return id;
		}
		public String toString() {
			return caption;
		}
	}

}
