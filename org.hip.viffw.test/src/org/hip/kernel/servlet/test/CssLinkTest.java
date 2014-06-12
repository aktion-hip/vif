package org.hip.kernel.servlet.test;

import static org.junit.Assert.*;
import org.hip.kernel.servlet.impl.CssLink;
import org.hip.kernel.servlet.impl.CssLinkList;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class CssLinkTest {

	@Test
	public void testCreate() {
		CssLink lCssLink = new CssLink("de/css/");
		assertNotNull("not null 1", lCssLink);
		String lExpected = "<link rel=\"stylesheet\" media=\"all\" type=\"text/css\" href=\"./de/css/\"/>";
		assertEquals("toString 1", lExpected, lCssLink.toString());
	
		CssLink lCssLink2 = new CssLink("de/css/", "screen");
		assertNotNull("not null 2", lCssLink2);
		lExpected = "<link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"./de/css/\"/>";
		assertEquals("toString 2", lExpected, lCssLink2.toString());
	}
	
	@Test
	public void testEquals() {
		CssLink lCssLink1 = new CssLink("de/css/");
		CssLink lCssLink2 = new CssLink("de/css/", "screen");
		CssLink lCssLink3 = new CssLink("de/css/");
	
		assertTrue("equals", lCssLink1.equals(lCssLink3));
		assertEquals("equal hash code", lCssLink1.hashCode(), lCssLink3.hashCode());
		assertTrue("not equals", !lCssLink1.equals(lCssLink2));
		assertTrue("not equal hash code", lCssLink1.hashCode() != lCssLink2.hashCode());
	}
	
	@Test
	public void testLinks() {
		CssLink lCssLink1 = new CssLink("de/css/");
		CssLink lCssLink2 = new CssLink("de/css/", "screen");
		CssLink lCssLink3 = new CssLink("de/css/");
	
		String lExpected = "";
	
		//create list
		CssLinkList lList = new CssLinkList();
		assertEquals("toString 0", lExpected, lList.toString());
	
		//add first CssLink
		lList.addCssLink(lCssLink1);
		lExpected = "<link rel=\"stylesheet\" media=\"all\" type=\"text/css\" href=\"./de/css/\"/>\n";
		assertEquals("toString 1", lExpected, lList.toString());
		
		//add second CssLink
		lList.addCssLink(lCssLink2);
		lExpected = "<link rel=\"stylesheet\" media=\"all\" type=\"text/css\" href=\"./de/css/\"/>\n" +
			"<link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"./de/css/\"/>\n";
		assertEquals("toString 2", lExpected, lList.toString());
	
		//add third CssLink which is equal to first, therefore, nothing is added
		lList.addCssLink(lCssLink3);
		assertEquals("toString 3", lExpected, lList.toString());
		
		//add first CssLink again, therefore, nothing is added
		lList.addCssLink(lCssLink1);
		assertEquals("toString 4", lExpected, lList.toString());
	}
}
