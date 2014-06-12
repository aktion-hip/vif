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

package org.hip.vif.web.dash;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.Activator;
import org.hip.vif.web.interfaces.IDashBoard;
import org.hip.vif.web.interfaces.IDisposable;
import org.hip.vif.web.internal.menu.ActorGroupState;
import org.hip.vif.web.internal.menu.MenuFactory;
import org.hip.vif.web.layout.VIFApplication;
import org.hip.vif.web.menu.IMenuCommand;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.hip.vif.web.tasks.LanguageSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * The application's dash board.
 * 
 * @author Luthiger
 * Created: 12.05.2011
 */
@SuppressWarnings("serial")
public abstract class VIFDashBoard extends CustomComponent implements ClickListener, IDisposable, IDashBoard {
	private static final Logger LOG = LoggerFactory.getLogger(VIFDashBoard.class);
	
	private VIFApplication app;
	private Button logout;

	private VerticalLayout layout;
	private VerticalLayout contentPane;
	private VerticalLayout menuPane;

	private LanguageSelect languageSelector;

	private MenuBar menuBar;


	/**
	 * Creates the application's dashboard instance.
	 * 
	 * @param inApplication {@link VIFApplication}
	 */
	public VIFDashBoard(VIFApplication inApplication) {
		app = inApplication;
		setSizeFull();
		
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		layout.setStyleName("vif-dash"); //$NON-NLS-1$
		layout.setSizeFull();
		layout.removeAllComponents();

		createServiceBar(layout);
		menuBar = createToolBar(layout);
		createMainArea(layout);
	}
	
	private void createMainArea(VerticalLayout inLayout) {
		HorizontalSplitPanel lPanel = new HorizontalSplitPanel();
		lPanel.setSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
		lPanel.setHeight(SIZE_UNDEFINED, 0);
		
		menuPane = new VerticalLayout();
		lPanel.setFirstComponent(menuPane);
		
		contentPane = new VerticalLayout();
		lPanel.setSecondComponent(contentPane);
		contentPane.setMargin(true);

		lPanel.setStyleName("vif-split-content"); //$NON-NLS-1$
		lPanel.setSizeFull();
		inLayout.addComponent(lPanel);
		inLayout.setExpandRatio(lPanel, 1);
	}

	/**
	 * The service bar displays the user's name and the logout button.
	 * 
	 * @param inLayout VerticalLayout parent layout
	 */
	private void createServiceBar(VerticalLayout inLayout) {
		HorizontalLayout lLayout = new HorizontalLayout();
		lLayout.setSpacing(true);
		lLayout.setMargin(false, true, false, true);
		lLayout.setWidth("100%"); //$NON-NLS-1$
		lLayout.setHeight(22, UNITS_PIXELS);
		lLayout.setStyleName("vif-servicebar"); //$NON-NLS-1$
		lLayout.setMargin(false, true, false, false);
		
		languageSelector = LanguageSelect.getLanguageSelection(app);
		lLayout.addComponent(languageSelector);
		lLayout.setComponentAlignment(languageSelector, Alignment.MIDDLE_RIGHT);
		lLayout.setExpandRatio(languageSelector, 1);
		
		lLayout.addComponent(createSeparator());

		IMessages lMessages = Activator.getMessages();
		Label lUser = new Label(String.format(lMessages.getMessage("service.bar.login.name"), ApplicationData.getActor().getUserID()), Label.CONTENT_XHTML); //$NON-NLS-1$
		lUser.setStyleName("vif-servicebar-label"); //$NON-NLS-1$
		lUser.setSizeUndefined();
		lLayout.addComponent(lUser);		
		lLayout.setComponentAlignment(lUser, Alignment.MIDDLE_RIGHT);
		
		lLayout.addComponent(createSeparator());
		
		logout = new Button(lMessages.getMessage("service.bar.logout.button")); //$NON-NLS-1$
		logout.setStyleName(BaseTheme.BUTTON_LINK);
		logout.addListener(this);
		logout.setClickShortcut(KeyCode.L, ModifierKey.ALT);
		
		lLayout.addComponent(logout);
		lLayout.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);
		
		inLayout.addComponent(lLayout);
	}

	private Label createSeparator() {
		Label lSeparator = new Label("&bull;", Label.CONTENT_XHTML); //$NON-NLS-1$
		lSeparator.setSizeUndefined();
		return lSeparator;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	public void buttonClick(ClickEvent inEvent) {
		if (inEvent.getButton() != logout) return;
		
		app.getMainWindow().getApplication().close();
	}
	
	/**
	 * Toolbar layout (style <code>vif-toolbar</code>) containing the
	 * menu bar (style <code>vif-menubar</code>).<br />
	 * The toolbar has a height of 32px.
	 * 
	 * @param inLayout {@link VerticalLayout} the container layout
	 * @return {@link MenuBar}
	 */
	private MenuBar createToolBar(VerticalLayout inLayout) {
		HorizontalLayout lLayout = new HorizontalLayout();
		lLayout.setMargin(false, false, false, true);
		lLayout.setWidth("100%"); //$NON-NLS-1$
		lLayout.setHeight(32, UNITS_PIXELS);
		lLayout.setStyleName("vif-toolbar"); //$NON-NLS-1$
		
		MenuBar outMenuBar = new MenuBar();
		outMenuBar.setAutoOpen(true);
		outMenuBar.setStyleName("vif-menubar"); //$NON-NLS-1$
		lLayout.addComponent(outMenuBar);
		
		createMenu(outMenuBar);
		
		inLayout.addComponent(lLayout);
		
		return outMenuBar;
	}

	private void createMenu(MenuBar inMenuBar) {
		MenuBar.Command lCommand = new MenuBar.Command() {			
			@Override
			public void menuSelected(MenuItem inSelected) {
				IMenuCommand lAction = getMenuMap().get(inSelected.getId());
				//clicking the menu resets the stored group ID
				ApplicationData.setGroupID(0l);				
				if (lAction != null) {
					try {
						setContentView(getContentComponent(lAction.getTaskName()));
					} 
					catch (NoTaskFoundException exc) {
						handleNoTaskFound(exc);
					}
				}
			}
		};

		
		Map<String, MenuItem> lMenuMap = new HashMap<String, MenuBar.MenuItem>();
		ActorGroupState lState = ActorGroupState.getActorGroupState(ApplicationData.getGroupID(), ApplicationData.getActor());
		for (MenuFactory lFactory : getMenus()) {
			MenuItem lItem = lFactory.createMenu(inMenuBar, getMenuMap(), lCommand, lState);
			lMenuMap.put(lFactory.getProviderSymbolicName(), lItem);
		}
		ApplicationData.setMenuMap(lMenuMap);
		
		LOG.debug("Menu created for VIF."); //$NON-NLS-1$
	}

	/**
	 * Returns the <code>Component</code> provided and controlled by the task with the specified name.
	 * This ui component will be displayed in the content area.
	 * 
	 * @param inTaskName String
	 * @return {@link Component}
	 * @throws NoTaskFoundException
	 */
	public abstract Component getContentComponent(String inTaskName) throws NoTaskFoundException;

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IDashBoard#setContentView(com.vaadin.ui.Component)
	 */
	public void setContentView(Component inComponent) {
		contentPane.removeAllComponents();
		contentPane.addComponent(inComponent);
	}
	
	public void setContextMenu(Component inComponent) {
		menuPane.removeAllComponents();
		menuPane.addComponent(inComponent);
	}
	
	/**
	 * Returns the part specific (forum / admin) collection of menus.
	 * 
	 * @return Collection<MenuFactory> the menus.
	 */
	protected abstract Collection<MenuFactory> getMenus();
	
	/**
	 * Returns the part specific (forum / admin) command map.
	 * 
	 * @return Map<Integer, IMenuCommand> we have to map VAADIN command ids with VIF menu commands.
	 */
	protected abstract Map<Integer, IMenuCommand> getMenuMap();
	
	public void showNotification(String inNotification, int inNotificationType) {
		getWindow().showNotification(inNotification, inNotificationType);
	}
	
	private void handleNoTaskFound(NoTaskFoundException inExc) {
		LOG.error("Configuration error:", inExc); //$NON-NLS-1$
		setContentView(new DefaultVIFView(inExc));
	}
	
	/**
	 * Refresh the application's view e.g. after the user changed the language.
	 */
	public void refreshDash() {
		app.refreshDash();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IDashBoard#showDefault()
	 */
	public void showDefault() {
		List<MenuItem> lMenuItems = menuBar.getItems();
		if (lMenuItems.size() != 0) {
			MenuItem lMenuItem = lMenuItems.get(0);
			lMenuItem.getCommand().menuSelected(lMenuItem);
		}
	}

	@Override
	public void dispose() {
		if (languageSelector != null) {
			languageSelector.dispose();
		}
		app = null;
	}
	
}
