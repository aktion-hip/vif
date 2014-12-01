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

import org.hip.vif.web.tasks.ForwardControllerRegistry;
import org.ripla.web.interfaces.IForwarding;
import org.ripla.web.interfaces.IForwardingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The client class for service components implementing the <code>IForwarding</code> interface.
 *
 * @author Luthiger Created: 28.09.2011 */
public class ForwardingController {
    private final static Logger LOG = LoggerFactory
            .getLogger(ForwardingController.class);

    /** @param inForwarding {@link IForwarding} bind */
    public void addForwarding(final IForwarding inForwarding) {
        for (final IForwardingConfig config : inForwarding.getForwardingConfigs()) {
            ForwardControllerRegistry.INSTANCE.registerTarget(config.getAlias(), config.getTarget());
            LOG.debug("Registered target controller for forward with alias \"{}\".", config.getAlias()); //$NON-NLS-1$
        }
    }

    /** @param inForwarding {@link IForwarding} unbind */
    public void removeForwarding(final IForwarding inForwarding) {
        for (final IForwardingConfig config : inForwarding.getForwardingConfigs()) {
            ForwardControllerRegistry.INSTANCE.unregisterTarget(config.getAlias());
            LOG.debug("Unregistered target controller for forward with alias \"{}\".", config.getAlias()); //$NON-NLS-1$
        }
    }

}
