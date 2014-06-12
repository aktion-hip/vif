package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.OrderItem;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidSortCriteriaException;
import org.hip.kernel.util.VInvalidValueException;
import org.junit.Test;

/**
 * OrderObjectImplTest.java
 * 
 * Created on 15.09.2002
 * @author Benno Luthiger
 */
public class OrderObjectImplTest {
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSetValue() {
		String lColumn1 = "sColumn1";
		String lColumn2 = "sColumn2";
		String lColumn3 = "sColumn3";
		String[] lColumns = {lColumn1, lColumn2, lColumn3};

		OrderObject lOrderObject = new OrderObjectImpl();
		try {
			lOrderObject.setValue(lColumn1, true, 1);
			lOrderObject.setValue(lColumn3, false, 3);
			lOrderObject.setValue(lColumn2, true, 2);

			assertEquals("size", 3, lOrderObject.size());

			int i = 0;
			for (SortableItem lItem : lOrderObject.getItems2()) {
				assertEquals(lColumns[i], ((OrderItem)lItem).getValue().toString());
				i++;
			}
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}

		lOrderObject = new OrderObjectImpl();
		try {
			lOrderObject.setValue("sColumn1", 1);
			assertTrue("order direction", !((OrderItem)lOrderObject.getItems().next()).isDescending());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
		
		lOrderObject = new OrderObjectImpl();
		try {
			lOrderObject.setValue(new Integer(12), 1);
			fail("exception 1: shouldn't get here 1");
		}
		catch (VInvalidValueException exc) {
			assertNotNull("exception 1", exc);
		}
		catch (VInvalidSortCriteriaException exc) {
			fail(exc.getMessage());
		}
		
		lOrderObject = new OrderObjectImpl();
		try {
			lOrderObject.setValue("sColumn1", "a");
			fail("exception 2: shouldn't get here 2");
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
		catch (VInvalidSortCriteriaException exc) {
			assertNotNull("exception 2", exc);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testEmpty() {
		OrderObject lOrder = new OrderObjectImpl();
		assertFalse("empty order object", lOrder.getItems().hasNext());
	}
	
	@Test
	public void testEquals() {
		OrderObject lOrderObject1 = new OrderObjectImpl();
		OrderObject lOrderObject2 = new OrderObjectImpl();
		OrderObject lOrderObject3 = new OrderObjectImpl();
		OrderObject lOrderObject4 = new OrderObjectImpl();

		try {
			lOrderObject1.setValue("sColumn1", true, 1);
			lOrderObject1.setValue("sColumn2", true, 2);
			lOrderObject1.setValue("sColumn3", true, 3);
	
			lOrderObject2.setValue("sColumn1", false, 1);
			lOrderObject2.setValue("sColumn2", true, 2);
			lOrderObject2.setValue("sColumn3", true, 3);
	
			lOrderObject3.setValue("sColumn1", true, 1);
			lOrderObject3.setValue("sColumn2", true, 3);
			lOrderObject3.setValue("sColumn3", true, 2);
	
			lOrderObject4.setValue("sColumn1", true, 1);
			lOrderObject4.setValue("sColumn2", true, 2);
			lOrderObject4.setValue("sColumn3", true, 3);
			
			assertTrue("equals", lOrderObject1.equals(lOrderObject4));
			assertTrue("not equals 1", !lOrderObject1.equals(lOrderObject2));
			assertTrue("not equals 2", !lOrderObject1.equals(lOrderObject3));
	
			assertEquals("equal hashcode 1", lOrderObject1.hashCode(), lOrderObject4.hashCode());
			assertTrue("not equal hashcode 1", lOrderObject1.hashCode() != lOrderObject2.hashCode());
			assertEquals("equal hashcode 2", lOrderObject1.hashCode(), lOrderObject3.hashCode());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	public void testToString() {
		String lExpected = 
			"<org.hip.kernel.bom.impl.OrderObjectImpl >\n" +
			"\t< org.hip.kernel.bom.impl.OrderItemImpl <value='sColumn1' /> />\n" +
			"\t< org.hip.kernel.bom.impl.OrderItemImpl <value='sColumn2' /> />\n" +
			"\t< org.hip.kernel.bom.impl.OrderItemImpl <value='sColumn3' /> />\n" +
			"</org.hip.kernel.bom.impl.OrderObjectImpl>";
		OrderObject lOrderObject = new OrderObjectImpl();
		try {
			lOrderObject.setValue("sColumn1", true, 1);
			lOrderObject.setValue("sColumn2", false, 2);
			lOrderObject.setValue("sColumn3", true, 3);
			assertEquals("to string", lExpected, lOrderObject.toString());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
}
