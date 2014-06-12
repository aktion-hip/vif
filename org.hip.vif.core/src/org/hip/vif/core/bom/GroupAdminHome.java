/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

package org.hip.vif.core.bom;

import java.sql.SQLException;
import java.util.Collection;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * GroupAdminHome is responsible to manage instances
 * of class org.hip.vif.bom.GroupAdmin.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.GroupAdmin
 */
public interface GroupAdminHome extends DomainObjectHome {
	//constants
	public final static String KEY_GROUP_ID 	= "GroupID";
	public final static String KEY_MEMBER_ID	= "MemberID";
	
	/**
	 * Associates the indicated members as group administrators 
	 * to the specified discussion group.<br/>
	 * <b>Note</b>: this method only adds those members not already assigned as group administrators.
	 * Therefore, an internal check is made for each entry in the list.
	 * 
	 * @param inGroupID Long
	 * @param inGroupAdmins java.lang.String[]
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	void associateGroupAdmins(Long inGroupID, String[] inGroupAdmins) throws BOMChangeValueException;
	
	/**
	 * Associates the indicated members as group administrators 
	 * to the specified discussion group.<br/>
	 * <b>Note</b>: this method only adds those members not already assigned as group administrators.
	 * Therefore, an internal check is made for each entry in the list.
	 * 
	 * @param inGroupID Long
	 * @param inGroupAdmins {@link Collection} of <code>Long</code>
	 * @throws BOMChangeValueException
	 */
	void associateGroupAdmins(Long inGroupID, Collection<Long> inGroupAdmins) throws BOMChangeValueException;
	
	/**
	 * Associates the member as group administrator to the specified discussion group.<br />
	 * <b>Note</b>: this method does not check whether the specified member is already associated as group administrator.
	 * 
	 * @param inGroupID Long
	 * @param inMemberID Long
	 * @throws VException
	 * @throws SQLException
	 */
	void associateGroupAdmin(Long inGroupID, Long inMemberID) throws VException, SQLException;
	
	/**
	 * Tests whether the specified member is group admin.
	 * 
	 * @param inMemberID Long
	 * @return boolean true, if the member is group admin.
	 * @throws VException
	 */
	boolean isGroupAdmin(Long inMemberID) throws VException;
	
	/**
	 * Tests whether the specified member is admin of the specified group.
	 *  
	 * @param inMemberID Long
	 * @param inGroupID Long
	 * @return boolean true, if the member is admin of the specified group.
	 * @throws BOMChangeValueException
	 */
	boolean isGroupAdmin(Long inMemberID, Long inGroupID) throws BOMChangeValueException;
	
	/**
	 * Tests the specified list of member ids and returns a collection of ids
	 * of those members which are not group administrators.
	 * 
	 * @param inMemberIDs String[] 
	 * @return Collection of ids (String) of those members which are not group administrators. 
	 * @throws VException
	 */
	Collection<String> checkGroupAdmins(String[] inMemberIDs) throws VException;
}
