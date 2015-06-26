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
import java.net.URI;
import java.sql.SQLException;

import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.WorkspaceHelper;
import org.hip.vif.web.Constants;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.ripla.exceptions.LoginException;
import org.ripla.interfaces.IAuthenticator;
import org.ripla.util.ParameterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/** Helper class providing functionality for VIF application classes.
 *
 * @author lbenno */
public final class VIFAppHelper {
    private static final Logger LOG = LoggerFactory.getLogger(VIFAppHelper.class);

    private static final String NL = System.getProperty("line.separator"); // NOPMD by lbenno

    private VIFAppHelper() {
        // prevent instantiation
    }

    /** Helper method to initialize the servlet context.<br />
     * This method sets the servlet's context path to <code>VSys</code> and initializes the
     * <code>DataSourceRegistry</code> singleton. */
    public static void initializeContext() {
        final PreferencesHandler lPreferences = PreferencesHandler.INSTANCE;
        if (!lPreferences.isVifInitialized()) {
            final String lContextDir = VaadinServlet.getCurrent().getServletContext().getContextPath();
            if (lContextDir.length() <= 1) { // NOPMD by lbenno
                // embedded app, i.e. Jetty
                VSys.useConfPath(false);
                final String lConfigPath = new File("").getAbsolutePath(); //$NON-NLS-1$
                VSys.setContextPath(lConfigPath);
            }
            else {
                // OSGi in servlet container, e.g. Tomcat
                VSys.useConfPath(true);
                VSys.setContextPath(VaadinServlet.getCurrent().getServletContext().getRealPath(""));
            }
            DataSourceRegistry.INSTANCE.setActiveConfiguration(lPreferences.getDBConfiguration());
            setLogDir();
            lPreferences.setVifInitialization(true);
        }
    }

    private static void setLogDir() {
        String lPath = PreferencesHandler.INSTANCE.get(PreferencesHandler.KEY_LOG_PATH,
                ApplicationConstants.LOG_DESTINATION_DFT);
        if (lPath.charAt(0) == '.') { // NOPMD
            lPath = lPath.substring(1);
        }
        lPath = WorkspaceHelper.getRootDir() + lPath; // NOPMD
        System.setProperty(ApplicationConstants.LOG_DESTINATION, lPath);
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
        return new URLHelper(UI.getCurrent().getPage().getLocation(), true).getUrl();

    }

    /** @return String e.g. <code>http://localhost:8084/admin</code> */
    public static String getMainAdminURL() {
        return new URLHelper(UI.getCurrent().getPage().getLocation(), false).getUrl();
    }

    /** Creates an instance of <code>IAuthenticator</code> for the <code>org.ripla.interfaces.IAppConfiguration</code>.
     *
     * @return {@link IAuthenticator} */
    public static IAuthenticator createLoginAuthenticator() {
        return new IAuthenticator() {

            @Override
            public User authenticate(final String inName, // NOPMD
                    final String inPassword, final UserAdmin inUserAdmin)
                    throws LoginException {
                try {
                    // first we check whether the user can be authenticated
                    // this will create an OSGi user object when the VIF user is set to the context
                    MemberUtility.INSTANCE.getActiveAuthenticator().checkAuthentication(inName, inPassword);
                    // if authentication is successful, we can retrieve the OSGi user object from the user admin
                    // instance
                    return inUserAdmin.getUser(ApplicationConstants.VIF_USER, inName);
                } catch (final InvalidAuthenticationException exc) {
                    final StringBuilder lLog = new StringBuilder(200);
                    lLog.append(NL).append("   Note: Invalid try to authenticate:").append(NL).append("   User: ")
                    .append(inName).append(NL).append("   IP number: ")
                    .append(Page.getCurrent().getWebBrowser().getAddress()).append(NL);
                    LOG.warn(new String(lLog));
                    throw new LoginException(exc.getMessage()); // NOPMD
                } catch (final VException | SQLException exc) {
                    LOG.warn("Problem during login encoutered!", exc);
                    throw new LoginException(exc.getMessage()); // NOPMD
                }
            }
        };
    }

    // ---

    /** scheme://host:port/path */
    private static class URLHelper {
        private static final String TMPL = "%s://%s%s";
        private static final String TMPL_PORT = "%s://%s:%s%s";
        private final String url;

        /** @param inURI {@link URI} the URI to evaluate
         * @param inForum boolean <code>true</code> if the URL is created for the forum app, else for the admin app */
        protected URLHelper(final URI inURI, final boolean inForum) {
            if (inURI.getPort() == -1) {
                url = String.format(TMPL, inURI.getScheme(), inURI.getHost(), getPath(inURI.getPath(), inForum));
            }
            else {
                url = String.format(TMPL_PORT, inURI.getScheme(), inURI.getHost(), inURI.getPort(),
                        getPath(inURI.getPath(), inForum));
            }
        }

        private String getPath(final String inPath, final boolean inForum) {
            if (inForum) {
                return inPath.replaceAll(Constants.CONTEXT_ADMIN, Constants.CONTEXT_FORUM);
            }
            return inPath.replaceAll(Constants.CONTEXT_FORUM, Constants.CONTEXT_ADMIN);
        }

        private String getUrl() {
            return url;
        }
    }

}
