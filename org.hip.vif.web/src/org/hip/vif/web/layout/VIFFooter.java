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
package org.hip.vif.web.layout;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * The layout for the page footer.
 * 
 * @author Luthiger Created: 11.05.2011
 */
@SuppressWarnings("serial")
public class VIFFooter extends CustomComponent {
	public static final String DFT_FOOTER_TEXT = "&copy; Aktion HIP"; //$NON-NLS-1$

	private final HorizontalLayout layout;

	/**
	 * VIFFooter constructor
	 * 
	 * @param inFooterText
	 *            String the text to display in the footer
	 */
	public VIFFooter(final String inFooterText) {
		layout = createLayout();
		setCompositionRoot(layout);
		populateLayout(layout, inFooterText);
	}

	private void populateLayout(final HorizontalLayout inLayout,
			final String inFooterText) {
		final Label lFooterText = new Label(inFooterText, ContentMode.HTML); //$NON-NLS-1$
		inLayout.addComponent(lFooterText);
		inLayout.setComponentAlignment(lFooterText, Alignment.BOTTOM_LEFT);
	}

	private HorizontalLayout createLayout() {
		final HorizontalLayout out = new HorizontalLayout();
		out.setStyleName("vif-footer-layout"); //$NON-NLS-1$
		out.setWidth("100%"); //$NON-NLS-1$
		return out;
	}

	/**
	 * Add some component to footer after the user logged in.
	 */
	public void setLoggedIn() {
		// empty for the moment
	}

	/**
	 * Factory method.
	 * 
	 * @param inFooterText
	 *            String
	 * @return {@link VIFFooter}
	 */
	public static VIFFooter createFooter(final String inFooterText) {
		return new VIFFooter(inFooterText);
	}

}
