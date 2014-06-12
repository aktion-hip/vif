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

package org.hip.vif.admin.groupadmin.ui;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.tasks.AdminShowPendingTask;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * Displays the list of questions users requested a state change, either the
 * open question should be set to 'answered' or an answered question should be
 * reopened.
 * 
 * @author Luthiger Created: 25.11.2011
 */
@SuppressWarnings("serial")
public class PendingListView extends AbstractContributionsProcessView {

	private final boolean confirmationMode;

	/**
	 * Constructor
	 * 
	 * @param inSetAnswerables
	 *            {@link ContributionContainer}
	 * @param inSetReopen
	 *            ContributionContainer
	 * @param inTitle
	 *            String
	 * @param inTask
	 *            {@link AdminShowPendingTask}
	 */
	public PendingListView(final ContributionContainer inSetAnswerables,
			final ContributionContainer inSetReopen, final String inTitle,
			final AdminShowPendingTask inTask) {
		confirmationMode = false;
		final IMessages lMessages = Activator.getMessages();
		final VerticalLayout lLayout = initComponent(inTitle);

		if (inSetAnswerables.hasItems()) {
			final Button lSetAnswered = new Button(
					lMessages.getMessage("ui.pending.button.answered")); //$NON-NLS-1$
			lSetAnswered.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (VIFViewHelper.processAction(inSetAnswerables)) {
						if (!inTask.processAnswered()) {
							Notification.show(
									lMessages
											.getMessage("errmsg.pending.state.change"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
			final Button lReject1 = new Button(
					lMessages.getMessage("ui.pending.button.reject")); //$NON-NLS-1$
			lReject1.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (VIFViewHelper.processAction(inSetAnswerables)) {
						if (!inTask.rejectAnswered()) {
							Notification.show(
									lMessages
											.getMessage("errmsg.pending.state.change"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});

			final Table lTable = createTable(inSetAnswerables,
					WorkflowAwareContribution.S_ANSWERED_REQUESTED, inTask);
			lLayout.addComponent(lTable);

			final HorizontalLayout lButtons = RiplaViewHelper.createButtons(
					lSetAnswered, lReject1);
			lLayout.addComponent(lButtons);

			lLayout.addComponent(RiplaViewHelper.createSpacer());
		}

		if (inSetReopen.hasItems()) {
			final Button lReopen = new Button(
					lMessages.getMessage("ui.pending.button.reopen")); //$NON-NLS-1$
			lReopen.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (VIFViewHelper.processAction(inSetReopen)) {
						if (!inTask.processReopen()) {
							Notification.show(
									lMessages
											.getMessage("errmsg.pending.state.change"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});
			final Button lReject2 = new Button(
					lMessages.getMessage("ui.pending.button.reject")); //$NON-NLS-1$
			lReject2.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent inEvent) {
					if (VIFViewHelper.processAction(inSetReopen)) {
						if (!inTask.rejectReopen()) {
							Notification.show(
									lMessages
											.getMessage("errmsg.pending.state.change"), Type.WARNING_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			});

			final Table lTable = createTable(inSetReopen,
					WorkflowAwareContribution.S_REOPEN_REQUESTED, inTask);
			lLayout.addComponent(lTable);

			final HorizontalLayout lButtons = RiplaViewHelper.createButtons(
					lReopen, lReject2);
			lLayout.addComponent(lButtons);
		}
	}

	@Override
	protected boolean isConfirmationMode() {
		return confirmationMode;
	}

}
