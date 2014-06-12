package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class Test2DomainObjectHomeImpl extends DomainObjectHomeImpl {
	public final static String KEY_ID 			= "TestID";
	public final static String KEY_NAME 		= "Name";
	public final static String KEY_FIRSTNAME 	= "Firstname";
	public final static String KEY_STREET 		= "Street";
	public final static String KEY_PLZ 			= "PLZ";
	public final static String KEY_CITY 		= "City";
	public final static String KEY_TEL 			= "Tel";
	public final static String KEY_FAX 			= "Fax";
	public final static String KEY_MAIL 		= "Mail";
	public final static String KEY_SEX 			= "Sex";
	public final static String KEY_AMOUNT 		= "Amount";
	public final static String KEY_DOUBLE 		= "Double";
	public final static String KEY_LANGUAGE 	= "Language";
	public final static String KEY_PASSWORD 	= "Password";
	public final static String KEY_MUTATION 	= "Mutation";
	
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.Test2DomainObjectImpl" ;

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
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='TestID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_NAME + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sName'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_FIRSTNAME + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sFirstname'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_STREET + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sStreet'/>	\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='" + KEY_PLZ + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sPLZ'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_CITY + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sCity'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_TEL + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sTel'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_FAX + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sFax'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_MAIL + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sMail'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_SEX + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='bSex'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_AMOUNT + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='fAmount'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_DOUBLE + "' valueType='Number' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='fDouble'/>	\n" +
		"		</propertyDef>	\n" +		
		"		<propertyDef propertyName='" + KEY_LANGUAGE + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sLanguage'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_PASSWORD + "' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='sPassword'/>	\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='" + KEY_MUTATION + "' valueType='Timestamp' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblTest' columnName='dtMutation'/>	\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";
public Test2DomainObjectHomeImpl() {
	super();
}
protected java.util.Vector<Object> createTestObjects() {
	return null;
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
