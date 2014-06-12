package org.hip.kernel.code.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class CodeTest {
	private static TestCode testCode;
	
	@BeforeClass
	public static void init() {
		testCode = new TestCode("2");		
	}
	
	@Test
	public void testChangeByElementID() throws Exception {
		assertEquals("pre", "2", testCode.getElementID());
		assertEquals("pre", "ist", testCode.getLabel("de"));
		testCode.changeByElementID("4", "de");
		assertEquals("post 1", "4", testCode.getElementID());
		assertEquals("post 1", "Test!", testCode.getLabel("de"));
		testCode.changeByElementID("2", "de");
		assertEquals("post 2", "2", testCode.getElementID());
		assertEquals("post 2", "ist", testCode.getLabel("de"));
	}
	
	@Test
	public void testChangeByLabel() throws Exception {
		assertEquals("pre", "2", testCode.getElementID());
		assertEquals("pre", "ist", testCode.getLabel("de"));
		testCode.changeByLabel("Test!", "de");
		assertEquals("post 1", "4", testCode.getElementID());
		assertEquals("post 1", "Test!", testCode.getLabel("de"));
		testCode.changeByLabel("ist", "de");
		assertEquals("post 2", "2", testCode.getElementID());
		assertEquals("post 2", "ist", testCode.getLabel("de"));
	}
	
	@Test
	public void testCompare() {
		TestCode lCode1 = new TestCode("1");
		TestCode lCode2 = new TestCode("2");
		TestCode lCode3 = new TestCode("3");
	
		//test compare
		assertEquals("compare this with less", 1, testCode.compareTo(lCode1));
		assertEquals("compare this with equal", 0, testCode.compareTo(lCode2));
		assertEquals("compare this with greater", -1, testCode.compareTo(lCode3));
	
		//test equals
		assertTrue("equals", testCode.equals(lCode2));
		assertEquals("equal hash code", testCode.hashCode(), lCode2.hashCode());
	
		assertTrue("not equals 1", !testCode.equals(lCode1));
		assertTrue("not equal hashCode 1", testCode.hashCode() != lCode1.hashCode());
		assertTrue("not equals 2", !testCode.equals(lCode3));
		assertTrue("not equal hashCode 2", testCode.hashCode() != lCode3.hashCode());
	}
	
	@Test
	public void testGetElementIDs() throws Exception {
		String[] lExpected = {"1", "2", "3", "4"};
		Vector<String> lVExpected = new Vector<String>(Arrays.asList(lExpected));
		
		String[] lIDs = testCode.getElementIDs();
		assertEquals("equal size", lExpected.length, lIDs.length);
		for (int i = lIDs.length-1; i>=0; i--) 
			assertTrue("getElementIDs " + i, lVExpected.contains(lIDs[i]));
	}
	
	@Test
	public void testGetLabels() throws Exception {
		String[] lExpected1 = {"Dies", "ist", "ein", "Test!"};
		Vector<String> lVExpected1 = new Vector<String>(Arrays.asList(lExpected1));
		String[] lExpected2 = {"This", "is", "a", "test!"};
		Vector<String> lVExpected2 = new Vector<String>(Arrays.asList(lExpected2));
		
		String[] lLabels = testCode.getLabels("de");
		assertEquals("equal size", lExpected1.length, lLabels.length);
		for (int i = lLabels.length-1; i>=0; i--) 
			assertTrue("getElementIDs de " + i, lVExpected1.contains(lLabels[i]));
			
		lLabels = testCode.getLabels("en");
		assertEquals("equal size", lExpected2.length, lLabels.length);
		for (int i = lLabels.length-1; i>=0; i--) 
			assertTrue("getElementIDs en " + i, lVExpected2.contains(lLabels[i]));
	}
}
