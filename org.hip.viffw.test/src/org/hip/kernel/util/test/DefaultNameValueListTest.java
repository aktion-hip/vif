package org.hip.kernel.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Vector;

import org.hip.kernel.util.DefaultNameValue;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.NameValue;
import org.hip.kernel.util.NameValueList;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class DefaultNameValueListTest {

	@Test
	public void testBasic() throws Exception {
		String lName1 = "firstName";
		String lName2 = "lastName";
		String lValue1 = "Adam";
		String lValue2 = "Blunder";
		String lValue3 = "testNull";
		String lValue4 = "Eva";
		String lValue5 = "Bach";
		Integer lValue6 = new Integer(100);
		Integer lValue7 = new Integer(150);
		
		String[] lExpNames = {lName1, lName2};
		Vector<String> lVExpNames = new Vector<String>(Arrays.asList(lExpNames));
		
		NameValueList lNameValueList = new DefaultNameValueList();
		assertNotNull(lNameValueList);
	
		lNameValueList.setValue(lName1, lValue1);
		assertEquals("size 1", 1, lNameValueList.size());
		assertEquals("getValue 1", lValue1, lNameValueList.getValue(lName1));
		assertEquals("get 1", lValue1, lNameValueList.get(lName1).getValue());
		
		lNameValueList.setValue(lName2, lValue2);
		assertEquals("size 2", 2, lNameValueList.size());
		assertEquals("getValue 2", lValue2, lNameValueList.getValue(lName2));
		assertEquals("get 2", lValue2, lNameValueList.get(lName2).getValue());

		lNameValueList.setValue(null, lValue3);
		assertEquals("size 3", 2, lNameValueList.size());

		lNameValueList.setValue(lName1, lValue4);
		assertEquals("size 4", 2, lNameValueList.size());
		assertEquals("getValue 3", lValue4, lNameValueList.getValue(lName1));
		assertEquals("get 3", lValue4, lNameValueList.get(lName1).getValue());

		lNameValueList.setValue(lName2, lValue5);
		assertEquals("size 5", 2, lNameValueList.size());
		assertEquals("getValue 4", lValue5, lNameValueList.getValue(lName2));
		assertEquals("get 4", lValue5, lNameValueList.get(lName2).getValue());

		lNameValueList.setValue(lName2, lValue6);
		assertEquals("size 6", 2, lNameValueList.size());
		assertEquals("getValue 5", lValue6, lNameValueList.getValue(lName2));
		assertEquals("get 5", lValue6, lNameValueList.get(lName2).getValue());

		for (String lName : lNameValueList.getNames2()) {
			assertTrue(lVExpNames.contains(lName));
		}

		NameValue lNameValue1 = new DefaultNameValue(null, lName1, lValue4);
		NameValue lNameValue2 = new DefaultNameValue(null, lName2, lValue6);
		NameValue[] lExpNameValues = {lNameValue1, lNameValue2};
		Vector<Object> lVExpNameValues = new Vector<Object>(Arrays.asList(lExpNameValues));
		for (Object lNameValue : lNameValueList.getNameValues2()) {
			assertTrue(lVExpNameValues.contains(lNameValue));
		}		

		//adding a new NameValue with an Name already existing in the List doesn't change the size
		NameValue lNameValue3 = new DefaultNameValue(null, lName1, lValue7);
		lNameValueList.add(lNameValue3);
		assertEquals("size 7", 2, lNameValueList.size());
		assertEquals("getValue 6", lValue7, lNameValueList.getValue(lName1));
		assertEquals("get 6", lValue7, lNameValueList.get(lName1).getValue());
		
		//adding a new NameValue
		String lName3 = "New";
		NameValue lNameValue4 = new DefaultNameValue(null, lName3, lValue7);
		lNameValueList.add(lNameValue4);
		assertEquals("size 8", 3, lNameValueList.size());
		assertEquals("getValue 7", lValue7, lNameValueList.getValue(lName3));
		assertEquals("get 7", lValue7, lNameValueList.get(lName3).getValue());
	}

	public void testEquals() throws Exception {
		String lName1 = "firstName";
		String lName2 = "lastName";
		String lValue1 = "Adam";
		String lValue2 = "Blunder";
		
		NameValueList lNameValueList1 = new DefaultNameValueList();
		assertNotNull( lNameValueList1 );
	
		NameValueList lNameValueList2 = new DefaultNameValueList();
		assertNotNull( lNameValueList2 );
		
		lNameValueList1.setValue(lName1, lValue1);
		lNameValueList1.setValue(lName2, lValue2);

		lNameValueList2.setValue(lName1, lValue1);
		lNameValueList2.setValue(lName2, lValue2);

		assertTrue("NameValueList equals 1", lNameValueList1.equals(lNameValueList2));
		assertEquals("hashCode 1", lNameValueList1.hashCode(), lNameValueList2.hashCode());

		//NameValues are equal if their name and value is equal
		lNameValueList1.setValue(lName2, lNameValueList2);
		assertTrue("NameValueList not equals 1", !lNameValueList1.equals(lNameValueList2));
		assertTrue("hashCode not equal 1", lNameValueList1.hashCode() != lNameValueList2.hashCode());

		lNameValueList1.setValue(lName2, lValue2);
		assertTrue("NameValueList equals 2", lNameValueList1.equals(lNameValueList2));
		assertEquals("hashCode 2", lNameValueList1.hashCode(), lNameValueList2.hashCode());
		
		lNameValueList1.setValue("test", new Integer(12));
		assertTrue("NameValueList not equals 2", !lNameValueList1.equals(lNameValueList2));
		assertTrue("hashCode not equal 2", lNameValueList1.hashCode() != lNameValueList2.hashCode());

		lNameValueList2 = new DefaultNameValueList();
		lNameValueList2.setValue(lName1, lValue1);
		lNameValueList2.setValue("test", lValue2);
		assertTrue("NameValueList not equals 3", !lNameValueList1.equals(lNameValueList2));
		assertTrue("hashCode not equal 3", lNameValueList1.hashCode() != lNameValueList2.hashCode());
	}
}
