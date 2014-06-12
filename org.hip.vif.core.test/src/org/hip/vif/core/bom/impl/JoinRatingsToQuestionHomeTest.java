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
 * Created 30.08.2009 
 */
public class JoinRatingsToQuestionHomeTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjects() throws Exception {
		JoinRatingsToQuestionHome lHome = new JoinRatingsToQuestionHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblRatingsQuestion.RATINGEVENTSID, tblQuestion.QUESTIONID, tblQuestion.SQUESTION, tblQuestion.SQUESTIONID FROM tblRatingsQuestion INNER JOIN tblQuestion ON tblRatingsQuestion.QUESTIONID = tblQuestion.QUESTIONID WHERE tblRatingsQuestion.RATINGEVENTSID = 67";
		assertEquals("test sql", lExpected, (String)lTest.next());
	}

//  
	
	@SuppressWarnings("serial")
	private class JoinRatingsToQuestionHomeSub extends JoinRatingsToQuestionHome {
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
