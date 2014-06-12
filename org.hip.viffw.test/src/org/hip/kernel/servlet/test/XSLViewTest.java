package org.hip.kernel.servlet.test;

import static org.junit.Assert.*;
import org.hip.kernel.servlet.Context;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class XSLViewTest {
	private static final String NL = "" + (char)13 + (char)10;
	private final static String EXPECTED = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><transformed><Root>" + NL +
		"<firstLine>This is the first line.</firstLine>" + NL +
		"<secondLine>This line follows the first.</secondLine>" + NL +
		"</Root></transformed>";
	private final static String EXPECTED1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><transformed><Root>" + NL +
		"<firstLine>This is the first line.</firstLine>" + NL +
		"<secondLine>This line follows the first.</secondLine>" + NL +
		"</Root><added/></transformed>";
	private final static String EXPECTED2 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><transformed><Root>" + NL +
		"<firstLine>This is the first line.</firstLine>" + NL +
		"<secondLine>This line follows the first.</secondLine>" + NL +
		"</Root><added>;jsessionid=10101</added></transformed>";
		
	@Test
	public void testRender() throws Exception {
		Context lContext = new TestContext();
		TestXSLView lView = new TestXSLView(lContext);
		TestPrintWriter lWriter = new TestPrintWriter();

		lView.renderToWriter(lWriter, "");
		assertEquals(EXPECTED, lWriter.getStreamedText());
	}
	
	@Test
	public void testRenderWithSessionID() throws Exception {
		Context lContext = new TestContext();
		TestXSLView lView = new TestXSLView(lContext, true);
		TestPrintWriter lWriter = new TestPrintWriter();

		lView.renderToWriter(lWriter, "");
		assertEquals(EXPECTED1, lWriter.getStreamedText());
		lWriter.close();
		lWriter = new TestPrintWriter();
		
		lView.renderToWriter(lWriter, "10101");
		assertEquals(EXPECTED2, lWriter.getStreamedText());
	}
	
}
