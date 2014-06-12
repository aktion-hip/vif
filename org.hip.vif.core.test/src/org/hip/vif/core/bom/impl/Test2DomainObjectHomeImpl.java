package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class Test2DomainObjectHomeImpl extends DomainObjectHomeImpl {
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.vif.bom.impl.Test2DomainObjectImpl" ;

	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='us-ascii'?>	\n" +
		"<objectDef objectName='TestDomainObject' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='TestID'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='TestID' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='TestID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sName'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Firstname' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sFirstname'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Street' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sStreet'/>	\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='PLZ' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sPLZ'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='City' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sCity'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Tel' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sTel'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Fax' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sFax'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Mail' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sMail'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Sex' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='bSex'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Amount' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='fAmount'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Double' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='fDouble'/>	\n" +
		"		</propertyDef>	\n" +		
		"		<propertyDef propertyName='Language' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sLanguage'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sPassword'/>	\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Mutation' valueType='Timestamp' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='dtMutation'/>	\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";

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
