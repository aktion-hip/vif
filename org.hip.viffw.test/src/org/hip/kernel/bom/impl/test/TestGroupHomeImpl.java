package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;

/**
 * Home to manage the test object used for the unit test 
 * JoinedObjectDefGeneratorTest (and others)
 * 
 * @author Benno Luthiger
 * Created on Dec 21, 2003
 */
@SuppressWarnings("serial")
public class TestGroupHomeImpl extends DomainObjectHomeImpl	{
	public final static String KEY_ID = "ID";
	public final static String KEY_NAME = "Name";
	public final static String KEY_DESCRIPTION = "Description";
	public final static String KEY_REVIEWER = "Reviewer";
	public final static String KEY_GUESTDEPTH = "GuestDepth";
	public final static String KEY_MINGROUPSIZE = "MinGroupSize";
	public final static String KEY_STATE = "State";

	private final static String OBJECT_CLASS_NAME =	"org.hip.kernel.bom.impl.test.TestGroupImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TestGroupImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='GroupID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_NAME + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='sName'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_DESCRIPTION + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='sDescription'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_REVIEWER + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='nReviewer'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_GUESTDEPTH + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='nGuestDepth'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_MINGROUPSIZE + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='nMinGroupSize'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_STATE + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblGroup' columnName='nState'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * TestGroupHomeImpl constructor.
	 */
	public TestGroupHomeImpl() {
		super();
	}
	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}
	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

}
