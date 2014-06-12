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

import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.ripla.interfaces.IAppConfiguration;
import org.ripla.interfaces.IAuthenticator;
import org.ripla.web.RiplaApplication;

import com.vaadin.annotations.Theme;

/**
 * The forum administration application, i.e. the Vaadin servlet.
 * 
 * @author lbenno
 */
@SuppressWarnings("serial")
@Theme(AdminApplication.DFT_SKIN_ID)
public class AdminApplication extends RiplaApplication {
	public static final String DFT_SKIN_ID = "org.hip.vif.default";
	private static final String APP_NAME = "VIF Admin";

	@Override
	protected IAppConfiguration getAppConfiguration() {
		return new IAppConfiguration() {

			@Override
			public IAuthenticator getLoginAuthenticator() {
				// TODO Auto-generated method stub
				return null;
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

		// TODO
		final User lAdmin = (User) inUserAdmin.createRole("admin", Role.USER);
		final Group lAdministrators = (Group) inUserAdmin.createRole(
				"vif.admin", Role.GROUP);
		if (lAdministrators != null) {
			lAdministrators.addRequiredMember(lAdmin);
			lAdministrators.addMember(inUserAdmin.getRole(Role.USER_ANYONE));
		}
		initializePermissions();
	}
}
