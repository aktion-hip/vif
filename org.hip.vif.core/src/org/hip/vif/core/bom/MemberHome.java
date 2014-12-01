/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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
import java.util.Collection;

import org.hip.kernel.bom.BOMInvalidKeyException;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.member.IMemberInformation;

/** MemberHome is responsible to manage instances of class org.hip.vif.bom.Member.
 *
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.Member */
public interface MemberHome extends DomainObjectHome {
    public final static String KEY_ID = "ID";
    public final static String KEY_USER_ID = "UserID";
    public final static String KEY_NAME = "Name";
    public final static String KEY_FIRSTNAME = "Firstname";
    public final static String KEY_STREET = "Street";
    public final static String KEY_ZIP = "ZIP";
    public final static String KEY_CITY = "City";
    public final static String KEY_PHONE = "Tel";
    public final static String KEY_FAX = "Fax";
    public final static String KEY_MAIL = "Mail";
    public final static String KEY_SEX = "Sex";
    public final static String KEY_LANGUAGE = "Language";
    public final static String KEY_PASSWORD = "Password";
    public final static String KEY_SETTINGS = "Settings";
    public final static String KEY_MUTATION = "Mutation";

    /** Check whether the specified user is authenticated and returns the member entry in the authentication table,
     * possibly <code>null</code>.
     * 
     * @param inUserID java.lang.String
     * @param inPassword java.lang.String
     * @return Member or <code>null</code>.
     * @exception org.hip.vif.core.exc.bom.impl.InvalidAuthenticationException
     * @exception org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    Member checkAuthentication(String inUserID, String inPassword)
            throws InvalidAuthenticationException, BOMChangeValueException;

    /** Returns the member identified by the specified ID
     * 
     * @param inMemberID java.lang.String
     * @return Member
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    Member getMember(String inMemberID) throws BOMInvalidKeyException;

    /** Returns the member identified by the specified ID
     * 
     * @param inMemberID java.lang.Long
     * @return Member
     * @throws BOMInvalidKeyException */
    Member getMember(Long inMemberID) throws BOMInvalidKeyException;

    /** Returns the member identified by the specified user ID. The user ID is the string the user enters to
     * authenticate.
     * 
     * @param inUserID java.lang.String
     * @throws org.hip.kernel.bom.BOMInvalidKeyException */
    Member getMemberByUserID(String inUserID) throws BOMInvalidKeyException;

    /** Returns a collection of members identified by the specified array of IDs.
     * 
     * @param inMemberIDs Collection<Long>
     * @return java.util.Collection<Member> Collection of Member domain objects. */
    Collection<Member> getMembers(Collection<Long> inMemberIDs);

    /** Updates the Member cache, i.e. uses the member table as cache to store member information stored externally.
     * 
     * @return Member member entry in table of cached members.
     * @param inInformation MemberInformation
     * @throws SQLException
     * @throws VException */
    Member updateMemberCache(IMemberInformation inInformation)
            throws SQLException, VException;

}
