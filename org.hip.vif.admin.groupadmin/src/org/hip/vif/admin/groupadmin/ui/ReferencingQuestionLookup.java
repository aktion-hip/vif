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
import org.hip.vif.admin.groupadmin.data.ContributionWrapper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Displays the table of questions referencing a bibliography entry in a lookup
 * window.
 * 
 * @author Luthiger Created: 10.12.2011
 */
@SuppressWarnings("serial")
public class ReferencingQuestionLookup extends CustomComponent {
	private static final Object[] NATURAL_COL_ORDER = new String[] {
			"publicID", ContributionContainer.CONTRIBUTION_TEXT, "contributionState" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String[] COL_HEADERS = new String[] {
			"container.table.headers.nr", "container.table.headers.question", "container.table.headers.state" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private final Window lookup;

	/**
	 * Constructor
	 * 
	 * @param inQuestions
	 *            ContributionContainer
	 * @param inWidth
	 *            int
	 * @param inHeight
	 *            int
	 */
	public ReferencingQuestionLookup(final ContributionContainer inQuestions,
			final int inWidth, final int inHeight) {
		final IMessages lMessages = Activator.getMessages();
		lookup = new Window(
				lMessages.getMessage("lookup.window.title.referencing")); //$NON-NLS-1$
		lookup.addStyleName("vif-lookup"); //$NON-NLS-1$
		lookup.setWidth(inWidth, Unit.PIXELS);
		lookup.setHeight(inHeight, Unit.PIXELS);

		final VerticalLayout lLayout = (VerticalLayout) lookup.getContent();
		lLayout.setStyleName("vif-lookup"); //$NON-NLS-1$
		lLayout.setMargin(true);
		lLayout.setSpacing(true);

		lLayout.addComponent(createTable(inQuestions));

		final Button lClose = new Button(
				lMessages.getMessage("lookup.window.button.close"), new Button.ClickListener() { //$NON-NLS-1$
					@Override
					public void buttonClick(final ClickEvent inEvent) {
						lookup.close();
					}
				});
		lClose.setClickShortcut(KeyCode.ESCAPE);
		lClose.setImmediate(true);
		lClose.setStyleName("vif-lookup-close"); //$NON-NLS-1$
		lLayout.addComponent(lClose);
	}

	private Table createTable(final ContributionContainer inData) {
		final Table outTable = new Table();
		outTable.setStyleName("vif-table"); //$NON-NLS-1$
		outTable.setWidth("100%"); //$NON-NLS-1$

		outTable.setContainerDataSource(inData);

		// generate label component for html text
		outTable.addGeneratedColumn(ContributionContainer.CONTRIBUTION_TEXT,
				new Table.ColumnGenerator() {
					@Override
					public Component generateCell(final Table inSource,
							final Object inItemId, final Object inColumnId) {
						return new Label(((ContributionWrapper) inItemId)
								.getContributionText(), ContentMode.HTML);
					}
				});

		outTable.setColumnCollapsingAllowed(true);
		outTable.setColumnReorderingAllowed(true);
		outTable.setPageLength(0);
		outTable.setColumnExpandRatio(ContributionContainer.CONTRIBUTION_TEXT,
				1);
		outTable.setVisibleColumns(NATURAL_COL_ORDER);
		outTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(COL_HEADERS,
				Activator.getMessages()));

		return outTable;
	}

	public Window getLookupWindow() {
		return lookup;
	}

}
