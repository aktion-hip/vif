package org.hip.kernel.bom.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.impl.DefaultSemanticObject;
import org.hip.kernel.bom.impl.ObjectRefPropertyImpl;
import org.hip.kernel.bom.impl.PropertyImpl;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.util.VInvalidValueException;
import org.junit.Test;

/** Test cases to test the Property implementation.
 *
 * @author Benno Luthiger */
public class PropertyImplTest {

    @Test
    public void testBasic() {
        XMLSerializer lXML = new XMLSerializer();
        final String lNL = System.getProperty("line.separator");

        // we define property but don't set a value yet
        final Property lProp = new PropertyImpl(null, "firstName", null);
        assertNotNull(lProp);
        assertTrue("isSimple", lProp.isSimple());
        assertTrue("isChanged1", !lProp.isChanged());
        assertEquals("getValue1", lProp.getValue(), null);

        // we set a value now
        try {
            lProp.setValue("Adam");
        } catch (final VInvalidValueException ex) {
            fail("VInvalidValueException");
        }
        assertTrue("isChanged2", !lProp.isChanged());
        assertEquals("getValue2", lProp.getValue(), "Adam");
        lXML = new XMLSerializer();
        lProp.accept(lXML);
        assertEquals("serialized2", lNL + "<firstName>Adam</firstName>", lXML.toString());

        // wet set a second property
        final Property lProp2 = new PropertyImpl(null, "firstName", "Adam");
        assertNotNull(lProp2);
        assertTrue("equals1", lProp.equals(lProp2));

        // we change the value and reset them after
        try {
            lProp.setValue("Eva");
            assertTrue("isChanged3", lProp.isChanged());
            lXML = new XMLSerializer();
            lProp.accept(lXML);
            assertEquals("serialized3", lNL + "<firstName>Eva</firstName>", lXML.toString());
            lProp.setValue("Adam");
        } catch (final VInvalidValueException ex) {
            fail("VInvalidValueException");
        }
        assertTrue("isChanged4", !lProp.isChanged());
        assertTrue("equals3", lProp.equals(lProp2));
        lXML = new XMLSerializer();
        lProp.accept(lXML);
        assertEquals("serialized4", lNL + "<firstName>Adam</firstName>", lXML.toString());

        // we change the value again
        try {
            lProp.setValue("Hans");
            assertTrue("isChanged5", lProp.isChanged());
            lXML = new XMLSerializer();
            lProp.accept(lXML);
            assertEquals("serialized5", lNL + "<firstName>Hans</firstName>", lXML.toString());
        } catch (final VInvalidValueException ex) {
            fail("VInvalidValueException");
        }

        try {
            lProp.getPropertyDef();
            assertTrue("getPropertyDef", false);
        } catch (final NullPointerException ex) {
        }

        // we set the property to vergin
        lProp.setVirgin();
        assertNull("virgin value", lProp.getValue());
        assertTrue("isChanged5", !lProp.isChanged());

        final Property objRef = new ObjectRefPropertyImpl(null, "dummytest", null);
        assertNotNull(objRef);
        assertTrue(objRef.isObjectRef());
    }

    @Test
    public void testChecked() {
        XMLSerializer lXML = new XMLSerializer();
        final String lNL = System.getProperty("line.separator");

        Property lProp = null;
        try {
            // we define property with typeInformation but don't set a value yet
            lProp = new PropertyImpl(null, "firstName", null, "java.lang.String");
            assertNotNull(lProp);
            assertTrue("isSimple", lProp.isSimple());
            assertTrue("isChanged1", !lProp.isChanged());
            assertEquals("getValue1", lProp.getValue(), null);
            lProp.accept(lXML);
            assertEquals("serialized1", lNL + "<firstName></firstName>", lXML.toString());

            // we set a value now
            lProp.setValue("Adam");
            assertTrue("isChanged2", !lProp.isChanged());
            assertEquals("getValue2", lProp.getValue(), "Adam");
            lXML = new XMLSerializer();
            lProp.accept(lXML);
            assertEquals("serialized2", lNL + "<firstName>Adam</firstName>", lXML.toString());

        } catch (final VInvalidValueException ex) {
            fail("VInvalidValueException");
        }

        assertNotNull(lProp);

        try {
            // we set a value with wrong type
            lProp.setValue(lXML);
            fail("Wrong type: shouldn't get here!");
        } catch (final VInvalidValueException ex) {
        }
    }

    @Test
    public void testEquals() {
        final Property lProperty1 = new PropertyImpl(null, "firstName", "Adam");
        final Property lProperty2 = new PropertyImpl(null, "firstName", "Eva");
        final Property lProperty3 = lProperty1;
        final Property lProperty4 = new PropertyImpl(null, "lastName", "Adam");

        assertTrue("identity", lProperty1 == lProperty3);
        assertTrue("no identity", lProperty1 != lProperty2);
        assertTrue("equality", lProperty1.equals(lProperty2));
        assertTrue("no equality 1", !lProperty1.equals(lProperty4));
        assertTrue("no equality 2", !lProperty1.equals(null));
    }

    @Test
    public void testToString() {
        final Property lProperty1 = new PropertyImpl(null, "firstName", "Adam");
        final Property lProperty2 = new PropertyImpl(null, "firstName", "Eva");
        final Property lProperty3 = new PropertyImpl(null, "firstName", null);
        final Property lProperty4 = new PropertyImpl(null, "lastName", lProperty1);

        final String lExpcected1 = "<Property name=\"firstName\" value=\"Adam\" /> ";
        final String lExpcected2 = "<Property name=\"firstName\" value=\"Eva\" /> ";
        final String lExpcected3 = "<Property name=\"firstName\" value=\"null\" /> ";
        final String lExpcected4 = "<Property name=\"lastName\" value=\"<Property name=\"firstName\" value=\"Adam\" /> \" /> ";

        assertEquals("toString 1", lExpcected1, lProperty1.toString());
        assertEquals("toString 2", lExpcected2, lProperty2.toString());
        assertEquals("toString 3", lExpcected3, lProperty3.toString());
        assertEquals("toString 4", lExpcected4, lProperty4.toString());
    }

    @Test
    public void testNotification() {
        // we define property but don't set a value yet
        final Property lProperty1 = new PropertyImpl(null, "firstName", null);
        final Property lProperty2 = new PropertyImpl(null, "firstName", null);
        assertTrue("isChanged1", !lProperty1.isChanged());
        assertEquals("getValue1", lProperty1.getValue(), null);
        assertTrue("isChanged2", !lProperty2.isChanged());
        assertEquals("getValue2", lProperty2.getValue(), null);

        // set notification of property 2
        lProperty2.notifyInit(true);

        // we set a value now
        try {
            lProperty1.setValue("Adam");
            lProperty2.setValue("Adam");
        } catch (final VInvalidValueException exc) {
            fail("VInvalidValueException");
        }

        assertNotNull("getValue3", lProperty1.getValue());
        assertNotNull("getValue4", lProperty1.getValue());
        assertTrue("isChanged3", !lProperty1.isChanged());
        // property 2 is marked as changed because of notification status
        assertTrue("isChanged4", lProperty2.isChanged());
    }

    @Test
    public void testClone() throws Exception {
        final DefaultSemanticObject lValue = new DefaultSemanticObject();
        lValue.set("prop1", "String value");
        lValue.set("prop2", Integer.valueOf(42));
        final Property lOriginal = new PropertyImpl(null, "firstName", lValue);

        // clone
        final Property lClone = (Property) lOriginal.clone();
        assertEquals("firstName", lClone.getName());
        final Object lClonedObj = lClone.getValue();
        assertEquals("org.hip.kernel.bom.impl.DefaultSemanticObject", lClonedObj.getClass().getName());
        final DefaultSemanticObject lClonedValue = (DefaultSemanticObject) lClonedObj;
        assertEquals("String value", lClonedValue.get("prop1"));
        assertEquals(42, lClonedValue.get("prop2"));

        // change values
        lClonedValue.set("prop1", "different string");
        lClonedValue.set("prop2", Long.valueOf(100));
        assertEquals("String value", lValue.get("prop1"));
        assertEquals(42, lValue.get("prop2"));
        assertEquals("different string", lClonedValue.get("prop1"));
        assertEquals(100l, lClonedValue.get("prop2"));

        lClone.setValue("something different");
        assertEquals("firstName", lClone.getName());
        assertEquals("something different", lClone.getValue().toString());
        assertEquals("firstName", lOriginal.getName());
        assertEquals("org.hip.kernel.bom.impl.DefaultSemanticObject", lOriginal.getValue().getClass().getName());
    }

}
