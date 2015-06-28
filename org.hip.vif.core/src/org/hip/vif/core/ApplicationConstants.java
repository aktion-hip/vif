/**
	This package is part of the application VIF.
	Copyright (C) 2007-2014, Benno Luthiger

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
package org.hip.vif.core;

/** Central provider of application's constants.
 *
 * @author Luthiger */
public final class ApplicationConstants {
    public static final String DFLT_SEARCHER = "org.hip.vif.member.standard"; //$NON-NLS-1$
    public static final String DFLT_USER_ID = "Guest"; //$NON-NLS-1$
    public static final String FOOTER_TEXT = "&copy; Aktion HIP";

    public static final String PROCESSING_INSTRUCTION = "version=\"1.0\" encoding=\"UTF-8\""; //$NON-NLS-1$
    public static final String HEADER = "<?xml " + PROCESSING_INSTRUCTION + "?>"; //$NON-NLS-1$ //$NON-NLS-2$
    public static final String ROOT_BEGIN = "<Root>"; //$NON-NLS-1$
    public static final String ROOT_END = "</Root>"; //$NON-NLS-1$

    public static final String ROLE_ID_SU = "1"; //$NON-NLS-1$
    public static final String ROLE_ID_ADMIN = "2"; //$NON-NLS-1$
    public static final String ROLE_ID_GROUP_ADMIN = "3"; //$NON-NLS-1$
    public static final String ROLE_ID_PARTICIPANT = "4"; //$NON-NLS-1$
    public static final String ROLE_ID_MEMBER = "5"; //$NON-NLS-1$
    public static final String ROLE_ID_GUEST = "6"; //$NON-NLS-1$
    public static final String ROLE_ID_EXCL_PARTICIPANT = "7"; //$NON-NLS-1$

    public static final String DB_TYPE_DERBY = "Derby"; //$NON-NLS-1$
    public final static String WORKSPACE_DIR = "workspace"; //$NON-NLS-1$

    public static final String KEY_GROUP_ID = "groupID"; //$NON-NLS-1$
    public static final String KEY_RATING_ID = "ratingID"; //$NON-NLS-1$

    public static final String PARAMETER_KEY_GENERIC = "generic_key"; //$NON-NLS-1$

    // fallback for the configuration
    public static final String DFLT_SKIN = "org.hip.vif.default"; //$NON-NLS-1$
    public static final String DFLT_PATTERN = "yyyy/MM/dd"; //$NON-NLS-1$

    // name given in OSGI-INF/skin.xml
    public static final String PREFERENCES_SERVICE_NAME = "preferences"; //$NON-NLS-1$

    public static final String USER_SETTINGS_LANGUAGE = "language"; //$NON-NLS-1$

    public static final int DFLT_UPLOADE_QUOTA = 50;
    public static final int DFLT_REQUEST_LATENCY = 3;

    public static final String LOCAL_RESOURCES_DIR = "/resources"; //$NON-NLS-1$

    // the application's actual version, needed for the upgrader
    public static final String APP_VERSION = "1.2"; //$NON-NLS-1$

    // logging
    public static final String[] LOG_LEVELS = { "trace", "debug", "info",
        "warn", "error", "fatal" };
    public static final String LOG_DESTINATION = "log.destination";
    public static final String LOG_DESTINATION_DFT = "./vif_logs";
    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_LEVEL_DFT = "trace";
    public static final String LOG_CONFIG = "logback.configurationFile";

    public static final String VIF_USER = "vif.user";

    private ApplicationConstants() {
        // prevent instantiation
    }
}
