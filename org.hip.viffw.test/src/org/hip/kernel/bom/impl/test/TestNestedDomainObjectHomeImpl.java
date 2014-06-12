package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestNestedDomainObjectHomeImpl extends JoinedDomainObjectHomeImpl {
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestNestedDomainObjectImpl" ;
	
	public final static String KEY_REGISTERED = "REGISTERED"; //Registered

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestNestedDomainObject' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>	\n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='ID' alias='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='Description' domainObject='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<columnDef columnName='" + KEY_REGISTERED + "' nestedObject='count' valueType='Number'/>	\n" +
		"	</columnDefs>	\n" +
		"	<joinDef joinType='EQUI_JOIN'>	\n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.TestGroupImpl'/>	\n" +
		"		<objectNested name='count'>	\n" +
		"			<columnDef columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
		"			<columnDef columnName='MemberID' as='" + KEY_REGISTERED + "' modifier='COUNT' domainObject='org.hip.kernel.bom.impl.test.TestParticipantImpl'/>	\n" +
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

	public TestNestedDomainObjectHomeImpl() {
		super();
	}
	public String getObjectClassName() {
		return TESTOBJECT_CLASS_NAME;
	}
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
}
