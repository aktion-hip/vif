package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 13.08.2010
 */
public class TextMaxHomeTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() throws Exception {
		String lExpected = "SELECT MAX(tblTextVersion.NVERSION) AS MaxVersion FROM tblTextVersion WHERE tblTextVersion.TEXTID = 8";
		TextMaxHome lHome = new TextMaxHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		assertEquals("select max", lExpected, lTest.next().toString());
	}
	
	@Test
	public void testMaxVersion() throws Exception {
		Long lTextID = data.createText("The first Book", "Riese, Adam");
		
		TextHome lTextHome = data.getTextHome();
		TextMaxHome lHome = BOMHelper.getTextMaxHome();
		
		assertEquals("count 0", 1, lTextHome.getCount());
		assertEquals("max 0", 0, lHome.getMaxVersion(lTextID));
		
		int lVersion = 0;
		lVersion = clone(lTextID, lVersion, lTextHome);
		lVersion = clone(lTextID, lVersion, lTextHome);
		lVersion = clone(lTextID, lVersion, lTextHome);
		lVersion = clone(lTextID, lVersion, lTextHome);
		lVersion = clone(lTextID, lVersion, lTextHome);
		lVersion = clone(lTextID, lVersion, lTextHome);
		
		assertEquals("count 1", 7, lTextHome.getCount());
		assertEquals("max 1", 6, lHome.getMaxVersion(lTextID));
	}

	private int clone(Long inTextID, int inOldVersion, TextHome inTextHome) throws VException, SQLException {
		Text lText = inTextHome.getText(inTextID, inOldVersion);
		DomainObject lClone = inTextHome.create();
		lClone.set(TextHome.KEY_TYPE, lText.get(TextHome.KEY_TYPE));
		lClone.set(TextHome.KEY_TITLE, lText.get(TextHome.KEY_TITLE));
		lClone.set(TextHome.KEY_AUTHOR, lText.get(TextHome.KEY_AUTHOR));
		lClone.set(TextHome.KEY_COAUTHORS, lText.get(TextHome.KEY_COAUTHORS));
		lClone.set(TextHome.KEY_SUBTITLE, lText.get(TextHome.KEY_SUBTITLE));
		lClone.set(TextHome.KEY_YEAR, lText.get(TextHome.KEY_YEAR));
		lClone.set(TextHome.KEY_PUBLICATION, lText.get(TextHome.KEY_PUBLICATION));
		lClone.set(TextHome.KEY_PAGES, lText.get(TextHome.KEY_PAGES));
		lClone.set(TextHome.KEY_VOLUME, lText.get(TextHome.KEY_VOLUME));
		lClone.set(TextHome.KEY_NUMBER, lText.get(TextHome.KEY_NUMBER));
		lClone.set(TextHome.KEY_PUBLISHER, lText.get(TextHome.KEY_PUBLISHER));
		lClone.set(TextHome.KEY_PLACE, lText.get(TextHome.KEY_PLACE));
		lClone.set(TextHome.KEY_REMARK, lText.get(TextHome.KEY_REMARK));
		lClone.set(TextHome.KEY_STATE, lText.get(TextHome.KEY_STATE));
		lClone.set(TextHome.KEY_ID, lText.get(TextHome.KEY_ID));
		int outVersion = inOldVersion + 1;
		lClone.set(TextHome.KEY_VERSION, new Long(outVersion));
		lClone.insert(true);
		return outVersion;
	}
	
//	---
	
	@SuppressWarnings("serial")
	private class TextMaxHomeSub extends TextMaxHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			KeyObject lKey = new KeyObjectImpl();
			try {
				lKey.setValue(TextHome.KEY_ID, new Long(8));
				out.add(createSelectString(lKey));
			} 
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}

}
