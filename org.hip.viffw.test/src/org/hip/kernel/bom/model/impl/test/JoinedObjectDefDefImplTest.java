package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.model.JoinedObjectDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.impl.JoinedObjectDefDefImpl;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class JoinedObjectDefDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
	
		String[] lExpected = {JoinedObjectDefDef.version, JoinedObjectDefDef.joinDef, JoinedObjectDefDef.columnDefs, JoinedObjectDefDef.parent, JoinedObjectDefDef.objectName};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		
		JoinedObjectDefDef lDef = MetaModelHome.singleton.getJoinedObjectDefDef();
		assertNotNull("testCreation not null", lDef );
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext(); ) {
			assertTrue("testCreation " + i, lVExpected.contains((String)lNames.next()));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testInitializePropertySet() {
	
		String[] lExpected = {JoinedObjectDefDef.version, JoinedObjectDefDef.joinDef, JoinedObjectDefDef.columnDefs, JoinedObjectDefDef.parent, JoinedObjectDefDef.objectName};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		
		PropertySet lPropertySet = new PropertySetImpl(null);
		JoinedObjectDefDef lDef = MetaModelHome.singleton.getJoinedObjectDefDef();
		((JoinedObjectDefDefImpl)lDef).initializePropertySet(lPropertySet);
		
		int i = 0;
		for (Iterator<?> lNames = lPropertySet.getNames(); lNames.hasNext(); ) {
			assertTrue("testInitialization " + i, lVExpected.contains((String)lNames.next()));
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
