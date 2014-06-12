package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.model.TypeDef;
import org.hip.kernel.bom.model.impl.MappingDefImpl;
import org.hip.kernel.bom.model.impl.PropertyDefImpl;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class PropertyDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
	
		String[] lExpected = {"propertyType", "valueType", "propertyName", "mappingDef", "formatPattern", "relationshipDef"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		
		PropertyDef lDef = new PropertyDefImpl() ;
		assertNotNull("testCreation not null", lDef ) ;
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext();) {
			assertTrue("testCreation " + i, lVExpected.contains((String)lNames.next()));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testCreationWithInitialValues() {
	
		Object[][] lInitValues = {
			{ "propertyName"	, "firstName"					}
		,	{ "propertyType"	, "simple"						}		
		,	{ "valueType"		, "java.lang.String" 			}
		} ;
		
		PropertyDef lDef = new PropertyDefImpl( lInitValues ) ;
		assertNotNull("testCreationWithInitialValues not null", lDef ) ;
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext(); ) {
			String lName = (String) lNames.next();
			try {
				i++;
				VSys.out.println( "name=" + lName + " value=" + lDef.get( lName )  ) ;
			} 
			catch ( Exception exc ) {
				VSys.err.println( exc ) ;
			}
		}
		assertEquals("number of propertiess", 6, i);
	}
	
	@Test
	public void testEquals() {
	
		PropertyDef lDef1 = new PropertyDefImpl();
		PropertyDef lDef2 = new PropertyDefImpl();
		PropertyDef lDef3 = new PropertyDefImpl();
		assertNotNull("not null 1", lDef1);
		assertNotNull("not null 2", lDef2);
		assertNotNull("not null 3", lDef3);
	
		String[] lExpected1 = {"PERSON", "NAME"};
		String[] lExpected2 = {"PERSON", "VORNAME"};
		Object[][] lInitValues1 = {
			{ MappingDefDef.tableName	, lExpected1[0]	}
		,	{ MappingDefDef.columnName	, lExpected1[1]	}		
		} ;
		Object[][] lInitValues2 = {
			{ MappingDefDef.tableName	, lExpected2[0]	}
		,	{ MappingDefDef.columnName	, lExpected2[1]	}		
		} ;
		MappingDef lMappingDef1 = new MappingDefImpl(lInitValues1);
		MappingDef lMappingDef2 = new MappingDefImpl(lInitValues2);
		
		try {
			lDef1.set(PropertyDefDef.propertyName, "ID");
			lDef1.set(PropertyDefDef.propertyType, "simple");
			lDef1.set(PropertyDefDef.valueType, "Number");
	
			lDef2.set(PropertyDefDef.propertyName, "Name");
			lDef2.set(PropertyDefDef.propertyType, "simple");
			lDef2.set(PropertyDefDef.valueType, "String");
			
			lDef3.set(PropertyDefDef.propertyName, "ID");
			lDef3.set(PropertyDefDef.propertyType, "simple");
			lDef3.set(PropertyDefDef.valueType, "Number");
	
			assertTrue("equals 1", lDef1.equals(lDef3));
			assertEquals("equal hash code 1", lDef1.hashCode(), lDef3.hashCode());
			assertTrue("not equals 1", !lDef1.equals(lDef2));
			assertTrue("not equal hash code 1", lDef1.hashCode() != lDef2.hashCode());
	
			lDef1.set(PropertyDefDef.mappingDef, lMappingDef1);
			lDef3.set(PropertyDefDef.mappingDef, lMappingDef1);
	
			assertTrue("equals 2", lDef1.equals(lDef3));
			assertEquals("equal hash code 2", lDef1.hashCode(), lDef3.hashCode());
			
			lDef3.set(PropertyDefDef.mappingDef, lMappingDef2);
			assertTrue("not equals 2", !lDef1.equals(lDef3));
			assertTrue("not equal hash code 2", lDef1.hashCode() != lDef3.hashCode());
		}
		catch (org.hip.kernel.bom.SettingException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testGetValueClassType() {
	
		PropertyDef lDef = new PropertyDefImpl();
		try {
			lDef.set(PropertyDefDef.valueType, TypeDef.String);
			assertEquals("ValueType String", "java.lang.String", lDef.getValueClassType());
	
			lDef.set(PropertyDefDef.valueType, TypeDef.Number);
			assertEquals("ValueType Number", "java.lang.Number", lDef.getValueClassType());
			
			lDef.set(PropertyDefDef.valueType, TypeDef.Timestamp);
			assertEquals("ValueType Timestamp", "java.sql.Timestamp", lDef.getValueClassType());
			
			lDef.set(PropertyDefDef.valueType, TypeDef.Date);
			assertEquals("ValueType Date", "java.sql.Date", lDef.getValueClassType());
		}
		catch (org.hip.kernel.bom.SettingException exc) {
			fail("Setting of ValueType");
		}
	}
	
	@Test
	public void testToString() {
		PropertyDef lDef = new PropertyDefImpl();
		assertNotNull("testToString", lDef);
	
		try {
			lDef.set(PropertyDefDef.propertyName, "ID");
			lDef.set(PropertyDefDef.propertyType, "simple");
			lDef.set(PropertyDefDef.valueType, "Number");
	
			assertEquals("toString", "< org.hip.kernel.bom.model.impl.PropertyDefImpl propertyName=\"ID\" valueType=\"Number\" propertyType=\"simple\" />", lDef.toString());
		}
		catch (org.hip.kernel.bom.SettingException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testClone() throws Exception {
		PropertyDef lDef1 = new PropertyDefImpl();
		assertNotNull("testClone", lDef1);
	
		lDef1.set(PropertyDefDef.propertyName, "ID1");
		lDef1.set(PropertyDefDef.propertyType, "simple");
		lDef1.set(PropertyDefDef.valueType, "Number");
		
		PropertyDef lDef2 = (PropertyDef)((PropertyDefImpl)lDef1).clone();
		assertTrue("equal objects", lDef1.equals(lDef2));
		
		//PropertyDefs are independent
		lDef2.set(PropertyDefDef.propertyName, "ID2");
		assertTrue("not equal objects", !lDef1.equals(lDef2));
	}
}
