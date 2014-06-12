package org.hip.kernel.bitmap.test;

import static org.junit.Assert.*;

import org.hip.kernel.bitmap.BitRow;
import org.hip.kernel.bitmap.BitRowImpl;
import org.hip.kernel.bitmap.Tuple;
import org.junit.Test;

/**
 * @author Benno Luthiger
 */
public class TupleTest {
	
	@Test
	public void testClone() {
		BitRow lBits = new BitRowImpl(8);
		lBits.setBitValue(12);
		Tuple lTuple1 = new Tuple("original", lBits);
		Tuple lTuple2 = (Tuple)lTuple1.clone();
		
		assertEquals("equal tuples", lTuple1, lTuple2);
		assertEquals("equal value 1", 12, lTuple2.getTupleBitRow().getBitValue());
		
		lTuple2.getTupleBitRow().setBitValue(8);
		assertTrue("not equal tuples", !lTuple1.equals(lTuple2));
		assertEquals("equal value 2", 12, lTuple1.getTupleBitRow().getBitValue());
		assertEquals("equal value 3", 8, lTuple2.getTupleBitRow().getBitValue());		
	}
}
