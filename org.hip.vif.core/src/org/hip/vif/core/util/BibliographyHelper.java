/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

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

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.hip.kernel.exc.VException;
import org.hip.kernel.servlet.impl.FileItem;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadText.IDownloadTextValues;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.exc.ProhibitedFileException;

/**
 * Helper class for bibliography tasks.
 *
 * @author Luthiger
 * Created: 18.08.2010
 */
public class BibliographyHelper {
	public static final String NAME_STORE = "store";
	
	/**
	 * @see DownloadText.IDownloadTextValues
	 */
	public static class DownloadTextValues implements DownloadText.IDownloadTextValues {
		private static final String[] PROHIBITED_TYPES = {"application/octet-stream", "application/x-msi"};
		
		private Long memberID;
		private Long textID;
		private String doctype = "";
		private String mimetype = "";
		private String uuid = "";
		private String label = "";
		private FileItem file;
		
		/**
		 * 
		 * @param inTempUpload {@link File}
		 * @param inFileName String
		 * @param inMimeType String
		 * @param inTextID String
		 * @param inMemberID Long
		 */
		public DownloadTextValues(File inTempUpload, String inFileName, String inMimeType, Long inTextID, Long inMemberID) {
			textID = inTextID;
			memberID = inMemberID;
			if (inTempUpload == null) return;
			uuid = UUID.randomUUID().toString();
			mimetype = inMimeType;
			label = inFileName;
			String[] lSplits = inFileName.split("\\.");
			doctype = lSplits[lSplits.length-1];
		}
		
		public void setTextID(Long inTextID) {
			textID = inTextID;
		}
		public Long getTextID() {
			return textID;
		}
		public String getDoctype() {
			return doctype;
		}
		public String getLabel() {
			return label;
		}
		public Long getMemberID() {
			return memberID;
		}
		public String getMimetype() {
			return mimetype;
		}
		public String getUUID() {
			return uuid;
		}
		public FileItem getFile() {
			return file;
		}
		public boolean hasUpload() {
			return uuid.length() != 0;
		}
		public void checkType() throws ProhibitedFileException {
			if (Arrays.asList(PROHIBITED_TYPES).contains(mimetype.toLowerCase())) {
				throw new ProhibitedFileException();
			}
		}
	}
	
	/**
	 * Convenience method
	 * 
	 * @param inUUID String
	 * @param inDoctype String e.g. <code>pdf</code>
	 * @return File the upload file generated with uuid and doctype, e.g. <code>store/a/b/cdef...xyz.pdf</code>
	 */
	public static File createUploadFile(String inUUID, String inDoctype) {
		File out = createDirChecked(getStore(), inUUID.substring(0, 1));
		out = createDirChecked(out, inUUID.substring(1, 2));
		out = new File(out, String.format("%s.%s", inUUID.substring(2), inDoctype));
		return out;
	}
	
	private static File createDirChecked(File inParent, String inSub) {
		File out = new File(inParent.getAbsolutePath(), inSub);
		if (!out.exists()) {
			out.mkdir();
		}
		return out;
	}
	
	private static File getStore() {
		File outStore = new File(WorkspaceHelper.getRootDir(), NAME_STORE);
		if (!outStore.exists()) {
			outStore.mkdir();
		}
		return outStore;
	}
	
	/**
	 * Convenience method: uploads file, i.e. writes the file to the destination location.
	 * 
	 * @param inValues {@link IDownloadTextValues}
	 * @throws Exception
	 */
	public static void uploadFile(IDownloadTextValues inValues) throws Exception {
		inValues.getFile().write(BibliographyHelper.createUploadFile(inValues.getUUID(), inValues.getDoctype()));
	}

	/**
	 * Convenience method: deletes the uploaded file, i.e. removes the file from the destination location.
	 * 
	 * @param inDownload DownloadText the entry whose file is to delete
	 * @return boolean <code>true</code> if and only if the file is successfully deleted; <code>false</code> otherwise
	 * @throws VException
	 */
	public static boolean deleteUpload(DownloadText inDownload) throws VException {
		return BibliographyHelper.createUploadFile(inDownload.get(DownloadTextHome.KEY_UUID).toString(),
				inDownload.get(DownloadTextHome.KEY_DOCTYPE).toString()).delete();
	}

}
