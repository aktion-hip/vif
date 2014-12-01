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

package org.hip.vif.web.util;

import org.hip.vif.web.interfaces.IPluggableWithLookup;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

/** Helper class to create link buttons with preconfigured click listeners.
 *
 * @author Luthiger */
public class LinkButtonHelper {

    /** Type of lookup windows. */
    public enum LookupType {
        MEMBER, MEMBER_SEARCH, MEMBER_SELECT, CONTENT, BIBLIOGRAPHY;
    }

    @SuppressWarnings("serial")
    public static Button createLinkButton(final String inCaption,
            final LookupType inType, final Long inID,
            final IPluggableWithLookup inController) {
        final Button outLink = new Button(inCaption);
        outLink.setStyleName(BaseTheme.BUTTON_LINK);
        outLink.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                inController.requestLookup(inType, inID);
            }
        });
        return outLink;
    }

}