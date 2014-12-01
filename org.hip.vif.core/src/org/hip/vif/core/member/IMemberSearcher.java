/**
    This package is part of the application VIF.
    Copyright (C) 2005-2014, Benno Luthiger

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
package org.hip.vif.core.member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.lucene.queryparser.classic.ParseException;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.search.NoHitsException;

/** Interface for classes used to search for member entries.
 *
 * @author Luthiger 30.06.2007 */
public interface IMemberSearcher {

    /** Prepares the search in the store for member entries using a single query term.
     *
     * @param inQueryTerm String search term
     * @throws IOException
     * @throws ParseException
     * @throws NoHitsException
     * @throws VException
     * @throws SQLException */
    public void prepareSearch(String inQueryTerm) throws IOException, ParseException, NoHitsException, VException,
    SQLException;

    /** Prepares the search in the store for member entries using a detailed search query.
     *
     * @param inName String
     * @param inFirstName String
     * @param inStreet String
     * @param inPostal String
     * @param inCity String
     * @param inMail String
     * @throws NoHitsException
     * @throws ParseException
     * @throws IOException
     * @throws VException
     * @throws SQLException */
    public void prepareSearch(String inName, String inFirstName, String inStreet, String inPostal, String inCity,
            String inMail)
                    throws NoHitsException, ParseException, IOException, VException, SQLException;

    /** This method gives information about whether this <code>MemberSearcher</code> can show all member entries in the
     * member store.
     *
     * @return boolean <code>true</code> if this searcher can show all member entries in the member store. */
    public boolean canShowAll();

    /** This method gives information about whether this <code>MemberSearcher</code> can order the member entries
     * retrieved.
     *
     * @return boolean <code>true</code> if ordering of member entries is possible.
     * @see OrderObject */
    public boolean canReorder();

    /** Carries out the search.
     *
     * @param inOrder OrderObject
     * @return QueryResult the result set of member entries matching the specified query.
     * @throws SQLException
     * @throws VException */
    public QueryResult doSearch(OrderObject inOrder) throws VException, SQLException;

    /** Returns an instance of <code>MemberHome</code> managing the entries (i.e. <code>Member</code>) residing in an
     * external database.<br />
     * This home must be used to authenticate against.
     *
     * @return MemberHome */
    public MemberHome getMemberAuthenticationHome();

    /** Returns an instance of <code>MemberHome</code> managing the entries (i.e. <code>Member</code>) residing in the
     * internal members cache.<br />
     * This home must be used to store application specific user data.
     *
     * @return MemberHome */
    public MemberHome getMemberCacheHome();

    /** Creates an entry in the members cache for the <code>Member</code> with specified member ID. If the member entry
     * exists yet, the member information is updated. This method returns an array of unique IDs of the members in the
     * members cache. If this functionality is not needed, nothing is done and the inputed array of member IDs is
     * returned as is.
     *
     * @param Collection<Long> Collection of Member IDs in authentication table.
     * @return Collection<Long> Collection of Member IDs in cached member table.
     * @throws VException
     * @throws SQLException */
    public Collection<Long> createMemberCacheEntryChecked(Collection<Long> inMemberIDs) throws VException, SQLException;

    /** Returns the ID of the member's entry in the table of cached members given the ID of the member's entry in the
     * external member table. If there's no external member table, the specified ID is returned as is.
     *
     * @param inMemberID Long Member ID (unique identification) in authentication store. In a database, this usually is
     *            a number.
     * @return Long Member ID in cached member table.
     * @throws VException
     * @throws SQLException */
    public Long getAssociatedCacheID(Long inMemberID) throws VException, SQLException;

}
