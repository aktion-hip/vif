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

import java.util.Hashtable;
import java.util.Map;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.web.interfaces.ITaskConfiguration;
import org.hip.vif.web.interfaces.ITaskSet;
import org.hip.vif.web.tasks.DefaultVIFView;
import org.osgi.framework.Bundle;
import org.osgi.service.event.EventAdmin;
import org.ripla.web.util.UseCaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

/** Singleton instance, responsible for managing all tasks.
 *
 * @author Luthiger Created: 19.05.2011 */
@Deprecated
public enum TaskManager {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    private final Map<String, BundleClassLoader> forumTaskMappingTable = new Hashtable<String, BundleClassLoader>();
    private final Map<String, BundleClassLoader> adminTaskMappingTable = new Hashtable<String, BundleClassLoader>();

    private EventAdmin eventAdmin;

    // --- bind ---

    /** Registers the specified set of forum tasks to the task manager.
     * 
     * @param inTaskSet {@link ITaskSet} */
    public void addForumTaskSet(final ITaskSet inTaskSet) {
        addTaskSet(inTaskSet, forumTaskMappingTable);
    }

    /** Registers the specified set of admin tasks to the task manager.
     * 
     * @param inTaskSet {@link ITaskSet} */
    void addAdminTaskSet(final ITaskSet inTaskSet) {
        addTaskSet(inTaskSet, adminTaskMappingTable);
    }

    private void addTaskSet(final ITaskSet inTaskSet, final Map<String, BundleClassLoader> inMap) {
        for (final ITaskConfiguration lTaskConfiguration : inTaskSet.getTaskConfigurations()) {
            final Bundle lBundle = lTaskConfiguration.getBundle();
            final String lTaskName = lTaskConfiguration.getTaskName();
            inMap.put(UseCaseHelper.createFullyQualifiedTaskName(lBundle, lTaskName), new BundleClassLoader(lTaskName,
                    lBundle));
        }
    }

    // --- unbind ---

    /** Unregisters the specified set of forum tasks from the task manager.
     * 
     * @param inTaskSet */
    public void removeForumTaskSet(final ITaskSet inTaskSet) {
        removeTaskSet(inTaskSet, forumTaskMappingTable);
    }

    /** Unregisters the specified set of admin tasks from the task manager.
     * 
     * @param inTaskSet */
    public void removeAdminTaskSet(final ITaskSet inTaskSet) {
        removeTaskSet(inTaskSet, adminTaskMappingTable);
    }

    private void removeTaskSet(final ITaskSet inTaskSet, final Map<String, BundleClassLoader> inMap) {
        for (final ITaskConfiguration lTaskConfiguration : inTaskSet.getTaskConfigurations()) {
            final Bundle lBundle = lTaskConfiguration.getBundle();
            final String lTaskName = lTaskConfiguration.getTaskName();
            final BundleClassLoader lLoader = inMap.get(UseCaseHelper.createFullyQualifiedTaskName(lBundle, lTaskName));
            if (lLoader != null) {
                lLoader.dispose();
                inMap.remove(lLoader);
            }
        }
    }

    // --- task processing ---

    /** Loads the specified forum task.<br />
     * Note: if no forum task is found with the specified task name, the admin task registry is searched as failover.
     * 
     * @param inTaskName String the task's fully qualified name, i.e. <code>org.hip.vif.mybundle/mytask</code>
     * @return {@link Component} the component instance loaded from the bundle
     * @throws NoTaskFoundException */
    public Component getForumContent(final String inTaskName) throws NoTaskFoundException {
        BundleClassLoader lTaskLoader = forumTaskMappingTable.get(inTaskName);
        if (lTaskLoader == null) {
            lTaskLoader = adminTaskMappingTable.get(inTaskName);
        }
        if (lTaskLoader == null) {
            throw new NoTaskFoundException(inTaskName);
        }
        return runTask(lTaskLoader);
    }

    /** Loads the specified admin task.
     * 
     * @param inTaskName String the task's fully qualified name, i.e. <code>org.hip.vif.mybundle/mytask</code>
     * @return {@link Component} the component instance loaded from the bundle
     * @throws NoTaskFoundException */
    public Component getAdminContent(final String inTaskName) throws NoTaskFoundException {
        BundleClassLoader lTaskLoader = adminTaskMappingTable.get(inTaskName);
        if (lTaskLoader == null) {
            lTaskLoader = forumTaskMappingTable.get(inTaskName);
        }
        if (lTaskLoader == null) {
            throw new NoTaskFoundException(inTaskName);
        }
        return runTask(lTaskLoader);
    }

    /** This is the central part of the application where registered task provided by different bundles are executed.
     * 
     * @param lTaskLoader {@link BundleClassLoader} the bundle class loader capable to load the task.
     * @return {@link Component} the ui view displaying the result of the task. */
    private Component runTask(final BundleClassLoader lTaskLoader) {
        try {
            final IPluggableTask lTask = lTaskLoader.createTask();
            lTask.setEventAdmin(eventAdmin);
            ApplicationData.pushToHistory(lTask);
            ApplicationData.setActiveMenuItem(lTaskLoader.getSymbolicName());
            return lTask.run();
        } catch (final Exception exc) {
            Throwable lThrowable = exc;
            if (exc instanceof VException) {
                lThrowable = ((VException) exc).getRootCause();
            }
            LOG.error("Problem during task execution.", lThrowable); //$NON-NLS-1$
            return new DefaultVIFView(exc);
        }
    }

    /** Sets the OSGi event admin.
     * 
     * @param inEventAdmin {@link EventAdmin} */
    public void setEventAdmin(final EventAdmin inEventAdmin) {
        eventAdmin = inEventAdmin;
        LOG.trace("Set OSGi event admin to VIF task manager."); //$NON-NLS-1$
    }

    // ---

    private static class BundleClassLoader {
        private final String taskName;
        private Bundle bundle;

        public BundleClassLoader(final String inTaskName, final Bundle inBundle) {
            taskName = inTaskName;
            bundle = inBundle;
        }

        /** Uses the registered <code>Bundle</code> to load the task.
         * 
         * @return IPluggableTask The task.
         * @throws ClassNotFoundException
         * @throws InstantiationException
         * @throws IllegalAccessException */
        public IPluggableTask createTask() throws ClassNotFoundException, InstantiationException,
                IllegalAccessException {
            final Class<?> lClass = bundle.loadClass(taskName);
            return (IPluggableTask) lClass.newInstance();
        }

        @Override
        public String toString() {
            return taskName;
        }

        String getSymbolicName() {
            return bundle.getSymbolicName();
        }

        public void dispose() {
            bundle = null;
        }
    }

}
