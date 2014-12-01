/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.forum.usersettings.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.SubscriptionHome;
import org.hip.vif.core.bom.impl.JoinSubscriptionToQuestionHome;
import org.hip.vif.forum.usersettings.Activator;
import org.hip.vif.forum.usersettings.Constants;
import org.hip.vif.forum.usersettings.data.SubscriptionBean;
import org.hip.vif.forum.usersettings.data.SubscriptionContainer;
import org.hip.vif.forum.usersettings.ui.SubscriptionListView;
import org.hip.vif.web.exc.VIFWebException;
import org.hip.vif.web.tasks.AbstractWebController;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.ripla.annotations.UseCaseController;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;

/** Task to show the list of subscriptions.
 *
 * @author Luthiger Created: 19.12.2011 */
@SuppressWarnings("serial")
@UseCaseController
public class SubscriptionsManageTask extends AbstractWebController implements Property.ValueChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionsManageTask.class);

    private SubscriptionContainer subscriptions;

    @Override
    protected String needsPermission() {
        return Constants.PERMISSION_MANAGE_SUBSCRIPTIONS;
    }

    @Override
    protected Component runChecked() throws VIFWebException {
        try {
            emptyContextMenu();

            final JoinSubscriptionToQuestionHome lHome = BOMHelper.getJoinSubscriptionToQuestionHome();
            final KeyObject lKey = new KeyObjectImpl();
            lKey.setValue(SubscriptionHome.KEY_MEMBERID, getActor().getActorID());
            subscriptions = SubscriptionContainer.createData(lHome.select(lKey));
            return new SubscriptionListView(subscriptions, this);
        } catch (final SQLException | VException exc) {
            throw createContactAdminException(exc);
        }
    }

    /** Callback method to change the subscription's range.
     *
     * @param inEntry {@link SubscriptionBean}
     * @param inIsLocal boolean
     * @throws SQLException
     * @throws VException */
    public void changeRange(final SubscriptionBean inEntry, final boolean inIsLocal) throws VException, SQLException {
        BOMHelper.getSubscriptionHome().updateRange(inEntry.getQustionID(), getActor().getActorID(), inIsLocal);
    }

    /** Callback method to delete the selected entries.
     *
     * @return boolean <code>true</code> if successful */
    public boolean deleteSubscriptions() {
        try {
            int i = 0;
            final SubscriptionHome lHome = BOMHelper.getSubscriptionHome();
            for (final SubscriptionBean lSubscription : subscriptions.getItemIds()) {
                if (lSubscription.isChecked()) {
                    lHome.delete(lSubscription.getQustionID(), getActor().getActorID());
                    i++;
                }
            }
            final IMessages lMessages = Activator.getMessages();
            showNotification(i == 1 ? lMessages.getMessage("msg.question.subscription.deleted") : lMessages.getMessage("msg.question.subscription.deletedP")); //$NON-NLS-1$ //$NON-NLS-2$
            sendEvent(SubscriptionsManageTask.class);
            return true;
        } catch (final VException exc) {
            LOG.error("Error encountered while deleting the subscription!", exc); //$NON-NLS-1$
        } catch (final SQLException exc) {
            LOG.error("Error encountered while deleting the subscription!", exc); //$NON-NLS-1$
        }
        return false;
    }

    @Override
    public void valueChange(final ValueChangeEvent inEvent) {
        final Object lEntry = inEvent.getProperty().getValue();
        if (lEntry instanceof SubscriptionBean) {
            final SubscriptionBean lSubscription = (SubscriptionBean) lEntry;
            setQuestionID(lSubscription.getQustionID());
            setGroupID(lSubscription.getGroupID());
            sendAliasEvent(ForwardControllerRegistry.Alias.FORWARD_QUESTION_SHOW);
        }
    }

}
