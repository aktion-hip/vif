package org.hip.kernel.bom.impl.test;

import java.util.Vector;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

/**
 * Test home for <code>DefaultDBAdapterSimpleTest</code>.
 *
 * @see DefaultDBAdapterSimpleTest#testCreateUpdateString2()
 */
@SuppressWarnings("serial")
public class TestShortHome extends DomainObjectHomeImpl {
	public final static String KEY_SHORTID = "ShortID";
	public final static String KEY_TESTID = "TestID";
	public final static String KEY_TYPE = "Type";

	private final static String OBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestShort";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TestShort' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_SHORTID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_TESTID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_SHORTID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTestShort' columnName='ShortID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TESTID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTestShort' columnName='TestID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TYPE + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTestShort' columnName='nType'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	protected Vector<Object> createTestObjects() {
		return null;
	}

}
