/**
 This package is part of the application VIF.
 Copyright (C) 2008-2015, Benno Luthiger

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
package org.hip.vif.member.ldap;

import java.sql.SQLException;
import java.util.Map;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.member.AbstractMemberInformation;
import org.hip.vif.core.member.IMemberInformation;

/** @author Luthiger Created: 07.01.2008 */
public class LDAPMemberInformation extends AbstractMemberInformation implements IMemberInformation {
    private final static Object[][] MEMBER_PROPERTIES = { { MemberHome.KEY_USER_ID, "" },
            { MemberHome.KEY_NAME, "" },
            { MemberHome.KEY_FIRSTNAME, "" },
            { MemberHome.KEY_MAIL, "" },
            { MemberHome.KEY_SEX, Integer.valueOf(-1) },
            { MemberHome.KEY_CITY, "" },
            { MemberHome.KEY_STREET, "" },
            { MemberHome.KEY_ZIP, "" },
            { MemberHome.KEY_PHONE, "" },
            { MemberHome.KEY_FAX, "" } };

    private transient String userID = ""; // NOPMD by lbenno

    /** LDAPMemberInformation constructor.
     *
     * @param inSource {@link Member} the member instance backing this information
     * @throws VException */
    public LDAPMemberInformation(final Member inSource) throws VException {
        super();
        userID = loadValues(inSource);
    }

    @Override
    public void update(final Member inMember) throws SQLException, VException { // NOPMD by lbenno
        setAllTo(inMember);
        inMember.update(true);
    }

    @Override
    public Long insert(final Member inMember) throws SQLException, VException { // NOPMD by lbenno
        setAllTo(inMember);
        return inMember.insert(true);
    }

    private String loadValues(final Member inSource) throws VException {
        for (int i = 0; i < MEMBER_PROPERTIES.length; i++) {
            final String lKey = MEMBER_PROPERTIES[i][0].toString();
            Object lValue = null;
            try {
                lValue = inSource.get(lKey);
            } catch (final GettingException exc) {
                lValue = MEMBER_PROPERTIES[i][1];
            }
            put(lKey, lValue);
        }
        return inSource.get(MemberHome.KEY_USER_ID).toString();
    }

    private void setAllTo(final Member inMember) throws VException {
        for (final Map.Entry<String, Object> lEntry : entries()) {
            inMember.set(lEntry.getKey(), lEntry.getValue());
        }
    }

    @Override
    public String getUserID() { // NOPMD by lbenno
        return userID;
    }

}
