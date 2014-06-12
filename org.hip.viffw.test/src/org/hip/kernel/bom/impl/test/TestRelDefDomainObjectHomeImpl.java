package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

/**
 * DomainObjectHome with RelationshipDef for testing purpose.
 *
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestRelDefDomainObjectHomeImpl extends DomainObjectHomeImpl {
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestRelDefDomainObjectImpl" ;

	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.

	private final static String XML_OBJECT_DEF =
		"<?xml version='1.0' encoding='us-ascii'?>			\n" +
		"<objectDef objectName='TestDomainObject' parent='org.hip.kernel.bom.DomainObject' version='1.0'>			\n" +
		"	<keyDefs>			\n" +
		"		<keyDef>			\n" +
		"			<keyItemDef seq='0' keyPropertyName='TestID'/>			\n" +
		"		</keyDef>			\n" +
		"	</keyDefs>			\n" +
		"	<propertyDefs>			\n" +
		"		<propertyDef propertyName='TestID' valueType='Number' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='TESTMEMBERID'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='FirstName' valueType='String' propertyType='objectRef'>			\n" +
		"			<relationshipDef homeName='org.hip.kernel.bom.impl.test.Test2DomainObjectHomeImpl' keyDefName='TestID'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SPASSWORD'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Mutation' valueType='Timestamp' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='DTMUTATION'/>			\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>			\n" +
		"</objectDef>			";

	public TestRelDefDomainObjectHomeImpl() {
		super();
	}
	/**
	 * getObjectClassName method comment.
	 */
	public String getObjectClassName() {
		return TESTOBJECT_CLASS_NAME;
	}
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
}
