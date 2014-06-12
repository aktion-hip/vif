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

package org.hip.vif.web.util;

import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Activator;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Helper class to display a view component in a popup window.
 * 
 * @author Luthiger
 * Created: 08.01.2012
 */
public class Popup {

	/**
	 * Displays the specified component in a popup window.
	 * 
	 * @param inTitle String the popup's title
	 * @param inLayout {@link Layout} the view component to display
	 * @param inWidth int the window width
	 * @param inHeight int the window height
	 */
	public static void displayPopup(String inTitle, Layout inLayout, int inWidth, int inHeight) {
		PopupWindow lPopup = new PopupWindow(inTitle, inLayout, inWidth, inHeight);
		if (lPopup.getParent() == null) {
			ApplicationData.getWindow().addWindow(lPopup.getWindow());
		}
		lPopup.setPosition(50, 50);
	}
	
// ---
	
	@SuppressWarnings("serial")
	public static class PopupWindow extends VerticalLayout {
		private Window popupWindow;
		
		/**
		 * Set constructor private
		 */
		PopupWindow(String inTitle, Layout inLayout, int inWidth, int inHeight) {
			popupWindow = new Window(inTitle);
			popupWindow.setWidth(inWidth, Sizeable.UNITS_PIXELS);
			popupWindow.setHeight(inHeight, Sizeable.UNITS_PIXELS);
			
			VerticalLayout lLayout = (VerticalLayout) popupWindow.getContent();
			lLayout.setStyleName("vif-lookup"); //$NON-NLS-1$
			lLayout.setMargin(true);
			lLayout.setSpacing(true);
			lLayout.setSizeFull();
			lLayout.addComponent(inLayout);
			
			Button lClose = new Button(Activator.getMessages().getMessage("lookup.window.button.close"),  //$NON-NLS-1$
					new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent inEvent) {
					(popupWindow.getParent()).removeWindow(popupWindow);
				}
			});
			lClose.setClickShortcut(KeyCode.ESCAPE);
			lClose.setImmediate(true);
			lClose.setStyleName("vif-lookup-close"); //$NON-NLS-1$
			lLayout.addComponent(lClose);
		};
		
		/**
		 * @return {@link Window}
		 */
		public Window getWindow() {
			return popupWindow;
		}

		void setPosition(int inPositionX, int inPositionY) {
			popupWindow.setPositionX(inPositionX);
			popupWindow.setPositionX(inPositionY);
		}

		/**
		 * Sets the dialog's visibility.
		 * 
		 * @param inVisible boolean <code>true</code> makes the existing dialog window visible, <code>false</code> makes the visible window invisible
		 */
		public void setVisible(boolean inVisible) {
			popupWindow.setVisible(inVisible);
		}
	}


}
