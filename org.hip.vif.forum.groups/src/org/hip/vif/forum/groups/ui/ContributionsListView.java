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

package org.hip.vif.forum.groups.ui;

import java.util.Iterator;

import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.tasks.ContributionsListTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View displaying the actor's contributions for that he can process them.
 * 
 * @author Luthiger
 * Created: 01.07.2011
 */
@SuppressWarnings("serial")
public class ContributionsListView extends AbstractContributionsProcessView {
	private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$
	
	private HorizontalLayout buttons;
	private Table table;
	private Label subtitle;
	
	boolean confirmationMode;
	
	/**
	 * Constructor for a view to display an author's contributions for that he can either delete them or send a request for review.
	 * In case of discussion groups without review (<code>inNeedsReview = false</code>), the contributions can be published directly.
	 * 
	 * @param inData {@link ContributionContainer}
	 * @param inMember {@link Member}
	 * @param inGroupTitle String the group's title
	 * @param inNeedsReview boolean <code>true</code> if the group with the specified ID is a reviewed group
	 * @param inTask {@link ContributionsListTask} for callback
	 */
	public ContributionsListView(final ContributionContainer inData, Member inMember, String inGroupTitle, boolean inNeedsReview, 
			final ContributionsListTask inTask) {
		confirmationMode = false;
		String lFirstname = BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inMember);
		String lFamilyname = BeanWrapperHelper.getString(MemberHome.KEY_NAME, inMember);
		final IMessages lMessages = Activator.getMessages();
		
		VerticalLayout lLayout = initComponent(lFirstname, lFamilyname, inGroupTitle, lMessages, "ui.contributions.process.title"); //$NON-NLS-1$
		
		if (inData.hasItems()) {
			Button lProcess;
			if (inNeedsReview) {
				lProcess = new Button(lMessages.getMessage("ui.contributions.process.button.request.review")); //$NON-NLS-1$
				lProcess.addListener(new ClickListener() {
					public void buttonClick(ClickEvent inEvent) {
						if (VIFViewHelper.processAction(inData, getWindow())) {
							inTask.requestReview();
						}
					}
				});
			}
			else {
				lProcess = new Button(lMessages.getMessage("ui.contributions.process.button.publish")); //$NON-NLS-1$
				lProcess.addListener(new ClickListener() {
					public void buttonClick(ClickEvent inEvent) {
						if (VIFViewHelper.processAction(inData, getWindow())) {
							inTask.publishContribution(false);
						}
					}
				});
			}
			
			Button lDelete = new Button(lMessages.getMessage("ui.contributions.process.button.delete")); //$NON-NLS-1$
			lDelete.addListener(new ClickListener() {
				public void buttonClick(ClickEvent inEvent) {
					if (isConfirmationMode()) {
						inTask.deleteContributions();
					}
					else {
						if (VIFViewHelper.processAction(inData, getWindow())) {
							confirmationMode = true;
							inData.addContainerFilter(new SelectedFilter());
							subtitle.setPropertyDataSource(new ObjectProperty<String>(String.format(SUBTITLE_WARNING, lMessages.getMessage("ui.contributions.process.warning")), String.class)); //$NON-NLS-1$
							table.setSelectable(false);
							disableOtherButtons(inEvent.getButton());
						}		
					}
				}
			});
			
			subtitle = new Label(lMessages.getMessage("ui.contributions.process.subtitle"), Label.CONTENT_XHTML); //$NON-NLS-1$
			lLayout.addComponent(subtitle);
			
			//table of pending contributions
			table = createTable(inData, WorkflowAwareContribution.S_PRIVATE, inTask);
			lLayout.addComponent(table);
			
			//action buttons
			buttons = VIFViewHelper.createButtons(lProcess, lDelete);
			lLayout.addComponent(buttons);
		}
		else {
			lLayout.addComponent(new Label(lMessages.getFormattedMessage("ui.contributions.process.no.pending", lFirstname, lFamilyname))); //$NON-NLS-1$
		}
	}

	private void disableOtherButtons(Button inButton) {
		Iterator<Component> lIterator = buttons.getComponentIterator();
		while (lIterator.hasNext()) {
			Component lComponent = lIterator.next();
			if (lComponent instanceof Button) {
				if (!inButton.equals(lComponent)) {
					lComponent.setEnabled(false);
				}
			}
		}
	}
	
	private class SelectedFilter implements Filter {
		public boolean passesFilter(Object inItemId, Item inItem) throws UnsupportedOperationException {
			Property lCheckBox = inItem.getItemProperty(ContributionContainer.CONTRIBUTION_CHECKED);
			return (Boolean) lCheckBox.getValue();
		}

		public boolean appliesToProperty(Object inPropertyId) {
			return ContributionContainer.CONTRIBUTION_CHECKED.equals(inPropertyId);
		}
		
	}

	@Override
	protected boolean isConfirmationMode() {
		return confirmationMode;
	}

	/**
	 * Checks whether this component is the origin of a value change event.
	 * 
	 * @param inProperty {@link Property}
	 * @return boolean <code>true</code> if the value change event originated on this component
	 */
	public boolean checkSelectionSource(Property inProperty) {
		return table.equals(inProperty);
	}

}
