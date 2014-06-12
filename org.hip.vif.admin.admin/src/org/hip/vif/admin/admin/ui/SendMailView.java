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
package org.hip.vif.admin.admin.ui;

import java.util.Collection;

import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.data.GroupContainer;
import org.hip.vif.admin.admin.data.GroupWrapper;
import org.hip.vif.admin.admin.tasks.SendMailTask;
import org.hip.vif.web.components.LabelValueTable;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display the input form to send mails to participants.
 * 
 * @author Luthiger Created: 30.10.2011
 */
@SuppressWarnings("serial")
public class SendMailView extends AbstractAdminView {
	private static final int SELECT_SIZE = 5;
	private static final int WIDTH = 600;
	private static final String VIF_STYLE = "vif-send-mail"; //$NON-NLS-1$
	private Button send;

	/**
	 * 
	 * @param inGroups
	 *            {@link GroupContainer}
	 * @param inTask
	 *            {@link SendMailTask}
	 */
	public SendMailView(final GroupContainer inGroups, final SendMailTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		final VerticalLayout lLayout = initLayout(lMessages,
				"admin.send.mail.title.page"); //$NON-NLS-1$

		lLayout.addComponent(new Label(lMessages
				.getMessage("admin.send.remark"), ContentMode.HTML)); //$NON-NLS-1$
		lLayout.addComponent(RiplaViewHelper.createSpacer());

		final LabelValueTable lTable = new LabelValueTable();
		final ListSelect lGroups = new ListSelect();
		lGroups.setContainerDataSource(inGroups);
		lGroups.setRows(Math.min(SELECT_SIZE, inGroups.size()));
		lGroups.setStyleName(VIF_STYLE);
		lGroups.setMultiSelect(true);
		lGroups.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		lGroups.setItemCaptionPropertyId(GroupContainer.PROPERTY_CAPTION);
		lGroups.focus();
		lTable.addRowEmphasized(
				lMessages.getMessage("admin.send.mail.label.select"), lGroups); //$NON-NLS-1$

		final TextField lSubject = new TextField();
		lSubject.setWidth(WIDTH, Unit.PIXELS);
		lSubject.setStyleName(VIF_STYLE);
		lTable.addRowEmphasized(
				lMessages.getMessage("admin.send.mail.label.subject"), lSubject); //$NON-NLS-1$

		final RichTextArea lBody = new RichTextArea();
		lBody.setStyleName("vif-editor " + VIF_STYLE); //$NON-NLS-1$
		lBody.setWidth(WIDTH, Unit.PIXELS);
		lTable.addRowEmphasized(
				lMessages.getMessage("admin.send.mail.label.body"), lBody); //$NON-NLS-1$
		lLayout.addComponent(lTable);

		send = new Button(lMessages.getMessage("admin.send.mail.button.send")); //$NON-NLS-1$
		send.setClickShortcut(KeyCode.ENTER);
		send.addClickListener(new Button.ClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void buttonClick(final ClickEvent inEvent) {
				if (!isValid(lGroups, lSubject, lBody)) {
					Notification.show(
							lMessages
									.getMessage("admin.send.mail.msg.not.valid"), Type.WARNING_MESSAGE); //$NON-NLS-1$
					return;
				}
				if (!inTask.processGroups((Collection<GroupWrapper>) lGroups
						.getValue(), lSubject.getValue().toString(), lBody
						.getValue())) {
					Notification.show(
							lMessages.getMessage("admin.send.mail.msg.errmsg"), Type.WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
		lLayout.addComponent(send);
	}

	@SuppressWarnings("unchecked")
	private boolean isValid(final ListSelect inGroups,
			final TextField inSubject, final RichTextArea inBody) {
		return !(((Collection<GroupWrapper>) inGroups.getValue()).isEmpty()
				|| inSubject.getValue().toString().trim().length() == 0 || inBody
				.getValue().trim().length() == 0);
	}

}
