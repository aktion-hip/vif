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
package org.hip.vif.admin.admin.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.hip.vif.admin.admin.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ripla.services.ISkinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The configuration bundle's skin registry.
 * 
 * @author Luthiger
 */
public enum SkinConfigRegistry {
	INSTANCE;

	private static final Logger LOG = LoggerFactory
			.getLogger(SkinConfigRegistry.class);

	private final Collection<ISkinService> skins = new ArrayList<ISkinService>();
	private ConfigurationAdmin configAdmin;

	/**
	 * @param inSkin
	 */
	public void register(final ISkinService inSkin) {
		skins.add(inSkin);
	}

	/**
	 * @param inSkin
	 */
	public void unregister(final ISkinService inSkin) {
		skins.remove(inSkin);
	}

	/**
	 * @param inConfigAdmin
	 */
	public void setConfigAdmin(final ConfigurationAdmin inConfigAdmin) {
		configAdmin = inConfigAdmin;
	}

	/**
	 * @return List&lt;SkinBean> list of registered skins
	 */
	public List<SkinBean> getSkins() {
		final List<SkinBean> out = new ArrayList<SkinBean>(skins.size());
		for (final ISkinService lSkin : skins) {
			out.add(SkinBean.createItem(lSkin)); // NOPMD
		}
		Collections.sort(out);
		return out;
	}

	/**
	 * Changes the actual skin to the skin with the specified id.
	 * 
	 * @param inSkinId
	 *            String the selected skin's id
	 */
	public void changeSkin(final String inSkinId) {
		if (configAdmin != null) {
			try {
				final Configuration lConfiguration = configAdmin
						.getConfiguration(Constants.COMPONENT_NAME, null);
				final Dictionary<String, Object> lProperties = new Hashtable<String, Object>();
				lProperties.put(Constants.KEY_CONFIG_SKIN, inSkinId);
				lConfiguration.update(lProperties);
			}
			catch (final IOException exc) {
				LOG.error(
						"Error encounteres while updating the OSGi configuration admin!",
						exc);
			}
		}
	}

}
