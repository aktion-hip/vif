package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyCriterion;
import org.hip.kernel.bom.impl.HavingObjectImpl;
import org.hip.kernel.util.SortableItem;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on Apr 13, 2005
 */
public class HavingObjectImplTest {

	@Test
	public void testCreate() throws Exception {
		String lExpected1 = "first:76";
		String lExpected2 = "first:76, contained:55";
		
		HavingObject lHaving1 = new HavingObjectImpl();
		lHaving1.setValue("first", new Integer(76));
		assertEquals("key value 1", lExpected1, evaluate(lHaving1));
		
		HavingObject lHaving2 = new HavingObjectImpl();
		lHaving2.setValue("contained", new Integer(55), ">=");
		lHaving1.setValue(lHaving2);
		assertEquals("key value 2", lExpected2, evaluate(lHaving1));
	}
	
	private String evaluate(HavingObject inHaving) {
		String outValue = "";
		boolean lFirst = true;
		
		for (SortableItem lItem : inHaving.getItems2()) {
			if (!lFirst) {
				outValue += ", ";
			}
			lFirst = false;
			KeyCriterion lKeyCriterion = (KeyCriterion)lItem;
			if (isKey(lKeyCriterion)) {
				outValue += evaluate((HavingObject)lKeyCriterion.getValue());
			}
			else {
				outValue += lKeyCriterion.getName() + ":" + lKeyCriterion.getValue().toString();				
			}			
		}
		return outValue;
	}
	
	private boolean isKey(KeyCriterion inCriterion) {
		return KeyCriterion.NAME_FOR_KEY.equals(inCriterion.getName());
	}	
}
