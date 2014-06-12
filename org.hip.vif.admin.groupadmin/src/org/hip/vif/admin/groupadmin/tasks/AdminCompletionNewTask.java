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

import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.ui.CompletionView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.exc.VIFWebException;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Creates a new completion for a question in the discussion group.
 * 
 * @author Luthiger Created: 23.11.2011
 */
@UseCaseController
public class AdminCompletionNewTask extends AbstractCompletionTask {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminCompletionNewTask.class);

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_CONTRIBUTION_EDIT);

			final Long lQuestionID = getQuestionID();
			final DomainObject lQuestion = BOMHelper.getQuestionHome()
					.getQuestion(lQuestionID.toString());
			final QueryResult lCompletions = BOMHelper
					.getJoinCompletionToMemberHome().getAuthorView(lQuestionID,
							getActor().getActorID());

			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			return new CompletionView(
					"", 0l, lQuestion, lCompletions, VifBOMHelper.getGroupHome().getGroup(getGroupID()), lCodeList, this); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	@Override
	public boolean saveCompletion(final String inCompletionText) {
		try {
			final Completion lCompletion = (Completion) BOMHelper
					.getCompletionHome().create();
			lCompletion.ucNew(cleanUp(inCompletionText), getQuestionID()
					.toString(), getActor().getActorID());
			showNotification(Activator.getMessages().getMessage(
					"admin.msg.completion.create")); //$NON-NLS-1$
			sendEvent(AdminQuestionShowTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error("Error encountered while creating a new completion!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
