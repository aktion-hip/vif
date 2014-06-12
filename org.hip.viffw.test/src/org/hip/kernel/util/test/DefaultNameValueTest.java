package org.hip.kernel.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Vector;

import org.hip.kernel.util.DefaultNameValue;
import org.hip.kernel.util.NameValue;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class DefaultNameValueTest {

	@Test
	public void testDo() {
		String lName = "Test";
		Object lValue = new Integer(100);
		NameValue lNameValue = new DefaultNameValue(null, lName, lValue);
	
		assertEquals("getName", lName, lNameValue.getName());
		assertEquals("getValue", lValue, lNameValue.getValue());
	
		//setName() does nothing
		try {
			lNameValue.setName(lName + "!!");
			assertEquals("getName", lName, lNameValue.getName());
		}
		catch (org.hip.kernel.util.VInvalidNameException exc) {
			fail(exc.getMessage());
		}
	
		//set() does nothing
		try {
			lNameValue.set(lName + "!!", lName + "!!!");
			assertEquals("getName", lName, lNameValue.getName());
			assertEquals("getValue", lValue, lNameValue.getValue());
		}
		catch (org.hip.kernel.util.VInvalidNameException exc) {
			fail(exc.getMessage());
		}
		catch (org.hip.kernel.util.VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	
		//setValue() changes the value
		try {
			lNameValue.setValue(lName + "!!!");
			assertEquals("getValue", lName + "!!!", lNameValue.getValue());
		}
		catch (org.hip.kernel.util.VInvalidValueException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testEquals() {
		String lName1 = "Test";
		Object lValue1 = new Integer(100);
		NameValue lNameValue1 = new DefaultNameValue(null, lName1, lValue1);
	
		String lName2 = "Test2";
		Object lValue2 = "Value";
		NameValue lNameValue2 = new DefaultNameValue(null, lName2, lValue2);
		
		NameValue lNameValue3 = new DefaultNameValue(null, lName1, lValue1);
	
		assertTrue("equals 1", lNameValue1.equals(lNameValue3));
		assertEquals("equal hashCode 1", lNameValue1.hashCode(), lNameValue3.hashCode());
	
		assertTrue("not equals 1", !lNameValue1.equals(lNameValue2));
		assertTrue("not equal hashCode 1", lNameValue1.hashCode() != lNameValue2.hashCode());
	
		//change the value of lNameValue3
		try {
			lNameValue3.setValue(lValue2);
		}
		catch (org.hip.kernel.util.VInvalidValueException exc) {
			fail(exc.getMessage());
		}
		assertTrue("not equals 2", !lNameValue1.equals(lNameValue3));
		assertTrue("not equal hashCode 2", lNameValue1.hashCode() != lNameValue3.hashCode());
		assertTrue("not equals 3", !lNameValue1.equals(lNameValue3));
		assertTrue("not equal hashCode 3", lNameValue1.hashCode() != lNameValue3.hashCode());
	
		//change back the value of lNameValue3
		try {
			lNameValue3.setValue(lValue1);
		}
		catch (org.hip.kernel.util.VInvalidValueException exc) {
			fail(exc.getMessage());
		}
		assertTrue("equals 2", lNameValue1.equals(lNameValue3));
		assertEquals("equal hashCode 2", lNameValue1.hashCode(), lNameValue3.hashCode());
		assertTrue("not equals 4", !lNameValue3.equals(lNameValue2));
		assertTrue("not equal hashCode 4", lNameValue3.hashCode() != lNameValue2.hashCode());
		
	}
	
	@Test
	public void testExtract() {
		
		String lName1 = "Test";
		Object lValue1 = new Integer(100);
		String lName2 = "Test2";
		Object lValue2 = "Value";
		
		String lToExtract = lName1 + "=" + lValue1.toString() + ", " +
							lName2 + "=" + lValue2 + ", " +
							lName1 + "=" + lValue1.toString(); 
		Vector<NameValue> lExtracted = DefaultNameValue.extract(lToExtract);
		
		NameValue lNameValue1 = new DefaultNameValue(null, lName1, lValue1.toString());
		NameValue lNameValue2 = new DefaultNameValue(null, lName2, lValue2);
		NameValue lNameValue3 = new DefaultNameValue(null, lName1, lValue1.toString());
		NameValue[] lExpected = {lNameValue1, lNameValue2, lNameValue3};
		int i = 0;
		for (NameValue lNameValue : lExtracted) {
			assertTrue(lExpected[i++].equals(lNameValue));
		}
	}

}
