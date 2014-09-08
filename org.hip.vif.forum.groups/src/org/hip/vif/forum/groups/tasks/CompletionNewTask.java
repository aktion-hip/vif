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

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.ripla.annotations.UseCaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification.Type;

/** Task to create a new completion entry.
 *
 * Created on 11.08.2003
 *
 * @author Luthiger */
@UseCaseController
public class CompletionNewTask extends AbstractCompletionTask {
    private static final Logger LOG = LoggerFactory.getLogger(CompletionNewTask.class);

    /** @see org.hip.vif.servlets.AbstractVIFTask#needsPermission() */
    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_NEW_COMPLETION;
    }

    @Override
    protected Long getCompletionsQuestionID() throws VException, SQLException {
        return getQuestionID();
    }

    @Override
    protected String getCompletionText() throws VException, SQLException {
        return ""; //$NON-NLS-1$
    }

    @Override
    protected Long getActCompletionID() throws VException, SQLException {
        return 0l;
    }

    /** Callback function for view.
     *
     * @param inCompletion String the inputed completion text.
     * @return boolean <code>true</code> if the completion has been saved successfully */
    @Override
    public boolean saveCompletion(final String inCompletion) {
        try {
            final Completion lCompletionBOM = (Completion) BOMHelper.getCompletionHome().create();
            lCompletionBOM.ucNew(cleanUp(inCompletion), getQuestionID().toString(), getActor().getActorID());
            showNotification(
                    Activator.getMessages().getMessage("msg.completion.create"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(ContributionsListTask.class);
            return true;
        } catch (final BOMException exc) {
            LOG.error("Error while saving the completion.", exc); //$NON-NLS-1$
        }
        return false;
    }

}
