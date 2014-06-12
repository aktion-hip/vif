package org.hip.kernel.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.hip.kernel.exc.VException;
import org.hip.kernel.util.DefaultNameValueListVisitor;
import org.hip.kernel.util.NameValueListVisitor;
import org.hip.kernel.util.SortedList;
import org.hip.kernel.util.VInvalidSortCriteriaException;
import org.hip.kernel.util.VInvalidValueException;
import org.junit.Test;

/**
 * SortedListTest.java
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
public class SortedListTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testSort() {
		String lItem1 = "Z: Item for position 1";
		String lItem2 = "Y: Item for position 2";
		String lItem3 = "X: Item for position 3";
		String lItem4 = "A: Item for position 4";
		String[] lExpected = {lItem1, lItem2, lItem3, lItem4};

		SortedList lSortedList = new TestSortedList();
		try {
			lSortedList.setValue(lItem4, 4);
			lSortedList.setValue(lItem2, 2);
			lSortedList.setValue(lItem3, 3);
			lSortedList.setValue(lItem1, 1);
			
			assertEquals("size", 4, lSortedList.size());

			int i = 0;
			for (Iterator<?> lItems = lSortedList.getItems(); lItems.hasNext();) {
				assertEquals("sorted position: " + i, lExpected[i], (String)((TestSortableItem)lItems.next()).getValue());
				i++;
			}
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testException() {
		String lItem1 = "Z: Item for position 1";
//		String lItem2 = "Y: Item for position 2";
//		String lItem3 = "Z: Item for position 1";

		try {
			SortedList lSortedList = new TestSortedList();
			lSortedList.setValue(lItem1, "a");
			fail("exception 1: shouldn't get here");
		}
		catch (VInvalidSortCriteriaException exc) {
			assertNotNull("exception 1", exc);
		}
		catch (VInvalidValueException exc) {
			fail(exc.getMessage());
		}	

		try {
			SortedList lSortedList = new TestSortedList();
			lSortedList.setValue(null, 1);
			fail("exception 2: shouldn't get here");
		}
		catch (VInvalidSortCriteriaException exc) {
			fail(exc.getMessage());
		}
		catch (VInvalidValueException exc) {
			assertNotNull("exception 2", exc);
		}	
	}

	@Test
	public void testAccept() {
		String lItem1 = "Z: Item for position 1";
		String lItem2 = "Y: Item for position 2";
		String lItem3 = "X: Item for position 3";
		String lItem4 = "A: Item for position 4";
		String lExpected = "<org.hip.kernel.util.test.TestSortedList >\n" +
			"\t< org.hip.kernel.util.test.TestSortableItem <value='Z: Item for position 1' /> />\n" +
			"\t< org.hip.kernel.util.test.TestSortableItem <value='Y: Item for position 2' /> />\n" +
			"\t< org.hip.kernel.util.test.TestSortableItem <value='X: Item for position 3' /> />\n" +
			"\t< org.hip.kernel.util.test.TestSortableItem <value='A: Item for position 4' /> />\n" +
			"</org.hip.kernel.util.test.TestSortedList>";

		SortedList lSortedList = new TestSortedList();
		try {
			lSortedList.setValue(lItem4, 4);
			lSortedList.setValue(lItem2, 2);
			lSortedList.setValue(lItem3, 3);
			lSortedList.setValue(lItem1, 1);
			
			NameValueListVisitor lVisitor = new DefaultNameValueListVisitor();
			lSortedList.accept(lVisitor);
			assertEquals("accept", lExpected, lVisitor.toString());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testDublicates() {
		String lItem1 = "Z: Item for position 1";
		String lItem2 = "Y: Item for position 2";
		String lItem3 = "Z: Item for position 1";

		try {
			SortedList lSortedList = new TestSortedList();
			lSortedList.setValue(lItem1, 1);
			lSortedList.setValue(lItem2, 2);
			lSortedList.setValue(lItem3, 1);
			assertEquals("size 1", 2, lSortedList.size());
			
			NameValueListVisitor lVisitor = new DefaultNameValueListVisitor();
			lSortedList.accept(lVisitor);
			System.out.println(lVisitor.toString());
			
			lSortedList = new TestSortedList();
			lSortedList.setValue(lItem1, 1);
			lSortedList.setValue(lItem2, 2);
			lSortedList.setValue(lItem3, 3);
			assertEquals("size 2", 3, lSortedList.size());
			
			lVisitor = new DefaultNameValueListVisitor();
			lSortedList.accept(lVisitor);
			System.out.println(lVisitor.toString());
		}
		catch (VException exc) {
			fail(exc.getMessage());
		}
	}
}

