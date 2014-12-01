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

import java.io.IOException;
import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PlacefillerCollection;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.admin.groupedit.Activator;
import org.hip.vif.admin.groupedit.Constants;
import org.hip.vif.admin.groupedit.data.ParticipantBean;
import org.hip.vif.admin.groupedit.data.ParticipantContainer;
import org.hip.vif.admin.groupedit.ui.ParticipantListView;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupAdminHome;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.LinkMemberRoleHome;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.impl.NestedParticipantsOfGroup;
import org.hip.vif.core.bom.impl.NestedParticipantsOfGroupHome;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.bom.VifBOMHelper;
import org.hip.vif.web.tasks.AbstractWebController;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification.Type;

/** Controller for the view displaying the list of participants of a discussion group.
 *
 * @author Luthiger Created: 16.11.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class ParticipantListTask extends AbstractWebController implements Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantListTask.class);

    private ParticipantContainer participants;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_LIST_PARTICIPANTS;
    }

    @Override
    protected Component runChecked() throws RiplaException {
        try {
            loadContextMenu(Constants.MENU_SET_ID_SHOW_PARTICIPANTS);

            final NestedParticipantsOfGroupHome lParticipantsHome = getNestedParticipantsHome();

            final Long lGroupID = getGroupID();
            String lGroupName = ""; //$NON-NLS-1$
            boolean lEnableDelete = false;
            if (lGroupID.intValue() > 0) {
                final Group lGroup = VifBOMHelper.getGroupHome().getGroup(lGroupID);
                if (lGroup != null) {
                    lGroupName = (String) lGroup.get(GroupHome.KEY_NAME);
                    lEnableDelete = lGroup.isPrivate() || lGroup.isCreated();
                }
            }
            KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(GroupAdminHome.KEY_GROUP_ID, lGroupID);

            final PlacefillerCollection lPlacefillers = new PlacefillerCollection();
            lPlacefillers.add(BOMHelper.getGroupAdminHome(), lKey, NestedParticipantsOfGroupHome.NESTED_ALIAS);

            lKey = new KeyObjectImpl();
            lKey.setValue(ParticipantHome.KEY_GROUP_ID, lGroupID);
            participants = ParticipantContainer.createData(lParticipantsHome.select(lKey,
                    createOrder(MemberHome.KEY_USER_ID, false), lPlacefillers));
            return new ParticipantListView(participants, lGroupName, lEnableDelete, this);
        } catch (final SQLException exc) {
            throw createContactAdminException(exc);
        } catch (final IOException exc) {
            throw createContactAdminException(exc);
        } catch (final VException exc) {
            throw createContactAdminException(exc);
        }
    }

    private NestedParticipantsOfGroupHome getNestedParticipantsHome() throws IOException {
        if (PreferencesHandler.INSTANCE.isDerbyDB()) {
            return (NestedParticipantsOfGroupHome) VSys.homeManager.getHome(NestedParticipantsOfGroup.HOME_CLASS_NAME2);
        }
        return BOMHelper.getNestedParticipantsOfGroupHome();
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lMember = inEvent.getProperty().getValue();
        if (lMember instanceof ParticipantBean) {
            requestLookup(LookupType.MEMBER, ((ParticipantBean) lMember).getMemberID());
        }
    }

    /** Callback method, removed the selected participants from the group.
     *
     * @return boolean <code>true</code> if successful */
    public boolean deleteParticipants() {
        try {
            final ParticipantHome lParticipantHome = VifBOMHelper.getParticipantHome();
            final LinkMemberRoleHome lRoleHome = BOMHelper.getLinkMemberRoleHome();
            final Long lGroupID = getGroupID();

            int lCount = 0;
            for (final ParticipantBean lParticipant : participants.getItemIds()) {
                if (lParticipant.isChecked()) {
                    final Long lMemberID = lParticipant.getMemberID();
                    lParticipantHome.removeParticipant(lGroupID, lMemberID);

                    // actualize the member's role if needed
                    if (!lParticipantHome.isParticipant(lMemberID)) {
                        lRoleHome.deleteParticipantRole(lMemberID);
                    }
                    lCount++;
                }
            }
            showNotification(
                    Activator.getMessages().getFormattedMessage("ui.participants.feedback.remove", lCount), Type.TRAY_NOTIFICATION); //$NON-NLS-1$
            sendEvent(ParticipantListTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encoutered while removing participants from group!", exc); //$NON-NLS-1$
        }
        return false;
    }

}
