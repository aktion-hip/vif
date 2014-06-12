package org.hip.kernel.bom.impl.test;

import java.sql.SQLException;
import java.util.Vector;

import junit.framework.Assert;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.PlacefillerCollection;
import org.hip.kernel.exc.VException;

/**
 * @author Benno Luthiger
 * Created on Nov 8, 2004
 */
@SuppressWarnings("serial")
public class TestJoinWithPlaceholderHome extends JoinedDomainObjectHomeImpl {
	private KeyObject keyForTest;
	public final static String NESTED_ALIAS = "Admins";

	private final static String OBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestJoinWithPlaceholder";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<joinedObjectDef objectName='TestJoinWithPlaceholder' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	" +
		"	<columnDefs>	" +
		"		<columnDef columnName='" + Test2DomainObjectHomeImpl.KEY_ID + "' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	  \n" +
		"		<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n" +
		"		<columnDef columnName='Firstname' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	 \n" +
		"		<columnDef columnName='Mutation' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n" +
		"	</columnDefs>	" +
		"	<joinDef joinType='LEFT_JOIN'>	" +
		"		<objectDesc objectClassName='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	" +
		"		<objectPlaceholder name='" + NESTED_ALIAS + "' />	\n" +
		"		<joinCondition>	" +
		"				<columnDef columnName='Name' domainObject='org.hip.kernel.bom.impl.test.Test2DomainObjectImpl'/>	\n" +
		"				<columnDef columnName='Name' nestedObject='" + NESTED_ALIAS + "' domainObject='" + TestDomainObjectHomeImpl.TESTOBJECT_CLASS_NAME + "'/>	\n" +
		"		</joinCondition>	" +
		"	</joinDef>	" +
		"</joinedObjectDef>";

	/**
	 * TestJoinWithPlaceholderHome constructor.
	 */
	public TestJoinWithPlaceholderHome() {
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

	/**
	 * Returns test objects of this class.
	 *
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#createTestObjects() *
	 * @return java.util.Vector
	 */
	protected Vector<Object> createTestObjects() {
		Vector<Object> outTest = new Vector<Object>();
		try {
			outTest.add(createSelectString(keyForTest));
		}
		catch (VException exc) {
			Assert.fail(exc.getMessage());
		}
		return outTest;
	}

	public QueryResult select(KeyObject inKey, PlacefillerCollection inPlacefillers) throws SQLException, BOMException {
		try {
			keyForTest = inKey;
			super.select(inKey, inPlacefillers);
		}
		catch (Exception exc) {
			//intentionally left empty
		}
		return null;
	}
	
}
