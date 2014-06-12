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

package org.hip.vif.forum.groups.internal;

import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.forum.groups.tasks.QuestionShowTask;
import org.hip.vif.forum.groups.tasks.RequestsListTask;
import org.hip.vif.web.interfaces.IForwarding;
import org.hip.vif.web.interfaces.ITargetConfiguration;
import org.hip.vif.web.tasks.ForwardTaskRegistry;

/**
 * The service provider for the <code>IForwarding</code> service.
 * 
 * @author Luthiger
 * Created: 28.09.2011
 */
public class ForwardingComponent implements IForwarding {

	/* (non-Javadoc)
	 * @see org.hip.vif.web.interfaces.IForwarding#getTargets()
	 */
	public ITargetConfiguration[] getTargets() {
		return new ITargetConfiguration[] {new ITargetConfiguration() {
			public String getAlias() {
				return ForwardTaskRegistry.FORWARD_REQUEST_LIST;
			}
			public Class<? extends IPluggableTask> getTarget() {
				return RequestsListTask.class;
			}
		},
		new ITargetConfiguration() {
			public String getAlias() {
				return ForwardTaskRegistry.FORWARD_QUESTION_SHOW;
			}
			public Class<? extends IPluggableTask> getTarget() {
				return QuestionShowTask.class;
			}
		}
		};
	}

}
