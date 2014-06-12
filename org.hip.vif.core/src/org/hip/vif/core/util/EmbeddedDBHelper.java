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

package org.hip.vif.core.util;

import java.io.File;

/**
 * Helper class for embedded DB.
 * 
 * @author Luthiger
 * Created: 11.02.2012
 */
public class EmbeddedDBHelper {
	private static final String EMBEDDED_INDICATOR = "embedded";
	
	public final static String DATA_SUBDIR = "data";
	public final static String DATA_DB = "vif_data";

	/**
	 * Returns the path to the embedded DB. The parent directory is guaranteed to exist.
	 * 
	 * @return String returns the path to the embedded DB
	 */
	public static String getEmbeddedDBChecked() {
		File lWorkspace = new File(".");
		File lDataSpace = new File(lWorkspace, DATA_SUBDIR);
		if (!lDataSpace.exists()) {
			lDataSpace.mkdir();
		}
		File lDBSpace = new File(lDataSpace, DATA_DB);
		return lDataSpace.exists() ? lDBSpace.getAbsolutePath() : lWorkspace.getAbsolutePath();
	}
	
	/**
	 * Checks whether the specified driver name is the driver of an embedded DB.
	 * 
	 * @param inDriverName String e.g. <code>org.apache.derby.jdbc.EmbeddedDriver/Derby (embedded)/10.5.1.1</code>
	 * @return boolean <code>true</code> if the driver indicates an embedded DB
	 */
	public static boolean checkEmbedded(String inDriverName) {
		return inDriverName.toLowerCase().contains(EMBEDDED_INDICATOR);
	}
}
