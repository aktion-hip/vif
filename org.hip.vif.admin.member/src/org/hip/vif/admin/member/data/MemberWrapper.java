/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.admin.member.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.MemberHitsObject;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.MemberBean;

/** Wrapper class / adapter for the member model class.
 *
 * @author Luthiger Created: 17.10.2011 */
public class MemberWrapper extends MemberBean {
    private final String street;
    private final String zip;
    private final String place;

    /** @param inDomainObject {@link GeneralDomainObject} */
    private MemberWrapper(final GeneralDomainObject inDomainObject) {
        super(inDomainObject);
        street = BeanWrapperHelper.getString(MemberHome.KEY_STREET, inDomainObject);
        zip = BeanWrapperHelper.getString(MemberHome.KEY_ZIP, inDomainObject);
        place = BeanWrapperHelper.getString(MemberHome.KEY_CITY, inDomainObject);
    }

    /** @param inMember {@link MemberHitsObject} */
    private MemberWrapper(final MemberHitsObject inMember) {
        super(inMember);
        street = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_STREET.fieldName, inMember);
        zip = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_POSTAL.fieldName, inMember);
        place = BeanWrapperHelper.getString(AbstractSearching.IndexField.MEMBER_CITY.fieldName, inMember);
    }

    /** Factory method, instance creation.
     *
     * @param inDomainObject {@link GeneralDomainObject}
     * @return {@link MemberWrapper} */
    public static MemberWrapper createItem(final GeneralDomainObject inDomainObject) {
        final MemberWrapper outMember = (inDomainObject instanceof MemberHitsObject) ?
                new MemberWrapper((MemberHitsObject) inDomainObject) :
                    new MemberWrapper(inDomainObject);
                return outMember;
    }

    /** @return the street */
    public String getStreet() {
        return street;
    }

    /** @return the place */
    public String getPlace() {
        return String.format("%s %s", zip, place); //$NON-NLS-1$
    }

}
