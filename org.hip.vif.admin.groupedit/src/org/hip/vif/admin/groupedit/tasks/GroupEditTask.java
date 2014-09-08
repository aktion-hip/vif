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

package org.hip.vif.admin.groupedit.tasks;

import java.sql.SQLException;
import java.util.Vector;

import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.data.MemberContainer;
import org.hip.vif.admin.groupedit.data.ParticipantBean;
import org.hip.vif.admin.groupedit.ui.GroupView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.core.exc.ExternIDNotUniqueException;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.hip.vif.web.util.MemberBean;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.ripla.interfaces.IMessages;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Task to edit a group, e.g. to change the group's values as well as its state.
 *
 * @author Luthiger Created: 08.11.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class GroupEditTask extends AbstractGroupTask implements
        Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory
            .getLogger(GroupEditTask.class);

    private int minGroupSizeBefore;
    private String groupNameBefore;
    private MemberContainer admins;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_GROUPS_EDIT;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        loadContextMenu(Constants.MENU_SET_ID_GROUP_SHOW);

        try {
            final Long lGroupID = getGroupID();
            final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
            groupNameBefore = lGroup.get(GroupHome.KEY_NAME).toString();
            minGroupSizeBefore = lGroup.getMinGroupSize();
            admins = MemberContainer.createData(BOMHelper
                    .getJoinGroupAdminToMemberHome().select(lGroupID));

            final CodeList lCodeList = CodeListHome.instance().getCodeList(
                    GroupState.class, getAppLocale().getLanguage());
            return new GroupView(lGroup, BOMHelper.getParticipantHome()
                    .getParticipantsOfGroup(lGroupID),
                    lCodeList.getLabel(BeanWrapperHelper.getString(
                            GroupHome.KEY_STATE, lGroup)), admins, this);
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    @Override
    public boolean save(final Group inGroup) throws ExternIDNotUniqueException {
        try {
            inGroup.ucSave(groupNameBefore, minGroupSizeBefore);
            showNotification(
                    Activator.getMessages().getMessage("admin.group.edit.data.saved"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(GroupEditTask.class);
            return true;
        } catch (final ExternIDNotUniqueException exc) {
            throw exc;
        } catch (final VException exc) {
            LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
        } catch (final WorkflowException exc) {
            LOG.error("Error while saving the member data.", exc); //$NON-NLS-1$
        }
        return false;
    }

    // Callback methods, see the view's TransitionRenderer.
    public boolean groupOpen() {
        return doTransition(VIFGroupWorkflow.TRANS_OPEN);
    }

    public boolean groupClose() {
        try {
            final Group lGroup = VifBOMHelper.getGroupHome()
                    .getGroup(getGroupID());
            return doTransition(lGroup.getCloseTransition());
        } catch (final VException exc) {
            LOG.error("Error encountered during a group's state transition!", exc); //$NON-NLS-1$
        }
        return false;
    }

    public boolean groupSuspend() {
        return doTransition(VIFGroupWorkflow.TRANS_SUSPEND);
    }

    public boolean groupReactivate() {
        try {
            final Group lGroup = VifBOMHelper.getGroupHome()
                    .getGroup(getGroupID());
            return doTransition(lGroup.getReactivateTransition());
        } catch (final VException exc) {
            LOG.error("Error encountered during a group's state transition!", exc); //$NON-NLS-1$
        }
        return false;
    }

    public boolean groupDeactivate() {
        return doTransition(VIFGroupWorkflow.TRANS_DEACTIVATE);
    }

    private boolean doTransition(final String inTransition) {
        try {
            final Group lGroup = VifBOMHelper.getGroupHome()
                    .getGroup(getGroupID());
            final GroupStateChangeParameters lStateChange = new GroupStateChangeParameters();
            ((WorkflowAware) lGroup).doTransition(inTransition,
                    new Object[] { lStateChange });

            // ask the administrator to notify the participants about the state
            // change
            if (lStateChange.doNotification()) {
                final ParameterObject lParameters = new ParameterObject();
                lParameters.set(Constants.KEY_PARAMETER_GROUP_NAME,
                        lStateChange.getGroupName());
                lParameters.set(Constants.KEY_PARAMETER_SUBJECT,
                        lStateChange.getMailSubject());
                lParameters.set(Constants.KEY_PARAMETER_BODY,
                        lStateChange.getMailBody());
                setParameters(lParameters);
                showNotification(
                        Activator.getMessages().getMessage("admin.group.state.change"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                sendEvent(GroupStateChangeNotificationTask.class);
            } else {
                // else show list of groups
                showNotification(
                        Activator.getMessages().getMessage("admin.group.edit.data.saved"), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
                sendEvent(GroupShowListTask.class);
            }
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered during a group's state transition!", exc); //$NON-NLS-1$
        } catch (final WorkflowException exc) {
            LOG.error("Error encountered during a group's state transition!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Callback method to manage the group's administration.
     *
     * @return */
    public boolean selectAdmin() {
        try {
            final IMessages lMessages = Activator.getMessages();
            final String lGroupName = BeanWrapperHelper.getString(
                    GroupHome.KEY_NAME,
                    VifBOMHelper.getGroupHome().getGroup(getGroupID()));
            final ParameterObject lParameters = new ParameterObject();
            lParameters.set(Constants.KEY_PARAMETER_TITLE_SEARCH, lMessages
                    .getFormattedMessage("ui.group.lookup.search1.title.page", lGroupName)); //$NON-NLS-1$
            lParameters.set(Constants.KEY_PARAMETER_SUBTITLE_SELECT, lMessages
                    .getFormattedMessage("ui.group.lookup.sub1.title.page", lGroupName)); //$NON-NLS-1$
            lParameters.set(Constants.KEY_PARAMETER_RIGHT_COLUMN, lMessages
                    .getFormattedMessage("ui.group.lookup.selected.admin", lGroupName)); //$NON-NLS-1$
            lParameters.set(Constants.KEY_PARAMETER_SELECTED,
                    admins.getItemIds());
            lParameters.set(Constants.KEY_PARAMETER_PROCESSING,
                    createFullyQualifiedControllerName(GroupAdminSelectTask.class));
            setParameters(lParameters);
            requestLookup(LinkButtonHelper.LookupType.MEMBER_SEARCH, 0l);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while selecting the group administration!", exc); //$NON-NLS-1$
        }
        return false;
    }

    /** Callback method to manage a private group's participants.
     *
     * @return */
    public boolean selectParticipants() {
        try {
            final IMessages lMessages = Activator.getMessages();
            final String lGroupName = BeanWrapperHelper.getString(
                    GroupHome.KEY_NAME,
                    VifBOMHelper.getGroupHome().getGroup(getGroupID()));
            final ParameterObject lParameters = new ParameterObject();
            lParameters.set(Constants.KEY_PARAMETER_TITLE_SEARCH, lMessages
                    .getFormattedMessage("ui.group.lookup.search2.title.page", lGroupName)); //$NON-NLS-1$
            lParameters.set(Constants.KEY_PARAMETER_SUBTITLE_SELECT, lMessages
                    .getFormattedMessage("ui.group.lookup.sub2.title.page", lGroupName)); //$NON-NLS-1$
            lParameters
                    .set(Constants.KEY_PARAMETER_RIGHT_COLUMN,
                            lMessages
                                    .getFormattedMessage("ui.group.lookup.selected.participants", lGroupName)); //$NON-NLS-1$
            lParameters.set(Constants.KEY_PARAMETER_SELECTED,
                    new Vector<ParticipantBean>());
            lParameters.set(Constants.KEY_PARAMETER_PROCESSING,
                    createFullyQualifiedControllerName(ParticipantSelectTask.class));
            setParameters(lParameters);
            requestLookup(LinkButtonHelper.LookupType.MEMBER_SEARCH, 0l);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while selecting the group participants!", exc); //$NON-NLS-1$
        }
        return false;
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lAdmin = inEvent.getProperty().getValue();
        if (lAdmin instanceof MemberBean) {
            requestLookup(LookupType.MEMBER,
                    ((MemberBean) lAdmin).getMemberID());
        }
    }

    /** Callback method, delete the selected admins.
     *
     * @return boolean */
    public boolean deleteAdmin() {
        try {
            final Long lGroupID = getGroupID();
            for (final MemberBean lAdmin : admins.getItemIds()) {
                if (lAdmin.isChecked()) {
                    deleteGroupAdmin(lGroupID, lAdmin.getMemberID());
                }
            }
            sendEvent(GroupEditTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while deleting the group administration!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while deleting the group administration!", exc); //$NON-NLS-1$
        }
        return false;
    }

}
