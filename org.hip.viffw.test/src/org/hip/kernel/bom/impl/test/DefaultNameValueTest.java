package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.util.DefaultNameValue;
import org.junit.Test;

/**
 * 	Test cases to test the implementation of DefaultNameValue
 *	
 *	@author	Benno Luthiger
 */
public class DefaultNameValueTest {
	
	@Test
	public void testEquals() {
		DefaultNameValue lNameValue1 = new DefaultNameValue(null, "firstName", "Adam");
		DefaultNameValue lNameValue2 = new DefaultNameValue( null, "firstName", "Eva");
		DefaultNameValue lNameValue3 = lNameValue1;
		DefaultNameValue lNameValue4 = new DefaultNameValue( null, "firstName", "Adam");
		
		assertTrue("identity", lNameValue1 == lNameValue3);
		assertTrue("no identity", lNameValue1 != lNameValue4);
		assertTrue("equality", lNameValue1.equals(lNameValue4));
		assertTrue("no equality 1", !lNameValue1.equals(lNameValue2));
		assertTrue("no equality 2", !lNameValue1.equals(null));
	}
	
	@Test
	public void testToString() {
		DefaultNameValue lNameValue1 = new DefaultNameValue( null, "firstName", "Adam");
		DefaultNameValue lNameValue2 = new DefaultNameValue( null, "firstName", "Eva");
		DefaultNameValue lNameValue3 = new DefaultNameValue( null, "firstName", null);
		DefaultNameValue lNameValue4 = new DefaultNameValue( null, "lastName", lNameValue1);
	
		String lExpcected1 = "< org.hip.kernel.util.DefaultNameValue name=\"firstName\" value=\"Adam\" />";
		String lExpcected2 = "< org.hip.kernel.util.DefaultNameValue name=\"firstName\" value=\"Eva\" />";
		String lExpcected3 = "< org.hip.kernel.util.DefaultNameValue name=\"firstName\" value=\"null\" />";
		String lExpcected4 = "< org.hip.kernel.util.DefaultNameValue name=\"lastName\" value=\"< org.hip.kernel.util.DefaultNameValue name=\"firstName\" value=\"Adam\" />\" />";
		
		assertEquals("toString 1", lExpcected1, lNameValue1.toString());
		assertEquals("toString 2", lExpcected2, lNameValue2.toString());
		assertEquals("toString 3", lExpcected3, lNameValue3.toString());
		assertEquals("toString 4", lExpcected4, lNameValue4.toString());
	}
}
