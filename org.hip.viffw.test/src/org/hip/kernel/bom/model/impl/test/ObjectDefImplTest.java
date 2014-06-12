package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Vector;

import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.bom.model.impl.ObjectDefImpl;
import org.hip.kernel.bom.model.impl.PropertyDefImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author: Benno Luthiger
 */
public class ObjectDefImplTest {
	private final static String XML_OBJECT_DEF1 = 
		"<?xml version='1.0' encoding='us-ascii'?>			\n" +
		"<objectDef objectName='TestDomainObject' parent='org.hip.kernel.bom.DomainObject' version='1.0'>			\n" +
		"	<keyDefs>			\n" +
		"		<keyDef>			\n" +
		"			<keyItemDef seq='0' keyPropertyName='MemberID'/>			\n" +
		"		</keyDef>			\n" +
		"	</keyDefs>			\n" +
		"	<propertyDefs>			\n" +
		"		<propertyDef propertyName='MemberID' valueType='Integer' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='TESTMEMBERID'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='FirstName' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SFIRSTNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SPASSWORD'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Mutation' valueType='Timestamp' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='DTMUTATION'/>			\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>			\n" +
		"</objectDef>			";
	private final static String XML_OBJECT_DEF2 = 
		"<?xml version='1.0' encoding='us-ascii'?>			\n" +
		"<objectDef objectName='Test2DomainObject' parent='org.hip.kernel.bom.DomainObject' version='1.0'>			\n" +
		"	<keyDefs>			\n" +
		"		<keyDef>			\n" +
		"			<keyItemDef seq='0' keyPropertyName='MemberID'/>			\n" +
		"		</keyDef>			\n" +
		"	</keyDefs>			\n" +
		"	<propertyDefs>			\n" +
		"		<propertyDef propertyName='MemberID' valueType='Integer' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='TESTMEMBERID'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='FirstName' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SFIRSTNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SPASSWORD'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Mutation' valueType='Timestamp' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='DTMUTATION'/>			\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>			\n" +
		"</objectDef>			";
	

	@Test
	public void testAddPropertyDef() {
		String[] lExpected1 = {"simple", "java.lang.String"};
		String[] lExpected2 = {"lastName", "firstName"};
		Vector<String> lVExpected2 = new Vector<String>(Arrays.asList(lExpected2));
		String lExpected3 = "addresses";

		// We create a simple object def
		Object[][] lConstObjectDef = {
				{"objectName", "Person"}
				,{"parent", "BusinessObject"}		
				,{"version", "1.0"}
				,{"propertyDefs", null}
		};
		ObjectDef lDef = new ObjectDefImpl( lConstObjectDef ) ;
		assertNotNull( lDef ) ;

		// We create a property def and add it to the object def
		Object[][] lConstPropertyDef1 = {
				{"propertyName", "firstName"}
				,{"propertyType", lExpected1[0]}		
				,{"valueType", lExpected1[1]}
		} ;
		PropertyDef lPropertyDef1 = new PropertyDefImpl(lConstPropertyDef1);
		assertNotNull(lPropertyDef1);
		lDef.addPropertyDef(lPropertyDef1);

		// We create a property def and add it to the object def
		Object[][] lConstPropertyDef2 = {
				{"propertyName", "lastName"}
				,{"propertyType", "simple"}		
				,{"valueType", "java.lang.String"}
		} ;
		PropertyDef lPropertyDef2 = new PropertyDefImpl( lConstPropertyDef2 ) ;
		assertNotNull( lPropertyDef2 ) ;
		lDef.addPropertyDef( lPropertyDef2 )	;

		// We create a property def and add it to the object def
		Object[][] lConstPropertyDef3 = {
				{"propertyName", "addresses"}
				,{"propertyType", "objectRef"}		
				,{"valueType", "Collection"}
		} ;
		PropertyDef lPropertyDef3 = new PropertyDefImpl( lConstPropertyDef3 ) ;
		assertNotNull( lPropertyDef3 ) ;
		lDef.addPropertyDef(lPropertyDef3);

		// Now let's test if all was fine
		PropertyDef lPropertyDef = lDef.getPropertyDef( "firstName" ) ;
		assertNotNull( "find property", lPropertyDef ) ;
		try {
			assertEquals("propertyType", 	lExpected1[0], lPropertyDef.get( "propertyType" ));
			assertEquals("valueType", 		lExpected1[1], lPropertyDef.get( "valueType" ));

			VSys.out.println( "propertyType=" + lPropertyDef.get( "propertyType" )) ;
			VSys.out.println( "valueType=" + lPropertyDef.get( "valueType" )) ;
		} 
		catch ( org.hip.kernel.bom.GettingException exc ) {
			fail("find property");
		}

		// Test getProperties
		int i = 0;
		for (PropertyDef lProperty : lDef.getPropertyDefs2(PropertyDefDef.propertyTypeSimple)) {
			assertEquals("getPropertyType " + i, lProperty.getPropertyType(), PropertyDefDef.propertyTypeSimple);
			assertTrue("getName " + i, lVExpected2.contains(lProperty.getName()));			
		}

		for (PropertyDef lProperty : lDef.getPropertyDefs2(PropertyDefDef.propertyTypeObjectRef)) {
			assertEquals("getPropertyType", lProperty.getPropertyType(), PropertyDefDef.propertyTypeObjectRef) ;
			assertEquals("getName", lExpected3, lProperty.getName());			
		}
	}
	
	@Test
	public void testCreation() throws SAXException, VException {

		String[] lExpected = {"version", "keyDefs", "propertyDefs", "parent", "objectName", ObjectDefDef.baseDir};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		ObjectDef lDef = new ObjectDefImpl() ;
		assertNotNull("testCreation", lDef ) ;

		int i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			assertTrue("testCreation 1." + i, lVExpected.contains(lName));
		}

		lDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF1);
		i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			assertTrue("testCreation 2." + i, lVExpected.contains(lName));
		}

		assertEquals("objectName", "TestDomainObject", lDef.get(ObjectDefDef.objectName));
		assertEquals("parent", "org.hip.kernel.bom.DomainObject", lDef.get(ObjectDefDef.parent));
		assertEquals("version", "1.0", lDef.get(ObjectDefDef.version));
	}
	
	@Test
	public void testCreationWithInitialValues() {

		// We create a def
		Object[][] lInitValues = {
				{ "objectName"	, "Person"				}
				,	{ "parent"		, "BusinessObject"		}		
				,	{ "version"		, "1.0" 				}
				, 	{ "propertyDefs", null					}
		} ;

		ObjectDef lDef = new ObjectDefImpl( lInitValues ) ;
		assertNotNull("testCreationWithInitialValues", lDef ) ;

		int i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			try {
				assertNotNull("name " + i, lName);
				VSys.out.println( "name=" + lName + ", value=" + lDef.get( lName ) ) ;			
			} 
			catch ( Exception exc ) {
				VSys.out.println( exc ) ;
			}			
		}
	}
	
	@Test
	public void testEquals() {
		try {
			ObjectDef lObjectDef1 = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF1);
			ObjectDef lObjectDef2 = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF2);
			ObjectDef lObjectDef3 = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF1);

			assertTrue("equals", lObjectDef1.equals(lObjectDef3));
			assertEquals("equal hash code", lObjectDef1.hashCode(), lObjectDef3.hashCode());
			assertTrue("not equals", !lObjectDef1.equals(lObjectDef2));
			assertTrue("not equal hash code", lObjectDef1.hashCode() != lObjectDef2.hashCode());
		}
		catch (org.xml.sax.SAXException exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void testSet() {
		ObjectDef lDef = new ObjectDefImpl() ;
		assertNotNull("testSet", lDef ) ;

		//correct set
		try {
			lDef.set("version", "1.0");
		}
		catch (SettingException exc) {
			fail("set 1");
		}

		//set with invalid value
		try {
			lDef.set("version", new Integer(1));
			fail("set 2");
		}
		catch (SettingException exc) {
		}

		try {
			lDef.set("version!", "1.1");
			fail("set 3");
		}
		catch (SettingException exc) {
		}
	}
	
	@Test
	public void testToString() {
		try {
			ObjectDef lObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF1);
			assertEquals("toString 1", "< org.hip.kernel.bom.model.impl.ObjectDefImpl objectName=\"TestDomainObject\" parent=\"org.hip.kernel.bom.DomainObject\" version=\"1.0\" />", lObjectDef.toString());
		}
		catch (org.xml.sax.SAXException exc) {
			fail(exc.getMessage());
		}
	}
}
