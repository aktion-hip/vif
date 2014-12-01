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

package org.hip.vif.forum.usersettings.data;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.SubscriptionHome;
import org.hip.vif.core.bom.impl.JoinSubscriptionToQuestionHome;
import org.hip.vif.core.interfaces.ISelectableBean;
import org.hip.vif.web.util.BeanWrapperHelper;

/** Bean class (adapter) for subscription models.
 *
 * @author Luthiger Created: 20.12.2011 */
public class SubscriptionBean implements ISelectableBean {
    private final Long qustionID;
    private final Long groupID;
    private final String questionDecimal;
    private final String questionText;
    private boolean local;
    private boolean checked;

    private SubscriptionBean(final GeneralDomainObject inDomainObject) {
        qustionID = BeanWrapperHelper.getLong(JoinSubscriptionToQuestionHome.KEY_ALIAS_QUESTION_ID, inDomainObject);
        groupID = BeanWrapperHelper.getLong(QuestionHome.KEY_GROUP_ID, inDomainObject);
        questionDecimal = BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inDomainObject);
        questionText = BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION, inDomainObject);
        local = BeanWrapperHelper.getInteger(SubscriptionHome.KEY_LOCAL, inDomainObject) != 0;
    }

    /** Factory method, creation the instance.
     * 
     * @param inDomainObject {@link GeneralDomainObject}
     * @return {@link SubscriptionBean} */
    public static SubscriptionBean createItem(final GeneralDomainObject inDomainObject) {
        return new SubscriptionBean(inDomainObject);
    }

    public Long getQustionID() {
        return qustionID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public String getQuestionDecimal() {
        return questionDecimal;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean getLocal() {
        return local;
    }

    public void setLocal(final boolean inLocal) {
        local = inLocal;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(final boolean inChecked) {
        checked = inChecked;
    }

}
