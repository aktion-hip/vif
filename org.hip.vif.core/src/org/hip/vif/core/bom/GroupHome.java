/*
	This package is part of the administration of the application VIF.
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

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.ExternIDNotUniqueException;

/**
 * GroupHome is responsible to manage instances of class org.hip.vif.bom.Group
 * 
 * Created on 19.07.2002
 * 
 * @author Benno Luthiger
 * @see GroupHome
 */
public interface GroupHome extends DomainObjectHome {
	// constants
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "Name";
	public final static String KEY_DESCRIPTION = "Description";
	public final static String KEY_REVIEWERS = "Reviewers";
	public final static String KEY_GUEST_DEPTH = "GuestDepth";
	public final static String KEY_MIN_GROUP_SIZE = "MinGroupSize";
	public final static String KEY_PRIVATE = "Private";
	public final static String KEY_STATE = "State";

	public final static Integer IS_PRIVATE = new Integer(1);
	public final static Integer IS_PUBLIC = new Integer(0);

	/**
	 * Returns the discussion group identified by the specified ID
	 * 
	 * @param inGroupID
	 *            java.lang.String
	 * @throws org.hip.kernel.bom.BOMInvalidKeyException
	 */
	Group getGroup(String inGroupID) throws BOMInvalidKeyException;

	Group getGroup(Long inGroupID) throws BOMInvalidKeyException;

	/**
	 * Creates a new discussion group with the specified data and returns the
	 * internal key of the newly created group.
	 * 
	 * @param inGroupID
	 *            java.lang.String
	 * @param inName
	 *            java.lang.String
	 * @param inReviewers
	 *            java.lang.String
	 * @param inGuestDepth
	 *            java.lang.String
	 * @param inMinGoupSize
	 *            java.lang.String
	 * @param inIsPrivate
	 *            boolean
	 * @return java.lang.Long Key of newly created discussion group
	 * @throws java.sql.SQLException
	 * @throws org.hip.kernel.exc.VException
	 * @throws ExternIDNotUniqueException
	 */
	Long createNew(String inGroupID, String inName, String inReviewers,
			String inGuestDepth, String inMinGoupSize, boolean inIsPrivate)
			throws SQLException, VException, ExternIDNotUniqueException;

	/**
	 * Returns the groups depending of the specified user's role.<br/>
	 * IF <code>inActorID</code> is SU or ADMIN, all groups are returned
	 * (instances of <code>Group</code>).<br/>
	 * IF <code>inActorID</code> is GROUP_ADMIN, only the specified user's
	 * groups are returned (i.e. instances of <code>JoinGroupAdminToGroup</code>
	 * ).<br/>
	 * ELSE, no groups are returned.
	 * 
	 * @param inActorID
	 *            Long
	 * @param inOrder
	 *            OrderObject
	 * @return {@link QueryResult} of <code>Group</code> or
	 *         <code>JoinGroupAdminToGroup</code> instances
	 * @throws Exception
	 */
	QueryResult selectForAdministration(Long inActorID, OrderObject inOrder)
			throws Exception;

	/**
	 * Returnt true if the specified group is of type where the contributions
	 * need to be reviewed (i.e. the number of reviewers is set > 0).
	 * 
	 * @param inGroupID
	 *            String
	 * @return true, if the contributions have to be reviewed.
	 * @throws VException
	 */
	boolean needsReview(String inGroupID) throws VException;
}
