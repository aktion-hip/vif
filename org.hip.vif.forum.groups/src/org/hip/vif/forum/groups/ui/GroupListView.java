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
import org.hip.vif.forum.groups.data.GroupContainer;
import org.hip.vif.forum.groups.tasks.GroupShowListTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** UI component to display the table of groups.
 *
 * @author Luthiger Created: 22.05.2011 */
@SuppressWarnings("serial")
public class GroupListView extends CustomComponent {

    /** Constructor
     *
     * @param inGroups {@link GroupContainer} the groups to display in the table
     * @param inTask {@link GroupShowListTask} */
    public GroupListView(final GroupContainer inGroups, final GroupShowListTask inTask) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-table"); //$NON-NLS-1$
        lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
                "vif-title", lMessages.getMessage("ui.group.list.view.title.page")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        final Table lTable = new Table();
        lTable.setWidth("100%"); //$NON-NLS-1$
        lTable.setContainerDataSource(inGroups);

        lTable.setColumnCollapsingAllowed(true);
        lTable.setColumnReorderingAllowed(true);
        lTable.setSelectable(true);
        lTable.setImmediate(true);
        lTable.addValueChangeListener(inTask);

        lTable.setVisibleColumns(GroupContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(GroupContainer.COL_HEADERS, lMessages));
        lLayout.addComponent(lTable);
    }

}
