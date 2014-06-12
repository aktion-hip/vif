package org.hip.vif.core.util;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.adapters.BibliographyAdapter;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextImpl;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class BibliographyFormatterTest {

	private static DataHouseKeeper data;
	private static Long textID;

	@BeforeClass
	public static void init() throws Exception {
		data = DataHouseKeeper.getInstance();
		textID = data.createText("The First Book", "Riese, Adam");
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromText();
	}

	@Test
	public final void testRender() throws Exception {
		Text lText = data.getTextHome().getText(textID, 0);
		
		BibliographyFormatter lFormatter = new BibliographyFormatter(new BibliographyAdapter(lText, TextHome.KEY_TYPE));

		//book
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.BOOK.getTypeValue()));
		assertEquals("Riese, Adam (2010). The First Book. About the subtitle.", lFormatter.renderPlain());
		
		//article
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.ARTICLE.getTypeValue()));
		assertEquals("Riese, Adam (2010). \"The First Book\".  12:8, 44-55.", lFormatter.renderPlain());
		
		//contribution
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.CONTRIBUTION.getTypeValue()));
		assertEquals("Riese, Adam (2010). \"The First Book\".", lFormatter.renderPlain());
		
		//webpage
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.WEBPAGE.getTypeValue()));
		assertEquals("Riese, Adam (2010). \"The First Book. About the subtitle\".", lFormatter.renderPlain());
		
		//
		lText.set(TextHome.KEY_COAUTHORS, "Doe, Jane");
		lText.set(TextHome.KEY_PUBLICATION, "The weekly news");
		lText.set(TextHome.KEY_PUBLISHER, "Publishing House");
		lText.set(TextHome.KEY_PLACE, "The Moon");
		lText.set(TextHome.KEY_REMARK, "for testing purposes");
		lText.set(TextHome.KEY_REFERENCE, "reference");

		//book
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.BOOK.getTypeValue()));
		assertEquals("Riese, Adam and Doe, Jane (2010). The First Book. About the subtitle. The Moon: Publishing House.", lFormatter.renderPlain());
		
		//article
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.ARTICLE.getTypeValue()));
		assertEquals("Riese, Adam and Doe, Jane (2010). \"The First Book\". The weekly news 12:8, 44-55.", lFormatter.renderPlain());
		
		//contribution
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.CONTRIBUTION.getTypeValue()));
		assertEquals("Riese, Adam (2010). \"The First Book\", in The weekly news. Eds. Doe, Jane, pp. 44-55. The Moon: Publishing House.", lFormatter.renderPlain());
		
		//webpage
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.WEBPAGE.getTypeValue()));
		assertEquals("Riese, Adam and Doe, Jane (2010). \"The First Book. About the subtitle\", The weekly news. (accessed The Moon)", lFormatter.renderPlain());
		
		//in DE
		ApplicationData.initLocale(Locale.GERMAN);
		assertEquals("Riese, Adam und Doe, Jane (2010). \"The First Book. About the subtitle\", The weekly news. (Zugriff The Moon)", lFormatter.renderPlain());
		
	}

}
