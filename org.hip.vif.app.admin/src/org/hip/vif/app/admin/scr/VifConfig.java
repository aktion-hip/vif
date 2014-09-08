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
package org.hip.vif.app.admin.scr;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.prefs.PreferencesService;
import org.ripla.util.PreferencesHelper;
import org.ripla.web.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Managed service implementation. This class is used to persist the changes made using the
 * <code>ConfigurationAmin</code> into the application's preferences.
 * <p>
 * This component is a service consumer of the <code>org.osgi.service.prefs.PreferencesService</code>.
 * </p>
 *
 * @author Luthiger
 * @see org.osgi.service.prefs.PreferencesService */
public class VifConfig { // implements ManagedService {
    private static final Logger LOG = LoggerFactory
            .getLogger(VifConfig.class);
    private final PreferencesHelper preferences = new PreferencesHelper();

    public void setPreferences(final PreferencesService inPreferences) {
        preferences.setPreferences(inPreferences);
        LOG.debug("The OSGi preferences service is made available.");
    }

    public void unsetPreferences(final PreferencesService inPreferences) {
        preferences.dispose();
        LOG.debug("Removed the OSGi preferences service.");
    }

    /** The service's modified method called when OSGi config admin is updated.
     *
     * @param inContext {@link ComponentContext}
     * @throws ConfigurationException */
    public void modified(final ComponentContext inContext)
            throws ConfigurationException {
        final Dictionary<String, Object> lProperties = inContext
                .getProperties();
        if (lProperties != null) {
            setChecked(lProperties, Constants.KEY_CONFIG_SKIN,
                    PreferencesHelper.KEY_SKIN);
            setChecked(lProperties, Constants.KEY_CONFIG_LANGUAGE,
                    PreferencesHelper.KEY_LANGUAGE);
        }
    }

    private boolean setChecked(final Dictionary<String, Object> inProperties,
            final String inPropKey, final String inKey) {
        final Object lValue = inProperties.get(inPropKey);
        return lValue == null ? false : preferences.set(inKey, (String) lValue);
    }

}
