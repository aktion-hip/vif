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

package org.hip.vif.admin.admin.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.admin.admin.Activator;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Wrapper class adapting the <code>Group</code> data model.
 * 
 * @author Luthiger
 * Created: 03.11.2011
 */
public class GroupWrapper {
	private Long groupID;
	private String name;

	/**
	 * Private constructor
	 * 
	 * @param inDomainObject
	 */
	private GroupWrapper(GeneralDomainObject inDomainObject) {
		groupID =  BeanWrapperHelper.getLong(GroupHome.KEY_ID, inDomainObject);
		name = BeanWrapperHelper.getString(GroupHome.KEY_NAME, inDomainObject);	
	}

	/**
	 * Private constructor
	 */
	private GroupWrapper() {
		groupID = 0l;
		name = Activator.getMessages().getMessage("admin.send.mail.item.all"); //$NON-NLS-1$
	}

	/**
	 * Factory method to adapt the specified domain object instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject}
	 * @return {@link GroupWrapper}
	 */
	public static GroupWrapper createItem(GeneralDomainObject inDomainObject) {
		return new GroupWrapper(inDomainObject);
	}
	
	/**
	 * Creates the item to select all groups.
	 * 
	 * @return {@link GroupWrapper}
	 */
	public static GroupWrapper createAllItem() {
		return new GroupWrapper();
	}
	
	public Long getGroupID() {
		return groupID;
	}
	
	public String getName() {
		return name;
	}
	
}
