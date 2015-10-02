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
package org.hip.vif.app.admin; // NOPMD

import java.sql.SQLException;
import java.util.Locale;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.exc.VException;
import org.hip.vif.app.admin.internal.VIFEventDispatcher;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.util.DBConnectionProber;
import org.hip.vif.web.components.VIFBody;
import org.hip.vif.web.interfaces.IVIFEventDispatcher;
import org.hip.vif.web.tasks.DBAccessWorkflow;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.WorkflowException;
import org.hip.vif.web.util.RoleHelper;
import org.hip.vif.web.util.VIFAppHelper;
import org.hip.vif.web.util.VIFPreferencesHelper;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.ripla.exceptions.LoginException;
import org.ripla.interfaces.IAppConfiguration;
import org.ripla.interfaces.IAuthenticator;
import org.ripla.interfaces.IWorkflowListener;
import org.ripla.util.PreferencesHelper;
import org.ripla.web.RiplaApplication;
import org.ripla.web.interfaces.IBodyComponent;
import org.ripla.web.services.ISkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/** The forum administration application, i.e. the Vaadin servlet.
 *
 * @author lbenno */
@SuppressWarnings("serial")
@Theme(AdminApplication.DFT_SKIN_ID)
public class AdminApplication extends RiplaApplication { // NOPMD
    private static final Logger LOG = LoggerFactory
            .getLogger(AdminApplication.class);

    public static final String DFT_SKIN_ID = "org.hip.vif.default";
    private static final String APP_NAME = "VIF Administration";

    private VIFEventDispatcher eventDispatcher; // NOPMD by lbenno

    @Override
    protected void beforeInitializeLayout() { // NOPMD
        VIFAppHelper.initializeContext();
        Page.getCurrent().setTitle(APP_NAME);

        eventDispatcher = new VIFEventDispatcher();
        eventDispatcher.setApplication(this);
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            VaadinSession.getCurrent().setAttribute(
                    IVIFEventDispatcher.class, eventDispatcher);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
        final DBConnectionProber lDBProber = new DBConnectionProber();
        if (lDBProber.isReady()) {
            initializePermissions();
        }
        super.beforeInitializeLayout();
    }

    @Override
    protected boolean beforeLogin(final IWorkflowListener inWorkflowListener, final Layout inBodyView, // NOPMD
            final IAppConfiguration inConfiguration) {
        final DBConnectionProber lProber = new DBConnectionProber();
        try {
            if (lProber.needsDBConfiguration()) {
                inBodyView.addComponent(getDftView(inConfiguration));
                DBAccessWorkflow.getInitialWorkflow(inWorkflowListener, getUserAdmin()).startWorkflow();
                return false;
            } else if (lProber.needsTableCreation()) {
                inBodyView.addComponent(getDftView(inConfiguration));
                DBAccessWorkflow.getInitialTblCreation(inWorkflowListener, getUserAdmin()).startWorkflow();
                return false;
            } else if (lProber.needsSUCreation()) {
                inBodyView.addComponent(getDftView(inConfiguration));
                DBAccessWorkflow.getCreateSUWorkflow(inWorkflowListener).startWorkflow();
                return false;
            } else if (lProber.isUndefined()) {
                LOG.error("Undefined problem encountered while trying to access the DB connection!"); //$NON-NLS-1$
                Notification.show(Activator.getMessages().getMessage("errmsg.init.dbaccess"), Type.ERROR_MESSAGE); //$NON-NLS-1$
            }
        } catch (BOMException | SQLException exc) {
            LOG.error("Error encountered while creating the SU record!", exc);
            Notification.show(Activator.getMessages().getMessage("errmsg.init.configuration"), Type.ERROR_MESSAGE); //$NON-NLS-1$
        } catch (final WorkflowException exc) {
            LOG.error("Error encountered during initial DB access configuration!", exc); //$NON-NLS-1$
            Notification.show(Activator.getMessages().getMessage("errmsg.init.configuration"), Type.ERROR_MESSAGE); //$NON-NLS-1$
        }
        return true;
    }

    @Override
    public void showAfterLogin(final User inUser) { // NOPMD
        // we put the user's language to the preferences, it will be set to the session in super.showAfterLogin()
        getPreferences().setLocale(getUserLocale(inUser), inUser);
        super.showAfterLogin(inUser);
    }

    /** Display the application's views after the user has successfully logged in with the specified credentials.
     *
     * @param inName String
     * @param inPassword String */
    public void showAfterLogin(final String inName, final String inPassword) {
        try {
            final User lUser = getAppConfiguration().getLoginAuthenticator()
                    .authenticate(inName, inPassword, getUserAdmin());
            showAfterLogin(lUser);
        } catch (final LoginException exc) {
            LOG.error("Error encountered during login!", exc);
            Notification.show("Error", "Unable to log in. See the error log for more information!", Type.ERROR_MESSAGE);
        }
    }

    private Locale getUserLocale(final User inUser) {
        try {
            final Member lMember = BOMHelper.getMemberHome().getMemberByUserID(inUser.getName());
            return new Locale((String) lMember.get(MemberHome.KEY_LANGUAGE));
        } catch (final Exception exc) { // NOPMD
            LOG.error("Error encountered while looking up the user's language!", exc);
        }
        return getLocale();
    }

    @Override
    protected IAppConfiguration getAppConfiguration() { // NOPMD
        return new IAppConfiguration() {

            @Override
            public IAuthenticator getLoginAuthenticator() { // NOPMD
                return VIFAppHelper.createLoginAuthenticator();
            }

            @Override
            public String getWelcome() { // NOPMD
                return Activator.getMessages().getMessage("LoginViewInformation.welcome.admin");
            }

            @Override
            public String getDftSkinID() { // NOPMD
                return DFT_SKIN_ID;
            }

            @Override
            public String getAppName() { // NOPMD
                return APP_NAME;
            }

            @Override
            public String getMenuTagFilter() { // NOPMD
                return "vif.admin.*";
            }
        };
    }

    @Override
    protected void initializePermissions() { // NOPMD
        final UserAdmin lUserAdmin = getUserAdmin();
        if (lUserAdmin != null) {
            try {
                RoleHelper.createRolesAndPermissions(lUserAdmin);
            } catch (SQLException | VException exc) {
                LOG.error(
                        "Error encountered while creating the OSGi roles for the VIF application!",
                        exc);
            }
        }
        // super.initializePermissions();
    }

    /** Refresh the permissions for the VIF application. */
    public void refreshPermissions() {
        final UserAdmin lUserAdmin = getUserAdmin();
        if (lUserAdmin != null) {
            try {
                RoleHelper.refreshPermissions(lUserAdmin);
            } catch (VException | SQLException exc) {
                LOG.error(
                        "Error encountered while refreshing the OSGi permisssions for the VIF application!",
                        exc);
            }
        }
    }

    @Override
    protected IBodyComponent createBodyView(final ISkin inSkin) { // NOPMD
        return VIFBody.createVIFInstance(inSkin, this, getAppConfiguration().getMenuTagFilter());
    }

    @Override
    protected PreferencesHelper createPreferencesHelper() { // NOPMD
        return new VIFPreferencesHelper();
    }

}
