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

package org.hip.vif.forum.register.data;

import java.util.Collection;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Wrapper class for group model instances.
 * 
 * @author Luthiger
 * Created: 21.05.2011
 */
public class GroupWrapper implements ISelectableBean {
	private static final String TMPL_REGISTERED = "%s (%s)"; //$NON-NLS-1$
	
	private Long groupID;
	private String name;
	private String description;
	private String registered;
	private String state;
	private boolean checked;

	/**
	 * Private constructor.
	 * 
	 * @param inDomainObject
	 * @param inRegisterings {@link Collection} the ids of groups the user has registered yet
	 * @param inCodeList
	 */
	private GroupWrapper(GeneralDomainObject inDomainObject, Collection<Long> inRegisterings, CodeList inCodeList) {
		groupID =  BeanWrapperHelper.getLong(NestedGroupHome.KEY_GROUP_ID, inDomainObject);
		checked = inRegisterings.contains(groupID);
		name = BeanWrapperHelper.getString(GroupHome.KEY_NAME, inDomainObject);
		description = BeanWrapperHelper.getString(GroupHome.KEY_DESCRIPTION, inDomainObject);
		registered = getValueRegistered(inDomainObject);
		state = inCodeList.getLabel(BeanWrapperHelper.getString(GroupHome.KEY_STATE, inDomainObject));
	}
	
	private String getValueRegistered(GeneralDomainObject inDomainObject) {
		return String.format(TMPL_REGISTERED, BeanWrapperHelper.getString(NestedGroupHome.KEY_REGISTERED, inDomainObject), 
				BeanWrapperHelper.getString(GroupHome.KEY_MIN_GROUP_SIZE, inDomainObject));
	}

	/**
	 * Factory method, creation the instance.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject} the domain object to wrap
	 * @param inRegisterings {@link Collection} the ids of groups the user has registered yet
	 * @param inCodeList {@link CodeList} the group states
	 * @return {@link GroupWrapper}
	 */
	public static GroupWrapper createItem(GeneralDomainObject inDomainObject, Collection<Long> inRegisterings, CodeList inCodeList) {
		GroupWrapper outGroup = new GroupWrapper(inDomainObject, inRegisterings, inCodeList);
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
	
	public String getRegistered() {
		return registered;
	}

	public String getState() {
		return state;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}
	
}
