package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.BookmarkHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Mar 3, 2004
 */
public class BookmarkHomeImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromBookmark();
	}
	
	@Test
	public void testDo() throws Exception {
		Long lMemberID = new Long(65);
		String lQuestionID1 = "73";
		String lBookmarkText1 = "Test of Bookmark 1";
		String lQuestionID2 = "100";
		String lBookmarkText2 = "Test of Bookmark 2";
		
		BookmarkHome lHome = data.getBookmarkHome();
		int lCount = lHome.getCount();
		lHome.ucNew(lQuestionID1, lMemberID, lBookmarkText1);
		assertEquals("count 1", lCount+1, lHome.getCount());
		lHome.ucNew(lQuestionID2, lMemberID, lBookmarkText2);
		assertEquals("count 2", lCount+2, lHome.getCount());
		
		assertTrue("has bookmark 1", lHome.hasBookmark(lQuestionID1, lMemberID));
		assertTrue("has bookmark 2", lHome.hasBookmark(lQuestionID2, lMemberID));
		
		lHome.delete(new Long(lQuestionID2), lMemberID);
		assertEquals("count 3", lCount+1, lHome.getCount());
		assertFalse("not bookmark", lHome.hasBookmark(lQuestionID2, lMemberID));
	}
}
