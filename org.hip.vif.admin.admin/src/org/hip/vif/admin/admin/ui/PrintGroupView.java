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
import org.hip.vif.admin.admin.tasks.PrintGroupTask;
import org.hip.vif.web.components.LabelValueTable;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

/**
 * View to select the discussion groups to print out.
 * 
 * @author Luthiger Created: 30.12.2011
 */
@SuppressWarnings("serial")
public class PrintGroupView extends AbstractAdminView {
	private static final int SELECT_SIZE = 7;
	private static final String VIF_STYLE = "vif-send-mail"; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param inGroups
	 *            {@link GroupContainer}
	 * @param inTask
	 *            {@link PrintGroupTask}
	 */
	public PrintGroupView(final GroupContainer inGroups,
			final PrintGroupTask inTask) {
		final IMessages lMessages = Activator.getMessages();
		final VerticalLayout lLayout = initLayout(lMessages,
				"admin.print.title.page"); //$NON-NLS-1$

		lLayout.addComponent(new Label(lMessages
				.getMessage("admin.print.remark"), ContentMode.HTML)); //$NON-NLS-1$
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
		lLayout.addComponent(lTable);

		final Button lPrint = new Button(
				lMessages.getMessage("admin.print.button.print")); //$NON-NLS-1$
		lPrint.addClickListener(new Button.ClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void buttonClick(final ClickEvent inEvent) {
				if (!isValid(lGroups)) {
					Notification.show(
							lMessages.getMessage("admin.print.msg.not.valid"), Type.WARNING_MESSAGE); //$NON-NLS-1$
				} else {
					if (!inTask.printGroups(
							(Collection<GroupWrapper>) lGroups.getValue(),
							lPrint)) {
						Notification.show(
								lMessages.getMessage("errmsg.print"), Type.WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
			}
		});
		lLayout.addComponent(lPrint);
	}

	@SuppressWarnings("unchecked")
	private boolean isValid(final ListSelect inGroups) {
		return !((Collection<GroupWrapper>) inGroups.getValue()).isEmpty();
	}

}
