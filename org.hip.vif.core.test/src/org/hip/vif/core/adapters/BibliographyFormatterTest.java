package org.hip.vif.core.adapters;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Locale;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.bom.IBibliography;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextImpl;
import org.hip.vif.core.service.ApplicationData;
import org.hip.vif.core.util.BibliographyFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 24.06.2010
 */
public class BibliographyFormatterTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
		ApplicationData.initLocale(Locale.ENGLISH);
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testRenderPlain() throws Exception {
		//book
		Long lTextID = createText1("The story of all", "Doe, Jane", TextImpl.TextType.BOOK.getTypeValue());
		IBibliography lText = retrieveText(lTextID);
		
		BibliographyFormatter lFormatter = new BibliographyFormatter(lText);
		assertEquals("book full", "Doe, Jane and Goldman, R. (2010). The story of all. About the subtitle. Hershey: IGI Publishing.", lFormatter.renderPlain());
		
		//article
		lTextID = createText1("The story of all", "Foo, Jane", TextImpl.TextType.ARTICLE.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("article full", "Foo, Jane and Goldman, R. (2010). \"The story of all\". NZZ 12:8, 44-55.", lFormatter.renderPlain());
		
		//contribution
		lTextID = createText1("The story of all", "Fane, Jane", TextImpl.TextType.CONTRIBUTION.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("contribution full", "Fane, Jane (2010). \"The story of all\", in NZZ. Eds. Goldman, R., pp. 44-55. Hershey: IGI Publishing.", lFormatter.renderPlain());
		
		//contribution 2
		ITextValues lValues = new DataHouseKeeper.TextValues("Fun and Software Development", "Luthiger, Benno", "Scotto, Marco, Giancarlo Succi", "", "2005", 
				"Proceedings of the 1st International Conference on Open Source Systems", "", "", "", "ECIG", 
				"Genova", "inText", TextImpl.TextType.CONTRIBUTION.getTypeValue());
		lTextID = createText(lValues);
		lText = retrieveText(lTextID);
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("contribution partially", "Luthiger, Benno (2005). \"Fun and Software Development\", in Proceedings of the 1st International Conference on Open Source Systems. Eds. Scotto, Marco, Giancarlo Succi. Genova: ECIG.", lFormatter.renderPlain());
		
		//webpage
		lTextID = createText1("The story of all", "Mane, Jane", TextImpl.TextType.WEBPAGE.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("webpage full", "Mane, Jane and Goldman, R. (2010). \"The story of all. About the subtitle\", NZZ. (accessed Hershey)", lFormatter.renderPlain());
	}
	
	@Test
	public void testRenderPlainMinimal() throws Exception {
		//book
		Long lTextID = createText2("The story of all", "Doe, Jane", TextImpl.TextType.BOOK.getTypeValue());
		IBibliography lText = retrieveText(lTextID);
		
		BibliographyFormatter lFormatter = new BibliographyFormatter(lText);
		assertEquals("book minimal", "Doe, Jane (2010). The story of all.", lFormatter.renderPlain());
		
		//article
		lTextID = createText2("The story of all", "Foo, Jane", TextImpl.TextType.ARTICLE.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("article minimal", "Foo, Jane (2010). \"The story of all\".", lFormatter.renderPlain());
		
		//contribution
		lTextID = createText2("The story of all", "Fane, Jane", TextImpl.TextType.CONTRIBUTION.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("contribution minimal", "Fane, Jane (2010). \"The story of all\".", lFormatter.renderPlain());
		
		//webpage
		lTextID = createText2("The story of all", "Mane, Jane", TextImpl.TextType.WEBPAGE.getTypeValue());
		lText = retrieveText(lTextID);
		
		lFormatter = new BibliographyFormatter(lText);
		assertEquals("webpage minimal", "Mane, Jane (2010). \"The story of all\".", lFormatter.renderPlain());		
	}

	private IBibliography retrieveText(Long lTextID) throws VException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, new Long(lTextID));
		ReadOnlyDomainObject lText = data.getJoinAuthorReviewerToTextHome().findByKey(lKey);
		return new BibliographyAdapter(lText, TextHome.KEY_BIBLIO_TYPE);
	}
	
	private Long createText1(String inTitle, String inAuthor, int inType) throws VException, SQLException {
		ITextValues lValues = new DataHouseKeeper.TextValues(inTitle, inAuthor, "Goldman, R.", "About the subtitle", "2010", "NZZ", "44-55", "12", 
															 "8", "IGI Publishing", "Hershey", "inText", inType);
		return createText(lValues);
	}
	
	private Long createText2(String inTitle, String inAuthor, int inType) throws VException, SQLException {
		return createText(new DataHouseKeeper.TextValues(inTitle, inAuthor, "", "", "2010", "", "", "", "", "", "", "inText", inType));
	}
	
	private Long createText(ITextValues inValues) throws VException, SQLException {
		Text lText = (Text) data.getTextHome().create();
		return lText.ucNew(inValues, new Long(12));
	}

}
