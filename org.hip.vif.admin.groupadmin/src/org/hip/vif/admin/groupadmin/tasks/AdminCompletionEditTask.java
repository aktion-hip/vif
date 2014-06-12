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

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.ui.CompletionView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.exc.VIFWebException;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Task to edit a completion.
 * 
 * @author Luthiger Created: 21.11.2011
 */
@UseCaseController
public class AdminCompletionEditTask extends AbstractCompletionTask {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminCompletionEditTask.class);

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_BACK);

			final Long lCompletionID = getCompletionID();
			final Long lQuestionID = getQuestionID();

			// get completions
			final CompletionHome lCompletionHome = BOMHelper
					.getCompletionHome();
			final Completion lCompletion = lCompletionHome
					.getCompletion(lCompletionID);
			final QueryResult lSiblings = lCompletionHome
					.getSiblingCompletions(lQuestionID, lCompletionID);

			// get questions
			final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
					lQuestionID);

			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			return new CompletionView(lCompletion, lSiblings, lQuestion,
					VifBOMHelper.getGroupHome().getGroup(getGroupID()),
					lCodeList, this);
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	/**
	 * Callback method.
	 * 
	 * @param inCompletionText
	 *            String the editor content
	 * @return
	 */
	@Override
	public boolean saveCompletion(final String inCompletionText) {
		try {
			final Completion lCompletion = BOMHelper.getCompletionHome()
					.getCompletion(getCompletionID());
			lCompletion.ucSave(cleanUp(inCompletionText), getActor()
					.getActorID());
			showNotification(Activator.getMessages().getMessage(
					"admin.msg.data.changed")); //$NON-NLS-1$
			sendEvent(AdminQuestionShowTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error("Error while saving the completion!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error("Error while saving the completion!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
