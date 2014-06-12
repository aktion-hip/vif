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
 * TextAuthorReviewerHome is responsible to manage instances
 * of class org.hip.vif.bom.TextAuthorReviewer.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.TextAuthorReviewer
 */
public interface TextAuthorReviewerHome extends DomainObjectHome {
	public final static String KEY_TEXT_ID 	= "TextID";
	public final static String KEY_VERSION 	= "Version";
	
	/**
	 * Sets the specified member as author of the specified text entry.
	 * 
	 * @param inMemberID Long
	 * @param inTextID Long
	 * @param inVersion int
	 * @throws VException 
	 * @throws SQLException 
	 */
	void setAuthor(Long inMemberID, Long inTextID, int inVersion) throws VException, SQLException;
	
	/**
	 * Sets the specified member as reviewer of the specified text entry.
	 * 
	 * @param inMemberID Long
	 * @param inTextID Long
	 * @param inVersion int
	 * @throws VException
	 * @throws SQLException
	 */
	void setReviewer(Long inMemberID, Long inTextID, int inVersion) throws VException, SQLException;

	/**
	 * Removes the specified member as reviewer of the specified text entry (i.e. sets the entry to REVIEWER_REFUSED).
	 * 
	 * @param inReviewerID Long
	 * @param inTextID Long
	 * @param inTextVersion int
	 * @throws VException
	 * @throws SQLException
	 */
	void removeReviewer(Long inReviewerID, Long inTextID, int inTextVersion) throws VException, SQLException;

	/**
	 * Returns the specified text entry's author.
	 * 
	 * @param inTextID Long
	 * @param inTextVersion int
	 * @return {@link Member}
	 * @throws VException
	 * @throws Exception
	 */
	Member getAuthor(Long inTextID, int inTextVersion) throws Exception;

	boolean checkRefused(Long inReviewerID, Long inTextID, int inVersion) throws VException, SQLException;
}
