package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.model.ColumnDefDef;
import org.hip.kernel.bom.model.HiddenDef;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.JoinedObjectDefDef;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.JoinedObjectDefGenerator;
import org.hip.kernel.bom.model.impl.JoinedObjectDefImpl;
import org.hip.kernel.util.DefaultNameValueList;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinedObjectDefImplTest {
	// We create a simple object def
	private Object[][] constObjectDef = {
			{JoinedObjectDefDef.objectName	, "Person"				}
		,	{JoinedObjectDefDef.parent		, "BusinessObject"		}		
		,	{JoinedObjectDefDef.version		, "1.0" 				}
		, 	{JoinedObjectDefDef.columnDefs	, null					}
		, 	{JoinedObjectDefDef.joinDef		, null					}
		};

	private final static String XML_OBJECT_DEF1 = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestJoin1' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='FirstName' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN' operatorType='eq'>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
	private final static String XML_OBJECT_DEF2 = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestJoin2' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='FirstName' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<hidden columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN' operatorType='eq'>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
	
	@Test
	public void testAddColumnDef() throws Exception {
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		assertNotNull(lDef);
	
		// We create a column def and add it to the object def
		DefaultNameValueList lList1 = new DefaultNameValueList();
		lList1.setValue(ColumnDefDef.columnName, "MemberID");
		lList1.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		
		assertNotNull(lList1);
		lDef.addColumnDef(lList1);
		
		// We create a column def and add it to the object def
		DefaultNameValueList lList2 = new DefaultNameValueList();
		lList2.setValue(ColumnDefDef.columnName, "FirstName");
		lList2.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		
		assertNotNull(lList2);
		lDef.addColumnDef(lList2);
		
		// We create a column def and add it to the object def
		DefaultNameValueList lList3 = new DefaultNameValueList();
		lList3.setValue(ColumnDefDef.columnName, "Mutation");
		lList3.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		
		assertNotNull(lList3);
		lDef.addColumnDef(lList3);
		
		assertEquals("columnName 1", "tblTestMember.TESTMEMBERID", lDef.getColumnName(lList1));
		assertEquals("columnName 2", "tblTestMember.SFIRSTNAME", lDef.getColumnName(lList2));
		assertEquals("columnName 3", "tblTestMember.DTMUTATION", lDef.getColumnName(lList3));
	}
	
	// <columnDef columnName='FirstName' nestedObject='SelectNested' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	@Test
	public void testAddColumnDef2() throws Exception {
		String lExpectedSQL = "SelectNested.SFIRSTNAME";
		String lExpectedColumn = "FirstName";
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn);
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.nestedObject, "SelectNested");
		
		lDef.addColumnDef(lList);
		
		assertEquals("nested with domain object", lExpectedSQL, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		MappingDef lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn).getMappingDef();
		assertEquals("mapped table name", "tblTestMember", lMapping.getTableName());
		assertEquals("mapped column name", "SFIRSTNAME", lMapping.getColumnName());
	}
	
	// <columnDef columnName='FirstName' nestedObject='SelectNested' valueType='String'/>
	@Test
	public void testAddColumnDef3() throws Exception {
		String lExpectedSQL = "SelectNested.FIRSTNAME";
		String lExpectedColumn = "FIRSTNAME";
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn);
		lList.setValue(ColumnDefDef.nestedObject, "SelectNested");
		lList.setValue(ColumnDefDef.valueType, "String");
		
		lDef.addColumnDef(lList);
		
		assertEquals("SQL of nested without domain object", lExpectedSQL, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		MappingDef lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn).getMappingDef();
		assertEquals("mapped table name", "SelectNested", lMapping.getTableName());
		assertEquals("mapped column name", lExpectedColumn, lMapping.getColumnName());
	}
	
	// <columnDef columnName='FirstName' as='AsSomethingDifferent' nestedObject='SelectNested' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	// <columnDef columnName='FirstName' as='AsSomethingDifferent' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	@Test
	public void testAddColumnDef4() throws Exception {
		String lExpectedSQL1 = "SelectNested.SFIRSTNAME AS ASSOMETHINGDIFFERENT";
		String lExpectedSQL2 = "tblTestMember.SFIRSTNAME AS ASSOMETHINGDIFFERENT";
		String lExpectedColumn = "ASSOMETHINGDIFFERENT";
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, "FirstName");
		lList.setValue(ColumnDefDef.nestedObject, "SelectNested");
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.as, lExpectedColumn);
		
		lDef.addColumnDef(lList);
		
		assertEquals("SQL of nested alterted", lExpectedSQL1, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		MappingDef lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn).getMappingDef();
		assertEquals("mapped table name", "", lMapping.getTableName());
		assertEquals("mapped column name", lExpectedColumn, lMapping.getColumnName());
		
		lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, "FirstName");
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.as, lExpectedColumn);
		
		lDef = new JoinedObjectDefImpl(constObjectDef);
		lDef.addColumnDef(lList);
		
		assertEquals("SQL of alterted", lExpectedSQL2, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn).getMappingDef();
		assertEquals("mapped table name", "", lMapping.getTableName());
		assertEquals("mapped column name", lExpectedColumn, lMapping.getColumnName());			
	}	

	// <columnDef columnName='FirstName' alias='AliasSomethingDifferent' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	@Test
	public void testAddColumnDef5() throws Exception {
		String lExpectedSQL = "tblTestMember.SFIRSTNAME";
		String lExpectedColumn = "AliasSomethingDifferent";
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, "FirstName");
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.alias, lExpectedColumn);
		
		lDef.addColumnDef(lList);
		
		assertEquals("SQL of nested alterted", lExpectedSQL, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		MappingDef lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn).getMappingDef();
		assertEquals("mapped table name", "tblTestMember", lMapping.getTableName());
		assertEquals("mapped column name", "SFIRSTNAME", lMapping.getColumnName());
	}	
	
	// <columnDef columnName='Mutation' template='NOW() > {0}' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	// <columnDef columnName='Mutation' template='NOW() > {0}' as='isActive' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	// <columnDef columnName='hasSuspended' template='testSuspended1 AND testSuspended2' as='hasSuspended' nestedObject='SelectNested' valueType='Number'/>
	// <columnDef columnName='Mutation' template='NOW() > {0}' as='isActive' valueType='Number' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>
	@Test
	public void testAddColumnDef6() throws Exception {
		String lExpectedSQL1 = "(NOW() > tblTestMember.DTMUTATION)";
		String lExpectedSQL2 = "(NOW() > tblTestMember.DTMUTATION) AS ISACTIVE";
		String lExpectedSQL3 = "(testSuspended1 AND testSuspended2) AS HASSUSPENDED";

		String lExpectedColumn1 = "Mutation";
		String lExpectedColumn2 = "ISACTIVE";
		String lExpectedColumn3 = "HASSUSPENDED";
		
		JoinedObjectDef lDef = new JoinedObjectDefImpl(constObjectDef);
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn1);
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.template, "NOW() > {0}");
		
		lDef.addColumnDef(lList);
		
		assertEquals("SQL with template", lExpectedSQL1, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		MappingDef lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn1).getMappingDef();
		assertEquals("mapped table name 1", "tblTestMember", lMapping.getTableName());
		assertEquals("mapped column name 1", "DTMUTATION", lMapping.getColumnName());
		//
		lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn1);
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.template, "NOW() > {0}");
		lList.setValue(ColumnDefDef.as, lExpectedColumn2);
		
		lDef = new JoinedObjectDefImpl(constObjectDef);
		lDef.addColumnDef(lList);
		
		assertEquals("SQL with template altered", lExpectedSQL2, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn2).getMappingDef();
		assertEquals("mapped table name 2", "", lMapping.getTableName());
		assertEquals("mapped column name 2", lExpectedColumn2, lMapping.getColumnName());			
		//
		lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn3);
		lList.setValue(ColumnDefDef.template, "testSuspended1 AND testSuspended2");
		lList.setValue(ColumnDefDef.as, lExpectedColumn3);
		lList.setValue(ColumnDefDef.nestedObject, "SelectNested");
		lList.setValue(ColumnDefDef.valueType, "Number");			
		
		lDef = new JoinedObjectDefImpl(constObjectDef);
		lDef.addColumnDef(lList);
		
		assertEquals("SQL with template and virtual column", lExpectedSQL3, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		lMapping = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn3).getMappingDef();
		assertEquals("mapped table name 3", "", lMapping.getTableName());
		assertEquals("mapped column name 3", lExpectedColumn3, lMapping.getColumnName());			
		//
		lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, lExpectedColumn1);
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		lList.setValue(ColumnDefDef.template, "NOW() > {0}");
		lList.setValue(ColumnDefDef.as, lExpectedColumn2);
		lList.setValue(ColumnDefDef.valueType, "Number");			
		
		lDef = new JoinedObjectDefImpl(constObjectDef);
		lDef.addColumnDef(lList);
		
		assertEquals("SQL with template and value type", lExpectedSQL2, ((Collection<?>)lDef.get(JoinedObjectDefDef.columnDefs)).iterator().next());
		
		PropertyDef lPropertyDef = lDef.getDomainObjectDef().getPropertyDef(lExpectedColumn2);
		lMapping = lPropertyDef.getMappingDef();
		assertEquals("mapped table name 4", "", lMapping.getTableName());
		assertEquals("mapped column name 4", lExpectedColumn2, lMapping.getColumnName());
		assertEquals("value type", "Number", lPropertyDef.getValueType());
	}	
	
	@Test
	public void testCreation() throws Exception {
		String[] lExpected = {"version", "columnDefs", "joinDef", "parent", "objectName"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		JoinedObjectDef lDef = new JoinedObjectDefImpl() ;
		assertNotNull("testCreation", lDef ) ;
	
		int i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			assertTrue("testCreation 1." + i, lVExpected.contains(lName));			
		}
	
		lDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			assertTrue("testCreation 2." + i, lVExpected.contains(lName));
		}
		
		assertEquals("objectName", "TestJoin1", lDef.get(JoinedObjectDefDef.objectName));
		assertEquals("parent", "org.hip.kernel.bom.ReadOnlyDomainObject", lDef.get(JoinedObjectDefDef.parent));
		assertEquals("version", "1.0", lDef.get(JoinedObjectDefDef.version));
	}
	
	@Test
	public void testEquals() throws Exception {
		JoinedObjectDef lObjectDef1 = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		JoinedObjectDef lObjectDef2 = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF2);
		JoinedObjectDef lObjectDef3 = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		
		assertTrue("equals", lObjectDef1.equals(lObjectDef3));
		assertEquals("equal hash code", lObjectDef1.hashCode(), lObjectDef3.hashCode());
		assertTrue("not equals", !lObjectDef1.equals(lObjectDef2));
		assertTrue("not equal hash code", lObjectDef1.hashCode() != lObjectDef2.hashCode());
	}
	
	@Test
	public void testGetColumnName() throws Exception {
		JoinedObjectDef lObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(ColumnDefDef.columnName, "MemberID");
		lList.setValue(ColumnDefDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		
		assertEquals("getColumnName", "tblTestMember.TESTMEMBERID", lObjectDef.getColumnName(lList));
	}
	
	@Test
	public void testGetTableName() throws Exception {
		JoinedObjectDef lObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		
		assertEquals("getTableName 1", "tblTestMember", lObjectDef.getTableName("org.hip.kernel.bom.impl.test.TestDomainObjectImpl"));
		assertEquals("getTableName 2", "tblLinkGroupMember", lObjectDef.getTableName("org.hip.kernel.bom.impl.test.LinkGroupMemberImpl"));
	}
	
	@Test
	public void testSet() throws Exception {
		JoinedObjectDef lDef = new JoinedObjectDefImpl() ;
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
	
		//set with invalid value
		try {
			lDef.set("version!", "1.1");
			fail("set 3");
		}
		catch (SettingException exc) {
		}
	
		try {
			lDef.set(JoinedObjectDefDef.columnDefs, new Vector<String>());
		}
		catch (SettingException exc) {
			fail("set 4");
		}
	
		try {
			lDef.set(JoinedObjectDefDef.joinDef, new org.hip.kernel.bom.model.impl.JoinDefImpl());
		}
		catch (SettingException exc) {
			fail("set 5");
		}
	}
	
	@Test
	public void testHidden() throws Exception {
		JoinedObjectDef lObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF2);
		assertEquals("hidden", "tblLinkGroupMember.GROUPID", lObjectDef.getHidden("GroupID"));
	}
	
	@Test
	public void testAddHidden() throws Exception {
		String lHidden = "MemberID";
		DefaultNameValueList lList = new DefaultNameValueList();
		lList.setValue(HiddenDef.columnName, lHidden);
		lList.setValue(HiddenDef.domainObject, "org.hip.kernel.bom.impl.test.TestDomainObjectImpl");
		
		JoinedObjectDef lObjectDef = new JoinedObjectDefImpl();
		lObjectDef.addHidden(lList);
		assertEquals("hidden", "tblTestMember.TESTMEMBERID", lObjectDef.getHidden(lHidden));
	}
	
	@Test
	public void testToString() throws Exception {
		JoinedObjectDef lObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		assertEquals("toString 1", "< org.hip.kernel.bom.model.impl.JoinedObjectDefImpl objectName=\"TestJoin1\" parent=\"org.hip.kernel.bom.ReadOnlyDomainObject\" version=\"1.0\" />", lObjectDef.toString());
	}
}
