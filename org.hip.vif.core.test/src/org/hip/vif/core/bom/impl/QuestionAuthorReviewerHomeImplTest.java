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
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 29.05.2003
 * @author Luthiger
 */
public class QuestionAuthorReviewerHomeImplTest {
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
		data.deleteAllFromQuestionAuthorReviewer();
		data.deleteAllFromMember();		
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testSetAuthorReviewer() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lContributionID = new Long(33);

		QuestionAuthorReviewerHome lAuthorReviewerHome = (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
		int lCount = lAuthorReviewerHome.getCount();
		
		lAuthorReviewerHome.setAuthor(lAuthorID, lContributionID);
		assertEquals("count authors 1", lCount + 1, lAuthorReviewerHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lAuthorID);
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lContributionID);
		DomainObject lAuthorReviewer = lAuthorReviewerHome.findByKey(lKey);
		
		assertTrue("is author", ResponsibleHome.Type.AUTHOR.check(lAuthorReviewer.get(ResponsibleHome.KEY_TYPE)));
		
		lAuthorReviewerHome.setReviewer(lReviewerID, lContributionID);
		assertEquals("count authors 2", lCount + 2, lAuthorReviewerHome.getCount());
		
		lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lReviewerID);
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lContributionID);
		lAuthorReviewer = lAuthorReviewerHome.findByKey(lKey);
		
		assertTrue("is reviewer", ResponsibleHome.Type.REVIEWER.check(lAuthorReviewer.get(ResponsibleHome.KEY_TYPE)));
	}
	
	@Test
	public void testRemoveReviewer() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lContributionID = new Long(33);
		
		QuestionAuthorReviewerHome lHome = (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
		
		lHome.setAuthor(lAuthorID, lContributionID);
		lHome.setReviewer(lReviewerID, lContributionID);		
		assertEquals(2, lHome.getCount());
		
		lHome.removeReviewer(lReviewerID, new Long(lContributionID.toString()));
		assertEquals(2, lHome.getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ResponsibleHome.KEY_MEMBER_ID, lReviewerID);
		lKey.setValue(QuestionAuthorReviewerHome.KEY_QUESTION_ID, lContributionID);
		assertTrue(ResponsibleHome.Type.REVIEWER_REFUSED.check(lHome.findByKey(lKey).get(ResponsibleHome.KEY_TYPE)));
		
		try {
			lHome.removeReviewer(lAuthorID, new Long(lContributionID.toString()));
			fail("shouldn't get here");
		}
		catch (BOMNotFoundException exc) {
			// intentionally left empty
		}
	}
	
	@Test
	public void testGetAuthor() throws Exception {
		String lMemberID = data.createMember();
		Long lContributionID = new Long(33);

		QuestionAuthorReviewerHome lHome = (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
		assertEquals(0, lHome.getCount());
		
		lHome.setAuthor(new Long(lMemberID), lContributionID);
		assertEquals(1, lHome.getCount());
		Member lMember = lHome.getAuthor(new Long(lContributionID.longValue()));
		assertEquals(lMemberID, lMember.get(MemberHome.KEY_ID).toString());
	}
	
	@Test
	public void testCheckRefused() throws Exception {
		Long lAuthorID = new Long(22);
		Long lReviewerID = new Long(23);
		Long lContributionID = new Long(33);
		
		QuestionAuthorReviewerHome lHome = (QuestionAuthorReviewerHome)BOMHelper.getQuestionAuthorReviewerHome();
		
		lHome.setAuthor(lAuthorID, lContributionID);
		lHome.setReviewer(lReviewerID, lContributionID);		
		lHome.removeReviewer(lReviewerID, lContributionID);
		
		assertTrue(lHome.checkRefused(lReviewerID, lContributionID));
		assertFalse(lHome.checkRefused(lAuthorID, lContributionID));
		assertFalse(lHome.checkRefused(new Long(10), lContributionID));
		assertFalse(lHome.checkRefused(lReviewerID, new Long(10)));
		assertFalse(lHome.checkRefused(new Long(10), new Long(10)));
	}
	
}
