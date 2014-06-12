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

package org.hip.vif.admin.member.util;

import java.sql.SQLException;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.member.Constants;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.IMemberQueryStrategy;
import org.hip.vif.core.member.IMemberSearcher;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.util.ParameterObject;

/**
 * Helper class providing functionality to query the member data.
 * 
 * @author Luthiger
 * Created: 17.11.2011
 */
public class QueryHelper {

	/**
	 * Factory method to create the <code>IMemberQueryStrategy</code> by evaluating the search query.
	 * 
	 * @param inParameters {@link ParameterObject} 
	 * @return {@link IMemberQueryStrategy}
	 * @throws Exception
	 */
	public static IMemberQueryStrategy getQueryStrategy(ParameterObject inParameters) throws Exception {
		if (inParameters == null) return new QueryDBTable();
		
		QueryHitsList lQuery;
		switch ((Constants.SearchType)inParameters.get(Constants.KEY_PARAMETER_SEARCH_TPYE)) {
		case QUICK:
			lQuery = new QueryHitsList(inParameters.get(Constants.KEY_PARAMETER_SEARCH_QUERY).toString());
			return lQuery;

		case DETAILED:
			lQuery = new QueryHitsList(inParameters.get(Constants.KEY_PARAMETER_SEARCH_NAME).toString(), 
					inParameters.get(Constants.KEY_PARAMETER_SEARCH_FIRSTNAME).toString(), 
					inParameters.get(Constants.KEY_PARAMETER_SEARCH_STREET).toString(), 
					inParameters.get(Constants.KEY_PARAMETER_SEARCH_ZIP).toString(), 
					inParameters.get(Constants.KEY_PARAMETER_SEARCH_CITY).toString(), 
					inParameters.get(Constants.KEY_PARAMETER_SEARCH_MAIL).toString());
			return lQuery;
		}
		return new QueryDBTable();
	}
	
	/**
	 * Returns the strategy that returns the member from the DB table.
	 * 
	 * @return {@link IMemberQueryStrategy}
	 */
	public static IMemberQueryStrategy getFullDBTableStrategy() {
		return new QueryDBTable();
	}
	
// --- private classes ---
	
	public static class QueryDBTable implements IMemberQueryStrategy {
		private MemberHome home;
		private boolean isCached = false;

		QueryDBTable() {
			IMemberSearcher lSearcher = MemberUtility.INSTANCE.getActiveMemberSearcher();
			home = lSearcher.getMemberAuthenticationHome();
		}

		public QueryResult getQueryResult(OrderObject inOrder) throws VException, SQLException {
			return home.select(inOrder);
		}
	
		public boolean isFromCache() {
			return isCached;
		}
	}

	public static class QueryHitsList implements IMemberQueryStrategy {
		private IMemberSearcher searcher;
		
		QueryHitsList(String inQueryString) throws Exception {
			searcher = MemberUtility.INSTANCE.getActiveMemberSearcher();
			searcher.prepareSearch(inQueryString);
		}
		public QueryHitsList(String inName, String inFirstName, String inStreet, String inPostal, String inCity, String inMail) throws Exception {
			searcher = MemberUtility.INSTANCE.getActiveMemberSearcher();
			searcher.prepareSearch(inName, inFirstName, inStreet, inPostal, inCity, inMail);
		}
		public QueryResult getQueryResult(OrderObject inOrder) throws VException, SQLException {
			return searcher.doSearch(inOrder);
		}
		public boolean canReorder() {
			return searcher.canReorder();
		}
		public boolean isFromCache() {
			return false;
		}
	}
	
}
