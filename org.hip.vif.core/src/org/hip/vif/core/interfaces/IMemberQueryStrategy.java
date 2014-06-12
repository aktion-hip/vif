/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.hip.vif.core.interfaces;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;

/**
 * Interface for different strategies to query member sets.
 * 
 * @author Benno Luthiger
 */
public interface IMemberQueryStrategy {
	
	/**
	 * Returns the <code>QueryResult</code> containing the entries found.
	 * 
	 * @param inOrder OrderObject
	 * @return QueryResult
	 * @throws BOMException
	 * @throws SQLException
	 */
	QueryResult getQueryResult(OrderObject inOrder) throws VException, SQLException;

	/**
	 * This method gives information about whether this strategy retrieves the entries from the cache or the original member store.
	 * 
	 * @return boolean <code>true</code> if retrieved entries are from members cache.
	 */
	boolean isFromCache();
	
}
