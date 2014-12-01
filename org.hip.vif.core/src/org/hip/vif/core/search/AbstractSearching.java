/**
 This package is part of the application VIF.
 Copyright (C) 2005-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.search;

import java.io.IOException;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.Version;
import org.hip.vif.core.service.PreferencesHandler;

/** Provides basic functionality for full text search using lucene.
 *
 * @author Benno Luthiger Created on 27.09.2005 */
public abstract class AbstractSearching {
    protected static final int NUMBER_OF_HITS = 50;
    public static final Version LUCENE_VERSION = Version.LUCENE_4_10_1;

    // enum for language analyzers (see lucene-analyzers-common-4.10.1.jar)
    private enum LanguageAnalyzer {
        AR("ar", new ArabicAnalyzer()),
        BG("bg", new BulgarianAnalyzer()),
        BR("br", new BrazilianAnalyzer()),
        CA("ca", new CatalanAnalyzer()),
        CN("cn", new StandardAnalyzer()),
        CZ("cz", new CzechAnalyzer()),
        DA("da", new DanishAnalyzer()),
        DE("de", new GermanAnalyzer()),
        EL("el", new GreekAnalyzer()),
        EN("en", new StandardAnalyzer()),
        ES("es", new SpanishAnalyzer()),
        EU("eu", new BasqueAnalyzer()),
        FA("fa", new PersianAnalyzer()),
        FI("fi", new FinnishAnalyzer()),
        FR("fr", new FrenchAnalyzer()),
        GL("gl", new GalicianAnalyzer()),
        HI("hi", new HindiAnalyzer()),
        HU("hu", new HungarianAnalyzer()),
        HY("hy", new ArmenianAnalyzer()),
        ID("id", new IndonesianAnalyzer()),
        IT("it", new ItalianAnalyzer()),
        LV("lv", new LatvianAnalyzer()),
        NL("nl", new DutchAnalyzer()),
        NO("no", new NorwegianAnalyzer()),
        PT("pt", new PortugueseAnalyzer()),
        RO("ro", new RomanianAnalyzer()),
        RU("ru", new RussianAnalyzer()),
        SV("sv", new SwedishAnalyzer()),
        TH("th", new ThaiAnalyzer()),
        TR("tr", new TurkishAnalyzer());

        public final String isoLanguage;
        public final Analyzer analyzer;

        LanguageAnalyzer(final String inISOLanguage, final Analyzer inAnalyzer) {
            isoLanguage = inISOLanguage;
            analyzer = inAnalyzer;
            analyzer.setVersion(LUCENE_VERSION);
        }
    }

    // see http://stackoverflow.com/questions/18564029/how-to-control-indexing-a-field-in-lucene-4-0
    public enum IndexField {
        QUESTION_TEXT("questionText", Field.Store.YES, new TextFieldFactory(), 1),
        QUESTION_COMPLETION("questionText", Field.Store.YES, new StoredFieldFactory(), 1),
        DECIMAL_ID("id", Field.Store.YES, new StringFieldFactory(), 1),
        AUTHOR_NAME("author", Field.Store.YES, new TextFieldFactory(), 2),
        CONTRIBUTION_ID("contributionId", Field.Store.YES, new StringFieldFactory(), 1),
        GROUP_ID("groupId", Field.Store.YES, new StoredFieldFactory(), 1),
        GROUP_NAME("groupName", Field.Store.YES, new StoredFieldFactory(), 2),
        CONTENT_FULL("fullContent", Field.Store.NO, new TextFieldFactory(), 1),
        MEMBER_ID("memberID", Field.Store.YES, new StringFieldFactory(), 1),
        MEMBER_USER_ID("memberUserID", Field.Store.YES, new StoredFieldFactory(), 1),
        MEMBER_NAME("memberName", Field.Store.YES, new TextFieldFactory(), 2),
        MEMBER_FIRSTNAME("memberFirstname", Field.Store.YES, new TextFieldFactory(), 1),
        MEMBER_STREET("memberStreet", Field.Store.YES, new TextFieldFactory(), 1),
        MEMBER_POSTAL("memberPostal", Field.Store.YES, new StringFieldFactory(), 1),
        MEMBER_CITY("memberCity", Field.Store.YES, new TextFieldFactory(), 1),
        MEMBER_MAIL("memberMail", Field.Store.YES, new StringFieldFactory(), 1),
        MEMBER_FULL_TEXT("memberFullText", Field.Store.NO, new TextFieldFactory(), 1),
        BIBLIO_ID("biblioID", Field.Store.YES, new StringFieldFactory(), 1),
        BIBLIO_TITLE("biblioTitle", Field.Store.YES, new TextFieldFactory(), 2),
        BIBLIO_AUTHOR("biblioAuthor", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_COAUTHOR("biblioCoAuthor", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_SUBTITLE("biblioSubtitle", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_YEAR("biblioYear", Field.Store.YES, new StringFieldFactory(), 1),
        BIBLIO_PUBLICATION("biblioPublication", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_PAGES("biblioPages", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_VOLUME("biblioVolume", Field.Store.YES, new StoredFieldFactory(), 1),
        BIBLIO_NUMBER("biblioNumber", Field.Store.YES, new StoredFieldFactory(), 1),
        BIBLIO_PUBLISHER("biblioPublisher", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_PLACE("biblioPlace", Field.Store.YES, new TextFieldFactory(), 1),
        BIBLIO_REMARK("biblioRemark", Field.Store.NO, new TextFieldFactory(), 1);

        public final String fieldName;
        private final Field.Store storeValue;
        private final IFieldFactory factory;
        private final float boostFactor;

        IndexField(final String inFieldName, final Field.Store inStore, final IFieldFactory inFactory,
                final float inBoost) {
            fieldName = inFieldName;
            storeValue = inStore;
            factory = inFactory;
            boostFactor = inBoost;
        }

        public Field createField(final String inValue) {
            final Field out = factory.createField(fieldName, inValue, storeValue);
            if (out.fieldType().indexed()) {
                out.setBoost(boostFactor);
            }
            return out;
        }
    }

    public final static IndexField[] CONTENT_FIELDS = { IndexField.QUESTION_TEXT,
        IndexField.DECIMAL_ID,
        IndexField.AUTHOR_NAME,
        IndexField.CONTRIBUTION_ID,
        IndexField.GROUP_ID,
        IndexField.GROUP_NAME };

    public final static IndexField[] MEMBER_FIELDS = { IndexField.MEMBER_ID,
        IndexField.MEMBER_USER_ID,
        IndexField.MEMBER_NAME,
        IndexField.MEMBER_FIRSTNAME,
        IndexField.MEMBER_STREET,
        IndexField.MEMBER_POSTAL,
        IndexField.MEMBER_CITY,
        IndexField.MEMBER_MAIL };

    public static final IndexField[] BIBLIO_FIELDS = { IndexField.BIBLIO_ID,
        IndexField.BIBLIO_TITLE,
        IndexField.BIBLIO_AUTHOR,
        IndexField.BIBLIO_COAUTHOR,
        IndexField.BIBLIO_SUBTITLE,
        IndexField.BIBLIO_YEAR,
        IndexField.BIBLIO_PUBLICATION,
        IndexField.BIBLIO_PAGES,
        IndexField.BIBLIO_VOLUME,
        IndexField.BIBLIO_NUMBER,
        IndexField.BIBLIO_PUBLISHER,
        IndexField.BIBLIO_PLACE,
        IndexField.BIBLIO_REMARK,
    };

    /** Hook for subclasses
     *
     * @throws IOException */
    protected void afterChange() throws IOException {
        // left empty intentionally
    }

    /** Hook for subclasses
     *
     * @throws IOException */
    protected void beforeChange() throws IOException {
        // left empty intentionally
    }

    /** @return the {@link Analyzer} for the correct language according to the application's language settings.
     * @see vif.properties <code>org.hip.vif.content.language</code> */
    static Analyzer getAnalyzer() {
        final String lLanguage = PreferencesHandler.INSTANCE.getLocale(false, Locale.ENGLISH).getLanguage();
        for (final LanguageAnalyzer lAnalyzer : LanguageAnalyzer.values()) {
            if (lLanguage.equals(lAnalyzer.isoLanguage)) {
                return lAnalyzer.analyzer;
            }
        }
        return new StandardAnalyzer();
    }

    // ---

    private static interface IFieldFactory {
        Field createField(String inName, String inValue, Field.Store inStored);
    }

    private static class TextFieldFactory implements IFieldFactory {

        @Override
        public Field createField(final String inName, final String inValue, final Store inStored) {
            return new TextField(inName, inValue, inStored);
        }

    }

    private static class StringFieldFactory implements IFieldFactory {

        @Override
        public Field createField(final String inName, final String inValue, final Store inStored) {
            return new StringField(inName, inValue, inStored);
        }

    }

    private static class StoredFieldFactory implements IFieldFactory {

        @Override
        public Field createField(final String inName, final String inValue, final Store inStored) {
            return new StoredField(inName, inValue);
        }

    }

}
