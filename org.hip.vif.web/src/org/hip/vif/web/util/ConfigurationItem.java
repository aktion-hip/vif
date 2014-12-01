/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

import java.io.IOException;

import org.hip.vif.core.service.PreferencesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/** An <code>Item</code> implementation for the application configuration.<br />
 * Instances of this item contain <code>DirtyableProperty</code>s.<br />
 * Instances of this class can be used as view model for the application's configuration editor.
 *
 * @author Luthiger */
@SuppressWarnings("serial")
public class ConfigurationItem extends PropertysetItem {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationItem.class);

    public static enum PropertyDef {
        LANGUAGE_DEFAULT(PreferencesHandler.KEY_LANGUAGE_DFT), LANGUAGE_CONTENT(
                PreferencesHandler.KEY_LANGUAGE_CONTENT), COUNTRY_DFT(
                PreferencesHandler.KEY_COUNTRY_DFT), DATE_PATTERN(
                PreferencesHandler.KEY_DATE_PATTERN), LATENCY_DAYS(
                PreferencesHandler.KEY_LATENCY_DAYS), MEMBER_SEARCHER(
                PreferencesHandler.KEY_MEMBER_SEARCHER), GUEST_ALLOW(
                PreferencesHandler.KEY_GUEST_ALLOW), PW_DISPLAY(
                PreferencesHandler.KEY_PW_DISPLAY), UPLOAD_QUOTA(
                PreferencesHandler.KEY_UPLOAD_QUOTA), DOCS_ROOT(
                PreferencesHandler.KEY_DOCS_ROOT), FORUM_NAME(
                PreferencesHandler.KEY_FORUM_NAME), MAIL_HOST(
                PreferencesHandler.KEY_MAIL_HOST), MAIL_ADDRESS(
                PreferencesHandler.KEY_MAIL_ADDRESS), MAIL_SUBJECT_ID(
                PreferencesHandler.KEY_MAIL_SUBJECT_ID), MAIL_SUBJECT_TEXT(
                PreferencesHandler.KEY_MAIL_SUBJECT_TEXT), MAIL_NAMING(
                PreferencesHandler.KEY_MAIL_NAMING), SKIN(
                PreferencesHandler.KEY_SKIN), LOG_PATH(
                PreferencesHandler.KEY_LOG_PATH), LOG_LEVEL(
                PreferencesHandler.KEY_LOG_LEVEL), LOG_CONFIG(
                PreferencesHandler.KEY_LOG_CONFIG), LOGOUT_URL(
                PreferencesHandler.KEY_LOGOUT_URL), DB_DRIVER(
                PreferencesHandler.KEY_DB_DRIVER), DB_SERVER(
                PreferencesHandler.KEY_DB_SERVER), DB_SCHEMA(
                PreferencesHandler.KEY_DB_SCHEMA), DB_USER(
                PreferencesHandler.KEY_DB_USER), DB_PASSWD(
                PreferencesHandler.KEY_DB_PW), DBX_DRIVER(
                PreferencesHandler.KEY_DBX_DRIVER), DBX_SERVER(
                PreferencesHandler.KEY_DBX_SERVER), DBX_SCHEMA(
                PreferencesHandler.KEY_DBX_SCHEMA), DBX_USER(
                PreferencesHandler.KEY_DBX_USER), DBX_PASSWD(
                PreferencesHandler.KEY_DBX_PW), LDAP_URL(
                PreferencesHandler.KEY_LDAP_URL), LDAP_MANAGER_DN(
                PreferencesHandler.KEY_LDAP_MANAGER_DN), LDAP_MANAGER_PW(
                PreferencesHandler.KEY_LDAP_MANAGER_PW);

        private String pid;

        PropertyDef(final String inPID) {
            pid = inPID;
        }

        public String getPID() {
            return pid;
        }
    }

    /** Private constructor. */
    private ConfigurationItem() {
        // prevent instantiation
    }

    /** Factory method to create and initialize an instance of <code>ConfigurationItem</code>.
     *
     * @return {@link ConfigurationItem}
     * @throws IOException */
    public static ConfigurationItem createConfiguration() throws IOException {
        final PreferencesHandler lConfiguration = PreferencesHandler.INSTANCE;
        final ConfigurationItem out = new ConfigurationItem();
        for (final PropertyDef lProperty : PropertyDef.values()) {
            out.addItemProperty(
                    lProperty.getPID(),
                    new DirtyableProperty<String>(lConfiguration.get(lProperty
                            .getPID()), String.class, lProperty));
        }
        return out;
    }

    /** Saves the changes to the preferences service.
     *
     * @return <code>true</code> if any changes have been stored */
    @SuppressWarnings("rawtypes")
    public boolean saveChanges() {
        boolean outChanged = false;
        final PreferencesHandler lConfiguration = PreferencesHandler.INSTANCE;
        for (final Object lPID : getItemPropertyIds()) {
            final Property lItemProperty = getItemProperty(lPID);
            if (lItemProperty instanceof DirtyableProperty<?>) {
                final DirtyableProperty<?> lProperty = (DirtyableProperty<?>) lItemProperty;
                if (lProperty.isDirty()) {
                    outChanged = true;
                    lConfiguration.set(lProperty.getPropertyDef().getPID(),
                            lProperty.getValue().toString());
                }
            }
        }
        return outChanged;
    }

    /** Checks whether the property with the specified definition is dirty.
     *
     * @param inPropertyDef {@link PropertyDef}
     * @return boolean <code>true</code> if the specified property is dirty */
    @SuppressWarnings("unchecked")
    public boolean isDirty(final PropertyDef inPropertyDef) {
        final DirtyableProperty<String> lProperty = (DirtyableProperty<String>) getItemProperty(inPropertyDef
                .getPID());
        return lProperty.isDirty();
    }

    // ---

    private static class DirtyableProperty<T> extends ObjectProperty<T> {
        private T origValue;
        private final PropertyDef propertyDef;

        DirtyableProperty(final T inValue, final Class<T> inType,
                final PropertyDef inPropertyDef) {
            super(inValue, inType);
            propertyDef = inPropertyDef;
            origValue = inValue;
            if (inValue == null) {
                try {
                    origValue = inType.newInstance();
                    setValue(origValue);
                } catch (final InstantiationException exc) {
                    LOG.error("Error encountered!", exc);
                } catch (final IllegalAccessException exc) {
                    LOG.error("Error encountered!", exc);
                }
            }
        }

        boolean isDirty() {
            return !origValue.equals(getValue());
        }

        PropertyDef getPropertyDef() {
            return propertyDef;
        }
    }

}
