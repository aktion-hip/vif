package org.hip.kernel.stext.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hip.kernel.stext.HTMLSerializer;
import org.hip.kernel.stext.StructuredTextBullet;
import org.hip.kernel.stext.StructuredTextIndented;
import org.hip.kernel.stext.StructuredTextNumbered;
import org.hip.kernel.stext.StructuredTextParagraph;
import org.hip.kernel.stext.StructuredTextPlain;
import org.hip.kernel.stext.StructuredTextSerializer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luthiger
 */
public class StructuredTextParagraphTest {
	private final static String NL = System.getProperty("line.separator");
	private final static String PARAGRAPH1 = "Test paragraph.";
	private final static String PARAGRAPH2 = "Another paragraph.";
	
	private StructuredTextBullet structuredTextBullet;
	private StructuredTextNumbered structuredTextNumbered;
	private StructuredTextPlain structuredTextPlain;
	private StructuredTextIndented structuredTextIndented;

	@Before
	public void setUp() throws Exception {
		structuredTextBullet = new StructuredTextBullet(PARAGRAPH1);
		structuredTextNumbered = new StructuredTextNumbered(PARAGRAPH1);
		structuredTextPlain = new StructuredTextPlain(PARAGRAPH1);
		structuredTextIndented = new StructuredTextIndented(PARAGRAPH1);
	}

	@Test
	public void testGetRawString() {
		String lExpected = "Test paragraph.";
		assertEquals("raw 1", lExpected, structuredTextBullet.getRawString());
		assertEquals("raw 2", lExpected, structuredTextNumbered.getRawString());
		assertEquals("raw 3", lExpected, structuredTextPlain.getRawString());
		assertEquals("raw 4", lExpected, structuredTextIndented.getRawString());
	}
	
	@Test
	public void testAccept() {
		String lExpected1 = "<ul><li><p>Test paragraph.</p></li>" + NL + "</ul>" + NL;
		String lExpected2 = "<ol><li><p>Test paragraph.</p></li>" + NL + "</ol>" + NL;
		String lExpected3 = "<p>Test paragraph.</p>" + NL;
		
		StructuredTextSerializer lSerializer = new HTMLSerializer();
		structuredTextBullet.accept(lSerializer);
		assertEquals("serialized 1", lExpected1, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextNumbered.accept(lSerializer);
		assertEquals("serialized 2", lExpected2, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextPlain.accept(lSerializer);
		assertEquals("serialized 3", lExpected3, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextIndented.accept(lSerializer);
		assertEquals("serialized 4", lExpected3, lSerializer.toString());
	}
	
	@Test
	public void testEqualsType() {
		StructuredTextBullet lStructuredTextBullet1 = new StructuredTextBullet(PARAGRAPH1);
		StructuredTextBullet lStructuredTextBullet2 = new StructuredTextBullet(PARAGRAPH2);
		assertTrue("equals 1", structuredTextBullet.equalsType(lStructuredTextBullet1));
		assertTrue("equals 2", structuredTextBullet.equalsType(lStructuredTextBullet2));
		
		assertFalse("not equals 1", structuredTextBullet.equalsType(structuredTextPlain));
		assertFalse("not equals 2", structuredTextPlain.equalsType(structuredTextIndented));
		assertFalse("not equals 3", structuredTextBullet.equalsType(structuredTextNumbered));
	}
	
	@Test
	public void testGetParagraphType() {
		assertEquals("type 1", StructuredTextParagraph.PARAGRAPH_BULLET, structuredTextBullet.getParagraphType());
		assertEquals("type 2", StructuredTextParagraph.PARAGRAPH_NUMBERED, structuredTextNumbered.getParagraphType());
		assertEquals("type 3", StructuredTextParagraph.PARAGRAPH_PLAIN, structuredTextPlain.getParagraphType());
		assertEquals("type 4", StructuredTextParagraph.PARAGRAPH_INDENTED, structuredTextIndented.getParagraphType());
	}
	
	@Test
	public void testAdd() {
		String lExpected1 = "<ul><li><p>Test paragraph.</p></li>" + NL + "<li><p>Another paragraph.</p></li>" + NL + "</ul>" + NL;
		String lExpected2 = "<ol><li><p>Test paragraph.</p></li>" + NL + "<li><p>Another paragraph.</p></li>" + NL + "</ol>" + NL;
		String lExpected3 = "<p>Test paragraph.</p>" + NL +  NL + "<p>Another paragraph.</p>" + NL;
		
		StructuredTextSerializer lSerializer = new HTMLSerializer();
		structuredTextBullet.add(PARAGRAPH2);
		structuredTextBullet.accept(lSerializer);
		assertEquals("added 1", lExpected1, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextNumbered.add(PARAGRAPH2);
		structuredTextNumbered.accept(lSerializer);
		assertEquals("added 2", lExpected2, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextPlain.add(PARAGRAPH2);
		structuredTextPlain.accept(lSerializer);
		assertEquals("added 3", lExpected3, lSerializer.toString());
		
		lSerializer = new HTMLSerializer();
		structuredTextIndented.add(PARAGRAPH2);
		structuredTextIndented.accept(lSerializer);
		assertEquals("added 4", lExpected3, lSerializer.toString());
	}
	
	@Test
	public void testToString() {
		String lExpected1 = "<org.hip.kernel.stext.StructuredTextBullet>\nTest paragraph.</org.hip.kernel.stext.StructuredTextBullet>";
		String lExpected2 = "<org.hip.kernel.stext.StructuredTextPlain>\nTest paragraph.</org.hip.kernel.stext.StructuredTextPlain>";
		String lExpected3 = "<org.hip.kernel.stext.StructuredTextIndented>\nTest paragraph.</org.hip.kernel.stext.StructuredTextIndented>";
		String lExpected4 = "<org.hip.kernel.stext.StructuredTextNumbered>\nTest paragraph.</org.hip.kernel.stext.StructuredTextNumbered>";
		
		assertEquals("to string 1", lExpected1, structuredTextBullet.toString());
		assertEquals("to string 2", lExpected2, structuredTextPlain.toString());
		assertEquals("to string 3", lExpected3, structuredTextIndented.toString());
		assertEquals("to string 4", lExpected4, structuredTextNumbered.toString());
	}
}
