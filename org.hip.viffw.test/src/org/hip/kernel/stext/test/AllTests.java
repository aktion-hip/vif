package org.hip.kernel.stext.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EscapedHTMLSerializerTest.class, HTMLSerializerTest.class,
		IgnoringFormatSerializerTest.class,
		InlineStructuredText2HTMLTest.class, StructuredTextParagraphTest.class })
public class AllTests {

}
