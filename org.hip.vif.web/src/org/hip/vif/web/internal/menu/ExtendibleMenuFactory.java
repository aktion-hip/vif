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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hip.vif.web.menu.ExtendibleMenuMarker;
import org.hip.vif.web.menu.ExtendibleMenuMarker.Position;
import org.hip.vif.web.menu.IExtendibleMenuContribution;
import org.hip.vif.web.menu.IMenuCommand;
import org.hip.vif.web.menu.IVIFMenuExtendible;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Menu factory for an extendible menu.
 *  
 * @author Luthiger
 * Created: 29.10.2011
 */
public class ExtendibleMenuFactory extends MenuFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ExtendibleMenuFactory.class);
	
	private IVIFMenuExtendible menu;
	private List<IMenuItem> contributions = new Vector<ExtendibleMenuFactory.IMenuItem>();

	/**
	 * Constructor
	 * 
	 * @param inMenu {@link IVIFMenuExtendible} the extendible menu to process
	 * @param inContributions Collection of {@link IExtendibleMenuContribution}s the set of contributions to the extendible menu
	 */
	public ExtendibleMenuFactory(IVIFMenuExtendible inMenu, Collection<IExtendibleMenuContribution> inContributions) {
		super(inMenu);
		menu = inMenu;
		initializePositions(inMenu.getMarkers(), inContributions);
	}
	
	private void initializePositions(ExtendibleMenuMarker[] inMarkers, Collection<IExtendibleMenuContribution> inContributions) {
		for (ExtendibleMenuMarker lMarker : inMarkers) {
			contributions.add(new MarkerItem(lMarker.getMarkerID()));
		}
		for (IExtendibleMenuContribution lContribution : inContributions) {
			Position lPosition = lContribution.getPosition();
			switch (lPosition.getType()) {
			case APPEND:
				appendTo(lPosition.getMarkerID(), lContribution);
				break;
			case INSERT_BEFORE:
				insert(lPosition.getMarkerID(), lContribution, 0);
				break;
			case INSERT_AFTER:				
				insert(lPosition.getMarkerID(), lContribution, 1);
				break;
			}
		}
	}

	private void insert(String inMarkerID, IExtendibleMenuContribution inContribution, int inInsertOffset) {
		IMenuItem lItem = find(inMarkerID);
		if (lItem == null) {
			LOG.error("Can't find ID {}!", inMarkerID); //$NON-NLS-1$
			throw new IllegalArgumentException("Can't find ID " + inMarkerID); //$NON-NLS-1$
		}
		
		int lIndex = contributions.indexOf(lItem);
		if (lIndex >= 0) {
			contributions.add(lIndex + inInsertOffset, new ContributionAdapter(inContribution));
		}
	}

	private IMenuItem find(String inMarkerID) {
		for (IMenuItem lItem : contributions) {
			String lItemID = lItem.getMarkerID();
			if (lItemID.equalsIgnoreCase(inMarkerID)) {
				return lItem;
			}
		}
		return null;
	}

	private void appendTo(String inMarkerID, IExtendibleMenuContribution inContribution) {
		int i;
		Iterator<IMenuItem> lItems = contributions.iterator();
		for (i = 0; lItems.hasNext(); i++) {
			IMenuItem lItem = lItems.next();
			if (lItem.isMarker()) {
				String lID = lItem.getMarkerID();
				if (lID.equalsIgnoreCase(inMarkerID)) {
					i++;
					for (; lItems.hasNext(); i++) {
						IMenuItem lNextItem = lItems.next();
						if (lNextItem.isMarker()) {
							break;
						}
					}
					contributions.add(i-1, new ContributionAdapter(inContribution));
					return;
				}
			}
		}
	}

	@Override
	public MenuItem createMenu(MenuBar inMenuBar, Map<Integer, IMenuCommand> inMap, Command inCommand, ActorGroupState inState) {
		if (!checkPermissions(menu.getPermission(), inState)) return null;
		
		MenuItem outItem = inMenuBar.addItem(menu.getLabel(), null, inCommand);
		
		boolean lFirst = true;
		for (IMenuItem lItem : contributions) {
			if (lItem.isMarker()) {
				continue;
			}
			
			final IExtendibleMenuContribution lContribution = lItem.getContribution();
			
			//check the permission the item needs to be displayed
			if (!checkPermissions(lContribution.getPermission(), inState)) {
				continue;
			}
			
			MenuItem lMenuItem = outItem.addItem(lContribution.getLabel(), null, inCommand);
			addCommand(inMap, lMenuItem, createMenuCommand(lContribution.getTaskName()));
			if (lFirst) {
				//clicking the menu has the same effect as clicking the first sub menu item
				addCommand(inMap, outItem, createMenuCommand(lContribution.getTaskName()));
				lFirst = false;
			}
			
			//process sub menu
			List<IVIFMenuItem> lSubMenu = lContribution.getSubMenu();
			if (!lSubMenu.isEmpty()) {
				createSubMenu(lSubMenu, lMenuItem, inMap, inCommand, inState);
			}
		}
		return outItem;
	}
	
// --- inner classes ---
	
	private static interface IMenuItem {
		boolean isMarker();
		String getMarkerID();
		IExtendibleMenuContribution getContribution();
	}
	
	private static class MarkerItem implements IMenuItem {
		private String markerID;

		public MarkerItem(String inMarkerID) {
			markerID = inMarkerID;
		}
		@Override
		public boolean isMarker() {
			return true;
		}
		@Override
		public String getMarkerID() {
			return markerID;
		}
		@Override
		public IExtendibleMenuContribution getContribution() {
			return null;
		}
	}
	
	private static class ContributionAdapter implements IMenuItem {
		private IExtendibleMenuContribution contribution;
		
		public ContributionAdapter(IExtendibleMenuContribution inContribution) {
			contribution = inContribution;
		}
		@Override
		public boolean isMarker() {
			return false;
		}
		@Override
		public String getMarkerID() {
			return ""; //$NON-NLS-1$
		}
		@Override
		public IExtendibleMenuContribution getContribution() {
			return contribution;
		}
	}

	@Override
	public String getProviderSymbolicName() {
		return FrameworkUtil.getBundle(contributions.get(0).getContribution().getClass()).getSymbolicName();
	}

}
