package org.hip.vif.markup.html;


import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class HtmlToTextileConverterTest {
	private static final String NL = System.getProperty("line.separator");
	private static final Pattern WHITE_SPACE = Pattern.compile("\\s+");
	
	@Test
	public void testNormalizeSpace() throws Exception {
		assertEquals("<p>Hallo Velo<p>", normalizeSpace("<p>Hallo   Velo<p>"));
		assertEquals("<p>Hallo Velo<br />with many white spaces<p>",
				normalizeSpace("<p>Hallo   Velo<br  />with  many  white spaces<p>"));
	}
	private String normalizeSpace(String inText) {
		return WHITE_SPACE.matcher(inText).replaceAll(" ");
	}
	
	@Test
	public void testConvertToTextile() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		
		String lInput = "<h1>Hello <strong>world</strong>!</h1>";
		assertEquals("h1. Hello *world*!", lConverter.convertToTextile(lInput));
		
		lInput = "<h1>Hello world!</h1><p>Welcome to my home page.</p>";
		assertEquals("h1. Hello world!"+NL+NL+"p. Welcome to my home page.", 
				lConverter.convertToTextile(lInput));
		
		lInput = "Hallo <span style=\"font-style: italic;\">Velo</span>!";
		assertEquals("Hallo _Velo_!", 
				lConverter.convertToTextile(lInput));
		
		lInput = "I <em>believe</em> every word.";
		assertEquals("I _believe_ every word.", lConverter.convertToTextile(lInput));
		
		lInput = "I <i>know</i>. I <b>really</b> <i>know</i>.";
		assertEquals("I _know_. I *really* _know_.", lConverter.convertToTextile(lInput));
		
		lInput = "<cite>Cat&#8217;s Cradle</cite> by Vonnegut";
		assertEquals("??Cat’s Cradle?? by Vonnegut", lConverter.convertToTextile(lInput));
		
		lInput = "Convert with <code>r.to_html</code>";
		assertEquals("Convert with @r.to_html@", lConverter.convertToTextile(lInput));
		
		lInput = "I&#8217;m <del>sure</del> not sure.";
		assertEquals("I’m -sure- not sure.", lConverter.convertToTextile(lInput));
		
		lInput = "You are a <ins>pleasant</ins> child.";
		assertEquals("You are a +pleasant+ child.", lConverter.convertToTextile(lInput));
		
		lInput = "a <sup>2</sup> + b <sup>2</sup> = c <sup>2</sup>";
		assertEquals("a ^2^ + b ^2^ = c ^2^", lConverter.convertToTextile(lInput));

		lInput = "log <sub>2</sub> x";
		assertEquals("log ~2~ x", lConverter.convertToTextile(lInput));
		
		lInput = "<blockquote>A block quotation.</blockquote>";
		assertEquals("bq. A block quotation.", lConverter.convertToTextile(lInput));
		
		lInput = "Text on one<br/>and the next line.";
		String lExpected = "Text on one" +NL+
				" and the next line.";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "aaa <span style=\"font-weight: bold;\">bbb </span>ccc <span style=\"font-style: italic;\">ddd </span>eee <span style=\"text-decoration: underline;\">fff</span> ggg <span style=\"text-decoration: line-through;\">hhh</span> iii.<br>";
		lExpected = "aaa *bbb* ccc _ddd_ eee +fff+  ggg -hhh-  iii.";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
	}
	
	@Test
	public void testList() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		
		String lInput = "<ol><li>A first item</li><li>A second item</li><li>A third</li></ol>";
		String lExpected = "# A first item"+NL+"# A second item"+NL+"# A third";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "<ol><li>Fuel could be:<ol><li>Coal</li><li>Gasoline</li><li>Electricity</li></ol></li><li>Humans need only:<ol><li>Water</li><li>Protein</li></ol></li></ol>";
		lExpected = "# Fuel could be:" +NL+
				"## Coal" +NL+
				"## Gasoline" +NL+
				"## Electricity" +NL+
				"# Humans need only:" +NL+
				"## Water" +NL+
				"## Protein";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "<ul><li>A first item</li><li>A second item</li><li>A third</li></ul>";
		lExpected = "* A first item" +NL+
				"* A second item" +NL+
				"* A third";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "<ul><li>Fuel could be:<ul><li>Coal</li><li>Gasoline</li><li>Electricity</li></ul></li><li>Humans need only:<ul><li>Water</li><li>Protein</li></ul></li></ul>";
		lExpected = "* Fuel could be:" +NL+
				"** Coal" +NL+
				"** Gasoline" +NL+
				"** Electricity" +NL+
				"* Humans need only:" +NL+
				"** Water" +NL+
				"** Protein";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
	}

	@Test
	public void testTable() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		String lInput = "<table><tr><td><b>11</b></td><td>12</td><td><i>13</i></td></tr><tr><td>21</td><td><b><i>22</i></b></td><td>23</td></tr><tr><td><i>31</i></td><td>32</td><td><b>33</b></td></tr></table>";
		String lExpected = "|*11*|12|_13_|" +NL+
				"|21|*_22_*|23|" +NL+
				"|_31_|32|*33*|";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));

		lInput = "<a href=\"http://www.world.org/\">the world</a>";
		lExpected = "\"the world\":http://www.world.org/";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "<img src=\"http://www.world.org/universe.jpg\"/>";
		lExpected = "!http://www.world.org/universe.jpg!";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
	}
	
	@Test
	public void testIndent() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		String lInput = "left<br><div style=\"text-align: center;\">middle<br></div><div style=\"text-align: right;\">right<br></div>And further text.<br>";
		String lExpected = "left" +NL+NL+
				"p=. middle" +NL+NL+
				"p>. right" +NL+NL+
				"And further text.";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));

		lInput = "left<br><p style=\"text-align: center;\">middle<br></p><p style=\"text-align: right;\">right<br></p>And further text.<br>";
		lExpected = "left" +NL+NL+
		"p=. middle" +NL+NL+
		"p>. right" +NL+NL+
		"And further text.";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));

		lInput = "text 1<br><div style=\"margin-left: 40px;\">text 2<br></div>text 3<br><br>";
		lExpected = "text 1" +NL+NL+
				"p(. text 2" +NL+NL+
				"text 3";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
		lInput = "text 1<br><div style=\"margin-left: 80px;\">text 2<br></div>text 3<br><div style=\"margin-left: 40px;\">text 4<br></div>text 5<br>";
		lExpected = "text 1" +NL+NL+
		"p((. text 2" +NL+NL+
		"text 3" +NL+NL+
		"p(. text 4" +NL+NL+
		"text 5";
		assertEquals(lExpected, lConverter.convertToTextile(lInput));
		
	}

}
