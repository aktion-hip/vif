package org.hip.kernel.bom.impl.test;

import java.util.Vector;

import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestJoinedDomainObjectHomeImpl extends JoinedDomainObjectHomeImpl {
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestReadOnlyDomainObjectImpl" ;

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
public TestJoinedDomainObjectHomeImpl() {
	super();
}
protected Vector<Object> createTestObjects() {
	KeyObject lKey = new KeyObjectImpl();
	try {
		lKey.setValue("MemberID", new Integer(12));
		lKey.setValue("Name", "Luthiger");
//		lKey.setValue("Mutation", new java.sql.Timestamp(new GregorianCalendar(2001,11,24,14,30).getTime().getTime()));
	}
	catch (VInvalidNameException exc) {}
	catch (VInvalidValueException exc) {}

	Vector<Object> lTestObjects = new Vector<Object>();

	try {
		lTestObjects.addElement(createCountAllString());
		lTestObjects.addElement(createCountString(lKey));
		lTestObjects.addElement(createSelectAllString());
		lTestObjects.addElement(createSelectString(lKey));
	}
	catch (BOMException exc) {}

	return lTestObjects;
}
public String getObjectClassName() {
	return TESTOBJECT_CLASS_NAME;
}
protected String getObjectDefString() {
	return XML_OBJECT_DEF;
}
}
