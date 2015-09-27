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
import org.hip.vif.admin.groupedit.data.ParticipantBean;
import org.hip.vif.admin.groupedit.data.ParticipantContainer;
import org.hip.vif.admin.groupedit.tasks.ParticipantListTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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

/** View to display the group's list of participants.
 *
 * @author Luthiger Created: 18.11.2011 */
@SuppressWarnings("serial")
public class ParticipantListView extends CustomComponent {
    private static final String SUBTITLE_WARNING = "<div class=\"vif-warning\">%s</div>"; //$NON-NLS-1$
    private static final int TABLE_SIZE = 18;

    private boolean confirmationMode;

    /** View constructor.
     *
     * @param inParticipants {@link ParticipantContainer}
     * @param inGroupName String
     * @param inEnableDelete boolean
     * @param inTask {@link ParticipantListTask} */
    public ParticipantListView(final ParticipantContainer inParticipants, final String inGroupName,
            final boolean inEnableDelete,
            final ParticipantListTask inTask) {
        confirmationMode = false;
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        final IMessages lMessages = Activator.getMessages();
        lLayout.setStyleName("vif-table"); //$NON-NLS-1$
        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-pagetitle", //$NON-NLS-1$
                        lMessages.getFormattedMessage("ui.group.participants.view.title.page", inGroupName)), //$NON-NLS-1$
                ContentMode.HTML));

        final Label lSubtitle = new Label(String.format(SUBTITLE_WARNING,
                lMessages.getMessage("ui.participants.delete.warning")), ContentMode.HTML); //$NON-NLS-1$
        lSubtitle.setVisible(false);
        lLayout.addComponent(lSubtitle);

        final Table lTable = new Table();
        lTable.setWidth("100%"); //$NON-NLS-1$
        lTable.setContainerDataSource(inParticipants);
        if (inEnableDelete) {
            lTable.addGeneratedColumn(ParticipantContainer.ENTRY_CHECK, new Table.ColumnGenerator() {
                @Override
                public Object generateCell(final Table inSource, final Object inItemId, final Object inColumnId) {
                    return createCheck((ParticipantBean) inItemId, new VIFViewHelper.IConfirmationModeChecker() {
                        @Override
                        public boolean inConfirmationMode() {
                            return confirmationMode;
                        }
                    });
                }
            });
            lTable.setVisibleColumns(VIFViewHelper.getModifiedArray(ParticipantContainer.ENTRY_CHECK,
                    ParticipantContainer.NATURAL_COL_ORDER));
            lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(
                    VIFViewHelper.getModifiedArray("", (Object[]) ParticipantContainer.COL_HEADERS), lMessages)); //$NON-NLS-1$
        } else {
            lTable.setVisibleColumns(ParticipantContainer.NATURAL_COL_ORDER);
            lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(ParticipantContainer.COL_HEADERS, lMessages));
        }
        lTable.setPageLength(inParticipants.size() > TABLE_SIZE ? TABLE_SIZE : 0);
        lTable.setColumnCollapsingAllowed(true);
        lTable.setColumnReorderingAllowed(true);
        lTable.setSelectable(true);
        lTable.setImmediate(true);
        lTable.setColumnWidth(ParticipantContainer.PARTICIPANT_PLACE, 200);
        lTable.setColumnWidth(ParticipantContainer.PARTICIPANT_MAIL, 200);
        lTable.addValueChangeListener(inTask);

        lLayout.addComponent(lTable);

        if (inEnableDelete) {
            lLayout.addComponent(RiplaViewHelper.createSpacer());
            final Button lDelete = new Button(lMessages.getMessage("ui.participants.button.delete")); //$NON-NLS-1$
            lDelete.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final ClickEvent inEvent) {
                    if (confirmationMode) {
                        if (!inTask.deleteParticipants()) {
                            Notification.show(lMessages.getMessage("errmsg.process.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                        }
                    } else {
                        if (VIFViewHelper.processAction(inParticipants)) {
                            confirmationMode = true;
                            inParticipants.addContainerFilter(new SelectedFilter());
                            lSubtitle.setVisible(true);
                            lTable.setSelectable(false);
                            lTable.setPageLength(0);
                        }
                    }
                }
            });
            lLayout.addComponent(lDelete);
        }
    }

    private CheckBox createCheck(final ParticipantBean inEntry,
            final VIFViewHelper.IConfirmationModeChecker inChecker) {
        final CheckBox out = new CheckBox();
        out.setImmediate(true);
        out.setValue(inEntry.isChecked());
        if (inEntry.isAdmin()) {
            out.setEnabled(false);
        } else {
            out.setEnabled(!inChecker.inConfirmationMode());
        }
        out.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) {
                inEntry.setChecked(((CheckBox) inEvent.getProperty()).getValue());
            }
        });
        return out;
    }

    // --- private classes ---

    private static class SelectedFilter implements Container.Filter {
        @Override
        public boolean passesFilter(final Object inItemId, final Item inItem) throws UnsupportedOperationException {
            final Property<?> lCheckBox = inItem.getItemProperty(ParticipantContainer.ENTRY_CHECKED);
            return (Boolean) lCheckBox.getValue();
        }

        @Override
        public boolean appliesToProperty(final Object inPropertyId) {
            return ParticipantContainer.ENTRY_CHECKED.equals(inPropertyId);
        }
    }

}
