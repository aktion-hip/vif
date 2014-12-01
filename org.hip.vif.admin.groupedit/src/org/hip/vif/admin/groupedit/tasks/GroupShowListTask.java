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
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.data.GroupContainer;
import org.hip.vif.admin.groupedit.data.GroupWrapper;
import org.hip.vif.admin.groupedit.ui.GroupListView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Show the list of all discussion groups for that they can be edited.
 *
 * @author Luthiger Created: 06.11.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class GroupShowListTask extends AbstractWebController implements Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(GroupShowListTask.class);

    private static final String SORT_ORDER = GroupHome.KEY_ID;
    private GroupContainer groups;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_GROUPS_EDIT;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        loadContextMenu(Constants.MENU_SET_ID_DEFAULT);

        final GroupHome lGroupHome = VifBOMHelper.getGroupHome();
        try {
            final CodeList lCodeList = CodeListHome.instance().getCodeList(GroupState.class,
                    getAppLocale().getLanguage());
            final QueryResult lResult = lGroupHome.selectForAdministration(getActor().getActorID(),
                    createOrder(SORT_ORDER, true));
            groups = GroupContainer.createData(lResult, lCodeList);
            return new GroupListView(groups, this);
        } catch (final Exception exc) {
            throw createContactAdminException(exc);
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lGroup = inEvent.getProperty().getValue();
        if (lGroup instanceof GroupWrapper) {
            setGroupID(((GroupWrapper) lGroup).getGroupID());
            sendEvent(GroupEditTask.class);
        }
    }

    /** Callback method, deleted the selected groups.
     *
     * @return boolean <code>true</code> if the process was successful */
    public boolean deleteGroups() {
        try {
            final Collection<String> lUndeletable = new Vector<String>();
            final Collection<String> lDeleted = new Vector<String>();
            final GroupHome lHome = VifBOMHelper.getGroupHome();
            final QuestionHome lQuestionHome = BOMHelper.getQuestionHome();
            for (final GroupWrapper lGroup : groups.getItemIds()) {
                if (lGroup.isChecked()) {
                    final Long lGroupID = lGroup.getGroupID();
                    final Group lGroupModel = lHome.getGroup(lGroupID);
                    if (checkDeletable(lGroupModel, lGroupID, lQuestionHome)) {
                        deleteGroupAndAdmins(lGroupID);
                        lDeleted.add(lGroup.getName());
                    }
                    else {
                        lUndeletable.add(lGroup.getName());
                    }
                }
            }
            if (lUndeletable.size() > 0) {
                showNotification(
                        Activator.getMessages().getFormattedMessage("errmsg.groups.nodelete", renderList(lUndeletable)), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            }
            else {
                if (lDeleted.size() > 0) {
                    final String lKey = lDeleted.size() == 1 ? "admin.group.edit.delete.ok1" : "admin.group.edit.delete.okP"; //$NON-NLS-1$ //$NON-NLS-2$
                    showNotification(Activator.getMessages().getFormattedMessage(lKey, renderList(lDeleted)),
                            Type.TRAY_NOTIFICATION);
                }
            }
            sendEvent(GroupShowListTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error while deleting a group entry.", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error while deleting a group entry.", exc); //$NON-NLS-1$
        }
        return false;
    }

    private StringBuilder renderList(final Collection<String> inGroups) {
        boolean isFirst = true;
        final StringBuilder out = new StringBuilder();
        for (final String lGroup : inGroups) {
            if (!isFirst) {
                out.append(", "); //$NON-NLS-1$
            }
            isFirst = false;
            out.append(lGroup);
        }
        return out;
    }

    private void deleteGroupAndAdmins(final Long inGroupID) throws VException, SQLException {
        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
        BOMHelper.getGroupAdminHome().delete(lKey, true);

        lKey = new KeyObjectImpl();
        lKey.setValue(ParticipantHome.KEY_GROUP_ID, inGroupID);
        VifBOMHelper.getParticipantHome().delete(lKey, true);

        lKey = new KeyObjectImpl();
        lKey.setValue(GroupHome.KEY_ID, inGroupID);
        VifBOMHelper.getGroupHome().delete(lKey, true);
    }

    private boolean checkDeletable(final Group inGroup, final Long inGroupID, final QuestionHome inQuestionHome)
            throws VException, SQLException {
        return inGroup.isDeletable() && !inQuestionHome.hasQuestionsInGroup(inGroupID.toString());
    }

}
