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

package org.hip.vif.web.util;

import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.interfaces.ITaskConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper class for partlet creation.
 * 
 * @author Luthiger
 * Created: 19.05.2011
 */
public class PartletHelper {
	
	/**
	 * Creates a task configuration from the specified task class.
	 * This helper method can be used as follows:<pre>
	 * new ITaskSet() {
	 *   public ITaskConfiguration[] getTaskConfigurations() {
	 *     return new ITaskConfiguration[] {PartletHelper.createTaskConfiguration(MyTask.class)};
	 *   }
	 *   public String getSetID() {return "myTasks"}
	 * };
	 * </pre>
	 * 
	 * @param inTaskClass {@link IPluggableTask} the task which should be configured
	 * @return {@link ITaskConfiguration} the task configuration which can be used to register the task with the application
	 */
	public static ITaskConfiguration createTaskConfiguration(final Class<? extends IPluggableTask> inTaskClass) {
		final Bundle bundle = FrameworkUtil.getBundle(inTaskClass);
		
		return new ITaskConfiguration() {
			
			@Override
			public String getTaskName() {
				return inTaskClass.getName();
			}
			
			@Override
			public Bundle getBundle() {
				return bundle;
			}
		};
	}

}
