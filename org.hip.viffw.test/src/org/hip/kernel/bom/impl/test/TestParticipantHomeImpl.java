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
public class TestParticipantHomeImpl extends DomainObjectHomeImpl {
	public final static String KEY_MEMBERID = "MemberID";
	public final static String KEY_GROUPID = "GroupID";
	public final static String KEY_SUSPENDFROM = "SuspendFrom";
	public final static String KEY_SUSPENDTO = "SuspendTo";
	private final static String OBJECT_CLASS_NAME =	"org.hip.kernel.bom.impl.test.TestParticipantImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TestParticipantImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_MEMBERID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_GROUPID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_MEMBERID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblParticipant' columnName='MemberID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_GROUPID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblParticipant' columnName='GroupID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_SUSPENDFROM + "' valueType='Timestamp' propertyType='simple'>	" +
		"			<mappingDef tableName='tblParticipant' columnName='dtSuspendFrom'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_SUSPENDTO + "' valueType='Timestamp' propertyType='simple'>	" +
		"			<mappingDef tableName='tblParticipant' columnName='dtSuspendTo'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * TestParticipantHomeImpl constructor.
	 */
	public TestParticipantHomeImpl() {
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
