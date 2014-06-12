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

package org.hip.vif.admin.member.tasks;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.ui.MemberListView;
import org.hip.vif.admin.member.ui.MemberSearchView;
import org.hip.vif.admin.member.util.QueryHelper;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.search.NoHitsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Task/controller to display the list of members, either the full list or the result list after a member search. 
 * 
 * @author Luthiger
 * Created: 17.10.2011
 */
@SuppressWarnings("serial")
@Partlet
public class MemberShowListTask extends AbstractMemberSearchTask {
	static final Logger LOG = LoggerFactory.getLogger(MemberShowListTask.class);

	/* (non-Javadoc)
	 * @see org.hip.vif.web.tasks.AbstractVIFTask#runChecked()
	 */
	@Override
	protected Component runChecked() throws VException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_LIST);
			return new MemberListView(createMemberContainer(QueryHelper.getQueryStrategy(getParameters())), !isExternal(), this);
		}
		catch (NoHitsException exc) {
			showNotification(Activator.getMessages().getFormattedMessage("errmsg.search.no.hits", exc.getQueryString()), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			return new MemberSearchView(getHelpContent(), this);
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}
	
}
