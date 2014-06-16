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
package org.hip.vif.app.admin;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.web.util.RoleHelper;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.ripla.exceptions.LoginException;
import org.ripla.interfaces.IAppConfiguration;
import org.ripla.interfaces.IAuthenticator;
import org.ripla.web.RiplaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.Theme;

/**
 * The forum administration application, i.e. the Vaadin servlet.
 * 
 * @author lbenno
 */
@SuppressWarnings("serial")
@Theme(AdminApplication.DFT_SKIN_ID)
public class AdminApplication extends RiplaApplication {
	private static final Logger LOG = LoggerFactory
			.getLogger(AdminApplication.class);

	public static final String DFT_SKIN_ID = "org.hip.vif.default";
	private static final String APP_NAME = "VIF Admin";

	@Override
	protected IAppConfiguration getAppConfiguration() {
		return new IAppConfiguration() {

			@Override
			public IAuthenticator getLoginAuthenticator() {
				// TODO Auto-generated method stub
				return new IAuthenticator() {

					@Override
					public User authenticate(final String inName,
							final String inPassword, final UserAdmin inUserAdmin)
							throws LoginException {
						return null;
					}
				};
			}

			@Override
			public String getWelcome() {
				return "Willkommen bei VIF";
			}

			@Override
			public String getDftSkinID() {
				return DFT_SKIN_ID;
			}

			@Override
			public String getAppName() {
				return APP_NAME;
			}
		};
	}

	@Override
	public void setUserAdmin(final UserAdmin inUserAdmin) {
		super.setUserAdmin(inUserAdmin);
		try {
			RoleHelper.createVIFRoles(inUserAdmin);
			initializePermissions();
		}
		catch (SQLException | VException exc) {
			LOG.error(
					"Error encountered while creating the OSGi roles for the VIF application!",
					exc);
		}
	}

	@Override
	protected void initializePermissions() {
		// TODO Auto-generated method stub
		super.initializePermissions();
	}

}
