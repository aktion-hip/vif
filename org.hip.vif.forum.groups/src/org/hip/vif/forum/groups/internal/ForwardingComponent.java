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

package org.hip.vif.forum.groups.internal;

import org.hip.vif.forum.groups.tasks.QuestionShowTask;
import org.hip.vif.forum.groups.tasks.RequestsListTask;
import org.ripla.web.interfaces.IForwarding;
import org.ripla.web.interfaces.IForwardingConfig;
import org.ripla.web.interfaces.IPluggable;

/** The service provider for the <code>IForwarding</code> service.
 *
 * @author Luthiger Created: 28.09.2011 */
public class ForwardingComponent implements IForwarding {

    @Override
    public IForwardingConfig[] getForwardingConfigs() {
        return new IForwardingConfig[] { new IForwardingConfig() {
            @Override
            public String getAlias() {
                // TODO
                return "ForwardTaskRegistry.FORWARD_REQUEST_LIST";
            }

            @Override
            public Class<? extends IPluggable> getTarget() {
                return RequestsListTask.class;
            }
        },
        new IForwardingConfig() {
            @Override
            public String getAlias() {
                // TODO
                return "ForwardTaskRegistry.FORWARD_QUESTION_SHOW";
            }

            @Override
            public Class<? extends IPluggable> getTarget() {
                return QuestionShowTask.class;
            }
        }
        };
    }

}
