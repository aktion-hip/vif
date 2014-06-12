package org.hip.vif.core.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.exc.ProhibitedFileException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 01.10.2010
 */
public class BibliographyHelperTest {

	@Before
	public void setUp() throws Exception {
		String lHere = new File("").getAbsolutePath();
		VSys.setContextPath(lHere);
	}

	@After
	public void tearDown() throws Exception {
		File lStore = new File(getLocalPath());
		if (!lStore.exists()) return;
		deleteChildren(lStore);
		lStore.delete();
	}

	@Test
	public void testCreateUploadFile() throws Exception {
		String lUUID = "1234567890abcdefghijklmnopqrstxyz";
		File lUpload = BibliographyHelper.createUploadFile(lUUID, "pdf");
		String lPath = lUpload.getCanonicalPath().replace("\\", "/");
		assertTrue(lPath.endsWith("store/1/2/34567890abcdefghijklmnopqrstxyz.pdf"));
	}
	
	@Test
	public void testCheckType() throws Exception {
		DownloadText.IDownloadTextValues lValues = new DownloadTextValues("application/pdf", "Test");
		lValues.checkType();
		
		//we don't like "application/octet-stream", "application/x-msi" to upload
		lValues = new DownloadTextValues("application/octet-stream", "Test");
		try {
			lValues.checkType();
			fail("Shouldn't get here!");
		}
		catch (ProhibitedFileException exc) {
			//intentionally left empty
		}
		lValues = new DownloadTextValues("application/x-msi", "Test");
		try {
			lValues.checkType();
			fail("Shouldn't get here!");
		}
		catch (ProhibitedFileException exc) {
			//intentionally left empty
		}
	}
	
	private String getLocalPath() {
		String lHere = new File("").getAbsolutePath();
		return new File(new File(lHere).getParent(), BibliographyHelper.NAME_STORE).getAbsolutePath();
	}
	
	private void deleteChildren(File inFile) {
		for (File lFile : inFile.listFiles()) {
			deleteChildren(lFile);
			lFile.delete();
		}
	}
	
// ---	
	public class DownloadTextValues extends BibliographyHelper.DownloadTextValues implements DownloadText.IDownloadTextValues {
		private String label;

		public DownloadTextValues(File inTempUpload, String inFileName, String inMimeType, Long inTextID, Long inMemberID) {
			super(inTempUpload, inFileName, inMimeType, inTextID, inMemberID);
		}
		DownloadTextValues(String inMime, String inLabel) {
			super(new File("."), "FileName", inMime, 44l, 55l);
			label = inLabel;
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
	}
	
}
