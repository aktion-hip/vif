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

package org.hip.vif.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.util.BibliographyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;

/**
 * A download file type of resource.
 * 
 * @author Luthiger
 * Created: 19.06.2011
 */
@SuppressWarnings("serial")
public class DownloadFileResouce extends FileResource {
	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileResouce.class);
			
	private DownloadFile download;

	/**
	 * Constructor
	 * 
	 * @param inLabelName String the file's external name
	 * @param inDownload {@link DownloadFile} a wrapper around the downloadable file
	 * @param inApplication {@link Application} the application instance
	 */
	public DownloadFileResouce(String inLabelName, DownloadFile inDownload, Application inApplication) {
		super(new File(inLabelName), inApplication);
		download = inDownload;
	}
	
	@Override
	public DownloadStream getStream() {
		try {
			DownloadStream out = new DownloadStream(new FileInputStream(download.getFile()), download.getMIMEType(), download.getFileName());
			out.setCacheTime(0);
			out.setParameter("Content-Disposition", "attachment; filename=\"" + download.getFileName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			LOG.debug("Downloaded file \"{}\"", download.getFileName()); //$NON-NLS-1$
			return out;
		} 
		catch (FileNotFoundException exc) {
			LOG.error("Couldn't find download file \"{}\"!", download.getFileName(), exc); //$NON-NLS-1$
			return null;
		}
	}
	
// ---
	
	/**
	 * Inner class to wrapp a downloadable file.
	 */
	public static class DownloadFile {
		private String fileName;
		private String mimeType;
		private File file;

		/**
		 * Constructor
		 * 
		 * @param inDownload {@link DownloadText} the download text business object instance
		 * @throws VException
		 */
		public DownloadFile(DownloadText inDownload) throws VException {
			fileName = inDownload.get(DownloadTextHome.KEY_LABEL).toString();
			mimeType = inDownload.get(DownloadTextHome.KEY_MIME).toString();
			String lUUID = inDownload.get(DownloadTextHome.KEY_UUID).toString();
			String lType = inDownload.get(DownloadTextHome.KEY_DOCTYPE).toString();
			file = BibliographyHelper.createUploadFile(lUUID, lType);
		}
		
		/**
		 * @return String the download file's internal name
		 */
		public String getFileName() {
			return fileName;
		}
		
		/**
		 * @return String the download file's MIME type
		 */
		public String getMIMEType() {
			return mimeType;
		}
		
		/**
		 * @return int the download file's length
		 */
		public int getLength() {
			return (int) (file.exists() ? file.length() : 0);
		}
		
		public File getFile() {
			return file;
		}
	}

}
