package org.hip.kernel.stext.test;

import static org.junit.Assert.*;
import org.hip.kernel.stext.HTMLSerializer;
import org.hip.kernel.stext.StructuredText;
import org.hip.kernel.stext.StructuredTextGenerator;
import org.hip.kernel.stext.StructuredTextSerializer;
import org.junit.Test;

/**
 * 
 * @author: Benno Luthiger
 */
public class HTMLSerializerTest {
	public final static String NL = System.getProperty("line.separator");
	public final static String INPUT1= 
		"This is an example of a text formatted according to structured text rules." + NL + NL +
		"This is the next paragraph." + NL + NL +
		"Here comes a bulleted list:" + NL + NL +
		"o List element 1" + NL + NL +
		"o List element 2" + NL + NL +
		"  Following paragraph in the same list element." + NL + NL +
		"o This is the last list element" + NL + NL +
		"Here's a new paragraph." + NL + NL +
		"Let's now test a nubered list:" + NL + NL +
		"5 List element 1" + NL + NL +
		"5 List element 2" + NL + NL +
		"  Following paragraph in the same list element." + NL + NL +
		"5 This is the last list element" + NL + NL +
		"This is the end (of the first example).";
		
	public final static String EXPECTED1 = 
		"<p>This is an example of a text formatted according to structured text rules.</p>" + NL +
		"<p>This is the next paragraph.</p>" + NL +
		"<p>Here comes a bulleted list:</p>" + NL +
		"<ul><li><p>List element 1</p></li>" + NL +
		"<li><p>List element 2</p>" + NL +
		"<p>Following paragraph in the same list element.</p></li>" + NL +
		"<li><p>This is the last list element</p></li>" + NL +
		"</ul>" + NL +
		"<p>Here's a new paragraph.</p>" + NL +
		"<p>Let's now test a nubered list:</p>" + NL +
		"<ol><li><p>List element 1</p></li>" + NL +
		"<li><p>List element 2</p>" + NL +
		"<p>Following paragraph in the same list element.</p></li>" + NL +
		"<li><p>This is the last list element</p></li>" + NL +
		"</ol>" + NL +
		"<p>This is the end (of the first example).</p>" + NL;	

	@Test
	public void testDo() {
		StructuredText lStructuredText = StructuredTextGenerator.getSingleton().createStructuredText(INPUT1);
		StructuredTextSerializer lSerializer = new HTMLSerializer();
		lStructuredText.accept(lSerializer);
		assertEquals("simple paragraphs", EXPECTED1, lSerializer.toString());
	}

//	public void testVisitStructuredText() {
//	}
//
//	public void testVisitStructuredTextBullet() {
//	}
//
//	public void testVisitStructuredTextNumbered() {
//	}
//
//	public void testVisitStructuredTextPlain() {
//	}
}
