package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.hip.kernel.servlet.impl.FileItem;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadText.IDownloadTextValues;
import org.hip.vif.core.bom.DownloadTextHome;
import org.hip.vif.core.util.BibliographyHelper;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 01.10.2010
 */
public class DownloadTextImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromDownload();
	}
	
	@Test
	public void testNew() throws Exception {
		DownloadTextHome lHome = data.getDownloadTextHome();
		assertEquals(0, lHome.getCount());
		
		String lLabel = "Download entry";
		DownloadText lDownload = (DownloadText) data.getDownloadTextHome().create();
		IDownloadTextValues lValues = new DownloadTextValues("application/pdf", lLabel);
		Long lID = lDownload.ucNew(lValues);
		
		assertEquals(1, lHome.getCount());
		assertEquals(lLabel, lHome.getDownload(lID.toString()).get(DownloadTextHome.KEY_LABEL).toString());
	}
	
// ---	
	public static class DownloadTextValues extends BibliographyHelper.DownloadTextValues implements DownloadText.IDownloadTextValues {
		private String mime;
		private String label;

		public DownloadTextValues(File inTempUpload, String inFileName, String inMimeType, Long inTextID, Long inMemberID) {
			super(inTempUpload, inFileName, inMimeType, inTextID, inMemberID);
		}
		DownloadTextValues(String inMime, String inLabel) {
			super(new File("."), "testFile", inMime, 22l, 88l);
			mime = inMime;
			label = inLabel;
		}
		public String getMimetype() {
			return mime;
		}
		public String getLabel() {
			return label;
		}
		public String getUUID() {
			return "DownloadTextValues.uuid";
		}
		public String getDoctype() {
			return ".type";
		}
		public Long getTextID() {
			return 55l;
		}
		public Long getMemberID() {
			return 66l;
		}
		public FileItem getFile() {
			return null;
		}
		public boolean hasUpload() {
			return false;
		}		
	}

}
