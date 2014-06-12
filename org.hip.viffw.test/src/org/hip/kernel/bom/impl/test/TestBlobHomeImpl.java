package org.hip.kernel.bom.impl.test;

import java.io.File;
import java.sql.SQLException;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PreparedInsertStatement;
import org.hip.kernel.exc.VException;

/**
 * @author Benno Luthiger
 * Created on 23.02.2006
 */
@SuppressWarnings("serial")
public class TestBlobHomeImpl extends DomainObjectHomeImpl implements TestBlobHome {

	private final static String OBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.TestBlobImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TestBlobImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Number' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBlobTest' columnName='BlobTestID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_NAME + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBlobTest' columnName='sName'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_XVALUE + "' valueType='Binary' propertyType='simple'>	" +
		"			<mappingDef tableName='tblBlobTest' columnName='xValue'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * TestBlobHomeImpl constructor.
	 */
	public TestBlobHomeImpl() {
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
	
	public TestBlobImpl ucNew(String inContent, File inFile) throws VException, SQLException {
		PreparedInsertStatement lStatement = new PreparedInsertStatement(this);
		DomainObject lModel = create();
		lModel.set(KEY_NAME, inContent);
		lModel.set(KEY_XVALUE, inFile);

		lStatement.setValues(lModel);
		lStatement.executeUpdate();
		lStatement.commit();
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_ID, getMax(KEY_ID));
		return (TestBlobImpl)findByKey(lKey);
	}

}
