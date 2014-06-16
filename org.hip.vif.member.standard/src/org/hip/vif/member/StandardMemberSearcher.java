/**
	This package is part of the application VIF.
	Copyright (C) 2007-2014, Benno Luthiger

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
package org.hip.vif.member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.lucene.queryParser.ParseException;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.MemberImpl;
import org.hip.vif.core.member.IMemberSearcher;
import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.core.search.VIFMemberSearcher;

/**
 * Class for searching member entries using standard VIF setups, i.e. members
 * are stored in the internal member table. Searching is done using the
 * <code>VIFMemberSearcher</code> object.
 * 
 * @author Luthiger 30.06.2007
 */
public class StandardMemberSearcher implements IMemberSearcher {
	private static String MEMBER_HOME_CLASS_NAME = MemberImpl.HOME_CLASS_NAME;
	private QueryResult retrieved = null;

	@Override
	public void prepareSearch(final String inQueryTerm) throws IOException,
			ParseException, NoHitsException, VException, SQLException {
		retrieved = (new VIFMemberSearcher()).search(inQueryTerm);
	}

	@Override
	public void prepareSearch(final String inName, final String inFirstName,
			final String inStreet, final String inPostal, final String inCity,
			final String inMail) throws NoHitsException, ParseException,
			IOException, VException, SQLException {
		retrieved = (new VIFMemberSearcher()).search(inName, inFirstName,
				inStreet, inPostal, inCity, inMail);
	}

	@Override
	public boolean canShowAll() {
		return true;
	}

	@Override
	public boolean canReorder() {
		return false;
	}

	@Override
	public QueryResult doSearch(final OrderObject inOrder) throws VException,
			SQLException {
		VSys.assertNotNull(Assert.ERROR, this, "doSearch", retrieved);
		return retrieved;
	}

	@Override
	public MemberHome getMemberCacheHome() {
		return getMemberAuthenticationHome();
	}

	/**
	 * In the standard case, we only have one type of member entries.
	 */
	@Override
	public MemberHome getMemberAuthenticationHome() {
		return (MemberHome) VSys.homeManager.getHome(MEMBER_HOME_CLASS_NAME);
	}

	@Override
	public Collection<Long> createMemberCacheEntryChecked(
			final Collection<Long> inMemberIDs) throws VException, SQLException {
		// We don't have a 'member cache', therefore, nothing to do.
		return inMemberIDs;
	}

	@Override
	public Long getAssociatedCacheID(final Long inMemberID) throws VException,
			SQLException {
		// We don't have a 'member cache', therefore, nothing to do.
		return inMemberID;
	}

}
