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

package org.hip.vif.forum.member.ui;

import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.forum.member.Activator;
import org.hip.vif.forum.member.tasks.PwrdEditTask;
import org.hip.vif.web.components.LabelValueTable;
import org.hip.vif.web.util.PasswordInputChecker;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to edit the password.
 * 
 * @author Luthiger
 * Created: 12.10.2011
 */
@SuppressWarnings("serial")
public class EditPwrdView extends CustomComponent {
	private static final int DFT_WIDTH_INPUT = 300;
	
	/**
	 * View to display message that password change is disabled.
	 */
	public EditPwrdView() {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", "ui.pwrd.view.title.disabled"), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Displays the password edit form.
	 * 
	 * @param inUserID String the user id
	 * @param inTask {@link PwrdEditTask} this view's controller
	 */
	public EditPwrdView(String inUserID, final PwrdEditTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle",  //$NON-NLS-1$
				lMessages.getFormattedMessage("ui.pwrd.view.title.page", inUserID)), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
	
		LabelValueTable lTable = new LabelValueTable();
		final PasswordField lOld = createPasswordField(DFT_WIDTH_INPUT);
		lOld.focus();
		lTable.addRow(lMessages.getMessage("ui.pwrd.editor.label.old"), lOld); //$NON-NLS-1$

		final PasswordField lNew1 = createPasswordField(DFT_WIDTH_INPUT);
		lTable.addRow(lMessages.getMessage("ui.pwrd.editor.label.new"), lNew1); //$NON-NLS-1$
		final PasswordField lNew2 = createPasswordField(DFT_WIDTH_INPUT);
		lTable.addRow(lMessages.getMessage("ui.pwrd.editor.label.confirm"), lNew2); //$NON-NLS-1$
		lLayout.addComponent(lTable);
		
		lLayout.addComponent(VIFViewHelper.createSpacer());
		Button lSave = new Button(lMessages.getMessage("ui.member.button.save")); //$NON-NLS-1$
		lLayout.addComponent(lSave);
		lSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				if (new PasswordInputChecker(lNew1, lNew2, getWindow()).checkInput()) {
					try {
						if (!inTask.savePwrd(lOld.getValue().toString(), lNew1.getValue().toString(), lNew2.getValue().toString())) {
							getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
						}
					} 
					catch (InvalidAuthenticationException exc) {
						getWindow().showNotification(lMessages.getMessage("errmsg.pwrd.invalid"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			}
		});

	}
	
	private PasswordField createPasswordField(int inWidth) {
		PasswordField out = new PasswordField();
		out.setWidth(inWidth, Sizeable.UNITS_PIXELS);
		out.setStyleName("vif-input"); //$NON-NLS-1$
		return out;
	}
	
}
