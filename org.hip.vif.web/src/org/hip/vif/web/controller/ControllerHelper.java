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

package org.hip.vif.web.controller;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.interfaces.IMenuSet;
import org.hip.vif.web.interfaces.ITaskConfiguration;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.interfaces.IUseCase;
import org.hip.vif.web.interfaces.IUseCaseAdmin;
import org.hip.vif.web.interfaces.IUseCaseForum;
import org.hip.vif.web.internal.menu.ExtendibleMenuHandler;
import org.hip.vif.web.internal.menu.MenuFactory;
import org.hip.vif.web.internal.menu.MenuManager;
import org.hip.vif.web.menu.IExtendibleMenuContribution;
import org.hip.vif.web.menu.IExtendibleMenuContributions;
import org.hip.vif.web.menu.IVIFMenuExtendible;
import org.hip.vif.web.menu.IVIFMenuItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class acting as central registery for all use cases. 
 * 
 * @author Luthiger
 * Created: 16.05.2011
 */
public enum ControllerHelper {
	INSTANCE;
	
	private static final Logger LOG = LoggerFactory.getLogger(ControllerHelper.class);
	private static final String PREFIX_ROOT = "/"; //$NON-NLS-1$
	private static final String PREFIX_BIN = "/bin/"; //$NON-NLS-1$
	private static final String SUFFIX_CLASS = ".class"; //$NON-NLS-1$
	
	private List<IUseCase> useCasesForum = new Vector<IUseCase>();
	private List<IUseCase> useCasesAdmin = new Vector<IUseCase>();
	private Map<String, ExtendibleMenuHandler> extendibleMenus = new HashMap<String, ExtendibleMenuHandler>();
	
	// --- forum ---
	
	/**
	 * Create the menu based on the registered use cases.
	 * 
	 * @return Collection&lt;MenuFactory&gt; the collection of menus for the forum.
	 */
	public Collection<MenuFactory> getForumMenus() {
		return getMenus(useCasesForum);
	}

	/**
	 * Adds the specified forum use case.
	 * 
	 * @param inUseCase {@link IUseCaseForum}
	 */
	public void addForumUseCase(IUseCaseForum inUseCase) {
		//register menu
		useCasesForum.add(inUseCase);
		//register task classes
		TaskManager.INSTANCE.addForumTaskSet(inUseCase.getTaskSet());
		TaskManager.INSTANCE.addForumTaskSet(lookupTasks(inUseCase.getTaskClasses(), inUseCase.getClass()));
		//register sub menus
		for (IMenuSet lMenuSet : inUseCase.getContextMenus()) {
			MenuManager.INSTANCE.addContextMenuSet(lMenuSet);
		}
		LOG.debug("Added forum use case {}.", inUseCase.toString()); //$NON-NLS-1$
	}

	/**
	 * Removes the specified forum use case.
	 * 
	 * @param inUseCase {@link IUseCaseForum}
	 */
	public void removeForumUseCase(IUseCaseForum inUseCase) {
		useCasesForum.remove(inUseCase);
		TaskManager.INSTANCE.removeForumTaskSet(inUseCase.getTaskSet());
		TaskManager.INSTANCE.removeForumTaskSet(lookupTasks(inUseCase.getTaskClasses(), inUseCase.getClass()));
		for (IMenuSet lMenuSet : inUseCase.getContextMenus()) {
			MenuManager.INSTANCE.removeContextMenuSet(lMenuSet);
		}
		LOG.debug("Removed forum use case {}.", inUseCase.toString()); //$NON-NLS-1$
	}
	
	// --- admin ---
	
	/**
	 * Create the menu based on the registered use cases.
	 * 
	 * @return Collection&lt;MenuFactory&gt; the collection of menus for the admin.
	 */
	public Collection<MenuFactory> getAdminMenus() {
		return getMenus(useCasesAdmin);
	}
	
	/**
	 * Adds the specified admin use case.
	 * 
	 * @param inUseCase {@link IUseCaseAdmin}
	 */
	public void addAdminUseCase(IUseCaseAdmin inUseCase) {
		//register menu
		useCasesAdmin.add(inUseCase);
		//register task classes
		TaskManager.INSTANCE.addAdminTaskSet(inUseCase.getTaskSet());
		TaskManager.INSTANCE.addAdminTaskSet(lookupTasks(inUseCase.getTaskClasses(), inUseCase.getClass()));
		//register sub menus
		for (IMenuSet lMenuSet : inUseCase.getContextMenus()) {
			MenuManager.INSTANCE.addContextMenuSet(lMenuSet);
		}
		LOG.debug("Added admin use case {}.", inUseCase.toString()); //$NON-NLS-1$
	}
	
	/**
	 * Removes the specified admin use case.
	 * 
	 * @param inUseCase {@link IUseCaseAdmin}
	 */
	public void removeAdminUseCase(IUseCaseAdmin inUseCase) {
		useCasesAdmin.remove(inUseCase);
		TaskManager.INSTANCE.removeAdminTaskSet(inUseCase.getTaskSet());
		TaskManager.INSTANCE.removeAdminTaskSet(lookupTasks(inUseCase.getTaskClasses(), inUseCase.getClass()));
		for (IMenuSet lMenuSet : inUseCase.getContextMenus()) {
			MenuManager.INSTANCE.removeContextMenuSet(lMenuSet);
		}
		LOG.debug("Removed admin use case {}.", inUseCase.toString()); //$NON-NLS-1$
	}
	
// --- extendible menu contributions ---

	/**
	 * Registers the contributions to the extendible menus.
	 * 
	 * @param inContributions {@link IExtendibleMenuContributions}
	 */
	public void registerMenuContributions(IExtendibleMenuContributions inContributions) {
		for (IExtendibleMenuContribution lContribution : inContributions.getContributions()) {
			String lMenuID = lContribution.getExtendibleMenuID();
			ExtendibleMenuHandler lExtendibleMenu = extendibleMenus.get(lMenuID);
			if (lExtendibleMenu == null) {
				extendibleMenus.put(lMenuID, new ExtendibleMenuHandler(lContribution));
			}
			else {
				lExtendibleMenu.addContribution(lContribution);
			}
		}
		
	}	
	
	/**
	 * Unregisters the contributions to the extendible menus.
	 * 
	 * @param inContributions {@link IExtendibleMenuContributions}
	 */
	public void unregisterMenuContributions(IExtendibleMenuContributions inContributions) {
		for (IExtendibleMenuContribution lContribution : inContributions.getContributions()) {
			ExtendibleMenuHandler lExtendibleMenu = extendibleMenus.get(lContribution.getExtendibleMenuID());
			if (lExtendibleMenu != null) {
				lExtendibleMenu.removeContribution(lContribution);
			}
		}
	}

	
// --- unspecific methods ---
	
	private Collection<MenuFactory> getMenus(List<IUseCase> inUseCases) {
		List<MenuFactory> outFactories = new Vector<MenuFactory>();		
		for (IUseCase lUseCase : inUseCases) {
			IVIFMenuItem lMenu = lUseCase.getMenu();
			if (lMenu instanceof IVIFMenuExtendible) {
				String lMenuID = ((IVIFMenuExtendible) lMenu).getMenuID();
				ExtendibleMenuHandler lExtendibleMenu = extendibleMenus.get(lMenuID);
				if (lExtendibleMenu != null) {
					outFactories.add(lExtendibleMenu.getMenuFactory((IVIFMenuExtendible) lMenu));					
				}
			}
			else {				
				outFactories.add(new MenuFactory(lUseCase.getMenu()));
			}
		}
		Collections.sort(outFactories);
		return outFactories;		
	}
	
	private ITaskSet lookupTasks(Package inTaskClasses, Class<?> inClass) {
		if (inTaskClasses == null) return new EmptyTaskSet();
		
		String lPackagName = inTaskClasses.getName();
		String lPath = lPackagName.replace(".", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		Bundle lBundle = FrameworkUtil.getBundle(inClass);
		Enumeration<?> lTasks = lBundle.findEntries(String.format("%s%s", PREFIX_BIN, lPath), "*"+SUFFIX_CLASS, false); //$NON-NLS-1$ //$NON-NLS-2$
		if (lTasks != null) {
			return createTaskSet(lTasks, lBundle, PREFIX_BIN);
		}
		lTasks = lBundle.findEntries(String.format("%s%s", PREFIX_ROOT, lPath), "*"+SUFFIX_CLASS, false); //$NON-NLS-1$ //$NON-NLS-2$
		if (lTasks != null) {
			return createTaskSet(lTasks, lBundle, PREFIX_ROOT);
		}
		return new EmptyTaskSet();
	}
	
	private ITaskSet createTaskSet(Enumeration<?> inTasks, Bundle inBundle, String inPrefix) {
		Collection<ITaskConfiguration> lTaskConfigurations = new Vector<ITaskConfiguration>();
		while (inTasks.hasMoreElements()) {
			ITaskConfiguration lTaskConfiguration = createTaskConfiguration((URL) inTasks.nextElement(), inBundle, inPrefix);
			if (lTaskConfiguration != null) {
				lTaskConfigurations.add(lTaskConfiguration);
			}
		}
		return new TaskSet(lTaskConfigurations);
	}
	
	@SuppressWarnings("unchecked")
	private ITaskConfiguration createTaskConfiguration(URL inTask, Bundle inBundle, String inPrefix) {
		String lPath = inTask.getPath();
		String lClassName = lPath.substring(inPrefix.length(), lPath.length() - SUFFIX_CLASS.length()).replace("/", "."); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			Class<IPluggableTask> lClass = (Class<IPluggableTask>) inBundle.loadClass(lClassName);
			Annotation lPartletAnnotations = lClass.getAnnotation(Partlet.class);
			if (lPartletAnnotations != null) {
				return new TaskConfiguration(inBundle, lClassName);
			}
		} 
		catch (ClassNotFoundException exc) {
			//intentionally left empty
		}
		catch (NoClassDefFoundError exc) {
			//intentionally left empty
		}
		return null;
	}
	
// --- private classes ---
	
	private static class TaskSet implements ITaskSet {
		private ITaskConfiguration[] tasks;
		
		TaskSet(Collection<ITaskConfiguration> inTasks) {
			tasks = new ITaskConfiguration[inTasks.size()];
			Iterator<ITaskConfiguration> lTasks = inTasks.iterator();
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = lTasks.next();
			}
		}
		@Override
		public ITaskConfiguration[] getTaskConfigurations() {
			return tasks;
		}
	}
	
	private static class EmptyTaskSet implements ITaskSet {
		@Override
		public ITaskConfiguration[] getTaskConfigurations() {
			return new ITaskConfiguration[] {};
		}
	}
	
	private static class TaskConfiguration implements ITaskConfiguration {
		private Bundle bundle;
		private String taskName;

		TaskConfiguration(Bundle inBundle, String inTaskName) {
			bundle = inBundle;
			taskName = inTaskName;
		}
		@Override
		public Bundle getBundle() {
			return bundle;
		}
		@Override
		public String getTaskName() {
			return taskName;
		}
	}

}