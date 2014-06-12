/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * A blank popup window that can be configured using the
 * <code>BlankPopup.addComponent()</code> method.
 * 
 * @author Luthiger Created: 15.02.2012
 */
@SuppressWarnings("serial")
public class BlankPopup extends AbstractConfigurationPopup {

	private final VerticalLayout layout;
	private final Window popup;

	/**
	 * BlankPopup constructor.
	 * 
	 * @param inCaption
	 *            String
	 * @param inWidth
	 *            int
	 * @param inHeight
	 *            int
	 */
	public BlankPopup(final String inCaption, final int inWidth,
			final int inHeight) {
		popup = createPopup(inCaption);
		popup.setWidth(inWidth, Unit.PIXELS);
		popup.setHeight(inHeight, Unit.PIXELS);

		layout = createLayout((VerticalLayout) popup.getContent());
	}

	public void addComponent(final Component inComponent) {
		layout.addComponent(inComponent);
	}

	/**
	 * Displays the popup window.
	 */
	public void show() {
		UI.getCurrent().addWindow(popup);
	}

	@Override
	public void setClosable(final boolean inCloseable) {
		popup.setClosable(inCloseable);
	}

}
