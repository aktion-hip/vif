package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.impl.HelperTableDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/** @author: Benno Luthiger */
public class HelperTableDefTest {
    private static ObjectDef objectDef = null;

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='us-ascii'?>			\n"
                    +
                    "<objectDef objectName='TestDomainObject' parent='org.hip.kernel.bom.DomainObject' version='1.0'>			\n"
                    +
                    "	<keyDefs>			\n" +
                    "		<keyDef>			\n" +
                    "			<keyItemDef seq='0' keyPropertyName='MemberID'/>			\n" +
                    "		</keyDef>			\n" +
                    "	</keyDefs>			\n" +
                    "	<propertyDefs>			\n" +
                    "		<propertyDef propertyName='MemberID' valueType='Integer' propertyType='simple'>			\n" +
                    "			<mappingDef tableName='tblTestMember' columnName='TESTMEMBERID'/>			\n" +
                    "		</propertyDef>			\n" +
                    "		<propertyDef propertyName='Name' valueType='String' propertyType='simple'>			\n" +
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

    @BeforeClass
    public static void init() throws SAXException {
        objectDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF);
    }

    @Test
    public void testGetMappingDefs() {
        final String lTableNameExpected = "tblTestMember";
        final String[] lExpected = { "TESTMEMBERID", "SNAME", "SFIRSTNAME", "SPASSWORD", "DTMUTATION" };
        final Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

        assertNotNull(objectDef);
        final Map<String, HelperTableDef> lHelperTableDefs = HelperTableDef.createTableDefs(objectDef);
        final Iterator<HelperTableDef> iter1 = lHelperTableDefs.values().iterator();
        while (iter1.hasNext()) {
            final HelperTableDef lHelperTableDef = iter1.next();
            assertEquals("tableName 0", lTableNameExpected, lHelperTableDef.getTableName());

            int i = 0;
            final Iterator<MappingDef> iter2 = lHelperTableDef.getMappingDefs2().iterator();
            while (iter2.hasNext()) {
                i++;
                final MappingDef lMappingDef = iter2.next();
                assertEquals("tableName " + i, lTableNameExpected, lMappingDef.getTableName());
                assertTrue("getMappingDefs " + i, lVExpected.contains(lMappingDef.getColumnName()));
            }
        }
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Map<String, HelperTableDef> lHelperTableDefs = HelperTableDef.createTableDefs(objectDef);

        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lHelperTableDefs);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lHelperTableDefs = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final Map<?, ?> lRetrieved = (Map<?, ?>) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        final String lTableNameExpected = "tblTestMember";
        final Iterator<?> iter = lRetrieved.values().iterator();
        while (iter.hasNext()) {
            final HelperTableDef lHelperTableDef = (HelperTableDef) iter.next();
            assertEquals("tableName", lTableNameExpected, lHelperTableDef.getTableName());
        }
    }

}
