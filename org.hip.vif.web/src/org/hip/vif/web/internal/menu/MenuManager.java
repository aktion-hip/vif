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

package org.hip.vif.web.internal.menu;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.Constants;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.hip.vif.web.util.UseCaseHelper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Singleton instance, responsible for managing all context menu items.
 * 
 * @author Luthiger
 * Created: 26.06.2011
 */
public enum MenuManager {
	INSTANCE;
	
	private Map<String, ContextMenuSet> contextMenus = new Hashtable<String, MenuManager.ContextMenuSet>();
	private EventAdmin eventAdmin;
	
	MenuManager() {
		//add empty menu
		addContextMenuSet(new IMenuSet() {			
			public String getSetID() {
				return Constants.MENU_SET_ID_EMPTY;
			}			
			public IVIFContextMenuItem[] getContextMenuItems() {
				return new IVIFContextMenuItem[] {};
			}
		});
	}
	
	/**
	 * Sets the <code>EventAdmin</code> to the menu manager for that it can send events.
	 * 
	 * @param inEventAdmin {@link EventAdmin}
	 */
	public void setEventAdmin(EventAdmin inEventAdmin) {
		eventAdmin = inEventAdmin;
	}

	/**
	 * Adds the configuration of a context menu set to the menu manager.
	 * 
	 * @param inMenuSet {@link IMenuSet}
	 */
	public void addContextMenuSet(IMenuSet inMenuSet) {
		String lSetID = UseCaseHelper.createFullyQualifiedID(inMenuSet.getSetID(), inMenuSet.getClass());
		ContextMenuSet lContextMenuSet = new ContextMenuSet();
		for (IVIFContextMenuItem lContextMenuItem : inMenuSet.getContextMenuItems()) {
			lContextMenuSet.addContextMenuItem(new ContextMenuItem(lContextMenuItem));
		}
		contextMenus.put(lSetID, lContextMenuSet);
	}

	/**
	 * Removes the configuration of a context menu set from the menu manager.
	 * 
	 * @param inMenuSet {@link IMenuSet}
	 */
	public void removeContextMenuSet(IMenuSet inMenuSet) {
		contextMenus.remove(UseCaseHelper.createFullyQualifiedID(inMenuSet.getSetID(), inMenuSet.getClass()));
	}

	/**
	 * Method to render the context menu.
	 * 
	 * @param inMenuSetName String the fully qualified ID of the context menu
	 * @param inState {@link ActorGroupState} the actual state that has to be taken into consideration to decide whether to render a menu item or not 
	 * @return {@link Component} the component that displays the rendered context menu
	 */
	public Component renderContextMenu(String inMenuSetName, ActorGroupState inState) {
		VerticalLayout outContextMenu = new VerticalLayout();
		outContextMenu.setMargin(true);
		outContextMenu.setStyleName("vif-submenu"); //$NON-NLS-1$
		
		ContextMenuSet lContextMenuSet = contextMenus.get(inMenuSetName);
		if (lContextMenuSet == null) return outContextMenu;
		
		for (ContextMenuItem lItem : lContextMenuSet.getContextMenuItems()) {
			if (lItem.checkConditions(inState)) {
				Button lContextMenuLink = new Button(lItem.getCaption());
				lContextMenuLink.setStyleName(BaseTheme.BUTTON_LINK);
				lContextMenuLink.addListener(new ContextMenuListener(lItem.getTaskClass(), eventAdmin));
				outContextMenu.addComponent(lContextMenuLink);
			}
		}
		
		return outContextMenu;
	}
	
// --- 	
	
	@SuppressWarnings("serial")
	private static class ContextMenuListener implements ClickListener {
		private Class<? extends IPluggableTask> taskClass;
		private EventAdmin eventAdmin;
		
		ContextMenuListener(Class<? extends IPluggableTask> inTaskClass, EventAdmin inEventAdmin) {
			taskClass = inTaskClass;
			eventAdmin = inEventAdmin;
		}

		@Override
		public void buttonClick(ClickEvent inEvent) {
			Map<String, Object> lProperties = new HashMap<String, Object>();
			lProperties.put(Constants.EVENT_PROPERTY_NEXT_TASK, UseCaseHelper.createFullyQualifiedControllerName(taskClass));
			
			Event lEvent = new Event(Constants.EVENT_TOPIC_TASKS, lProperties);
			eventAdmin.sendEvent(lEvent);			
		}
	}
	
	private static class ContextMenuSet {
		private Collection<ContextMenuItem> items = new Vector<MenuManager.ContextMenuItem>();
		
		void addContextMenuItem(ContextMenuItem inItem) {
			items.add(inItem);
		}
		
		Collection<ContextMenuItem> getContextMenuItems() {
			return items;
		}
	}
	
	private static class ContextMenuItem {

		private IVIFContextMenuItem vifContextMenuItem;

		ContextMenuItem(IVIFContextMenuItem inContextMenuItem) {
			vifContextMenuItem = inContextMenuItem;
		}

		String getCaption() {
			return vifContextMenuItem.getTitleMsg();
		}

		public Class<? extends IPluggableTask> getTaskClass() {
			return vifContextMenuItem.getTaskClass();
		}
		
		boolean checkConditions(ActorGroupState inState) {
			if (vifContextMenuItem.needsTypePrivate() && !inState.isPrivateType) return false;
			if (vifContextMenuItem.needsGroupAmin() && !inState.isGroupAdmin) return false;
			if (vifContextMenuItem.needsRegistration() && !inState.isRegistered) return false;
			if (vifContextMenuItem.getMenuPermission().length() > 0 && !inState.actorPermissions.contains(vifContextMenuItem.getMenuPermission())) return false;
			if (vifContextMenuItem.getGroupStates().length > 0 && !Arrays.asList(vifContextMenuItem.getGroupStates()).contains(inState.groupState)) return false;
			return true;
		}
		
	}
	
}
