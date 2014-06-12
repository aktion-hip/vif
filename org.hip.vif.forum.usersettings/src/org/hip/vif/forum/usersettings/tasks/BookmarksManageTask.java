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
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.BookmarkHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.data.BookmarkBean;
import org.hip.vif.forum.usersettings.data.BookmarkContainer;
import org.hip.vif.forum.usersettings.ui.BookmarkListView;
import org.hip.vif.web.tasks.AbstractVIFTask;
import org.hip.vif.web.tasks.ForwardTaskRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * Task to show the list of bookmarks.
 * 
 * @author Luthiger
 * Created: 19.12.2011
 */
@SuppressWarnings("serial")
@Partlet
public class BookmarksManageTask extends AbstractVIFTask implements Property.ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(BookmarksManageTask.class);
	
	private BookmarkContainer bookmarks;

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_MANAGE_BOOKMARKS;
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		try {
			emptyContextMenu();
			
			BookmarkHome lBookmarkHome = BOMHelper.getBookmarkHome();
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(BookmarkHome.KEY_MEMBERID, getActor().getActorID());
			bookmarks = BookmarkContainer.createData(lBookmarkHome.select(lKey));
			return new BookmarkListView(bookmarks, this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}

	public void valueChange(ValueChangeEvent inEvent) {
		try {
			Object lEntry = inEvent.getProperty().getValue();
			if (lEntry instanceof BookmarkBean) {
				BookmarkBean lBookmark = (BookmarkBean) lEntry;
				setQuestionID(lBookmark.getQuestionID());
				setGroupID(BeanWrapperHelper.getLong(QuestionHome.KEY_GROUP_ID, BOMHelper.getQuestionHome().getQuestion(lBookmark.getQuestionID())));
				sendEvent(ForwardTaskRegistry.ForwardQuestionShow.class);
			}
		}
		catch (VException exc) {
			LOG.error("An error encountered while looking up the bookmarked question!", exc); //$NON-NLS-1$
		}
		catch (SQLException exc) {
			LOG.error("An error encountered while looking up the bookmarked question!", exc); //$NON-NLS-1$
		}
	}

	/**
	 * Callback method to delete the selected bookmarks.
	 * 
	 * @return boolean <code>true</code> if successful
	 */
	public boolean deleteBookmarks() {
		try {
			int i = 0;
			BookmarkHome lHome = BOMHelper.getBookmarkHome();
			for (BookmarkBean lBookmark : bookmarks.getItemIds()) {
				if (lBookmark.isChecked()) {
					lHome.delete(lBookmark.getQuestionID(), getActor().getActorID());
					i++;
				}
			}
			IMessages lMessages = Activator.getMessages();
			showNotification(i == 1 ? lMessages.getMessage("msg.question.bookmark.deleted") : lMessages.getMessage("msg.question.bookmark.deletedP")); //$NON-NLS-1$ //$NON-NLS-2$
			sendEvent(BookmarksManageTask.class);
			return true;
		}
		catch (VException exc) {
			LOG.error("An error encountered while deleting the bookmark!", exc); //$NON-NLS-1$
		}
		catch (SQLException exc) {
			LOG.error("An error encountered while deleting the bookmark!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
