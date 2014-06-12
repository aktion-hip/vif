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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.hip.kernel.bom.QueryResult;

/**
 * Base class for searching member or content entries in the VIF application.
 * 
 * @author Benno Luthiger
 * Created on 02.10.2005
 */
public abstract class AbstractVIFSearcher extends AbstractSearching {
	
	/**
	 * @return the appropriate {@link IndexReader} 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected abstract IndexReader getIndexReader() throws CorruptIndexException, IOException;
	
	/**
	 * Starts the search with the query term(s) provided and returns the
	 * hits as QueryResult.
	 * 
	 * @param inQueryTerm String
	 * @return QueryResult
	 * @throws IOException
	 * @throws ParseException
	 * @throws NoHitsException 
	 */
	public QueryResult search(String inQueryTerm) throws IOException, ParseException, NoHitsException {
		IndexReader lReader = getIndexReader();
		IndexSearcher lSearcher = new IndexSearcher(lReader);
		try {
			TopDocs lHits = lSearcher.search(parseQuery(inQueryTerm), NUMBER_OF_HITS);
			return getHitsQueryResult(processSearchResults(lHits, lSearcher, inQueryTerm));
		} 
		finally {
			lSearcher.close();
		}
	}
	
	/**
	 * Starts the search with a detailed search query. 
	 * 
	 * @param inName String
	 * @param inFirstName String
	 * @param inStreet String
	 * @param inPostal String
	 * @param inCity String
	 * @param inMail String
	 * @return QueryResult
	 * @throws NoHitsException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public QueryResult search(String inName, String inFirstName, String inStreet, String inPostal, String inCity, String inMail) throws NoHitsException, ParseException, IOException {
		IndexReader lReader = getIndexReader();
		IndexSearcher lSearcher = new IndexSearcher(lReader);
		try {
			StringBuilder lFeedback = new StringBuilder();
			BooleanQuery lMain = new BooleanQuery();
			lMain = addQueryChecked(inName, IndexField.MEMBER_NAME.fieldName, lMain, lFeedback);
			lMain = addQueryChecked(inFirstName, IndexField.MEMBER_FIRSTNAME.fieldName, lMain, lFeedback);
			lMain = addQueryChecked(inStreet, IndexField.MEMBER_STREET.fieldName, lMain, lFeedback);
			lMain = addQueryChecked(inPostal, IndexField.MEMBER_POSTAL.fieldName, lMain, lFeedback);
			lMain = addQueryChecked(inCity, IndexField.MEMBER_CITY.fieldName, lMain, lFeedback);
			lMain = addQueryChecked(inMail, IndexField.MEMBER_MAIL.fieldName, lMain, lFeedback);
			
			TopDocs lHits = lSearcher.search(lMain, NUMBER_OF_HITS);
			return getHitsQueryResult(processSearchResults(lHits, lSearcher, new String(lFeedback).substring(0, lFeedback.length()-3)));			
		}
		finally {
			lSearcher.close();
		}
	}
	
	private BooleanQuery addQueryChecked(String inValue, String inFieldName, BooleanQuery inQuery, StringBuilder inFeedback) throws ParseException {
		if (inValue.length() == 0)
			return inQuery;
		
		inFeedback.append(inValue).append(" / ");
		QueryParser lParser = new QueryParser(LUCENE_VERSION, inFieldName, getAnalyzer());
		Query lQuery = lParser.parse(inValue);
		inQuery.add(lQuery, BooleanClause.Occur.MUST);
		return inQuery;
	}
	
	/**
	 * Hook for subclasses: creates the appropriate HitsQueryResult.
	 * 
	 * @param inHits Document[]
	 * @return QueryResult
	 * @throws NoHitsException
	 * @throws IOException
	 */
	abstract QueryResult getHitsQueryResult(Document[] inHits) throws NoHitsException, IOException;
	
	/**
	 * Hook for subclasses: parses the query string.
	 * Subclasses decide which analyzer to use and which field to query.
	 * 
	 * @param inQueryTerm String
	 * @return Query
	 * @throws ParseException
	 */
	abstract Query parseQuery(String inQueryTerm) throws ParseException;
	
	/**
	 * Processes the lucene search result into a (ordered) set of <code>Document</code>s.
	 * 
	 * @param inHits TopDocs the lucene search result 
	 * @param inSearcher IndexSearcher
	 * @param inQuery String the query string
	 * @return Document[] the resulting set of <code>Document</code>s.
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws NoHitsException
	 */
	protected Document[] processSearchResults(TopDocs inHits, IndexSearcher inSearcher, String inQuery) throws CorruptIndexException, IOException, NoHitsException {
		if (inHits.totalHits == 0) throw new NoHitsException(inQuery);
		
		ScoreDoc[] lHits = inHits.scoreDocs;
		Document[] outDocuments = new Document[lHits.length];
		for (int i = 0; i < outDocuments.length; i++) {
			outDocuments[i] = inSearcher.doc(lHits[i].doc);
		}
		return outDocuments;
	}
	
}
