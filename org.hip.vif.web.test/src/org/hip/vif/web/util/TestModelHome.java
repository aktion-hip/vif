package org.hip.vif.web.util;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

@SuppressWarnings("serial")
public  class TestModelHome extends DomainObjectHomeImpl {
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='us-ascii'?>	\n" +
		"<objectDef objectName='TestModel' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
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
	
	@Override
	public String getObjectClassName() {
		return "org.hip.vif.web.util.TestModel";
	}
	@Override
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
}
