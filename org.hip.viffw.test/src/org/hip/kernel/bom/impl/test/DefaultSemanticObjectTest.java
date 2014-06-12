package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.impl.DefaultSemanticObject;
import org.hip.kernel.exc.VException;
import org.junit.Test;

/**
 * 	Test cases to test the functionality of the 
 *	DomainObject framework.
 *	@author	Benno Luthiger
 */
public class DefaultSemanticObjectTest {

	@Test
	public void testDynamicSetting() {
	
		DefaultSemanticObject lTestObject = new DefaultSemanticObject();
		assertNotNull( lTestObject ) ;
		
		String	lName		= "firstName";
		String  lValue		= "Adam";
		String  lNewValue	= "Eva";
	
		try { 
			lTestObject.set( lName, lValue ) 					;
			assertEquals( lTestObject.get( lName ), lValue ) 	;
			lTestObject.set( lName, lNewValue	) 				;
			assertEquals( lTestObject.get( lName ), lNewValue ) ;
			
			lTestObject.setVirgin();
			assertNull("virgin", lTestObject.get(lName));
		} 
		catch ( Exception exc ) {
			fail(exc.toString()) ;
		}
	}
	
	@Test
	public void testEquals() {
	
		DefaultSemanticObject lSemantic1 = new DefaultSemanticObject();
		assertNotNull(lSemantic1);
		DefaultSemanticObject lSemantic2 = new DefaultSemanticObject();
		assertNotNull(lSemantic2);
		DefaultSemanticObject lSemantic3 = new DefaultSemanticObject();
		assertNotNull(lSemantic3);
		
		String lFName		= "firstName";
		String lLName		= "lastName";
		String lFValue		= "Adam";
		String lLValue		= "HIP";
		String lNewValue	= "Eva";
	
		try { 
			lSemantic1.set(lFName, lFValue);
			lSemantic1.set(lLName, lLValue);
			lSemantic2.set(lFName, lFValue);
			lSemantic2.set(lLName, lNewValue);
			lSemantic3.set(lFName, lFValue);
			lSemantic3.set(lLName, lLValue);
	
			assertTrue("equals", lSemantic1.equals(lSemantic3));
			assertEquals("equal hash code", lSemantic1.hashCode(), lSemantic3.hashCode());
	
			assertTrue("not equals", !lSemantic1.equals(lSemantic2));
			assertTrue("not equal hash code", lSemantic1.hashCode() != lSemantic2.hashCode());
	
			lSemantic1.setVirgin();
			lSemantic3.setVirgin();
			assertTrue("equals 2", lSemantic1.equals(lSemantic3));
			assertEquals("equal hash code 2", lSemantic1.hashCode(), lSemantic3.hashCode());
		} 
		catch ( Exception exc ) {
			fail(exc.toString()) ;
		}
	}
	
	@Test
	public void testGetPropertyNames() throws VException {
		String[] lExpectedNames = new String[] {"firstName", "lastName", "birthDate"};
	
		String lExpected1 = "<org.hip.kernel.bom.impl.DefaultSemanticObject>\n" +
							"</org.hip.kernel.bom.impl.DefaultSemanticObject>";
		
		DefaultSemanticObject lTestObject = new DefaultSemanticObject();
		assertNotNull( lTestObject );
		assertEquals("toString 1", lExpected1, lTestObject.toString());
	
		lTestObject.set(lExpectedNames[0], "T. ");
		lTestObject.set(lExpectedNames[1], "Dummy");
		assertTrue("toString 2", lTestObject.toString().indexOf("<name=\"lastName\" value=\"Dummy\"/>")>0);
		assertTrue("toString 3", lTestObject.toString().indexOf("<name=\"firstName\" value=\"T. \"/>")>0);
		assertTrue("toString 4", lTestObject.toString().indexOf("<org.hip.kernel.bom.impl.DefaultSemanticObject>")==0);
		assertTrue("toString 5", lTestObject.toString().indexOf("</org.hip.kernel.bom.impl.DefaultSemanticObject>")>0);
		
		lTestObject.set(lExpectedNames[2], new java.util.GregorianCalendar( 1960, 01 , 22 ) 	);

		Collection<String> lExpected = Arrays.asList(lExpectedNames);
		for (Iterator<String> lNames = lTestObject.getPropertyNames(); lNames.hasNext();) {
			assertTrue("PropertyNames", lExpected.contains(lNames.next()));
		}
		
		for (int i = 0; i < lExpectedNames.length; i++) {
			assertTrue("PropertyNames " + i, lTestObject.getPropertyNames2().contains(lExpectedNames[i]));
		}
		assertEquals("equal size", lExpectedNames.length, lTestObject.getPropertyNames2().size());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException, VException {
		String lName = "Dummy";
		String lFirst = "T. ";
		
		DefaultSemanticObject lTestObject = new DefaultSemanticObject();
		lTestObject.set("firstName", lFirst);
		lTestObject.set("lastName", lName);
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lTestObject);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lTestObject = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		DefaultSemanticObject lRetrieved = (DefaultSemanticObject)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();
		
		assertEquals("retrieved first name", lFirst, lRetrieved.get("firstName"));
		assertEquals("retrieved last name", lName, lRetrieved.get("lastName"));
	}
	
}
