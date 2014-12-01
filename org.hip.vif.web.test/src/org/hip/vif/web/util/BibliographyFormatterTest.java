package org.hip.vif.web.util;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.ReadOnlyDomainObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.biblio.IBibliography;
import org.hip.vif.web.biblio.TextType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 24.06.2010 */
public class BibliographyFormatterTest {
    private static DataHouseKeeper data;

    @BeforeClass
    public static void init() throws VException {
        data = DataHouseKeeper.getInstance();
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
    public final void testRender() throws Exception {
        final Long textID = data.createText("The First Book", "Riese, Adam");
        final Text lText = data.getTextHome().getText(textID, 0);

        final BibliographyFormatter lFormatter = new BibliographyFormatter(new BibliographyAdapter(lText,
                TextHome.KEY_TYPE));

        // book
        lText.set(TextHome.KEY_TYPE, new Long(TextType.BOOK.getTypeValue()));
        assertEquals("Riese, Adam (2010). The First Book. About the subtitle.", lFormatter.renderPlain());

        // article
        lText.set(TextHome.KEY_TYPE, new Long(TextType.ARTICLE.getTypeValue()));
        assertEquals("Riese, Adam (2010). \"The First Book\".  12:8, 44-55.", lFormatter.renderPlain());

        // contribution
        lText.set(TextHome.KEY_TYPE, new Long(TextType.CONTRIBUTION.getTypeValue()));
        assertEquals("Riese, Adam (2010). \"The First Book\".", lFormatter.renderPlain());

        // webpage
        lText.set(TextHome.KEY_TYPE, new Long(TextType.WEBPAGE.getTypeValue()));
        assertEquals("Riese, Adam (2010). \"The First Book. About the subtitle\".", lFormatter.renderPlain());

        //
        lText.set(TextHome.KEY_COAUTHORS, "Doe, Jane");
        lText.set(TextHome.KEY_PUBLICATION, "The weekly news");
        lText.set(TextHome.KEY_PUBLISHER, "Publishing House");
        lText.set(TextHome.KEY_PLACE, "The Moon");
        lText.set(TextHome.KEY_REMARK, "for testing purposes");
        lText.set(TextHome.KEY_REFERENCE, "reference");

        // book
        lText.set(TextHome.KEY_TYPE, new Long(TextType.BOOK.getTypeValue()));
        assertEquals(
                "Riese, Adam and Doe, Jane (2010). The First Book. About the subtitle. The Moon: Publishing House.",
                lFormatter.renderPlain());

        // article
        lText.set(TextHome.KEY_TYPE, new Long(TextType.ARTICLE.getTypeValue()));
        assertEquals("Riese, Adam and Doe, Jane (2010). \"The First Book\". The weekly news 12:8, 44-55.",
                lFormatter.renderPlain());

        // contribution
        lText.set(TextHome.KEY_TYPE, new Long(TextType.CONTRIBUTION.getTypeValue()));
        assertEquals(
                "Riese, Adam (2010). \"The First Book\", in The weekly news. Eds. Doe, Jane, pp. 44-55. The Moon: Publishing House.",
                lFormatter.renderPlain());

        // webpage
        lText.set(TextHome.KEY_TYPE, new Long(TextType.WEBPAGE.getTypeValue()));
        assertEquals(
                "Riese, Adam and Doe, Jane (2010). \"The First Book. About the subtitle\", The weekly news. (accessed The Moon)",
                lFormatter.renderPlain());

    }

    @Test
    public void testRenderPlain() throws Exception {
        // book
        Long lTextID = createText1("The story of all", "Doe, Jane", TextType.BOOK.getTypeValue());
        IBibliography lText = retrieveText(lTextID);

        BibliographyFormatter lFormatter = new BibliographyFormatter(lText);
        assertEquals("book full",
                "Doe, Jane and Goldman, R. (2010). The story of all. About the subtitle. Hershey: IGI Publishing.",
                lFormatter.renderPlain());

        // article
        lTextID = createText1("The story of all", "Foo, Jane", TextType.ARTICLE.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals("article full", "Foo, Jane and Goldman, R. (2010). \"The story of all\". NZZ 12:8, 44-55.",
                lFormatter.renderPlain());

        // contribution
        lTextID = createText1("The story of all", "Fane, Jane", TextType.CONTRIBUTION.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals(
                "contribution full",
                "Fane, Jane (2010). \"The story of all\", in NZZ. Eds. Goldman, R., pp. 44-55. Hershey: IGI Publishing.",
                lFormatter.renderPlain());

        // contribution 2
        final ITextValues lValues = new DataHouseKeeper.TextValues("Fun and Software Development", "Luthiger, Benno",
                "Scotto, Marco, Giancarlo Succi", "", "2005",
                "Proceedings of the 1st International Conference on Open Source Systems", "", "", "", "ECIG",
                "Genova", "inText", TextType.CONTRIBUTION.getTypeValue());
        lTextID = createText(lValues);
        lText = retrieveText(lTextID);
        lFormatter = new BibliographyFormatter(lText);
        assertEquals(
                "contribution partially",
                "Luthiger, Benno (2005). \"Fun and Software Development\", in Proceedings of the 1st International Conference on Open Source Systems. Eds. Scotto, Marco, Giancarlo Succi. Genova: ECIG.",
                lFormatter.renderPlain());

        // webpage
        lTextID = createText1("The story of all", "Mane, Jane", TextType.WEBPAGE.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals("webpage full",
                "Mane, Jane and Goldman, R. (2010). \"The story of all. About the subtitle\", NZZ. (accessed Hershey)",
                lFormatter.renderPlain());
    }

    @Test
    public void testRenderPlainMinimal() throws Exception {
        // book
        Long lTextID = createText2("The story of all", "Doe, Jane", TextType.BOOK.getTypeValue());
        IBibliography lText = retrieveText(lTextID);

        BibliographyFormatter lFormatter = new BibliographyFormatter(lText);
        assertEquals("book minimal", "Doe, Jane (2010). The story of all.", lFormatter.renderPlain());

        // article
        lTextID = createText2("The story of all", "Foo, Jane", TextType.ARTICLE.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals("article minimal", "Foo, Jane (2010). \"The story of all\".", lFormatter.renderPlain());

        // contribution
        lTextID = createText2("The story of all", "Fane, Jane", TextType.CONTRIBUTION.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals("contribution minimal", "Fane, Jane (2010). \"The story of all\".", lFormatter.renderPlain());

        // webpage
        lTextID = createText2("The story of all", "Mane, Jane", TextType.WEBPAGE.getTypeValue());
        lText = retrieveText(lTextID);

        lFormatter = new BibliographyFormatter(lText);
        assertEquals("webpage minimal", "Mane, Jane (2010). \"The story of all\".", lFormatter.renderPlain());
    }

    private IBibliography retrieveText(final Long lTextID) throws VException {
        final KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextHome.KEY_ID, new Long(lTextID));
        final ReadOnlyDomainObject lText = data.getJoinAuthorReviewerToTextHome().findByKey(lKey);
        return new BibliographyAdapter(lText, TextHome.KEY_BIBLIO_TYPE);
    }

    private Long createText1(final String inTitle, final String inAuthor, final int inType) throws VException,
            SQLException {
        final ITextValues lValues = new DataHouseKeeper.TextValues(inTitle, inAuthor, "Goldman, R.",
                "About the subtitle", "2010", "NZZ", "44-55", "12",
                "8", "IGI Publishing", "Hershey", "inText", inType);
        return createText(lValues);
    }

    private Long createText2(final String inTitle, final String inAuthor, final int inType) throws VException,
            SQLException {
        return createText(new DataHouseKeeper.TextValues(inTitle, inAuthor, "", "", "2010", "", "", "", "", "", "",
                "inText", inType));
    }

    private Long createText(final ITextValues inValues) throws VException, SQLException {
        final Text lText = (Text) data.getTextHome().create();
        return lText.ucNew(inValues, new Long(12));
    }

}
