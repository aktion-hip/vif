package org.hip.kernel.util.test;

import static org.junit.Assert.*;
import junit.framework.AssertionFailedError;

import org.hip.kernel.util.DefaultNameValue;
import org.hip.kernel.util.DefaultNameValueList;
import org.hip.kernel.util.DefaultNameValueListVisitor;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class NameValueListVisitorTest {

	@Test
	public void testVisit() {
		DefaultNameValueList lList = new DefaultNameValueList();
		
		DefaultNameValue lNameValue1 = new DefaultNameValue(lList, "testValue1", "Hallo Velo");
		DefaultNameValueListVisitor lVisitor = new DefaultNameValueListVisitor();
		lNameValue1.accept(lVisitor);
		String lExpected1 = "<org.hip.kernel.util.DefaultNameValue name='testValue1'>\n" + 
			"	< java.lang.String 'Hallo Velo' />\n" + 
			"</org.hip.kernel.util.DefaultNameValue>";
		assertEquals("visit NameValue1", lExpected1, lVisitor.toString());
		
		DefaultNameValue lNameValue2 = new DefaultNameValue(lList, "testValue2", null);
		lVisitor = new DefaultNameValueListVisitor();
		lNameValue2.accept(lVisitor);
		String lExpected2 = "<org.hip.kernel.util.DefaultNameValue name='testValue2'>\n" + 
			"	<value=?/>\n" + 
			"</org.hip.kernel.util.DefaultNameValue>";
		assertEquals("visit NameValue2", lExpected2, lVisitor.toString());
		
		DefaultNameValue lNameValue3 = new DefaultNameValue(lList, "testValue3", new AssertionFailedError("Hallo Error"));
		lVisitor = new DefaultNameValueListVisitor();
		lNameValue3.accept(lVisitor);
		String lExpected3 = "<org.hip.kernel.util.DefaultNameValue name='testValue3'>\n" + 
			"	< junit.framework.AssertionFailedError 'junit.framework.AssertionFailedError: Hallo Error' />\n" + 
			"</org.hip.kernel.util.DefaultNameValue>";
		assertEquals("visit NameValue3", lExpected3, lVisitor.toString());
	
		lVisitor = new DefaultNameValueListVisitor();
		lList.add(lNameValue1);
		lList.add(lNameValue2);
		lList.add(lNameValue3);
		lList.accept(lVisitor);
		String lExpectedV1 = "\t<org.hip.kernel.util.DefaultNameValue name='testValue1'>\n" + 
			"\t\t< java.lang.String 'Hallo Velo' />\n" + 
			"\t</org.hip.kernel.util.DefaultNameValue>";
		String lExpectedV2 = "\t<org.hip.kernel.util.DefaultNameValue name='testValue2'>\n" + 
			"\t\t<value=?/>\n" + 
			"\t</org.hip.kernel.util.DefaultNameValue>";		
		String lExpectedV3 = "\t<org.hip.kernel.util.DefaultNameValue name='testValue3'>\n" + 
			"\t\t< junit.framework.AssertionFailedError 'junit.framework.AssertionFailedError: Hallo Error' />\n" + 
			"\t</org.hip.kernel.util.DefaultNameValue>";
		assertTrue("visit List1", lVisitor.toString().indexOf(lExpectedV1) > 0);
		assertTrue("visit List2", lVisitor.toString().indexOf(lExpectedV2) > 0);
		assertTrue("visit List3", lVisitor.toString().indexOf(lExpectedV3) > 0);
	}
}
