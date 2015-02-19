package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.impl.DefaultSemanticObject;
import org.hip.kernel.exc.VException;
import org.junit.Test;

/** Test cases to test the functionality of the DomainObject framework.
 *
 * @author Benno Luthiger */
public class DefaultSemanticObjectTest {

    @Test
    public void testDynamicSetting() {

        final DefaultSemanticObject lTestObject = new DefaultSemanticObject();
        assertNotNull(lTestObject);

        final String lName = "firstName";
        final String lValue = "Adam";
        final String lNewValue = "Eva";

        try {
            lTestObject.set(lName, lValue);
            assertEquals(lTestObject.get(lName), lValue);
            lTestObject.set(lName, lNewValue);
            assertEquals(lTestObject.get(lName), lNewValue);

            lTestObject.setVirgin();
            assertNull("virgin", lTestObject.get(lName));
        } catch (final Exception exc) {
            fail(exc.toString());
        }
    }

    @Test
    public void testEquals() {

        final DefaultSemanticObject lSemantic1 = new DefaultSemanticObject();
        assertNotNull(lSemantic1);
        final DefaultSemanticObject lSemantic2 = new DefaultSemanticObject();
        assertNotNull(lSemantic2);
        final DefaultSemanticObject lSemantic3 = new DefaultSemanticObject();
        assertNotNull(lSemantic3);

        final String lFName = "firstName";
        final String lLName = "lastName";
        final String lFValue = "Adam";
        final String lLValue = "HIP";
        final String lNewValue = "Eva";

        try {
            lSemantic1.set(lFName, lFValue);
            lSemantic1.set(lLName, lLValue);
            lSemantic2.set(lFName, lFValue);
            lSemantic2.set(lLName, lNewValue);
            lSemantic3.set(lFName, lFValue);
            lSemantic3.set(lLName, lLValue);

            assertTrue("equals", lSemantic1.equals(lSemantic3));
            assertEquals("equal hash code", lSemantic1.hashCode(), lSemantic3.hashCode());

            assertTrue("not equals", !lSemantic1.equals(lSemantic2));
            assertTrue("not equal hash code", lSemantic1.hashCode() != lSemantic2.hashCode());

            lSemantic1.setVirgin();
            lSemantic3.setVirgin();
            assertTrue("equals 2", lSemantic1.equals(lSemantic3));
            assertEquals("equal hash code 2", lSemantic1.hashCode(), lSemantic3.hashCode());
        } catch (final Exception exc) {
            fail(exc.toString());
        }
    }

    @Test
    public void testGetPropertyNames() throws VException {
        final String[] lExpectedNames = new String[] { "firstName", "lastName", "birthDate" };

        final String lExpected1 = "<org.hip.kernel.bom.impl.DefaultSemanticObject>\n" +
                "</org.hip.kernel.bom.impl.DefaultSemanticObject>";

        final DefaultSemanticObject lTestObject = new DefaultSemanticObject();
        assertNotNull(lTestObject);
        assertEquals("toString 1", lExpected1, lTestObject.toString());

        lTestObject.set(lExpectedNames[0], "T. ");
        lTestObject.set(lExpectedNames[1], "Dummy");
        assertTrue("toString 2", lTestObject.toString().indexOf("<name=\"lastName\" value=\"Dummy\"/>") > 0);
        assertTrue("toString 3", lTestObject.toString().indexOf("<name=\"firstName\" value=\"T. \"/>") > 0);
        assertTrue("toString 4", lTestObject.toString().indexOf("<org.hip.kernel.bom.impl.DefaultSemanticObject>") == 0);
        assertTrue("toString 5", lTestObject.toString().indexOf("</org.hip.kernel.bom.impl.DefaultSemanticObject>") > 0);

        lTestObject.set(lExpectedNames[2], new java.util.GregorianCalendar(1960, 01, 22));

        final Collection<String> lExpected = Arrays.asList(lExpectedNames);
        for (final Iterator<String> lNames = lTestObject.getPropertyNames(); lNames.hasNext();) {
            assertTrue("PropertyNames", lExpected.contains(lNames.next()));
        }

        for (int i = 0; i < lExpectedNames.length; i++) {
            assertTrue("PropertyNames " + i, lTestObject.getPropertyNames2().contains(lExpectedNames[i]));
        }
        assertEquals("equal size", lExpectedNames.length, lTestObject.getPropertyNames2().size());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, VException {
        final String lName = "Dummy";
        final String lFirst = "T. ";

        DefaultSemanticObject lTestObject = new DefaultSemanticObject();
        lTestObject.set("firstName", lFirst);
        lTestObject.set("lastName", lName);

        final ByteArrayOutputStream lBytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream lObjectOut = new ObjectOutputStream(lBytesOut);
        lObjectOut.writeObject(lTestObject);
        final byte[] lSerialized = lBytesOut.toByteArray();
        lObjectOut.close();
        lBytesOut.close();
        lTestObject = null;

        final ByteArrayInputStream lBytesIn = new ByteArrayInputStream(lSerialized);
        final ObjectInputStream lObjectIn = new ObjectInputStream(lBytesIn);
        final DefaultSemanticObject lRetrieved = (DefaultSemanticObject) lObjectIn.readObject();
        lObjectIn.close();
        lBytesIn.close();

        assertEquals("retrieved first name", lFirst, lRetrieved.get("firstName"));
        assertEquals("retrieved last name", lName, lRetrieved.get("lastName"));
    }

    @Test
    public void testClone() throws Exception {
        final DefaultSemanticObject lOriginal = new DefaultSemanticObject();
        lOriginal.set("prop1", "String value");
        lOriginal.set("prop2", Integer.valueOf(42));

        // clone
        final DefaultSemanticObject lClone = (DefaultSemanticObject) lOriginal.clone();
        assertEquals("String value", lOriginal.get("prop1"));
        assertEquals("String value", lClone.get("prop1"));
        assertEquals(42, lOriginal.get("prop2"));
        assertEquals(42, lClone.get("prop2"));

        // change values
        lClone.set("prop1", "different string");
        assertEquals("String value", lOriginal.get("prop1"));
        assertEquals("different string", lClone.get("prop1"));

        lClone.set("prop2", Long.valueOf(100));
        assertEquals(42, lOriginal.get("prop2"));
        assertEquals(100l, lClone.get("prop2"));
    }

    @Test
    public void testClone2() throws Exception {
        DefaultSemanticObject lChild = new DefaultSemanticObject();
        lChild.set("prop1", "String value");
        lChild.set("prop2", Integer.valueOf(42));
        final DefaultSemanticObject lOriginal = new DefaultSemanticObject();
        lOriginal.set("prop", lChild);

        // clone
        final DefaultSemanticObject lClone = (DefaultSemanticObject) lOriginal.clone();
        final Object lCloneChild = lClone.get("prop");
        assertEquals("org.hip.kernel.bom.impl.DefaultSemanticObject", lCloneChild.getClass().getName());
        assertEquals("String value", ((DefaultSemanticObject) lCloneChild).get("prop1"));
        assertEquals(42, ((DefaultSemanticObject) lCloneChild).get("prop2"));

        // change values
        final DefaultSemanticObject lChildObj = (DefaultSemanticObject) lCloneChild;
        lChildObj.set("prop1", "different string");
        lChildObj.set("prop2", Long.valueOf(100));

        lChild = (DefaultSemanticObject) lOriginal.get("prop");
        assertEquals("String value", lChild.get("prop1"));
        assertEquals(42, lChild.get("prop2"));
        assertEquals("different string", ((DefaultSemanticObject) lClone.get("prop")).get("prop1"));
        assertEquals(100l, ((DefaultSemanticObject) lClone.get("prop")).get("prop2"));
    }

}
