package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Enumeration;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.JoinedObjectDefDef;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.PrimaryKeyDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.JoinedObjectDefGenerator;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinedObjectDefGeneratorTest {
	private final static String XML_OBJECT_DEF1 =
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestJoin1' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='FirstName' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
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
		"		<columnDef columnName='DecimalID' domainObject='org.hip.kernel.bom.impl.test.QuestionImpl'/>	\n" +
		"		<columnDef columnName='Question' domainObject='org.hip.kernel.bom.impl.test.QuestionImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.TestDomainObjectImpl'/>	\n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.LinkQuestionMemberImpl'/>	\n" +
		"		</joinCondition>	\n" +
		"		<joinDef joinType='EQUI_JOIN'>	\n" +
		"			<objectDesc objectClassName='org.hip.kernel.bom.impl.test.LinkQuestionMemberImpl'/>	\n" +
		"			<objectDesc objectClassName='org.hip.kernel.bom.impl.test.QuestionImpl'/>	\n" +
		"			<joinCondition>	\n" +
		"				<columnDef columnName='QuestionID' domainObject='org.hip.kernel.bom.impl.test.LinkQuestionMemberImpl'/>	\n" +
		"				<columnDef columnName='QuestionID' domainObject='org.hip.kernel.bom.impl.test.QuestionImpl'/>	\n" +
		"			</joinCondition>	\n" +
		"		</joinDef>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
	
	private final static String XML_OBJECT_DEF3 =
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestJoin3' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='ID' alias='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='Description' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='Registered' nestedObject='count' valueType='Number'/>	\n" +
		"		<hidden columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<objectNested name='count'>	\n" +
		"			<columnDef columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
		"			<columnDef columnName='MemberID' alias='Registered' modifier='COUNT' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
		"			<resultGrouping modifier='GROUP'>	\n" +
		"				<columnDef columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
		"			</resultGrouping>	\n" +
		"		</objectNested>	\n" +
		"		<joinCondition>	\n" +
		"			<columnDef columnName='ID' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"			<columnDef columnName='GroupID' nestedObject='count'/>	\n" +
		"		</joinCondition>	\n" +
		"	</joinDef>	\n" +
		"</joinedObjectDef>";
		
	
	private void outMapping(ObjectDef inObjectDef) {
		for (String lTableName : inObjectDef.getTableNames2()) {
			System.out.println( "\t\tMapping for table: " + lTableName ) ;
			for (MappingDef lMappingDef : inObjectDef.getMappingDefsForTable2(lTableName)) {
				System.out.println( "\t\t\tcolumnName=" + (String) lMappingDef.getColumnName()) ;				
			}
		}
	}
	
	private void outPrimary(ObjectDef inObjectDef) {
		try {
			VSys.out.println( "objectName=" + (String) inObjectDef.get( "objectName" 	)) ;
			VSys.out.println( "\tparent=" 	+ (String) inObjectDef.get( "parent" 		)) ;
			VSys.out.println( "\tversion=" 	+ (String) inObjectDef.get( "version" 	)) ;
	
			PrimaryKeyDef lPrimKeyDef = (PrimaryKeyDef) inObjectDef.getPrimaryKeyDef() ;
			
			if ( lPrimKeyDef != null ) {
				VSys.out.println( "\tprimaryKey") ;
				for (String lKey : lPrimKeyDef.getKeyNames2()) {
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
	
	private void outPropertySet(PropertySet inSet) {
		try {
			for (String lName : inSet.getNames2()) {
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
	
	private String getColumns(Collection<String> inColumns) {
		StringBuffer outSQL = new StringBuffer();
	
		boolean lFirst = true;
		for (String lColumn : inColumns) {
			if (!lFirst) {
				outSQL.append(", ");
			}
			lFirst = false;
			outSQL.append(lColumn);
		}
		return new String(outSQL);
	}
		
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	@Test
	public void testGenerate() throws Exception {
		// first join
		JoinedObjectDef lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF1);
		
		ObjectDef lSimpleObjectDef = lJoinedObjectDef.getDomainObjectDef();
		assertEquals("testGenerate 1.1", "TestJoin1", (String)lSimpleObjectDef.get("objectName"));
		assertEquals("testGenerate 1.2", "org.hip.kernel.bom.ReadOnlyDomainObject", (String)lSimpleObjectDef.get("parent"));		
		assertEquals("testGenerate 1.3", "1.0", (String)lSimpleObjectDef.get("version"));
		assertEquals("columns 1", "tblTestMember.TESTMEMBERID, tblTestMember.SNAME, tblTestMember.SFIRSTNAME, tblTestMember.DTMUTATION", getColumns((Collection)lJoinedObjectDef.get(JoinedObjectDefDef.columnDefs)));
		
		outPrimary(lSimpleObjectDef);
		outPropertySet((PropertySet)lSimpleObjectDef.get( "propertyDefs" ));
		outMapping(lSimpleObjectDef);
		
		Enumeration lTableNames = lSimpleObjectDef.getTableNames();
		if (lTableNames.hasMoreElements()) 
			assertEquals("testGenerate 1.4", "tblTestMember", (String)lTableNames.nextElement());
		
		// second join
		lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF2);
		
		lSimpleObjectDef = lJoinedObjectDef.getDomainObjectDef();
		assertEquals("testGenerate 2.1", "TestJoin2", (String)lSimpleObjectDef.get("objectName"));
		assertEquals("testGenerate 2.2", "org.hip.kernel.bom.ReadOnlyDomainObject", (String)lSimpleObjectDef.get("parent"));		
		assertEquals("testGenerate 2.3", "1.0", (String)lSimpleObjectDef.get("version"));
		assertEquals("columns 2", "tblTestMember.TESTMEMBERID, tblTestMember.SNAME, tblQuestion.SQUESTIONID, tblQuestion.SQUESTION", getColumns((Collection<String>)lJoinedObjectDef.get(JoinedObjectDefDef.columnDefs)));
		
		outPrimary(lSimpleObjectDef);
		outPropertySet((PropertySet)lSimpleObjectDef.get( "propertyDefs" ));
		outMapping(lSimpleObjectDef);
		
		lTableNames = lSimpleObjectDef.getTableNames();
		if (lTableNames.hasMoreElements()) 
			assertEquals("testGenerate 2.4", "tblQuestion", (String)lTableNames.nextElement());
		
		//third join with nested tables
		lJoinedObjectDef = JoinedObjectDefGenerator.getSingleton().createJoinedObjectDef(XML_OBJECT_DEF3);
		assertEquals("columns 3", "tblGroup.SNAME, tblGroup.GROUPID, tblGroup.SDESCRIPTION, count.Registered", getColumns((Collection)lJoinedObjectDef.get(JoinedObjectDefDef.columnDefs)));
		assertEquals("hidden field mapping", "tblParticipant.GROUPID", lJoinedObjectDef.getHidden("GroupID"));
	}
}
