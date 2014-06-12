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
package org.hip.vif.web.interfaces;

import java.io.File;

import org.hip.vif.core.bom.Text;
import org.hip.vif.core.exc.ProhibitedFileException;

/**
 * Interface for bibliography tasks. This interface defines the methods a
 * bibliography task has to provide.
 * 
 * @author Luthiger Created: 01.12.2011
 */
public interface IBibliographyTask extends IPluggableWithLookup {

	/**
	 * Deletes the existing download file(s).
	 * 
	 * @return boolean <code>true</code> if the files could be deleted
	 *         successfully
	 */
	boolean deleteDownloads();

	/**
	 * Persist the changes made to the text.
	 * 
	 * @param inText
	 *            {@link Text} the actual text model containing the changes
	 * @param inCreateVersion
	 *            boolean <code>true</code> if the changes should be saved to a
	 *            new version, <code>false</code> if the actual version should
	 *            be updated
	 * @return boolean <code>true</code> if the changes could be successfully
	 *         persisted
	 */
	boolean saveText(Text inText, boolean inCreateVersion);

	/**
	 * Saves the uploaded file to the system.
	 * 
	 * @param inTempUpload
	 *            File the temporary upload
	 * @param inFileName
	 *            String
	 * @param inMimeType
	 *            String
	 * @param inDeleteDownloads
	 *            boolean <code>true</code> if the existing download files
	 *            should be deleted first
	 * @return Long the download entry's ID or 0, if an error happened
	 * @throws ProhibitedFileException
	 */
	Long saveFileUpload(File inTempUpload, String inFileName,
			String inMimeType, boolean inDeleteDownloads)
			throws ProhibitedFileException;

}
