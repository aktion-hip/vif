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
package org.hip.vif.admin.groupadmin.tasks;

import java.sql.SQLException;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.GroupContainer;
import org.hip.vif.admin.groupadmin.data.GroupWrapper;
import org.hip.vif.admin.groupadmin.ui.AdminGroupListView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.impl.JoinGroupAdminToGroupHome;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/**
 * Show the list of all discussion groups for that they can be administered,
 * i.e. for that the starting questions can be added by group administrators.
 * 
 * @author Luthiger Created: 19.11.2011
 */
@SuppressWarnings("serial")
@UseCaseController
public class AdminGroupShowListTask extends AbstractWebController implements
		Property.ValueChangeListener {

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_GROUPS_ADMIN;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			emptyContextMenu();

			final JoinGroupAdminToGroupHome lGroupHome = BOMHelper
					.getJoinGroupAdminToGroupHome();
			final CodeList lList = CodeListHome.instance().getCodeList(
					GroupState.class, getLocaleChecked().getLanguage());
			return new AdminGroupListView(GroupContainer.createData(
					lGroupHome.select(getActor().getActorID(),
							createOrder(GroupHome.KEY_ID, false)), lList), this);
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	@Override
	public void valueChange(final ValueChangeEvent inEvent) {
		final Object lItem = inEvent.getProperty().getValue();
		if (lItem instanceof GroupWrapper) {
			setGroupID(((GroupWrapper) lItem).getGroupID());
			sendEvent(AdminQuestionListTask.class);
		}
	}

}
