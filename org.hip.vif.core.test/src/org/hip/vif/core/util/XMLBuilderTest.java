package org.hip.vif.core.util;

import static org.junit.Assert.assertEquals;

import org.hip.vif.core.ApplicationConstants;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 16.07.2010
 */
public class XMLBuilderTest {
	private static final String XML_PREFIX = ApplicationConstants.HEADER;

	@Test
	public void testDo() throws Exception {
		XMLBuilder lXML = new XMLBuilder();
		String lExpected = XML_PREFIX + "<Root></Root>";
		assertEquals("minimal", lExpected, lXML.toString());
		
		lXML = new XMLBuilder();
		lXML.inject(XMLBuilder.createNode("a", "AAA")).inject(XMLBuilder.createNode("b", "BBB"));
		lExpected = XML_PREFIX + "<Root><a>AAA</a><b>BBB</b></Root>";
		assertEquals("normal", lExpected, lXML.toString());

		lXML = new XMLBuilder();
		lXML.inject("a", "AAA").inject("b", "BBB");
		assertEquals("reduced", lExpected, lXML.toString());
	}

}
