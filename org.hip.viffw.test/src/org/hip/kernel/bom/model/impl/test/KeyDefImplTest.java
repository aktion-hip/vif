package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.impl.PrimaryKeyDefImpl;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class KeyDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
	
		KeyDef lDef = new PrimaryKeyDefImpl() ;
		assertNotNull("testCreation 1", lDef ) ;
	
		Iterator<?> lNames = lDef.getKeyNames();
		assertTrue("testCreation 2", !lNames.hasNext());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testCreationWithInitialValues() {
	
		String[] lValues = {"codeSchemaId", "codeId"};
		
		Vector<String> lKeyItems = new Vector<String>(Arrays.asList(lValues));	
		Object[][] lInitValues = {
			{KeyDefDef.keyItems, lKeyItems}
		} ;
		
		KeyDef lDef = new PrimaryKeyDefImpl( lInitValues ) ;
		assertNotNull("testCreationWithInitialValues not null", lDef ) ;
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getKeyNames(); lNames.hasNext(); ) {
			String lName = (String)lNames.next();
			assertEquals("testCreationWithInitialValues " + i, lValues[i++], lName);
		}
		assertEquals("number of values", i, lValues.length);
	
		try {
			for (Iterator<?> lProperties = lDef.getPropertyNames(); lProperties.hasNext(); ) {
				String lName = (String)lProperties.next();
				VSys.out.println(lName + " -> " + lDef.get(lName));
			}
		}
		catch (org.hip.kernel.bom.GettingException exc) {
			fail();
		}
		
	}
	
	@Test
	public void testEquals() {
	
		String[] lValues1 = {"codeSchemaId", "codeId"};
		String[] lValues2 = {"codeSchemaId", "memberId"};
		
		Vector<String> lKeyItems1 = new Vector<String>(Arrays.asList(lValues1));
		Object[][] lInitValues1 = {
			{KeyDefDef.keyItems, lKeyItems1}
		} ;
		Vector<String> lKeyItems2 = new Vector<String>(Arrays.asList(lValues2));
		Object[][] lInitValues2 = {
			{KeyDefDef.keyItems, lKeyItems2}
		} ;
		
		KeyDef lDef1 = new PrimaryKeyDefImpl(lInitValues1);
		assertNotNull("testEquals1 not null", lDef1);
		KeyDef lDef2 = new PrimaryKeyDefImpl(lInitValues2);
		assertNotNull("testEquals2 not null", lDef2);
		KeyDef lDef3 = new PrimaryKeyDefImpl(lInitValues1);
		assertNotNull("testEquals3 not null", lDef3);
	
		assertTrue("equals", lDef1.equals(lDef3));
		assertEquals("equal hash code", lDef1.hashCode(), lDef3.hashCode());
	
		assertTrue("not equals", !lDef1.equals(lDef2));
		assertTrue("not equal hash code", lDef1.hashCode() != lDef2.hashCode());
	}
	
	@Test
	public void testToString() {
	
		String[] lValues = {"codeSchemaId", "codeId"};
		
		Vector<String> lKeyItems = new Vector<String>(Arrays.asList(lValues));	
		Object[][] lInitValues = {
			{KeyDefDef.keyItems, lKeyItems}
		} ;
		
		KeyDef lDef = new PrimaryKeyDefImpl( lInitValues ) ;
		assertNotNull("testToString not null", lDef ) ;
		assertEquals("toString", "< org.hip.kernel.bom.model.impl.PrimaryKeyDefImpl keyPropertyName=\"codeSchemaId\" keyPropertyName=\"codeId\"  />", lDef.toString());
	}
}
