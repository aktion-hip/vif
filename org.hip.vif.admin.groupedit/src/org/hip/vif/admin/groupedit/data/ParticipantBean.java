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

package org.hip.vif.admin.groupedit.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.impl.NestedParticipantsOfGroupHome;
import org.hip.vif.core.member.MemberBean;
import org.hip.vif.core.util.BeanWrapperHelper;

/**
 * Adapter class for participant models, i.e. instances of <code>NestedParticipantsOfGroup</code>.
 * 
 * @author Luthiger
 * Created: 18.11.2011
 */
public class ParticipantBean extends MemberBean {
	private String zip;
	private String place;
	private Integer groupAdminID;
	private Integer suspendFrom;
	private Integer suspendTo;

	private ParticipantBean(GeneralDomainObject inDomainObject) {
		super(inDomainObject);
		zip = BeanWrapperHelper.getString(MemberHome.KEY_ZIP, inDomainObject);
		place = BeanWrapperHelper.getString(MemberHome.KEY_CITY, inDomainObject);
		groupAdminID = BeanWrapperHelper.getInteger(NestedParticipantsOfGroupHome.KEY_GROUPADMIN, inDomainObject);
		suspendFrom = BeanWrapperHelper.getInteger(NestedParticipantsOfGroupHome.KEY_SUSPENDED_TEST1, inDomainObject);
		suspendTo= BeanWrapperHelper.getInteger(NestedParticipantsOfGroupHome.KEY_SUSPENDED_TEST2, inDomainObject);
	}
	
	/**
	 * Factory method.
	 * 
	 * @param inParticipant {@link GeneralDomainObject}
	 * @return {@link ParticipantBean}
	 */
	public static ParticipantBean createItem(GeneralDomainObject inParticipant) {
		return new ParticipantBean(inParticipant);
	}
	
	public String getPlace() {
		return String.format("%s %s", zip, place); //$NON-NLS-1$
	}

	public String getIsAdmin() {
		return groupAdminID > 0 ? "+" : "-"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	public boolean isAdmin() {
		return groupAdminID > 0;
	}
	
	public String getIsActive() {
		return suspendFrom + suspendTo < 2 ? "+" : "-"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
