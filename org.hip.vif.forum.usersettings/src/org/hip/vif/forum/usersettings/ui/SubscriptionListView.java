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

package org.hip.vif.forum.usersettings.ui;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.data.SubscriptionBean;
import org.hip.vif.forum.usersettings.data.SubscriptionContainer;
import org.hip.vif.forum.usersettings.tasks.SubscriptionsManageTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** Displays the list of personal subscriptions.
 *
 * @author Luthiger Created: 20.12.2011 */
@SuppressWarnings("serial")
public class SubscriptionListView extends AbstactUsersettingsView {
    private boolean confirmationMode;

    /** View constructor.
     *
     * @param inSubscriptions {@link SubscriptionContainer}
     * @param inTask {@link SubscriptionsManageTask} */
    public SubscriptionListView(final SubscriptionContainer inSubscriptions, final SubscriptionsManageTask inTask) {
        super();
        confirmationMode = false;
        final IMessages lMessages = Activator.getMessages();

        final Label lSubtitle = new Label(String.format(VIFViewHelper.TMPL_WARNING,
                lMessages.getMessage("ui.usersettings.delete.subscription.warning")), ContentMode.HTML); //$NON-NLS-1$
        final VerticalLayout lLayout = createLayout(lMessages, lSubtitle, "usersettings.menu.subscription"); //$NON-NLS-1$

        if (inSubscriptions.getItemIds().isEmpty()) {
            lLayout.addComponent(new Label(lMessages.getMessage("ui.usersettings.subscription.empty"))); //$NON-NLS-1$
            return;
        }

        final Table lTable = createTable(inTask);
        lTable.setContainerDataSource(inSubscriptions);

        lTable.addGeneratedColumn(SubscriptionContainer.ITEM_CHK, new VIFViewHelper.CheckBoxColumnGenerator(
                new VIFViewHelper.IConfirmationModeChecker() {
                    @Override
                    public boolean inConfirmationMode() { // NOPMD
                        return confirmationMode;
                    }
                }));
        lTable.addGeneratedColumn(SubscriptionContainer.ITEM_LOCAL, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(final Table inSource, final Object inItemId, final Object inColumnId) { // NOPMD
                return createCheck((SubscriptionBean) inItemId, inTask, lMessages);
            }
        });

        lTable.setPageLength(VIFViewHelper.getTablePageLength(inSubscriptions.size()));

        lTable.setVisibleColumns(SubscriptionContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(SubscriptionContainer.COL_HEADERS, lMessages));
        lLayout.addComponent(lTable);

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        final Button lDelete = new Button(lMessages.getMessage("ui.usersettings.button.delete")); //$NON-NLS-1$
        lDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) { // NOPMD
                if (confirmationMode) {
                    if (!inTask.deleteSubscriptions()) {
                        Notification.show(lMessages.getMessage("errmsg.subscription.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                else {
                    if (VIFViewHelper.processAction(inSubscriptions)) {
                        confirmationMode = true;
                        inSubscriptions.addContainerFilter(new SelectedFilter(SubscriptionContainer.ITEM_CHECKED));
                        lSubtitle.setVisible(true);
                        lTable.setSelectable(false);
                        lTable.setPageLength(0);
                    }
                }
            }
        });
        lLayout.addComponent(lDelete);
    }

    /** Creates a check box for the list entries to switch the range of the subscription.
     *
     * @param inEntry {@link SubscriptionBean}
     * @param inTask {@link SubscriptionsManageTask}
     * @param inMessages {@link IMessages}
     * @return {@link CheckBox} */
    public CheckBox createCheck(final SubscriptionBean inEntry, final SubscriptionsManageTask inTask,
            final IMessages inMessages) {
        final CheckBox out = new CheckBox();
        out.setImmediate(true);
        out.setValue(inEntry.getLocal());
        out.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent inEvent) { // NOPMD
                final boolean isLocal = ((CheckBox) inEvent.getProperty()).getValue();
                inEntry.setChecked(isLocal);
                try {
                    inTask.changeRange(inEntry, isLocal);
                    Notification.show(
                            inMessages.getMessage("msg.question.subscription.updated"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                }
                catch (final VException | SQLException exc) {
                    Notification.show(inMessages.getMessage("errmsg.subscription.change"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        return out;
    }

}
