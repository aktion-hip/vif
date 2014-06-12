package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;

import org.hip.kernel.bom.impl.DBAdapterType;
import org.hip.vif.core.DataHouseKeeper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 16.04.2007
 */
public class ExtMemberHomeImplTest {

	@BeforeClass
	public static void init() {
		DataHouseKeeper.getInstance();
	}
	
	@Test
	public void testGetObjectDefString() {
		String lExpected = "<?xml version='1.0' encoding='ISO-8859-1'?><objectDef objectName='Member' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	<keyDefs>		<keyDef>			<keyItemDef seq='0' keyPropertyName='ID'/>		</keyDef>	</keyDefs>	<propertyDefs>		<propertyDef propertyName='ID' valueType='Number' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='MEMBERID'/>		</propertyDef>		<propertyDef propertyName='UserID' valueType='String' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='SUSERID'/>		</propertyDef>		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='SNAME'/>		</propertyDef>		<propertyDef propertyName='Firstname' valueType='String' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='SFIRSTNAME'/>		</propertyDef>		<propertyDef propertyName='Mail' valueType='String' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='SMAIL'/>		</propertyDef>		<propertyDef propertyName='Sex' valueType='Number' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='BSEX'/>		</propertyDef>		<propertyDef propertyName='Password' valueType='String' propertyType='simple'>			<mappingDef tableName='tblMember' columnName='SPASSWORD'/>		</propertyDef>	</propertyDefs></objectDef>";
		
		ExtMemberHomeImplSub lHome = new ExtMemberHomeImplSub();
		assertEquals("external object def", lExpected, lHome.getObjectDefString());
	}
	
	
	@SuppressWarnings("serial")
	private class ExtMemberHomeImplSub extends ExtMemberHomeImpl {
		public String getObjectDefString() {
			return super.getObjectDefString();
		}
		@Override
		protected DBAdapterType retrieveDBAdapterType() {
			return DBAdapterType.DB_TYPE_MYSQL;
		}
	}

}
