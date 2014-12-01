/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.app.forum;

import java.sql.SQLException;
import java.util.Locale;

import org.hip.kernel.exc.VException;
import org.hip.vif.app.forum.internal.VIFEventDispatcher;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.exc.InvalidAuthenticationException;
import org.hip.vif.core.service.MemberUtility;
import org.hip.vif.web.components.VIFBody;
import org.hip.vif.web.interfaces.IVIFEventDispatcher;
import org.hip.vif.web.util.RoleHelper;
import org.hip.vif.web.util.VIFAppHelper;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.ripla.exceptions.LoginException;
import org.ripla.interfaces.IAppConfiguration;
import org.ripla.interfaces.IAuthenticator;
import org.ripla.web.RiplaApplication;
import org.ripla.web.interfaces.IBodyComponent;
import org.ripla.web.services.ISkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;

/** The forum administration application, i.e. the Vaadin servlet.
 *
 * @author lbenno */
@SuppressWarnings("serial")
@Theme(ForumApplication.DFT_SKIN_ID)
public class ForumApplication extends RiplaApplication {
    private static final Logger LOG = LoggerFactory
            .getLogger(ForumApplication.class);

    public static final String DFT_SKIN_ID = "org.hip.vif.default";
    private static final String APP_NAME = "VIF Forum";

    private VIFEventDispatcher eventDispatcher;

    @Override
    protected void beforeInitializeLayout() {
        Page.getCurrent().setTitle(APP_NAME);

        eventDispatcher = new VIFEventDispatcher();
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            VaadinSession.getCurrent().setAttribute(
                    IVIFEventDispatcher.class, eventDispatcher);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }

        super.beforeInitializeLayout();
    }

    @Override
    public void showAfterLogin(final User inUser) {
        // we put the user's language to the preferences, it will be set to the session in super.showAfterLogin()
        getPreferences().setLocale(getUserLocale(inUser), inUser);
        super.showAfterLogin(inUser);
    }

    private Locale getUserLocale(final User inUser) {
        try {
            final Member lMember = BOMHelper.getMemberHome().getMemberByUserID(inUser.getName());
            return new Locale((String) lMember.get(MemberHome.KEY_LANGUAGE));
        } catch (final Exception exc) {
            LOG.error("Error encountered while looking up the user's language!", exc);
        }
        return getLocale();
    }

    @Override
    protected IAppConfiguration getAppConfiguration() {
        return new IAppConfiguration() {

            @Override
            public IAuthenticator getLoginAuthenticator() {
                return new IAuthenticator() {

                    @Override
                    public User authenticate(final String inName,
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
                            LOG.error("User with ID '{}' could not authenticate!", inName, exc);
                        } catch (final VException exc) {
                            LOG.error("Problem during login encoutered!", exc);
                        } catch (final SQLException exc) {
                            LOG.error("Problem during login encoutered!", exc);
                        }
                        return null;
                    }
                };
            }

            @Override
            public String getWelcome() {
                return Activator.getMessages().getMessage("LoginViewInformation.welcome.forum");
            }

            @Override
            public String getDftSkinID() {
                return DFT_SKIN_ID;
            }

            @Override
            public String getAppName() {
                return APP_NAME;
            }

            @Override
            public String getMenuTagFilter() {
                return "vif.forum.*";
            }
        };
    }

    @Override
    public void setUserAdmin(final UserAdmin inUserAdmin) {
        super.setUserAdmin(inUserAdmin);

        VIFAppHelper.initializeContext();
        try {
            RoleHelper.createRolesAndPermissions(inUserAdmin);
            initializePermissions();
        } catch (final SQLException exc) {
            LOG.error(
                    "Error encountered while creating the OSGi roles for the VIF application!",
                    exc);
        } catch (final VException exc) {
            LOG.error(
                    "Error encountered while creating the OSGi roles for the VIF application!",
                    exc);
        }
    }

    @Override
    protected void initializePermissions() {
        // we do nothing at the moment
        // super.initializePermissions();
    }

    @Override
    protected IBodyComponent createBodyView(final ISkin inSkin) {
        return VIFBody.createVIFInstance(inSkin, this, getAppConfiguration().getMenuTagFilter());
    }

}
