package org.hip.vif.markup;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.05.2010
 */
public class TextParserTest {
	private static final String NL = System.getProperty("line.separator");
	
	private static final String[] INPUT = {
		"Text with _emphasis_ markup.",
		"Text with *strong* markup.",
		"Text with +underline+ markup.",
		"Text with @code@ markup.",
		"Text with *_bold italic_* markup.",
		"Text with -crossed out- markup.",
		"Text with *-bold crossed out-* markup.",
		"Text with +*bold underlined*+ markup.",
		"Text with +_italique souligné_+ markup.",
		"Text with +*_italic bold underlined_*+ markup.",
		"Text with %{color:red}Text in red % markup.",
		"Text with bulleted list\n* line 1\n* line 2\n* line 3",
		"Text with ordered list\n# line 1\n# line 2\n# line 3",
		"Text^superscript^ markup",
		"Text~subscript~ markup",
		"Text with \"Link to the VIF\":http://vif.sf.net/ web site",
		"Text with \"some\" special - characters.",
		"Some entities: a - dash, (tm) (c) (r) 2 x 2. Text 'with' apostrophe's example."
	};
	
	private static final String[] EXPECTED_HTML = {
		"<p>Text with <em>emphasis</em> markup.</p>",
		"<p>Text with <strong>strong</strong> markup.</p>",
		"<p>Text with <ins>underline</ins> markup.</p>",
		"<p>Text with <code>code</code> markup.</p>",
		"<p>Text with <strong><em>bold italic</em></strong> markup.</p>",
		"<p>Text with <del>crossed out</del> markup.</p>",
		"<p>Text with <strong><del>bold crossed out</del></strong> markup.</p>",
		"<p>Text with <ins><strong>bold underlined</strong></ins> markup.</p>",
		"<p>Text with <ins><em>italique souligné</em></ins> markup.</p>",
		"<p>Text with <ins><strong><em>italic bold underlined</em></strong></ins> markup.</p>",
		"<p>Text with %{color:red}Text in red % markup.</p>",
		"<p>Text with bulleted list</p><ul><li>line 1</li><li>line 2</li><li>line 3</li></ul>",
		"<p>Text with ordered list</p><ol><li>line 1</li><li>line 2</li><li>line 3</li></ol>",
		"<p>Text^superscript^ markup</p>",
		"<p>Text~subscript~ markup</p>",
		"<p>Text with <a href=\"http://vif.sf.net/\">Link to the VIF</a> web site</p>",
		"<p>Text with &#8222;some&#8221; special &#8211; characters.</p>",
		"<p>Some entities: a &#8211; dash, &#8482; &#169; &#174; 2 &#215; 2. Text &#8218;with&#8217; apostrophe&#8217;s example.</p>"
	};
	private static final String[] EXPECTED_PLAIN = {
		"Text with emphasis markup.",
		"Text with strong markup.",
		"Text with underline markup.",
		"Text with code markup.",
		"Text with bold italic markup.",
		"Text with crossed out markup.",
		"Text with bold crossed out markup.",
		"Text with bold underlined markup.",
		"Text with italique souligné markup.",
		"Text with italic bold underlined markup.",
		"Text with %{color:red}Text in red % markup.",
		"Text with bulleted list"+NL+"* line 1"+NL+"* line 2"+NL+"* line 3",
		"Text with ordered list"+NL+"1 line 1"+NL+"2 line 2"+NL+"3 line 3",
		"Text^superscript^ markup",
		"Text~subscript~ markup",
		"Text with Link to the VIF [http://vif.sf.net/] web site",
		"Text with \"some\" special - characters.",
		"Some entities: a - dash, (tm) (c) (r) 2 x 2. Text 'with' apostrophe's example."
	};

	@Test
	public void testParseToHtml() {
		TextParser lParser = new TextParser();
		int i = 0;
		for (String lInput : INPUT) {
			assertEquals("html "+i, EXPECTED_HTML[i++], lParser.parseToHtml(lInput));
		}
	}

	@Test
	public void testParesToPlain() throws IOException {
		TextParser lParser = new TextParser();
		int i = 0;
		for (String lInput : INPUT) {
			assertEquals("plain "+i, EXPECTED_PLAIN[i++], lParser.parseToPlain(lInput));
		}
	}

}
