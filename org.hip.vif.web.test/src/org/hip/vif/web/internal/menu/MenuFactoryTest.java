package org.hip.vif.web.internal.menu;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.hip.vif.web.menu.IMenuCommand;
import org.hip.vif.web.menu.VIFMenuComposite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.MenuBar;

/**
 * @author Luthiger
 * Created: 30.10.2011
 */
public class MenuFactoryTest {
	private Map<Integer, IMenuCommand> testMap;

	@Before
	public void setUp() throws Exception {
		testMap = new HashMap<Integer, IMenuCommand>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCreateMenu() {
		String lExpectedLabel = "Test Menu Item";
		
		//menu item that doesn't need a permission
		VIFMenuComposite lMenuItem = new VIFMenuComposite(lExpectedLabel);
		lMenuItem.setTaskName("menu.task.MyTask");
		MenuFactory lMenuFactory = new MenuFactory(lMenuItem);
		
		//prepare actor with no permissions
		ActorGroupState lState = new ActorGroupState();
		lState.actorPermissions = Collections.emptySet();
		
		TestMenuBar lTestMenu = new TestMenuBar();
		lMenuFactory.createMenu(lTestMenu, testMap, null, lState);
		Assert.assertEquals(1, lTestMenu.getCaptions().size());
		Assert.assertEquals(lExpectedLabel, lTestMenu.getCaptions().get(0));
		
		//add permission to menu item
		String lPermission = "permissionToTest";
		lMenuItem.setPermission(lPermission);
		
		lMenuFactory = new MenuFactory(lMenuItem);
		lTestMenu = new TestMenuBar();
		lMenuFactory.createMenu(lTestMenu, testMap, null, lState);
		Assert.assertEquals(0, lTestMenu.getCaptions().size());
		
		//prepare actor with different permission
		lState.actorPermissions = Arrays.asList(new String[] {"somePermission"});
		Assert.assertEquals(0, lTestMenu.getCaptions().size());
		
		//prepare actor with correct permission
		lState.actorPermissions = Arrays.asList(new String[] {lPermission});
		lTestMenu = new TestMenuBar();
		lMenuFactory.createMenu(lTestMenu, testMap, null, lState);
		Assert.assertEquals(1, lTestMenu.getCaptions().size());
		Assert.assertEquals(lExpectedLabel, lTestMenu.getCaptions().get(0));
		
		//prepare actor with additional permission
		lState.actorPermissions = Arrays.asList(new String[] {lPermission, "somePermission"});
		lTestMenu = new TestMenuBar();
		lMenuFactory.createMenu(lTestMenu, testMap, null, lState);
		Assert.assertEquals(1, lTestMenu.getCaptions().size());
		Assert.assertEquals(lExpectedLabel, lTestMenu.getCaptions().get(0));
	}
	
// ---
	
	@SuppressWarnings("serial")
	private class TestMenuBar extends MenuBar {
		private List<String> captions = new Vector<String>();
		
		@Override
		public MenuItem addItem(String inCaption, Resource inIcon, Command inCommand) {
			captions.add(inCaption);
			return new MenuItem(inCaption, inIcon, inCommand);
		}
		
		List<String> getCaptions() {
			return captions;
		}
	}

}
