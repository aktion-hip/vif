package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.util.NameValue;
import org.junit.Test;

/**
 * 	TestCases to test the PropertySet implementation.
 *	
 *	@author	Benno Luthiger
 */
public class PropertySetImplTest {
	private static final String[] EXPECTED_NAMES = new String[] {"firstName", "lastName"};
	private static final String[] EXPECTED_VALUES1 = new String[] {"Adam", "Blunder"};
	private static final String[] EXPECTED_VALUES2 = new String[] {"Bach", "Eva"};
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testBasic() throws VException {
		PropertySet lPropertySet = new PropertySetImpl( null );
		assertNotNull( lPropertySet );
	
		lPropertySet.setValue(EXPECTED_NAMES[0], EXPECTED_VALUES1[0]);
		assertEquals("size 1", 1, lPropertySet.size());
		
		lPropertySet.setValue(EXPECTED_NAMES[1], EXPECTED_VALUES1[1]);
		assertEquals("size 2", 2, lPropertySet.size());
		
		Collection<String> lNames = lPropertySet.getNames2();
		for (int i = 0; i < EXPECTED_NAMES.length; i++) {
			assertTrue("names " + i, lNames.contains(EXPECTED_NAMES[i]));
		}
		Collection<String> lExpected = Arrays.asList(EXPECTED_VALUES1);		
		for (NameValue lNameValue : lPropertySet.getNameValues2()) {
			assertTrue("values 1", lExpected.contains(lNameValue.getValue()));
		}

		lPropertySet.setValue(null, "testNull");
		assertEquals("size 3", 2, lPropertySet.size());

		Iterator<Property> lChanged = lPropertySet.getChangedProperties();
		assertTrue("numberOfChanged 1", !lChanged.hasNext());

		lPropertySet.setValue("firstName", "Eva");
		int lCount = 0;
		for (lChanged = lPropertySet.getChangedProperties(); lChanged.hasNext();) {
			lChanged.next();
			lCount++;
		}
		assertEquals("numberOfChanged 2", 1, lCount);
		assertEquals("numberOfChanged 3", 1, lPropertySet.getChangedProperties2().size());

		lPropertySet.setValue("lastName", "Bach");
		assertEquals("numberOfChanged 4", 2, lPropertySet.getChangedProperties2().size());
		
		lExpected = Arrays.asList(EXPECTED_VALUES2);		
		for (NameValue lNameValue : lPropertySet.getNameValues2()) {
			assertTrue("values 2", lExpected.contains(lNameValue.getValue()));
		}

		lPropertySet.setVirgin();
		assertEquals("size 4", 2, lPropertySet.size());
		assertTrue("numberOfChanged 5", !lChanged.hasNext());
	}
	
	@Test
	public void testEquals() throws VException {
		String lName1 = "firstName";
		String lName2 = "lastName";
		String lValue1 = "Adam";
		String lValue2 = "Blunder";
		
		PropertySet lPropertySet1 = new PropertySetImpl( null );
		assertNotNull( lPropertySet1 );
	
		PropertySet lPropertySet2 = new PropertySetImpl( null );
		assertNotNull( lPropertySet2 );
		
		lPropertySet1.setValue(lName1, lValue1);
		lPropertySet1.setValue(lName2, lValue2);

		lPropertySet2.setValue(lName1, lValue1);
		lPropertySet2.setValue(lName2, lValue2);

		assertTrue("PropertySet equals 1", lPropertySet1.equals(lPropertySet2));
		assertEquals("hashCode 1", lPropertySet1.hashCode(), lPropertySet2.hashCode());

		//properties are equal if their name is equal
		//therefore the propertysets are equal even if their values differ!
		lPropertySet1.setValue(lName2, lPropertySet2);
		assertTrue("PropertySet equals 2", lPropertySet1.equals(lPropertySet2));
		assertEquals("hashCode 2", lPropertySet1.hashCode(), lPropertySet2.hashCode());

		lPropertySet1.setValue(lName2, lValue2);
		assertTrue("PropertySet equals 3", lPropertySet1.equals(lPropertySet2));
		assertEquals("hashCode 3", lPropertySet1.hashCode(), lPropertySet2.hashCode());
		
		lPropertySet1.setValue("test", new Integer(12));
		assertTrue("PropertySet not equals 1", !lPropertySet1.equals(lPropertySet2));
		assertTrue("hashCode not equal 1", lPropertySet1.hashCode() != lPropertySet2.hashCode());

		lPropertySet2 = new PropertySetImpl( null );
		lPropertySet2.setValue(lName1, lValue1);
		lPropertySet2.setValue("test", lValue2);
		assertTrue("PropertySet not equals 2", !lPropertySet1.equals(lPropertySet2));
		assertTrue("hashCode not equal 2", lPropertySet1.hashCode() != lPropertySet2.hashCode());
	}
}
