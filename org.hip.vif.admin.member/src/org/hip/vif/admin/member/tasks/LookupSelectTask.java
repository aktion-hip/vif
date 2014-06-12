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

import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Activator;
import org.hip.vif.admin.member.data.MemberBeanContainer;
import org.hip.vif.admin.member.ui.MemberSearchView;
import org.hip.vif.admin.member.ui.SelectMemberLookup;
import org.hip.vif.admin.member.util.QueryHelper;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMemberQueryStrategy;
import org.hip.vif.core.member.MemberBean;
import org.hip.vif.core.search.NoHitsException;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

/**
 * Controller for views that display the result of a member search in a lookup window.<br />
 * This task's purpose is to select members, e.g. as group administrators.
 * 
 * @author Luthiger
 * Created: 17.11.2011
 */
@SuppressWarnings("serial")
@Partlet
public class LookupSelectTask extends AbstrachtMemberLookupTask {

	@Override
	protected Component runChecked() throws VException {
		try {
			IMemberQueryStrategy lStrategy = QueryHelper.getQueryStrategy(getParameters(false));
			prepareParameters();
			Collection<MemberBean> lAssignedMembers = getAssignedMembers();
			MemberBeanContainer lSearchResult = MemberBeanContainer.createData(lStrategy.getQueryResult(createOrder(MemberHome.KEY_NAME, false)));
			lSearchResult.addSelected(lAssignedMembers);
			return new SelectMemberLookup(getLookupSubtitle(), getLookupRightColumnTitle(), lSearchResult,  lAssignedMembers, this);
		}
		catch (NoHitsException exc) {
			//display the search form again
			prepareParameters();
			showNotification(Activator.getMessages().getFormattedMessage("errmsg.search.no.hits", exc.getQueryString()), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			return new MemberSearchView(getLookupTitle(), this);
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}

}
