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

package org.hip.vif.admin.groupedit.ui;

import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.data.GroupContainer;
import org.hip.vif.admin.groupedit.data.GroupWrapper;
import org.hip.vif.admin.groupedit.tasks.GroupShowListTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** View to display a list of discussion groups.
 *
 * @author Luthiger Created: 06.11.2011 */
@SuppressWarnings("serial")
public class GroupListView extends CustomComponent {
    private static final int TABLE_SIZE = 14;

    private boolean confirmationMode;

    /** Constructor
     *
     * @param inGroups {@link GroupContainer} the groups to display
     * @param inTask {@link GroupShowTask} the controller */
    public GroupListView(final GroupContainer inGroups, final GroupShowListTask inTask) {
        confirmationMode = false;
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-table"); //$NON-NLS-1$
        lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE,
                "vif-pagetitle", lMessages.getMessage("ui.group.list.view.title.page")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

        final Label lSubtitle = new Label(String.format(VIFViewHelper.TMPL_WARNING,
                lMessages.getMessage("ui.group.delete.warning")), ContentMode.HTML); //$NON-NLS-1$
        lSubtitle.setVisible(false);
        lLayout.addComponent(lSubtitle);

        final Table lTable = new Table();
        lTable.setWidth("100%"); //$NON-NLS-1$
        lTable.setContainerDataSource(inGroups);
        lTable.addGeneratedColumn(GroupContainer.GROUP_CHECK, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(final Table inSource, final Object inItemId, final Object inColumnId) {
                return createCheck((GroupWrapper) inItemId);
            }
        });

        lTable.setPageLength(inGroups.size() > TABLE_SIZE ? TABLE_SIZE : 0);
        lTable.setColumnCollapsingAllowed(true);
        lTable.setColumnReorderingAllowed(true);
        lTable.setSelectable(true);
        lTable.setImmediate(true);
        lTable.addValueChangeListener(inTask);

        lTable.setVisibleColumns(GroupContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(GroupContainer.COL_HEADERS, lMessages));
        lLayout.addComponent(lTable);

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        final Button lDelete = new Button(lMessages.getMessage("ui.group.editor.button.delete")); //$NON-NLS-1$
        lDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                if (confirmationMode) {
                    if (!inTask.deleteGroups()) {
                        Notification.show(lMessages.getMessage("errmsg.process.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                else {
                    if (VIFViewHelper.processAction(inGroups)) {
                        confirmationMode = true;
                        inGroups.addContainerFilter(new SelectedFilter());
                        lSubtitle.setVisible(true);
                        lTable.setSelectable(false);
                        lTable.setPageLength(0);
                    }
                }
            }
        });
        lLayout.addComponent(lDelete);
    }

    private CheckBox createCheck(final GroupWrapper inEntry) {
        final CheckBox out = new CheckBox();
        out.setImmediate(true);
        out.setValue(inEntry.isChecked());
        if (inEntry.getIsDeletable()) {
            out.setEnabled(!confirmationMode);
        }
        else {
            out.setEnabled(false);
        }
        out.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                inEntry.setChecked(((CheckBox) inEvent.getProperty()).getValue());
            }
        });
        return out;
    }

    // --- private classes ---

    private static class SelectedFilter implements Filter {
        @Override
        public boolean passesFilter(final Object inItemId, final Item inItem) throws UnsupportedOperationException {
            final Property<?> lCheckBox = inItem.getItemProperty(GroupContainer.GROUP_CHECKED);
            return (Boolean) lCheckBox.getValue();
        }

        @Override
        public boolean appliesToProperty(final Object inPropertyId) {
            return GroupContainer.GROUP_CHECKED.equals(inPropertyId);
        }
    }

}
