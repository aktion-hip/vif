/**
    This package is part of the application VIF.
    Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.util;

import java.io.File;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.web.Constants;
import org.ripla.util.ParameterObject;

import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/** Helper class providing functionality for VIF application classes.
 *
 * @author lbenno */
public final class VIFAppHelper {

    private VIFAppHelper() {
        // prevent instantiation
    }

    /** Helper method to initialize the servlet context.<br />
     * This method sets the servlet's context path to <code>VSys</code> and initializes the
     * <code>DataSourceRegistry</code> singleton. */
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

    /** Set generic parameters.<br/>
     * Use e.g.:
     *
     * <pre>
     * ParameterObject lParameters = new ParameterObject();
     * lParameters.set(Constants.KEY_PARAMETER_MEMBER, lMemberID);
     * setParameters(lParameters);
     * </pre>
     *
     * @param inParameters {@link ParameterObject} the parameters to set or <code>null</code> to clear the parameter
     *            settings */
    public static void setParameters(final ParameterObject inParameters) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            VaadinSession.getCurrent().setAttribute(ParameterObject.class,
                    inParameters);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** Returns the generic parameters passed by a task/controller.
     *
     * @param inClear boolean if <code>true</code>, the parameter settings are cleared, if <code>false</code>, they are
     *            retained
     * @return {@link ParameterObject} generich parameters */
    public static ParameterObject getParameters(final boolean inClear) {
        ParameterObject out = null;
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            out = VaadinSession.getCurrent()
                    .getAttribute(ParameterObject.class);
            if (inClear) {
                VaadinSession.getCurrent().setAttribute(ParameterObject.class,
                        null);
            }
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
        return out;
    }

    /** Set's the specified value to the vaadin session.
     *
     * @param inKey String
     * @param inValue Long */
    public static void setValueToSession(final String inKey, final Long inValue) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            VaadinSession.getCurrent().setAttribute(inKey, inValue);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** Get's the value for the specified key from the Vaadin session.
     *
     * @param inKey String
     * @return Long If no value is stored for the name, null is returned. */
    public static Long getValueFromSession(final String inKey) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            return (Long) VaadinSession.getCurrent().getAttribute(inKey);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /** @return String e.g. <code>http://localhost:8084/forum</code> */
    public static String getMainForumURL() {
        final String outServletPath = UI.getCurrent().getPage().getLocation().toString();
        return outServletPath.replaceAll(Constants.CONTEXT_ADMIN, Constants.CONTEXT_FORUM);

    }

    /** @return String e.g. <code>http://localhost:8084/admin</code> */
    public static String getMainAdminURL() {
        final String outServletPath = UI.getCurrent().getPage().getLocation().toString();
        return outServletPath.replaceAll(Constants.CONTEXT_FORUM, Constants.CONTEXT_ADMIN);
    }

}
