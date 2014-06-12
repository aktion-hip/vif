package org.hip.vif.web.internal.menu;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.hip.vif.web.menu.ExtendibleMenuMarker;
import org.hip.vif.web.menu.ExtendibleMenuMarker.Position;
import org.hip.vif.web.menu.ExtendibleMenuMarker.PositionType;
import org.hip.vif.web.menu.IExtendibleMenuContribution;
import org.hip.vif.web.menu.IMenuCommand;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.util.AbstractExtendibleMenu;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * @author Luthiger
 * Created: 30.10.2011
 */
public class ExtendibleMenuFactoryTest {
	private static final String MENU_ID = "extendibleTestMenu";
	private static final String EXTENDIBLE_MENU_POSITION_START = "startMenu";
	private static final String EXTENDIBLE_MENU_POSITION_ADDITIONS = "additons";
	private static final String EXTENDIBLE_MENU_POSITION_END = "endMenu";
	
	private static final String PERMISSION_1 = "permission1";
	private static final String PERMISSION_2 = "permission2";

	private static IExtendibleMenuContribution CONTRIBUTION1 = new MenuContribution("Insert after start", MENU_ID, 
			new Position(PositionType.INSERT_AFTER, EXTENDIBLE_MENU_POSITION_START));
	private static IExtendibleMenuContribution CONTRIBUTION2 = new MenuContribution("Append to start", MENU_ID, 
			new Position(PositionType.APPEND, EXTENDIBLE_MENU_POSITION_START));
	private static IExtendibleMenuContribution CONTRIBUTION3 = new MenuContribution("Insert before additions", MENU_ID, 
			new Position(PositionType.INSERT_BEFORE, EXTENDIBLE_MENU_POSITION_ADDITIONS));
	private static IExtendibleMenuContribution CONTRIBUTION4 = new MenuContribution("Insert after additions", MENU_ID, 
			new Position(PositionType.INSERT_AFTER, EXTENDIBLE_MENU_POSITION_ADDITIONS));
	private static IExtendibleMenuContribution CONTRIBUTION5 = new MenuContribution("Append to additions", MENU_ID, 
			new Position(PositionType.APPEND, EXTENDIBLE_MENU_POSITION_ADDITIONS));
	private static IExtendibleMenuContribution CONTRIBUTION6 = new MenuContribution("Insert before end", MENU_ID, 
			new Position(PositionType.INSERT_BEFORE, EXTENDIBLE_MENU_POSITION_END));
	private static IExtendibleMenuContribution CONTRIBUTION7 = new MenuContribution("Insert before additions 2", MENU_ID, 
			new Position(PositionType.INSERT_BEFORE, EXTENDIBLE_MENU_POSITION_ADDITIONS));
	private static IExtendibleMenuContribution CONTRIBUTION8 = new MenuContribution("Insert after additions 2", MENU_ID, 
			new Position(PositionType.INSERT_AFTER, EXTENDIBLE_MENU_POSITION_ADDITIONS));
	
	private HashMap<Integer, IMenuCommand> testMap;

	@Before
	public void setUp() throws Exception {
		testMap = new HashMap<Integer, IMenuCommand>();
		((MenuContribution)CONTRIBUTION1).setPermission("");
		((MenuContribution)CONTRIBUTION2).setPermission("");
		((MenuContribution)CONTRIBUTION3).setPermission("");
		((MenuContribution)CONTRIBUTION4).setPermission("");
		((MenuContribution)CONTRIBUTION5).setPermission("");
		((MenuContribution)CONTRIBUTION6).setPermission("");
		((MenuContribution)CONTRIBUTION7).setPermission("");
		((MenuContribution)CONTRIBUTION8).setPermission("");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCreateMenuPosition() {
		IExtendibleMenuContribution[] lContributions = new IExtendibleMenuContribution[] {CONTRIBUTION6, CONTRIBUTION5, CONTRIBUTION4, CONTRIBUTION3, CONTRIBUTION2, CONTRIBUTION1, CONTRIBUTION7, CONTRIBUTION8};
		ExtendibleMenuFactory lMenu = new ExtendibleMenuFactory(new ExtendibleMenu(), Arrays.asList(lContributions));
		TestMenuBar lTestMenu = new TestMenuBar();
		
		//prepare actor with no permissions
		ActorGroupState lState = new ActorGroupState();
		lState.actorPermissions = Collections.emptySet();
		
		MenuItem lItem = lMenu.createMenu(lTestMenu, testMap, null, lState);
		List<String> lMenuCaptions = ((TestMenuItem)lItem).getCaptions();
		assertEquals(8, lMenuCaptions.size());
		assertEquals("Insert after start", lMenuCaptions.get(0));
		assertEquals("Append to start", lMenuCaptions.get(1));
		assertEquals("Insert before additions", lMenuCaptions.get(2));
		assertEquals("Insert before additions 2", lMenuCaptions.get(3));
		assertEquals("Insert after additions 2", lMenuCaptions.get(4));
		assertEquals("Insert after additions", lMenuCaptions.get(5));
		assertEquals("Append to additions", lMenuCaptions.get(6));
		assertEquals("Insert before end", lMenuCaptions.get(7));
	}
	
	@Test
	public void testCreateMenuPermissions() throws Exception {
		IExtendibleMenuContribution[] lContributions = new IExtendibleMenuContribution[] {CONTRIBUTION6, CONTRIBUTION5, CONTRIBUTION4, CONTRIBUTION3, CONTRIBUTION2, CONTRIBUTION1, CONTRIBUTION7, CONTRIBUTION8};
		ExtendibleMenuFactory lMenu = new ExtendibleMenuFactory(new ExtendibleMenu(), Arrays.asList(lContributions));
		TestMenuBar lTestMenu = new TestMenuBar();
		
		//prepare actor with some permissions
		ActorGroupState lState = new ActorGroupState();
		lState.actorPermissions = Arrays.asList(new String[] {"somePermission"});
		
		MenuItem lItem = lMenu.createMenu(lTestMenu, testMap, null, lState);
		List<String> lMenuCaptions = ((TestMenuItem)lItem).getCaptions();
		assertEquals(8, lMenuCaptions.size());
		
		//set permissions to contributions
		((MenuContribution)CONTRIBUTION1).setPermission(PERMISSION_1); //"Insert after start"
		((MenuContribution)CONTRIBUTION2).setPermission(PERMISSION_2); //"Append to start"
		((MenuContribution)CONTRIBUTION5).setPermission(PERMISSION_1); //"Append to additions"
		((MenuContribution)CONTRIBUTION6).setPermission(PERMISSION_2); //"Insert before end"
		
		lItem = lMenu.createMenu(lTestMenu, testMap, null, lState);
		lMenuCaptions = ((TestMenuItem)lItem).getCaptions();
		assertEquals(4, lMenuCaptions.size());
		assertEquals("Insert before additions", lMenuCaptions.get(0));
		assertEquals("Insert before additions 2", lMenuCaptions.get(1));
		assertEquals("Insert after additions 2", lMenuCaptions.get(2));
		assertEquals("Insert after additions", lMenuCaptions.get(3));

		//prepare actor with PERMISSION_1
		lState.actorPermissions = Arrays.asList(new String[] {PERMISSION_1});
		lItem = lMenu.createMenu(lTestMenu, testMap, null, lState);
		lMenuCaptions = ((TestMenuItem)lItem).getCaptions();
		assertEquals(6, lMenuCaptions.size());
		assertEquals("Insert after start", lMenuCaptions.get(0));
		assertEquals("Insert before additions", lMenuCaptions.get(1));
		assertEquals("Insert before additions 2", lMenuCaptions.get(2));
		assertEquals("Insert after additions 2", lMenuCaptions.get(3));
		assertEquals("Insert after additions", lMenuCaptions.get(4));
		assertEquals("Append to additions", lMenuCaptions.get(5));
		
		//prepare actor with PERMISSION_1
		lState.actorPermissions = Arrays.asList(new String[] {PERMISSION_1, PERMISSION_2});
		lItem = lMenu.createMenu(lTestMenu, testMap, null, lState);
		lMenuCaptions = ((TestMenuItem)lItem).getCaptions();
		assertEquals(8, lMenuCaptions.size());
		assertEquals("Insert after start", lMenuCaptions.get(0));
		assertEquals("Append to start", lMenuCaptions.get(1));
		assertEquals("Insert before additions", lMenuCaptions.get(2));
		assertEquals("Insert before additions 2", lMenuCaptions.get(3));
		assertEquals("Insert after additions 2", lMenuCaptions.get(4));
		assertEquals("Insert after additions", lMenuCaptions.get(5));
		assertEquals("Append to additions", lMenuCaptions.get(6));
		assertEquals("Insert before end", lMenuCaptions.get(7));
	}
	
	// ---
		
	@SuppressWarnings("serial")
	private class TestMenuBar extends MenuBar {
		@Override
		public MenuItem addItem(String inCaption, Resource inIcon, Command inCommand) {
			return new TestMenuItem(this, inCaption, inIcon, inCommand);
		}		
	}
	
	@SuppressWarnings("serial")
	private class TestMenuItem extends MenuItem {
		private List<String> captions = new Vector<String>();
		private MenuBar menuBar;
		public TestMenuItem(MenuBar inMenuBar, String inCaption, Resource inIcon, Command inCommand) {
			inMenuBar.super(inCaption, inIcon, inCommand);
			menuBar = inMenuBar;
		}
		@Override
		public MenuItem addItem(String inCaption, Resource inIcon, Command inCommand) {
			captions.add(inCaption);
			return menuBar.new MenuItem(inCaption, inIcon, inCommand);
		};
		List<String> getCaptions() {
			return captions;
		}		
	}

	private static class ExtendibleMenu extends AbstractExtendibleMenu {
		public String getMenuID() {
			return MENU_ID;
		}
		public String getLabel() {
			return "The extendible menu";
		}
		public int getPosition() {
			return 50;
		}
		public ExtendibleMenuMarker[] getMarkers() {
			return new ExtendibleMenuMarker[] {
					new ExtendibleMenuMarker(EXTENDIBLE_MENU_POSITION_START),
					new ExtendibleMenuMarker(EXTENDIBLE_MENU_POSITION_ADDITIONS),
					new ExtendibleMenuMarker(EXTENDIBLE_MENU_POSITION_END)
			};
		}
	}
	
	private static class MenuContribution implements IExtendibleMenuContribution {
		private String label;
		private String menuID;
		private String permission = "";
		private Position position;

		MenuContribution(String inLabel, String inExtendibleMenuID, Position inPosition) {
			label = inLabel;
			menuID = inExtendibleMenuID;
			position = inPosition;
		}

		@Override
		public String getLabel() {
			return label;
		}
		@Override
		public String getTaskName() {
			return "MyTask";
		}
		@Override
		public List<IVIFMenuItem> getSubMenu() {
			return Collections.emptyList();
		}
		void setPermission(String inPermission) {
			permission = inPermission;
		}
		@Override
		public String getPermission() {
			return permission;
		}
		@Override
		public String getExtendibleMenuID() {
			return menuID;
		}
		@Override
		public Position getPosition() {
			return position;
		}
	}
	
}
