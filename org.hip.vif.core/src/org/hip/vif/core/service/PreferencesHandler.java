/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.core.service;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.hip.kernel.dbaccess.DBAccessConfiguration;
import org.hip.kernel.dbaccess.DataSourceRegistry;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The application's singleton instance to handle preferences, i.e. the application configuration. 
 * 
 * @author Luthiger
 * Created: 07.01.2012
 */
public enum PreferencesHandler {
	INSTANCE;
	
	private static final Logger LOG = LoggerFactory.getLogger(PreferencesHandler.class);
	
	//keys
	public static final String KEY_LANGUAGE_DFT 		= "org.hip.vif.dftLanguage";
	public static final String KEY_LANGUAGE_CONTENT 	= "org.hip.vif.content.language";
	public static final String KEY_COUNTRY_DFT 			= "org.hip.vif.dftCountry";
	public static final String KEY_DATE_PATTERN			= "org.hip.vif.datePattern";
	public static final String KEY_LATENCY_DAYS			= "org.hip.vif.request.latency";
	public static final String KEY_MEMBER_SEARCHER		= "org.hip.vif.memberSearcher";
	public static final String KEY_GUEST_ALLOW			= "org.hip.vif.guest.allowed";
	public static final String KEY_PW_DISPLAY			= "org.hip.vif.password.display";
	public static final String KEY_UPLOAD_QUOTA			= "org.hip.vif.upload.size";
	public static final String KEY_DOCS_ROOT			= "org.hip.vif.docs.root";
	public static final String KEY_FORUM_NAME			= "org.hip.vif.forum.name";
	public static final String KEY_MAIL_HOST			= "org.hip.vif.mail.host";
	public static final String KEY_MAIL_ADDRESS			= "org.hip.vif.mail.address";
	public static final String KEY_MAIL_SUBJECT_ID		= "org.hip.vif.mail.subjectId";
	public static final String KEY_MAIL_SUBJECT_TEXT	= "org.hip.vif.mail.subjectText";
	public static final String KEY_MAIL_NAMING			= "org.hip.vif.mail.naming";
	public static final String KEY_SKIN					= "org.hip.vif.skin";
	public static final String KEY_DB_DRIVER			= "org.hip.vif.db.driver";
	public static final String KEY_DB_SERVER			= "org.hip.vif.db.server";
	public static final String KEY_DB_SCHEMA			= "org.hip.vif.db.schema";
	public static final String KEY_DB_CONNECTION		= "org.hip.vif.db.url";
	public static final String KEY_DB_USER				= "org.hip.vif.db.userId";
	public static final String KEY_DB_PW				= "org.hip.vif.db.password";
	public static final String KEY_DBX_DRIVER			= "org.hip.vif.xmember.driver";
	public static final String KEY_DBX_SERVER			= "org.hip.vif.xmember.server";
	public static final String KEY_DBX_SCHEMA			= "org.hip.vif.xmember.schema";
	public static final String KEY_DBX_USER				= "org.hip.vif.xmember.userId";
	public static final String KEY_DBX_PW				= "org.hip.vif.xmember.password";
	public static final String KEY_DBX_JNDI				= "org.hip.vif.xmember.jndi";
	public static final String KEY_LDAP_URL				= "org.hip.vif.ldap.url";
	public static final String KEY_LDAP_MANAGER_DN		= "org.hip.vif.ldap.managerDN";
	public static final String KEY_LDAP_MANAGER_PW		= "org.hip.vif.ldap.password";
	public static final String KEY_LOGOUT_URL			= "org.hip.vif.url.logout";
	public static final String KEY_LOG_PATH				= "org.hip.vif.log.path";
	public static final String KEY_LOG_LEVEL			= "org.hip.vif.log.level";
	public static final String KEY_LOG_CONFIG			= "org.hip.vif.log.config";
	public static final String KEY_CONF_ROOT			= "org.hip.vif.conf.root"; //not displayed on config view
	
	private PreferencesService preferences;

	/**
	 * @param inPreferences {@link PreferencesService} setter for the preferences service
	 */
	public void setPreferences(PreferencesService inPreferences) {
		if (inPreferences == null) return;
		preferences = inPreferences;
	}	
	
	/**
	 * Getter for the property with the specified key.<br />
	 * This method returns the specified property's value either from the preferences (i.e. the stored application's configuration settings)
	 * or, if none are defined yet, from the application's properties file.
	 * 
	 * @param inKey String
	 * @return String the property's value
	 * @throws IOException
	 */
	public String get(String inKey) throws IOException {
		String lValue = VSys.getVSysProperties().getProperty(inKey);
		if (preferences == null) {
			return lValue;
		}
		
		return get(inKey, lValue);
	}
	
	/**
	 * Getter for the property with the specified key.
	 * 
	 * @param inKey String the key for the property value
	 * @param inDftValue String
	 * @return String the property's value (from the preferences) or the specified default value
	 */
	public String get(String inKey, String inDftValue) {
		Preferences lPreferences = preferences.getSystemPreferences();
		return lPreferences.get(inKey, inDftValue);
	}
	
	/**
	 * Setter for the value of the property with the specified key.
	 * 
	 * @param inKey String
	 * @param inValue String
	 */
	public void set(String inKey, String inValue) {
		if (preferences == null) {
			return;
		}
		
		Preferences lPreferences = preferences.getSystemPreferences();
		lPreferences.remove(inKey);
		lPreferences.put(inKey, inValue);
		savePreferences(lPreferences);
	}

	private void savePreferences(Preferences inPreferences) {
		try {
			inPreferences.flush();
		}
		catch (BackingStoreException exc) {
			LOG.error("Can't save preferences!", exc);
		}
	}
	
	/**
	 * Set the db access configuration for an embedded DB.
	 */
	public void setEmbeddedDB() {
		set(KEY_DB_SCHEMA, EmbeddedDBHelper.getEmbeddedDBChecked());
		set(KEY_DB_SERVER, "");
		set(KEY_DB_USER, "");
		set(KEY_DB_PW, "");
	}
	
	/**
	 * Retrieves the configured skin id.
	 * 
	 * @return String the skin's id
	 */
	public String getActiveSkinID() {
		try {
			return get(KEY_SKIN);
		}
		catch (IOException exc) {
			LOG.error("Couldn't read configured skin, fallback to default skin!", exc);
		}
		return ApplicationConstants.DFLT_SKIN;
	}
	
	/**
	 * Retrieves the configured locale.
	 * 
	 * @param inApp boolean if <code>true</code> the application language is returned, if <code>false</code> the content language
	 * @param inDft {@link Locale} the system's locale
	 * @return {@link Locale}
	 */
	public Locale getLocale(boolean inApp, Locale inDft) {
		try {
			String lLanguage = get(inApp ? KEY_LANGUAGE_DFT : KEY_LANGUAGE_CONTENT);
			return lLanguage == null ? inDft : new Locale(lLanguage);
		}
		catch (IOException exc) {
			LOG.error("Couldn't read configured language, fallback to default language!", exc);
		}
		return inDft;
	}
	
	/**
	 * Retrieves the configured date pattern.
	 * 
	 * @return String date pattern, e.g. <code>yyyy/MM/dd</code>
	 */
	public String getDatePattern() {
		try {
			return get(KEY_DATE_PATTERN);
		}
		catch (IOException exc) {
			LOG.error("Couldn't read configured date pattern, fallback to default pattern!", exc);
		}
		return ApplicationConstants.DFLT_PATTERN;
	}
	
	/**
	 * Checks the DB type actually used.
	 * 
	 * @return boolean <code>true</code> if the DB connection string indicates a Derby DB
	 * @throws IOException
	 */
	public boolean isDerbyDB() throws IOException {
		return get(PreferencesHandler.KEY_DB_DRIVER).indexOf(ApplicationConstants.DB_TYPE_DERBY) >= 0;
	}
	
	/**
	 * Checks whether the application is started in embedded mode.<br />
	 * Embedded mode is indicated by empty DB username and password.
	 * 
	 * @return boolean <code>true</code> if the application is started with database and http server embedded.
	 */
	public boolean isEmbedded() {
		try {
			String lUserID = get(KEY_DB_USER);
			String lPWrd = get(KEY_DB_PW);
			return (lUserID == null ? true : lUserID.length() == 0) && (lPWrd == null ? true : lPWrd.length() == 0);
		} 
		catch (IOException exc) {
			// intentionally left empty
		}
		return false;
	}
	
	/**
	 * Returns the DB access configuration.
	 * 
	 * @return {@link DBAccessConfiguration}
	 */
	public DBAccessConfiguration getDBConfiguration() {
		try {
			return new DBAccessConfiguration(get(KEY_DB_DRIVER), 
											get(KEY_DB_SERVER), 
											get(KEY_DB_SCHEMA), 
											get(KEY_DB_USER), 
											get(KEY_DB_PW));
		}
		catch (IOException exc) {
			// intentionally left empty
		}
		return DBAccessConfiguration.getEmptyConfiguration();
	}

	/**
	 * Restores the DB access settings.
	 * 
	 * @param inDBConfiguration {@link DBAccessConfiguration} the DB access settings of a previous state
	 */
	public void restoreDBConfiguration(DBAccessConfiguration inDBConfiguration) {
		set(KEY_DB_DRIVER, inDBConfiguration.getDBSourceID());
		
		Properties lSettings = inDBConfiguration.getProperties();
		set(KEY_DB_SERVER, lSettings.getProperty(DataSourceFactory.JDBC_SERVER_NAME));
		set(KEY_DB_SCHEMA, lSettings.getProperty(DataSourceFactory.JDBC_DATABASE_NAME));
		set(KEY_DB_USER, lSettings.getProperty(DataSourceFactory.JDBC_USER));
		set(KEY_DB_PW, lSettings.getProperty(DataSourceFactory.JDBC_PASSWORD));
		
		DataSourceRegistry.INSTANCE.setActiveConfiguration(getDBConfiguration());
	}
	
	/**
	 * Returns the configuration to access an external (member) database.
	 * 
	 * @return {@link DBAccessConfiguration}
	 */
	public DBAccessConfiguration getExtDBConfiguration() {
		try {
			return new DBAccessConfiguration(get(KEY_DBX_DRIVER), 
					get(KEY_DBX_SERVER), 
					get(KEY_DBX_SCHEMA), 
					get(KEY_DBX_USER), 
					get(KEY_DBX_PW));
		}
		catch (IOException exc) {
			// intentionally left empty
		}
		return DBAccessConfiguration.getEmptyConfiguration();
	}
	
}
