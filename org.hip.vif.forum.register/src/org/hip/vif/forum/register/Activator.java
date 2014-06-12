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

package org.hip.vif.forum.register;

import org.hip.vif.core.interfaces.IMessages;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activator for this OSGi bundle.
 * 
 * @author Luthiger
 * Created: 30.09.2011
 */
public class Activator implements BundleActivator {
	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	volatile private static IMessages cMessages;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext inContext) throws Exception {
		LOG.debug("{} started.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		cMessages = new Messages();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext inContext) throws Exception {
		LOG.debug("{} stopped.", inContext.getBundle().getSymbolicName()); //$NON-NLS-1$
		cMessages = null;
	}

	public static IMessages getMessages() {
		return cMessages;
	}

}
