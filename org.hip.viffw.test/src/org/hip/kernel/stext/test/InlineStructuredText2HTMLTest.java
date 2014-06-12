package org.hip.kernel.stext.test;

import static org.junit.Assert.*;
import org.hip.kernel.stext.InlineStructuredText2HTML;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class InlineStructuredText2HTMLTest {

	@Test
	public void testConvertToHTML() {
		String lInput1 = "*Bemerkung:* _LDAPUserFolder_ ben�tigt (*als LDAP-Client*) das *python-ldap Modul*. Damit dieses **mit _SSL_ arbeitet**, m�ssen die richtigen Bibliotheken eingebunden werden, was die *korrekte Installation* dieser Bibliotheken voraussetzt.";
		String lExpected1 = "<em>Bemerkung:</em> <u>LDAPUserFolder</u> ben�tigt (<em>als LDAP-Client</em>) das <em>python-ldap Modul</em>. Damit dieses <strong>mit <u>SSL</u> arbeitet</strong>, m�ssen die richtigen Bibliotheken eingebunden werden, was die <em>korrekte Installation</em> dieser Bibliotheken voraussetzt.";

		String lInput2 = "xx _this is html_ xx";
		String lExpected2 = "xx <u>this is html</u> xx";

		String lInput3 = "xx _this is html_";
		String lExpected3 = "xx <u>this is html</u>";

		String lInput4 = "xx *this is html* xx";
		String lExpected4 = "xx <em>this is html</em> xx";

		String lInput5 = "xx **this is html** xx";
		String lExpected5 = "xx <strong>this is html</strong> xx";

		String lInput6 = "<a href=\"index_html\">index_html</a>";
		String lExpected6 = "<a href=\"index_html\">index_html</a>";

		String lInput7 = "def __init__(self)";
		String lExpected7 = "def __init__(self)";

		String lInput8 = "this is '__a_literal__' eh";
		String lExpected8 = "this is <code>__a_literal__</code> eh";

		String lInput9 = "Note: structured_text is sometimes a night_mare";
		String lExpected9 = "Note: structured_text is sometimes a night_mare";

		String lInput10 = "xx 'this is literal' xx";
		String lExpected10 = "xx <code>this is literal</code> xx";

		assertEquals("convert 1", lExpected1, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput1)));
		assertEquals("convert 2", lExpected2, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput2)));
		assertEquals("convert 3", lExpected3, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput3)));
		assertEquals("convert 4", lExpected4, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput4)));
		assertEquals("convert 5", lExpected5, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput5)));
		assertEquals("convert 6", lExpected6, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput6)));
		assertEquals("convert 7", lExpected7, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput7)));
		assertEquals("convert 8", lExpected8, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput8)));
		assertEquals("convert 9", lExpected9, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput9)));
		assertEquals("convert 10", lExpected10, new String(InlineStructuredText2HTML.getSingleton().convertToHTML(lInput10)));
	}

}
