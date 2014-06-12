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

import java.util.Iterator;

import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.ContributionContainer;
import org.hip.vif.admin.groupadmin.tasks.AdminContributionsPublishablesTask;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View showing the private contributions a group administrator can publish.
 * 
 * @author Luthiger Created: 25.11.2011
 */
@SuppressWarnings("serial")
public class ContributionsListView extends AbstractContributionsProcessView {
	private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$

	private boolean confirmationMode;

	/**
	 * Constructor for view to display the table with contributions ready to be
	 * published.
	 * 
	 * @param inData
	 *            {@link ContributionContainer}
	 * @param inTitle
	 *            String
	 * @param inTask
	 *            {@link AdminContributionsPublishablesTask}
	 */
	public ContributionsListView(final ContributionContainer inData,
			final String inTitle,
			final AdminContributionsPublishablesTask inTask) {
		confirmationMode = false;
		final IMessages lMessages = Activator.getMessages();

		final VerticalLayout lLayout = initComponent(inTitle);

		final Button lProcess = new Button(
				lMessages.getMessage("ui.contributions.process.button.publish")); //$NON-NLS-1$
		lProcess.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				if (VIFViewHelper.processAction(inData)) {
					inTask.publishContributions();
				}
			}
		});

		final Button lDelete = new Button(
				lMessages.getMessage("ui.contributions.process.button.delete")); //$NON-NLS-1$

		final Label lSubtitle = new Label(
				lMessages.getMessage("ui.contributions.process.subtitle"), ContentMode.HTML); //$NON-NLS-1$
		lLayout.addComponent(lSubtitle);

		// table of pending contributions
		final Table lTable = createTable(inData,
				WorkflowAwareContribution.S_PRIVATE, inTask);
		lLayout.addComponent(lTable);

		// action buttons
		final HorizontalLayout lButtons = RiplaViewHelper.createButtons(
				lProcess, lDelete);
		lLayout.addComponent(lButtons);

		lDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				if (confirmationMode) {
					inTask.deleteContributions();
				} else {
					if (VIFViewHelper.processAction(inData)) {
						confirmationMode = true;
						inData.addContainerFilter(new SelectedFilter());
						lSubtitle
								.setPropertyDataSource(new ObjectProperty<String>(
										String.format(
												SUBTITLE_WARNING,
												lMessages
														.getMessage("ui.contributions.process.warning")), String.class)); //$NON-NLS-1$
						lTable.setSelectable(false);
						disableOtherButtons(inEvent.getButton(), lButtons);
					}
				}
			}
		});
	}

	private void disableOtherButtons(final Button inButton,
			final HorizontalLayout inButtons) {
		final Iterator<Component> lIterator = inButtons.iterator();
		while (lIterator.hasNext()) {
			final Component lComponent = lIterator.next();
			if (lComponent instanceof Button) {
				if (!inButton.equals(lComponent)) {
					lComponent.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected boolean isConfirmationMode() {
		return confirmationMode;
	}

	// ---

	private class SelectedFilter implements Filter {
		@SuppressWarnings("unchecked")
		@Override
		public boolean passesFilter(final Object inItemId, final Item inItem)
				throws UnsupportedOperationException {
			final Property<Boolean> lCheckBox = inItem
					.getItemProperty(ContributionContainer.CONTRIBUTION_CHECKED);
			return lCheckBox.getValue();
		}

		@Override
		public boolean appliesToProperty(final Object inPropertyId) {
			return ContributionContainer.CONTRIBUTION_CHECKED
					.equals(inPropertyId);
		}
	}

}
