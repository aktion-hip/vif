/**
    This package is part of the application VIF.
    Copyright (C) 2011-2015, Benno Luthiger

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

package org.hip.vif.quickstart;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/** Helper class to start the OSGi runtime on the class path.
 *
 * @author Luthiger Created: 20.01.2012 */
public class OSGiStarter {
    private static final Logger LOG = Logger.getLogger(OSGiStarter.class.getName());

    private final Framework framework;
    private final File workingDir;

    /** Private constructor.
     *
     * @param inPort String the port to configure the http service listening
     * @param inWorkingDir File */
    private OSGiStarter(final String inPort, final File inWorkingDir) {
        workingDir = inWorkingDir;
        final FrameworkFactory lFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

        final Map<String, String> lConfiguration = new HashMap<String, String>();
        // lConfiguration.put(org.osgi.framework.Constants.FRAMEWORK_STORAGE_CLEAN, "true");
        lConfiguration.put(Constants.OSGI_PROPERTY_CONSOLE_PORT, Constants.OSGI_CONSOLE_PORT);
        lConfiguration.put(Constants.OSGI_PROPERTY_HTTP_PORT, inPort);
        framework = lFactory.newFramework(lConfiguration);
    }

    /** Starts the OSGi runtime.
     *
     * @throws IOException */
    public void run() throws IOException {
        try {
            framework.start();
        } catch (final BundleException exc) {
            LOG.log(Level.SEVERE, "Error encountered while starting the OSGi runtime!", exc);
        }
    }

    /** Installs the bundles in the auto lookup directory. */
    public void installBundles() {
        final File lLookupDir = new File(workingDir, Constants.PLUGINS_DIR);
        final File[] lBundleFiles = lLookupDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File inDir, final String inName) {
                return inName.endsWith(".jar");
            }
        });

        final BundleContext lContext = framework.getBundleContext();
        final List<Bundle> lInstalledBundles = new LinkedList<Bundle>();
        for (final File lFile : lBundleFiles) {
            try {
                lInstalledBundles.add(lContext.installBundle("file:" + lFile.getAbsolutePath()));
                LOG.finer("Installed bundle " + lFile.getAbsolutePath());
            } catch (final BundleException exc) {
                LOG.log(Level.SEVERE, "Error encountered while installing OSGi bundles!", exc);
            }
        }

        for (final Bundle lBundle : lInstalledBundles) {
            try {
                if (lBundle.getHeaders().get(org.osgi.framework.Constants.FRAGMENT_HOST) == null) {
                    lBundle.start();
                    LOG.finer("Installed bundle " + lBundle.getSymbolicName());
                }
            } catch (final BundleException exc) {
                LOG.log(Level.SEVERE, "Error encountered while starting OSGi bundles!", exc);
            }
        }
    }

    /** Stops the OSGi runtime.
     *
     * @throws InterruptedException */
    public void shutdown() {
        try {
            framework.stop();
            framework.waitForStop(0);
        } catch (final BundleException exc) {
            LOG.log(Level.SEVERE, "Error encountered while shutting down the OSGi runtime!", exc);
        } catch (final InterruptedException exc) {
            LOG.log(Level.SEVERE, "Error encountered while shutting down the OSGi runtime!", exc);
        } finally {
            System.exit(0);
        }
    }

    /** Factory method, returns an <code>OSGiStarter</code> instance.
     *
     * @param inPort String the port the OSGi http service is listing
     * @param inWorkingDir {@link File} the application's working directory
     * @return {@link OSGiStarter} */
    public static OSGiStarter getOSGi(final String inPort, final File inWorkingDir) {
        return new OSGiStarter(inPort, inWorkingDir);
    }

}
