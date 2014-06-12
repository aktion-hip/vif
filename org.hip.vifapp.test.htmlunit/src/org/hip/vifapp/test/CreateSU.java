package org.hip.vifapp.test;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

/**
 * @author Luthiger
 * Created 25.01.2009
 */
public class CreateSU extends VifTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateSU() throws Exception {
		final HtmlPage lPage = (HtmlPage)webClient.getPage(getRequestAdminMaster("createSU"));
		assertEquals("page title", "VIF", lPage.getTitleText());
		List<?> lParas = lPage.getByXPath("//p[@class='title2']");
		assertEquals("number of title paragraphs", 1, lParas.size());
		HtmlParagraph lTitleNode = (HtmlParagraph) lParas.get(0);
		assertEquals("title text", "Create the Super User entry", lTitleNode.asText());
		
		String[] lExpectedCells = {"User-ID:", "Password:", "Confirmation:", "Send"};
		List<?> lTableCells = lPage.getByXPath("//td[@class='data']");
		assertEquals("number of data cells", 4, lTableCells.size());
		int i = 0;
		for (Object lCell : lTableCells) {
			assertEquals("cell value " + i, lExpectedCells[i++], ((HtmlTableDataCell)lCell).asText());
		}
		
		final HtmlForm lForm = lPage.getFormByName("Form");
		System.out.println(lForm.getInputByName("userId"));
		System.out.println(lForm.getInputByName("passwd"));
		System.out.println(lForm.getInputByName("passwd_confirm"));
		System.out.println(lForm.getInputByName("button"));
	}

}
