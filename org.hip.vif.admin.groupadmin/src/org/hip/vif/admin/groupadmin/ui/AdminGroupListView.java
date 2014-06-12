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
import org.hip.vif.admin.groupadmin.data.GroupContainer;
import org.hip.vif.admin.groupadmin.tasks.AdminGroupShowListTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * View to display a list of discussion groups administered by a specified group
 * administrator.
 * 
 * @author Luthiger Created: 19.11.2011
 */
@SuppressWarnings("serial")
public class AdminGroupListView extends CustomComponent {
	private static final int TABLE_SIZE = 15;

	public AdminGroupListView(final GroupContainer inGroups,
			final AdminGroupShowListTask inTask) {
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-table"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getMessage("ui.discussion.list.view.title.page")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final Label lSubtitle = new Label(
				lMessages.getMessage("ui.discussion.list.view.title.sub"), ContentMode.HTML); //$NON-NLS-1$
		lLayout.addComponent(lSubtitle);

		final Table lTable = new Table();
		lTable.setWidth("100%"); //$NON-NLS-1$
		lTable.setContainerDataSource(inGroups);

		lTable.setPageLength(inGroups.size() > TABLE_SIZE ? TABLE_SIZE : 0);
		lTable.setColumnCollapsingAllowed(true);
		lTable.setColumnReorderingAllowed(true);
		lTable.setSelectable(true);
		lTable.setImmediate(true);
		lTable.addValueChangeListener(inTask);

		lTable.setVisibleColumns(GroupContainer.NATURAL_COL_ORDER);
		lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
				GroupContainer.COL_HEADERS, lMessages));
		lLayout.addComponent(lTable);
	}

}
