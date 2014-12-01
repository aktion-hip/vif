/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.web.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.web.TestMessages;
import org.hip.vif.web.biblio.BibliographyAdapter;
import org.hip.vif.web.biblio.TextType;
import org.hip.vif.web.bom.Text;
import org.hip.vif.web.bom.TextHome;
import org.hip.vif.web.bom.VifBOMHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author lbenno */
public class TextImplTest {
    private static final String NL = System.getProperty("line.separator");

    private static DataHouseKeeper data;
    private static org.hip.vif.web.DataHouseKeeper dataG;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
        dataG = org.hip.vif.web.DataHouseKeeper.getInstance();
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

    private ITextValues createTestValues(final String inTitle, final String inAuthor) {
        return new DataHouseKeeper.TextValues(inTitle, inAuthor, "Foo, James", "Everything you need to know", "2010",
                "The Publication", "20-77", "5", "76", "NZZ Press", "Zürich", "Very <strong>strange</strong> story", 1);
    }

    /** Note: the label <code>Artikel</code> is in German in this test because the label is taken from the
     * <code>TextType</code> enum which instance variables are static */
    @Test
    public void testGetNotification() throws Exception {
        final String lTitle = "My Bibliography";
        final String lAuthor = "Doe, Jane";
        final Long lActor = new Long(77);
        final Integer lVersion = new Integer(0);

        final TextHome lHome = VifBOMHelper.getTextHome();
        Text lText = (Text) lHome.create();
        Long lTextId = lText.ucNew(createTestValues(lTitle, lAuthor), lActor);

        KeyObject lKeyText = new KeyObjectImpl();
        lKeyText.setValue(TextHome.KEY_ID, lTextId);
        lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
        lText = (Text) lHome.findByKey(lKeyText);

        final String lTypeLabel = TextType.ARTICLE.getLabel();

        System.setProperty(TestMessages.PROP_LOCALE, Locale.GERMAN.getLanguage());
        String lExptected = "Typ: " + lTypeLabel + NL + "Titel: My Bibliography" + NL
                + "Untertitel: Everything you need to know" + NL + "Autor: Doe, Jane" + NL + "Co-Autoren: Foo, James"
                + NL + "Jahr: 2010" + NL + "Zeitschrift: The Publication" + NL + "Herausgeber: NZZ Press" + NL
                + "Ort: Zürich" + NL + "Seiten: 20-77" + NL + "Vol.: 5" + NL + "Nr.: 76" + NL
                + "Bemerkung: Very strange story" + NL;
        assertEquals("notification de", lExptected, lText.getNotification());

        System.setProperty(TestMessages.PROP_LOCALE, Locale.ENGLISH.getLanguage());
        lExptected = "Type: " + lTypeLabel + NL + "Title: My Bibliography" + NL
                + "Subtitle: Everything you need to know"
                + NL + "Author: Doe, Jane" + NL + "Co-Authors: Foo, James" + NL + "Year: 2010" + NL
                + "Publication: The Publication" + NL + "Publisher: NZZ Press" + NL + "Place: Zürich" + NL
                + "Pages: 20-77" + NL + "Volume: 5" + NL + "Number: 76" + NL + "Remark: Very strange story" + NL;
        assertEquals("notification en", lExptected, lText.getNotification());

        System.setProperty(TestMessages.PROP_LOCALE, Locale.GERMAN.getLanguage());
        lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Typ</i>:</td><td>"
                + lTypeLabel
                + "</td></tr><tr><td><i>Titel</i>:</td><td>My Bibliography</td></tr><tr><td><i>Untertitel</i>:</td><td>Everything you need to know</td></tr><tr><td><i>Autor</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Co-Autoren</i>:</td><td>Foo, James</td></tr><tr><td><i>Jahr</i>:</td><td>2010</td></tr><tr><td><i>Zeitschrift</i>:</td><td>The Publication</td></tr><tr><td><i>Herausgeber</i>:</td><td>NZZ Press</td></tr><tr><td><i>Ort</i>:</td><td>Zürich</td></tr><tr><td><i>Seiten</i>:</td><td>20-77</td></tr><tr><td><i>Vol.</i>:</td><td>5</td></tr><tr><td><i>Nr.</i>:</td><td>76</td></tr><tr><td><i>Bemerkung</i>:</td><td>Very <strong>strange</strong> story</td></tr></table>";
        assertEquals("notification de html", lExptected, lText.getNotificationHtml());

        System.setProperty(TestMessages.PROP_LOCALE, Locale.ENGLISH.getLanguage());
        lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Type</i>:</td><td>"
                + lTypeLabel
                + "</td></tr><tr><td><i>Title</i>:</td><td>My Bibliography</td></tr><tr><td><i>Subtitle</i>:</td><td>Everything you need to know</td></tr><tr><td><i>Author</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Co-Authors</i>:</td><td>Foo, James</td></tr><tr><td><i>Year</i>:</td><td>2010</td></tr><tr><td><i>Publication</i>:</td><td>The Publication</td></tr><tr><td><i>Publisher</i>:</td><td>NZZ Press</td></tr><tr><td><i>Place</i>:</td><td>Zürich</td></tr><tr><td><i>Pages</i>:</td><td>20-77</td></tr><tr><td><i>Volume</i>:</td><td>5</td></tr><tr><td><i>Number</i>:</td><td>76</td></tr><tr><td><i>Remark</i>:</td><td>Very <strong>strange</strong> story</td></tr></table>";
        assertEquals("notification en html", lExptected, lText.getNotificationHtml());

        lText.ucSave(new DataHouseKeeper.TextValues(lTitle, lAuthor, "", "", "2010", "", "", "", "", "", "", "", 1),
                lActor);

        System.setProperty(TestMessages.PROP_LOCALE, Locale.GERMAN.getLanguage());
        lExptected = "Typ: " + lTypeLabel + NL + "Titel: My Bibliography" + NL + "Autor: Doe, Jane" + NL + "Jahr: 2010"
                + NL;
        assertEquals("notification de minimal 1", lExptected, lText.getNotification());

        lText.setVirgin();
        lTextId = lText.ucNew(new DataHouseKeeper.TextValues(lTitle, lAuthor, "", "", "2010", "", "", "", "", "", "",
                "", 1), lActor);

        lKeyText = new KeyObjectImpl();
        lKeyText.setValue(TextHome.KEY_ID, lTextId);
        lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
        lText = (Text) lHome.findByKey(lKeyText);

        assertEquals("notification de minimal 2", lExptected, lText.getNotification());

        lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Typ</i>:</td><td>"
                + lTypeLabel
                + "</td></tr><tr><td><i>Titel</i>:</td><td>My Bibliography</td></tr><tr><td><i>Autor</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Jahr</i>:</td><td>2010</td></tr></table>";
        assertEquals("notification de html minimal", lExptected, lText.getNotificationHtml());
        // System.out.println(lText.getNotificationHtml(Locale.GERMAN.getLanguage()));
    }

    @Test
    public void testHasWebPageUrl() throws Exception {
        final Text lText = (Text) dataG.getTextHome().create();
        lText.set(TextHome.KEY_TYPE, new Long(TextType.WEBPAGE.getTypeValue()));

        lText.set(TextHome.KEY_PUBLICATION, "http://aaa.bb.org/");
        assertTrue("url 1", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());

        lText.set(TextHome.KEY_PUBLICATION, "https://aaa.bb.org/");
        assertTrue("url 2", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());

        lText.set(TextHome.KEY_PUBLICATION, "ftp://aaa.bb.org/");
        assertTrue("url 3", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());

        lText.set(TextHome.KEY_PUBLICATION, "www.bb.org/path");
        assertTrue("url 4", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());

        lText.set(TextHome.KEY_PUBLICATION, "some/path");
        assertFalse("no url 1", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());

        lText.set(TextHome.KEY_PUBLICATION, "http://aaa.bb.org/");
        lText.set(TextHome.KEY_TYPE, new Long(TextType.ARTICLE.getTypeValue()));
        assertFalse("no url 2", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
    }

}
