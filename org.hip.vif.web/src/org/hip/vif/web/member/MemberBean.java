/**
    This package is part of the persistency layer of the application VIF.
    Copyright (C) 2003-2014, Benno Luthiger

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
package org.hip.vif.web.member;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.web.util.AbstractBean;

import com.vaadin.data.util.BeanItem;

/** This is a POJO bean for the <code>Member</code> model.
 *
 * @author lbenno */
public class MemberBean extends AbstractBean {
    public static final String FN_USER_ID = "userid";
    public static final String FN_PASSWORD = "password";
    public static final String FN_NAME = "name";
    public static final String FN_FIRSTNAME = "firstname";
    public static final String FN_STREET = "street";
    public static final String FN_ZIP = "zip";
    public static final String FN_CITY = "city";
    public static final String FN_PHONE = "phone";
    public static final String FN_FAX = "fax";
    public static final String FN_MAIL = "mail";
    public static final String FN_SEX = "sex";
    public static final String FN_ROLES = "roles";

    private RoleContainer roles;

    /** Private constructor.
     *
     * @param inMember */
    private MemberBean(final Member inMember) {
        super(inMember);
    }

    /** Private constructor.
     *
     * @param inMember
     * @param inRoles */
    private MemberBean(final Member inMember, final RoleContainer inRoles) {
        super(inMember);
        roles = inRoles;
    }

    public String getUserid() {
        return getString(MemberHome.KEY_USER_ID);
    }

    public void setUserid(final String inUserid) throws VException {
        setValue(MemberHome.KEY_USER_ID, inUserid);
    }

    public String getPassword() {
        return getString(MemberHome.KEY_PASSWORD);
    }

    public void setPassword(final String inValue) throws VException {
        setValue(MemberHome.KEY_PASSWORD, inValue);
    }

    public String getName() {
        return getString(MemberHome.KEY_NAME);
    }

    public void setName(final String inValue) throws VException {
        setValue(MemberHome.KEY_NAME, inValue);
    }

    public String getFirstname() {
        return getString(MemberHome.KEY_FIRSTNAME);
    }

    public void setFirstname(final String inValue) throws VException {
        setValue(MemberHome.KEY_FIRSTNAME, inValue);
    }

    public String getStreet() {
        return getString(MemberHome.KEY_STREET);
    }

    public void setStreet(final String inValue) throws VException {
        setValue(MemberHome.KEY_STREET, inValue);
    }

    public String getZip() {
        return getString(MemberHome.KEY_ZIP);
    }

    public void setZip(final String inValue) throws VException {
        setValue(MemberHome.KEY_ZIP, inValue);
    }

    public String getCity() {
        return getString(MemberHome.KEY_CITY);
    }

    public void setCity(final String inValue) throws VException {
        setValue(MemberHome.KEY_CITY, inValue);
    }

    public String getPhone() {
        return getString(MemberHome.KEY_PHONE);
    }

    public void setPhone(final String inValue) throws VException {
        setValue(MemberHome.KEY_PHONE, inValue);
    }

    public String getFax() {
        return getString(MemberHome.KEY_FAX);
    }

    public void setFax(final String inValue) throws VException {
        setValue(MemberHome.KEY_FAX, inValue);
    }

    public String getMail() {
        return getString(MemberHome.KEY_MAIL);
    }

    public void setMail(final String inValue) throws VException {
        setValue(MemberHome.KEY_MAIL, inValue);
    }

    public String getSex() {
        return getString(MemberHome.KEY_SEX);
    }

    public void setSex(final String inValue) throws VException {
        setValue(MemberHome.KEY_SEX, inValue);
    }

    public Set<RoleWrapper> getRoles() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return new HashSet<RoleWrapper>(roles.getSelected());
    }

    public void setRoles(final Set<RoleWrapper> inRoles) {
        roles.setSelected(inRoles);
    }

    // ---

    /** Factory method to create Vaadin bean items of member model instances.
     *
     * @param inMember {@link Member} the model the bean instance should be bound to
     * @return BeanItem&lt;MemberBean> the bean instance */
    public static BeanItem<MemberBean> createMemberBean(final Member inMember) {
        return new BeanItem<MemberBean>(new MemberBean(inMember));
    }

    /** Factory method to create Vaadin bean items of member model instances.
     *
     * @param inMember {@link Member} the model the bean instance should be bound to
     * @param inRoles {@link RoleContainer} the member's roles
     * @return BeanItem&lt;MemberBean> the bean instance */
    public static BeanItem<MemberBean> createMemberBean(final Member inMember, final RoleContainer inRoles) {
        return new BeanItem<MemberBean>(new MemberBean(inMember, inRoles));
    }

}
