package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.HavingObjectImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.QuestionHome;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Apr 12, 2005
 */
public class QuestionForGuestsHomeTest {
	private static DataHouseKeeper data;
	
	@SuppressWarnings("serial")
	private class QuestionForGuestsHomeSub extends QuestionForGuestsHome {
		public QuestionForGuestsHomeSub() {
			super();
		}
		public Vector<Object> createTestObjects() {
			Vector<Object> outTest = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(QuestionHome.KEY_GROUP_ID, new Integer(2));
				HavingObject lHaving = new HavingObjectImpl();
				lHaving.setValue(QuestionForGuestsHome.KEY_DEPTH, new Integer(4), "<");
				OrderObject lOrder = new OrderObjectImpl();
				lOrder.setValue(QuestionHome.KEY_QUESTION_DECIMAL, true, 0);
				outTest.add(createSelectString(lKey, lOrder, lHaving));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return outTest;
		}		
	}

	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testObjects() {
		String lExpected = "";
		if (data.isDBMySQL()) {
			lExpected = "SELECT tblQuestion.QUESTIONID, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION, tblQuestion.SREMARK, tblQuestion.NSTATE, tblQuestion.DTMUTATION, tblQuestion.GROUPID, tblQuestion.BROOTQUESTION, (LENGTH(tblQuestion.SQUESTIONID)-LENGTH(REPLACE(tblQuestion.SQUESTIONID, \".\", \"\"))) AS GUEST_DEPTH FROM tblQuestion WHERE tblQuestion.GROUPID = 2 HAVING GUEST_DEPTH < 4 ORDER BY tblQuestion.SQUESTIONID DESC";
		}
		else if (data.isDBOracle()) {
			lExpected = "";
		}
		QuestionForGuestsHome lSubHome = new QuestionForGuestsHomeSub();
		Iterator<Object> lTest = lSubHome.getTestObjects();
		assertEquals("test object", lExpected, (String)lTest.next());
	}
	
}
