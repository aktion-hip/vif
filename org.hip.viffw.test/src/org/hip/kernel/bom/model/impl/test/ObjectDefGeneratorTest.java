package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.model.MappingDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.PrimaryKeyDef;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.impl.ObjectDefGenerator;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/** @author: Benno Luthiger */
public class ObjectDefGeneratorTest {
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

    private void outMapping(final ObjectDef inObjectDef) {
        final Iterator<String> iter1 = inObjectDef.getTableNames2().iterator();
        while (iter1.hasNext()) {
            final String lTableName = iter1.next();
            assertEquals("testGenerate 5", "tblTestMember", lTableName);
            System.out.println("\t\tMapping for table: " + lTableName);
            final Iterator<MappingDef> iter2 = inObjectDef.getMappingDefsForTable2(lTableName).iterator();
            while (iter2.hasNext()) {
                final MappingDef lMappingDef = iter2.next();
                System.out.println("\t\t\tcolumnName=" + lMappingDef.getColumnName());
            }
        }
    }

    private void outPrimary(final ObjectDef inObjectDef) {
        try {
            VSys.out.println("objectName=" + (String) inObjectDef.get("objectName"));
            VSys.out.println("\tparent=" + (String) inObjectDef.get("parent"));
            VSys.out.println("\tversion=" + (String) inObjectDef.get("version"));

            final PrimaryKeyDef lPrimKeyDef = (PrimaryKeyDef) inObjectDef.getPrimaryKeyDef();
            assertNotNull(lPrimKeyDef);
            VSys.out.println("\tprimaryKey");
            final Iterator<String> iter = lPrimKeyDef.getKeyNames2().iterator();
            while (iter.hasNext()) {
                final String lKey = iter.next();
                assertEquals("testGenerate 4", "MemberID", lKey);
                VSys.out.println("\t\t" + lKey);
            }
        } catch (final org.hip.kernel.bom.GettingException exc) {
            fail(exc.toString());
        }
    }

    private void outPropertySet(final PropertySet inSet) {
        try {
            final Iterator<String> iter = inSet.getNames2().iterator();
            while (iter.hasNext()) {
                final String lName = iter.next();
                final PropertyDef lPropertyDef = (PropertyDef) inSet.getValue(lName);
                System.out.println("\tpropertyName=" + (String) lPropertyDef.get("propertyName"));
                System.out.println("\t\tpropertyType=" + (String) lPropertyDef.get("propertyType"));
                System.out.println("\t\tvalueType=" + (String) lPropertyDef.get("valueType"));

                final MappingDef lMapDef = lPropertyDef.getMappingDef();
                if (lMapDef != null) {
                    System.out.println("\t\tMapping:");
                    System.out.println("\t\t\ttableName=" + lMapDef.getTableName());
                    System.out.println("\t\t\tcolumnName=" + lMapDef.getColumnName());
                }
            }
        } catch (final org.hip.kernel.bom.GettingException exc) {
            fail(exc.toString());
        } catch (final org.hip.kernel.util.VInvalidNameException exc) {
            fail(exc.toString());
        }
    }

    @Test
    public void testGenerate() {
        try {
            final ObjectDef lObjectDef = ObjectDefGenerator.getSingleton().createObjectDef(XML_OBJECT_DEF);

            assertEquals("testGenerate 1", "TestDomainObject", lObjectDef.get(ObjectDefDef.objectName));
            assertEquals("testGenerate 2", "org.hip.kernel.bom.DomainObject", lObjectDef.get(ObjectDefDef.parent));
            assertEquals("testGenerate 3", "1.0", lObjectDef.get(ObjectDefDef.version));

            outPrimary(lObjectDef);
            outPropertySet((PropertySet) lObjectDef.get(ObjectDefDef.propertyDefs));
            outMapping(lObjectDef);
        } catch (final org.xml.sax.SAXException exc) {
            fail("testGenerate " + exc.toString());
        } catch (final org.hip.kernel.bom.GettingException exc) {
            fail("testGenerate " + exc.toString());
        }
    }
}
