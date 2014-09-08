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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification.Type;

/** Task to display the completion's data in an editable form.
 *
 * Created on 13.08.2003
 *
 * @author Luthiger */
@UseCaseController
public class CompletionEditTask extends AbstractCompletionTask {
    private static final Logger LOG = LoggerFactory.getLogger(CompletionEditTask.class);

    private Completion completion;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_QUESTION;
    }

    private Completion getCompletion() throws VException, SQLException {
        if (completion == null) {
            completion = BOMHelper.getCompletionHome().getCompletion(getCompletionID().toString());
        }
        return completion;
    }

    @Override
    protected Long getCompletionsQuestionID() throws VException, SQLException {
        return BeanWrapperHelper.getLong(CompletionHome.KEY_QUESTION_ID, getCompletion());
    }

    @Override
    protected String getCompletionText() throws VException, SQLException {
        return BeanWrapperHelper.getString(CompletionHome.KEY_COMPLETION, getCompletion());
    }

    @Override
    protected Long getActCompletionID() throws VException, SQLException {
        return getCompletionID();
    }

    /** Callback function for view.
     *
     * @param inCompletion String the inputed completion text.
     * @return boolean <code>true</code> if the completion has been saved successfully */
    @Override
    public boolean saveCompletion(final String inCompletion) {
        try {
            getCompletion().ucSave(cleanUp(inCompletion), getActor().getActorID());
            showNotification(
                    Activator.getMessages().getMessage("msg.task.data.changed"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(ContributionsListTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error while saving the completion.", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while saving the completion.", exc); //$NON-NLS-1$
        }
        return false;
    }

}
