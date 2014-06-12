package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 20.09.2009
 */
public class JoinQuestionToCompletionAndContributorsHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObject() throws Exception {
		JoinQuestionToCompletionAndContributorsHome lHome = new JoinQuestionToCompletionAndContributorsHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblQuestion.QUESTIONID, tblQuestion.GROUPID, tblCompletion.COMPLETIONID, tblCompletion.SCOMPLETION, tblCompletion.QUESTIONID, tblCompletion.DTMUTATION, tblCompletion.NSTATE, tblCompletionAuthorReviewer.MEMBERID, tblCompletionAuthorReviewer.NTYPE, tblMember.SNAME, tblMember.SFIRSTNAME FROM tblQuestion INNER JOIN tblCompletion ON tblQuestion.QUESTIONID = tblCompletion.QUESTIONID INNER JOIN tblCompletionAuthorReviewer ON tblCompletion.COMPLETIONID = tblCompletionAuthorReviewer.COMPLETIONID INNER JOIN tblMember ON tblCompletionAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblQuestion.GROUPID = 8";
		assertEquals("test object", lExpected, (String)lTest.next());
//		System.out.println((String)lTest.next());
	}
	
//	---
	@SuppressWarnings("serial")
	private class JoinQuestionToCompletionAndContributorsHomeSub extends JoinQuestionToCompletionAndContributorsHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHome.KEY_GROUP_ID, new Long(8));
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}

}
