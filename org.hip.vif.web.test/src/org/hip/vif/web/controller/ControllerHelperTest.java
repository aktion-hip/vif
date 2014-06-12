package org.hip.vif.web.controller;


import java.util.List;

import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskConfiguration;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCase;
import org.hip.vif.web.interfaces.IUseCaseAdmin;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.hip.vif.web.util.PartletHelper;
import org.hip.vif.web.util.UseCaseHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * Note: needs OSGi container.
 * 
 * @author Luthiger
 * Created: 03.07.2011
 */
public class ControllerHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddForumUseCase() throws Exception {
		//no task registered so far, therefore, requesting a content returns the DefaultVIFView
		Component lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());

		TestUseCase lUseCase = new TestUseCase(new MenuItem(), TestTask.class.getPackage(), UseCaseHelper.EMPTY_TASK_SET);
		ControllerHelper.INSTANCE.addForumUseCase(lUseCase);
		
		//the test task, if correctly registered, returns null when runned
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertNull(lComponent);
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestSpecialTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());

		//removing the use case unregisteres the task class
		ControllerHelper.INSTANCE.removeForumUseCase(lUseCase);
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());
		
		//use case with hard wired task set
		lUseCase = new TestUseCase(new MenuItem(), TestTask.class.getPackage(), new ITaskSet() {
			@Override
			public ITaskConfiguration[] getTaskConfigurations() {
				return new ITaskConfiguration[] {PartletHelper.createTaskConfiguration(TestSpecialTask.class)};
			}
		});
		ControllerHelper.INSTANCE.addForumUseCase(lUseCase);
		
		//the special test task, if correctly registered, returns a VerticalLayout when runned
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertNull(lComponent);
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestSpecialTask.class));
		Assert.assertEquals(VerticalLayout.class, lComponent.getClass());
		
		//removing the use case unregisteres the task class
		ControllerHelper.INSTANCE.removeForumUseCase(lUseCase);
		lComponent = TaskManager.INSTANCE.getForumContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());		
	}
	
	@Test
	public void testAddAdminUseCase() throws Exception {
		//no task registered so far, therefore, requesting a content returns the DefaultVIFView
		Component lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());
		
		TestUseCase lUseCase = new TestUseCase(new MenuItem(), TestTask.class.getPackage(), UseCaseHelper.EMPTY_TASK_SET);
		ControllerHelper.INSTANCE.addAdminUseCase(lUseCase);
		
		//the test task, if correctly registered, returns null when runned
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertNull(lComponent);
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestSpecialTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());
		
		//removing the use case unregisteres the task class
		ControllerHelper.INSTANCE.removeAdminUseCase(lUseCase);
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());
		
		//use case with hard wired task set
		lUseCase = new TestUseCase(new MenuItem(), TestTask.class.getPackage(), new ITaskSet() {
			@Override
			public ITaskConfiguration[] getTaskConfigurations() {
				return new ITaskConfiguration[] {PartletHelper.createTaskConfiguration(TestSpecialTask.class)};
			}
		});
		ControllerHelper.INSTANCE.addAdminUseCase(lUseCase);
		
		//the special test task, if correctly registered, returns a VerticalLayout when runned
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertNull(lComponent);
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestSpecialTask.class));
		Assert.assertEquals(VerticalLayout.class, lComponent.getClass());
		
		//removing the use case unregisteres the task class
		ControllerHelper.INSTANCE.removeAdminUseCase(lUseCase);
		lComponent = TaskManager.INSTANCE.getAdminContent(UseCaseHelper.createFullyQualifiedTaskName(TestTask.class));
		Assert.assertEquals(DefaultVIFView.class, lComponent.getClass());		
	}
	
	@Test
	public void testGetForumMenus() throws Exception {
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getForumMenus().size());
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getAdminMenus().size());

		TestUseCase lUseCase = new TestUseCase(new MenuItem(), TestTask.class.getPackage(), UseCaseHelper.EMPTY_TASK_SET);
		ControllerHelper.INSTANCE.addForumUseCase(lUseCase);
		
		Assert.assertEquals(1, ControllerHelper.INSTANCE.getForumMenus().size());
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getAdminMenus().size());
		
		ControllerHelper.INSTANCE.removeForumUseCase(lUseCase);
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getForumMenus().size());
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getAdminMenus().size());
		
		ControllerHelper.INSTANCE.addAdminUseCase(lUseCase);
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getForumMenus().size());
		Assert.assertEquals(1, ControllerHelper.INSTANCE.getAdminMenus().size());
		
		ControllerHelper.INSTANCE.removeAdminUseCase(lUseCase);
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getForumMenus().size());
		Assert.assertEquals(0, ControllerHelper.INSTANCE.getAdminMenus().size());
	}
	
// --- 
	
	private static class TestUseCase implements IUseCase, IUseCaseForum, IUseCaseAdmin {
		private IVIFMenuItem menuItem;
		private Package taskPackage;
		private ITaskSet taskSet;

		TestUseCase(IVIFMenuItem inMenuItem, Package inTaskPackage, ITaskSet inTaskSet) {
			menuItem = inMenuItem;
			taskPackage = inTaskPackage;
			taskSet = inTaskSet;
		}

		@Override
		public IVIFMenuItem getMenu() {
			return menuItem;
		}
		@Override
		public Package getTaskClasses() {
			return taskPackage;
		}
		@Override
		public ITaskSet getTaskSet() {
			return taskSet;
		}
		@Override
		public IMenuSet[] getContextMenus() {
			return UseCaseHelper.EMPTY_SUB_MENU_SET;
		}
	}
	
	private static class MenuItem implements IVIFMenuItem {
		@Override
		public String getLabel() {
			return "Click to test";
		}
		@Override
		public int getPosition() {
			return 0;
		}
		@Override
		public List<IVIFMenuItem> getSubMenu() {
			return null;
		}
		@Override
		public String getTaskName() {
			return null;
		}
		@Override
		public String getPermission() {
			return null;
		}
	}

}
