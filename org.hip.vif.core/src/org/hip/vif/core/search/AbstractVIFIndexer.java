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
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.LimitObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.service.PreferencesHandler;

/**
 * Provides basic functionality to create the index for full text search using lucene.
 * 
 * @author Benno Luthiger
 * Created on 02.10.2005
 */
public abstract class AbstractVIFIndexer extends AbstractSearching {

	/**
	 * Method to refresh the index managed by this class.
	 * 
	 * @return Integer[] Array containing the number of indexed questions, completions respectively.
	 * @throws VException
	 * @throws SQLException
	 * @throws IOException
	 */
	public Integer[] refreshIndex() throws Exception {
		IndexWriter lWriter = getIndexWriter(true);
		Integer[] outIndexed = doIndex(lWriter);
		synchronized (this) {
			lWriter.optimize();
		}
		afterChange();
		return outIndexed;
	}
	
	/**
	 * @param inCreate Boolean <code>true</code> if the <code>IndexWirter</code> has <code>OpenMode.CREATE</code> 
	 * (i.e. the index is created and an existing index is overwritten), 
	 * if <code>false</code>, the <code>IndexWirter</code> has <code>OpenMode.APPEND</code> 
	 * (i.e. new documents are appended to existing index).<br />
	 * Use <code>true</code> only to build a new index (e.g. for an admin refresh). 
	 * 
	 * @return the appropriate {@link IndexWriter} 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected abstract IndexWriter getIndexWriter(boolean inCreate) throws CorruptIndexException, IOException;
	
	/**
	 * This method goes through the DB table by sending several limited select 
	 * queries one after the other and then indexing the data returned.
	 * 
	 * @param inWriter IndexWriter
	 * @param inHome GeneralDomainObjectHome
	 * @param inKey KeyObject
	 * @return int Number of entries indexed.
	 * @throws VException
	 * @throws SQLException
	 * @throws IOException
	 */
	protected int processSelection(IndexWriter inWriter, GeneralDomainObjectHome inHome, KeyObject inKey) throws VException, SQLException, IOException {
		//in the embedded case using Derby, we cant's use the LIMIT constraint
		if (PreferencesHandler.INSTANCE.isDerbyDB()) {
			QueryResult lResult = inHome.select(inKey);
			return indexEntries(lResult, inWriter);
		}
		
		int outNumberOfIndexed = 0;
		int lLimit = getLimit();
		int lOffset = 0;
		QueryResult lResult = inHome.select(inKey, new LimitObjectImpl(lLimit, lOffset));
		while (lResult.hasMoreElements()) {
			outNumberOfIndexed += indexEntries(lResult, inWriter);
	
			lOffset += lLimit;
			lResult = inHome.select(inKey, new LimitObjectImpl(lLimit, lOffset));
		}
		return outNumberOfIndexed;		
	}

	private int indexEntries(QueryResult inResult, IndexWriter inWriter) throws VException, SQLException, IOException {
		int outNumberOfIndexed = 0;
		while (inResult.hasMoreElements()) {
			Indexable lIndexable = (Indexable)inResult.nextAsDomainObject();
			lIndexable.indexContent(inWriter);
			((DomainObject)lIndexable).release();
			outNumberOfIndexed++;
		}
		return outNumberOfIndexed;
	}

	/**
	 * Adds the content of a single entry in a DB table to the index.
	 * 
	 * @param inHome GeneralDomainObjectHome
	 * @param inKey KeyObject
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws VException 
	 */
	protected void addEntryToIndex(GeneralDomainObjectHome inHome, KeyObject inKey) throws IOException, VException, SQLException {
		beforeChange();
		IndexWriter lWriter = getIndexWriter(false);
		processSelection(lWriter, inHome, inKey);
		synchronized (this) {
			lWriter.optimize();
		}
		lWriter.commit();
		afterChange();
	}
	
	protected void deleteEntryInIndex(Term inTerm) throws IOException {
		IndexWriter lWriter = getIndexWriter(false);
		lWriter.deleteDocuments(inTerm);
		lWriter.commit();
	}

	/**
	 * Starts the indexing and returns the numbers of indexed entries.
	 * 
	 * @param inWriter
	 * @return Integer[] {number of indexed questions, number of indexed completions}
	 * @throws VException
	 * @throws SQLException
	 * @throws IOException
	 */
	protected abstract Integer[] doIndex(IndexWriter inWriter) throws Exception;

	/**
	 * @return int Number of rows to be returned for each DB request when indexing a whole DB table. 
	 */
	protected abstract int getLimit();

}
