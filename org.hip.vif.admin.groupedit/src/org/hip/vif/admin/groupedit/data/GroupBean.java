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
package org.hip.vif.admin.groupedit.data;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.web.util.AbstractBean;

import com.vaadin.data.util.BeanItem;

/** This is a POJO bean for the <code>Group</code> model.
 *
 * @author lbenno */
public class GroupBean extends AbstractBean {
    public static final String FN_NAME = "name";
    public static final String FN_DESC = "description";
    public static final String FN_REVIEWERS = "numberOfReviewers";
    public static final String FN_GUEST_DEPTH = "guestDepth";
    public static final String FN_GROUP_SIZE = "minGroupSize";
    public static final String FN_PRIVATE = "private";
    public static final String FN_STATE = "state";

    /** Private constructor.
     *
     * @param inGroup {@link Group} */
    private GroupBean(final Group inGroup) {
        super(inGroup);
    }

    public String getName() {
        return getString(GroupHome.KEY_NAME);
    }

    public void setName(final String inName) throws VException {
        setValue(GroupHome.KEY_NAME, inName);
    }

    public String getDescription() {
        return getString(GroupHome.KEY_DESCRIPTION);
    }

    public void setDescription(final String inDescription) throws VException {
        setValue(GroupHome.KEY_DESCRIPTION, inDescription);
    }

    public Long getNumberOfReviewers() {
        return getLong(GroupHome.KEY_REVIEWERS);
    }

    public void setNumberOfReviewers(final Long inNumberOfReviewers) throws VException {
        setValue(GroupHome.KEY_REVIEWERS, inNumberOfReviewers);
    }

    public Long getGuestDepth() {
        return getLong(GroupHome.KEY_GUEST_DEPTH);
    }

    public void setGuestDepth(final Long inGuestDepth) throws VException {
        setValue(GroupHome.KEY_GUEST_DEPTH, inGuestDepth);
    }

    public Long getMinGroupSize() {
        return getLong(GroupHome.KEY_MIN_GROUP_SIZE);
    }

    public void setMinGroupSize(final Long inMinGroupSize) throws VException {
        setValue(GroupHome.KEY_MIN_GROUP_SIZE, inMinGroupSize);
    }

    public boolean isPrivate() {
        return GroupHome.IS_PRIVATE.equals(getInteger(GroupHome.KEY_PRIVATE));
    }

    public void setPrivate(final boolean inIsPrivate) throws VException {
        setValue(GroupHome.KEY_PRIVATE,
                inIsPrivate ? GroupHome.IS_PRIVATE.longValue() : GroupHome.IS_PUBLIC.longValue());
    }

    public Integer getState() {
        return getInteger(GroupHome.KEY_STATE);
    }

    public void setState(final Integer inState) throws VException {
        setValue(GroupHome.KEY_STATE, inState);
    }

    // ---

    /** Factory method to create Vaadin bean items of group model instances.
     *
     * @param inGroup {@link Group} the model the bean instance should be bound to
     * @return BeanItem&lt;GroupBean> the bean instance */
    public static BeanItem<GroupBean> createGroupBean(final Group inGroup) {
        return new BeanItem<GroupBean>(new GroupBean(inGroup));
    }

}
