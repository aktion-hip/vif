package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class KeyDefDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
		String[] lExpected = {"keyType", "schemaName", "keyItems"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		KeyDefDef lDef = MetaModelHome.singleton.getKeyDefDef() ;
		assertNotNull("testCreation not null", lDef );
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext(); ) {
			assertTrue("testCreation " + i, lVExpected.contains((String)lNames.next()));
		}	
	}
	
	@Test
	public void testToString() {
		KeyDefDef lDef = MetaModelHome.singleton.getKeyDefDef();
		assertNotNull("testToString not null", lDef);
	
		String lExected = "<org.hip.kernel.bom.model.impl.KeyDefDefImpl>\n" +
			"	<Attribute name=\"keyType\" type=\"java.lang.String\"/>\n" +
			"	<Attribute name=\"schemaName\" type=\"java.lang.String\"/>\n" +
			"	<Attribute name=\"keyItems\" type=\"java.util.Vector\"/>\n" +
			"</org.hip.kernel.bom.model.impl.KeyDefDefImpl>";
		assertEquals("toString", lExected, lDef.toString());
	}
}
