/**
    This package is part of the application VIF.
    Copyright (C) 2011-2015, Benno Luthiger

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

import org.hip.vif.forum.usersettings.Activator;
import org.ripla.interfaces.IMessages;

import com.vaadin.ui.AbstractSelect;

/** Container for the rating items used in the rating form's option group.
 *
 * @author Luthiger Created: 23.12.2011 */
@SuppressWarnings("serial")
public class RatingsContainer extends AbstractSelect {
    private static final String[] LABELS = { "ui.rating.label.negative", "ui.rating.label.neutral",
    "ui.rating.label.positive" };

    private RatingsContainer() {
        super();

        final IMessages lMessages = Activator.getMessages();
        addItem(new RatingsContainer.RatingItem(1, lMessages.getMessage(LABELS[2]))); //$NON-NLS-1$
        addItem(new RatingsContainer.RatingItem(0, lMessages.getMessage(LABELS[1]))); //$NON-NLS-1$
        addItem(new RatingsContainer.RatingItem(-1, lMessages.getMessage(LABELS[0]))); //$NON-NLS-1$
    }

    /** Factory method, creates the data source for the option group.
     *
     * @return {@link RatingsContainer} */
    public static RatingsContainer getRatingsContainer() {
        return new RatingsContainer();
    }

    /** Factory method for a RatingItem with the specified value.
     *
     * @param inValue int
     * @return {@link RatingItem} */
    public static RatingItem getRatingItem(final int inValue) {
        return new RatingsContainer.RatingItem(inValue, Activator.getMessages().getMessage(LABELS[inValue + 1]));
    }

    // ---

    /** The bean for the rating item. */
    public static class RatingItem {
        private transient final int id; // NOPMD
        private final String caption;

        private RatingItem(final int inID, final String inCaption) {
            id = inID;
            caption = inCaption;
        }

        /** @return int the rating id */
        public int getId() {
            return id;
        }

        @Override
        public String toString() { // NOPMD
            return caption;
        }
    }

}
