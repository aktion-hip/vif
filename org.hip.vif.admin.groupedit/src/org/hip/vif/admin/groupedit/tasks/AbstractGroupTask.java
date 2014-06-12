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

package org.hip.vif.admin.groupedit.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.web.tasks.AbstractVIFTask;

/**
 * Base class of controllers for group administration. 
 * 
 * @author Luthiger
 * Created: 08.11.2011
 */
public abstract class AbstractGroupTask extends AbstractVIFTask {	

	abstract public boolean save(Group inGroup) throws ExternIDNotUniqueException;

	protected void deleteGroupAdmin(Long inGroupID, Long inAdminID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(GroupAdminHome.KEY_MEMBER_ID, inAdminID);
		GroupAdminHome lHome = BOMHelper.getGroupAdminHome();
		//remove group admin entry
		lHome.delete(lKey, true);
		if (!lHome.isGroupAdmin(inAdminID)) {
			//remove group admin role
			BOMHelper.getLinkMemberRoleHome().deleteGroupAdminRole(inAdminID);
		}
	}

}
