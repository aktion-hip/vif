/**
	This package is part of the application VIF.
	Copyright (C) 2012-2015, Benno Luthiger

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

package org.hip.vif.core.internal.service;

import java.io.File;
import java.io.IOException;

import org.hip.vif.core.ApplicationConstants;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/** Helper class to prepare the logback configuration.
 *
 * @author Luthiger Created: 24.03.2012 */
public final class LoggingHelper {
    // we have to duplicate the log constants from PreferencesHandler here for that we can remove any references to
    // PreferencesHandler
    private static final String KEY_LOG_PATH = "org.hip.vif.log.path";
    private static final String KEY_LOG_LEVEL = "org.hip.vif.log.level";
    private static final String KEY_LOG_CONFIG = "org.hip.vif.log.config";
    private static final String NAME_LOGBACK_CONFIG = "logback.xml";

    private static final String PATTERN = "%d{HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n";

    private LoggingHelper() {
        // prevent instantiation
    }

    /** Convenience method to set the environmental variables for the logback configuration. The values for these
     * settings can be configured and, thus, must be read from the application's preferences.
     *
     * @param inPreferences {@link PreferencesService} */
    public static void setLoggingEnvironment(final PreferencesService inPreferences) {
        final Preferences lPreferences = inPreferences.getSystemPreferences();
        try {
            setValueChecked(ApplicationConstants.LOG_DESTINATION,
                    lPreferences.get(KEY_LOG_PATH, ApplicationConstants.LOG_DESTINATION_DFT));
            setValueChecked(ApplicationConstants.LOG_LEVEL,
                    lPreferences.get(KEY_LOG_LEVEL, ApplicationConstants.LOG_LEVEL_DFT));
            setConfigurationFile(lPreferences);
        } catch (final IOException exc) { // NOPMD
            // intentionally left empty
        }
    }

    /** Setting the environmental value <code>logback.configurationFile</code>.<br />
     * If this value is already set, through the JVM parameter <code>-Dlogback.configurationFile=...</code>, we leave
     * this value untouched.
     *
     * @param inPreferences
     * @throws IOException */
    private static void setConfigurationFile(final Preferences inPreferences) throws IOException {
        String lConfigFile = System.getProperty(ApplicationConstants.LOG_CONFIG);
        if (lConfigFile != null && new File(lConfigFile).exists()) {
            return;
        }
        lConfigFile = inPreferences.get(KEY_LOG_CONFIG, null);
        if (lConfigFile == null) {
            setDftLogging();
            return;
        }
        File lConfig = new File(lConfigFile);
        if (!lConfigFile.endsWith(NAME_LOGBACK_CONFIG)) {
            lConfig = new File(lConfig, NAME_LOGBACK_CONFIG);
        }
        if (lConfig.exists()) {
            setValueChecked(ApplicationConstants.LOG_CONFIG, lConfig.getAbsolutePath());
        }
    }

    private static void setValueChecked(final String inKey, final String inValue) {
        if (inValue != null) {
            System.setProperty(inKey, inValue);
        }
    }

    private static void setDftLogging() {
        final ConsoleAppender<ILoggingEvent> lConsole = new ConsoleAppender<ILoggingEvent>();

        final PatternLayout lPattern = new PatternLayout();
        lPattern.setPattern(PATTERN);
        lPattern.start();
        lConsole.setLayout(lPattern);

        final Logger lLogger = (Logger) LoggerFactory.getLogger("vif");
        lLogger.addAppender(lConsole);
        lLogger.setLevel(Level.DEBUG);
        lLogger.setAdditive(true);
    }

}
