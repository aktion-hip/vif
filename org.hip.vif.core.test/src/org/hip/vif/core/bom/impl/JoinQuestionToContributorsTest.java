package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.sys.VSys;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 20.09.2009
 */
public class JoinQuestionToContributorsTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}

	@Test
	public void testGetSortString() throws Exception {
		JoinQuestionToContributorsHome lHome = (JoinQuestionToContributorsHome) VSys.homeManager.getHome(JoinQuestionToContributors.HOME_CLASS_NAME);
		JoinQuestionToContributors lQuestion = (JoinQuestionToContributors) lHome.create();
		lQuestion.set(QuestionHome.KEY_QUESTION_DECIMAL, "1:1.4");
		assertEquals("sort for 1.4", "00001.00004", lQuestion.getSortString());
		
		lQuestion.set(QuestionHome.KEY_QUESTION_DECIMAL, "87:23.4.1.10.5");
		assertEquals("sort for 23.4.1.10.5", "00023.00004.00001.00010.00005", lQuestion.getSortString());		
	}

}
