package org.hip.vif.markup.html;

import static org.junit.Assert.*;

import org.hip.vif.markup.TextParser;
import org.junit.Test;

/**
 * Test to control the full cycle starting from html converting to textile and converting back the html.
 * 
 * @author Luthiger
 * Created: 17.07.2011
 */
public class FullCycleTest {

	@Test
	public void test1() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		TextParser lParser = new TextParser();
		
		String lInput = "<h1>Hello <strong>world</strong>!</h1>";
		assertEquals("<h1 id=\"Helloworld\">Hello <strong>world</strong>!</h1>", lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "<h1>Hello world!</h1><p>Welcome to my home page.</p>";
		String lExpected = "<h1 id=\"Helloworld\">Hello world!</h1><p>Welcome to my home page.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "I <em>believe</em> every word.";
		lExpected = "<p>I <em>believe</em> every word.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "I <i>know</i>. I <b>really</b> <i>know</i>.";
		lExpected = "<p>I <em>know</em>. I <strong>really</strong> <em>know</em>.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "<cite>Cat&#8217;s Cradle</cite> by Vonnegut";
		lExpected = "<p><cite>Cat’s Cradle</cite> by Vonnegut</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "Convert with <code>r.to_html</code>";
		lExpected = "<p>Convert with <code>r.to_html</code></p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "I&#8217;m <del>sure</del> not sure.";
		lExpected = "<p>I’m <del>sure</del> not sure.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "You are a <ins>pleasant</ins> child.";
		lExpected = "<p>You are a <ins>pleasant</ins> child.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "a <sup>2</sup> + b <sup>2</sup> = c <sup>2</sup>";
		lExpected = "<p>a <sup>2</sup> + b <sup>2</sup> = c <sup>2</sup></p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "log <sub>2</sub> x";
		lExpected = "<p>log <sub>2</sub> x</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "<blockquote>A block quotation.</blockquote>";
		lExpected = "<blockquote><p>A block quotation.</p></blockquote>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "Text on one<br/>and the next line.";
		lExpected = "<p>Text on one<br/> and the next line.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
	}
	
	@Test
	public void test2() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		TextParser lParser = new TextParser();
		
		String lInput = "<ol><li>A first item</li><li>A second item</li><li>A third</li></ol>";
		assertEquals(lInput, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "<ol><li>Fuel could be:<ol><li>Coal</li><li>Gasoline</li><li>Electricity</li></ol></li><li>Humans need only:<ol><li>Water</li><li>Protein</li></ol></li></ol>";
		assertEquals(lInput, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "<ul><li>A first item</li><li>A second item</li><li>A third</li></ul>";
		assertEquals(lInput, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "<ul><li>Fuel could be:<ul><li>Coal</li><li>Gasoline</li><li>Electricity</li></ul></li><li>Humans need only:<ul><li>Water</li><li>Protein</li></ul></li></ul>";
		assertEquals(lInput, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
	}
	
	@Test
	public void test3() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		TextParser lParser = new TextParser();

		String lInput = "<table><tr><td><b>11</b></td><td>12</td><td><i>13</i></td></tr><tr><td>21</td><td><b><i>22</i></b></td><td>23</td></tr><tr><td><i>31</i></td><td>32</td><td><b>33</b></td></tr></table>";
		String lExpected = "<table><tr><td><strong>11</strong></td><td>12</td><th>13_</th></tr><tr><td>21</td><td><strong><em>22</em></strong></td><td>23</td></tr><tr><th>31_</th><td>32</td><td><strong>33</strong></td></tr></table>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "<a href=\"http://www.world.org/\">the world</a>";
		lExpected = "<a href=\"http://www.world.org/\">the world</a> ";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));

		lInput = "<img src=\"http://www.world.org/universe.jpg\"/>";
		lExpected = "<img border=\"0\" src=\"http://www.world.org/universe.jpg\"/> ";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
	}
	
	@Test
	public void test4() throws Exception {
		HtmlToTextileConverter lConverter = new HtmlToTextileConverter();
		TextParser lParser = new TextParser();

		String lInput = "left<br><div style=\"text-align: center;\">middle<br></div><div style=\"text-align: right;\">right<br></div>And further text.<br>";
		String lExpected = "<p>left</p><p style=\"text-align: center;\">middle</p><p style=\"text-align: right;\">right</p><p>And further text.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "left<br><p style=\"text-align: center;\">middle<br></p><p style=\"text-align: right;\">right<br></p>And further text.<br>";
		lExpected = "<p>left</p><p style=\"text-align: center;\">middle</p><p style=\"text-align: right;\">right</p><p>And further text.</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "text 1<br><div style=\"margin-left: 80px;\">text 2<br></div>text 3<br><div style=\"margin-left: 40px;\">text 4<br></div>text 5<br>";
		lExpected = "<p>text 1</p><p style=\"padding-left: 2em;\">text 2</p><p>text 3</p><p style=\"padding-left: 1em;\">text 4</p><p>text 5</p>";
		assertEquals(lExpected, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
		lInput = "<p>text 1</p><p style=\"padding-left: 2em;\">text 2</p><p>text 3</p><p style=\"padding-left: 1em;\">text 4</p><p>text 5</p>";
		assertEquals(lInput, lParser.parseToHtml(lConverter.convertToTextile(lInput)));
		
//		System.out.println(lParser.parseToHtml(lConverter.convertToTextile(lInput)));
	}

}
