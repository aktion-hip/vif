package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.impl.PrimaryKeyDefImpl;
import org.hip.kernel.sys.VSys;
import org.junit.Test;

/** @author: Benno Luthiger */
public class KeyDefImplTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testCreation() {

        final KeyDef lDef = new PrimaryKeyDefImpl();
        assertNotNull("testCreation 1", lDef);

        final Iterator<?> lNames = lDef.getKeyNames();
        assertTrue("testCreation 2", !lNames.hasNext());
    }

    @Test
    public void testCreationWithInitialValues() {

        final String[] lValues = { "codeSchemaId", "codeId" };

        final Vector<String> lKeyItems = new Vector<String>(Arrays.asList(lValues));
        final Object[][] lInitValues = {
                { KeyDefDef.keyItems, lKeyItems }
        };

        final KeyDef lDef = new PrimaryKeyDefImpl(lInitValues);
        assertNotNull("testCreationWithInitialValues not null", lDef);

        int i = 0;
        final Iterator<String> iter = lDef.getKeyNames2().iterator();
        while (iter.hasNext()) {
            final String lName = iter.next();
            assertEquals("testCreationWithInitialValues " + i, lValues[i++], lName);
        }
        assertEquals("number of values", i, lValues.length);

        try {
            final Iterator<String> iter2 = lDef.getPropertyNames2().iterator();
            while (iter2.hasNext()) {
                final String lName = iter2.next();
                VSys.out.println(lName + " -> " + lDef.get(lName));
            }
        } catch (final org.hip.kernel.bom.GettingException exc) {
            fail();
        }

    }

    @Test
    public void testEquals() {

        final String[] lValues1 = { "codeSchemaId", "codeId" };
        final String[] lValues2 = { "codeSchemaId", "memberId" };

        final Vector<String> lKeyItems1 = new Vector<String>(Arrays.asList(lValues1));
        final Object[][] lInitValues1 = {
                { KeyDefDef.keyItems, lKeyItems1 }
        };
        final Vector<String> lKeyItems2 = new Vector<String>(Arrays.asList(lValues2));
        final Object[][] lInitValues2 = {
                { KeyDefDef.keyItems, lKeyItems2 }
        };

        final KeyDef lDef1 = new PrimaryKeyDefImpl(lInitValues1);
        assertNotNull("testEquals1 not null", lDef1);
        final KeyDef lDef2 = new PrimaryKeyDefImpl(lInitValues2);
        assertNotNull("testEquals2 not null", lDef2);
        final KeyDef lDef3 = new PrimaryKeyDefImpl(lInitValues1);
        assertNotNull("testEquals3 not null", lDef3);

        assertTrue("equals", lDef1.equals(lDef3));
        assertEquals("equal hash code", lDef1.hashCode(), lDef3.hashCode());

        assertTrue("not equals", !lDef1.equals(lDef2));
        assertTrue("not equal hash code", lDef1.hashCode() != lDef2.hashCode());
    }

    @Test
    public void testToString() {

        final String[] lValues = { "codeSchemaId", "codeId" };

        final Vector<String> lKeyItems = new Vector<String>(Arrays.asList(lValues));
        final Object[][] lInitValues = {
                { KeyDefDef.keyItems, lKeyItems }
        };

        final KeyDef lDef = new PrimaryKeyDefImpl(lInitValues);
        assertNotNull("testToString not null", lDef);
        assertEquals(
                "toString",
                "< org.hip.kernel.bom.model.impl.PrimaryKeyDefImpl keyPropertyName=\"codeSchemaId\" keyPropertyName=\"codeId\"  />",
                lDef.toString());
    }
}
