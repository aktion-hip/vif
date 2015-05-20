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

import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** Base class for views displaying the list of contributions pending in the workflow.
 *
 * @author Luthiger Created: 10.07.2011 */
@SuppressWarnings("serial")
public abstract class AbstractContributionsProcessView extends CustomComponent { // NOPMD

    /** @param inFirstname String
     * @param inFamilyname String
     * @param inGroupTitle String
     * @param inMessages {@link IMessages}
     * @param inTitleKey String
     * @return {@link VerticalLayout} */
    protected VerticalLayout initComponent(final String inFirstname, final String inFamilyname, // NOPMD
            final String inGroupTitle, final IMessages inMessages, final String inTitleKey) {
        final VerticalLayout outLayout = new VerticalLayout();
        setCompositionRoot(outLayout);

        outLayout.setStyleName("vif-view"); //$NON-NLS-1$

        final String lTitle = String.format(inMessages.getFormattedMessage(inTitleKey, inFirstname, inFamilyname,
                inGroupTitle)); //$NON-NLS-1$
        outLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$
        return outLayout;
    }

    /** Table | chk : Nr. : Question : Status |
     *
     * @param inData {@link ContributionContainer}
     * @param inCheckedState int the contribution's workflow state (e.g.
     *            <code>WorkflowAwareContribution.S_PRIVATE</code>)
     * @param inListener {@link ValueChangeListener}
     * @return Table */
    protected Table createTable(final ContributionContainer inData, final int inCheckedState,
            final ValueChangeListener inListener) {
        final Table outTable = new Table();
        outTable.setStyleName("vif-table"); //$NON-NLS-1$
        outTable.setWidth("100%"); //$NON-NLS-1$

        outTable.setContainerDataSource(inData);
        // generate check box
        outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_CHECK, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(final Table inSource, final Object inItemId, final Object inColumnId) { // NOPMD
                final ContributionWrapper lContribution = (ContributionWrapper) inItemId;
                // the check box is generated only if the contributions state is e.g. 'private' or 'waiting for review'
                return lContribution.getState() == inCheckedState ? VIFViewHelper.createCheck(lContribution,
                        new VIFViewHelper.IConfirmationModeChecker() {
                    @Override
                    public boolean inConfirmationMode() { // NOPMD
                        return isConfirmationMode();
                    }
                }) : new Label();
            }
        });
        // generate label component for html text
        outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_TEXT, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(final Table inSource, final Object inItemId, final Object inColumnId) { // NOPMD
                final Label out = new Label(((ContributionWrapper) inItemId).getContributionText(), ContentMode.HTML);
                out.setStyleName("vif-cell");
                return out;
            }
        });

        outTable.setColumnCollapsingAllowed(true);
        outTable.setColumnReorderingAllowed(true);
        outTable.setSelectable(true);
        outTable.setImmediate(true);
        outTable.setPageLength(0);
        outTable.setColumnExpandRatio(ContributionContainer.CONTRIBUTION_TEXT, 1);
        outTable.addValueChangeListener(inListener);

        outTable.setVisibleColumns(ContributionContainer.NATURAL_COL_ORDER);
        outTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(ContributionContainer.COL_HEADERS,
                Activator.getMessages()));

        return outTable;
    }

    /** @return boolean */
    abstract protected boolean isConfirmationMode();

}
