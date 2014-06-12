package org.hip.vif.web.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Luthiger
 * Created: 15.01.2012
 */
public class RichTextSanitizerTest {
	private static final String NL = System.getProperty("line.separator");

	@Test
	public final void testSanitize() {
		String lDoNothing = "<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>";
		String lTest1 = "para 1<p>para 2</p><p>para 3</p><p>para 4</p>";
		String lTest2 = "<p>para 1</p><p>para 2</p><p>para 3</p>para 4";
		String lTest3 = "para 1<p>para 2</p><p>para 3</p>para 4";
		String lTest4 = "para 0";
		String lTest5 = "<p>para 1</p><p>para 2</p><p></p><p>  <br /> \r\n  <br/></p><p style=\"centered;\">  <br /> \r\n  <br/></p><p>para 3</p>para 4";
		String lTest6 = "<p>para 1</p><p>  <br /> \r\n  <br/>\r\n  <br/></p><p style=\"centered;\">para 2</p>";
		
		String lOut = RichTextSanitizer.sanitize(lDoNothing);
		assertEquals("<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize(lTest1);
		assertEquals("<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize(lTest2);
		assertEquals("<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>", lOut);

		lOut = RichTextSanitizer.sanitize(lTest3);
		assertEquals("<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize(lTest4);
		assertEquals("<p>para 0</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize(lTest5);
		assertEquals("<p>para 1</p><p>para 2</p><p>para 3</p><p>para 4</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize(lTest6);
		assertEquals("<p>para 1</p><p style=\"centered;\">para 2</p>", lOut);
		
		lOut = RichTextSanitizer.sanitize("");
		assertEquals("", lOut);
	}
	
	@Test
	public void testCheckInputEmpty() throws Exception {
		String lEmptyNL = "   <br>  &nbsp; " + NL + "  <br> " + NL + "&nbsp;";
		String lEmpty = "   <br>  &nbsp;   <br> ";
		String lNotEmpty = "   <br>.  &nbsp;   <br> ";
		
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmptyNL));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmpty));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmpty));
		
		String lEmptyPar1 = "<p style=\"some\">" + lEmptyNL + "</p>";
		String lEmptyDiv1 = "<div style=\"some\">" + lEmptyNL + "</div>";
		String lNotEmptyPar1 = "<p style=\"some\">" + lNotEmpty + "</p>";
		String lNotEmptyDiv1 = "<div style=\"some\">" + lNotEmpty + "</div>";
		
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmptyPar1));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmptyDiv1));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmptyPar1));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmptyDiv1));
		
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmpty + lEmptyPar1));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmptyPar1 + lEmpty));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmpty + lEmptyPar1 + lEmpty));
		
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmpty + lEmptyDiv1));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmptyDiv1 + lEmpty));
		assertTrue(RichTextSanitizer.checkInputEmpty(lEmpty + lEmptyDiv1 + lEmptyPar1 + lEmpty));
		
		assertFalse(RichTextSanitizer.checkInputEmpty(lEmpty + lNotEmptyPar1));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmptyPar1 + lEmpty));
		assertFalse(RichTextSanitizer.checkInputEmpty(lEmpty + lNotEmptyPar1 + lEmpty));
		
		assertFalse(RichTextSanitizer.checkInputEmpty(lEmpty + lNotEmptyDiv1));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmptyDiv1 + lEmpty));
		assertFalse(RichTextSanitizer.checkInputEmpty(lEmpty + lNotEmptyDiv1 + lEmpty));
		
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmpty + lEmptyPar1));
		assertFalse(RichTextSanitizer.checkInputEmpty(lEmptyPar1 + lNotEmpty));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmpty + lEmptyPar1 + lNotEmpty));
		assertFalse(RichTextSanitizer.checkInputEmpty(lNotEmpty + lNotEmptyPar1 + lNotEmpty));
	}
	
	@Test
	public void testToPara() throws Exception {
		String lInput = "<div>test 1</div><div style=\"testing;\">test 2</div>";
		assertEquals("<p>test 1</p><p style=\"testing;\">test 2</p>", RichTextSanitizer.toPara(lInput));
		assertEquals("", RichTextSanitizer.toPara(""));
		assertEquals("no change", RichTextSanitizer.toPara("no change"));
	}
	
	@Test
	public void testRemovePara() throws Exception {
		String lInput = "<p>test 1 \r\nline2</p>";
		assertEquals("test 1 \r\nline2", RichTextSanitizer.removePara(lInput));

		lInput = "<p style\"testing;\">test 1 \r\nline2</p>";
		assertEquals("test 1 \r\nline2", RichTextSanitizer.removePara(lInput));
		
		assertEquals("", RichTextSanitizer.removePara(""));
		assertEquals("no change", RichTextSanitizer.removePara("no change"));
	}
	
}
