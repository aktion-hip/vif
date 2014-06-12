package org.hip.vif.web.internal.menu;

import java.util.List;
import java.util.Map;

import org.hip.vif.web.menu.IMenuCommand;
import org.hip.vif.web.menu.IVIFMenuItem;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Helper class for creating the main menu's pulldown menu entries.
 * 
 * @author Luthiger
 */
public class MenuFactory implements Comparable<MenuFactory> {
	
	private IVIFMenuItem menu;

	/**
	 * Constructor
	 * 
	 * @param inMenu {@link IVIFMenuItem} the main menu item to process.
	 */
	public MenuFactory(IVIFMenuItem inMenu) {
		menu = inMenu;
	}

	/**
	 * Creates the menu for the use case and adds it to the specified menu bar.
	 * 
	 * @param inMenuBar {@link MenuBar}
	 * @param inMap Map<Integer, IMenuCommand> the map to register the menu's command
	 * @param inCommand {@link Command} the Vaadin menu bar command
	 * @param inState {@link ActorGroupState} the actor's state parameter object containing the permissions the user has 
	 * @return MenuItem the Vaadin menu item
	 */
	public MenuItem createMenu(MenuBar inMenuBar, Map<Integer, IMenuCommand> inMap, Command inCommand, ActorGroupState inState) {
		if (!checkPermissions(menu.getPermission(), inState)) return null;
		
		MenuItem outItem = inMenuBar.addItem(menu.getLabel(), null, inCommand);
		outItem.setStyleName(""); //$NON-NLS-1$
		addCommand(inMap, outItem, menu);
		
		List<IVIFMenuItem> lSubMenu = menu.getSubMenu();
		if (!lSubMenu.isEmpty()) {
			createSubMenu(lSubMenu, outItem, inMap, inCommand, inState);
		}
		
		return outItem;
	}

	/**
	 * Check permission.
	 * 
	 * @param inMenuPermission String the permission, may be an empty string
	 * @param inState {@link ActorGroupState} the parameter object containing the permissions the user has
	 * @return boolean <code>true</code> if the user has sufficient permissions for the command handled by the menu item
	 */
	protected boolean checkPermissions(String inMenuPermission, ActorGroupState inState) {
		if (inMenuPermission.length() > 0 && !inState.actorPermissions.contains(inMenuPermission)) return false;
		return true;
	}

	protected void addCommand(Map<Integer, IMenuCommand> inMap, MenuItem inItem, IVIFMenuItem inVIFItem) {
		addCommand(inMap, inItem, createMenuCommand(inVIFItem.getTaskName()));
	}
	
	protected void addCommand(Map<Integer, IMenuCommand> inMap, MenuItem inItem, IMenuCommand inCommand) {
		if (inCommand != null) {			
			inMap.put(inItem.getId(), inCommand);
		}
	}
	
	protected IMenuCommand createMenuCommand(final String inTaskName) {
		return new IMenuCommand() {			
			@Override
			public String getTaskName() {
				return inTaskName;
			}
		};
	}

	/**
	 * Recurse structure to create the menu's pulldown entries.
	 * 
	 * @param inSubItems
	 * @param inItem
	 * @param inMap
	 * @param inState 
	 * @param inComman
	 */
	protected void createSubMenu(List<IVIFMenuItem> inSubItems, MenuItem inItem, Map<Integer, IMenuCommand> inMap, Command inCommand, ActorGroupState inState) {
		for (IVIFMenuItem lItem : inSubItems) {
			if (!checkPermissions(lItem.getPermission(), inState)) {
				continue;
			}
			MenuItem lMenuItem = inItem.addItem(lItem.getLabel(), null, inCommand);
			addCommand(inMap, lMenuItem, lItem);
			List<IVIFMenuItem> lSubMenu = lItem.getSubMenu();
			if (!lSubMenu.isEmpty()) {
				createSubMenu(lSubMenu, lMenuItem, inMap, inCommand, inState);
			}
		}
	}
	
	/**
	 * This menu's position compared to other menu entry's position.
	 * Higher position values are displayed more to the right. 
	 * 
	 * @return int the position
	 */
	public int getPosition() {
		return menu.getPosition();
	}

	@Override
	public int compareTo(MenuFactory inCompare) {
		return getPosition() - inCompare.getPosition();
	}
	
	/**
	 * @return String the symbolic name of the bundle providing this menu
	 */
	public String getProviderSymbolicName() {
		return menu.getTaskName().split("/")[0]; //$NON-NLS-1$
	}

}
