package org.hip.kernel.servlet.test;

import static org.junit.Assert.*;
import java.io.File;

import org.hip.kernel.servlet.Context;
import org.hip.kernel.servlet.HtmlView;
import org.hip.kernel.servlet.impl.DynamicHtmlView;
import org.junit.Test;

/**
 * This test needs an html-File VIFTest.html in the subdirectory de (according to vif.properties)
 * ./de/VIFTest.html
 *
 * @author: Benno Luthiger
 */
public class HtmlViewTest {

	@Test
	public void testDo() throws Exception {
		Context lContext = new TestContext();
		TestHtmlView lView = new TestHtmlView(lContext);
		TestPrintWriter lWriter = new TestPrintWriter();
		lView.renderToWriter(lWriter, "");

		char lCR = (char)13;
		char lLF = (char)10;
		String lExpected = "No HTML for view!" + lCR + lLF;
		assertEquals("stream 1", lExpected, lWriter.getStreamedText());
		lWriter.close();
		lWriter = new TestPrintWriter();
	
		lView.setHTMLString(lView.getReadHTML());
		lView.renderToWriter(lWriter, "");
		lExpected = FileReadWriteUtility.readFile(new File("de/VIFTest.html"));
		assertEquals("stream 2", lExpected.substring(0, lExpected.length()-2), lWriter.getStreamedText());	
	}

	@Test
	public void testReadHTML() {
		Context lContext = new TestContext();
		TestHtmlView lView = new TestHtmlView(lContext);
		String lExpected = FileReadWriteUtility.readFile(new File("de/VIFTest.html"));
		assertEquals("html-file", lExpected.substring(0, lExpected.length()-2), lView.getReadHTML());
	}
	
	@Test
	public void testEquals() throws Exception {
		HtmlView lView1 = new DynamicHtmlView("");
		HtmlView lView2 = new TestHtmlPage();
		HtmlView lView3 = new DynamicHtmlView("");
		
		assertFalse("[0] not equal", lView1.equals(this));
		assertFalse("[1] not equal 1:2", lView1.equals(lView2));
		assertFalse("[1] not equal 2:1", lView2.equals(lView1));
		assertTrue("[1] equal 1:3", lView1.equals(lView3));
		assertFalse("[1] not equal 2:3", lView2.equals(lView3));
		
		lView1 = new DynamicHtmlView("<p>Test1</p>");
		lView3 = new DynamicHtmlView("<p>Test2</p>");
		
		assertFalse("[2] not equal 1:3", lView1.equals(lView3));

		lView3 = new DynamicHtmlView("<p>Test1</p>");
		assertTrue("[3] equal 1:3", lView1.equals(lView3));
	}
	
}
