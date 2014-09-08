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

package org.hip.vif.admin.groupedit.data;

import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.IGroup;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.util.BeanWrapperHelper;

/** Wrapper class (i.e. adapter) for group model instances.
 *
 * @author Luthiger Created: 06.11.2011 */
public class GroupWrapper implements ISelectableBean {
    private final Long groupID;
    private final String name;
    private final String description;
    private final Integer numberOfReviewers;
    private final Integer guestDepth;
    private final Integer minGroupSize;
    private final String state;
    private final boolean isDeletable;
    private boolean checked;

    private GroupWrapper(final GeneralDomainObject inDomainObject, final CodeList inCodeList,
            final QuestionHome inQuestionHome) throws VException, SQLException {
        groupID = BeanWrapperHelper.getLong(GroupHome.KEY_ID, inDomainObject);
        name = BeanWrapperHelper.getString(GroupHome.KEY_NAME, inDomainObject);
        description = BeanWrapperHelper.getString(GroupHome.KEY_DESCRIPTION, inDomainObject);
        numberOfReviewers = BeanWrapperHelper.getInteger(GroupHome.KEY_REVIEWERS, inDomainObject);
        guestDepth = BeanWrapperHelper.getInteger(GroupHome.KEY_GUEST_DEPTH, inDomainObject);
        minGroupSize = BeanWrapperHelper.getInteger(GroupHome.KEY_MIN_GROUP_SIZE, inDomainObject);
        state = inCodeList.getLabel(BeanWrapperHelper.getString(GroupHome.KEY_STATE, inDomainObject));
        isDeletable = ((IGroup) inDomainObject).isDeletable() && !inQuestionHome.hasQuestionsInGroup(groupID);
    }

    /** Factory method, creates an adapted instance of the group model.
     * 
     * @param inGroup {@link GeneralDomainObject}
     * @param inCodeList {@link CodeList}
     * @param inQuestionHome {@link QuestionHome}
     * @return {@link GroupWrapper}
     * @throws SQLException
     * @throws VException */
    public static GroupWrapper createItem(final GeneralDomainObject inGroup, final CodeList inCodeList,
            final QuestionHome inQuestionHome) throws VException, SQLException {
        return new GroupWrapper(inGroup, inCodeList, inQuestionHome);
    }

    public Long getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNumberOfReviewers() {
        return numberOfReviewers;
    }

    public Integer getGuestDepth() {
        return guestDepth;
    }

    public Integer getMinGroupSize() {
        return minGroupSize;
    }

    public String getState() {
        return state;
    }

    public boolean getIsDeletable() {
        return isDeletable;
    }

    @Override
    public void setChecked(final boolean inChecked) {
        checked = inChecked;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

}
