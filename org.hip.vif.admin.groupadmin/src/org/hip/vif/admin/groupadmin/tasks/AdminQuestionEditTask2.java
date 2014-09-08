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
import java.util.Collections;

import org.hip.kernel.bom.impl.EmptyQueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper.Completion;
import org.hip.vif.admin.groupadmin.ui.QuestionEditor;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Task to edit questions (called from the list of pending contributions).<br />
 * In this case, we display both the actual question (in editable form) as well
 * as the question's context, i.e. the parent question.
 * 
 * @author Luthiger Created: 24.11.2011
 */
@UseCaseController
public class AdminQuestionEditTask2 extends AdminQuestionNewTask {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminQuestionEditTask2.class);

	private Question question = null;

	private Question getQuestion() throws VException, SQLException {
		if (question == null) {
			question = BOMHelper.getQuestionHome().getQuestion(getQuestionID());
		}
		return question;
	}

	@Override
	protected Long getParentQuestionID() throws VException, SQLException {
		final QuestionHierarchyHome lHierarchyHome = BOMHelper
				.getQuestionHierarchyHome();
		if (lHierarchyHome.hasParent(getQuestionID())) {
			return BeanWrapperHelper.getLong(
					QuestionHierarchyHome.KEY_PARENT_ID,
					BOMHelper.getQuestionHierarchyHome().getParent(
							getQuestionID()));
		}
		return 0l;
	}

	@Override
	protected Component handleRootQuestion() throws VException, SQLException {
		final CodeList lCodeList = CodeListHome.instance().getCodeList(
				QuestionState.class, getAppLocale().getLanguage());
		return new QuestionEditor(VifBOMHelper.getGroupHome().getGroup(
				getGroupID()), getQuestion(),
				Collections.<Completion> emptyList(), new EmptyQueryResult(
						BOMHelper.getTextHome()), lCodeList, this);
	}

	@Override
	protected String getQuestionText() throws VException, SQLException {
		return BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION,
				getQuestion());
	}

	@Override
	protected String getRemarkText() throws VException, SQLException {
		return BeanWrapperHelper.getString(QuestionHome.KEY_REMARK,
				getQuestion());
	}

	@Override
	public boolean saveQuestion(final String inQuestion, final String inRemark) {
		try {
			final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
					getQuestionID());
			lQuestion.ucSave(cleanUp(inQuestion), cleanUp(inRemark), getActor()
					.getActorID());
			showNotification(Activator.getMessages().getMessage(
					"admin.msg.data.changed")); //$NON-NLS-1$
			sendEvent(AdminContributionsPublishablesTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error("Error encountered while saving the question!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error("Error encountered while saving the question!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
