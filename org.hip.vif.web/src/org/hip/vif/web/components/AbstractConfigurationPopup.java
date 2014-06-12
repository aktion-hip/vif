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

package org.hip.vif.web.components;

import org.hip.vif.web.Activator;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Base class for popup windows displayed in the initial configuration workflow.
 * 
 * @author Luthiger Created: 12.02.2012
 */
@SuppressWarnings("serial")
public abstract class AbstractConfigurationPopup extends Window {
	private Window popup;

	/**
	 * @param inCaption
	 *            String
	 * @return {@link Window} the popup window
	 */
	protected Window createPopup(final String inCaption) {
		popup = new Window(inCaption);
		popup.setModal(true);
		return popup;
	}

	protected Window getPopup() {
		return popup;
	}

	/**
	 * @return {@link VerticalLayout} the configured layout of the popup window
	 */
	protected VerticalLayout createLayout(final VerticalLayout outLayout) {
		outLayout.setStyleName("vif-lookup"); //$NON-NLS-1$
		outLayout.setMargin(true);
		outLayout.setSpacing(true);
		outLayout.setSizeFull();
		return outLayout;
	}

	/**
	 * @return {@link Button} the popup's close button
	 */
	protected Button createCloseButton() {
		final Button outClose = new Button(Activator.getMessages().getMessage(
				"lookup.window.button.close"), new Button.ClickListener() { //$NON-NLS-1$
					@Override
					public void buttonClick(final ClickEvent inEvent) {
						close();
					}
				});
		outClose.setClickShortcut(KeyCode.ESCAPE);
		outClose.setImmediate(true);
		outClose.setStyleName("vif-lookup-close"); //$NON-NLS-1$
		return outClose;
	}

	/**
	 * @param inListener
	 *            {@link CloseListener} adds close listener to popup
	 */
	@Override
	public void addCloseListener(final CloseListener inListener) {
		popup.addCloseListener(inListener);
	}

}
