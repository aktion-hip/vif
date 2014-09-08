/**
 *
 */
package org.hip.vif.web.util;

import java.io.File;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.service.PreferencesHandler;

import com.vaadin.server.VaadinServlet;

/** Helper class providing functionality for VIF application classes.
 *
 * @author lbenno */
public final class VIFAppHelper {

    private VIFAppHelper() {
        // prevent instantiation
    }

    public static void initializeContext() {
        // String lContextDir = getContext().getBaseDirectory().getPath();
        final String lContextDir = VaadinServlet.getCurrent().getServletContext().getContextPath();
        if (lContextDir.length() <= 1) {
            // embedded app, i.e. Jetty
            VSys.useConfPath(false);
            final String lConfigPath = new File("").getAbsolutePath(); //$NON-NLS-1$
            VSys.setContextPath(lConfigPath);
        }
        else {
            // OSGi in servlet container, e.g. Tomcat
            VSys.useConfPath(true);
            VSys.setContextPath(lContextDir);
        }
        DataSourceRegistry.INSTANCE.setActiveConfiguration(PreferencesHandler.INSTANCE.getDBConfiguration());
    }

}
