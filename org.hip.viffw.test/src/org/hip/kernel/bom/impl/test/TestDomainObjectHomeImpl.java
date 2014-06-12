package org.hip.kernel.bom.impl.test;

import java.util.Vector;
import java.util.GregorianCalendar;

import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * TestHome used for the unit test DomainObjectHomeImplTest
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestDomainObjectHomeImpl extends DomainObjectHomeImpl {
	public final static String KEY_NAME = "Name";
	
	/** Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	public final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestDomainObjectImpl" ;

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
		"			<keyItemDef seq='0' keyPropertyName='MemberID'/>			\n" +
		"		</keyDef>			\n" +
		"	</keyDefs>			\n" +
		"	<propertyDefs>			\n" +
		"		<propertyDef propertyName='MemberID' valueType='Number' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='TESTMEMBERID'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='" + KEY_NAME + "' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='FirstName' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SFIRSTNAME'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='SPASSWORD'/>			\n" +
		"		</propertyDef>			\n" +
		"		<propertyDef propertyName='Mutation' valueType='Timestamp' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblTestMember' columnName='DTMUTATION'/>			\n" +
		"		</propertyDef>			\n" +
		"	</propertyDefs>			\n" +
		"</objectDef>			";
	
	/**
	 * TestDomainObjectHomeImpl default constructor.
	 */
	public TestDomainObjectHomeImpl() {
		super();
	}
	
	/**
	 * Creates a vector containing objects for testing purpose.
	 */
	protected Vector<Object> createTestObjects() {
		Vector<Object> lTestObjects = new Vector<Object>();
		KeyObject lKey = new KeyObjectImpl();
		KeyObject lKey2 = new KeyObjectImpl();
		try {
			lKey.setValue("Name", "Luthiger");
			lKey.setValue("MemberID", new Integer(12));
			lKey.setValue("Mutation", new java.sql.Timestamp(new GregorianCalendar(2001,11,24).getTime().getTime()));
			lKey2.setValue("Name", "Luthiger");
			lKey2.setValue("MemberID", new Integer(12));
		
			lTestObjects.addElement(createCountAllString());
			lTestObjects.addElement(createCountString(lKey));
			lTestObjects.addElement(createKeyCountColumnList());
			lTestObjects.addElement(createPreparedSelectString(lKey2));
			lTestObjects.addElement(createSelectAllString());
			lTestObjects.addElement(createSelectString(lKey));
		}
		catch (VInvalidNameException exc) {}
		catch (VInvalidValueException exc) {}
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
