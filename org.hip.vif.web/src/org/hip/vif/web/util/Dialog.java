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

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Displays a popup window for a dialog. Usage:<pre> DialogWindow lDialog = Dialog.openQuestion("Warning", "Do you ...?", 
 *     new Dialog.ICommand() {});
 * Button lButton = new Button("Button with confirmation");
 * lButton.addListener(Dialog.createClickListener(lDialog, this));</pre>
 * 
 * @author Luthiger
 * Created: 21.08.2011
 */
public class Dialog {
	
	/**
	 * Creates a dialog window with two buttons (yes / no).
	 * 
	 * @param inTitle String the dialog window's title
	 * @param inQuestion String the question
	 * @return {@link DialogWindow}
	 */
	public static DialogWindow openQuestion(String inTitle, String inQuestion) {
		return new DialogWindow(inTitle, inQuestion);
	}
	
	private static HorizontalLayout createButtons(Button inButton1, Button inButton2) {
		HorizontalLayout outButtons = new HorizontalLayout();
		outButtons.setStyleName("vif-buttons"); //$NON-NLS-1$
		outButtons.setSpacing(true);
		outButtons.setWidth(Sizeable.SIZE_UNDEFINED, 0);
		outButtons.addComponent(inButton1);
		outButtons.addComponent(inButton2);
		outButtons.setExpandRatio(inButton2, 1);
		outButtons.setComponentAlignment(inButton2, Alignment.MIDDLE_LEFT);
		return outButtons;
	}
	
	/**
	 * Creates a dialog window with two buttons (yes / no).<br />
	 * This dialog adds click listeners to the buttons. 
	 * The <code>NO</code> listener closes the dialog window.
	 * The <code>YES</code> listener closes the dialog window and executes the passed <code>ICommand</code>.  
	 * 
	 * @param inTitle String the dialog window's title
	 * @param inQuestion String the question
	 * @param inCommand {@link ICommand} the command to execute if the yes-button is clicked
	 * @return
	 */
	@SuppressWarnings("serial")
	public static DialogWindow openQuestion(String inTitle, String inQuestion, final ICommand inCommand) {
		final DialogWindow out = new DialogWindow(inTitle, inQuestion);
		out.addNoListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent inEvent) {
				out.setVisible(false);
			}
		});
		out.addYesListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent inEvent) {
				out.setVisible(false);
				inCommand.execute();
			}
		});
		return out;
	}
	
	/**
	 * Creates a <code>Button.ClickListener</code> that displays the passed <code>DialogWindow</code>. Usage: <pre>
	 * Button lButton = new Button("Button with confirmation");
	 * lButton.addListener(Dialog.createClickListener(lDialog, this));</pre>
	 * 
	 * @param inDialog {@link DialogWindow}
	 * @param inComponent {@link Component}
	 * @return {@link ClickListener}
	 */
	@SuppressWarnings("serial")
	public static Button.ClickListener createClickListener(final DialogWindow inDialog, final Component inComponent) {
		return new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent inEvent) {
				if (inDialog.isDisplayable()) {
					inComponent.getWindow().addWindow(inDialog.getWindow());
					inDialog.center();
				}
				inDialog.setVisible(true);
			}
		};
	}
	
// ---
	
	/**
	 * Interface for commands to execute if the dialog's <code>YES</code> button is clicked.
	 * 
	 * @author Luthiger
	 * Created: 24.11.2011
	 */
	public static interface ICommand {
		/**
		 * Execute the command
		 */
		void execute();
	}
	
	/**
	 * A dialog window with title, dialog and two click buttons.
	 */
	public static class DialogWindow {
		private Window dialog;
		private Button buttonYes;
		private Button buttonNo;
		
		/**
		 * Set constructor private
		 */
		DialogWindow(String inTitle, String inQuestion) {
			IMessages lMessages = Activator.getMessages();
			dialog = new Window(inTitle);
			VerticalLayout lLayout = (VerticalLayout) dialog.getContent();
			lLayout.setMargin(true);
			lLayout.setSpacing(true);
			lLayout.setSizeUndefined();
			
			dialog.addComponent(new Label(inQuestion));
			buttonYes = new Button(lMessages.getMessage("dialog.button.lbl.yes")); //$NON-NLS-1$
			buttonYes.setClickShortcut(KeyCode.ENTER);
			buttonNo = new Button(lMessages.getMessage("dialog.button.lbl.no")); //$NON-NLS-1$
			buttonNo.setClickShortcut(KeyCode.ESCAPE);
			lLayout.addComponent(createButtons(buttonYes, buttonNo));
			dialog.setModal(true);
		};
		
		/**
		 * @return {@link Window}
		 */
		public Window getWindow() {
			return dialog;
		}
		
		/**
		 * Closes the window, can be called when the parent window is detached.
		 */
		public void close() {
			Window lParent = dialog.getParent();
			if (lParent != null) {				
				lParent.removeWindow(dialog);
			}
		}

		/**
		 * Sets the dialog's visibility.
		 * 
		 * @param inVisible boolean <code>true</code> makes the existing dialog window visible, <code>false</code> makes the visible window invisible
		 */
		public void setVisible(boolean inVisible) {
			dialog.setVisible(inVisible);
		}

		/**
		 * Centers the window.
		 */
		public void center() {
			dialog.center();
		}
		
		/**
		 * @param inListener {@link ClickListener} adds the <code>yes</code> button's click listener
		 */
		public void addYesListener(Button.ClickListener inListener) {
			buttonYes.addListener(inListener);
		}

		/**
		 * @param inListener {@link ClickListener} adds the <code>no</code> button's click listener
		 */
		public void addNoListener(Button.ClickListener inListener) {
			buttonNo.addListener(inListener);
		}
		
		/**
		 * @return boolean <code>true</code> if the dialog window can be displayed
		 */
		public boolean isDisplayable() {
			return dialog.getParent() == null;
		}
	}

}
