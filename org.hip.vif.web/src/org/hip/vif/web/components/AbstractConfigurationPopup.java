/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.web.components;

import org.ripla.web.util.Popup;

import com.vaadin.ui.VerticalLayout;

/** Base class for popup windows displayed in the initial configuration workflow.
 *
 * @author lbenno */
public class AbstractConfigurationPopup { // NOPMD

    /** @return {@link VerticalLayout} the configured layout of the popup window */
    public static VerticalLayout createLayout() {
        final VerticalLayout out = new VerticalLayout();
        out.setStyleName("vif-lookup"); //$NON-NLS-1$
        out.setMargin(true);
        out.setSpacing(true);
        out.setSizeFull();
        return out;
    }

    /** Closes the popup. */
    public void close() {
        Popup.removePopups();
    }

}
