package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 07.08.2010
 */
public class TextQuestionHomeTest {
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
	public void testCreateDelete() throws Exception {
		Long lTextID1 = 87l;
		Long lTextID2 = 88l;
		String lQuestionID1 = "543";
		String lQuestionID2 = "600";
		
		TextQuestionHome lHome = data.getTextQuestionHome();
		lHome.createEntry(lTextID1, lQuestionID1);
		lHome.createEntry(lTextID2, lQuestionID2);
		lHome.createEntry(lTextID1, lQuestionID2);
		
		assertEquals("number 1", 3, lHome.getCount());
		
		lHome.deleteByText(lTextID1);

		assertEquals("number 2", 1, lHome.getCount());
	}

}
