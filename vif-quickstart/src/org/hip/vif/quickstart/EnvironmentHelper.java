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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.hip.vif.quickstart.SplashWindow.ProgressBar;

/** Helper class to create the OSGi runtime environment.<br />
 * This class has to unzip the resources in the runtime part and copy them to the file system.
 *
 * @author Luthiger Created: 19.01.2012 */
public final class EnvironmentHelper { // NOPMD

    private EnvironmentHelper() {
        // prevent instantiation
    }

    /** Factory method.
     *
     * @return {@link EnvironmentHelper} */
    public static EnvironmentHelper createHelper() {
        return new EnvironmentHelper();
    }

    /** Creates the OSGi environment needed for the application by unzipping the libraries contained in the bundle.
     *
     * @param inPort the port the http service should be configured to listen
     * @param inProgress {@link ProgressBar} to signal unzipping progress
     *
     * @throws IOException */
    public void createEnvironment(final String inPort, final ProgressBar inProgress) throws IOException { // NOPMD
        final String lTargetDir = new File(".").getAbsolutePath();
        final JarFile lJar = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()
                .replaceAll("%20", " "));

        int lCount = 0;
        Enumeration<JarEntry> lEntries = lJar.entries();
        while (lEntries.hasMoreElements()) {
            if (lEntries.nextElement().getName().startsWith(Constants.RUNTIME_DIR)) {
                lCount++;
            }
        }
        inProgress.setMaxValue(lCount);

        InputStream lIn = null;
        FileOutputStream lOut = null;
        try {
            lEntries = lJar.entries();
            while (lEntries.hasMoreElements()) {
                final JarEntry lEntry = lEntries.nextElement();
                if (!lEntry.getName().startsWith(Constants.RUNTIME_DIR)) {
                    continue;
                }
                final String lEntryName = getName(lEntry);
                final File lFile = new File(lTargetDir + File.separator + lEntryName);
                if (lFile.exists()) {
                    continue;
                }
                if (lEntry.isDirectory()) {
                    lFile.mkdir();
                    continue;
                }
                inProgress.echoExtracted(lEntryName);
                lIn = lJar.getInputStream(lEntry);
                lOut = new FileOutputStream(lFile);
                while (lIn.available() > 0) {
                    lOut.write(lIn.read());
                }
                if (lEntry.getName().endsWith(Constants.CONFIG_INI_NAME)) {
                    lOut.write(String.format("org.osgi.service.http.port=%s%s", inPort,
                            System.getProperty("line.separator")).getBytes());
                }
                inProgress.progress();
            }
        } finally {
            if (lIn != null) {
                lIn.close();
            }
            if (lOut != null) {
                lOut.close();
            }
            lJar.close();
        }
    }

    private String getName(final JarEntry inEntry) {
        return inEntry.getName().substring(Constants.RUNTIME_DIR.length() + 1);
    }

}
