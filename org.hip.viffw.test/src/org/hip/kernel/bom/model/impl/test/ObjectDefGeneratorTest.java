package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Enumeration;
import java.util.Iterator;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PrimaryKeyDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class ObjectDefGeneratorTest {
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
	

	@SuppressWarnings("deprecation")
	private void outMapping(ObjectDef inObjectDef) {
		for (Enumeration<?> lTableNames = inObjectDef.getTableNames(); lTableNames.hasMoreElements();) {
			String lTableName = (String) lTableNames.nextElement();
			assertEquals("testGenerate 5", "tblTestMember", lTableName);
			System.out.println( "\t\tMapping for table: " + lTableName ) ;
			for ( Enumeration<?> lMappingDefs = inObjectDef.getMappingDefsForTable( lTableName ); lMappingDefs.hasMoreElements() ; ) {
				MappingDef lMappingDef = (MappingDef) lMappingDefs.nextElement() ;
				System.out.println( "\t\t\tcolumnName=" + (String) lMappingDef.getColumnName()) ;
			}				
		}	
	}
	
	@SuppressWarnings("deprecation")
	private void outPrimary(ObjectDef inObjectDef) {
		try {
			VSys.out.println( "objectName=" + (String) inObjectDef.get( "objectName" 	)) ;
			VSys.out.println( "\tparent=" 	+ (String) inObjectDef.get( "parent" 		)) ;
			VSys.out.println( "\tversion=" 	+ (String) inObjectDef.get( "version" 	)) ;
	
			PrimaryKeyDef lPrimKeyDef = (PrimaryKeyDef) inObjectDef.getPrimaryKeyDef() ;
			
			if ( lPrimKeyDef != null ) {
				VSys.out.println( "\tprimaryKey") ;
				for (Iterator<?> lKeyNames = lPrimKeyDef.getKeyNames(); lKeyNames.hasNext(); ) {
					String lKey = (String)lKeyNames.next();
					assertEquals("testGenerate 4", "MemberID", lKey);
					VSys.out.println( "\t\t" + lKey) ; 
				}	
			}
			else {
				VSys.out.println( "\tprimaryKey=NO PRIMARY KEY DEFINED" ) ; 			
			}
		}
		catch (org.hip.kernel.bom.GettingException exc) {
			fail(exc.toString());
		}
	}
	
	@SuppressWarnings("deprecation")
	private void outPropertySet(PropertySet inSet) {
		try {
			for (Iterator<?> lNames = inSet.getNames(); lNames.hasNext(); ) {
				String lName = (String) lNames.next();
				PropertyDef lPropertyDef = (PropertyDef) inSet.getValue( lName )  ;
				System.out.println( "\tpropertyName=" + (String) lPropertyDef.get( "propertyName" )) ;
				System.out.println( "\t\tpropertyType=" + (String) lPropertyDef.get( "propertyType" )) ;
				System.out.println( "\t\tvalueType=" + (String) lPropertyDef.get( "valueType" )) ;
				
				MappingDef lMapDef = lPropertyDef.getMappingDef()	;
				if ( lMapDef != null ) {
					System.out.println( "\t\tMapping:" ) ;
					System.out.println( "\t\t\ttableName=" + (String) lMapDef.getTableName()) ;
					System.out.println( "\t\t\tcolumnName=" + (String) lMapDef.getColumnName()) ;
				}	
			}
		}
		catch (org.hip.kernel.bom.GettingException exc) {
			fail(exc.toString());
		}
		catch (org.hip.kernel.util.VInvalidNameException exc) {
			fail(exc.toString());
		}
	}

	@Test
	public void testGenerate() {
		try {
			ObjectDef lObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF);
	
			assertEquals("testGenerate 1", "TestDomainObject", (String)lObjectDef.get(ObjectDefDef.objectName));
			assertEquals("testGenerate 2", "org.hip.kernel.bom.DomainObject", (String)lObjectDef.get(ObjectDefDef.parent));		
			assertEquals("testGenerate 3", "1.0", (String)lObjectDef.get(ObjectDefDef.version));
	
			outPrimary(lObjectDef);
			outPropertySet((PropertySet)lObjectDef.get(ObjectDefDef.propertyDefs));
			outMapping(lObjectDef);
		}
		catch (org.xml.sax.SAXException exc) {
			fail("testGenerate " + exc.toString());
		}
		catch (org.hip.kernel.bom.GettingException exc) {
			fail("testGenerate " + exc.toString());
		}
	}
}
