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

package org.hip.vif.web.internal.controller;

import org.hip.vif.web.controller.TaskManager;
import org.hip.vif.web.internal.menu.MenuManager;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** OSGi service component class, declared in <code>OSGI-INF/vifEventAdmin.xml</code>.
 *
 * @author Luthiger Created: 22.05.2011
 * @see org.osgi.service.event.EventAdmin */
@Deprecated
public class EventController {
    private final static Logger LOG = LoggerFactory.getLogger(EventController.class);

    /** Injecting the OSGi <code>EventAdmin</code> instance.
     *
     * @param inEventAdmin {@link EventAdmin} */
    protected void setEventAdmin(final EventAdmin inEventAdmin) {
        LOG.debug("Installing OSGi event admin."); //$NON-NLS-1$
        TaskManager.INSTANCE.setEventAdmin(inEventAdmin);
        MenuManager.INSTANCE.setEventAdmin(inEventAdmin);
    }

}
