/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;

/**
 * CompletionAuthorReviewerHome is responsible to manage instances
 * of class org.hip.vif.bom.CompletionAuthorReviewer.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.CompletionAuthorReviewer
 */
public interface CompletionAuthorReviewerHome extends DomainObjectHome {
	public final static String KEY_COMPLETION_ID 	= "CompletionID";

	/**
	 * Sets the specified member as author of the specified completion.
	 * 
	 * @param inMemberID java.lang.Long
	 * @param inCompletionID java.lang.Long
	 * @throws org.hip.kernel.exc.VException
	 * @throws java.sql.SQLException
	 */
	void setAuthor(Long inMemberID, Long inCompletionID) throws VException, SQLException;

	/**
	 * Sets the specified member as reviewer of the specified completion.
	 * 
	 * @param inMemberID java.lang.Long
	 * @param inCompletionID java.lang.Long
	 * @throws org.hip.kernel.exc.VException
	 * @throws java.sql.SQLException
	 */
	void setReviewer(Long inMemberID, Long inCompletionID) throws VException, SQLException;

	/**
	 * Removes the specified member as reviewer of the specified completion (i.e. sets the entry to REVIEWER_REFUSED).
	 * 
	 * @param inReviewerID Long
	 * @param inCompletionID Long
	 * @throws VException
	 * @throws SQLException
	 */
	void removeReviewer(Long inReviewerID, Long inCompletionID) throws VException, SQLException;

	/**
	 * Returns the specified completion's author.
	 * 
	 * @param inCompletionID Long
	 * @return {@link Member}
	 * @throws VException
	 * @throws Exception
	 */
	Member getAuthor(Long inCompletionID) throws VException, Exception;

	/**
	 * Checks whether the specified reviewer refused to review the specified completion.
	 * 
	 * @param inReviewerID Long
	 * @param inCompletionID Long
	 * @return boolean <code>true</code> if the reviewer has formerly refused, else <code>false</code>.
	 * @throws VException
	 * @throws SQLException
	 */
	boolean checkRefused(Long inReviewerID, Long inCompletionID) throws VException, SQLException;
	
}
