package org.hip.kernel.code.test;

import static org.junit.Assert.*;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.code.CodeNotFoundException;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.12.2007
 */
public class CodeListHomeTest {

	@Test
	public void testGetCodeList() throws Exception {
		CodeList lCodeList = CodeListHome.instance().getCodeList(TestCode.class, "de");
		assertNotNull("existance", lCodeList);
		
		assertTrue("ID 1", lCodeList.existElementID("1"));
		assertTrue("ID 2", lCodeList.existElementID("2"));
		assertTrue("ID 3", lCodeList.existElementID("3"));
		assertTrue("ID 4", lCodeList.existElementID("4"));
		
		try {
			lCodeList.existElementID("5");
			fail("ID 5 doesn't exist");
		}
		catch (CodeNotFoundException exc) {
		}
	}

}
