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
import org.hip.vif.admin.member.Constants;
import org.hip.vif.admin.member.data.MemberBeanContainer;
import org.hip.vif.admin.member.ui.MemberSearchView;
import org.hip.vif.admin.member.ui.SelectMemberLookup;
import org.hip.vif.admin.member.util.QueryHelper;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMemberQueryStrategy;
import org.hip.vif.core.member.IMemberSearcher;
import org.hip.vif.core.service.MemberUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Controller for the view to display either the list of members or the form to search for members in a popup window.
 * This task is used to search for members to select them as administrators for a discussion group.
 * 
 * @author Luthiger
 * Created: 14.11.2011
 */
@SuppressWarnings("serial")
@Partlet
public class LookupSearchTask extends AbstrachtMemberLookupTask {
	static final Logger LOG = LoggerFactory.getLogger(LookupSearchTask.class);
	
	@Override
	protected Component runChecked() throws VException {	
		IMemberSearcher lSearcher = MemberUtility.INSTANCE.getActiveMemberSearcher();
		try {
			prepareParameters();
			if (lSearcher.canShowAll() && BOMHelper.getMemberHome().getCount() <= Constants.FULL_LIST_THRESHOLD) {
				IMemberQueryStrategy lStrategy = QueryHelper.getFullDBTableStrategy();
				return new SelectMemberLookup(getLookupSubtitle(), getLookupRightColumnTitle(),
						MemberBeanContainer.createData(lStrategy.getQueryResult(createOrder(MemberHome.KEY_NAME, false))), 
						getAssignedMembers(), this);
			}			
			return new MemberSearchView(getLookupTitle(), this);
		} 
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}
	
}
