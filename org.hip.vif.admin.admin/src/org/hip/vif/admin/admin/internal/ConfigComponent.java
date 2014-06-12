/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2014, Benno Luthiger

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
package org.hip.vif.admin.admin.internal;

import org.hip.vif.admin.admin.data.SkinConfigRegistry;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ripla.services.ISkinService;

/**
 * The consumer of the <code>ISkin</code> service.
 * 
 * @author Luthiger
 */
public class ConfigComponent {

	public void registerSkin(final ISkinService inSkin) {
		SkinConfigRegistry.INSTANCE.register(inSkin);
	}

	public void unregisterSkin(final ISkinService inSkin) {
		SkinConfigRegistry.INSTANCE.unregister(inSkin);
	}

	public void setConfigAdmin(final ConfigurationAdmin inConfigAdmin) {
		SkinConfigRegistry.INSTANCE.setConfigAdmin(inConfigAdmin);
	}

	public void unsetConfigAdmin(final ConfigurationAdmin inConfigAdmin) {
		SkinConfigRegistry.INSTANCE.setConfigAdmin(null);
	}

}
