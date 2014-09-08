/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.GroupContentContainer;
import org.hip.vif.forum.groups.data.GroupContentWrapper;
import org.hip.vif.forum.groups.ui.GroupContentView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/** Task to display the contribution threads of a discussion group.
 *
 * Created on 10.08.2002
 *
 * @author Benno Luthiger */
@UseCaseController
public class GroupShowTask extends AbstractWebController {
    private final static String SORT_ORDER = QuestionHome.KEY_QUESTION_DECIMAL;

    /** @see org.hip.vif.servlets.AbstractVIFTask#needsPermission() */
    @Override
    protected String needsPermission() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public Component runChecked() throws RiplaException {
        try {
            loadContextMenu(Constants.MENU_SET_ID_GROUP_REVIEW);

            final Long lGroupID = getGroupID();
            final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
            final QuestionHierarchyHome lHierarchyHome = BOMHelper.getQuestionHierarchyHome();

            Group lGroup = null;
            QueryResult lResult = null;
            if (getActor().isGuest()) {
                lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
                lResult = BOMHelper.getQuestionForGuestsHome().selectOfGroup(lGroupID, lGroup.getGuestDepth(),
                        createOrder(SORT_ORDER, false));
            }
            else {
                lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
                lResult = lQuestionHome.selectOfGroupPublished(lGroupID, createOrder(SORT_ORDER, false));
            }
            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new GroupContentView(lGroup, GroupContentContainer.createData(lResult,
                    lHierarchyHome.getChildrenChecker(lGroupID), lCodeList, 2), this);
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Callback method for components managed by this task instance.
     *
     * @param inSelection {@link GroupContentWrapper} the selected item of the group's content */
    public void processSelection(final GroupContentWrapper inSelection) {
        setQuestionID(inSelection.getQuestionID());
        sendEvent(QuestionShowTask.class);
    }

}
