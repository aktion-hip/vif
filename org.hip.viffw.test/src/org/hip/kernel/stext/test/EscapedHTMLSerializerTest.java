package org.hip.kernel.stext.test;

import static org.junit.Assert.*;
import org.hip.kernel.stext.EscapedHTMLSerializer;
import org.hip.kernel.stext.StructuredText;
import org.hip.kernel.stext.StructuredTextGenerator;
import org.hip.kernel.stext.StructuredTextSerializer;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class EscapedHTMLSerializerTest {
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
		"&lt;p&gt;This is an example of a text formatted according to structured text rules.&lt;/p&gt;" + NL +
		"&lt;p&gt;This is the next paragraph.&lt;/p&gt;" + NL +
		"&lt;p&gt;Here comes a bulleted list:&lt;/p&gt;" + NL +
		"&lt;ul&gt;&lt;li&gt;&lt;p&gt;List element 1&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;List element 2&lt;/p&gt;" + NL +
		"&lt;p&gt;Following paragraph in the same list element.&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;This is the last list element&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;/ul&gt;" + NL +
		"&lt;p&gt;Here's a new paragraph.&lt;/p&gt;" + NL +
		"&lt;p&gt;Let's now test a nubered list:&lt;/p&gt;" + NL +
		"&lt;ol&gt;&lt;li&gt;&lt;p&gt;List element 1&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;List element 2&lt;/p&gt;" + NL +
		"&lt;p&gt;Following paragraph in the same list element.&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;This is the last list element&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;/ol&gt;" + NL +
		"&lt;p&gt;This is the end (of the first example).&lt;/p&gt;" + NL;	

	public final static String INPUT2= 
		"This is an example of a text formatted according to structured text rules." + NL + NL +
		"This is the next paragraph." + NL + NL +
		"Here comes a bulleted list:" + NL + NL +
		"o List element 1" + NL + NL +
		"o List element 2" + NL + NL +
		"  Following paragraph in the _same list_ element." + NL + NL +
		"o This is the last list element" + NL + NL +
		"**Here's a new paragraph**." + NL + NL +
		"Let's now test a nubered list:" + NL + NL +
		"5 List element 1" + NL + NL +
		"5 List element 2" + NL + NL +
		"  Following paragraph in the same list element." + NL + NL +
		"5 This is the last list element" + NL + NL +
		"This is the end (of the first example).";
		
	public final static String EXPECTED2 = 
		"&lt;p&gt;This is an example of a text formatted according to structured text rules.&lt;/p&gt;" + NL +
		"&lt;p&gt;This is the next paragraph.&lt;/p&gt;" + NL +
		"&lt;p&gt;Here comes a bulleted list:&lt;/p&gt;" + NL +
		"&lt;ul&gt;&lt;li&gt;&lt;p&gt;List element 1&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;List element 2&lt;/p&gt;" + NL +
		"&lt;p&gt;Following paragraph in the &lt;u&gt;same list&lt;/u&gt; element.&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;This is the last list element&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;/ul&gt;" + NL +
		"&lt;p&gt;&lt;strong&gt;Here's a new paragraph&lt;/strong&gt;.&lt;/p&gt;" + NL +
		"&lt;p&gt;Let's now test a nubered list:&lt;/p&gt;" + NL +
		"&lt;ol&gt;&lt;li&gt;&lt;p&gt;List element 1&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;List element 2&lt;/p&gt;" + NL +
		"&lt;p&gt;Following paragraph in the same list element.&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;li&gt;&lt;p&gt;This is the last list element&lt;/p&gt;&lt;/li&gt;" + NL +
		"&lt;/ol&gt;" + NL +
		"&lt;p&gt;This is the end (of the first example).&lt;/p&gt;" + NL;	

	@Test
	public void testDo() {
		StructuredText lStructuredText = StructuredTextGenerator.getSingleton().createStructuredText(INPUT1);
		StructuredTextSerializer lSerializer = new EscapedHTMLSerializer();
		lStructuredText.accept(lSerializer);
		assertEquals("simple paragraphs", EXPECTED1, lSerializer.toString());
		
		lStructuredText = StructuredTextGenerator.getSingleton().createStructuredText(INPUT2);
		lSerializer = new EscapedHTMLSerializer();
		lStructuredText.accept(lSerializer);
		assertEquals("paragraphs with inline format", EXPECTED2, lSerializer.toString());		
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
