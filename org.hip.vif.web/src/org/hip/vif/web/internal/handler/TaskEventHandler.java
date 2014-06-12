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

package org.hip.vif.web.internal.handler;

import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Constants;
import org.hip.vif.web.dash.VIFDashBoard;
import org.hip.vif.web.internal.menu.ActorGroupState;
import org.hip.vif.web.internal.menu.MenuManager;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/**
 * Implementation class of the event handler service.<br/>
 * This event handler is registered for the <code>event.topics</code> <i>org/hip/vif/web/TaskEvent/*</i>.
 * (see <code>OSGI-INF/eventHandlerTask.xml</code>)
 * 
 * @author Luthiger
 * Created: 18.11.2011
 */
public class TaskEventHandler implements EventHandler {
	private static final Logger LOG = LoggerFactory.getLogger(TaskEventHandler.class);

	/* (non-Javadoc)
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	@Override
	public void handleEvent(Event inEvent) {
		VIFDashBoard lDashboard = ApplicationDash.getDash();
		
		Object lNext = inEvent.getProperty(Constants.EVENT_PROPERTY_NEXT_TASK);
		if (lNext != null) {			
			LOG.debug("next task={}.", lNext); //$NON-NLS-1$
			try {
				lDashboard.setContentView(lDashboard.getContentComponent(lNext.toString()));
			} 
			catch (NoTaskFoundException exc) {
				handleNoTaskFound(exc, lDashboard);
			}
		}
		
		Object lContextMenuSet = inEvent.getProperty(Constants.EVENT_PROPERTY_CONTEXT_MENU_ID);
		if (lContextMenuSet != null) {
			LOG.debug("Event: displaying context menu={}", lContextMenuSet); //$NON-NLS-1$
			lDashboard.setContextMenu(getContextMenuComponent(lContextMenuSet.toString()));
		}
		
		Object lNotification = inEvent.getProperty(Constants.EVENT_PROPERTY_NOTIFICATION_MSG);
		Object lNotificationType = inEvent.getProperty(Constants.EVENT_PROPERTY_NOTIFICATION_TYPE);
		if (lNotification != null) {
			LOG.debug("Event: show notification \"{}\"", lNotification); //$NON-NLS-1$
			lDashboard.showNotification((String) lNotification, ((Integer)lNotificationType).intValue());
		}
		
		Object lRefresh = inEvent.getProperty(Constants.EVENT_PROPERTY_REFRESH);
		if (lRefresh != null) {
			LOG.debug("Event: refresh dash");
			lDashboard.refreshDash();
		}
	}
	
	private Component getContextMenuComponent(String inMenuSetName) {
		ActorGroupState lState = ActorGroupState.getActorGroupState(ApplicationData.getGroupID(), ApplicationData.getActor());
		return MenuManager.INSTANCE.renderContextMenu(inMenuSetName, lState);
	}
	
	private void handleNoTaskFound(NoTaskFoundException inExc, VIFDashBoard inDashboard) {
		LOG.error("Configuration error:", inExc); //$NON-NLS-1$
		inDashboard.setContentView(new DefaultVIFView(inExc));
	}
	
}
