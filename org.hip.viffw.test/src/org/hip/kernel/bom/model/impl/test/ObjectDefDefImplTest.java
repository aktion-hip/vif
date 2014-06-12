package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Vector;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.ObjectDefDef;
import org.hip.kernel.bom.model.impl.ObjectDefDefImpl;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class ObjectDefDefImplTest {

	@Test
	public void testCreation() {
		String[] lExpected = {ObjectDefDef.version, ObjectDefDef.propertyDefs, ObjectDefDef.keyDefs, ObjectDefDef.parent, ObjectDefDef.objectName, ObjectDefDef.baseDir};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

		ObjectDefDef lDef = MetaModelHome.singleton.getObjectDefDef() ;
		assertNotNull("testCreation not null", lDef );

		int i = 0;
		for (String lName : lDef.getPropertyNames2()) {
			assertTrue("testCreation " + i, lVExpected.contains(lName));
		}
	}

	@Test
	public void testInitializePropertySet() {

		String[] lExpected = {ObjectDefDef.version, ObjectDefDef.propertyDefs, ObjectDefDef.keyDefs, ObjectDefDef.parent, ObjectDefDef.objectName, ObjectDefDef.baseDir};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));

		PropertySet lPropertySet = new PropertySetImpl(null);
		ObjectDefDef lDef = MetaModelHome.singleton.getObjectDefDef();
		((ObjectDefDefImpl)lDef).initializePropertySet(lPropertySet);

		int i = 0;
		for (String lName : lPropertySet.getNames2()) {
			assertTrue("testInitialization " + i, lVExpected.contains(lName));
		}

		String lTest1 = "";
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
		}
		catch (ClassNotFoundException exc) {}
	}
}
