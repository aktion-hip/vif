package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 10.10.2010
 */
public class TextAuthorReviewerHomeImplTest {	
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
		data.deleteAllFromTextAuthorReviewer();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testSetAuthorReviewer() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lTextID = new Long(33);
		int lVersion = 0;
		
		TextAuthorReviewerHome lHome = BOMHelper.getTextAuthorReviewerHome();
		assertEquals(0, lHome.getCount());
		
		lHome.setAuthor(lAuthorID, lTextID, lVersion);
		assertEquals(1, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthorID);
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Integer(lVersion));
		DomainObject lAuthorReviewer = lHome.findByKey(lKey);
		
		assertTrue(ResponsibleHome.Type.AUTHOR.check(lAuthorReviewer.get(ResponsibleHome.KEY_TYPE)));
		
		lHome.setReviewer(lReviewerID, lTextID, lVersion);
		assertEquals(2, lHome.getCount());
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lReviewerID);
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Integer(lVersion));
		lAuthorReviewer = lHome.findByKey(lKey);
		
		assertTrue(ResponsibleHome.Type.REVIEWER.check(lAuthorReviewer.get(ResponsibleHome.KEY_TYPE)));
	}
	
	@Test
	public void testRemoveReviewer() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lTextID = new Long(33);
		int lVersion = 0;
		
		TextAuthorReviewerHome lHome = BOMHelper.getTextAuthorReviewerHome();
		lHome.setAuthor(lAuthorID, lTextID, lVersion);
		lHome.setReviewer(lReviewerID, lTextID, lVersion);
		assertEquals(2, lHome.getCount());
		
		lHome.removeReviewer(lReviewerID, lTextID, lVersion);
		assertEquals(2, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lReviewerID);
		lKey.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextID);
		lKey.setValue(TextAuthorReviewerHome.KEY_VERSION, new Integer(lVersion));		
		assertTrue(ResponsibleHome.Type.REVIEWER_REFUSED.check(lHome.findByKey(lKey).get(ResponsibleHome.KEY_TYPE)));
		
		
		try {
			lHome.removeReviewer(lAuthorID, lTextID, lVersion);
			fail("shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			// intentionally left empty
		}
	}
	
	@Test
	public void testGetAuthor() throws Exception {
		String lMemberID = data.createMember();
		Long lTextID = new Long(33);
		int lVersion = 0;
		
		TextAuthorReviewerHome lHome = BOMHelper.getTextAuthorReviewerHome();
		lHome.setAuthor(new Long(lMemberID), lTextID, lVersion);
		assertEquals(1, lHome.getCount());
		Member lMember = lHome.getAuthor(lTextID, lVersion);
		assertEquals(lMemberID, lMember.get(MemberHome.KEY_ID).toString());

	}
	
	@Test
	public void testCheckRefuse() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lTextID = new Long(33);
		int lVersion = 0;
		
		TextAuthorReviewerHome lHome = BOMHelper.getTextAuthorReviewerHome();
		lHome.setAuthor(lAuthorID, lTextID, lVersion);
		lHome.setReviewer(lReviewerID, lTextID, lVersion);
		lHome.removeReviewer(lReviewerID, lTextID, lVersion);
		
		assertTrue(lHome.checkRefused(lReviewerID, lTextID, lVersion));
		assertFalse(lHome.checkRefused(lAuthorID, lTextID, lVersion));
		assertFalse(lHome.checkRefused(new Long(10), lTextID, lVersion));
		assertFalse(lHome.checkRefused(lReviewerID, new Long(10), lVersion));
		assertFalse(lHome.checkRefused(new Long(10), new Long(10), lVersion));
	}
	
}
