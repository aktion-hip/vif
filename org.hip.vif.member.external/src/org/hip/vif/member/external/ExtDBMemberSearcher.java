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
package org.hip.vif.member.external;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.queryparser.classic.ParseException;
import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.ExtMemberImpl;
import org.hip.vif.core.bom.impl.MemberImpl;
import org.hip.vif.core.member.IMemberInformation;
import org.hip.vif.core.member.IMemberSearcher;
import org.hip.vif.core.search.NoHitsException;

/** Class for searching member entries using a setup where the members are stored in an external member table. Searching
 * is done using the DB search index of the external member table.
 *
 * @author Luthiger 30.06.2007 */
public class ExtDBMemberSearcher implements IMemberSearcher {
    private final MemberHome memberHome;
    protected KeyObject searchKey = null;

    public ExtDBMemberSearcher() {
        super();
        memberHome = getMemberAuthenticationHome();
    }

    @Override
    public void prepareSearch(final String inQueryTerm) throws IOException,
            ParseException, NoHitsException, VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(MemberHome.KEY_NAME, inQueryTerm + "%", "LIKE");
        lKey.setValue(MemberHome.KEY_FIRSTNAME, inQueryTerm + "%", "LIKE",
                BinaryBooleanOperator.OR);
        searchKey = lKey;
    }

    @Override
    public void prepareSearch(final String inName, final String inFirstName,
            final String inStreet, final String inPostal, final String inCity,
            final String inMail) throws NoHitsException, ParseException,
            IOException, VException, SQLException {
        final KeyObject lKey = new KeyObjectImpl();
        modifySearch(lKey, inName, MemberHome.KEY_NAME);
        modifySearch(lKey, inFirstName, MemberHome.KEY_FIRSTNAME);
        modifySearch(lKey, inStreet, MemberHome.KEY_STREET);
        modifySearch(lKey, inPostal, MemberHome.KEY_ZIP);
        modifySearch(lKey, inCity, MemberHome.KEY_CITY);
        modifySearch(lKey, inMail, MemberHome.KEY_MAIL);
        searchKey = lKey;
    }

    private void modifySearch(final KeyObject inKey, final String inSearch,
            final String inColumnName) throws VException {
        if (inSearch.length() != 0) {
            if (memberHome.getColumnNameFor(inColumnName).length() != 0) {
                inKey.setValue(inColumnName, inSearch + "%", "LIKE");
            }
        }
    }

    @Override
    public boolean canShowAll() {
        return true;
    }

    @Override
    public QueryResult doSearch(final OrderObject inOrder) throws VException,
            SQLException {
        VSys.assertNotNull(Assert.ERROR, this, "doSearch", searchKey);
        return memberHome.select(searchKey, inOrder);
    }

    @Override
    public boolean canReorder() {
        return true;
    }

    /** For authentication, we call the home of the member entries residing in the external database.
     * 
     * @see org.hip.vif.member.MemberHandlerFactory#getMemberHome() */
    @Override
    public MemberHome getMemberAuthenticationHome() {
        return (MemberHome) VSys.homeManager
                .getHome(ExtMemberImpl.HOME_CLASS_NAME);
    }

    /** Member entries are cached in the internal member table.
     * 
     * @see org.hip.vif.member.MemberHandlerFactory#getMemberCacheHome() */
    @Override
    public MemberHome getMemberCacheHome() {
        return (MemberHome) VSys.homeManager
                .getHome(MemberImpl.HOME_CLASS_NAME);
    }

    @Override
    public Collection<Long> createMemberCacheEntryChecked(
            final Collection<Long> inMemberIDs) throws VException, SQLException {
        final Collection<Long> outIDs = new ArrayList<Long>(inMemberIDs.size());
        final MemberHome lHome = getMemberAuthenticationHome();
        for (final Member lMember : lHome.getMembers(inMemberIDs)) {
            final IMemberInformation lInformation = new ExtDBMemberInformation(
                    lMember);
            final Member lCached = getMemberCacheHome().updateMemberCache(
                    lInformation);
            outIDs.add((Long) lCached.get(MemberHome.KEY_ID));
        }
        return outIDs;
    }

    @Override
    public Long getAssociatedCacheID(final Long inMemberID) throws VException,
            SQLException {
        final IMemberInformation lInformation = new ExtDBMemberInformation(
                getMemberAuthenticationHome().getMember(new Long(inMemberID)));
        final String lUserID = lInformation.getUserID();
        try {
            final Member lMember = getMemberCacheHome().getMemberByUserID(
                    lUserID);
            return new Long(lMember.get(MemberHome.KEY_ID).toString());
        } catch (final BOMInvalidKeyException exc) {
            // the member entry is not cached yet, therefore, we do that now
            final Member lMember = (Member) getMemberCacheHome().create();
            return lInformation.insert(lMember);
        }
    }

}
