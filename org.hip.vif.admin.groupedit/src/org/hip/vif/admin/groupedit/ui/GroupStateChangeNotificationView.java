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

package org.hip.vif.admin.groupedit.ui;

import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.tasks.GroupStateChangeNotificationTask;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to ask the group administrator whether to notify the group's
 * participants about state changes of the discussion group.
 * 
 * @author Luthiger Created: 13.11.2011
 */
@SuppressWarnings("serial")
public class GroupStateChangeNotificationView extends CustomComponent {
	private static final int DEF_WIDTH = 700;
	private static final int DEF_HEIGHT = 250;

	/**
	 * View constructor.
	 * 
	 * @param inGroupName
	 *            String
	 * @param inSubject
	 *            String
	 * @param inBody
	 *            String
	 * @param inTask
	 *            {@link GroupStateChangeNotificationTask}
	 */
	public GroupStateChangeNotificationView(String inGroupName,
			String inSubject, String inBody,
			final GroupStateChangeNotificationTask inTask) {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getFormattedMessage("ui.group.notify.view.title.page", inGroupName)), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-remark", lMessages.getMessage("ui.group.notify.label.remark")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final Form lForm = new Form();
		lForm.setLayout(new VerticalLayout());

		String lLabel = lMessages.getMessage("ui.group.notify.label.subject"); //$NON-NLS-1$
		lForm.getLayout().addComponent(
				new Label(String.format(VIFViewHelper.TMPL_TITLE,
						"vif-title", lLabel), Label.CONTENT_XHTML)); //$NON-NLS-1$
		final TextField lSubject = RiplaViewHelper.createTextField(inSubject,
				DEF_WIDTH, new NotEmptyValidator(lLabel, lMessages));
		addField(lLabel, lSubject, lForm, lMessages);

		lLabel = lMessages.getMessage("ui.group.notify.label.body"); //$NON-NLS-1$
		lForm.getLayout().addComponent(
				new Label(String.format(VIFViewHelper.TMPL_TITLE,
						"vif-title", lLabel), Label.CONTENT_XHTML)); //$NON-NLS-1$
		final RichTextArea lBody = VIFViewHelper
				.createTextArea(inBody, DEF_WIDTH, DEF_HEIGHT,
						new NotEmptyValidator(lLabel, lMessages));
		addField(lLabel, lBody, lForm, lMessages);
		lLayout.addComponent(lForm);

		Button lNotify = new Button(
				lMessages.getMessage("ui.group.notify.button.notify")); //$NON-NLS-1$
		lNotify.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					if (!inTask.doNotification(lSubject.getValue().toString(),
							lBody.getValue().toString())) {
						getWindow()
								.showNotification(
										lMessages
												.getMessage("errmsg.notification.send"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
				catch (InvalidValueException exc) {
					// intentionally left empty
				}
			}
		});
		Button lContinue = new Button(
				lMessages.getMessage("ui.group.notify.button.continue")); //$NON-NLS-1$
		lContinue.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				inTask.doContinue();
			}
		});
		lLayout.addComponent(VIFViewHelper.createButtons(lNotify, lContinue));
	}

	private void addField(String inFieldID, AbstractField inField, Form inForm,
			IMessages inMessages) {
		inForm.addField(inFieldID, inField);
		inField.setRequiredError(inMessages.getFormattedMessage(
				"errmsg.field.not.empty", inFieldID)); //$NON-NLS-1$
		inField.setRequired(true);
		inField.setImmediate(true);
	}

	// --- private classes ---

	private static class NotEmptyValidator extends StringLengthValidator {
		NotEmptyValidator(String inFieldName, IMessages inMessages) {
			super(inMessages.getFormattedMessage(
					"errmsg.field.not.empty", inFieldName), 1, -1, false); //$NON-NLS-1$
		}
	}

}
