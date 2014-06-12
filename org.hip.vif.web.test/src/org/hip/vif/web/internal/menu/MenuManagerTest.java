package org.hip.vif.web.internal.menu;


import java.util.Arrays;

import junit.framework.Assert;

import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.IVIFContextMenuItem;
import org.hip.vif.web.util.UseCaseHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * Note: needs OSGi container.
 * 
 * @author Luthiger
 * Created: 08.03.2012
 */
public class MenuManagerTest {
	private static final String SET_ID = "test.set";
	private ActorGroupState state;
	private ContextMenuSet contextMenuSet;

	@Before
	public void setUp() throws Exception {
		state = new ActorGroupState();
		state.actorPermissions = Arrays.asList(new String[] {});
		state.groupState = "";
		state.isGroupAdmin = false;
		state.isPrivateType = false;
		state.isRegistered = false;
		
		contextMenuSet = new ContextMenuSet();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testRenderContextMenu() throws Exception {
		String lSetID = UseCaseHelper.createFullyQualifiedID(SET_ID, this.getClass());
		
		//default
		contextMenuSet.contextMenuItem = createContextMenuItem();
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		Component lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());

		//permission
		//permission: config: no permissions / state: 1 permission
		state.actorPermissions = Arrays.asList(new String[] {"test"});
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//permission: config: needs permission / state: no permissions
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		IVIFContextMenuItem lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).menuPermission = "test";
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.actorPermissions = Arrays.asList(new String[] {});
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//permission: config: needs permission / state: 1 permission but different
		state.actorPermissions = Arrays.asList(new String[] {"some"});
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//permission: config: needs permission / state: same permission 
		state.actorPermissions = Arrays.asList(new String[] {"test"});
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//permission: config: needs permission / state: same permission and another 
		state.actorPermissions = Arrays.asList(new String[] {"test", "some"});
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//group state
		//group state: config: no state / state: some state
		state.groupState = VIFGroupWorkflow.STATE_ACTIVE;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());

		//group state: config: some state / state: no state
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).groupStates = new String[] {VIFGroupWorkflow.STATE_ACTIVE};
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.groupState = "";
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//group state: config: some state / state: different state
		state.groupState = VIFGroupWorkflow.STATE_CLOSED;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//group state: config: some state / state: same state
		state.groupState = VIFGroupWorkflow.STATE_ACTIVE;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//group state: config: various states / state: matching state
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).groupStates = new String[] {VIFGroupWorkflow.STATE_ACTIVE, VIFGroupWorkflow.STATE_OPEN};
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.groupState = VIFGroupWorkflow.STATE_OPEN;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//group admin
		//group admin: config: false / state: true
		state.isGroupAdmin = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());

		//group admin: config: true / state: false
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).needsGroupAmin = true;
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.isGroupAdmin = false;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//group admin: config: true / state: true
		state.isGroupAdmin = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//private type
		//private type: config: false / state: true
		state.isPrivateType = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//private type: config: true / state: false
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).needsTypePrivate = true;
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.isPrivateType = false;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//private type: config: true / state: true
		state.isPrivateType = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//registration
		//registration: config: false / state: true
		state.isRegistered = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
		
		//registration: config: true / state: false
		MenuManager.INSTANCE.removeContextMenuSet(contextMenuSet);
		lContextMenuItem = createContextMenuItem();
		((ContextMenuItem)lContextMenuItem).needsRegistration = true;
		contextMenuSet.contextMenuItem = lContextMenuItem;
		MenuManager.INSTANCE.addContextMenuSet(contextMenuSet);
		state.isRegistered = false;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(0, ((VerticalLayout)lMenu).getComponentCount());
		
		//registration: config: true / state: true
		state.isRegistered = true;
		lMenu = MenuManager.INSTANCE.renderContextMenu(lSetID, state);
		Assert.assertEquals(1, ((VerticalLayout)lMenu).getComponentCount());
	}
	
	private IVIFContextMenuItem createContextMenuItem() {
		return new ContextMenuItem("default menu", "", false, false, false, new String[] {});
	}
	
// ---
	private class ContextMenuSet implements IMenuSet {
		IVIFContextMenuItem contextMenuItem;

		@Override
		public String getSetID() {
			return SET_ID;
		}
		@Override
		public IVIFContextMenuItem[] getContextMenuItems() {
			return new IVIFContextMenuItem[] {contextMenuItem};
		}
		
	}
	
	private class ContextMenuItem implements IVIFContextMenuItem {
		String titleMsg;
		String menuPermission;
		boolean needsGroupAmin;
		boolean needsRegistration;
		boolean needsTypePrivate;
		String[] groupStates;

		ContextMenuItem(String inTitleMsg, String inMenuPermission,
				boolean inNeedsGroupAmin, boolean inNeedsRegistration, boolean inNeedsTypePrivate, String[] inGroupStates) {
			titleMsg = inTitleMsg;
			menuPermission = inMenuPermission;
			needsGroupAmin = inNeedsGroupAmin;
			needsRegistration = inNeedsRegistration;
			needsTypePrivate = inNeedsTypePrivate;
			groupStates = inGroupStates;
		}

		@Override
		public Class<? extends IPluggableTask> getTaskClass() {
			return null;
		}
		@Override
		public String getTitleMsg() {
			return titleMsg;
		}
		@Override
		public String getMenuPermission() {
			return menuPermission;
		}
		@Override
		public boolean needsGroupAmin() {
			return needsGroupAmin;
		}
		@Override
		public boolean needsRegistration() {
			return needsRegistration;
		}
		@Override
		public boolean needsTypePrivate() {
			return needsTypePrivate;
		}
		@Override
		public String[] getGroupStates() {
			return groupStates;
		}
		
	}

}
