package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.impl.HelperTableDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author: Benno Luthiger
 */
public class HelperTableDefTest {
	private static ObjectDef objectDef = null;
	
	private final static String XML_OBJECT_DEF = 
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
	
	@BeforeClass
	public static void init() throws SAXException {
		objectDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF);		
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetMappingDefs() {
		String lTableNameExpected = "tblTestMember";
		String[] lExpected = {"TESTMEMBERID", "SNAME", "SFIRSTNAME", "SPASSWORD", "DTMUTATION"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		
		assertNotNull(objectDef);
		Hashtable<String, HelperTableDef> lHelperTableDefs = HelperTableDef.createTableDefs(objectDef);
		for (Enumeration<HelperTableDef> lHelperTables = lHelperTableDefs.elements(); lHelperTables.hasMoreElements();) {
			HelperTableDef lHelperTableDef = (HelperTableDef)lHelperTables.nextElement();
			assertEquals("tableName 0", lTableNameExpected, lHelperTableDef.getTableName());
			int i = 0;
			for (Enumeration<?> lMappingDefs = lHelperTableDef.getMappingDefs(); lMappingDefs.hasMoreElements();) {
				i++;
				MappingDef lMappingDef = (MappingDef)lMappingDefs.nextElement();
				assertEquals("tableName " + i, lTableNameExpected, lMappingDef.getTableName());
				assertTrue("getMappingDefs " + i, lVExpected.contains(lMappingDef.getColumnName()));
			}
		}
	}
	
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Hashtable<String, HelperTableDef> lHelperTableDefs = HelperTableDef.createTableDefs(objectDef);
		
		ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
		ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
		lObjectOut.writeObject(lHelperTableDefs);
		byte[] lSerialized = lBytesOut.toByteArray();
		lObjectOut.close();
		lBytesOut.close();
		lHelperTableDefs = null;
		
		ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
		ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
		Hashtable<?, ?> lRetrieved = (Hashtable<?, ?>)lObjectIn.readObject();
		lObjectIn.close();
		lBytesIn.close();

		String lTableNameExpected = "tblTestMember";
		for (Enumeration<?> lHelperTables = lRetrieved.elements(); lHelperTables.hasMoreElements();) {
			HelperTableDef lHelperTableDef = (HelperTableDef)lHelperTables.nextElement();
			assertEquals("tableName", lTableNameExpected, lHelperTableDef.getTableName());
		}
	}
	
}
