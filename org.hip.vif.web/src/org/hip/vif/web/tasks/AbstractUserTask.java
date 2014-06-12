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

package org.hip.vif.web.tasks;

import java.util.HashMap;
import java.util.Map;

import org.hip.vif.core.interfaces.IEventableTask;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.Constants;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.UseCaseHelper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Window.Notification;

/**
 * Base class for user tasks, i.e. task implementing the <code>IUserTask</code> interface.
 * 
 * @see IUserTask
 * @author Luthiger
 * Created: 23.12.2011
 */
public abstract class AbstractUserTask implements IEventableTask {
	private EventAdmin eventAdmin;
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IEventableTask#setEventAdmin(org.osgi.service.event.EventAdmin)
	 */
	public void setEventAdmin(EventAdmin inEventAdmin) {
		eventAdmin = inEventAdmin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IEventableTask#requestLookup(org.hip.vif.core.util.LinkButtonHelper.LookupType, java.lang.Long)
	 */
	public void requestLookup(LinkButtonHelper.LookupType inType, Long inID) {
		requestLookup(inType, inID.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.interfaces.IEventableTask#requestLookup(org.hip.vif.core.util.LinkButtonHelper.LookupType, java.lang.String)
	 */
	public void requestLookup(LinkButtonHelper.LookupType inType, String inTextID) {
		Map<String, Object> lProperties = new HashMap<String, Object>();
		lProperties.put(Constants.EVENT_PROPERTY_LOOKUP_TYPE, inType);
		lProperties.put(Constants.EVENT_PROPERTY_LOOKUP_ID, inTextID);
		
		Event lEvent = new Event(Constants.EVENT_TOPIC_LOOKUP, lProperties);
		eventAdmin.sendEvent(lEvent);		
	}
	
	/**
	 * Use OSGi event service to display a notification message.
	 * 
	 * @param inMessage String
	 * @param inNotificationType int The message type (e.g. <code>Notification.TYPE_HUMANIZED_MESSAGE</code>)
	 * @see com.vaadin.ui.Window.Notification
	 */
	protected void showNotification(String inMessage, int inNotificationType) {
		Map<String, Object> lProperties = new HashMap<String, Object>();
		lProperties.put(Constants.EVENT_PROPERTY_NOTIFICATION_MSG, inMessage);
		lProperties.put(Constants.EVENT_PROPERTY_NOTIFICATION_TYPE, inNotificationType);
		
		Event lEvent = new Event(Constants.EVENT_TOPIC_NOTIFICATION, lProperties);
		eventAdmin.sendEvent(lEvent);
	}
	
	/**
	 * Use OSGi event service to display a notification message with type <code>Notification.TYPE_TRAY_NOTIFICATION</code>.
	 * 
	 * @param inMessage String
	 */
	protected void showNotification(String inMessage) {
		showNotification(inMessage, Notification.TYPE_TRAY_NOTIFICATION);
	}
	
	/**
	 * Create a fully qualified task name with the specified task.
	 * 
	 * @param inTask {@link IPluggableTask}
	 * @return String the fully qualified name of the task
	 */
	protected String createFullyQualifiedTaskName(Class<? extends IPluggableTask> inTask) {
		return UseCaseHelper.createFullyQualifiedControllerName(inTask);
	}
	
	/**
	 * Use OSGi event service to display the next content view.
	 * 
	 * @param inClass Class the next task 
	 */
	protected void sendEvent(Class<? extends IPluggableTask> inTask) {
		sendEvent(createFullyQualifiedTaskName(inTask));
	}

	/**
	 * Use OSGi event service to display the next content view.
	 * 
	 * @param inTaskName String the fully qualified name of the next task 
	 */
	protected void sendEvent(String inTaskName) {
		Map<String, Object> lProperties = new HashMap<String, Object>();
		lProperties.put(Constants.EVENT_PROPERTY_NEXT_TASK, inTaskName);
		
		Event lEvent = new Event(Constants.EVENT_TOPIC_TASKS, lProperties);
		eventAdmin.sendEvent(lEvent);
	}

}
