/**
 This package is part of the application VIF.
 Copyright (C) 2010-2015, Benno Luthiger

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
package org.hip.vif.core.util;

import java.io.File;
import java.io.IOException;

import org.hip.kernel.servlet.impl.ServletContainer;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.PreferencesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class providing functionality concerning the workspace.
 *
 * @author Luthiger Created: 23.09.2010 */
public final class WorkspaceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceHelper.class);

    private WorkspaceHelper() {
        // prevent instantiation
    }

    /** Returns the application's root. To create a workspace area, use:
     *
     * <pre>
     * WorkspaceHelper.getRootDir() + File.separator + &quot;myArea&quot;
     * </pre>
     *
     * @return String */
    public static String getRootDir() {
        File outRootDir = getContextDir();
        if (outRootDir != null) {
            if (PreferencesHandler.INSTANCE.isEmbedded()) {
                return getWorkspaceDir(outRootDir);
            }
            else {
                return outRootDir.getParent();
            }
        }

        try {
            outRootDir = getFile(PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_DOCS_ROOT));
            if (outRootDir != null) {
                return outRootDir.getParent();
            }
        } catch (final IOException exc) {
            LOG.error("Error encountered while retrieving the application's doc root!", exc);
        }

        final String lProperty = ServletContainer.getInstance().getBasePath();
        try {
            if (lProperty != null) {
                outRootDir = getFile(PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_DOCS_ROOT), lProperty);
            }
            return outRootDir.getParent();
        } catch (final IOException exc) {
            LOG.error("Error encountered while retrieving the application's doc root!", exc);
        }
        return ".";
    }

    private static String getWorkspaceDir(final File inDir) {
        final File lWorkspaceDir = new File(inDir, ApplicationConstants.WORKSPACE_DIR);
        return lWorkspaceDir.getAbsolutePath();
    }

    private static File getContextDir() {
        final String lContextPath = VSys.getContextPath();
        if (lContextPath == null) {
            return null;
        }

        final File outDir = new File(lContextPath);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        if (outDir.exists()) {
            try {
                return outDir.getCanonicalFile();
            } catch (final IOException exc) {
                return null;
            }
        }
        return null;
    }

    private static File getFile(final String inPath) {
        return getFileChecked(new File(inPath));
    }

    private static File getFile(final String inPath, final String inPrefix) {
        return getFileChecked(new File(inPrefix + File.separator + inPath));
    }

    private static File getFileChecked(final File inDir) {
        try {
            if (inDir.exists()) {
                return inDir.getCanonicalFile();
            }
            return null;
        } catch (final IOException exc) {
            return null;
        }
    }

}
