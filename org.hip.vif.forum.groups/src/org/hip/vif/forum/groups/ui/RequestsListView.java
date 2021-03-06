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

import java.util.Collection;
import java.util.Vector;

import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.tasks.RequestsListTask;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** View to display the reviewer's contributions his responsible for. The reviewer's tasks are: accept or review a review
 * request, publish or give back a contribution under revision.
 *
 * @author Luthiger Created: 10.07.2011 */
@SuppressWarnings("serial")
public class RequestsListView extends AbstractContributionsProcessView {
    private final Collection<Table> tables;

    /** Constructor for a view to display a reviewer's contributions for that he can either give back the review task or
     * publish the contributions.
     *
     * @param inDataForReview {@link ContributionContainer}
     * @param inDataUnderRevision {@link ContributionContainer}
     * @param inMember {@link Member}
     * @param inGroupTitle String
     * @param inTask {@link RequestsListTask} for callback */
    public RequestsListView(final ContributionContainer inDataForReview,
            final ContributionContainer inDataUnderRevision,
            final Member inMember, final String inGroupTitle, final RequestsListTask inTask) {
        final String lFirstname = BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inMember);
        final String lFamilyname = BeanWrapperHelper.getString(MemberHome.KEY_NAME, inMember);
        final IMessages lMessages = Activator.getMessages();
        tables = new Vector<Table>();

        final VerticalLayout lLayout = initComponent(lFirstname, lFamilyname, inGroupTitle, lMessages,
                "ui.contributions.review.title"); //$NON-NLS-1$

        // display subtitle if at least on of the data sets contains data
        if (inDataForReview.hasItems() || inDataUnderRevision.hasItems()) {
            lLayout.addComponent(new Label(
                    lMessages.getMessage("ui.contributions.process.subtitle"), ContentMode.HTML)); //$NON-NLS-1$
        }
        else {
            // display 'no pending requests' in none of the data sets contain data
            lLayout.addComponent(new Label(lMessages.getFormattedMessage(
                    "ui.contributions.review.no.pending", lFirstname, lFamilyname))); //$NON-NLS-1$
        }

        if (inDataForReview.hasItems()) {
            final Button lAccept = new Button(lMessages.getMessage("ui.contributions.review.button.accept")); //$NON-NLS-1$
            lAccept.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(inDataForReview)) {
                        inTask.acceptRequest();
                    }
                }
            });
            final Button lRefuse = new Button(lMessages.getMessage("ui.contributions.review.button.refuse")); //$NON-NLS-1$
            lRefuse.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(inDataForReview)) {
                        inTask.refuseRequest();
                    }
                }
            });

            // table of contributions 'waiting for review'
            final Table table1 = createTable(inDataForReview, WorkflowAwareContribution.S_WAITING_FOR_REVIEW, inTask);
            lLayout.addComponent(table1);
            tables.add(table1);

            // action buttons
            lLayout.addComponent(RiplaViewHelper.createButtons(lAccept, lRefuse));

            lLayout.addComponent(new Label("&#160;", ContentMode.HTML)); //$NON-NLS-1$
        }

        if (inDataUnderRevision.hasItems()) {
            final Button lPublish = new Button(lMessages.getMessage("ui.contributions.review.button.publish")); //$NON-NLS-1$
            lPublish.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(inDataUnderRevision)) {
                        inTask.publishContribution(true);
                    }
                }
            });
            final Button lGiveBack = new Button(lMessages.getMessage("ui.contributions.review.button.return")); //$NON-NLS-1$
            lGiveBack.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (VIFViewHelper.processAction(inDataUnderRevision)) {
                        inTask.giveBack();
                    }
                }
            });

            // table of contributions 'under revision'
            final Table table2 = createTable(inDataUnderRevision, WorkflowAwareContribution.S_UNDER_REVISION, inTask);
            lLayout.addComponent(table2);
            tables.add(table2);

            // action buttons
            lLayout.addComponent(RiplaViewHelper.createButtons(lPublish, lGiveBack));
        }
    }

    @Override
    protected boolean isConfirmationMode() {
        return false;
    }

    /** Checks whether this component is the origin of a value change event.
     *
     * @param inProperty {@link Property}
     * @return boolean */
    public boolean checkSelectionSource(final Property<?> inProperty) {
        return tables.contains(inProperty);
    }

}
