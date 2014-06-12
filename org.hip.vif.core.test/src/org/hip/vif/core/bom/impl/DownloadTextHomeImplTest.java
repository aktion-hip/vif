package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.DownloadText;
import org.hip.vif.core.bom.DownloadTextHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 01.10.2010
 */
public class DownloadTextHomeImplTest {
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
	public void testGetDownload() throws Exception {
		String lLabel = "Test File Entry";
		long lID = createDownload(lLabel, 73l);
		
		DownloadTextHome lHome = data.getDownloadTextHome();
		DownloadText lEntry = lHome.getDownload(String.valueOf(lID));
		assertEquals(lLabel, lEntry.get(DownloadTextHome.KEY_LABEL));
		
		lHome.deleteDownload(String.valueOf(lID));
		try {
			lHome.getDownload(String.valueOf(lID));
			fail("Shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			//intentionally left empty
		}
	}
	
	@Test
	public void testGetDownloads() throws Exception {
		Long lTextID = 73l;
		String[] lLabels = new String[] {"file entry 1", "file entry 2"};
		createDownload(lLabels[0], lTextID);
		createDownload("Different entry", 100l);
		createDownload(lLabels[1], lTextID);
		
		QueryResult lResult = data.getDownloadTextHome().getDownloads(lTextID);
		List<String> lExpected = Arrays.asList(lLabels);
		int i = 0;
		while (lResult.hasMoreElements()) {
			i++;
			assertTrue(lExpected.contains(lResult.next().get(DownloadTextHome.KEY_LABEL)));
		}
		assertEquals(2, i);
	}
	
	private long createDownload(String inLabel, Long inTextID) throws VException, SQLException {
		DomainObject lDownload = data.getDownloadTextHome().create();
		lDownload.set(DownloadTextHome.KEY_DOCTYPE, ".pdf");
		lDownload.set(DownloadTextHome.KEY_LABEL, inLabel);
		lDownload.set(DownloadTextHome.KEY_MIME, "application/pdf");
		lDownload.set(DownloadTextHome.KEY_MEMBERID, new Long(9));
		lDownload.set(DownloadTextHome.KEY_UUID, UUID.randomUUID().toString());
		lDownload.set(DownloadTextHome.KEY_TEXTID, inTextID);
		return lDownload.insert(true);
	}

}
