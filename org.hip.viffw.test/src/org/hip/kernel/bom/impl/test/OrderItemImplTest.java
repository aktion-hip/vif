package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import org.hip.kernel.bom.OrderItem;
import org.hip.kernel.bom.impl.OrderItemImpl;
import org.hip.kernel.util.VInvalidValueException;
import org.junit.Test;

/**
 * OrderItemImplTest.java
 * 
 * Created on 15.09.2002
 * @author Benno Luthiger
 */
public class OrderItemImplTest {

	@Test
	public void testEquals() {
		String lValue1 = "fColumnName";
		String lValue2 = "fColumnName2";
		
		try {
			OrderItem lOrderItem1 = new OrderItemImpl(lValue1, true, 1);
			OrderItem lOrderItem2 = new OrderItemImpl(lValue2, true, 1);
			OrderItem lOrderItem3 = new OrderItemImpl(lValue1, false, 1);
			OrderItem lOrderItem4 = new OrderItemImpl(lValue1, true, 2);
			OrderItem lOrderItem5 = new OrderItemImpl(lValue1, true, 1);
	
			assertTrue("equals", lOrderItem1.equals(lOrderItem5));
			assertTrue("not equals 1", !lOrderItem1.equals(lOrderItem2));
			assertTrue("not equals 2", !lOrderItem1.equals(lOrderItem3));
			assertTrue("not equals 3", !lOrderItem1.equals(lOrderItem4));

			assertEquals("equal hashcode", lOrderItem1.hashCode(), lOrderItem5.hashCode());
			assertTrue("not equal hashcode 1", lOrderItem1.hashCode() != lOrderItem2.hashCode());
			assertTrue("not equal hashcode 2", lOrderItem1.hashCode() != lOrderItem3.hashCode());
			assertTrue("not equal hashcode 3", lOrderItem1.hashCode() != lOrderItem4.hashCode());
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testToString() {
		String lValue1 = "fColumnName1";
		String lValue2 = "fColumnName2";
		String lExpected1 = "< org.hip.kernel.bom.impl.OrderItemImpl value=\"fColumnName1\" position=\"1\" direction=\"DESC\" />";
		String lExpected2 = "< org.hip.kernel.bom.impl.OrderItemImpl value=\"fColumnName2\" position=\"2\" direction=\"ASC\" />";
		try {
			OrderItem lOrderItem1 = new OrderItemImpl(lValue1, true, 1);
			OrderItem lOrderItem2 = new OrderItemImpl(lValue2, false, 2);
	
			assertEquals("toString 1", lExpected1, lOrderItem1.toString());
			assertEquals("toString 2", lExpected2, lOrderItem2.toString());
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
}
