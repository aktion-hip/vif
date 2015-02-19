package org.hip.kernel.bom.impl.test;

import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/** TestHome used to test the joining of domain objects.
 *
 * @author: Benno Luthiger */
@SuppressWarnings("serial")
public class LinkGroupMemberHomeImpl extends DomainObjectHomeImpl {
    /** Every home has to know the class it handles. They provide access to this name through the method
     * <I>getObjectClassName</I>; */
    private final static String TESTOBJECT_CLASS_NAME = "org.hip.kernel.bom.impl.test.LinkGroupMemberImpl";

    /*
     * The current version of the domain object framework provides no support for externelized metadata. We build them
     * up with hard coded definition strings.
     */
    // CAUTION: The current version of the lightweight DomainObject
    // framework makes only a limited check of the correctness
    // of the definition string. Make extensive basic test to
    // ensure that the definition works correct.
    // See as an example: TestPerson

    private final static String XML_OBJECT_DEF =
            "<?xml version='1.0' encoding='us-ascii'?>			\n"
                    +
                    "<objectDef objectName='LinkGroupMember' parent='org.hip.kernel.bom.DomainObject' version='1.0'>			\n"
                    +
                    "	<keyDefs>			\n" +
                    "		<keyDef>			\n" +
                    "			<keyItemDef seq='0' keyPropertyName='MemberID'/>			\n" +
                    "			<keyItemDef seq='1' keyPropertyName='GroupID'/>			\n" +
                    "		</keyDef>			\n" +
                    "	</keyDefs>			\n" +
                    "	<propertyDefs>			\n" +
                    "		<propertyDef propertyName='MemberID' valueType='Number' propertyType='simple'>			\n" +
                    "			<mappingDef tableName='tblGroupAdmin' columnName='MEMBERID'/>			\n" +
                    "		</propertyDef>			\n" +
                    "		<propertyDef propertyName='GroupID' valueType='Number' propertyType='simple'>			\n" +
                    "			<mappingDef tableName='tblGroupAdmin' columnName='GROUPID'/>			\n" +
                    "		</propertyDef>			\n" +
                    "	</propertyDefs>			\n" +
                    "</objectDef>			";

    /** TestDomainObjectHomeImpl default constructor. */
    public LinkGroupMemberHomeImpl() {
        super();
    }

    /** Creates a vector containing objects for testing purpose. */
    @Override
    protected Vector<Object> createTestObjects() {
        final Vector<Object> lTestObjects = new Vector<Object>();
        final KeyObject lKey = new KeyObjectImpl();
        try {
            lKey.setValue("Name", "Luthiger");
            lKey.setValue("FirstName", "Benno");

            lTestObjects.addElement(createCountAllString());
            lTestObjects.addElement(createCountString(lKey));
            lTestObjects.addElement(createKeyCountColumnList());
            lTestObjects.addElement(createPreparedSelectString(lKey));
            lTestObjects.addElement(createSelectAllString());
            lTestObjects.addElement(createSelectString(lKey));
        } catch (final VInvalidNameException exc) {
        } catch (final VInvalidValueException exc) {
        } catch (final BOMException exc) {
        }

        return lTestObjects;
    }

    @Override
    public String getObjectClassName() {
        return TESTOBJECT_CLASS_NAME;
    }

    @Override
    protected String getObjectDefString() {
        return XML_OBJECT_DEF;
    }
}
