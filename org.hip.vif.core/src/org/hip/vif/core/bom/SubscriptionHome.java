package org.hip.vif.core.bom;

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;

/**
 * Interface for home managing the Subscription models.
 * 
 * @author Benno Luthiger
 * Created on Feb 14, 2004
 */
public interface SubscriptionHome extends DomainObjectHome {
	public final static String KEY_MEMBERID = "MemberID";
	public final static String KEY_QUESTIONID = "QuestionID";
	public final static String KEY_LOCAL = "Local";
	
	public final static Integer IS_SUBTREE = new Integer(0);
	public final static Integer IS_LOCAL = new Integer(1);
	
	/**
	 * Creates a new subscription with the specified values.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @param inLocal boolean
	 * @throws VException
	 * @throws SQLException
	 */
	void ucNew(String inQuestionID, Long inMemberID, boolean inLocal) throws VException, SQLException;

	/**
	 * Checks whether a subscription with the specified values exists.
	 * 
	 * @param inQuestionID String
	 * @param inMemberID Long
	 * @return boolean
	 * @throws VException
	 */
	boolean hasSubscription(Long inQuestionID, Long inMemberID) throws VException;
	/**
	 * @deprecated
	 */
	boolean hasSubscription(String inQuestionID, Long inMemberID) throws VException;
	
	/**
	 * Deletes the entry with the specified key.
	 * 
	 * @param inQuestionID Long
	 * @param inMemberID Long
	 * @throws VException
	 * @throws SQLException
	 */
	void delete(Long inQuestionID, Long inMemberID) throws VException, SQLException;

	/**
	 * Updates the range of the specified subscription.
	 * 
	 * @param inQuestionID Long
	 * @param inMemberID Long
	 * @param isLocal boolean If true, the range is set to local.
	 * @throws VException
	 * @throws SQLException
	 */
	void updateRange(Long inQuestionID, Long inMemberID, boolean isLocal) throws VException, SQLException;
}
