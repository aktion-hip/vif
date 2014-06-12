/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

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
import org.apache.lucene.util.Version;
import org.hip.vif.core.service.PreferencesHandler;

/**
 * Provides basic functionality for full text search using lucene.
 * 
 * @author Benno Luthiger
 * Created on 27.09.2005 
 */
public abstract class AbstractSearching {
	protected static final int NUMBER_OF_HITS = 50;
	public static final Version LUCENE_VERSION = Version.LUCENE_34;
	
	//enum for language analyzers (see contrib/analyzers/lucene-analyzers-3.4.jar)
	private enum LanguageAnalyzer {
		AR ("ar", new ArabicAnalyzer(LUCENE_VERSION)),
		BG ("bg", new BulgarianAnalyzer(LUCENE_VERSION)),
		BR ("br", new BrazilianAnalyzer(LUCENE_VERSION)),
		CA ("ca", new CatalanAnalyzer(LUCENE_VERSION)),
		CN ("cn", new StandardAnalyzer(LUCENE_VERSION)),
		CZ ("cz", new CzechAnalyzer(LUCENE_VERSION)),
		DA ("da", new DanishAnalyzer(LUCENE_VERSION)),
		DE ("de", new GermanAnalyzer(LUCENE_VERSION)),
		EL ("el", new GreekAnalyzer(LUCENE_VERSION)),
		EN ("en", new StandardAnalyzer(LUCENE_VERSION)),
		ES ("es", new SpanishAnalyzer(LUCENE_VERSION)),
		EU ("eu", new BasqueAnalyzer(LUCENE_VERSION)),
		FA ("fa", new PersianAnalyzer(LUCENE_VERSION)),
		FI ("fi", new FinnishAnalyzer(LUCENE_VERSION)),
		FR ("fr", new FrenchAnalyzer(LUCENE_VERSION)),
		GL ("gl", new GalicianAnalyzer(LUCENE_VERSION)),
		HI ("hi", new HindiAnalyzer(LUCENE_VERSION)),
		HU ("hu", new HungarianAnalyzer(LUCENE_VERSION)),
		HY ("hy", new ArmenianAnalyzer(LUCENE_VERSION)),
		ID ("id", new IndonesianAnalyzer(LUCENE_VERSION)),
		IT ("it", new ItalianAnalyzer(LUCENE_VERSION)),
		LV ("lv", new LatvianAnalyzer(LUCENE_VERSION)),
		NL ("nl", new DutchAnalyzer(LUCENE_VERSION)),
		NO ("no", new NorwegianAnalyzer(LUCENE_VERSION)),		
		PT ("pt", new PortugueseAnalyzer(LUCENE_VERSION)),
		RO ("ro", new RomanianAnalyzer(LUCENE_VERSION)),
		RU ("ru", new RussianAnalyzer(LUCENE_VERSION)),
		SV ("sv", new SwedishAnalyzer(LUCENE_VERSION)),
		TH ("th", new ThaiAnalyzer(LUCENE_VERSION)),
		TR ("tr", new TurkishAnalyzer(LUCENE_VERSION));
		
		public final String isoLanguage;
		public final Analyzer analyzer;
		LanguageAnalyzer(String inISOLanguage, Analyzer inAnalyzer) {
			isoLanguage = inISOLanguage;
			analyzer = inAnalyzer;
		}
	}
	
	public enum IndexField {
		QUESTION_TEXT		("questionText", Field.Store.YES, Field.Index.ANALYZED),
		QUESTION_COMPLETION	("questionText", Field.Store.YES, Field.Index.NO),
		DECIMAL_ID			("id", Field.Store.YES, Field.Index.NOT_ANALYZED),
		AUTHOR_NAME			("author", Field.Store.YES, Field.Index.ANALYZED),
		CONTRIBUTION_ID		("contributionId", Field.Store.YES, Field.Index.NOT_ANALYZED),
		GROUP_ID			("groupId", Field.Store.YES, Field.Index.NO),
		GROUP_NAME			("groupName", Field.Store.YES, Field.Index.NO),
		CONTENT_FULL		("fullContent", Field.Store.NO, Field.Index.ANALYZED),
		MEMBER_ID			("memberID", Field.Store.YES, Field.Index.NOT_ANALYZED),
		MEMBER_USER_ID		("memberUserID", Field.Store.YES, Field.Index.NO),
		MEMBER_NAME			("memberName", Field.Store.YES, Field.Index.ANALYZED),
		MEMBER_FIRSTNAME	("memberFirstname", Field.Store.YES, Field.Index.ANALYZED),
		MEMBER_STREET		("memberStreet", Field.Store.YES, Field.Index.ANALYZED),
		MEMBER_POSTAL		("memberPostal", Field.Store.YES, Field.Index.NOT_ANALYZED),
		MEMBER_CITY			("memberCity", Field.Store.YES, Field.Index.ANALYZED),
		MEMBER_MAIL			("memberMail", Field.Store.YES, Field.Index.NOT_ANALYZED),
		MEMBER_FULL_TEXT	("memberFullText", Field.Store.NO, Field.Index.ANALYZED),
		BIBLIO_ID			("biblioID", Field.Store.YES, Field.Index.NOT_ANALYZED),
		BIBLIO_TITLE		("biblioTitle", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_AUTHOR		("biblioAuthor", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_COAUTHOR		("biblioCoAuthor", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_SUBTITLE		("biblioSubtitle", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_YEAR			("biblioYear", Field.Store.YES, Field.Index.NOT_ANALYZED),
		BIBLIO_PUBLICATION	("biblioPublication", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_PAGES		("biblioPages", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_VOLUME		("biblioVolume", Field.Store.YES, Field.Index.NO),
		BIBLIO_NUMBER		("biblioNumber", Field.Store.YES, Field.Index.NO),
		BIBLIO_PUBLISHER	("biblioPublisher", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_PLACE		("biblioPlace", Field.Store.YES, Field.Index.ANALYZED),
		BIBLIO_REMARK		("biblioRemark", Field.Store.NO, Field.Index.ANALYZED),
		;
		
		public final String fieldName;
		private final Field.Store storeValue;
		private final Field.Index indexValue;
		IndexField(String inFieldName, Field.Store inStore, Field.Index inIndex) {
			fieldName = inFieldName;
			storeValue = inStore;
			indexValue = inIndex;
		}
		public Field createField(String inValue) {
			return new Field(fieldName, inValue, storeValue, indexValue);
		}
	}
	
	public final static IndexField[] CONTENT_FIELDS = {IndexField.QUESTION_TEXT,
														IndexField.DECIMAL_ID,
														IndexField.AUTHOR_NAME,
														IndexField.CONTRIBUTION_ID,
														IndexField.GROUP_ID,
														IndexField.GROUP_NAME};

	public final static IndexField[] MEMBER_FIELDS = {IndexField.MEMBER_ID, 
														IndexField.MEMBER_USER_ID, 
														IndexField.MEMBER_NAME, 
														IndexField.MEMBER_FIRSTNAME, 
														IndexField.MEMBER_STREET, 
														IndexField.MEMBER_POSTAL, 
														IndexField.MEMBER_CITY, 
														IndexField.MEMBER_MAIL};
	
	public static final IndexField[] BIBLIO_FIELDS = {IndexField.BIBLIO_ID,
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

	/**
	 * Hook for subclasses
	 * 
	 * @throws IOException
	 */
	protected void afterChange() throws IOException {
		//left empty intentionally
	}
	
	/**
	 * Hook for subclasses
	 * 
	 * @throws IOException
	 */
	protected void beforeChange() throws IOException {
		//left empty intentionally
	}

	/**
	 * @return the {@link Analyzer} for the correct language according to the application's language settings.
	 * @see vif.properties <code>org.hip.vif.content.language</code>
	 */
	static Analyzer getAnalyzer() {
		String lLanguage = PreferencesHandler.INSTANCE.getLocale(false, Locale.ENGLISH).getLanguage();
		for (LanguageAnalyzer lAnalyzer : LanguageAnalyzer.values()) {
			if (lLanguage.equals(lAnalyzer.isoLanguage)) {
				return lAnalyzer.analyzer;
			}
		}
		return new StandardAnalyzer(LUCENE_VERSION);
	}

}
