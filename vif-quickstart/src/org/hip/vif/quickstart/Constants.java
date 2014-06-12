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

package org.hip.vif.quickstart;

import java.util.regex.Pattern;

/**
 * This bundles constants.
 * 
 * @author Luthiger
 * Created: 19.01.2012
 */
public class Constants {
	public static final String URI_STRING = "http://localhost:%s/admin";
	
	public static final String DFT_PORT = "8080";
	public static final Pattern STARTER_JAR = Pattern.compile("vif-quickstart-(\\d\\d\\d\\d).jar");
	public static final String CONFIG_INI_NAME = "config.ini";
	
	public static final Pattern OSGI_FRAMEWORK = Pattern.compile("org.eclipse.osgi_(.*).jar");
	public static final String OSGI_PROPERTY_CONSOLE_PORT = "osgi.console";
	public static final String OSGI_CONSOLE_PORT = "1234";
	public static final String OSGI_PROPERTY_HTTP_PORT = "org.osgi.service.http.port";

	public static final String RUNTIME_DIR = "runtime";
	public static final String PLUGINS_DIR = "plugins";
	
}
