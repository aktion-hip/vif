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

package org.hip.vif.admin.admin.data;

import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.web.util.ConfigurationItem;
import org.hip.vif.web.util.ConfigurationItem.PropertyDef;

/**
 * Helper class to manage the settings after configuration changes.
 * 
 * @author Luthiger Created: 14.02.2012
 */
public class ConfigurationHelper {
	private static final PropertyDef LANGUAGE = ConfigurationItem.PropertyDef.LANGUAGE_CONTENT;
	private static final PropertyDef DB_DRIVER = ConfigurationItem.PropertyDef.DB_DRIVER;
	private static final PropertyDef DB_SERVER = ConfigurationItem.PropertyDef.DB_SERVER;
	private static final PropertyDef DB_SCHEMA = ConfigurationItem.PropertyDef.DB_SCHEMA;
	private static final PropertyDef DB_USER = ConfigurationItem.PropertyDef.DB_USER;
	private static final PropertyDef DB_PASSWD = ConfigurationItem.PropertyDef.DB_PASSWD;

	public enum ConfigurationTask {
		DB, INDEX, NONE;
	}

	private final boolean contentLangChange;
	private final boolean dbAccessChange;
	private final DBAccessConfiguration oldDBAccess;
	private ConfigurationTask configurationTask = ConfigurationTask.NONE;
	private final boolean isEmbedded;

	/**
	 * Constructor, must be called <b>before</b> the new values are stored to
	 * the preferences.
	 * 
	 * @param inConfiguration
	 *            {@link ConfigurationItem} the configuration with the new
	 *            settings
	 */
	public ConfigurationHelper(final ConfigurationItem inConfiguration) {
		oldDBAccess = PreferencesHandler.INSTANCE.getDBConfiguration();

		contentLangChange = inConfiguration.isDirty(LANGUAGE);
		dbAccessChange = inConfiguration.isDirty(DB_DRIVER)
				|| inConfiguration.isDirty(DB_SERVER)
				|| inConfiguration.isDirty(DB_SCHEMA)
				|| inConfiguration.isDirty(DB_USER)
				|| inConfiguration.isDirty(DB_PASSWD);
		isEmbedded = EmbeddedDBHelper.checkEmbedded(inConfiguration
				.getItemProperty(DB_DRIVER.getPID()).getValue().toString());

		if (contentLangChange && dbAccessChange) {
			configurationTask = ConfigurationTask.DB;
		} else if (dbAccessChange) {
			configurationTask = ConfigurationTask.DB;
		} else if (contentLangChange) {
			configurationTask = ConfigurationTask.INDEX;
		}
	}

	/**
	 * @return {@link ConfigurationTask} returns the task for the system after
	 *         analyzing the changes to the configuration
	 */
	public ConfigurationTask getConfigurationTask() {
		return configurationTask;
	}

	/**
	 * Convenience method, triggers the restoration of the old DB configuration.
	 * Can be called if the DB access with the new setting fails.
	 */
	public void restoreDBAccessSettings() {
		PreferencesHandler.INSTANCE.restoreDBConfiguration(oldDBAccess);
	}

	/**
	 * @return boolean <code>true</code> if the new configuration is for a
	 *         embedded DB
	 */
	public boolean isEmbedded() {
		return isEmbedded;
	}

}