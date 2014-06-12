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

package org.hip.vif.core.internal.service;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.service.PreferencesHandler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.prefs.PreferencesService;

/**
 * The client class for service component binding the <code>PreferencesService</code>.
 * 
 * @author Luthiger
 * Created: 08.01.2012
 */
public class PreferencesComponent {
	
	/**
	 * Accessing the <code>PreferencesService</code> using the lookup strategy.
	 * 
	 * @param inContext {@link ComponentContext}
	 */
	public void activate(ComponentContext inContext) {
		PreferencesService lPreferences = (PreferencesService)inContext.locateService(ApplicationConstants.PREFERENCES_SERVICE_NAME);
		LoggingHelper.setLoggingEnvironment(lPreferences);
		PreferencesHandler.INSTANCE.setPreferences(lPreferences);
	}

}
