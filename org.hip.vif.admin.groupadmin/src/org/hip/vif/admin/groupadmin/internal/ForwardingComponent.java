/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.admin.groupadmin.internal;

import org.hip.vif.admin.groupadmin.tasks.AdminShowPendingTask;
import org.hip.vif.web.interfaces.IForwarding;
import org.hip.vif.web.interfaces.ITargetConfiguration;
import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.ripla.web.interfaces.IPluggable;

/**
 * The service provider for the <code>IForwarding</code> service.
 * 
 * @author Luthiger Created: 29.11.2011
 */
public class ForwardingComponent implements IForwarding {

	@Override
	public ITargetConfiguration[] getTargets() {
		return new ITargetConfiguration[] { new ITargetConfiguration() {
			@Override
			public Class<? extends IPluggable> getTarget() {
				return AdminShowPendingTask.class;
			}

			@Override
			public String getAlias() {
				return ForwardControllerRegistry.FORWARD_GROUP_ADMIN_PENDING;
			}
		} };
	}

}
