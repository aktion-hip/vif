package org.hip.vif.markup.serializer;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.05.2010
 */
public class PlainTextDocumentBuilderTest {

	private Attributes attributes;
	private StringWriter out; 

	@Before
	public void setUp() throws Exception {
		attributes = new Attributes("id", "cssClass", "cssStyle", "language");
		out = new StringWriter();
	}
	
	@Test
	public void testAcronym() throws Exception {
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(out);
		lBuilder.acronym("text", "definition");
		assertEquals("acronym", "<acronym title=\"definition\">text</acronym>", out.toString());		
	}
	
	@Test
	public void testCharactersUnescaped() throws Exception {
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(out);
		String lExpected = "this is \"some\" text with Umlauts: 'üäö'";
		lBuilder.charactersUnescaped(lExpected);
		assertEquals("charactersUnescaped", lExpected, out.toString());		
	}
	
	@Test
	public void testEntity() throws Exception {
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(out);
		lBuilder.entityReference("name");
		assertEquals("entityReference", "'name'", out.toString());		
	}
	
	@Test
	public void testBR() throws Exception {
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(out);
		lBuilder.lineBreak();
		assertEquals("lineBreak", System.getProperty("line.separator"), out.toString());
	}
	
	@Test
	public void testLink() throws Exception {
		PlainTextDocumentBuilder lBuilder = new PlainTextDocumentBuilder(out);
		lBuilder.link(attributes, "hrefOrHashName", "text");
		assertEquals("link", "link:text", out.toString());
	}
	
}
