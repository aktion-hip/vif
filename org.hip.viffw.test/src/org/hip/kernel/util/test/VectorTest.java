package org.hip.kernel.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class VectorTest {
	
	@Test
	public void testAdd() {
		String[] lExpected = {"Element 1", "2nd Element"};
		
		TestVector lStringSet = new TestVector();
		
		assertEquals("size 1", 0, lStringSet.size());
		
		lStringSet.add(lExpected[0]);
		assertEquals("size 2", 1, lStringSet.size());
		
		lStringSet.add(lExpected[1]);
		assertEquals("size 3", 2, lStringSet.size());
		
		lStringSet.add(new Integer(12));
		assertEquals("size 4", 2, lStringSet.size());
		
		assertEquals("Element at 1", lExpected[1], lStringSet.get(1));
		assertEquals("Element at 0", lExpected[0], lStringSet.get(0));
	}
}
