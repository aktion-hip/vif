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
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.ui.QuestionEditor;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Functionality for question tasks.
 *
 * @author Luthiger Created: 09.07.2010 */
public abstract class AbstractQuestionTask extends AbstractWebController {

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_EDIT_QUESTION;
    }

    /** Callback function for view.
     *
     * @param inQuestion String
     * @param inRemark String
     * @return boolean <code>true</code> if the completion has been saved successfully */
    abstract public boolean saveQuestion(String inQuestion, String inRemark);

    @Override
    public Component runChecked() throws RiplaException {
        final Long lActorID = getActor().getActorID();
        final IMessages lMessages = Activator.getMessages();
        try {
            final Long lGroupID = getGroupID();
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
            if (!lGroup.isActive()) {
                return reDisplay(lMessages.getMessage("errmsg.not.active"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            if (!lGroup.isParticipant(lActorID)) {
                return reDisplay(lMessages.getMessage("errmsg.not.participant"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            final Long lParentID = getParentQuestionID();
            Question lParentQuestion = null;
            if (lParentID != 0) {
                lParentQuestion = BOMHelper.getQuestionHome().getQuestion(lParentID.toString());
            }
            if (lParentQuestion == null) {
                throw new VIFWebException("Unable to retrieve parent question with ID=" + lParentID.toString());
            }

            if (!WorkflowAwareContribution.STATE_OPEN.equals(lParentQuestion.get(QuestionHome.KEY_STATE).toString())) {
                return reDisplay(lMessages.getMessage("errmsg.not.open"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            loadContextMenu(Constants.MENU_SET_ID_EDIT);
            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new QuestionEditor(getQuestionText(), getRemarkText(),
                    lParentQuestion, lGroup,
                    getPublishedCompletions(lParentID),
                    getPublishedBibliography(lParentID),
                    getAuthors(lParentID.toString()),
                    getReviewers(lParentID.toString()),
                    lCodeList, this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    abstract protected Long getParentQuestionID() throws VException, SQLException;

    abstract protected String getQuestionText() throws VException, SQLException;

    abstract protected String getRemarkText() throws VException, SQLException;

    private QueryResult getPublishedCompletions(final Long inQuestionID)
            throws NumberFormatException, VException, SQLException {
        return BOMHelper.getJoinCompletionToMemberHome().selectPublished(inQuestionID);
    }

    private QueryResult getPublishedBibliography(final Long inQuestionID) throws VException,
            SQLException {
        return VifBOMHelper.getJoinQuestionToTextHome().selectPublished(inQuestionID);
    }

    private QueryResult getAuthors(final String inQuestionID) throws VException, SQLException {
        return BOMHelper.getJoinQuestionToAuthorReviewerHome().getAuthors(inQuestionID);
    }

    private QueryResult getReviewers(final String inQuestionID) throws VException, SQLException {
        return BOMHelper.getJoinQuestionToAuthorReviewerHome().getReviewers(inQuestionID);
    }

}
