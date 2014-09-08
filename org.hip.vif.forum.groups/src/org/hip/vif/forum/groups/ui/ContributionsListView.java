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

package org.hip.vif.forum.groups.ui;

import java.util.Iterator;

import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.tasks.ContributionsListTask;
import org.hip.vif.web.util.BeanWrapperHelper;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** View displaying the actor's contributions for that he can process them.
 *
 * @author Luthiger Created: 01.07.2011 */
@SuppressWarnings("serial")
public class ContributionsListView extends AbstractContributionsProcessView {
    private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$

    private HorizontalLayout buttons;
    private Table table;
    private Label subtitle;

    boolean confirmationMode;

    /** Constructor for a view to display an author's contributions for that he can either delete them or send a request
     * for review. In case of discussion groups without review (<code>inNeedsReview = false</code>), the contributions
     * can be published directly.
     *
     * @param inData {@link ContributionContainer}
     * @param inMember {@link Member}
     * @param inGroupTitle String the group's title
     * @param inNeedsReview boolean <code>true</code> if the group with the specified ID is a reviewed group
     * @param inTask {@link ContributionsListTask} for callback */
    public ContributionsListView(final ContributionContainer inData, final Member inMember, final String inGroupTitle,
            final boolean inNeedsReview,
            final ContributionsListTask inTask) {
        confirmationMode = false;
        final String lFirstname = BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inMember);
        final String lFamilyname = BeanWrapperHelper.getString(MemberHome.KEY_NAME, inMember);
        final IMessages lMessages = Activator.getMessages();

        final VerticalLayout lLayout = initComponent(lFirstname, lFamilyname, inGroupTitle, lMessages,
                "ui.contributions.process.title"); //$NON-NLS-1$

        if (inData.hasItems()) {
            Button lProcess;
            if (inNeedsReview) {
                lProcess = new Button(lMessages.getMessage("ui.contributions.process.button.request.review")); //$NON-NLS-1$
                lProcess.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent inEvent) {
                        if (VIFViewHelper.processAction(inData)) {
                            inTask.requestReview();
                        }
                    }
                });
            }
            else {
                lProcess = new Button(lMessages.getMessage("ui.contributions.process.button.publish")); //$NON-NLS-1$
                lProcess.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent inEvent) {
                        if (VIFViewHelper.processAction(inData)) {
                            inTask.publishContribution(false);
                        }
                    }
                });
            }

            final Button lDelete = new Button(lMessages.getMessage("ui.contributions.process.button.delete")); //$NON-NLS-1$
            lDelete.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (isConfirmationMode()) {
                        inTask.deleteContributions();
                    }
                    else {
                        if (VIFViewHelper.processAction(inData)) {
                            confirmationMode = true;
                            inData.addContainerFilter(new SelectedFilter());
                            subtitle.setPropertyDataSource(new ObjectProperty<String>(String.format(SUBTITLE_WARNING,
                                    lMessages.getMessage("ui.contributions.process.warning")), String.class)); //$NON-NLS-1$
                            table.setSelectable(false);
                            disableOtherButtons(inEvent.getButton());
                        }
                    }
                }
            });

            subtitle = new Label(lMessages.getMessage("ui.contributions.process.subtitle"), ContentMode.HTML); //$NON-NLS-1$
            lLayout.addComponent(subtitle);

            // table of pending contributions
            table = createTable(inData, WorkflowAwareContribution.S_PRIVATE, inTask);
            lLayout.addComponent(table);

            // action buttons
            buttons = RiplaViewHelper.createButtons(lProcess, lDelete);
            lLayout.addComponent(buttons);
        }
        else {
            lLayout.addComponent(new Label(lMessages.getFormattedMessage(
                    "ui.contributions.process.no.pending", lFirstname, lFamilyname))); //$NON-NLS-1$
        }
    }

    private void disableOtherButtons(final Button inButton) {
        final Iterator<Component> lIterator = buttons.iterator();
        while (lIterator.hasNext()) {
            final Component lComponent = lIterator.next();
            if (lComponent instanceof Button) {
                if (!inButton.equals(lComponent)) {
                    lComponent.setEnabled(false);
                }
            }
        }
    }

    private class SelectedFilter implements Filter {
        @Override
        public boolean passesFilter(final Object inItemId, final Item inItem) throws UnsupportedOperationException {
            final Property<?> lCheckBox = inItem.getItemProperty(ContributionContainer.CONTRIBUTION_CHECKED);
            return (Boolean) lCheckBox.getValue();
        }

        @Override
        public boolean appliesToProperty(final Object inPropertyId) {
            return ContributionContainer.CONTRIBUTION_CHECKED.equals(inPropertyId);
        }

    }

    @Override
    protected boolean isConfirmationMode() {
        return confirmationMode;
    }

    /** Checks whether this component is the origin of a value change event.
     *
     * @param inProperty {@link Property}
     * @return boolean <code>true</code> if the value change event originated on this component */
    public boolean checkSelectionSource(final Property<?> inProperty) {
        return table.equals(inProperty);
    }

}
