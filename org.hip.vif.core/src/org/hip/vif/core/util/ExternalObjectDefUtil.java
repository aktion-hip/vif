/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.hip.kernel.sys.VSys;
import org.hip.vif.core.VIFSys;
import org.hip.vif.core.service.PreferencesHandler;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to help domain object home classes that provide the object definition
 * in an external file.
 *
 * @author Luthiger
 * Created: 28.12.2008
 */
public class ExternalObjectDefUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ExternalObjectDefUtil.class);

	/**
	 * Returns the <code>File</code> object containing the object definition.
	 * 
	 * @param inObjectDefFilename String Name of the file containing the object definition.
	 * @return File
	 */
	public static File getObjectDefFile(String inObjectDefFilename) {
	  	String lFilename = null;
		String lDirectory = VIFSys.getVSysCanonicalPath(PreferencesHandler.KEY_CONF_ROOT);
		if (lDirectory.length() == 0) {
			String lProperty = VSys.getContextPath();
			if (lProperty != null) {
				//if no context path has been set, we assume an HTTP server embedded into Equinox, therefore, take the bundle as context
				if (lProperty.length() == 0) {
					lProperty = FrameworkUtil.getBundle(ExternalObjectDefUtil.class).getBundleContext().getBundle().getLocation();
				}
				lDirectory = VIFSys.getVSysCanonicalPath(PreferencesHandler.KEY_CONF_ROOT, lProperty);
			}
		}
		lFilename = lDirectory + inObjectDefFilename;
		return new File(lFilename);
	}
	
	/**
	 * Reads the specified file and returns it's content.
	 * 
	 * @param inObjectDefFile File
	 * @return String the content of the specified file.
	 */
	public static String readObjectDef(File inObjectDefFile) {
		StringBuilder lContent = new StringBuilder();
		FileReader lReader = null;
		BufferedReader lBuffer= null;
		try {
			lReader = new FileReader(inObjectDefFile);
			lBuffer= new BufferedReader(lReader);
			String lLine = lBuffer.readLine();
			while (lLine != null) {
				lContent.append(lLine);
				lLine = lBuffer.readLine();
			}
		}
		catch (FileNotFoundException exc) {
			LOG.error("Error encountered while reading the resource from the file system!", exc);
		}
		catch (IOException exc) {
			LOG.error("Error encountered while reading the resource from the file system!", exc);
		}
		finally {
			try {
				if (lBuffer != null) lBuffer.close();
			}
			catch (IOException exc) {
				//left blank intentionally
			}
		}
		return new String(lContent);
	}

}
