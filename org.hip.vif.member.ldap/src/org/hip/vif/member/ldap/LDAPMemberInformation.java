/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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

/**
 *
 * @author Luthiger
 * Created: 07.01.2008
 */
public class LDAPMemberInformation extends AbstractMemberInformation implements IMemberInformation {
	private final static Object[][] MEMBER_PROPERTIES = {{MemberHome.KEY_USER_ID, ""}, 
		{MemberHome.KEY_NAME, ""},
		{MemberHome.KEY_FIRSTNAME, ""},
		{MemberHome.KEY_MAIL, ""},
		{MemberHome.KEY_SEX, new Integer(-1)},
		{MemberHome.KEY_CITY, ""},
		{MemberHome.KEY_STREET, ""},
		{MemberHome.KEY_ZIP, ""},
		{MemberHome.KEY_PHONE, ""},
		{MemberHome.KEY_FAX, ""}};

	private String userID = "";
	
	public LDAPMemberInformation(Member inSource) throws VException {
		userID = loadValues(inSource);
	}

	/* (non-Javadoc)
	 * @see org.hip.vif.member.MemberInformation#update(org.hip.vif.bom.Member)
	 */
	public void update(Member inMember) throws SQLException, VException {
		setAllTo(inMember);
		inMember.update(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.member.MemberInformation#insert(org.hip.vif.bom.Member)
	 */
	public Long insert(Member inMember) throws SQLException, VException {
		setAllTo(inMember);
		return inMember.insert(true);
	}
	
	private String loadValues(Member inSource) throws VException {
		for (int i = 0; i < MEMBER_PROPERTIES.length; i++) {
			String lKey = MEMBER_PROPERTIES[i][0].toString();
			Object lValue = null;
			try {
				lValue = inSource.get(lKey);
			} catch (GettingException exc) {
				lValue = MEMBER_PROPERTIES[i][1];
			}
			put(lKey, lValue);
		}
		return inSource.get(MemberHome.KEY_USER_ID).toString();
	}

	private void setAllTo(Member inMember) throws VException {
		for (Map.Entry<String, Object> lEntry : entries()) {
			inMember.set((String)lEntry.getKey(), lEntry.getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.member.MemberInformation#getUserID()
	 */
	public String getUserID() {
		return userID;
	}

}
