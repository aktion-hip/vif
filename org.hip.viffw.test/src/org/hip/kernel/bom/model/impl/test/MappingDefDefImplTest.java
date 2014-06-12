package org.hip.kernel.bom.model.impl.test;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.model.MappingDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class MappingDefDefImplTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testCreation() {
		String[] lExpected = {"tableName", "columnName"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		MappingDefDef lDef = MetaModelHome.singleton.getMappingDefDef() ;
		assertNotNull("testCreation not null", lDef );
	
		int i = 0;
		for (Iterator<?> lNames = lDef.getPropertyNames(); lNames.hasNext(); ) {
			assertTrue("testCreation " + i, lVExpected.contains((String)lNames.next()));
		}
	}
}
