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
import org.hip.vif.admin.groupadmin.ui.QuestionEditor;
import org.hip.vif.core.bom.BOMHelper;
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
 * Creates a new question for a discussion group.
 * 
 * @author Luthiger Created: 23.11.2011
 */
@UseCaseController
public class AdminQuestionNewTask extends AbstractQuestionTask {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminQuestionNewTask.class);

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			loadContextMenu(Constants.MENU_SET_ID_CONTRIBUTION_EDIT);

			final Long lParentID = getParentQuestionID();
			if (lParentID == 0) {
				return handleRootQuestion();
			}

			// create follow up question
			final CodeList lCodeList = CodeListHome.instance().getCodeList(
					QuestionState.class, getAppLocale().getLanguage());
			return new QuestionEditor(cleanUp(getQuestionText()),
					cleanUp(getRemarkText()), BOMHelper.getQuestionHome()
							.getQuestion(lParentID), VifBOMHelper
							.getGroupHome().getGroup(getGroupID()),
					getPublishedCompletions(lParentID),
					getPublishedBibliography(lParentID),
					getAuthors(lParentID.toString()),
					getReviewers(lParentID.toString()), lCodeList, this);
		}
		catch (final NumberFormatException exc) {
			throw createContactAdminException(exc);
		}
		catch (final SQLException exc) {
			throw createContactAdminException(exc);
		}
		catch (final VException exc) {
			throw new VIFWebException(exc);
		}
	}

	protected Component handleRootQuestion() throws VException, SQLException {
		return new QuestionEditor(VifBOMHelper.getGroupHome().getGroup(
				getGroupID()), this);
	}

	protected Long getParentQuestionID() throws VException, SQLException {
		return getQuestionID();
	}

	protected String getQuestionText() throws VException, SQLException {
		return ""; //$NON-NLS-1$
	}

	protected String getRemarkText() throws VException, SQLException {
		return ""; //$NON-NLS-1$
	}

	private QueryResult getPublishedCompletions(final Long inQuestionID)
			throws NumberFormatException, VException, SQLException {
		return BOMHelper.getJoinCompletionToMemberHome().selectPublished(
				inQuestionID);
	}

	private QueryResult getPublishedBibliography(final Long inQuestionID)
			throws VException, SQLException {
		return BOMHelper.getJoinQuestionToTextHome().selectPublished(
				inQuestionID);
	}

	private QueryResult getAuthors(final String inQuestionID)
			throws VException, SQLException {
		return BOMHelper.getJoinQuestionToAuthorReviewerHome().getAuthors(
				inQuestionID);
	}

	private QueryResult getReviewers(final String inQuestionID)
			throws VException, SQLException {
		return BOMHelper.getJoinQuestionToAuthorReviewerHome().getReviewers(
				inQuestionID);
	}

	@Override
	public boolean saveQuestion(final String inQuestion, final String inRemark) {
		try {
			final Question lQuestion = (Question) BOMHelper.getQuestionHome()
					.create();
			lQuestion.ucNew(cleanUp(inQuestion), cleanUp(inRemark),
					getParentQuestionID(), getGroupID().toString(), getActor()
							.getActorID());
			showNotification(Activator.getMessages().getMessage(
					"admin.msg.question.create")); //$NON-NLS-1$
			sendEvent(AdminQuestionListTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error("Error encountered while creating the question!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error("Error encountered while creating the question!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
