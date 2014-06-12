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

package org.hip.vif.forum.suspend.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Adapter for group model instances.
 * 
 * @author Luthiger
 * Created: 21.05.2011
 */
public class GroupWrapper {
	private Long groupID;
	private String name;
	private String description;

	/**
	 * Private constructor.
	 * 
	 * @param inDomainObject
	 */
	private GroupWrapper(GeneralDomainObject inDomainObject) {
		groupID =  BeanWrapperHelper.getLong(NestedGroupHome.KEY_GROUP_ID, inDomainObject);
		name = BeanWrapperHelper.getString(GroupHome.KEY_NAME, inDomainObject);
		description = BeanWrapperHelper.getString(GroupHome.KEY_DESCRIPTION, inDomainObject);
	}
	
	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject} the domain object to wrap
	 * @return {@link GroupWrapper}
	 */
	public static GroupWrapper createItem(GeneralDomainObject inDomainObject) {
		GroupWrapper outGroup = new GroupWrapper(inDomainObject);
		return outGroup;
	}
	
	public Long getGroupID() {
		return groupID;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
}
