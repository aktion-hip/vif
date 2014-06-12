/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

package org.hip.kernel.sys;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * We only need the activator to read the bundle's release version.
 *
 * @author Luthiger
 * Created on 17.04.2008
 */
public class FWActivator implements BundleActivator {
	private static BundleContext cBundleContext;
	private static String cVersion = "Release ?.?.?";
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext inContext) throws Exception {
		cBundleContext = inContext;
		Object lVersion = inContext.getBundle().getHeaders().get("Bundle-Version");
		if (lVersion == null) return;
		cVersion = String.format("Release %s", lVersion);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext inContext) throws Exception {
		cBundleContext = null;
	}
	
	public static synchronized BundleContext getContext() {
		return cBundleContext;
	}
	
	/**
	 * @return String the bundle's release version.
	 */
	public static String getVersion() {
		return cVersion;
	}

}
