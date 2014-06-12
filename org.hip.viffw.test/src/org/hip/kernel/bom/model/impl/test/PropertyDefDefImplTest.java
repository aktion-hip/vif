package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.PropertyDefDef;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class PropertyDefDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
	
		String[] lExpected = {"propertyType", "valueType", "propertyName", "mappingDef", "relationshipDef", "formatPattern"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		PropertyDefDef lDef = MetaModelHome.singleton.getPropertyDefDef() ;
		assertNotNull("testCreation not null", lDef );
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext(); ) {
			assertTrue("testCreation " + i, lVExpected.contains((String)lNames.next()));
		}
	}
}
