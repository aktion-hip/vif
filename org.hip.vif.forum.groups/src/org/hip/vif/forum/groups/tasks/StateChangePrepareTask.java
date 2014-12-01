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
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFMember;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.QuestionStateChecker;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.mail.RequestForStateChangeMail;
import org.hip.vif.forum.groups.ui.StateChangeView;
import org.hip.vif.web.bom.QuestionStateChangeHelper;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.web.util.ControllerStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Task to prepare a state change, e.g. to set an open question answered or to reopen an answered question. Group
 * Administrators can trigger the short cut version of the state change whereas ordinary participants have the requests
 * the state change.
 *
 * @author Benno Luthiger Created on Apr 14, 2005 */
@UseCaseController
public class StateChangePrepareTask extends AbstractWebController { // extends AbstractGroupsTaks {
    private static final Logger LOG = LoggerFactory.getLogger(StateChangePrepareTask.class);

    private AbstractChangeProcessor stateChangeProcessor;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_STATE_CHANGE_REQUEST;
    }

    @Override
    public Component runChecked() throws RiplaException {
        try {
            loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);

            final Long lGroupID = getGroupID();
            final Long lQuestionID = getQuestionID();
            final Question lQuestion = retrieveQuestion(lQuestionID);

            if (isAnswered(lQuestion)) {
                if (isGroupAdmin(getActor().getActorID(), lGroupID)) {
                    stateChangeProcessor = new SetReopenProcessor();
                    return new StateChangeView(lQuestion, "ui.state.change.title.reopen", //$NON-NLS-1$
                            "ui.state.change.label.reopen.change", //$NON-NLS-1$
                            "ui.state.change.button.reopen", //$NON-NLS-1$
                            this);
                }
                else {
                    stateChangeProcessor = new RequestReopenProcessor();
                    return new StateChangeView(lQuestion, "ui.state.change.title.reopen", //$NON-NLS-1$
                            "ui.state.change.label.reopen.request", //$NON-NLS-1$
                            "ui.state.change.button.reopen", //$NON-NLS-1$
                            this);
                }
            }
            else {
                if (isOpen(lQuestion)) {
                    if (isAnswerable(lQuestionID)) {
                        if (isGroupAdmin(getActor().getActorID(), lGroupID)) {
                            stateChangeProcessor = new SetAnsweredProcessor();
                            return new StateChangeView(lQuestion, "ui.state.change.title.answered", //$NON-NLS-1$
                                    "ui.state.change.label.answered.change", //$NON-NLS-1$
                                    "ui.state.change.button.answered", //$NON-NLS-1$
                                    this);
                        }
                        else {
                            stateChangeProcessor = new RequestAnsweredProcessor();
                            return new StateChangeView(lQuestion, "ui.state.change.title.answered", //$NON-NLS-1$
                                    "ui.state.change.label.answered.request", //$NON-NLS-1$
                                    "ui.state.change.button.answered", //$NON-NLS-1$
                                    this);
                        }
                    }
                    else {
                        return noAction("msg.question.unanswered.followups"); //$NON-NLS-1$
                    }
                }
                else {
                    return noAction("msg.question.not.open"); //$NON-NLS-1$
                }
            }
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    private boolean isAnswered(final Question inQuestion) throws GettingException, VException, SQLException {
        return WorkflowAwareContribution.STATE_ANSWERED.equals(inQuestion.get(QuestionHome.KEY_STATE).toString());
    }

    private boolean isOpen(final Question inQuestion) throws GettingException, VException, SQLException {
        return WorkflowAwareContribution.STATE_OPEN.equals(inQuestion.get(QuestionHome.KEY_STATE).toString());
    }

    private boolean isAnswerable(final Long inQuestionID) {
        return QuestionStateChecker.checkStateOfChilds(inQuestionID, WorkflowAwareContribution.STATES_ANSWERED);
    }

    private boolean isGroupAdmin(final Long inActorID, final Long inGroupID) throws VException {
        return BOMHelper.getGroupAdminHome().isGroupAdmin(inActorID, inGroupID);
    }

    private Component noAction(final String inMsgKey) throws RiplaException {
        showNotification(Activator.getMessages().getMessage(inMsgKey), Type.WARNING_MESSAGE);
        final ControllerStack lControllerStack = ControllerStack.getControllerStack();
        lControllerStack.pop();
        return lControllerStack.peek().run();
    }

    private Question retrieveQuestion(final Long inQuestionID) throws VException, SQLException {
        return BOMHelper.getQuestionHome().getQuestion(inQuestionID.toString());
    }

    /** Callback from view: process the state change.
     *
     * @return boolean <code>true</code> if the action could be successfully processed */
    public boolean processStateChange() {
        try {
            stateChangeProcessor.doTransition();
            stateChangeProcessor.sendNotification();
            stateChangeProcessor.redisplayQuestion();
            stateChangeProcessor.showSuccess();
            return true;
        } catch (final Exception exc) {
            LOG.error("Error encountered while trying to change the question state.", exc); //$NON-NLS-1$
        }
        return false;
    }

    // --- inner classes ---

    private abstract class AbstractChangeProcessor {
        void doTransition() throws WorkflowException, VException, SQLException {
        };

        void sendNotification() throws Exception {
        };

        void redisplayQuestion() {
            sendEvent(QuestionShowTask.class);
        };

        void showSuccess() {
            showNotification(Activator.getMessages().getMessage(getSuccessMsgKey()),
                    Type.TRAY_NOTIFICATION); //$NON-NLS-1$
        }

        protected Object[] getArgs() {
            return new Object[] { getActor().getActorID() };
        }

        protected String getSuccessMsgKey() {
            return "msg.state.change.changed"; //$NON-NLS-1$
        }
    }

    // a group admin can change the state of an open question to answered
    private class SetAnsweredProcessor extends AbstractChangeProcessor {
        @Override
        void doTransition() throws WorkflowException, VException, SQLException {
            final QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(BOMHelper.getQuestionHome(),
                    VifBOMHelper.getGroupHome());
            lHelper.setAnsweredFromOpen((WorkflowAwareContribution) retrieveQuestion(getQuestionID()), getArgs());
        }
    }

    // a group admin can change a question's answered state to open
    private class SetReopenProcessor extends AbstractChangeProcessor {
        @Override
        void doTransition() throws WorkflowException, VException, SQLException {
            final QuestionStateChangeHelper lHelper = new QuestionStateChangeHelper(BOMHelper.getQuestionHome(),
                    VifBOMHelper.getGroupHome());
            lHelper.setOpenFromAnswered((WorkflowAwareContribution) retrieveQuestion(getQuestionID()), getArgs());
        }
    }

    // a participant can request a state change, i.e. send a mail to the group admin
    private abstract class AbstractRequestProcessor extends AbstractChangeProcessor {
        @Override
        void sendNotification() throws Exception {
            final Long lGroupID = getGroupID();
            final Member lActor = BOMHelper.getMemberCacheHome().getMember(getActor().getActorID());
            final QueryResult lAdmins = BOMHelper.getJoinGroupAdminToMemberHome().select(lGroupID);

            VIFMember lFirst = null;
            if (lAdmins.hasMoreElements()) {
                lFirst = (VIFMember) lAdmins.next();
            }

            // pre
            if (lFirst == null)
                return;

            final Collection<GeneralDomainObject> lAdditional = new Vector<GeneralDomainObject>();
            while (lAdmins.hasMoreElements()) {
                lAdditional.add(lAdmins.next());
            }

            final Question lQuestion = retrieveQuestion(getQuestionID());
            final String lGroupName = VifBOMHelper.getGroupHome().getGroup(lGroupID).get(GroupHome.KEY_NAME).toString();
            final String lMessage = Activator.getMessages().getFormattedMessage(getMsgKey(),
                    ((VIFMember) lActor).getFullName(),
                    lQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL),
                    lQuestion.get(QuestionHome.KEY_QUESTION));
            final RequestForStateChangeMail lMail = new RequestForStateChangeMail(lFirst, lAdditional,
                    (VIFMember) lActor, lGroupName, lMessage);
            lMail.send();
        }

        @Override
        protected String getSuccessMsgKey() {
            return "msg.state.change.requested"; //$NON-NLS-1$
        }

        protected abstract String getMsgKey();
    }

    // request state change to answered
    private class RequestAnsweredProcessor extends AbstractRequestProcessor {
        @Override
        void doTransition() throws WorkflowException, VException, SQLException {
            ((WorkflowAwareContribution) retrieveQuestion(getQuestionID())).doTransition(
                    WorkflowAwareContribution.TRANS_REQUEST_ANSWERED, getArgs());
        }

        @Override
        protected String getMsgKey() {
            return "msg.state.change.mail.answered"; //$NON-NLS-1$
        }
    }

    // request state change: reopen
    private class RequestReopenProcessor extends AbstractRequestProcessor {
        @Override
        void doTransition() throws WorkflowException, VException, SQLException {
            ((WorkflowAwareContribution) retrieveQuestion(getQuestionID())).doTransition(
                    WorkflowAwareContribution.TRANS_REQUEST_REOPEN, getArgs());
        }

        @Override
        protected String getMsgKey() {
            return "msg.state.change.mail.reopen"; //$NON-NLS-1$
        }
    }

}