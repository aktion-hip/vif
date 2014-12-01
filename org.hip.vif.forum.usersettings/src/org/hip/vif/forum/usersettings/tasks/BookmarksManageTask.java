/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.forum.usersettings.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.BookmarkHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.data.BookmarkBean;
import org.hip.vif.forum.usersettings.data.BookmarkContainer;
import org.hip.vif.forum.usersettings.ui.BookmarkListView;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/** Task to show the list of bookmarks.
 *
 * @author Luthiger Created: 19.12.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class BookmarksManageTask extends AbstractWebController implements Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(BookmarksManageTask.class);

    private BookmarkContainer bookmarks;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_MANAGE_BOOKMARKS;
    }

    @Override
    protected Component runChecked() throws VIFWebException {
        try {
            emptyContextMenu();

            final BookmarkHome lBookmarkHome = BOMHelper.getBookmarkHome();
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(BookmarkHome.KEY_MEMBERID, getActor().getActorID());
            bookmarks = BookmarkContainer.createData(lBookmarkHome.select(lKey));
            return new BookmarkListView(bookmarks, this);
        } catch (final SQLException | VException exc) {
            throw createContactAdminException(exc);
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        try {
            final Object lEntry = inEvent.getProperty().getValue();
            if (lEntry instanceof BookmarkBean) {
                final BookmarkBean lBookmark = (BookmarkBean) lEntry;
                setQuestionID(lBookmark.getQuestionID());
                setGroupID(BeanWrapperHelper.getLong(QuestionHome.KEY_GROUP_ID, BOMHelper.getQuestionHome()
                        .getQuestion(lBookmark.getQuestionID())));
                sendAliasEvent(ForwardControllerRegistry.Alias.FORWARD_QUESTION_SHOW);
            }
        } catch (final VException exc) {
            LOG.error("An error encountered while looking up the bookmarked question!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("An error encountered while looking up the bookmarked question!", exc); //$NON-NLS-1$
        }
    }

    /** Callback method to delete the selected bookmarks.
     *
     * @return boolean <code>true</code> if successful */
    public boolean deleteBookmarks() {
        try {
            int i = 0;
            final BookmarkHome lHome = BOMHelper.getBookmarkHome();
            for (final BookmarkBean lBookmark : bookmarks.getItemIds()) {
                if (lBookmark.isChecked()) {
                    lHome.delete(lBookmark.getQuestionID(), getActor().getActorID());
                    i++;
                }
            }
            final IMessages lMessages = Activator.getMessages();
            showNotification(i == 1 ? lMessages.getMessage("msg.question.bookmark.deleted") : lMessages.getMessage("msg.question.bookmark.deletedP")); //$NON-NLS-1$ //$NON-NLS-2$
            sendEvent(BookmarksManageTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("An error encountered while deleting the bookmark!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("An error encountered while deleting the bookmark!", exc); //$NON-NLS-1$
        }
        return false;
    }

}
