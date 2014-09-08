/**
 This package is part of the application VIF.
 Copyright (C) 2010-2014, Benno Luthiger

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

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.forum.groups.Activator;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification.Type;

/** Task to create a new question entry.
 *
 * Created on 11.08.2003
 *
 * @author Luthiger */
@UseCaseController
public class QuestionNewTask extends AbstractQuestionTask {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionNewTask.class);

    @Override
    protected String getQuestionText() throws VException, SQLException {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected String getRemarkText() throws VException, SQLException {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected Long getParentQuestionID() throws VException, SQLException {
        return getQuestionID();
    }

    @Override
    public boolean saveQuestion(final String inQuestion, final String inRemark) {
        try {
            final Question lQuestionBOM = (Question) BOMHelper.getQuestionHome().create();
            lQuestionBOM.ucNew(cleanUp(inQuestion), cleanUp(inRemark), getQuestionID(), getGroupID().toString(),
                    getActor().getActorID());
            showNotification(
                    Activator.getMessages().getMessage("msg.question.create"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(ContributionsListTask.class);
            return true;
        } catch (final BOMException exc) {
            LOG.error("Error while saving the completion.", exc); //$NON-NLS-1$
        }
        return false;
    }

}
