/*
 This package is part of the application VIF.
 Copyright (C) 2009, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;

/**
 * Model for table join between ratings and (rated) member.
 *
 * @author Luthiger
 * Created: 30.08.2009
 */
public class JoinRatingsToRater extends DomainObjectImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinRatingsToRaterHome";

	private final String TMPL = "%s %s";

	/**
	 * This Method returns the class name of the home.
	 *
	 * @return java.lang.String
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
	
	/**
	 * Returns the member's full name, i.e. <code>firstname name</code>.
	 * 
	 * @return String the member's full name
	 * @throws VException
	 */
	public String getFullName() throws VException {
		return String.format(TMPL, (String)get(MemberHome.KEY_FIRSTNAME), (String)get(MemberHome.KEY_NAME));
	}

	/**
	 * Returns the query result (strategy) to retrieve the questions related to a specific rating event.
	 * 
	 * @return QueryResult
	 * @throws SQLException 
	 * @throws VException 
	 */
	public QueryResult getQuestionsToBeRated() throws VException, SQLException {
		JoinRatingsToQuestionHome lHome = BOMHelper.getJoinRatingsToQuestionHome();
		return lHome.getQuestionsToBeRated(getRatingsID());
	}

	/**
	 * Returns the query result (strategy) to retrieve the completions related to a specific rating event.
	 * 
	 * @return IQueryStrategy
	 * @throws SQLException 
	 * @throws VException 
	 */
	public QueryResult getCompletionsToBeRated() throws VException, SQLException {
		JoinRatingsToCompletionHome lHome = BOMHelper.getJoinRatingsToCompletionHome();
		return lHome.getCompletionsToBeRated(getRatingsID());
	}

	/**
	 * Returns the query result (strategy) to retrieve the bibliography entries related to a specific rating event.
	 * 
	 * @return IQueryStrategy
	 * @throws SQLException 
	 * @throws VException 
	 */
	public QueryResult getTextsToBeRated() throws VException, SQLException {
		JoinRatingsToTextHome lHome = BOMHelper.getJoinRatingsToTextHome();
		return lHome.getTextsToBeRated(getRatingsID());
	}
	
	private Long getRatingsID() throws VException {
		return new Long(get(RatingsHome.KEY_RATINGEVENTS_ID).toString());
	}

}
