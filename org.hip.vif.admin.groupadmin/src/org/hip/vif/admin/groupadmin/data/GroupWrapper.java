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

package org.hip.vif.admin.groupadmin.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.web.util.BeanWrapperHelper;

/**
 * Bean adapting the group model instance.
 * 
 * @author Luthiger
 * Created: 19.11.2011
 */
public class GroupWrapper {	
	private Long groupID;
	private String name;
	private String description;
	private Long reviewers;
	private Long guestDepth;
	private Long minGroupSize;
	private String state;

	/**
	 * Private constructor.
	 * 
	 * @param inDomainObject
	 * @param inCodeList
	 */
	private GroupWrapper(GeneralDomainObject inDomainObject, CodeList inCodeList) {
		groupID =  BeanWrapperHelper.getLong(GroupHome.KEY_ID, inDomainObject);
		name = BeanWrapperHelper.getString(GroupHome.KEY_NAME, inDomainObject);
		description = BeanWrapperHelper.getString(GroupHome.KEY_DESCRIPTION, inDomainObject);
		reviewers = BeanWrapperHelper.getLong(GroupHome.KEY_REVIEWERS, inDomainObject);
		guestDepth = BeanWrapperHelper.getLong(GroupHome.KEY_GUEST_DEPTH, inDomainObject);
		minGroupSize = BeanWrapperHelper.getLong(GroupHome.KEY_MIN_GROUP_SIZE, inDomainObject);
		state = inCodeList.getLabel(BeanWrapperHelper.getString(GroupHome.KEY_STATE, inDomainObject));
	}

	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject} the domain object to wrap
	 * @param inCodeList {@link CodeList} the group states
	 * @return {@link GroupWrapper}
	 */
	public static GroupWrapper createItem(GeneralDomainObject inDomainObject, CodeList inCodeList) {
		GroupWrapper outGroup = new GroupWrapper(inDomainObject, inCodeList);
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

	public String getState() {
		return state;
	}

	public Long getReviewers() {
		return reviewers;
	}

	public Long getGuestDepth() {
		return guestDepth;
	}

	public Long getMinGroupSize() {
		return minGroupSize;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
