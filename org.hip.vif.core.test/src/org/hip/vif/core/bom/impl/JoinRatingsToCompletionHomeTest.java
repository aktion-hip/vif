package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created 31.08.2009
 */
public class JoinRatingsToCompletionHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}

	@Test
	public void testObjects() throws Exception {
		JoinRatingsToCompletionHome lHome = new JoinRatingsToCompletionHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblRatingsCompletion.RATINGEVENTSID, tblCompletion.COMPLETIONID, tblCompletion.QUESTIONID, tblCompletion.SCOMPLETION, tblQuestion.SQUESTION, tblQuestion.SQUESTIONID FROM tblRatingsCompletion INNER JOIN tblCompletion ON tblRatingsCompletion.COMPLETIONID = tblCompletion.COMPLETIONID INNER JOIN tblQuestion ON tblQuestion.QUESTIONID = tblCompletion.QUESTIONID WHERE tblRatingsCompletion.RATINGEVENTSID = 67";
		assertEquals("test objects", lExpected, (String)lTest.next());
	}

//	
	@SuppressWarnings("serial")
	private class JoinRatingsToCompletionHomeSub extends JoinRatingsToCompletionHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(RatingEventsHome.KEY_ID, new Long(67));
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
	
}
