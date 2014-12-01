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

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper;
import org.hip.vif.admin.groupadmin.ui.QuestionEditor;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

/** Task to edit questions (called from edit button on <code>QuestionView</code> ).<br />
 * The display should resemble as much as possible the question show view. We only set the question and remark texts in
 * editable fields.
 *
 * @author Luthiger Created: 22.11.2011 */
@UseCaseController
public class AdminQuestionEditTask extends AbstractQuestionTask {
    private static final Logger LOG = LoggerFactory
            .getLogger(AdminQuestionEditTask.class);

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            loadContextMenu(Constants.MENU_SET_ID_BACK);

            final Long lQuestionID = getQuestionID();

            final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
                    lQuestionID);

            // only edit question if it is private
            if (((VIFWorkflowAware) lQuestion).isPrivate()) {
                final CodeList lCodeList = CodeListHome.instance().getCodeList(
                        QuestionState.class, getAppLocale().getLanguage());

                return new QuestionEditor(VifBOMHelper.getGroupHome().getGroup(
                        getGroupID()), lQuestion,
                        CompletionsHelper.getNormalizedCompletions(BOMHelper
                                .getJoinCompletionToMemberHome().getFiltered(
                                        lQuestionID, getActor().getActorID())),
                        VifBOMHelper.getJoinQuestionToTextHome().selectPublished(
                                lQuestionID), lCodeList, this);
            }
            showNotification(
                    Activator.getMessages().getMessage("errmsg.question.published"), Notification.Type.WARNING_MESSAGE); //$NON-NLS-1$
            return forwardTo(AdminQuestionShowTask.class);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Callback method, saves the changes.
     * 
     * @param inQuestion String
     * @param inRemark String
     * @return boolean <code>true</code> if successful */
    @Override
    public boolean saveQuestion(final String inQuestion, final String inRemark) {
        try {
            final Question lQuestion = BOMHelper.getQuestionHome().getQuestion(
                    getQuestionID());
            lQuestion.ucSave(cleanUp(inQuestion), cleanUp(inRemark), getActor()
                    .getActorID());
            sendEvent(AdminQuestionShowTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while saving the question!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while saving the question!", exc); //$NON-NLS-1$
        }
        return false;
    }

}
