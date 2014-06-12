package org.hip.kernel.util.test;

import static org.junit.Assert.*;
import org.hip.kernel.util.DefaultNameValueListVisitor;
import org.hip.kernel.util.NameValueListVisitor;
import org.hip.kernel.util.SortableItem;
import org.hip.kernel.util.VInvalidValueException;
import org.junit.Test;

/**
 * SortableItemTest.java
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
public class SortableItemTest {

	@Test
	public void testToString() {
		String lExpectedValue = "Test of a SortableItem";
		String lExpectedString = "< org.hip.kernel.util.test.TestSortableItem value=\"Test of a SortableItem\" position=\"22\" />";
		try {
			SortableItem lSortableItem = new TestSortableItem(lExpectedValue, 22);
	
			assertEquals("get value", lExpectedValue, lSortableItem.getValue());		
			assertEquals("to string", lExpectedString, lSortableItem.toString());
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testEquals() {
		String lValue1 = "Test of a SortableItem";
		String lValue2 = "Test of a SortableItem2";
		
		try {
			SortableItem lSortableItem1 = new TestSortableItem(lValue1, 22);
			SortableItem lSortableItem2 = new TestSortableItem(lValue2, 22);
			SortableItem lSortableItem3 = new TestSortableItem(lValue1, 10);
			SortableItem lSortableItem4 = new TestSortableItem(lValue1, 22);
	
			assertTrue("equals", lSortableItem1.equals(lSortableItem4));
			assertTrue("not equals 1", !lSortableItem1.equals(lSortableItem2));
			assertTrue("not equals 2", !lSortableItem1.equals(lSortableItem3));

			assertEquals("equal hashcode", lSortableItem1.hashCode(), lSortableItem4.hashCode());
			assertTrue("not equal hashcode 1", lSortableItem1.hashCode() != lSortableItem2.hashCode());
			assertTrue("not equal hashcode 2", lSortableItem1.hashCode() != lSortableItem3.hashCode());
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testAccept() {
		String lValue1 = "Test of a SortableItem";
		String lValue2 = "Test of a SortableItem2";
		String lExpected1 = "< org.hip.kernel.util.test.TestSortableItem <value='Test of a SortableItem' /> />";
		String lExpected2 = "< org.hip.kernel.util.test.TestSortableItem <value='Test of a SortableItem2' /> />";
		try {
			SortableItem lSortableItem1 = new TestSortableItem(lValue1, 22);
			SortableItem lSortableItem2 = new TestSortableItem(lValue2, 10);
			
			NameValueListVisitor lVisitor = new DefaultNameValueListVisitor();
			lSortableItem1.accept(lVisitor);
			assertEquals("accept 1", lExpected1, lVisitor.toString());
			
			lVisitor = new DefaultNameValueListVisitor();
			lSortableItem2.accept(lVisitor);
			assertEquals("accept 2", lExpected2, lVisitor.toString());
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
}
