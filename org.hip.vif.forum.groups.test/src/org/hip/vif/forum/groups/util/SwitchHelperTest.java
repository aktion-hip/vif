package org.hip.vif.forum.groups.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.BookmarkHome;
import org.hip.vif.core.bom.SubscriptionHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwitchHelperTest {
	private static DataHouseKeeper data;
	
	private Long memberID;
	private Long questionID;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	
	@Before
	public void setUp() throws Exception {
		memberID = new Long(data.createMember());
		questionID = new Long(data.createQuestion("Test Question", "1.1.1"));
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}

	@Test
	public final void testCreateBookmark() throws Exception {
		BookmarkHome lBookmarkHome = data.getBookmarkHome();
		
		SwitchHelper lHelper = new SwitchHelper(false, false, questionID, memberID);
		
		assertEquals(0, lBookmarkHome.getCount());
		assertFalse(lHelper.hasBookmark());
		
		//create
		String lBookmarkText = "Test Bookmark";
		lHelper.createBookmark(lBookmarkText);
		
		assertEquals(1, lBookmarkHome.getCount());
		assertTrue(lHelper.hasBookmark());
		
		QueryResult lResult = lBookmarkHome.select();
		GeneralDomainObject lBookmark = lResult.next();
		assertEquals(lBookmarkText, lBookmark.get(BookmarkHome.KEY_BOOKMARKTEXT));
		assertEquals(memberID.toString(), lBookmark.get(BookmarkHome.KEY_MEMBERID).toString());
		assertEquals(questionID.toString(), lBookmark.get(BookmarkHome.KEY_QUESTIONID).toString());
		
		//delete
		lHelper.deleteBookmark();
		
		assertEquals(0, lBookmarkHome.getCount());
		assertFalse(lHelper.hasBookmark());
	}

	@Test
	public final void testCreateSubscription() throws Exception {
		SubscriptionHome lSubscriptionHome = data.getSubscriptionHome();
		
		SwitchHelper lHelper = new SwitchHelper(false, false, questionID, memberID);
		
		assertEquals(0, lSubscriptionHome.getCount());
		assertFalse(lHelper.hasSubscription());
		
		//create
		lHelper.createSubscription(true);
		
		assertEquals(1, lSubscriptionHome.getCount());
		assertTrue(lHelper.hasSubscription());
		
		QueryResult lResult = lSubscriptionHome.select();
		GeneralDomainObject lSubscription = lResult.next();
		assertEquals("1", lSubscription.get(SubscriptionHome.KEY_LOCAL).toString());
		assertEquals(memberID.toString(), lSubscription.get(SubscriptionHome.KEY_MEMBERID).toString());
		assertEquals(questionID.toString(), lSubscription.get(SubscriptionHome.KEY_QUESTIONID).toString());
		
		//delete
		lHelper.deleteSubscription();
		
		assertEquals(0, lSubscriptionHome.getCount());
		assertFalse(lHelper.hasSubscription());
	}

}
