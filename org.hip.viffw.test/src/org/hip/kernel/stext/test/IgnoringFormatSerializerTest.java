package org.hip.kernel.stext.test;

import static org.junit.Assert.*;
import org.hip.kernel.stext.IgnoringFormatSerializer;
import org.hip.kernel.stext.StructuredText;
import org.hip.kernel.stext.StructuredTextGenerator;
import org.hip.kernel.stext.StructuredTextSerializer;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class IgnoringFormatSerializerTest {
	public final static String NL = System.getProperty("line.separator");
	public final static String INPUT = 
		"This is an example of a text formatted according to structured text rules." + NL + NL +
		"This is the next paragraph." + NL + NL +
		"Here comes a bulleted list:" + NL + NL +
		"o List element 1" + NL + NL +
		"o List element 2" + NL + NL +
		"  Following paragraph in the *same list* element." + NL + NL +
		"o This is the last list element" + NL + NL +
		"**Here's a new paragraph**." + NL + NL +
		"Let's now test a numbered list:" + NL + NL +
		"5 List element 1" + NL + NL +
		"5 List element 2" + NL + NL +
		"  Following paragraph in the _same_ list element." + NL + NL +
		"  Further paragraph in the same list element." + NL + NL +
		"5 This is the last list element" + NL + NL +
		"This is the end (of the first example).";
	public final static String EXPECTED = 
		"This is an example of a text formatted according to structured text rules." + NL +
		"This is the next paragraph." + NL +
		"Here comes a bulleted list:" + NL +
		"List element 1" + NL +
		"List element 2" + NL +
		"Following paragraph in the same list element." + NL +
		"This is the last list element" + NL +
		"Here's a new paragraph." + NL +
		"Let's now test a numbered list:" + NL +
		"List element 1" + NL +
		"List element 2" + NL +
		"Following paragraph in the same list element." + NL +
		"Further paragraph in the same list element." + NL +
		"This is the last list element" + NL +
		"This is the end (of the first example)." + NL;

	@Test
	public void testDo() {
		StructuredText lStructuredText = StructuredTextGenerator.getSingleton().createStructuredText(INPUT);
		StructuredTextSerializer lSerializer = new IgnoringFormatSerializer();
		lStructuredText.accept(lSerializer);
		assertEquals("simple paragraphs", EXPECTED, lSerializer.toString());
	}
}
