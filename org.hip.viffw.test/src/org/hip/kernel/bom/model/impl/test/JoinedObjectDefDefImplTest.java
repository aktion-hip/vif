package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.model.JoinedObjectDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.impl.JoinedObjectDefDefImpl;
import org.junit.Test;

/** @author: Benno Luthiger */
public class JoinedObjectDefDefImplTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testCreation() {

        final String[] lExpected = { JoinedObjectDefDef.version, JoinedObjectDefDef.joinDef,
                JoinedObjectDefDef.columnDefs, JoinedObjectDefDef.parent, JoinedObjectDefDef.objectName };
        final Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

        final JoinedObjectDefDef lDef = MetaModelHome.singleton.getJoinedObjectDefDef();
        assertNotNull("testCreation not null", lDef);

        final int i = 0;
        for (final Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext();) {
            assertTrue("testCreation " + i, lVExpected.contains(lNames.next()));
        }
    }

    @Test
    public void testInitializePropertySet() {

        final String[] lExpected = { JoinedObjectDefDef.version, JoinedObjectDefDef.joinDef,
                JoinedObjectDefDef.columnDefs, JoinedObjectDefDef.parent, JoinedObjectDefDef.objectName };
        final Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

        final PropertySet lPropertySet = new PropertySetImpl(null);
        final JoinedObjectDefDef lDef = MetaModelHome.singleton.getJoinedObjectDefDef();
        ((JoinedObjectDefDefImpl) lDef).initializePropertySet(lPropertySet);

        final int i = 0;
        final Iterator<String> iter = lPropertySet.getNames2().iterator();
        while (iter.hasNext()) {
            assertTrue("testInitialization " + i, lVExpected.contains(iter.next()));
        }

        final String lTest1 = "";
        try {
            if (Class.forName("java.lang.String").isInstance(lTest1)) {
                System.out.println("ok");
            }
            if (Class.forName("org.hip.kernel.bom.model.ObjectDefDef").isInstance(lTest1)) {
                System.out.println("falsch");
            }
            if (Class.forName("org.hip.kernel.bom.model.ObjectDefDef").isInstance(lDef)) {
                System.out.println("ok");
            }
        } catch (final ClassNotFoundException exc) {
        }
    }
}
