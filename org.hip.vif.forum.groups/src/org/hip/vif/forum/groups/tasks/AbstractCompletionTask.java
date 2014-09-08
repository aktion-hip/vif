/**
    This package is part of the application VIF.
    Copyright (C) 2011-2014, Benno Luthiger

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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.ui.CompletionView;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Functionality for completion tasks.
 *
 * @author Luthiger Created: 10.07.2010 */
public abstract class AbstractCompletionTask extends AbstractWebController {

    @Override
    public Component runChecked() throws RiplaException {
        final Long lActorID = getActor().getActorID();
        final IMessages lMessages = Activator.getMessages();
        try {
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(getGroupID());
            if (!lGroup.isActive()) {
                return reDisplay(lMessages.getMessage("errmsg.not.active"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            if (!lGroup.isParticipant(lActorID)) {
                return reDisplay(lMessages.getMessage("errmsg.not.participant"), Type.WARNING_MESSAGE); //$NON-NLS-1$
            }

            final Long lQuestionID = getCompletionsQuestionID();
            final DomainObject lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionID.toString());
            final QueryResult lCompletions = BOMHelper.getJoinCompletionToMemberHome().getAuthorView(lQuestionID,
                    lActorID);

            loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);
            final CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class,
                    getAppLocale().getLanguage());
            return new CompletionView(getCompletionText(), getActCompletionID(), lQuestion, lCompletions, lGroup,
                    lCodeList, this); //$NON-NLS-1$
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Retrieves the completion's text.
     *
     * @return String the completion's text. May be an empty string for a new completion.
     * @throws SQLException
     * @throws VException */
    abstract protected String getCompletionText() throws VException, SQLException;

    /** Retrieves the completion's ID.
     *
     * @return Long the completion's ID. May be 0 for a new completion.
     * @throws VException
     * @throws SQLException */
    abstract protected Long getActCompletionID() throws VException, SQLException;

    /** Retrieves the completion's question.
     *
     * @return Long the question's ID the completion belongs to
     * @throws VException
     * @throws SQLException */
    abstract protected Long getCompletionsQuestionID() throws VException, SQLException;

    /** Callback function for view.
     *
     * @param inCompletion String the inputed completion text.
     * @return boolean <code>true</code> if the completion has been saved successfully */
    abstract public boolean saveCompletion(String inValue);

}
