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
package org.hip.vif.admin.groupadmin.tasks;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.GroupContentContainer;
import org.hip.vif.admin.groupadmin.data.GroupContentWrapper;
import org.hip.vif.admin.groupadmin.ui.GroupContentView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/**
 * Shows the list of all the group's questions.
 * 
 * @author Luthiger Created: 20.11.2011
 */
@UseCaseController
public class AdminQuestionListTask extends AbstractWebController {

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_QUESTION_NEW;
	}

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);

			final Long lGroupID = getGroupID();
			// If no questions found, run task to create a new question
			if (!BOMHelper.getQuestionHome().hasQuestionsInGroup(lGroupID)) {
				setQuestionID(0l);
				return forwardTo(AdminQuestionNewTask.class);
			}
			final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
			final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
			final QuestionHierarchyHome lHierarchyHome = BOMHelper
					.getQuestionHierarchyHome();
			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			return new GroupContentView(lGroup,
					GroupContentContainer.createData(lQuestionHome
							.selectOfGroupFiltered(
									lGroupID,
									createOrder(
											QuestionHome.KEY_QUESTION_DECIMAL,
											false), getActor().getActorID()),
							lHierarchyHome.getChildrenChecker(lGroupID),
							lCodeList, 2), this);
		}
		catch (final Exception exc) {
			throw createContactAdminException(exc);
		}
	}

	/**
	 * Callback method
	 * 
	 * @param inItemId
	 */
	public void processSelection(final GroupContentWrapper inItemId) {
		setQuestionID(inItemId.getQuestionID());
		sendEvent(AdminQuestionShowTask.class);
	}

}
