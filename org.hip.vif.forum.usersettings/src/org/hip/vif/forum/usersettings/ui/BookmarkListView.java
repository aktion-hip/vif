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

import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.data.BookmarkContainer;
import org.hip.vif.forum.usersettings.tasks.BookmarksManageTask;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/** Displays the list of personal bookmarks.
 *
 * @author Luthiger Created: 20.12.2011 */
@SuppressWarnings("serial")
public class BookmarkListView extends AbstactUsersettingsView {
    private boolean confirmationMode;

    /** View constructor
     *
     * @param inBookmarks {@link BookmarkContainer}
     * @param inTask {@link BookmarksManageTask} */
    public BookmarkListView(final BookmarkContainer inBookmarks, final BookmarksManageTask inTask) {
        super();

        confirmationMode = false;
        final IMessages lMessages = Activator.getMessages();
        final Label lSubtitle = new Label(String.format(VIFViewHelper.TMPL_WARNING,
                lMessages.getMessage("ui.usersettings.delete.bookmark.warning")), ContentMode.HTML); //$NON-NLS-1$
        final VerticalLayout lLayout = createLayout(lMessages, lSubtitle, "usersettings.menu.bookmarks"); //$NON-NLS-1$

        if (inBookmarks.getItemIds().isEmpty()) {
            lLayout.addComponent(new Label(lMessages.getMessage("ui.usersettings.bookmark.empty"))); //$NON-NLS-1$
            return;
        }

        final Table lTable = createTable(inTask);
        lTable.setContainerDataSource(inBookmarks);
        lTable.addGeneratedColumn(BookmarkContainer.ITEM_CHK, new VIFViewHelper.CheckBoxColumnGenerator(
                new VIFViewHelper.IConfirmationModeChecker() {
                    @Override
                    public boolean inConfirmationMode() { // NOPMD
                        return confirmationMode;
                    }
                }));
        lTable.setPageLength(VIFViewHelper.getTablePageLength(inBookmarks.size()));
        lTable.setVisibleColumns(BookmarkContainer.NATURAL_COL_ORDER);
        lTable.setColumnHeaders(VIFViewHelper.getColumnHeaders(BookmarkContainer.COL_HEADERS, lMessages));
        lLayout.addComponent(lTable);

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        final Button lDelete = new Button(lMessages.getMessage("ui.usersettings.button.delete")); //$NON-NLS-1$
        lDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) { // NOPMD
                if (confirmationMode) {
                    if (!inTask.deleteBookmarks()) {
                        Notification.show(lMessages.getMessage("errmsg.bookmark.delete"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    }
                }
                else {
                    if (VIFViewHelper.processAction(inBookmarks)) {
                        confirmationMode = true;
                        inBookmarks.addContainerFilter(new SelectedFilter(BookmarkContainer.ITEM_CHECKED));
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
