package org.hip.kernel.bom.impl.test;

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class Test2JoinedDomainObjectHomeImpl extends JoinedDomainObjectHomeImpl {
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.Test2JoinedDomainObjectImpl" ;

	/* 	The current version of the domain object framework provides
		no support for externelized metadata. We build them up with
		hard coded definition strings.
	*/
	//	CAUTION:	The current version of the lightweight DomainObject
	//				framework makes only a limited check of the correctness
	//				of the definition string. Make extensive basic test to
	//				ensure that the definition works correct.

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<joinedObjectDef objectName='TestJoin2' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<columnDefs>																							 \n" +
		"		<columnDef columnName='TestID' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	  \n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Firstname' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	 \n" +
		"		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n" +
		"		<columnDef columnName='GroupID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	\n" +
		"	</columnDefs>																							\n" +
		"	<joinDef joinType='EQUI_JOIN'>														 \n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>					  \n" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>					\n" +
		"		<joinCondition>																						 \n" +
		"			<columnDef columnName='TestID' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	 \n" +
		"			<columnDef columnName='MemberID' domainObject='org.hip.kernel.bom.impl.test.LinkGroupMemberImpl'/>	 \n" +
		"		</joinCondition>																						\n" +
		"	</joinDef>																								   \n" +
		"</joinedObjectDef>";

	public Test2JoinedDomainObjectHomeImpl() {
		super();
	}
	public String getObjectClassName() {
		return TESTOBJECT_CLASS_NAME;
	}
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
}
