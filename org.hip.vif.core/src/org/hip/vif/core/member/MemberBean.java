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

package org.hip.vif.core.member;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.MemberHitsObject;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Wrapper class / adapter for the member model class.
 * 
 * @author Luthiger
 * Created: 17.10.2011
 */
public class MemberBean implements ISelectableBean {
	private Long memberID;
	private String userID;
	private String firstName;
	private String familyName;
	private String mail;
	private boolean checked;

	/**
	 * @param inDomainObject {@link GeneralDomainObject}
	 */
	protected MemberBean(GeneralDomainObject inDomainObject) {
		memberID = BeanWrapperHelper.getLong(MemberHome.KEY_ID, inDomainObject);
		userID = BeanWrapperHelper.getString(MemberHome.KEY_USER_ID, inDomainObject);
		firstName = BeanWrapperHelper.getString(MemberHome.KEY_FIRSTNAME, inDomainObject);
		familyName = BeanWrapperHelper.getString(MemberHome.KEY_NAME, inDomainObject);
		mail = BeanWrapperHelper.getString(MemberHome.KEY_MAIL, inDomainObject);
	}
	
	/**
	 * @param inMember {@link MemberHitsObject}
	 */
	protected MemberBean(MemberHitsObject inMember) {
		memberID = BeanWrapperHelper.getLong(AbstractSearching.IndexField.MEMBER_ID.fieldName, inMember);
		userID = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_USER_ID.fieldName, inMember);
		firstName = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_FIRSTNAME.fieldName, inMember);
		familyName = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_NAME.fieldName, inMember);
		mail = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_MAIL.fieldName, inMember);    
	}
	
	/**
	 * Factory method, instance creation.
	 * 
	 * @param inDomainObject {@link GeneralDomainObject}
	 * @return {@link MemberWrapper}
	 */
	public static MemberBean createItem(GeneralDomainObject inDomainObject) {
		MemberBean outMember = (inDomainObject instanceof MemberHitsObject) ? 
				new MemberBean((MemberHitsObject) inDomainObject) : 
				new MemberBean(inDomainObject);
		return outMember;
	}	
	
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return String.format("%s %s", firstName, familyName);
	}
	
	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @return the memberID
	 */
	public Long getMemberID() {
		return memberID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.ISelectableBean#isChecked()
	 */
	public boolean isChecked() {
		return checked;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.ISelectableBean#setChecked(boolean)
	 */
	public void setChecked(boolean inChecked) {
		checked = inChecked;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s (%s)", familyName, firstName, getUserID());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((memberID == null) ? 0 : memberID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberBean other = (MemberBean) obj;
		if (memberID == null) {
			if (other.memberID != null)
				return false;
		} else if (!memberID.equals(other.memberID))
			return false;
		return true;
	}
	
}
