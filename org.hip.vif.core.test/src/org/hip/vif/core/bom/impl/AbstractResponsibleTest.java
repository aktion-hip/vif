package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.bom.DomainObject;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 15.08.2003
 * @author Luthiger
 */
public class AbstractResponsibleTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromQuestionAuthorReviewer();
	}
	
	@Test
	public void testSetRefused() throws Exception {
		Long lMemberID = new Long(99);
		Long lQuestionID = new Long(87);
		QuestionAuthorReviewerHome lHome = data.getQuestionAuthorReviewerHome();
		int lCount = lHome.getCount();
		lHome.setReviewer(lMemberID, lQuestionID);
		assertEquals("count 1", lCount+1, lHome.getCount());
		
		DomainObject lResponsible = (DomainObject)((ResponsibleHome)lHome).getResponsible(lQuestionID.toString(), lMemberID);
		assertTrue("state 1", ResponsibleHome.Type.REVIEWER.check(lResponsible.get(ResponsibleHome.KEY_TYPE)));
		
		((ResponsibleHome)lHome).getResponsible(lQuestionID.toString(), lMemberID).setRefused();
		lResponsible = (DomainObject)((ResponsibleHome)lHome).getResponsible(lQuestionID.toString(), lMemberID);
		assertTrue("state 2", ResponsibleHome.Type.REVIEWER_REFUSED.check(lResponsible.get(ResponsibleHome.KEY_TYPE)));
	}
}
