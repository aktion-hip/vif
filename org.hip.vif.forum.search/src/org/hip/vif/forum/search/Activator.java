/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.forum.search;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Activator for this OSGi bundle.
 *
 * @author Luthiger Created: 20.03.2008 */
public class Activator implements BundleActivator {
    private final static Logger LOG = LoggerFactory.getLogger(Activator.class);

    volatile private static IMessages cMessages;

    @Override
    public void start(final BundleContext inContext) throws Exception {
        LOG.debug("{} started.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
        cMessages = new Messages();
    }

    @Override
    public void stop(final BundleContext inContext) throws Exception {
        LOG.debug("{} stopped.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
        cMessages = null;
    }

    public static IMessages getMessages() {
        return cMessages;
    }

}
